package net.gichain.rechargeChannel.manager;

import net.gichain.rechargeChannel.dao.RechargeDao;
import net.gichain.rechargeChannel.domain.*;
import net.gichain.rechargeChannel.util.StringValueUtil;
import net.pay.entity.weixin.WxOrderEntity;
import net.pay.entity.weixin.WxRefundRespEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * recharge_interface
 * 2020/6/15 13:39
 * Manager
 *
 * @author ck
 * @since
 **/
@Component
public class RechargeManager {

    @Autowired
    private RechargeDao rechargeDao;

    /**
     * 保存充值记录
     * @param entity
     * @return
     */
    public int saveRecharge(RechargeEntity entity){
        return rechargeDao.saveRecharge(entity);
    }

    /**
     * 查询订单信息
     * @param serial 支付流水号
     * @return
     */
    public RechargeEntity getRecharge(String serial,String status){
        return rechargeDao.getRecharge(serial,status);
    }

    /**
     * 充值订单退款
     * @param serial 支付流水号
     */
    public int refundRecharge(String serial){
        return rechargeDao.refundSerial(serial);
    }

    /**
     * 已下单订单状态查询
     * @return
     */
    public Optional<List<Map<String,Object>>> serialList(){
        Optional<List<Map<String,Object>>> optionalMaps = rechargeDao.getQuerySerialList();
        return optionalMaps;
    }

    /**
     * 更新充值订单状态
     * @param batchArgs
     */
    public void batchUpdateRechargeSerialStatus(List<Object[]> batchArgs){
        if(batchArgs.isEmpty()){
            return;
        }
        rechargeDao.batchUpdateRechargeSerialStatus(batchArgs);
    }

    /**
     * 批量更新退款单号状态
     * @param batchArgs
     */
    public void batchUpdateRefundSerialStatus(List<Object[]> batchArgs){
        if(batchArgs.isEmpty()){
            return;
        }
        rechargeDao.batchUpdateRefundSerialStatus(batchArgs);
    }

    /**
     * 维护退款任务
     * @param notifyTasks
     */
    public void saveNotifyTask(List<NotifyTask> notifyTasks){
        if(notifyTasks == null || notifyTasks.isEmpty()){
            return;
        }
        rechargeDao.batchCreateNotifyTask(notifyTasks);
    }

    /**
     * 查询回调任务
     * @return
     */
    public List<NotifyTask> getNotifyTask(){
        Optional<List<Map<String,Object>>> optionalMaps = Optional.ofNullable(rechargeDao.getNotifyTaskList());
        if(!optionalMaps.isPresent()){
            return Collections.emptyList();
        }
        List<NotifyTask> taskList = new ArrayList<>(optionalMaps.get().size());
        optionalMaps.get().forEach(map -> {
            NotifyTask task = NotifyTask.builder().serial(StringValueUtil.getValueByMap(map,"serial",""))
                    .notifyUrl(StringValueUtil.getValueByMap(map,"notify_url",""))
                    .message(StringValueUtil.getValueByMap(map,"serial_message","")).build();
            taskList.add(task);
        });
        return taskList;
    }

    /**
     * 修改回调任务状态
     * @param batchArgs
     */
    public void updateNotifyTask(List<Object[]> batchArgs){
        rechargeDao.updateNotifyTask(batchArgs);
    }

