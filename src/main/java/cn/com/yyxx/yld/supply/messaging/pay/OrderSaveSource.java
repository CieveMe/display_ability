package cn.com.yyxx.yld.supply.messaging.pay;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.SubscribableChannel;

/**
 * <p>
 * 支付成功异步输入流
 * </p>
 *
 * @author liujun
 * @version 0.17.0
 * @date 2021/01/12 15:41
 **/
public interface OrderSaveSource {

    String YLD_ORDER_SAVE_OUT_PUT = "yldOrderSaveOutPut";
    /**
     * <P>
     *
     * </P>
     * @author liujun
     * @date 2021/1/12 15:57
     */
    @Output(YLD_ORDER_SAVE_OUT_PUT)
    SubscribableChannel output();

}
