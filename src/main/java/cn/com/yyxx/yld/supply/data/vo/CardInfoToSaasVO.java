package cn.com.yyxx.yld.supply.data.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @Description 卡信息ToSaas响应VO
 * @date 2021/1/20 11:15
 * @author Administrator
 */
@Data
@ApiModel
public class CardInfoToSaasVO implements Serializable {

    private static final long serialVersionUID = 8855060618755768845L;
    /**
     * 卡号
     */
    @ApiModelProperty("卡号")
    private String cardNo;
    /**
     * 商户id
     */
    @ApiModelProperty("商户id")
    private Long storeId;
    /**
     * 商户名称
     */
    @ApiModelProperty("商户名称")
    private String storeName;
    /**
     * 用户编号
     */
    @ApiModelProperty("用户编号")
    private String userNo;
    /**
     * 姓名
     */
    @ApiModelProperty("姓名")
    private String userName;
    /**
     * 会员手机号
     */
    @ApiModelProperty("会员手机号")
    private String mobilePhone;
    /**
     * 额度
     */
    @ApiModelProperty("额度")
    private Long quotaAmount = 0L;
    /**
     * 折扣
     */
    @ApiModelProperty("折扣")
    private Long discount;
    /**
     * 状态(1未激活 2审核中 3正常 4审核失败 5停用)
     */
    @ApiModelProperty("状态(1未激活 2审核中 3正常 4审核失败 5停用)")
    private Integer status;
    /**
     * 是否启用
     */
    @ApiModelProperty("是否启用")
    private Boolean flag;

}
