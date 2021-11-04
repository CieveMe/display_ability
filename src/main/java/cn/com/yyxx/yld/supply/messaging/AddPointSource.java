package cn.com.yyxx.yld.supply.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * <p>
 *
 * </p>
 *
 * @author linmeng
 * @version 1.0
 * @date 2019/10/21 12:02
 */

public interface AddPointSource {
    String YLD_ADD_POINT_OUT_PUT = "yldAddPointOutPut";

    /**
     * <p>
     *  增加积分
     * </p
     * @return org.springframework.messaging.MessageChannel
     * @author linmeng
     * @date 2019/10/21 12:03
     */
    @Output(YLD_ADD_POINT_OUT_PUT)
    MessageChannel output();
}
