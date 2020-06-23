package net.gichain.rechargeChannel.domain;

import lombok.Getter;

/**
 * recharge_interface
 * 2020/6/15 22:03
 * 退款订单状态枚举
 *
 * @author ck
 * @since
 **/
@Getter
public enum  RefundStatusEnum {

    SUCCESS("SUCCESS","支付成功"),
    REFUNDCLOSE("REFUNDCLOSE","退款关闭"),
    PROCESSING("PROCESSING","退款处理中"),
    CHANGE("CHANGE","退款异常");
    private String code;
    private String desc;

    RefundStatusEnum(String code,String desc){
        this.code = code;
        this.desc = desc;
    }
}
