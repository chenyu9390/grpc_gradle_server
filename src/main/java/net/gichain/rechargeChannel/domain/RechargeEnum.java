package net.gichain.rechargeChannel.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * recharge_interface
 * 2020/6/15 18:21
 * 充值订单状态
 *
 * @author ck
 * @since
 **/
@Getter
public enum RechargeEnum {

    CREATE("0","已下单"),
    RECHARGE_SUCCESS("10","充值成功"),
    REFUNDING("20","退款中"),
    REFUND_SUCCESS("30","退款成功"),
    CLOSE("40","订单已关闭"),
    FAIL("50","支付失败"),
    REFUND_FAIL("60","退款失败");

    private String status;
    private String desc;

    RechargeEnum (String status,String desc){
        this.status = status;
        this.desc = desc;
    }
}
