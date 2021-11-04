package cn.com.yyxx.yld.supply.entity.sm;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 订单被扫交易结果记录表
 * </p>
 *
 * @author hz
 * @since 2019-03-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sm_order_trans_code_result")
public class SmOrderTransCodeResult extends Model<SmOrderTransCodeResult> {

    private static final long serialVersionUID = 1L;

    /**
     * 结果ID
     */
    @TableId(value = "otcr_id", type = IdType.AUTO)
    private Long otcrId;

    /**
     * 订单ID
     */
    @TableField("otcr_mpso_id")
    private Long otcrMpsoId;

    /**
     * 结果编号
     */
    @TableField("otcr_no")
    private String otcrNo;

    /**
     * 报文类型
     */
    @TableField("otcr_message_type")
    private String otcrMessageType;

    /**
     * 报文内容
     */
    @TableField("otcr_content")
    private String otcrContent;

    /**
     * 接口类型
     */
    @TableField("otcr_interface_type")
    private String otcrInterfaceType;

    /**
     * 交易凭证号
     */
    @TableField("otcr_trans_no")
    private String otcrTransNo;

    /**
     * 交易批次号
     */
    @TableField("otcr_batch_no")
    private String otcrBatchNo;

    /**
     * 交易订单号
     */
    @TableField("otcr_trade_order_no")
    private String otcrTradeOrderNo;

    /**
     * 是否成功
     */
    @TableField("otcr_is_success")
    private Boolean otcrIsSuccess;

    /**
     * 交易结束码
     */
    @TableField("otcr_result_code")
    private String otcrResultCode;

    /**
     * 失败原因
     */
    @TableField("otcr_fail_message")
    private String otcrFailMessage;

    /**
     * 创建时间
     */
    @TableField("otcr_create_time")
    private LocalDateTime otcrCreateTime;

    /**
     * 修改时间
     */
    @TableField("otcr_modify_time")
    private LocalDateTime otcrModifyTime;

    /**
     * 是否删除
     */
    @TableField("otcr_is_delete")
    private Boolean otcrIsDelete;

    /**
     * 排序
     */
    @TableField("otcr_sort")
    private Integer otcrSort;

    /**
     * 描述
     */
    @TableField("otcr_description")
    private String otcrDescription;

    /**
     * 备注
     */
    @TableField("otcr_remark")
    private String otcrRemark;


    public static final String OTCR_ID = "otcr_id";

    public static final String OTCR_MPSO_ID = "otcr_mpso_id";

    public static final String OTCR_NO = "otcr_no";

    public static final String OTCR_MESSAGE_TYPE = "otcr_message_type";

    public static final String OTCR_CONTENT = "otcr_content";

    public static final String OTCR_INTERFACE_TYPE = "otcr_interface_type";

    public static final String OTCR_TRANS_NO = "otcr_trans_no";

    public static final String OTCR_BATCH_NO = "otcr_batch_no";

    public static final String OTCR_TRADE_ORDER_NO = "otcr_trade_order_no";

    public static final String OTCR_IS_SUCCESS = "otcr_is_success";

    public static final String OTCR_RESULT_CODE = "otcr_result_code";

    public static final String OTCR_FAIL_MESSAGE = "otcr_fail_message";

    public static final String OTCR_CREATE_TIME = "otcr_create_time";

    public static final String OTCR_MODIFY_TIME = "otcr_modify_time";

    public static final String OTCR_IS_DELETE = "otcr_is_delete";

    public static final String OTCR_SORT = "otcr_sort";

    public static final String OTCR_DESCRIPTION = "otcr_description";

    public static final String OTCR_REMARK = "otcr_remark";

    @Override
    protected Serializable pkVal() {
        return this.otcrId;
    }

}
