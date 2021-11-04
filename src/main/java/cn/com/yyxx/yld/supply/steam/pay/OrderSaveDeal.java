package cn.com.yyxx.yld.supply.steam.pay;

import cn.com.yyxx.yld.supply.data.vo.RidesOrderVO;
import cn.com.yyxx.yld.supply.messaging.pay.OrderSaveSink;
import cn.com.yyxx.yld.supply.redis.RedisUtil;
import cn.com.yyxx.yld.supply.service.ISalesOrderService;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Header;

/**
 * <p>
 * 订单保存
 * </p>
 *
 * @author liujun
 * @date 2021/01/11 18:49
 * @since 0.17.0
 */
@EnableBinding(OrderSaveSink.class)
public class OrderSaveDeal {

    private Logger log = LoggerFactory.getLogger(OrderSaveDeal.class);
    @StreamListener(OrderSaveSink.YLD_ORDER_SAVE_IN_PUT)
    public void saveOrder(String orderNo,
        @Header("deliveryAttempt") Long deliveryAttempt //重试次数
        ){
        try {
            log.info("异步保存开始,订单号:{}", orderNo);
            //1、Rides取值
            Object ridesOrderObject = redisUtil.get(orderNo);
            if (ridesOrderObject==null){
                log.warn("异步修改订单，rides获取订单失败，订单号：{},订单已处理或订单号错误",orderNo);
                return;
            }
            RidesOrderVO ridesOrder = JSON.parseObject(ridesOrderObject.toString(),RidesOrderVO.class);
            //2、保存订单、修改库存、发送市民网通知积分、发送实时到账提醒
            boolean saveStatus = salesOrderService.saveOrder(ridesOrder);
        } catch (Exception e) {
            log.error("支付异步保存订单出现异常，失败重试次数为：{}",deliveryAttempt,e);
            throw e;
        }

    }
    @Autowired
    private ISalesOrderService salesOrderService;
    @Resource
    private RedisUtil redisUtil;
    @Autowired
    private ObjectMapper objectMapper;
}
