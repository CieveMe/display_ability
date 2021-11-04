package cn.com.yyxx.yld.supply.data.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p></p>
 *
 * @author haoch
 * @version 1.0.0
 * @program yld-supply-chain-v2
 * @description
 * @date 2021-02-01 09:18
 * @since 0.1.0
 **/
@Data
public class RmInStoreRecordOrderVO implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 入库ID
     */
//    @TableId(value = "isro_id", type = IdType.AUTO)
    private Integer isroId;

    /**
     * 入库记录编号
     */
//    @TableField("isro_code")
    private String isroCode;

    /**
     * 入库类型
     */
//    @TableField("isro_in_type")
    private String isroInType;

    /**
     * 终端类型
     */
//    @TableField("isro_terminal_type")
    private String isroTerminalType;

    /**
     * 门店ID
     */
//    @TableField("isro_sbi_id")
    private Integer isroSbiId;

    /**
     * 门店编号
     */
//    @TableField("isro_sbi_code")
    private String isroSbiCode;

    /**
     * 门店名称
     */
//    @TableField("isro_sbi_full_name")
    private String isroSbiFullName;

    /**
     * 设备ID
     */
//    @TableField("isro_sdc_id")
    private Integer isroSdcId;

    /**
     * 设备名称
     */
//    @TableField("isro_sdc_client_name")
    private String isroSdcClientName;

    /**
     * 入库编号
     */
//    @TableField("isro_no")
    private String isroNo;

    /**
     * 商品类数
     */
//    @TableField("isro_row_num")
    private Integer isroRowNum;

    /**
     * 商品件数
     */
//    @TableField("isro_total_num")
    private BigDecimal isroTotalNum;

    /**
     * 总进货额
     */
//    @TableField("isro_real_price")
    private BigDecimal isroRealPrice;

    /**
     * 操作人员ID
     */
//    @TableField("isro_oper_user_id")
    private Long isroOperUserId;

    /**
     * 操作人员工号
     */
//    @TableField("isro_oper_user_num")
    private String isroOperUserNum;

    /**
     * 操作人员姓名
     */
//    @TableField("isro_oper_user_name")
    private String isroOperUserName;

    /**
     * 操作时间
     */
//    @TableField("isro_time")
    private LocalDateTime isroTime;

    /**
     * 同步人员ID
     */
//    @TableField("isro_sync_user_id")
    private Long isroSyncUserId;

    /**
     * 同步人员姓名
     */
//    @TableField("isro_sync_user_name")
    private String isroSyncUserName;

    /**
     * 创建时间
     */
//    @TableField("isro_create_time")
    private LocalDateTime isroCreateTime;

    /**
     * 修改时间
     */
//    @TableField("isro_modify_time")
    private LocalDateTime isroModifyTime;

    /**
     * 是否删除
     */
//    @TableField("isro_is_delete")
    private Boolean isroIsDelete;

    /**
     * 排序
     */
//    @TableField("isro_sort")
    private Integer isroSort;

    /**
     * 描述
     */
//    @TableField("isro_description")
    private String isroDescription;

    /**
     * 备注
     */
//    @TableField("isro_remark")
    private String isroRemark;



}
