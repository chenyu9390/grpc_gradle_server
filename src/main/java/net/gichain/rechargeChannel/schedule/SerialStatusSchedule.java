package net.gichain.rechargeChannel.schedule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.gichain.rechargeChannel.domain.NotifyTask;
import net.gichain.rechargeChannel.domain.RechargeEnum;
import net.gichain.rechargeChannel.domain.RefundStatusEnum;
import net.gichain.rechargeChannel.domain.TradeStatusEnum;
import net.gichain.rechargeChannel.manager.RechargeManager;
import net.gichain.rechargeChannel.util.HttpClientUtil;
import net.gichain.rechargeChannel.util.StringValueUtil;
import net.pay.entity.weixin.WxOrderEntity;
import net.pay.entity.weixin.WxRefundRespEntity;
import net.pay.sdk.WXSDK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * recharge_interface
 * 2020/6/15 18:29
 * 订单状态查询时间
 *
 * @author ck
 * @since
 **/
@Component
@Slf4j
public class SerialStatusSchedule {

    @Autowired
    private RechargeManager rechargeManager;

    @Autowired
    private WXSDK wxsdk;


    /**
     * 查询订单状态定时任务
     */
    @Scheduled(fixedDelay = 1000*10)
    public void querySerialStatus(){
        Optional<List<Map<String,Object>>> optionalMaps = rechargeManager.serialList();
        if(!optionalMaps.isPresent()){
            return;
        }
        //维护订单状态
        List<Object[]> objectList = new ArrayList<>(optionalMaps.get().size());
        //维护退款订单状态
        List<Object[]> refundObjectList = new ArrayList<>(optionalMaps.get().size());
        List<WxRefundRespEntity> refundRespEntities = new ArrayList<>();
        //维护支付成功回调任务
        List<NotifyTask> notifyTasks = new ArrayList<>();
        optionalMaps.get().forEach(map -> {
            try {
                String serial = StringValueUtil.getValueByMap(map,"serial","");
                String status = StringValueUtil.getValueByMap(map,"status","");
                if(RechargeEnum.CREATE.getStatus().equals(status)){
                    String notifyUrl = StringValueUtil.getValueByMap(map,"notify_url","");
                    getRechargeStatus(objectList,notifyTasks,serial,notifyUrl);
                }else if(RechargeEnum.REFUNDING.getStatus().equals(status)){
                    String refundNotifyUrl = StringValueUtil.getValueByMap(map,"refund_notify_url","");
                    getRefundStatus(refundObjectList,notifyTasks,serial,refundNotifyUrl,refundRespEntities);
                }

            } catch (Exception e) {
                log.error("订单状态查询失败",e);
            }
        });
        rechargeManager.batchUpdateRechargeSerialStatus(objectList);
        rechargeManager.saveNotifyTask(notifyTasks);
        rechargeManager.batchUpdateRefundSerialStatus(refundObjectList);
        rechargeManager.batchCreateRefundRecord(refundRespEntities);
    }

    /**
     * 查询退款单号状态信息
     * @param objectList
     * @param notifyTasks
     * @param serial
     * @param refundNotifyUr
     * @throws Exception
     */
    private void getRefundStatus(List<Object[]> objectList,List<NotifyTask> notifyTasks,String serial,String refundNotifyUr,List<WxRefundRespEntity> refundRespEntities) throws Exception {
        WxRefundRespEntity entity = wxsdk.refundQuery(serial,null,null,null,0);
        log.info("订单退款状态查询resp:{}", JSON.toJSONString(entity));
        if("success".equalsIgnoreCase(entity.getResultCode())){
            String tradeStatus = entity.getRefundStatus();
            refundRespEntities.add(entity);
            if(RefundStatusEnum.SUCCESS.getCode().equals(tradeStatus)){
                objectList.add(new Object[]{RechargeEnum.REFUND_SUCCESS.getStatus(),entity.getRefundSuccessTime(),entity.getRefundId(),serial,RechargeEnum.REFUNDING.getStatus()});
                notifyTasks.add(NotifyTask.builder().serial(serial)
                        .status(RefundStatusEnum.SUCCESS.getCode())
                        .message(RefundStatusEnum.SUCCESS.getDesc())
                        .notifyUrl(refundNotifyUr)
                        .build());
            }else if(RefundStatusEnum.REFUNDCLOSE.getCode().equals(tradeStatus)){
                objectList.add(new Object[]{RechargeEnum.RECHARGE_SUCCESS.getStatus(),null,entity.getRefundId(),serial,RechargeEnum.REFUNDING.getStatus()});
                notifyTasks.add(NotifyTask.builder().serial(serial)
                        .status(RefundStatusEnum.REFUNDCLOSE.getCode())
                        .message(RefundStatusEnum.REFUNDCLOSE.getDesc())
                        .notifyUrl(refundNotifyUr)
                        .build());
            }else if(RefundStatusEnum.CHANGE.getCode().equals(tradeStatus)){
                objectList.add(new Object[]{RechargeEnum.REFUND_FAIL.getStatus(),null,entity.getRefundId(),serial,RechargeEnum.REFUNDING.getStatus()});
                notifyTasks.add(NotifyTask.builder().serial(serial)
                        .status(RefundStatusEnum.CHANGE.getCode())
                        .message(RefundStatusEnum.CHANGE.getDesc())
                        .notifyUrl(refundNotifyUr)
                        .build());
            }
        }
    }

