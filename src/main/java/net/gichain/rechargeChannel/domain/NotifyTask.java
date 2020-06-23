package net.gichain.rechargeChannel.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * recharge_interface
 * 2020/6/15 19:26
 * 回调任务对象
 *
 * @author ck
 * @since
 **/
@Getter
@Builder
public class NotifyTask {
    /**
     * 支付流水号
     */
    private String serial;
    /**
     * 是否成功,成功：success，失败：fail
     */
    private String status;
    /**
     * 失败原因
     */
    private String message;

    /**
     * 回调地址
     */
    private String notifyUrl;
}
