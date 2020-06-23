package net.gichain.rechargeChannel.dao.impl;

import net.gichain.rechargeChannel.dao.BaseDao;
import net.gichain.rechargeChannel.dao.RechargeDao;
import net.gichain.rechargeChannel.domain.NotifyTask;
import net.gichain.rechargeChannel.domain.RechargeEntity;
import net.gichain.rechargeChannel.domain.RechargeEnum;
import net.gichain.rechargeChannel.util.StringValueUtil;
import net.pay.entity.weixin.WxRefundRespEntity;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

/**
 * recharge_interface
 * 2020/6/15 13:40
 * 数据库实现
 *
 * @author ck
 * @since
 **/
@Repository
public class RechargeDaoImpl extends BaseDao implements RechargeDao {


    @Override
    public int saveRecharge(RechargeEntity entity) {
        String sql = "INSERT IGNORE INTO `recharge_wx`(`bussiness_id`, `serial`, `trade_type`, `amount`, `currency`, `recharge_expire_time`, `notify_url`, `body`) VALUES (?,?,?,?,?,?,?,?);";
        return create(sql,new Object[]{entity.getBussinessId(),entity.getSerial(),entity.getTradeType(),entity.getAmount(),entity.getCurrency(),entity.getRechargeExpireTime(),entity.getNotifyUrl(),entity.getBody()});
    }

    /**
     * 查询充值记录
     * @param serial 商户订单号
     * @return
     */
    @Override
    public RechargeEntity getRecharge(String serial,String status) {
        StringBuilder builder = new StringBuilder("SELECT bussiness_id,serial,trade_type,amount,currency,notify_url,`status` FROM recharge_wx WHERE serial = ? ");
        List<Object> objectList = new ArrayList<>(2);
        objectList.add(serial);
        if(!StringValueUtil.isEmpty(status)){
            builder.append(" AND `status` = ?");
            objectList.add(status);
        }
        List<Map<String, Object>> mapList = queryForList(builder.toString(),objectList.toArray());
        RechargeEntity entity = null;
        if(mapList != null){
            Map<String, Object> map = mapList.get(0);
            entity = RechargeEntity.builder()
                    .bussinessId(StringValueUtil.getValueByMap(map,"bussiness_id",""))
                    .serial(StringValueUtil.getValueByMap(map,"serial",""))
                    .tradeType(StringValueUtil.getValueByMap(map,"trade_type",""))
                    .amount(new BigDecimal(StringValueUtil.getValueByMap(map,"amount", BigDecimal.ZERO)))
                    .currency(StringValueUtil.getValueByMap(map,"currency", BigDecimal.ZERO))
                    .notifyUrl(StringValueUtil.getValueByMap(map,"notify_url", BigDecimal.ZERO))
                    .status(StringValueUtil.getValueByMap(map,"status",""))
                    .build();

        }
        return entity;
    }

    /**
     * 充值订单退款
     * @param serial 支付流水号
     */
    @Override
    public int refundSerial(String serial) {
        String sql = "UPDATE recharge_wx SET `status` = ? WHERE serial = ? AND `status` = ?";
        return update(sql,new Object[]{RechargeEnum.REFUNDING,serial,RechargeEnum.RECHARGE_SUCCESS});
    }

    /**
     * 已下单或申请退款的订单列表
     * @return
     */
    @Override
    public Optional<List<Map<String, Object>>> getQuerySerialList() {
        String sql = "SELECT serial,`status`,notify_url,refund_notify_url FROM `recharge_wx` WHERE `status` IN (:params) AND modify_time < DATE_SUB(NOW(),INTERVAL 2 MINUTE) ORDER BY id DESC LIMIT 1000;";
        Map<String,List<String>> params = new HashMap<>(1);
        params.put("params",Arrays.asList(RechargeEnum.CREATE.getStatus(),RechargeEnum.REFUNDING.getStatus()));
        return Optional.ofNullable(queryForList(sql,params));
    }

    /**
     * 批量更新订单状态
     * @param batchArgs
     * @return
     */
    @Override
    public int[] batchUpdateRechargeSerialStatus(List<Object[]> batchArgs) {
        String sql = "UPDATE recharge_wx SET `status` = ?,recharge_end_time=?,open_id=?,transaction_id=? WHERE serial = ? AND `status` = ?";
        return batchUpdate(sql,batchArgs);
    }

    /**
     * 批量更新退款订单状态
     *
     * @param batchArgs
     * @return
     */
    @Override
    public int[] batchUpdateRefundSerialStatus(List<Object[]> batchArgs) {
        String sql = "UPDATE recharge_wx SET `status` = ?,refund_success_time=?,refund_id=? WHERE serial = ? AND `status` = ?";
        return batchUpdate(sql,batchArgs);
    }

    /**
     * 批量维护回调任务
     * @param taskList
     * @return
     */
    @Override
    public int[] batchCreateNotifyTask(List<NotifyTask> taskList) {
        String sql = "INSERT IGNORE INTO `notify_task`( `serial`, `notify_url`, `serial_status`, `serial_message`) VALUES (?,?,?,?);";
        List<Object[]> batchArgs = new ArrayList<>(taskList.size());
        taskList.forEach(task -> {
            if(!StringValueUtil.isEmpty(task.getNotifyUrl())){
                batchArgs.add(new Object[]{task.getSerial(),task.getNotifyUrl(),task.getStatus(),task.getMessage()});
            }

        });
        return batchCreate(sql,batchArgs);
    }

    /**
     * 查询回调任务
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> getNotifyTaskList() {
        String sql = "SELECT serial,notify_url,serial_message FROM `notify_task` WHERE `status` = 0 AND retry_times < 10";
        return queryForList(sql,new Object[]{});
    }

    /**
     * 修改回调任务状态
     * @param batchArgs
     */
    @Override
    public void updateNotifyTask(List<Object[]> batchArgs) {
        String sql = "UPDATE notify_task SET `status` = ?,retry_times = retry_times+1,message = ? WHERE serial = ?";
        batchUpdate(sql,batchArgs);
    }

    /**
     * 批量保存微信退款记录
     *
     * @param refundRespEntities 退款对象
     */
    @Override
    public void batchCreateRefundRecord(List<WxRefundRespEntity> refundRespEntities) {
        String sql = "INSERT INTO `refund_wx`( `serial`, `refund_no`, `refund_account`, `refund_channel`, `refund_id`, `refund_recv_account`, `refund_status`, `total_amount`, `refund_amount`,`refund_success_time`) VALUES (?,?,?,?,?,?,?,?,?,?)";
        List<Object[]> batchArgs = new ArrayList<>();
        refundRespEntities.forEach(entity -> {
            batchArgs.add(new Object[]{entity.getOutTradeNo(),entity.getOutRefundNo(),entity.getRefundAccount(),entity.getRefundChannel(),entity.getRefundId(),entity.getRefundRecvAccount(),
                    entity.getRefundStatus(),entity.getTotalFee(),entity.getRefundFee(),entity.getRefundSuccessTime()});
        });
        batchCreate(sql,batchArgs);
    }
}
