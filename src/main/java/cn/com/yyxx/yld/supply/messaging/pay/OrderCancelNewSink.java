package cn.com.yyxx.yld.supply.messaging.pay;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * <p>
 * 支付成功异步输出流
 * </p>
 *
 * @author liujun
 * @version 0.17.0
 * @date 2021/01/12 15:41
 */

public interface OrderCancelNewSink {

    String YLD_ORDER_CANCEL_NEW_IN_PUT = "yldOrderCancelNewInPut";


    /**
     * 支付成功异步输出流
     *
     * @return 消息通道
     */
    @Input(YLD_ORDER_CANCEL_NEW_IN_PUT)
    SubscribableChannel input();
}
