package cn.com.yyxx.yld.supply.service.impl;

import cn.com.yyxx.yld.supply.data.vo.*;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleOrder;
import cn.com.yyxx.yld.supply.exception.NotExceptException;
import cn.com.yyxx.yld.supply.manager.IPaymentCenterManger;
import cn.com.yyxx.yld.supply.manager.ISmMerchantProductSaleOrderManager;
import cn.com.yyxx.yld.supply.manager.supervip.SuperVIPDiscountFeign;
import cn.com.yyxx.yld.supply.redis.RedisUtil;
import cn.com.yyxx.yld.supply.service.IPayService;
import cn.com.yyxx.yld.supply.staticMap.PayStatusCode;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.com.yyxx.yld.supply.messaging.pay.OrderSaveSource.YLD_ORDER_SAVE_OUT_PUT;
import static cn.com.yyxx.yld.supply.messaging.pay.OrderUpdateSource.YLD_ORDER_UPDATE_OUT_PUT;

/**
 * <p>
 *
 * </p>
 *
 * @author hz
 * @version 0.17.0
 * @date 2021/01/15 10:09
 **/
@Service
public class PayServiceImpl implements IPayService {

    private static final String SOURCE_MARKET = "智慧菜场";
    private static final String SOURCE_YLD = "盈立多";
    private static final int SOURCE_YLD_LIMIT = 6;
    private Logger log = LoggerFactory.getLogger(PayServiceImpl.class);

    private  BigDecimal odrDiscountAssign(BigDecimal actualPrice, BigDecimal shouldPrice){
        System.out.println("*********实付款*********"+actualPrice+"*********应付款*********"+shouldPrice);
        //订单中discount重新赋值，赋的值记录整单中所有折扣
        if (BigDecimal.ZERO.compareTo(shouldPrice)!=0){
            System.out.println("*********实付款*********"+actualPrice+"*********应付款*********"+shouldPrice);
            return actualPrice.multiply(new BigDecimal(100)).divide(shouldPrice,2, BigDecimal.ROUND_HALF_EVEN);
        }else{
            return BigDecimal.ZERO;
        }
    }

    @Override
    public ResponsePayVO b2cPay(SmMerchantProductSaleOrderVO order, UserWithPermissionVO userInfo,
                                String serialNum, String userNo, String termIp) {
        //1、判断支付参数
        if (ObjectUtil.isNull(userInfo)) {
            throw new NotExceptException("当前登录用户为空，不可以做任何操作！");
        }
        if (ObjectUtil.isNull(order)) {
            throw new NotExceptException("订单为空！");
        }
        if (!StrUtil.equals(order.getSbiCode(), userInfo.getStoreNo())) {
            log.error("订单中门店编号为{},登录用户门店编号为{}", order.getSbiCode(), userInfo.getStoreNo());
            throw new NotExceptException("门店编号校验不通过!");
        }
        if (order.getMpsoActualPrice() == null || order.getMpsoActualPrice().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("订单中实付金额为{}", order.getMpsoActualPrice());
            throw new NotExceptException("实付金额必须大于零!");
        }
        if (StrUtil.isBlank(order.getPayCode())) {
            throw new NotExceptException("支付码不能为空!");
        }
        //订单中discount重新赋值，赋的值记录整单中所有折扣
        order.setMpsoDiscount(odrDiscountAssign(order.getMpsoActualPrice(),order.getMpsoShouldPrice()));
        //组装异步所需VO
        RidesOrderVO ridesOrderVO = new RidesOrderVO(order, userInfo, serialNum, userNo, termIp, "scanB_C");
        //2、存储Rides 暂时不设置超时时间，丢单后可以补回
        boolean redisStatus = redisUtil.set(order.getMpsoOrderNo(), JSONObject.toJSONString(ridesOrderVO));
        if (!redisStatus) {
            log.error("订单存储失败,订单号:{},订单:{}", order.getMpsoOrderNo(), order.toString());
            throw new NotExceptException("订单存储失败!");
        }
        log.info("b2c开始保存订单信息{}",order);
        //3、开始异步保存订单
        boolean mqstatus = orderSaveOutPut.send(MessageBuilder.withPayload(order.getMpsoOrderNo()).build());
        //4、发起支付
        //4.1 "智慧菜场"和"便利店的区分"
        String paySource = getPaySource(userInfo);
        ResponsePayVO execute = iPaymentCenterManger.b2cPay(order, userInfo.getStoreId(), termIp, paySource);
        //TODO 支付同步返回orderNo为订单ID需重新复制，异步时返回orderNo为UUID，(支付路由代码需要梳理)
        execute.setOrderNo(order.getMpsoOrderNo());
        execute.setStoreId(userInfo.getStoreId());
        execute.setOrderId(order.getMpsoSingleNo());
        //5、异步修改订单
        orderUpdateOutPut.send(MessageBuilder.withPayload(JSONObject.toJSONString(execute)).build());
        //6、返回响应结果
        return execute;
    }

