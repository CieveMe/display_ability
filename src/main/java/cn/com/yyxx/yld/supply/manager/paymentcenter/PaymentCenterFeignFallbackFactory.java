package cn.com.yyxx.yld.supply.manager.paymentcenter;

import cn.com.yyxx.yld.supply.data.vo.ResponsePayVO;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author duanweidong
 * @date 2021/2/8 13:20
 */
@Component
public class PaymentCenterFeignFallbackFactory implements FallbackFactory<PaymentCenterFeign> {
    public final static String TIP_FAIL_EXEC_CONSUMER_SERVICE = "无法连接支付服务,请联系管理员";

    private Logger log = LoggerFactory.getLogger(PaymentCenterFeignFallbackFactory.class);
    @Override
    public PaymentCenterFeign create(Throwable throwable) {
        log.error("支付中心微服务异常{}",throwable.toString());
        return new PaymentCenterFeign() {
            @Override
            public ResponsePayVO pay(Map<String,Object> requestPayVO) {
                log.error("支付-支付中心微服务连接失败");
                ResponsePayVO responsePayVO = new ResponsePayVO();
//                responsePayVO.setOrderId(requestPayVO.getOrder().getOrderId());
//                responsePayVO.setOrderNo(requestPayVO.getOrder().getOrderNo());
//                responsePayVO.setStoreId(requestPayVO.getOrder().getStoreId());
                responsePayVO.setErrInfo(TIP_FAIL_EXEC_CONSUMER_SERVICE);
//                responsePayVO.setAmount(requestPayVO.getOrder().getAmount());
                responsePayVO.setPayStatus("0002");
                responsePayVO.setErrCode("4441");
                return responsePayVO;
            }

            @Override
            public ResponsePayVO cancle(Map<String,Object> requestPayVO) {
                log.error("取消订单-支付中心微服务连接失败");
                ResponsePayVO responsePayVO = new ResponsePayVO();
//                responsePayVO.setOrderNo(requestPayVO.getOrder().getOrderId());
//                responsePayVO.setStoreId(requestPayVO.getOrder().getStoreId());
                responsePayVO.setErrInfo(TIP_FAIL_EXEC_CONSUMER_SERVICE);
                responsePayVO.setPayStatus("0002");
                responsePayVO.setErrCode("4441");
                return responsePayVO;
            }

            @Override
            public ResponsePayVO query(Map<String,Object> requestPayVO) {
                log.error("查询订单-支付中心微服务连接失败");
                ResponsePayVO responsePayVO = new ResponsePayVO();
//                responsePayVO.setOrderNo(requestPayVO.getOrder().getOrderId());
//                responsePayVO.setStoreId(requestPayVO.getOrder().getStoreId());
                responsePayVO.setErrInfo(TIP_FAIL_EXEC_CONSUMER_SERVICE);
                responsePayVO.setPayStatus("0002");
                responsePayVO.setErrCode("4441");
                return responsePayVO;
            }
        };
    }
}
