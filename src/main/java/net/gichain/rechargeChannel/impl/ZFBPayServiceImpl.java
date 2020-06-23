package net.gichain.rechargeChannel.impl;

import net.gichain.genergy.grpc.response.Result;
import net.gichain.genergy.recharge.RechargeDto;
import net.gichain.genergy.recharge.RefundDto;
import net.gichain.rechargeChannel.service.PayService;
import org.springframework.stereotype.Component;

/**
 * PaySDK
 * 2020/6/13 16:46
 * 支付宝支付
 *
 * @author ck
 * @since
 **/
@Component("aliipay")
public class ZFBPayServiceImpl implements PayService {
    /**
     * 充值
     *
     * @param request
     * @return
     */
    @Override
    public Result pay(RechargeDto request) {
        return null;
    }

    /**
     * 退款
     *
     * @param request
     * @return
     */
    @Override
    public Result refund(RefundDto request) {
        return null;
    }
}
