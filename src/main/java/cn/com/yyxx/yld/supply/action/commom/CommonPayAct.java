package cn.com.yyxx.yld.supply.action.commom;

import cn.com.yyxx.yld.supply.action.BaseResultResponse;
import cn.com.yyxx.yld.supply.action.NewAppBaseAction;
import cn.com.yyxx.yld.supply.data.vo.ResponsePayVO;
import cn.com.yyxx.yld.supply.data.vo.SmMerchantProductSaleOrderVO;
import cn.com.yyxx.yld.supply.data.vo.UserWithPermissionVO;
import cn.com.yyxx.yld.supply.service.IPayService;
import cn.com.yyxx.yld.supply.util.ClientIpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>
 *
 * </p>
 *
 * @author FYX
 * @version 1.0
 * @date 2020/4/29 15:54
 * @since 0.11.0
 */
@RestController
@RequestMapping("/common/order/factory/b2cv3")
@Api(tags = "收银机-被扫支付、现金支付、取消下单（退款交易）、查询订单支付结果、上传离线订单")
public class CommonPayAct extends NewAppBaseAction {

    /**
     * <p>
     * B扫C支付
     * </p>
     *
     * @param order 订单信息
     * @author hz
     * @date 2021/03/11 18:49
     * @since 0.17.0
     */
    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    @ApiOperation("被扫提交订单&支付")
    public BaseResultResponse<ResponsePayVO> pay(
            @Valid @RequestBody SmMerchantProductSaleOrderVO order,
            HttpServletRequest request) {

        String serialNum = request.getParameter("serial_num");
        String userNo = request.getParameter("user_no");
        UserWithPermissionVO userInfo = super.getUser(request);
        //获取POS ip
        String termIp = new ClientIpUtil().getClientIpAddress(request);
        return deal(() -> service.b2cPay(order, userInfo, serialNum, userNo, termIp));
    }

    /**
     * <p>
     * 超级会员支付
     * </p>
     *
     * @param order 订单信息
     * @author hezhen
     * @date 2021/03/06 13:38
     */
    @RequestMapping(value = "/vip/pay", method = RequestMethod.POST)
    @ApiOperation("被扫提交订单&支付")
    public BaseResultResponse<ResponsePayVO> vipPay(
            @Valid @RequestBody SmMerchantProductSaleOrderVO order,
            HttpServletRequest request) {

        String serialNum = request.getParameter("serial_num");
        String userNo = request.getParameter("user_no");
        UserWithPermissionVO userInfo = super.getUser(request);
        //获取POS ip
        String termIp = new ClientIpUtil().getClientIpAddress(request);
        return deal(() -> service.vipB2cPay(order, userInfo, serialNum, userNo, termIp));
    }

    @Autowired
    private IPayService service;
}
