package cn.com.yyxx.yld.supply.steam.pay;


import cn.com.yyxx.yld.supply.data.vo.ResponsePayVO;
import cn.com.yyxx.yld.supply.data.vo.RidesOrderVO;
import cn.com.yyxx.yld.supply.messaging.pay.OrderUpdateSink;
import cn.com.yyxx.yld.supply.redis.RedisUtil;
import cn.com.yyxx.yld.supply.service.ISalesOrderService;
import cn.com.yyxx.yld.supply.staticMap.PayStatusCode;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import javax.annotation.Resource;
import org.springframework.messaging.handler.annotation.Header;

/**
 * <p>
 * 订单修改
 * </p>
 *
 * @author hz
 * @date 2021/01/11 18:49
 * @since 0.17.0
 */
@EnableBinding(OrderUpdateSink.class)
public class OrderUpdateDeal {

    private Logger log = LoggerFactory.getLogger(OrderUpdateDeal.class);

    @StreamListener(OrderUpdateSink.YLD_ORDER_UPDATE_IN_PUT)
    public void updateOrder(String responsePayVOStr,
        @Header("deliveryAttempt") Long deliveryAttempt //重试次数
    ) {
        try {
            log.info("异步接收参数:{}", responsePayVOStr);
            ResponsePayVO responsePayVO = JSON.parseObject(responsePayVOStr, ResponsePayVO.class);
            log.info("异步修改订单开始,订单号:{}", responsePayVO.getOrderNo());
            /*1、Rides取值*/
            Object ridesOrderObject = redisUtil.get(responsePayVO.getOrderNo());
            if (ridesOrderObject == null) {
                log.warn("异步修改订单，rides获取订单失败，订单号：{},订单已处理或订单号错误", responsePayVO.getOrderNo());
                return;
            }
            RidesOrderVO ridesOrder = JSON.parseObject(ridesOrderObject.toString(), RidesOrderVO.class);
            /*2、修改订单（为避免重复查库，支付成功后修改库存、发送市民网通知积分、发送实时到账提醒）*/
            boolean saveStatus = salesOrderService.updateOrder(ridesOrder, responsePayVO);
            /*3、支付成功或失败清除Rides*/
            if (saveStatus && (PayStatusCode.SUCCESS.getCode().equals(responsePayVO.getPayStatus())
                || PayStatusCode.FAIL.getCode().equals(responsePayVO.getPayStatus()))) {
                redisUtil.del(responsePayVO.getOrderNo());
            }
        } catch (Exception e) {
            log.error("支付异步修改订单出现异常，失败重试次数为：{}", deliveryAttempt, e);
            throw e;
        }
    }

    @Autowired
    private ISalesOrderService salesOrderService;
    @Resource
    private RedisUtil redisUtil;
}
