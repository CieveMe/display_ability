package cn.com.yyxx.yld.supply.data.vo;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author FYX
 * @version 0.16.0
 * @date 2020/12/10 15:33
 **/
@Data
public class OrderCallBackVO {

    private Boolean status;

    private String message;

    private String url;
}
