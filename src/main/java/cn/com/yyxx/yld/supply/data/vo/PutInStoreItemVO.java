package cn.com.yyxx.yld.supply.data.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p></p>
 *
 * @author haoch
 * @version 1.0.0
 * @program yld-supply-chain-v2
 * @description
 * @date 2021-01-30 11:13
 * @since 0.1.0
 **/
@Data
public class PutInStoreItemVO implements Serializable {

    private static final long serialVersionUID = -5127808570869217446L;


    /**
     * 商品编码
     */
    @NotBlank
    private String productCode;
    /**
     * 入库数量
     */
    @NotNull
    private BigDecimal number;

}
