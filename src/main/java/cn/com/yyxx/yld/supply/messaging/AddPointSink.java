package cn.com.yyxx.yld.supply.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * <p>
 *
 * </p>
 *
 * @author linmeng
 * @version 1.0
 * @date 2019/10/21 11:54
 */

public interface AddPointSink {

    String YLD_ADD_POINT_IN_PUT = "yldAddPointInPut";
    /**
     * 增加积分
     * @return 消息通道
     */
    @Input(YLD_ADD_POINT_IN_PUT)
    SubscribableChannel input();
}
