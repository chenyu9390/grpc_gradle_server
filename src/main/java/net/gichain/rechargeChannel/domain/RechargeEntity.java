package net.gichain.rechargeChannel.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * recharge_interface
 * 2020/6/15 13:41
 * 充值订单Entity
 *
 * @author ck
 * @since
 **/
@Builder
@Getter
@Setter
public class RechargeEntity {
    /**
     * 业务订单号
     */
    private String bussinessId;
    /**
     * 支付订单号
     */
    private String serial;
    /**
     * 交易类型
     */
    private String tradeType;
    /**
     * 总金额
     */
    private BigDecimal amount;
    /**
     * 金额币种
     */
    private String currency;
    /**
     * 交易失效时间
     */
    private String rechargeExpireTime;
    /**
     * 充值完成时间
     */
    private String rechargeEndTime;
    /**
     * 回调url
     */
    private String notifyUrl;
    /**
     * 商品描述
     */
    private String body;
    /**
     * 状态
     */
    private String status;

}
