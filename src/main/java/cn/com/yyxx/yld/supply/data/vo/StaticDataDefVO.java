package cn.com.yyxx.yld.supply.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by hk on 2017/6/22.
 *
 * @author hk
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("静态数据类")
public class StaticDataDefVO implements Serializable {

    private static final long serialVersionUID = -6547414888907730042L;

    @ApiModelProperty("静态数据ID")
    private String sddId;

    @ApiModelProperty("数据字典ID")
    @NotNull
    private String sddDddId;

    @ApiModelProperty("静态数据编码")
    @NotNull
    private String sddCode;

    @ApiModelProperty("静态数据名称")
    @NotNull
    private String sddName;

    @ApiModelProperty("静态数据值")
    @NotNull
    private String sddValue;

    @ApiModelProperty("数据扩展1")
    private String sddExt1;

    @ApiModelProperty("数据扩展2")
    private String sddExt2;

    @ApiModelProperty("数据扩展3")
    private String sddExt3;

    @ApiModelProperty("关联字典")
    private String sddRelDdd;

    @ApiModelProperty("关联数据")
    private String sddRelSdd;

    @ApiModelProperty(value = "是否删除")
    private Boolean sddIsDelete;

    @ApiModelProperty("数据描述")
    private String sddDesc;

    @ApiModelProperty(value = "排序")
    private Integer sddSort;

    @ApiModelProperty("备注")
    private String sddRemark;

    @ApiModelProperty("关联字典名称")
    private String sddRelDddName;

    @ApiModelProperty("关联数据名称")
    private String sddRelSddName;

}
