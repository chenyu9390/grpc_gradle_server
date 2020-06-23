package net.gichain.rechargeChannel.impl;

import com.alibaba.fastjson.JSON;
import com.github.wxpay.sdk.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import net.gichain.genergy.grpc.response.Result;
import net.gichain.genergy.recharge.RechargeDto;
import net.gichain.genergy.recharge.RefundDto;
import net.gichain.rechargeChannel.domain.RechargeEntity;
import net.gichain.rechargeChannel.domain.RechargeEnum;
import net.gichain.rechargeChannel.domain.ResponseStatus;
import net.gichain.rechargeChannel.manager.RechargeManager;
import net.gichain.rechargeChannel.service.PayService;
import net.gichain.rechargeChannel.util.ResultUtil;
import net.gichain.rechargeChannel.util.StringValueUtil;
import net.pay.entity.weixin.*;
import net.pay.sdk.WXSDK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * PaySDK
 * 2020/6/13 16:46
 * 微信支付
 *
 * @author ck
 * @since
 **/
@Component("wepay")
@Slf4j
public class WXPayServiceImpl implements PayService {

    @Autowired
    private WXSDK wxsdk;
    @Autowired
    private RechargeManager rechargeManager;
    /**
     * 充值
     * @param request
     * @return
     */
    @Override
    public Result pay(RechargeDto request) {
        //封装微信支付请求参数
        WxPayEntity entity = WxPayEntity.builder().outTradeNo(WXPayUtil.generateNonceStr())
                                                    .totalFee(String.valueOf(request.getAmount()))
                                                    .tradeType(request.getTradeType())
                                                    .feeType(request.getCurrency())
                                                    .body(request.getBody())
                                                    .build();
        Result result = null;
        try {
            Map<String,String> map = new HashMap<>(8);
            if("APP".equals(request.getTradeType())){
                WxAppPayRespEntity respEntity = wxsdk.appPay(entity);
                log.info("充值通道微信支付响应：{}",JSON.toJSONString(respEntity));
                if(!"success".equalsIgnoreCase(respEntity.getResultCode()) && !"success".equals(respEntity.getReturnCode())){
                    result = ResultUtil.getResult(ResponseStatus.FAIL, StringValueUtil.isEmpty(respEntity.getErrCodeDes())?respEntity.getReturnMsg():respEntity.getErrCodeDes(),"");
                    return result;
                }
                map.put("prepayId",respEntity.getPrepayId());
                map.put("appid",respEntity.getAppId());
                map.put("partnerid",respEntity.getPartnerId());
                map.put("package",respEntity.getExtra());
                map.put("noncestr",respEntity.getNonceStr());
                map.put("timestamp",respEntity.getTimestamp());
                map.put("sign",respEntity.getSign());
                map.put("serial",entity.getOutTradeNo());
                result = ResultUtil.getResult(ResponseStatus.SUCCESS,null, JSON.toJSONString(map));
            }else if("NATIVE".equals(request.getTradeType())){
                WxPayRespEntity respEntity = wxsdk.nativePay(entity);
                if("success".equals(respEntity.getResultCode()) || "success".equals(respEntity.getReturnCode())){
                    result = ResultUtil.getResult(ResponseStatus.FAIL,respEntity.getErrCodeDes(),null);
                    return result;
                }
                map.put("codeUrl",respEntity.getCodeUrl());
            }
            result = ResultUtil.getResult(ResponseStatus.SUCCESS,null, JSON.toJSONString(map));
            if(result.getSuccess()){
                RechargeEntity rechargeEntity = RechargeEntity.builder().bussinessId(request.getBussinessId())
                        .serial(entity.getOutTradeNo())
                        .tradeType(request.getTradeType())
                        .amount(BigDecimal.valueOf(request.getAmount()))
                        .currency(request.getCurrency())
                        .rechargeExpireTime(request.getTimeExpire())
                        .notifyUrl(request.getNotifyUrl())
                        .body(request.getBody())
                        .build();
                rechargeManager.saveRecharge(rechargeEntity);
            }

        } catch (Exception e) {
            log.error("微信支付请求接口异常",e);
        }

        return result;
    }

    /**
     * 退款
     * @param request
     * @return
     */
    @Override
    public Result refund(RefundDto request) {
        //log.info("充值通道微信退款请求参数：{}",JSON.toJSONString(request));
        RechargeEntity rechargeEntity = rechargeManager.getRecharge(request.getSerial(), null);
        if(rechargeEntity == null){
            return ResultUtil.getResult(ResponseStatus.SERIAL_NOT_EXIST,null, "");
        }
        if(!RechargeEnum.RECHARGE_SUCCESS.equals(rechargeEntity.getStatus())){
            return ResultUtil.getResult(ResponseStatus.SERIAL_NOT_REFUND,null, "");
        }
        WxRefundEntity refundEntity = new WxRefundEntity();
        refundEntity.setOutTradeNo(request.getSerial());
        refundEntity.setOutRefundNo(WXPayUtil.generateNonceStr());
        refundEntity.setTotalFee(rechargeEntity.getAmount().toPlainString());
        refundEntity.setRefundFee(rechargeEntity.getAmount().toPlainString());
        refundEntity.setRefundDesc(String.format("%s退款",request.getSerial()));
        Result result = null;
        try {
            WxRefundRespEntity respEntity = wxsdk.refund(refundEntity);
            log.info("充值通道微信退款响应参数：{}",JSON.toJSONString(respEntity));
            if(!"success".equals(respEntity.getResultCode()) && !"success".equals(respEntity.getReturnCode())){
                result = ResultUtil.getResult(ResponseStatus.FAIL,respEntity.getErrCodeDes(),"");
                return result;
            }
            rechargeManager.refundRecharge(request.getSerial());

        } catch (Exception e) {
            log.error("微信退款申请失败",e);
        }
        result = ResultUtil.getResult(ResponseStatus.SUCCESS,null, "");
        return result;
    }
}
