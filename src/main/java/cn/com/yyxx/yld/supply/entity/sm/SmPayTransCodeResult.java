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
 * 支付交易结果记录表
 * </p>
 *
 * @author hz
 * @since 2019-09-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sm_pay_trans_code_result")
public class SmPayTransCodeResult extends Model<SmPayTransCodeResult> {

    private static final long serialVersionUID = 1L;

    /**
     * 结果ID
     */
    @TableId(value = "ptcr_id", type = IdType.AUTO)
    private Long ptcrId;

    /**
     * 门店ID
     */
    @TableField("prcr_mpso_sbi_id")
    private Integer prcrMpsoSbiId;

    /**
     * 订单编号
     */
    @TableField("ptcr_mpso_order_no")
    private String ptcrMpsoOrderNo;

    /**
     * 订单ID
     */
    @TableField("ptcr_mpso_id")
    private Long ptcrMpsoId;

    /**
     * 接口类型
     */
    @TableField("ptcr_interface_type")
    private String ptcrInterfaceType;

    /**
     * 报文内容
     */
    @TableField("ptcr_request_content")
    private String ptcrRequestContent;

    /**
     * 报文内容
     */
    @TableField("ptcr_response_content")
    private String ptcrResponseContent;

    /**
     * 扩展内容
     */
    @TableField("ptcr_ext_content")
    private String ptcrExtContent;

    /**
     * 是否成功
     */
    @TableField("ptcr_is_success")
    private Boolean ptcrIsSuccess;

    /**
     * 交易结果码
     */
    @TableField("ptcr_result_code")
    private String ptcrResultCode;

    /**
     * 失败原因
     */
    @TableField("ptcr_fail_message")
    private String ptcrFailMessage;

    /**
     * 创建时间
     */
    @TableField("ptcr_create_time")
    private LocalDateTime ptcrCreateTime;

    /**
     * 修改时间
     */
    @TableField("ptcr_modify_time")
    private LocalDateTime ptcrModifyTime;

    /**
     * 是否删除
     */
    @TableField("ptcr_is_delete")
    private Boolean ptcrIsDelete;

    /**
     * 排序
     */
    @TableField("ptcr_sort")
    private Integer ptcrSort;

    /**
     * 描述
     */
    @TableField("ptcr_description")
    private String ptcrDescription;


    public static final String PTCR_ID = "ptcr_id";

    public static final String PRCR_MPSO_SBI_ID = "prcr_mpso_sbi_id";

    public static final String PTCR_MPSO_ORDER_NO = "ptcr_mpso_order_no";

    public static final String PTCR_MPSO_ID = "ptcr_mpso_id";

    public static final String PTCR_INTERFACE_TYPE = "ptcr_interface_type";

    public static final String PTCR_REQUEST_CONTENT = "ptcr_request_content";

    public static final String PTCR_RESPONSE_CONTENT = "ptcr_response_content";

    public static final String PTCR_EXT_CONTENT = "ptcr_ext_content";

    public static final String PTCR_IS_SUCCESS = "ptcr_is_success";

    public static final String PTCR_RESULT_CODE = "ptcr_result_code";

    public static final String PTCR_FAIL_MESSAGE = "ptcr_fail_message";

    public static final String PTCR_CREATE_TIME = "ptcr_create_time";

    public static final String PTCR_MODIFY_TIME = "ptcr_modify_time";

    public static final String PTCR_IS_DELETE = "ptcr_is_delete";

    public static final String PTCR_SORT = "ptcr_sort";

    public static final String PTCR_DESCRIPTION = "ptcr_description";

    @Override
    protected Serializable pkVal() {
        return this.ptcrId;
    }

}
