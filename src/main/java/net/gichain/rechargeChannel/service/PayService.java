package net.gichain.rechargeChannel.service;

import net.gichain.genergy.grpc.response.Result;
import net.gichain.genergy.recharge.RechargeDto;
import net.gichain.genergy.recharge.RefundDto;

/**
 * PaySDK
 * 2020/6/13 16:43
 * 充值接口
 *
 * @author ck
 * @since
 **/
public interface PayService {

    /**
     * 充值
     * @param request
     * @return
     */
    Result pay(RechargeDto request) throws Exception;

    /**
     * 退款
     * @param request
     * @return
     */
    Result refund(RefundDto request);
}
