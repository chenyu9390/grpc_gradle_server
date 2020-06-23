package net.gichain.rechargeChannel.controller;

import com.alibaba.fastjson.JSON;
import com.github.wxpay.sdk.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import net.gichain.rechargeChannel.domain.NotifyTask;
import net.gichain.rechargeChannel.domain.RechargeEnum;
import net.gichain.rechargeChannel.domain.TradeStatusEnum;
import net.gichain.rechargeChannel.manager.RechargeManager;
import net.pay.entity.weixin.WxAppPayRespEntity;
import net.pay.entity.weixin.WxOrderEntity;
import net.pay.entity.weixin.WxPayEntity;
import net.pay.entity.weixin.WxRefundRespEntity;
import net.pay.sdk.WXSDK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * recharge_interface
 * 2020/6/16 10:08
 * 微信接口回调
 *
 * @author ck
 * @since
 **/
@RestController
@RequestMapping("/wePay")
@Slf4j
public class WxCallBackController {

    @Autowired
    private RechargeManager rechargeManager;
    @Autowired
    private WXSDK wxsdk;

    /**
     * 订单充值回调
     * @param data
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/rechargeCallBack",method = RequestMethod.POST)
    public String sync(@RequestBody String data) throws Exception {
        Map<String,String> respMap = new HashMap<>(2);
        try {
            WxOrderEntity entity = wxsdk.payCallBack(data);
            if("success".equalsIgnoreCase(entity.getResultCode())){
                rechargeManager.analyzeRechargeStatus(entity);
                respMap.put("return_code","SUCCESS");
                respMap.put("return_msg","OK");
            }else {
                respMap.put("return_code","FAIL");
                respMap.put("return_msg",entity.getReturnMsg());
            }
        } catch (Exception e) {
            log.error("微信支付回调签名错误");
            respMap.put("return_code","FAIL");
            respMap.put("return_msg","签名校验失败");
        }

        return WXPayUtil.mapToXml(respMap);
    }

    /**
     * 微信退款回调
     * @param data 回调参数
     * @return
     */
    @RequestMapping(value = "/refundCallBack",method = RequestMethod.POST)
    public String refund(@RequestBody String data) throws Exception {
        Map<String,String> respMap = new HashMap<>(2);
        WxRefundRespEntity entity = wxsdk.refundCallBack(data);
        if("success".equalsIgnoreCase(entity.getResultCode())){
            rechargeManager.analyzeRefundStatus(entity);
            respMap.put("return_code","SUCCESS");
            respMap.put("return_msg","OK");
        }else {
            respMap.put("return_code","FAIL");
            respMap.put("return_msg",entity.getReturnMsg());
        }
        return WXPayUtil.mapToXml(respMap);
    }

    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public String test() throws Exception {
        WxPayEntity entity = WxPayEntity.builder().outTradeNo(WXPayUtil.generateNonceStr())
                .totalFee("1")
                .tradeType("APP")
                .feeType("CNY")
                .body("TEST")
                .build();
        WxAppPayRespEntity respEntity = wxsdk.appPay(entity);
        log.info("充值通道微信支付响应：{}", JSON.toJSONString(respEntity));
        return JSON.toJSONString(respEntity);
    }
}
