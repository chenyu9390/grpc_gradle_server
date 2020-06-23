package net.gichain.rechargeChannel.util;

import net.gichain.genergy.grpc.response.Result;
import net.gichain.rechargeChannel.domain.ResponseStatus;
import org.springframework.util.StringUtils;

/**
 * PaySDK
 * 2020/6/13 17:10
 * 返回结果工具类
 *
 * @author ck
 * @since
 **/
public class ResultUtil {

    /**
     * 返回结果工具类
     * @param responseStatus 消息枚举
     * @param message 消息内容
     * @return
     */
    public static Result getResult(ResponseStatus responseStatus,String message,String data){
        Result result = Result.newBuilder().setCode(responseStatus.getCode())
                .setMessage(StringUtils.isEmpty(message)?responseStatus.getMessage():message)
                .setSuccess(responseStatus.isSuccess())
                .setData(data)
                .build();
        return result;
    }
}