    /**
     * 充值订单回调接口处理
     * @param orderEntity 订单信息
     */
    public boolean analyzeRechargeStatus(WxOrderEntity orderEntity){
        RechargeEntity rechargeEntity = rechargeDao.getRecharge(orderEntity.getOutTradeNo(), RechargeEnum.CREATE.getStatus());
        if(rechargeEntity == null){
            return true;
        }

        List<Object[]> objectList = new ArrayList<>(1);
        List<NotifyTask> notifyTasks = new ArrayList<>(1);
        String tradeStatus = orderEntity.getTradeState();
        if(TradeStatusEnum.SUCCESS.getCode().equalsIgnoreCase(tradeStatus)){
            objectList.add(new Object[]{RechargeEnum.RECHARGE_SUCCESS.getStatus(),orderEntity.getTimeEnd(),orderEntity.getOpenId(),orderEntity.getTransactionId(),orderEntity.getOutTradeNo(),RechargeEnum.CREATE.getStatus()});
            notifyTasks.add(NotifyTask.builder().serial(orderEntity.getOutTradeNo())
                    .status(TradeStatusEnum.SUCCESS.getCode())
                    .message(TradeStatusEnum.SUCCESS.getDesc()).notifyUrl(rechargeEntity.getNotifyUrl())
                    .build());
        }else if(TradeStatusEnum.CLOSED.getCode().equalsIgnoreCase(tradeStatus)){
            objectList.add(new Object[]{RechargeEnum.CLOSE.getStatus(),null,orderEntity.getOpenId(),orderEntity.getTransactionId(),orderEntity.getOutTradeNo(),RechargeEnum.CREATE.getStatus()});
            notifyTasks.add(NotifyTask.builder().serial(orderEntity.getOutTradeNo()).status(TradeStatusEnum.CLOSED.getCode()).message(TradeStatusEnum.CLOSED.getDesc()).notifyUrl(rechargeEntity.getNotifyUrl()).build());
        }else if(TradeStatusEnum.PAYERROR.getCode().equalsIgnoreCase(tradeStatus)){
            objectList.add(new Object[]{RechargeEnum.FAIL.getStatus(),null,orderEntity.getOpenId(),orderEntity.getTransactionId(),orderEntity.getOutTradeNo(),RechargeEnum.CREATE.getStatus()});
            notifyTasks.add(NotifyTask.builder().notifyUrl(rechargeEntity.getNotifyUrl()).serial(orderEntity.getOutTradeNo()).status(TradeStatusEnum.PAYERROR.getCode()).message(TradeStatusEnum.PAYERROR.getDesc()).build());
        }
        if(!objectList.isEmpty()){
            rechargeDao.batchUpdateRechargeSerialStatus(objectList);
        }
        if(!notifyTasks.isEmpty()){
            rechargeDao.batchCreateNotifyTask(notifyTasks);
        }
        return true;
    }

    /**
     * 充值订单回调接口处理
     * @param orderEntity 订单信息
     */
    public boolean analyzeRefundStatus(WxRefundRespEntity orderEntity){
        RechargeEntity rechargeEntity = rechargeDao.getRecharge(orderEntity.getOutTradeNo(), RechargeEnum.REFUNDING.getStatus());
        if(rechargeEntity == null){
            return true;
        }
        rechargeDao.batchCreateRefundRecord(Arrays.asList(orderEntity));
        List<Object[]> objectList = new ArrayList<>(1);
        List<NotifyTask> notifyTasks = new ArrayList<>(1);
        String tradeStatus = orderEntity.getRefundStatus();
        if(RefundStatusEnum.SUCCESS.getCode().equals(tradeStatus)){
            objectList.add(new Object[]{RechargeEnum.REFUND_SUCCESS,orderEntity.getRefundSuccessTime(),orderEntity.getRefundId(),orderEntity.getOutRefundNo(),RechargeEnum.REFUNDING.getStatus()});
            notifyTasks.add(NotifyTask.builder().serial(orderEntity.getOutRefundNo())
                    .status(RefundStatusEnum.SUCCESS.getCode())
                    .message(RefundStatusEnum.SUCCESS.getDesc())
                    .notifyUrl(rechargeEntity.getNotifyUrl())
                    .build());
        }else if(RefundStatusEnum.REFUNDCLOSE.getCode().equals(tradeStatus)){
            objectList.add(new Object[]{RechargeEnum.RECHARGE_SUCCESS,null,orderEntity.getRefundId(),orderEntity.getOutRefundNo(),RechargeEnum.REFUNDING.getStatus()});
            notifyTasks.add(NotifyTask.builder().serial(orderEntity.getOutRefundNo())
                    .status(RefundStatusEnum.REFUNDCLOSE.getCode())
                    .message(RefundStatusEnum.REFUNDCLOSE.getDesc())
                    .notifyUrl(rechargeEntity.getNotifyUrl())
                    .build());
        }else if(RefundStatusEnum.CHANGE.getCode().equals(tradeStatus)){
            objectList.add(new Object[]{RechargeEnum.REFUND_FAIL,null,orderEntity.getRefundId(),orderEntity.getOutRefundNo(),RechargeEnum.REFUNDING.getStatus()});
            notifyTasks.add(NotifyTask.builder().serial(orderEntity.getOutRefundNo())
                    .status(RefundStatusEnum.CHANGE.getCode())
                    .message(RefundStatusEnum.CHANGE.getDesc())
                    .notifyUrl(rechargeEntity.getNotifyUrl())
                    .build());
        }
        if(!objectList.isEmpty()){
            rechargeDao.batchUpdateRefundSerialStatus(objectList);
        }
        if(!notifyTasks.isEmpty()){
            rechargeDao.batchCreateNotifyTask(notifyTasks);
        }
        return true;
    }

    /**
     * 批量保存微信退款记录
     * @param refundRespEntities 退款对象
     */
    public void batchCreateRefundRecord(List<WxRefundRespEntity> refundRespEntities){
        if(refundRespEntities.isEmpty()){
            return;
        }
        rechargeDao.batchCreateRefundRecord(refundRespEntities);
    }

}
