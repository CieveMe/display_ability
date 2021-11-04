package cn.com.yyxx.yld.supply.data.vo;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * 支付结果
 * </p
 *
 * @author linmeng
 * @date 2020/4/28 10:21
 * @since 0.11.0
 */
@Data
@ApiModel(value = "支付请求返回VO对象", description = "支付请求返回VO对象")
public class ResponsePayVO implements Serializable {

    private static final long serialVersionUID = -5592757250132437299L;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final String OK = "0003";

    public ResponsePayVO() {
    }

    public ResponsePayVO(String errCode, String errInfo) {
        this.errCode = errCode;
        this.errInfo = errInfo;
    }

    @ApiModelProperty(value = "订单ID", name = "orderId")
    private String orderId;

    @ApiModelProperty(value = "会员手机号", name = "phone")
    private String phone;

    @ApiModelProperty(value = "支付渠道", name = "channelName")
    private String channelName;

    @ApiModelProperty(value = "返回编码", name = "errCode")
    private String errCode;

    @ApiModelProperty(value = "返回信息", name = "errInfo")
    private String errInfo;

    @ApiModelProperty(value = "返回时间", name = "reTime")
    private String reTime;

    @ApiModelProperty(value = "订单编号", name = "orderNo")
    private String orderNo;



    @ApiModelProperty(value = "门店ID", name = "storeId")
    private Integer storeId;

    @ApiModelProperty(value = "返回的整个包体", name = "reMsgBody")
    private String reMsgBody;

    @ApiModelProperty(value = "支付状态", name = "payStatus")
    private String payStatus;

    @ApiModelProperty(value = "金额", name = "amount")
    private Long amount;

    @ApiModelProperty(value = "支付方式", name = "payMode")
    private String payMode;

    @ApiModelProperty(value = "微信openid", name = "openid")
    private String openid;

    @ApiModelProperty(value = "三方订单", name = "threeOrderNo")
    private String threeOrderNo;

    @ApiModelProperty(value = "平台订单号", name = "tradeNo")
    private String tradeNo;

    @ApiModelProperty(value = "小程序支付需要参数", name = "payInfo")
    private Map<String, String> payInfo;

    private String notifyUrl;

    private String newOrderNo;

    public boolean isOk() {
        return !StrUtil.equals(OK, this.errCode);
    }


}
