package net.gichain.rechargeChannel.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * 接口返回调用状态
 * @author CHEN
 */
@Getter
public enum ResponseStatus {
    SUCCESS(0,"成功",true),
    FAIL(10,"支付方式异常",false),
    SERIAL_NOT_EXIST(20,"支付流水号不存在",false),
    SERIAL_NOT_REFUND(30,"订单已退款或未支付",false);
    private int code;
    private String message;
    private boolean success;

    ResponseStatus(int code, String message,boolean success){
        this.code = code;
        this.message = message;
        this.success = success;
    }
}
