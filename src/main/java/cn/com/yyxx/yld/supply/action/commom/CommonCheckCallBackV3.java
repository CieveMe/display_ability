package cn.com.yyxx.yld.supply.action.commom;

import cn.com.yyxx.yld.supply.action.BaseResultResponse;
import cn.com.yyxx.yld.supply.action.NewAppBaseAction;
import cn.com.yyxx.yld.supply.data.vo.ResponsePayVO;
import cn.com.yyxx.yld.supply.service.ICheckCallBackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *
 * </p>
 *
 * @author FYX
 * @version 0.16.0
 * @date 2020/12/10 09:38
 **/
@RestController
@RequestMapping("/common/supply/v2")
@Api(tags = "异步支付  支持3.2.以上版本 (支付路由版本)")
public class CommonCheckCallBackV3 extends NewAppBaseAction {

    private Logger log = LoggerFactory.getLogger(CommonCheckCallBackV3.class);
    /**
     * <p>
     * 微信支付结果通知 </p
     *
     * @param request 请求
     * @author linmeng
     * @date 2020/4/28 9:43
     * @since 0.14.0
     */
    @PostMapping(value = "/pay/back")
    @ApiOperation("支付结果通知")
    @ResponseBody
    public BaseResultResponse<String> payCallBack(HttpServletRequest request) {

        log.info("支付异步通知");
        return deal(() -> {
            service.payCallBack(request);
            return Boolean.TRUE.toString();
        });

    }

    /**
     * <p>
     * 撤销交易结果通知V3 </p
     *
     * @param request 请求
     */
    @RequestMapping(value = "/cancel/back", method = RequestMethod.POST)
    @ApiOperation("整单退款回调")
    public BaseResultResponse<String> cancelBack(HttpServletRequest request) {

        log.info("去掉订单异步回调通知");
        return deal(() -> {
            service.cancelBack(request);
            return Boolean.TRUE.toString();
        });
    }

    /**
     * <p>
     * 超级会员支付异步请求
     * </p
     *
     * @param responsePay 请求
     * @author hezhen
     * @date 2021/3/6 13:43
     * @since 0.14.0
     */
    @PostMapping(value = "vip/pay/back")
    @ApiOperation("支付结果通知")
    @ResponseBody
    public BaseResultResponse<String> vipPayCallBack(@RequestBody ResponsePayVO responsePay) {
        log.info("支付异步通知");
        return deal(() -> {
            Boolean bool = service.vipPayCallBack(responsePay);
            return bool.toString();
        });

    }

    /**
     * <p>
     * ---------------------- openId查询通知  ----------------------
     * </p>
     *
     * @param request 请求
     * @author guoyl
     * @date 2020/6/10 15:47
     * @since 1.0.0
     */
    @PostMapping(value = "/open_id/back")
    @ApiOperation("openId查询通知")
    @ResponseBody
    public BaseResultResponse<String> openIdCallBack(HttpServletRequest request){
        log.info("openid异步通知");
        BaseResultResponse<String> deal = deal(() -> {
            service.openIdCallBack(request);
            return Boolean.TRUE.toString();
        });
        log.debug("==========================openId查询通知结束=={}=============================",deal);
        return deal;
    }

    @Autowired
    private ICheckCallBackService service;
}
