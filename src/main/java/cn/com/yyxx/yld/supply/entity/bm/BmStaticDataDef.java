package cn.com.yyxx.yld.supply.entity.bm;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author hk
 * @since 2018-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bm_static_data_def")
public class BmStaticDataDef extends Model<BmStaticDataDef> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "sdd_id", type = IdType.UUID)
    private String sddId;

    @TableField("sdd_ddd_id")
    private String sddDddId;

    @TableField("sdd_code")
    private String sddCode;

    @TableField("sdd_name")
    private String sddName;

    @TableField("sdd_value")
    private String sddValue;

    @TableField("sdd_rel_ddd")
    private String sddRelDdd;

    @TableField("sdd_rel_sdd")
    private String sddRelSdd;

    @TableField("sdd_ext_1")
    private String sddExt1;

    @TableField("sdd_ext_2")
    private String sddExt2;

    @TableField("sdd_ext_3")
    private String sddExt3;

    @TableField("sdd_is_delete")
    private Boolean sddIsDelete;

    @TableField("sdd_desc")
    private String sddDesc;

    @TableField("sdd_sort")
    private Integer sddSort;

    @TableField("sdd_remark")
    private String sddRemark;


    public static final String SDD_ID = "sdd_id";

    public static final String SDD_DDD_ID = "sdd_ddd_id";

    public static final String SDD_CODE = "sdd_code";

    public static final String SDD_NAME = "sdd_name";

    public static final String SDD_VALUE = "sdd_value";

    public static final String SDD_REL_DDD = "sdd_rel_ddd";

    public static final String SDD_REL_SDD = "sdd_rel_sdd";

    public static final String SDD_EXT_1 = "sdd_ext_1";

    public static final String SDD_EXT_2 = "sdd_ext_2";

    public static final String SDD_EXT_3 = "sdd_ext_3";

    public static final String SDD_IS_DELETE = "sdd_is_delete";

    public static final String SDD_DESC = "sdd_desc";

    public static final String SDD_SORT = "sdd_sort";

    public static final String SDD_REMARK = "sdd_remark";

    @Override
    protected Serializable pkVal() {
        return this.sddId;
    }

}
