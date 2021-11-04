package cn.com.yyxx.yld.supply.data.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author hz
 * @version 1.0
 * @since 2019/2/14 11:32
 */
@Data
@NoArgsConstructor
public class SimpleUserInfoVO implements Serializable {

    private static final long serialVersionUID = -660991057062474085L;
    /**
     * 用户ID
     */
    private Long ubiId;

    /**
     * 归属门店
     */
    private Integer ubiStore;
    /**
     * 用户编号
     */
    private String ubiUserNo;

    /**
     * 用户名
     */
    private String ubiUserName;

    /**
     * 工号
     */
    private Integer ubiJobNumber;
}
