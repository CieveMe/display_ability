package cn.com.yyxx.yld.supply.entity.rm;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 入库记录单
 * </p>
 *
 * @author hz
 * @since 2019-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("rm_in_store_record_order")
public class RmInStoreRecordOrder extends Model<RmInStoreRecordOrder> {

    private static final long serialVersionUID = 1L;

    /**
     * 入库ID
     */
    @TableId(value = "isro_id", type = IdType.AUTO)
    private Integer isroId;

    /**
     * 入库记录编号
     */
    @TableField("isro_code")
    private String isroCode;

    /**
     * 入库类型
     */
    @TableField("isro_in_type")
    private String isroInType;

    /**
     * 终端类型
     */
    @TableField("isro_terminal_type")
    private String isroTerminalType;

    /**
     * 门店ID
     */
    @TableField("isro_sbi_id")
    private Integer isroSbiId;

    /**
     * 门店编号
     */
    @TableField("isro_sbi_code")
    private String isroSbiCode;

    /**
     * 门店名称
     */
    @TableField("isro_sbi_full_name")
    private String isroSbiFullName;

    /**
     * 设备ID
     */
    @TableField("isro_sdc_id")
    private Integer isroSdcId;

    /**
     * 设备名称
     */
    @TableField("isro_sdc_client_name")
    private String isroSdcClientName;

    /**
     * 入库编号
     */
    @TableField("isro_no")
    private String isroNo;

    /**
     * 商品类数
     */
    @TableField("isro_row_num")
    private Integer isroRowNum;

    /**
     * 商品件数
     */
    @TableField("isro_total_num")
    private BigDecimal isroTotalNum;

    /**
     * 总进货额
     */
    @TableField("isro_real_price")
    private BigDecimal isroRealPrice;

    /**
     * 操作人员ID
     */
    @TableField("isro_oper_user_id")
    private Long isroOperUserId;

    /**
     * 操作人员工号
     */
    @TableField("isro_oper_user_num")
    private String isroOperUserNum;

    /**
     * 操作人员姓名
     */
    @TableField("isro_oper_user_name")
    private String isroOperUserName;

    /**
     * 操作时间
     */
    @TableField("isro_time")
    private LocalDateTime isroTime;

    /**
     * 同步人员ID
     */
    @TableField("isro_sync_user_id")
    private Long isroSyncUserId;

    /**
     * 同步人员姓名
     */
    @TableField("isro_sync_user_name")
    private String isroSyncUserName;

    /**
     * 创建时间
     */
    @TableField("isro_create_time")
    private LocalDateTime isroCreateTime;

    /**
     * 修改时间
     */
    @TableField("isro_modify_time")
    private LocalDateTime isroModifyTime;

    /**
     * 是否删除
     */
    @TableField("isro_is_delete")
    private Boolean isroIsDelete;

    /**
     * 排序
     */
    @TableField("isro_sort")
    private Integer isroSort;

    /**
     * 描述
     */
    @TableField("isro_description")
    private String isroDescription;

    /**
     * 备注
     */
    @TableField("isro_remark")
    private String isroRemark;


    public static final String ISRO_ID = "isro_id";

    public static final String ISRO_CODE = "isro_code";

    public static final String ISRO_TERMINAL_TYPE = "isro_terminal_type";

    public static final String ISRO_SBI_ID = "isro_sbi_id";

    public static final String ISRO_SBI_FULL_NAME = "isro_sbi_full_name";

    public static final String ISRO_SDC_ID = "isro_sdc_id";

    public static final String ISRO_SDC_CLIENT_NAME = "isro_sdc_client_name";

    public static final String ISRO_NO = "isro_no";

    public static final String ISRO_ROW_NUM = "isro_row_num";

    public static final String ISRO_TOTAL_NUM = "isro_total_num";

    public static final String ISRO_REAL_PRICE = "isro_real_price";

    public static final String ISRO_OPER_USER_ID = "isro_oper_user_id";

    public static final String ISRO_OPER_USER_NUM = "isro_oper_user_num";

    public static final String ISRO_OPER_USER_NAME = "isro_oper_user_name";

    public static final String ISRO_TIME = "isro_time";

    public static final String ISRO_SYNC_USER_ID = "isro_sync_user_id";

    public static final String ISRO_SYNC_USER_NAME = "isro_sync_user_name";

    public static final String ISRO_CREATE_TIME = "isro_create_time";

    public static final String ISRO_MODIFY_TIME = "isro_modify_time";

    public static final String ISRO_IS_DELETE = "isro_is_delete";

    public static final String ISRO_SORT = "isro_sort";

    public static final String ISRO_DESCRIPTION = "isro_description";

    public static final String ISRO_REMARK = "isro_remark";

    @Override
    protected Serializable pkVal() {
        return this.isroId;
    }

}
