package cn.com.yyxx.yld.supply.data.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 门店基础信息 </p
 *
 * @author linmeng
 * @since 2018/12/25 10:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("门店基础信息")
public class SmStoreBasicInfoVO extends SmStoreProductRuleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 门店ID
     */
    @ApiModelProperty(value = "门店ID")
    private Integer sbiId;


    /**
     * 流程id
     */
    private String sbiProcessId;

    /**
     * 归属公司
     */
    @ApiModelProperty("归属公司")
    private Integer sbiCompany;

    /**
     * 归属公司名称
     */
    @ApiModelProperty("归属公司名称")
    private String sbiCompanyName;

    /**
     * 门店状态
     */
    @ApiModelProperty("门店状态")
    private String sbiStatus;

    /**
     * 门店编号
     */
    @ApiModelProperty("门店编号")
    private String sbiCode;

    /**
     * 门店类别名称
     */
    @ApiModelProperty("门店类别名称")
    private String sbiCategoryName;

    /**
     * 门店类型名称
     */
    @ApiModelProperty("门店类型名称")
    private String sbiChildTypeName;

    /**
     * 营业模式名称
     */
    @ApiModelProperty(value = "营业模式名称")
    private String sbiBusinessModelName;

    /**
     * 组成形式名称
     */
    @ApiModelProperty("组成形式名称")
    private String sbiPropertyName;

    /**
     * 门店级别名称
     */
    @ApiModelProperty("门店级别名称")
    private String sbiLevelName;

    /**
     * 门店名称
     */
    @ApiModelProperty("门店名称")
    private String sbiFullName;

    /**
     * 门店简称
     */
    @ApiModelProperty("门店简称")
    private String sbiShortName;

    /**
     * 社会统一代码
     */
    @ApiModelProperty("社会统一代码")
    private String sbiUnifiedSocialCreditCode;

    /**
     * 烟草证号
     */
    @ApiModelProperty("烟草证号")
    private String tobaccoNo;

    /**
     * 店主姓名
     */
    @ApiModelProperty("店主姓名")
    private String sbiOwnerName;

    /**
     * 店主手机号
     */
    @ApiModelProperty("店主手机号")
    private String sbiOwnerPhone;

    /**
     * 店主身份证号
     */
    @ApiModelProperty("店主身份证号")
    private String ownerIdCard;

    /**
     * 开户人姓名
     */
    @ApiModelProperty(value = "开户人姓名")
    private String bankUserName;

    /**
     * 开户人身份证号
     */
    @ApiModelProperty("开户人身份证号")
    private String bankIDCard;

    /**
     * 银行卡号
     */
    @ApiModelProperty("银行卡号")
    private String bankNo;

    /**
     * 开户银行名称
     */
    @ApiModelProperty(value = "开户银行名称")
    private String bankName;

    /**
     * 门店地址
     */
    @ApiModelProperty("门店地址")
    private String sbiAddress;

    /**
     * 门店简介
     */
    @ApiModelProperty("门店简介")
    private String sbiSummary;

    /**
     * 所在片区
     */
    @ApiModelProperty("所在片区")
    private String areaName;

    /**
     * 片区经理
     */
    @ApiModelProperty("片区经理")
    private String areaManager;

    /**
     * 片区经理电话
     */
    @ApiModelProperty("片区经理电话")
    private String areaManagerTell;

    /**
     * 审核失败原因
     */
    @ApiModelProperty("审核失败原因")
    private String sbiReviewFailureReason;


    /**
     * 门店状态名称
     */
    @ApiModelProperty("门店状态名称")
    private String sbiStatusName;

    /**
     * 门店状态code
     */
    private String sbiStatusCode;
    /**
     * 门店经度
     */
    @ApiModelProperty("门店经度")
    private String longitude;

    /**
     * 门店纬度
     */
    @ApiModelProperty("门店纬度")
    private String latitude;


    /**
     * 门店类别
     */
    @ApiModelProperty("门店类别")
    private String sbiCategory;


    /**
     * 门店类型
     */
    @ApiModelProperty("门店类型")
    private String sbiChildType;


    /**
     * 组成形式
     */
    @ApiModelProperty("组成形式")
    private String sbiProperty;

    /**
     * 营业模式
     */
    @ApiModelProperty(value = "营业模式")
    private String sbiBusinessModel;

    /**
     * 营业执照类型
     */
    @ApiModelProperty("营业执照类型")
    private String sdiBusinessLicenseType;

    /**
     * 门店级别
     */
    @ApiModelProperty("门店级别")
    private String sbiLevel;


    /**
     * 店主邮箱
     */
    @ApiModelProperty("店主邮箱")
    private String sbiOwnerEmail;

    /**
     * 省份ID
     */
    @ApiModelProperty("省份ID")
    private Integer sbiProvinceId;

    /**
     * 省份名称
     */
    @ApiModelProperty("省份名称")
    private String sbiProvinceName;

    /**
     * 城市ID
     */
    @ApiModelProperty("城市ID")
    private Integer sbiCityId;

    /**
     * 城市名称
     */
    @ApiModelProperty("城市名称")
    private String sbiCityName;

    /**
     * 区县ID
     */
    @ApiModelProperty("区县ID")
    private Integer sbiAreaId;

    /**
     * 区县名称
     */
    @ApiModelProperty("区县名称")
    private String sbiAreaName;

    /**
     * 门店创建者
     */
    @ApiModelProperty("门店创建者")
    private Long sbiCreaterId;
    /**
     * 门店图片
     */
    @ApiModelProperty("门店图片")
    private String sbiPicture;

    /**
     * 是否开通微信实名认证
     */
    private Boolean weChatIsSuccess;


    /**
     * 设备限制数量
     */
    @ApiModelProperty("设备限制数量")
    private Integer sbiDeviceLimitNum;

    /**
     * 员工限制数量
     */
    @ApiModelProperty("员工限制数量")
    private Integer sbiEmployeeLimitNum;

    /**
     * 是否激活
     */
    @ApiModelProperty("是否激活")
    private Boolean sbiIsActive;

    /**
     * 是否删除
     */
    @ApiModelProperty("是否删除")
    private Boolean sbiIsDelete;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime sbiGmtCreate;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private LocalDateTime sbiGmtModified;

    /**
     * 排序
     */
    @ApiModelProperty("排序")
    private Integer sbiSort;


    /**
     * 片区省名称
     */
    @ApiModelProperty("片区省名称")
    private String areaProvinceName;

    /**
     * 片区市名称
     */
    @ApiModelProperty("片区市名称")
    private String areaCityName;

    /**
     * 片区县名称
     */
    @ApiModelProperty("片区县名称")
    private String areaAreaName;


    /**
     * 商户号
     */
    @ApiModelProperty("商户号")
    private String shopNo;

    /**
     * 终端号
     */
    @ApiModelProperty("终端号")
    private String termNos;

    /**
     * 是否存量商户进件
     */
    @ApiModelProperty("银商是否签约")
    private Boolean sbiIsOk;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime creatTime;

    /**
     * 安装时间
     */
    @ApiModelProperty("安装时间")
    private LocalDateTime installTime;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private LocalDateTime modifyTime;


    /**
     * 终端号
     */
    @ApiModelProperty("是否推送微信")
    private String sendwxmsg;

    /**
     * 创建人id
     */
    public SmStoreBasicInfoVO(Integer sbiProvinceId, Integer sbiCityId, Integer sbiAreaId,
        String sbiFullName, String tobaccoNo, String sbiCode, String sbiCategory,
        String sbiChildType) {
        this.sbiProvinceId = sbiProvinceId;
        this.sbiCityId = sbiCityId;
        this.sbiAreaId = sbiAreaId;
        this.sbiFullName = sbiFullName;
        this.tobaccoNo = tobaccoNo;
        this.sbiCode = sbiCode;
        this.sbiCategory = sbiCategory;
        this.sbiChildType = sbiChildType;
    }

}
