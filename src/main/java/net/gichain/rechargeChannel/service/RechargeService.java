package net.gichain.rechargeChannel.service;

import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;
import net.devh.boot.grpc.server.service.GrpcService;
import net.gichain.genergy.grpc.response.Result;
import net.gichain.genergy.recharge.RechargeDto;
import net.gichain.genergy.recharge.RechargeServiceGrpc;
import net.gichain.genergy.recharge.RefundDto;
import net.gichain.rechargeChannel.SpringUtils;
import net.gichain.rechargeChannel.domain.ResponseStatus;
import net.gichain.rechargeChannel.util.ResultUtil;
import net.pay.entity.weixin.WxPayEntity;
import net.pay.sdk.WXSDK;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * PaySDK
 * 2020/6/13 16:15
 * 充值服务
 *
 * @author ck
 * @since
 **/
@GrpcService
public class RechargeService extends RechargeServiceGrpc.RechargeServiceImplBase {

    @Autowired
    private WXSDK wxpays;

    @SneakyThrows
    @Override
    public void recharge(RechargeDto request, StreamObserver<Result> responseObserver) {
        PayService payService = (PayService) SpringUtils.getBean(request.getRechargeType());
        Result result = null;
        if(payService == null){
            result = ResultUtil.getResult(ResponseStatus.FAIL,null,null);
        }else {
            result = payService.pay(request);
        }
        responseObserver.onNext(result);
        responseObserver.onCompleted();
    }

    @Override
    public void refund(RefundDto request, StreamObserver<Result> responseObserver) {
        PayService payService = (PayService) SpringUtils.getBean(request.getRechargeType());
        Result result = null;
        if(payService == null){
            result = ResultUtil.getResult(ResponseStatus.FAIL,null,null);
        }else {
            result = payService.refund(request);
        }
        responseObserver.onNext(result);
        responseObserver.onCompleted();
    }
}