    public String getPaySource(UserWithPermissionVO userInfo){
        String sddValue = StoreTypeStaticDataDef.findBySddId(userInfo.getSbiChildType()).getSddValue();
        log.info("***************店铺类型****************"+sddValue);
        if (Integer.parseInt(sddValue) <= SOURCE_YLD_LIMIT) {
            return SOURCE_YLD;
        } else {
            return SOURCE_MARKET;
        }
    }

    @Override
    public ResponsePayVO vipB2cPay(SmMerchantProductSaleOrderVO order, UserWithPermissionVO userInfo, String serialNum, String userNo, String termIp) {
        ResponsePayVO execute = new ResponsePayVO();
        //1、判断支付参数
        if (ObjectUtil.isNull(userInfo)) {
            throw new NotExceptException("当前登录用户为空，不可以做任何操作！");
        }
        if (ObjectUtil.isNull(order)) {
            throw new NotExceptException("订单为空！");
        }
        if (!StrUtil.equals(order.getSbiCode(), userInfo.getStoreNo())) {
            log.error("订单中门店编号为{},登录用户门店编号为{}", order.getSbiCode(), userInfo.getStoreNo());
            throw new NotExceptException("门店编号校验不通过!");
        }
        if (order.getMpsoActualPrice() == null || order.getMpsoActualPrice().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("订单中实付金额为{}", order.getMpsoActualPrice());
            throw new NotExceptException("实付金额必须大于零!");
        }
        if (StrUtil.isBlank(order.getPayCode())) {
            throw new NotExceptException("支付码不能为空!");
        }

        log.info("======================扫码订单进入REDIS=================================" + order);
        //获取折扣、和会员手机号
        order = storeSaleInfo(order);
        //订单中discount重新赋值，赋的值记录整单中所有折扣
        order.setMpsoDiscount(odrDiscountAssign(order.getMpsoActualPrice(),order.getMpsoShouldPrice()));
        //组装异步所需VO
        RidesOrderVO ridesOrderVO = new RidesOrderVO(order, userInfo, serialNum, userNo, termIp, "scanB_C");

        //2、存储Rides 暂时不设置超时时间，丢单后可以补回
        boolean redisStatus = redisUtil.set(order.getMpsoOrderNo(), JSONObject.toJSONString(ridesOrderVO));
        if (!redisStatus) {
            log.error("订单存储失败,订单号:{},订单:{}", order.getMpsoOrderNo(), order.toString());
            throw new NotExceptException("订单存储失败!");
        }
        log.info("vipb2c开始保存订单信息{}",order);
        //3、开始异步保存订单
        orderSaveOutPut.send(MessageBuilder.withPayload(order.getMpsoOrderNo()).build());
        //4、发起支付
        Result<Boolean> booleanResult = iPaymentCenterManger.vipB2cPay(order, userInfo.getStoreId(), termIp);

        if (booleanResult == null) {
            execute.setErrInfo("支付失败,支付接口调用异常");
            execute.setErrCode("0002");
            return execute;
        }
        if (booleanResult.getCode() == null || !"S0A000".equals(booleanResult.getCode())) {
            if (booleanResult.getMsg() != null && !"".equals(booleanResult.getMsg())) {
                execute.setErrInfo(booleanResult.getMsg());
                execute.setErrCode(booleanResult.getCode());
                return execute;
            }
            execute.setErrInfo("支付失败,支付接口调用异常");
            execute.setErrCode("0002");
            return execute;
        }
        if (booleanResult.getData() == null || "".equals(booleanResult.getData())) {
            execute.setErrInfo("支付接口返回异常，请联系管理员");
            execute.setErrCode("0002");
            return execute;
        }
        if (booleanResult.getData()) {
            //TODO 支付同步返回orderNo为订单ID需重新复制，异步时返回orderNo为UUID，(支付路由代码需要梳理)
            execute.setOrderNo(order.getMpsoOrderNo());
            execute.setStoreId(userInfo.getStoreId());
            execute.setOrderId(order.getMpsoSingleNo());
            //这里需要把实际付款金额和会员信息补充进来
            execute.setPhone(order.getMpsoSmiPhone());
            //支付中
            execute.setPayStatus("0003");
//            execute.setAmount(order.getMpsoRealPrice().multiply(new BigDecimal(100)).longValue());
            execute.setAmount(order.getMpsoActualPrice().multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).longValue());
        } else {
            execute.setErrInfo(booleanResult.getMsg());
            execute.setErrCode(booleanResult.getCode());
        }
        org.springframework.messaging.Message msg = MessageBuilder.withPayload(JSONObject.toJSONString(execute)).build();
        //5、异步修改订单
        orderUpdateOutPut.send(msg);
        //6、返回响应结果
        return execute;
    }

    @Override
    public ResponsePayVO query(String orderNo, Integer sbiId) {
        SmMerchantProductSaleOrder oldOrder = smMerchantProductSaleOrderManager.getByOrderNoAndSbiId(orderNo, sbiId, null);
        if (oldOrder == null) {
            throw new NotExceptException("查询支付结果,发生错误,没有找到编号[{}]的订单", orderNo);
        }
        //如果是现金支付不需要查
        if (StaticDataDef.SPM_CASH_PAYMENT.getSddId().equals(oldOrder.getMpsoPaymentMethod())) {
            return initResult(oldOrder);
        }
        //如果是订单状态不是支付中不需要去查
        if (!StaticDataDef.SOS_THE_SITUATION_IS_NOT_CLEAR.getSddId().equals(oldOrder.getMpsoOrderStatus()) && !StaticDataDef.SOS_WAITING_PAYMENT.getSddId().equals(oldOrder.getMpsoOrderStatus())) {
            return initResult(oldOrder);
        }

//        ResponsePayVO responsePay = storeAsyncPayManagerV3.query(oldOrder.getMpsoOrderNo(), sbiId);
        ResponsePayVO responsePay = iPaymentCenterManger.query(oldOrder.getMpsoOrderNo(), sbiId);
        if (!responsePay.isOk()) {
            throw new NotExceptException("查询支付状态失败,原因:{}", responsePay.getErrInfo());
        }
        responsePay.setOrderId(oldOrder.getMpsoSingleNo());
        responsePay.setOrderNo(oldOrder.getMpsoOrderNo());
        //异步修改订单
        orderUpdateOutPut.send(MessageBuilder.withPayload(JSONObject.toJSONString(responsePay)).build());
        return responsePay;
    }

    private ResponsePayVO initResult(SmMerchantProductSaleOrder oldOrder) {
        ResponsePayVO vo = new ResponsePayVO();
        vo.setErrCode("0000");

        vo.setStoreId(oldOrder.getMpsoSbiId());
        vo.setOrderId(oldOrder.getMpsoSingleNo());
        vo.setOrderNo(oldOrder.getMpsoOrderNo());
        StaticDataDef payStatus = StaticDataDef.findBySddId(oldOrder.getMpsoOrderStatus());
        switch (payStatus) {
            case SOS_SUCCESSFUL_PAYMENT:
                vo.setPayStatus(PayStatusCode.SUCCESS.getCode());
                vo.setErrInfo(PayStatusCode.SUCCESS.getName());
                break;
            case SOS_WAITING_PAYMENT:
                vo.setPayStatus(PayStatusCode.NEED_PASSWORD.getCode());
                vo.setErrInfo(PayStatusCode.NEED_PASSWORD.getName());
                break;
            case SOS_FAILURE_TO_PAY:
                vo.setPayStatus(PayStatusCode.FAIL.getCode());
                vo.setErrInfo(PayStatusCode.FAIL.getName());
                break;
            case SOS_DEAL_CANCELLATION:
            case SOS_REFUND_SUCCESSFUL:
                vo.setPayStatus(PayStatusCode.BACK_OUT.getCode());
                vo.setErrInfo(PayStatusCode.BACK_OUT.getName());
                break;
            default:
                vo.setPayStatus("0005");
                vo.setErrInfo("情况不明");
        }

        StaticDataDef staticDataDef = StaticDataDef.findBySddId(oldOrder.getMpsoPaymentMethod());
        if (StaticDataDef.UNKNOW == staticDataDef) {
            staticDataDef = StaticDataDef.SSY_OTHER;
        }
        vo.setPayMode(staticDataDef.getSddCode());
        vo.setThreeOrderNo(oldOrder.getMpsoPayOrderId());
        return vo;
    }

    private <T, R> Map<R, T> listToMap(List<T> oldOrders, Function<T, R> function) {
        return oldOrders.stream().collect(Collectors.toMap(function, Function.identity(), (temp1, temp2) -> temp1));
    }

    /*
     * <p>获取门店折扣信息</p>
     * @param order
     * @author hezhen
     * @date 2021/03/06 10:22
     */
    @Override
    public SmMerchantProductSaleOrderVO storeSaleInfo(SmMerchantProductSaleOrderVO order) {
        String cardCode = order.getPayCode().substring(3);
        List<SmMerchantProductSaleItemVO> orderItems = order.getItems();
        log.info("***************获取折扣的参数****************"+cardCode);
        Result<CardInfoToSaasVO> info = superVIPDiscountFeign.discountInfo(cardCode);
        log.info("-----------折扣返回值-------------：***" + info.toString());
        order.setMpsoSmiPhone(info.getData().getMobilePhone());
        order.setMpsoRealDiscount(order.getMpsoActualPrice().divide(order.getMpsoShouldPrice(), 2, BigDecimal.ROUND_HALF_EVEN));
        return order;
    }

    @Resource
    private RedisUtil redisUtil;
    @Autowired
    @Qualifier(YLD_ORDER_SAVE_OUT_PUT)
    private MessageChannel orderSaveOutPut;
    @Autowired
    @Qualifier(YLD_ORDER_UPDATE_OUT_PUT)
    private MessageChannel orderUpdateOutPut;
    @Autowired
    private ISmMerchantProductSaleOrderManager smMerchantProductSaleOrderManager;
    @Autowired
    SalesOrderServiceImpl salesOrderService;
    @Autowired
    private IPaymentCenterManger iPaymentCenterManger;
    @Autowired
    private SuperVIPDiscountFeign superVIPDiscountFeign;

}
