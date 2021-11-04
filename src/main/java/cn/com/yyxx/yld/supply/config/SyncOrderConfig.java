package cn.com.yyxx.yld.supply.config;

import cn.com.yyxx.yld.supply.messaging.AddPointSource;
import cn.com.yyxx.yld.supply.messaging.pay.OrderCancelNewSource;
import cn.com.yyxx.yld.supply.messaging.pay.OrderCancelSource;
import cn.com.yyxx.yld.supply.messaging.pay.OrderUpdateSource;
import cn.com.yyxx.yld.supply.messaging.pay.OrderSaveSource;
import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * <p>
 *
 * </p>
 *
 * @author FYX
 * @version 1.0
 * @since 2019/6/12 16:56
 */
@EnableBinding({OrderSaveSource.class, OrderUpdateSource.class, AddPointSource.class, OrderCancelSource.class, OrderCancelNewSource.class})
public class SyncOrderConfig {

}
