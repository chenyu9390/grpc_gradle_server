package net.gichain.rechargeChannel.dao;

import net.gichain.rechargeChannel.domain.NotifyTask;
import net.gichain.rechargeChannel.domain.RechargeEntity;
import net.pay.entity.weixin.WxRefundRespEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * recharge_interface
 * 2020/6/15 13:40
 * Dao
 *
 * @author ck
 * @since
 **/
public interface RechargeDao {

    /**
     * 保存充值记录
     * @param entity
     * @return
     */
    int saveRecharge(RechargeEntity entity);

    /**
     * 查询充值记录
     * @param serial 支付流水号
     * @return
     */
    RechargeEntity getRecharge(String serial,String status);

    /**
     * 充值订单退款
     * @param serial 支付流水号
     */
    int refundSerial(String serial);

    /**
     * 已下单或申请退款的订单列表
     * @return
     */
    Optional<List<Map<String,Object>>> getQuerySerialList();

    /**
     * 批量更新订单状态
     * @param batchArgs
     * @return
     */
    int[] batchUpdateRechargeSerialStatus(List<Object[]> batchArgs);

    /**
     * 批量更新退款订单状态
     * @param batchArgs
     * @return
     */
    int[] batchUpdateRefundSerialStatus(List<Object[]> batchArgs);

    /**
     * 批量维护回调任务
     * @param taskList
     * @return
     */
    int[] batchCreateNotifyTask(List<NotifyTask> taskList);

    /**
     * 查询回调任务
     * @return
     */
    List<Map<String,Object>> getNotifyTaskList();

    /**
     * 修改回调任务状态
     * @param batchArgs
     */
    void updateNotifyTask(List<Object[]> batchArgs);
    /**
     * 批量保存微信退款记录
     * @param refundRespEntities 退款对象
     */
    void batchCreateRefundRecord(List<WxRefundRespEntity> refundRespEntities);
}
