package net.gichain.rechargeChannel.domain;

import lombok.Getter;

/**
 * recharge_interface
 * 2020/6/15 19:02
 * 交易状态枚举
 *
 * @author ck
 * @since
 **/
@Getter
public enum  TradeStatusEnum {
    SUCCESS("SUCCESS","支付成功"),
    REFUND("REFUND","转入退款"),
    NOTPAY("NOTPAY","未支付"),
    CLOSED("CLOSED","已关闭"),
    REVOKED("REVOKED","已撤销"),
    USERPAYING("USERPAYING","用户支付中"),
    PAYERROR("PAYERROR","支付失败");

    private String code;
    private String desc;

    TradeStatusEnum(String code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public static TradeStatusEnum getTradeStatus(String code){
        for(TradeStatusEnum tradeStatusEnum : TradeStatusEnum.values()){
            if(tradeStatusEnum.getCode().equalsIgnoreCase(code)){
                return tradeStatusEnum;
            }
        }
        return null;
    }
}
