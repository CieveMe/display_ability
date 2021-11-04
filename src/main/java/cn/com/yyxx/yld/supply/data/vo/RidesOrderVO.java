package cn.com.yyxx.yld.supply.data.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>
 *     订单支付结果与操作信息
 * </p>
 *
 * @author hz
 * @version 1.0.0
 * @program yld-supply-chain-v2
 * @date 2021-01-13 09:29
 * @since 0.17.0
 **/

@Data
@AllArgsConstructor
@ApiModel(value="支付请求返回VO对象", description="支付请求返回VO对象")
public class RidesOrderVO {

    private SmMerchantProductSaleOrderVO order;
    private UserWithPermissionVO userInfo;
    private String serialNum;
    private String userNo;
    private String termIp;
    private String scanType;
}