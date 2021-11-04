package cn.com.yyxx.yld.supply.data.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author linmeng
 * @version 1.0
 * @since 2019/2/13 20:40
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SmOrderTransCodeResultVO implements Serializable {

    private static final long serialVersionUID = -3340508964008518732L;
    /**
     * 结果编号
     */
    private String otcrNo;

    /**
     * 订单编号
     */
    private String otcrMpsoNo;

    /**
     * 报文类型
     */
    private String otcrMessageType;

    /**
     * 报文内容
     */
    private String otcrContent;

    /**
     * 接口类型
     */
    private String otcrInterfaceType;

    /**
     * 交易凭证号
     */
    private String otcrTransNo;

    /**
     * 交易批次号
     */
    private String otcrBatchNo;

    /**
     * 交易订单号
     */
    private String otcrTradeOrderNo;

    /**
     * 是否成功
     */
    private Boolean otcrIsSuccess;

    /**
     * 交易结束码
     */
    private String otcrResultCode;

    /**
     * 失败原因
     */
    private String otcrFailMessage;

    /**
     * 创建时间
     */
    private LocalDateTime otcrCreateTime;

    /**
     * 修改时间
     */
    private LocalDateTime otcrModifyTime;

    /**
     * 是否删除
     */
    private Boolean otcrIsDelete;

    /**
     * 排序
     */
    private Integer otcrSort;

    /**
     * 描述
     */
    private String otcrDescription;

    /**
     * 备注
     */
    private String otcrRemark;
}
