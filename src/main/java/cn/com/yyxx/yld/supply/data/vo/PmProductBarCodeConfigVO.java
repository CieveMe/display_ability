package cn.com.yyxx.yld.supply.data.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
 * @since 2019/7/11 15:49
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PmProductBarCodeConfigVO implements Serializable {

    private static final long serialVersionUID = -3438244615294077933L;

    /**
     * 配置ID
     */
    @JsonIgnore
    private Long pbccId;

    /**
     * 配置编号
     */
    private String pbccNo;

    /**
     * 商品ID
     */
    private Long pbccPislId;

    /**
     * 商品编号
     */
    private String pbccPislNo;

    /**
     * 商品条码
     */
    private String pbccBarCode;

    /**
     * 创建时间
     */
    private LocalDateTime pbccCreateTime;

    /**
     * 修改时间
     */
    private LocalDateTime pbccModifyTime;

    /**
     * 是否生效
     */
    private Boolean pbccIsActive;

    /**
     * 描述
     */
    private String pbccDescription;

    /**
     * 排序
     */
    private Integer pbccSort;
}