    /**
     * 查询下单的订单状态
     */
    private void getRechargeStatus(List<Object[]> objectList,List<NotifyTask> notifyTasks,String serial,String notifyUrl) throws Exception {
        WxOrderEntity orderEntity = wxsdk.query(serial,null);
        log.info("订单充值状态查询resp:{}", JSON.toJSONString(orderEntity));
        if("success".equalsIgnoreCase(orderEntity.getResultCode())){
            String tradeStatus = orderEntity.getTradeState();
            if(TradeStatusEnum.SUCCESS.getCode().equalsIgnoreCase(tradeStatus)){

                objectList.add(new Object[]{RechargeEnum.RECHARGE_SUCCESS.getStatus(),orderEntity.getTimeEnd(),orderEntity.getOpenId(),orderEntity.getTransactionId(),serial,RechargeEnum.CREATE.getStatus()});
                notifyTasks.add(NotifyTask.builder().serial(serial)
                        .status(TradeStatusEnum.SUCCESS.getCode())
                        .message(TradeStatusEnum.SUCCESS.getDesc()).notifyUrl(notifyUrl)
                        .build());
            }else if(TradeStatusEnum.CLOSED.getCode().equalsIgnoreCase(tradeStatus)){
                objectList.add(new Object[]{RechargeEnum.CLOSE.getStatus(),null,orderEntity.getOpenId(),orderEntity.getTransactionId(),serial,RechargeEnum.CREATE.getStatus()});
                notifyTasks.add(NotifyTask.builder().serial(serial).status(TradeStatusEnum.CLOSED.getCode()).message(TradeStatusEnum.CLOSED.getDesc()).notifyUrl(notifyUrl).build());
            }else if(TradeStatusEnum.PAYERROR.getCode().equalsIgnoreCase(tradeStatus)){
                objectList.add(new Object[]{RechargeEnum.FAIL.getStatus(),null,orderEntity.getOpenId(),orderEntity.getTransactionId(),serial,RechargeEnum.CREATE.getStatus()});
                notifyTasks.add(NotifyTask.builder().notifyUrl(notifyUrl).serial(serial).status(TradeStatusEnum.PAYERROR.getCode()).message(TradeStatusEnum.PAYERROR.getDesc()).build());
            }
        }
    }

    /**
     * 回调通知充值和退款任务
     */
    @Scheduled(fixedDelay = 1000*60)
    public void notifyTask(){
        List<NotifyTask> taskList = rechargeManager.getNotifyTask();
        List<Object[]> batchArgs = new ArrayList<>(taskList.size());
        taskList.forEach(task -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("serial",task.getSerial());
            jsonObject.put("status",task.getStatus());
            jsonObject.put("message",task.getMessage());
            log.info("回调请求url:{}",task.getNotifyUrl());
            log.info("回调请求body:{}",jsonObject.toString());
            try {
                String result = HttpClientUtil.sendPost(task.getNotifyUrl(),jsonObject.toJSONString());
                JSONObject json = JSONObject.parseObject(result);
                if("0".equalsIgnoreCase(json.getString("code"))){
                    batchArgs.add(new Object[]{1,json.getString("message"),task.getSerial()});
                }
            } catch (Exception e) {
                log.error("回调接口请求异常",e);
                batchArgs.add(new Object[]{0,"回调接口请求异常",task.getSerial()});
            }
        });
        rechargeManager.updateNotifyTask(batchArgs);
    }
}
