package cn.com.yyxx.yld.supply.service;

import cn.com.yyxx.yld.supply.data.vo.ResponsePayVO;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *
 * </p>
 *
 * @author FYX
 * @version 0.16.0
 * @date 2020/12/10 09:43
 **/
public interface ICheckCallBackService {


    /**
     * <p>
     * 支付结果通知 </p
     *
     * @param request 请求
     * @author FYX
     * @date 2020/12/10 9:43
     * @since 0.11.0
     */
    void payCallBack(HttpServletRequest request);

    /**
     * <p>
     * 撤销交易
     * </p
     *
     * @param request   请求
     * @author hz
     */
    void cancelBack(HttpServletRequest request);

    /**
     * <p>
     * 支付结果通知
     * </p
     *
     * @param responsePay 请求
     * @author FYX
     * @date 2020/12/10 9:43
     * @since 0.11.0
     */
    Boolean vipPayCallBack(ResponsePayVO responsePay);

    void openIdCallBack(HttpServletRequest request);
}
