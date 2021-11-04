package cn.com.yyxx.yld.supply.service.impl;

import cn.com.yyxx.yld.supply.action.BaseResultResponse;
import cn.com.yyxx.yld.supply.dao.sm.ISmMerchantProductSaleItemCancelDao;
import cn.com.yyxx.yld.supply.dao.sm.ISmMerchantProductSaleOrderCancelDao;
import cn.com.yyxx.yld.supply.data.vo.*;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleItem;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleItemCancel;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleOrder;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleOrderCancel;
import cn.com.yyxx.yld.supply.exception.NotExceptException;
import cn.com.yyxx.yld.supply.manager.*;
import cn.com.yyxx.yld.supply.manager.impl.StoreAsyncPayManagerV3Impl;
import cn.com.yyxx.yld.supply.manager.product.IApiMerchantProductManager;
import cn.com.yyxx.yld.supply.manager.supervip.SuperVIPDiscountFeign;
import cn.com.yyxx.yld.supply.manager.supervip.SuperVIPFeign;
import cn.com.yyxx.yld.supply.manager.user.IApiUserGrantInfoManager;
import cn.com.yyxx.yld.supply.redis.RedisUtil;
import cn.com.yyxx.yld.supply.service.IPayService;
import cn.com.yyxx.yld.supply.staticMap.PayStatusCode;
import cn.com.yyxx.yld.supply.staticMap.StaticDataDef;
import cn.com.yyxx.yld.supply.staticMap.StoreTypeStaticDataDef;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.com.yyxx.yld.supply.messaging.pay.OrderCancelNewSource.YLD_ORDER_CANCEL_NEW_OUT_PUT;
import static cn.com.yyxx.yld.supply.messaging.pay.OrderCancelSource.YLD_ORDER_CANCEL_OUT_PUT;
import static cn.com.yyxx.yld.supply.messaging.pay.OrderSaveSource.YLD_ORDER_SAVE_OUT_PUT;
import static cn.com.yyxx.yld.supply.messaging.pay.OrderUpdateSource.YLD_ORDER_UPDATE_OUT_PUT;

/**
 * <p>
 *
 * </p>
 *
 * @author liujun
 * @version 0.17.0
 * @date 2021/01/15 10:09
 **/
@Service
public class PayServiceImpl implements IPayService {

    private static final String SOURCE_MARKET = "智慧菜场";
    private static final String SOURCE_YLD = "盈立多";
    private static final int SOURCE_YLD_LIMIT = 6;
    //退款方法（原路返回:1 现金退款：2）
    private static final String RETURN_BY_SAME_WAY = "1";
    private static final String RETURN_BY_CASH = "2";
    private Logger log = LoggerFactory.getLogger(PayServiceImpl.class);
    private static final Long RIDES_TIME = 24 * 60 * 60L;
    private static final int RECALL_MAX_TIMES = 2;
    private static final String STATUS_SUCCESS = "200";
    private static final long INTERVAL_TIME = 1000;

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
//        ResponsePayVO execute = storeAsyncPayManagerV3.b2cPay(order,userInfo.getStoreId(),termIp);
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
    public BaseResultResponse<String> cashPay(SmMerchantProductSaleOrderVO order, UserWithPermissionVO userInfo, String serialNum, String userNo, String termIp) {
        // 保存订单
        BaseResultResponse<String> response = new BaseResultResponse<>();
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
        if (order.getMpsoActualPrice() == null) {
            log.error("订单中实付金额为{}", order.getMpsoActualPrice());
            throw new NotExceptException("实付金额为空!");
        }
        //订单中discount重新赋值，赋的值记录整单中所有折扣
        order.setMpsoDiscount(odrDiscountAssign(order.getMpsoActualPrice(),order.getMpsoShouldPrice()));
        RidesOrderVO ridesOrderVO = new RidesOrderVO(order, userInfo, serialNum, userNo, termIp, null);
        SmMerchantProductSaleOrder saleOrder = new SmMerchantProductSaleOrder();
        salesOrderService.initOrder(saleOrder, ridesOrderVO);
        //初始化订单商品
        ArrayList<String> mpsiMpbiNos = new ArrayList<>();
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            order.getItems().forEach((item) -> {
                mpsiMpbiNos.add(item.getMpsiMpbiNo());
            });
        }
        //-----------------------------------------------------------------
        List<PmMerchantProductBasicAndLibVO> productInfos = new ArrayList<>();
        if (!mpsiMpbiNos.isEmpty()) {
            productInfos = BaseResultResponse.getResponseDataNotAllowNull(apiMerchantProductManager.listByNoAndStoreId(mpsiMpbiNos, saleOrder.getMpsoSbiId()));
        }
        //商品编号list
        Map<String, PmMerchantProductBasicAndLibVO> productsMap = productInfos.stream().collect(Collectors.toMap(PmMerchantProductBasicAndLibVO::getMpbiNo, temp -> temp));
        List<SmMerchantProductSaleItem> items = salesOrderService.initOrderProduct(order, saleOrder, productsMap);
        saleOrder.setMpsoOrderStatus(StaticDataDef.SOS_SUCCESSFUL_PAYMENT.getSddId());
        saleOrder.setMpsoIsPayment(true);
        saleOrder.setMpsoPayTime(LocalDateTime.now());
        saleOrder.setMpsoIsRefund(false);
        //保存订单
        boolean save = smMerchantProductSaleOrderManager.save(saleOrder);
        //保存完成.
        if (save) {
            if (!items.isEmpty()) {
                SmMerchantProductSaleOrder finalSaleOrder = saleOrder;
                items.forEach((item) -> {
                    item.setMpsiMpsoId(finalSaleOrder.getMpsoId());
                });
                //批量保存销售明细
                merchantProductSaleItemManager.saveBatch(items);
            }
            log.info("订单[{}]首次保存, 明细项共保存{}个", saleOrder.getMpsoId(), items.size());
            //变更库存
            try {
                productService.saleChangeProduct(ridesOrderVO, items);
            } catch (Exception e) {
                log.error("变更库存失败，订单号{}", saleOrder.getMpsoOrderNo(),e);
            }
            //发送市民网积分消息
            try {
                memberService.sendCashOrderSuccessMessage(saleOrder, items);
            } catch (Exception e) {
                log.error("退款扣除积分发送失败，订单号{}", saleOrder.getMpsoOrderNo(),e);
            }
            //发送到账卡片
            try {
                log.info("   saleOrder   "+saleOrder);
                Integer typeId = 0;
                ownerService.sendMessageToOwner(saleOrder,typeId,null, null);
            } catch (Exception e) {
                log.error("退款到账卡片发送失败，订单号{}", saleOrder.getMpsoOrderNo(),e);
            }
        }
        return response.initSuccess(null);
    }

    @Override
    public ResponsePayVO cancel(String orderNo, UserWithPermissionVO user, String serialNum, String userNo, String isOnlyRefund, String refundMethod, String refundReason) {
        log.info("============开始执行退款:" + orderNo + "============");
        SmMerchantProductSaleOrder oldOrder = smMerchantProductSaleOrderManager.getByOrderNo(orderNo);
        if (oldOrder == null) {
            log.error("查询支付结果,发生错误,没有找到编号[{}]的订单", orderNo);
            ResponsePayVO ppv = new ResponsePayVO();
            ppv.setOrderNo(orderNo);
            ppv.setErrCode("0005");
            ppv.setPayStatus("0002");
            ppv.setErrInfo("订单未上传成功，请返回收银页一分钟后重试!");
            return ppv;
        }


        if (!StrUtil.equals(oldOrder.getMpsoOrderStatus(), StaticDataDef.SOS_SUCCESSFUL_PAYMENT.getSddId())) {
            //只处理支付成功的订单
            log.info("门店的订单{}状态为退款成功,或取消订单,不更新状态,也不通知收银端!", JSONUtil.toJsonStr(oldOrder));
            ResponsePayVO ppv = new ResponsePayVO();
            ppv.setOrderNo(orderNo);
            ppv.setErrCode("0005");
            ppv.setPayStatus("0002");
            //ppv.setErrInfo("正在确认支付结果,请稍后重试!");
            ppv.setErrInfo("该订单已退款，请勿重复操作!");
            return ppv;
        }
        List<SmMerchantProductSaleItem> oldItem = smMerchantProductSaleItemManager.listByMpsoId(oldOrder.getMpsoId());
        oldOrder.setItems(oldItem);
        //2、存储Rides 暂时不设置超时时间，丢单后可以补回
        if (StringUtils.isBlank(refundMethod)){
            //老版本pos退款只会原路退回
            refundMethod = RETURN_BY_SAME_WAY;
        }
        RidesOrderCancelVO ridesOrderVO = new RidesOrderCancelVO(oldOrder, user, serialNum, userNo, null, null, isOnlyRefund, refundMethod, refundReason);
        boolean redisStatus = redisUtil.set(oldOrder.getMpsoOrderNo(), JSONObject.toJSONString(ridesOrderVO));
        if (!redisStatus) {
            log.error("退款订单存储失败,订单号:{},订单:{}", oldOrder.getMpsoOrderNo(), oldOrder.toString());
            throw new NotExceptException("退款订单存储失败!");
        }
        //pay模块撤销交易 ，成功后开始修改订单
//        ResponsePayVO cancel = storeAsyncPayManagerV3.cancel(orderNo, user.getStoreId());


//        mpsoPaymentMethod
//        oldOrder
        ResponsePayVO cancel = new ResponsePayVO();
        //判断订单支付时间
        LocalDateTime startTime = LocalDate.now().atTime(0, 0, 0);
        LocalDateTime endTime = LocalDate.now().atTime(23, 59, 59);
        //如果是扫码支付 并且 订单支付时间在同一天
        //"智慧菜场"和"便利店的区分"
        String paySource = getPaySource(user);
        if(StringUtils.isBlank(ridesOrderVO.getRefundMethod())){
            if (StrUtil.equals(oldOrder.getMpsoPaymentMethod(), StaticDataDef.SPM_SWEEP_CODE_PAYMENT.getSddId()) &&
                    oldOrder.getMpsoPayTime().isAfter(startTime) && oldOrder.getMpsoPayTime().isBefore(endTime) &&
                    oldOrder.getMpsoActualPrice().doubleValue() != 0
            ) {
                cancel = iPaymentCenterManger.cancel(orderNo, user.getStoreId(),paySource);
            } else {
                cancel.setErrCode("0000");
                cancel.setPayStatus("0001");
            }
        }else{
            if (StrUtil.equals(oldOrder.getMpsoPaymentMethod(), StaticDataDef.SPM_SWEEP_CODE_PAYMENT.getSddId()) &&
                    oldOrder.getMpsoPayTime().isAfter(startTime) && oldOrder.getMpsoPayTime().isBefore(endTime) &&
                    RETURN_BY_SAME_WAY.equals(ridesOrderVO.getRefundMethod()) &&
                    oldOrder.getMpsoActualPrice().doubleValue() != 0
            ) {

                cancel = iPaymentCenterManger.cancel(orderNo, user.getStoreId(), paySource);
            } else {
                cancel.setErrCode("0000");
                cancel.setPayStatus("0001");
            }
        }

        cancel.setOrderNo(oldOrder.getMpsoOrderNo());
        cancel.setStoreId(user.getStoreId());
        cancel.setOrderId(oldOrder.getMpsoSingleNo());
//        salesOrderService.updateCannelOrder(oldOrder,cancel);
        orderCancelOutPut.send(MessageBuilder.withPayload(JSONObject.toJSONString(cancel)).build());
        //判断时候交易成功修改为退款
        if ("0001".equals(cancel.getPayStatus())) {
            //5、异步修改订单
            cancel.setErrInfo("退款成功");
        }
        return cancel;
    }



    @Override
    public ResponsePayVO cancelNew(String orderNo, OrderCancelNewVO oc, UserWithPermissionVO user, String serialNum, String userNo) {
        ///查询该订单。。。
        ///如果订单没有查询到则直接报错
        ///判断退款类型 。这里只有现金
        /// 1.退货退款.2直接退款   cancelType  退货退款/退款
        ///退款方式：
        ///一共两种情况:1.整单现金 2.单品现金退款 id name
        ///单品现金退款需要，保存一个单品退款的位置。

        log.info("============开始执行退款:" + orderNo + "============");
        ResponsePayVO cancel = new ResponsePayVO();
        SmMerchantProductSaleOrder oldOrder = smMerchantProductSaleOrderManager.getByOrderNo(orderNo);
        if (oldOrder == null) {
            log.error("查询支付结果,发生错误,没有找到编号[{}]的订单", orderNo);
            //ResponsePayVO ppv = new ResponsePayVO();
            cancel.setOrderNo(orderNo);
            cancel.setErrCode("0005");
            cancel.setPayStatus("0002");
            cancel.setErrInfo("订单未上传成功，请返回收银页一分钟后重试!");
            return cancel;
        }

        //判断支付状态是否完成
        if (!StrUtil.equals(oldOrder.getMpsoOrderStatus(), StaticDataDef.SOS_SUCCESSFUL_PAYMENT.getSddId())) {
            //只处理支付成功的订单
            log.info("门店的订单{}状态为未成功支付,或取消订单,不更新状态,也不通知收银端!", JSONUtil.toJsonStr(oldOrder));
            //ResponsePayVO ppv = new ResponsePayVO();
            cancel.setOrderNo(orderNo);
            cancel.setErrCode("0005");
            cancel.setPayStatus("0002");
            cancel.setErrInfo("该订单已退款，请勿重复操作!");
            return cancel;
        }

        //判断退款类型
        if (StringUtil.isNotEmpty(oc.getCancelType())) {
            //退货退款的情况  ----  商品表中新增   商品数为负数的 退货商品   并更改原订单退款后的金额.
            if (oc.getCancelType().equals("退货退款")) {
                //退款实收
                Double tkActualPrice = 0.00;
                //退款应收
                Double tkShouldPrice = 0.00;
                //商品优惠总额
                Double tkDiscountPrice = 0.00;
                //新毛利额
                Double tkNewGrossPrice = 0.00;
                List<SmMerchantProductSaleItem> itemCollection = new ArrayList<>();
                List<SmMerchantProductSaleItem> oldItemCollection = new ArrayList<>();
                for (int i = 0; i < oc.getItems().size(); i++) {
                    SmMerchantProductSaleItem item = new SmMerchantProductSaleItem();
                    SmMerchantProductSaleItem olditem = new SmMerchantProductSaleItem();
                    BeanUtil.copyProperties(oc.getItems().get(i), item);
                    //通过品项编号查询商品信息
                    QueryWrapper<SmMerchantProductSaleItem> queryWrapper = new QueryWrapper<>();

                    queryWrapper.eq("mpsi_no", item.getMpsiNo());
                    item = merchantProductSaleItemManager.getOne(queryWrapper);
                    olditem = merchantProductSaleItemManager.getOne(queryWrapper);
                    olditem.setMpsiIsRefund(true);
                    //设置原商品退款类型
                    olditem.setMpsiIsRefundType(1);
                    oldItemCollection.add(olditem);


                    //退款实收总额
                    tkActualPrice += oc.getItems().get(i).getMpsiSubTotal().doubleValue();
                    //退款应收总额
                    tkShouldPrice += oc.getItems().get(i).getMpsiRetailPrice().multiply(oc.getItems().get(i).getMpsiNum()).doubleValue();
                    //优惠总额
                    tkDiscountPrice += oc.getItems().get(i).getMpsiRetailPrice().multiply(oc.getItems().get(i).getMpsiNum()).subtract(olditem.getMpsiSubTotal()).doubleValue();

                    //新毛利额总额
                    tkNewGrossPrice += oc.getItems().get(i).getMpsiSubTotal().subtract(oc.getItems().get(i).getMpsiWholesalePrice().multiply(oc.getItems().get(i).getMpsiNum()))
                            .doubleValue();

                    //修改商品金额和数量为负数并存入list

                    //----------为了解决统计退款后计算问题   -----------
                    item.setMpsiSubTotal(item.getMpsiSubTotal().negate());
                    item.setMpsiIsRefundType(1);
                    //---------------------
                    item.setMpsiCreateTime(LocalDateTime.now());
                    item.setMpsiModifyTime(LocalDateTime.now());
                    item.setMpsiNum(item.getMpsiNum().negate());
                    item.setMpsiNo(IdUtil.fastSimpleUUID());
                    item.setMpsiId(null);
                    item.setMpsiIsRefund(true);
                    itemCollection.add(item);
                }
                //优惠总额
                //tkDiscountPrice = new BigDecimal(tkShouldPrice).subtract(new BigDecimal(tkActualPrice)).doubleValue();


//                System.out.println("tkActualPrice"+tkActualPrice);
//                System.out.println("tkShouldPrice"+tkShouldPrice);
//                System.out.println("tkDiscountPrice"+ tkDiscountPrice);
//
//                System.out.println(oldOrder.getMpsoActualPrice().subtract(new BigDecimal(Double.toString(tkActualPrice))));
//                System.out.println(oldOrder.getMpsoShouldPrice().subtract(new BigDecimal(Double.toString(tkShouldPrice)).add(new BigDecimal(tkDiscountPrice))));
//                System.out.println(oldOrder.getMpsoTotalPrice().subtract(new BigDecimal(Double.toString(tkShouldPrice))));
//
//                System.out.println(" oldOrder.getMpsoShouldPrice() "+ oldOrder.getMpsoShouldPrice());
//                System.out.println("     getMpsoShouldPrice     "+(oldOrder.getMpsoShouldPrice().subtract(new BigDecimal(Double.toString(tkShouldPrice)))));
//                System.out.println("    getMpsoShouldPrice"+(oldOrder.getMpsoShouldPrice().subtract(new BigDecimal(Double.toString(tkShouldPrice))).add(new BigDecimal(tkDiscountPrice))));

                //修改退款订单的应收和应付金额和毛利额

                //判断实收金额为负值时  改为0
                if(oldOrder.getMpsoActualPrice().subtract(new BigDecimal(Double.toString(tkActualPrice))).compareTo(BigDecimal.ZERO) == -1){
                    oldOrder.setMpsoActualPrice(BigDecimal.ZERO);
                } else {
                    oldOrder.setMpsoActualPrice(oldOrder.getMpsoActualPrice().subtract(new BigDecimal(Double.toString(tkActualPrice))));
                }
                oldOrder.setMpsoShouldPrice((oldOrder.getMpsoShouldPrice().subtract(new BigDecimal(Double.toString(tkShouldPrice))).add(new BigDecimal(tkDiscountPrice))));
                oldOrder.setMpsoTotalPrice(oldOrder.getMpsoTotalPrice().subtract(new BigDecimal(Double.toString(tkShouldPrice))));
                oldOrder.setMpsoGrossProfit(oldOrder.getMpsoGrossProfit().subtract(new BigDecimal(tkNewGrossPrice)));

                //设置退款方式(1 现金收款 2 原路返回 3 微信转账 4 支付宝转账 0 未知 )
                Optional<String> stringOptional = Optional.ofNullable(oc.getRefundMethod());
                stringOptional.orElse("原路返回");
                int method = 0;
                switch (stringOptional.get()){
                    case "原路返回":{
                        method = 1;
                        break;
                    }
                    case "现金退款":{
                        method = 2;
                        break;
                    }
                    case "微信转账":{
                        method = 3;
                        break;
                    }
                    case "支付宝转账":{
                        method = 4;
                        break;
                    }
                }
                oldOrder.setMpsoRefundTime(LocalDateTime.now());
                oldOrder.setMpsoIsRefundFourVersion(true);
                oldOrder.setMpsoIsRefundType(1);
                oldOrder.setMpsoRefundMethod(method);
                oldOrder.setMpsoRefundReason(oc.getRefundReason());
                //修改退款商品为已退款
                merchantProductSaleItemManager.updateBatchById(oldItemCollection);
                //新增退款退货商品信息
                merchantProductSaleItemManager.saveBatch(itemCollection);
                //更改原订单退货后金额
                smMerchantProductSaleOrderManager.updateById(oldOrder);

                //将本次单品退款的记录存在表中,方便发送到账卡片时查询
                QueryWrapper<SmMerchantProductSaleItem> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("mpsi_mpso_id",oldOrder.getMpsoId());
                queryWrapper.gt("mpsi_num",0);
                queryWrapper.eq("mpsi_is_delete",0);
                List<SmMerchantProductSaleItem> saleAllItems = smMerchantProductSaleItemManager.list(queryWrapper);
                Integer orderCancelId = saveProductSaleOrderCancelData(oldOrder,itemCollection,saleAllItems);

                RidesOrderNewCancelVO ridesOrderVO = new RidesOrderNewCancelVO(oldOrder, oc, user, serialNum, userNo, null, null, orderCancelId);
                //堆成一个bean //放到json 存储至redis中
                boolean redisStatus = redisUtil.set(oldOrder.getMpsoOrderNo(), JSONObject.toJSONString(ridesOrderVO));
                if (!redisStatus) {
                    log.error("退款订单存储失败,订单号:{},订单:{}", oldOrder.getMpsoOrderNo(), oldOrder.toString());
                    throw new NotExceptException("退款订单存储失败!");
                }
                cancel.setPayStatus("0001");
                cancel.setErrCode("0000");
                cancel.setOrderNo(oldOrder.getMpsoOrderNo());
                cancel.setStoreId(user.getStoreId());
                cancel.setOrderId(oldOrder.getMpsoSingleNo());
                cancel.setErrInfo("退款成功");
                //5、异步修改订单
                orderCancelNewOutPut.send(MessageBuilder.withPayload(JSONObject.toJSONString(cancel)).build());

                //仅退款的情况   ---  商品表中直接更改原退款后的金额.
            } else if (oc.getCancelType().equals("仅退款")) {
                //退款实收
                Double tkActualPrice = 0.00;
                //退款应收
                Double tkShouldPrice = 0.00;
                //商品优惠总额
                Double tkDiscountPrice = 0.00;
                //新毛利额
                Double tkNewGrossPrice = 0.00;
                List<SmMerchantProductSaleItem> itemCollection = new ArrayList<>();
                List<SmMerchantProductSaleItem> oldItemCollection = new ArrayList<>();
                for (int i = 0; i < oc.getItems().size(); i++) {
                    SmMerchantProductSaleItem item = new SmMerchantProductSaleItem();
                    SmMerchantProductSaleItem olditem = new SmMerchantProductSaleItem();
                    BeanUtil.copyProperties(oc.getItems().get(i), item);
                    //通过品项编号查询商品信息
                    QueryWrapper<SmMerchantProductSaleItem> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("mpsi_no", item.getMpsiNo());
                    item = merchantProductSaleItemManager.getOne(queryWrapper);
                    olditem = merchantProductSaleItemManager.getOne(queryWrapper);
                    olditem.setMpsiIsRefund(true);
                    //-------------仅退款 退款类型-------折后小计改负--------
                    //olditem.setMpsiSubTotal(olditem.getMpsiSubTotal().negate());
                    olditem.setMpsiIsRefundType(2);
                    //-------------------------------------
                    oldItemCollection.add(olditem);

                    //------新增   新退款时也新增一条商品数据
                    item.setMpsiCreateTime(LocalDateTime.now());
                    item.setMpsiModifyTime(LocalDateTime.now());
                    item.setMpsiSubTotal(item.getMpsiSubTotal().negate());
                    item.setMpsiIsRefundType(2);
                    item.setMpsiNum(new BigDecimal(0.00));
                    item.setMpsiNo(IdUtil.fastSimpleUUID());
                    item.setMpsiId(null);
                    item.setMpsiIsRefund(true);
                    itemCollection.add(item);



                    //退款实收总额
                    tkActualPrice += oc.getItems().get(i).getMpsiSubTotal().doubleValue();
                    //退款应收总额
                    tkShouldPrice += oc.getItems().get(i).getMpsiRetailPrice().multiply(oc.getItems().get(i).getMpsiNum()).doubleValue();
                    //优惠总额
                    tkDiscountPrice += oc.getItems().get(i).getMpsiRetailPrice().multiply(oc.getItems().get(i).getMpsiNum()).subtract(olditem.getMpsiSubTotal()).doubleValue();
                    //新毛利额总额
                    tkNewGrossPrice += oc.getItems().get(i).getMpsiSubTotal().subtract(oc.getItems().get(i).getMpsiWholesalePrice().multiply(oc.getItems().get(i).getMpsiNum()))
                            .doubleValue();
                }
                //修改退款订单的应收和应付金额和毛利额
                //判断实收金额为负值时  改为0
                if(oldOrder.getMpsoActualPrice().subtract(new BigDecimal(Double.toString(tkActualPrice))).compareTo(BigDecimal.ZERO) == -1){
                    oldOrder.setMpsoActualPrice(BigDecimal.ZERO);
                } else {
                    oldOrder.setMpsoActualPrice(oldOrder.getMpsoActualPrice().subtract(new BigDecimal(Double.toString(tkActualPrice))));
                }
                oldOrder.setMpsoShouldPrice((oldOrder.getMpsoShouldPrice().subtract(new BigDecimal(Double.toString(tkShouldPrice))).add(new BigDecimal(tkDiscountPrice))));
                oldOrder.setMpsoTotalPrice(oldOrder.getMpsoTotalPrice().subtract(new BigDecimal(Double.toString(tkShouldPrice))));
                oldOrder.setMpsoGrossProfit(oldOrder.getMpsoGrossProfit().subtract(new BigDecimal(tkNewGrossPrice)));

                //设置退款方式(1 现金收款 2 原路返回 3 微信转账 4 支付宝转账 0 未知 )
                Optional<String> stringOptional = Optional.ofNullable(oc.getRefundMethod());
                stringOptional.orElse("原路返回");
                int method = 0;
                switch (stringOptional.toString()){
                    case "原路返回":{
                        method = 1;
                        break;
                    }
                    case "现金退款":{
                        method = 2;
                        break;
                    }
                    case "微信转账":{
                        method = 3;
                        break;
                    }
                    case "支付宝转账":{
                        method = 4;
                        break;
                    }
                }
                oldOrder.setMpsoRefundTime(LocalDateTime.now());
                oldOrder.setMpsoIsRefundFourVersion(true);
                oldOrder.setMpsoIsRefundType(2);
                oldOrder.setMpsoRefundMethod(method);
                oldOrder.setMpsoRefundReason(oc.getRefundReason());

                //新增退款退货商品信息
                merchantProductSaleItemManager.saveBatch(itemCollection);
                //修改退款商品为已退款
                merchantProductSaleItemManager.updateBatchById(oldItemCollection);
                //更改原订单退货后金额
                smMerchantProductSaleOrderManager.updateById(oldOrder);

                //将本次单品退款的记录存在表中,方便发送到账卡片时查询
                QueryWrapper<SmMerchantProductSaleItem> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("mpsi_mpso_id",oldOrder.getMpsoId());
                queryWrapper.gt("mpsi_num",0);
                queryWrapper.eq("mpsi_is_delete",0);
                List<SmMerchantProductSaleItem> saleAllItems = smMerchantProductSaleItemManager.list(queryWrapper);
                Integer orderCancelId = saveProductSaleOrderCancelData(oldOrder,itemCollection,saleAllItems);

                RidesOrderNewCancelVO ridesOrderVO = new RidesOrderNewCancelVO(oldOrder, oc, user, serialNum, userNo, null, null ,orderCancelId);
                //堆成一个bean //放到json 存储至redis中
                boolean redisStatus = redisUtil.set(oldOrder.getMpsoOrderNo(), JSONObject.toJSONString(ridesOrderVO));
                if (!redisStatus) {
                    log.error("退款订单存储失败,订单号:{},订单:{}", oldOrder.getMpsoOrderNo(), oldOrder.toString());
                    throw new NotExceptException("退款订单存储失败!");
                }
                cancel.setPayStatus("0001");
                cancel.setErrCode("0000");
                cancel.setOrderNo(oldOrder.getMpsoOrderNo());
                cancel.setStoreId(user.getStoreId());
                cancel.setOrderId(oldOrder.getMpsoSingleNo());
                cancel.setErrInfo("退款成功");
                //5、异步修改订单
                orderCancelNewOutPut.send(MessageBuilder.withPayload(JSONObject.toJSONString(cancel)).build());
            } else {
                log.info("门店的订单{}退款类型错误", JSONUtil.toJsonStr(oldOrder));
            }
//        }
        }else {
            log.info("门店的订单{}退款类型为空",JSONUtil.toJsonStr(oldOrder));
        }
        return cancel;
    }

    private Integer saveProductSaleOrderCancelData(SmMerchantProductSaleOrder oldOrder,List<SmMerchantProductSaleItem> oldItems,List<SmMerchantProductSaleItem> saleAllItems) {

        SmMerchantProductSaleOrderCancel orderCancel = new SmMerchantProductSaleOrderCancel();
        orderCancel.setMpsoCancelOrderId(oldOrder.getMpsoId()).
                    setMpsoCancelSbiId(oldOrder.getMpsoSbiId()).
                    setMpsoCancelSbiCode(oldOrder.getMpsoSbiCode()).
                    setMpsoCancelStoreName(oldOrder.getMpsoStoreName()).
                    setMpsoCancelOrderNo(oldOrder.getMpsoOrderNo()).
                    setMpsoCancelSingleNo(oldOrder.getMpsoSingleNo()).
                    setMpsoCancelOrderTime(oldOrder.getMpsoOrderTime()).
                    setMpsoCancelPaymentMethod(oldOrder.getMpsoPaymentMethod()).
                    setMpsoCancelTotalPrice(oldOrder.getMpsoTotalPrice()).
                    setMpsoCancelActualPrice(oldOrder.getMpsoActualPrice()).
                    setMpsoCancelDiscount(oldOrder.getMpsoDiscount()).
                    setMpsoCancelRealDiscount(oldOrder.getMpsoRealDiscount()).
                    setMpsoCancelOrderStatus(oldOrder.getMpsoOrderStatus()).
                    setMpsoCancelScanCodeChannel(oldOrder.getMpsoScanCodeChannel());
        smMerchantProductSaleOrderCancelDao.insert(orderCancel);

        if (saleAllItems != null){
            saleAllItems.forEach(item->{
                SmMerchantProductSaleItemCancel itemCancel = new SmMerchantProductSaleItemCancel();
                itemCancel.setMpsiMpsoCancelId(orderCancel.getMpsoCancelId())
                        .setMpsiMpsoCancelOrderId(item.getMpsiMpsoId())
                        .setMpsiCancelMpbiId(item.getMpsiMpbiId())
                        .setMpsiCancelName(item.getMpsiName())
                        .setMpsiCancelRetailPrice(item.getMpsiRetailPrice())
                        .setMpsiCancelNowPrice(item.getMpsiNowPrice())
                        .setMpsiCancelNum(item.getMpsiNum())
                        .setMpsiCancelSubTotal(item.getMpsiSubTotal())
                        .setMpsiCancelIsRefund(item.getMpsiIsRefund())
                        .setMpsiCancelIsRefundType(item.getMpsiIsRefundType())
                        .setMpsiCancelRefundMethod(item.getMpsiRefundMethod())
                        .setMpsiCancelRefundReason(item.getMpsiRefundReason());
                smMerchantProductSaleItemCancelDao.insert(itemCancel);
            });
        }
        if (oldItems != null){
            oldItems.forEach(item->{
                SmMerchantProductSaleItemCancel itemCancel = new SmMerchantProductSaleItemCancel();
                itemCancel.setMpsiMpsoCancelId(orderCancel.getMpsoCancelId())
                        .setMpsiMpsoCancelOrderId(item.getMpsiMpsoId())
                        .setMpsiCancelMpbiId(item.getMpsiMpbiId())
                        .setMpsiCancelName(item.getMpsiName())
                        .setMpsiCancelRetailPrice(item.getMpsiRetailPrice())
                        .setMpsiCancelNowPrice(item.getMpsiNowPrice())
                        .setMpsiCancelNum(item.getMpsiNum())
                        .setMpsiCancelSubTotal(item.getMpsiSubTotal())
                        .setMpsiCancelIsRefund(item.getMpsiIsRefund())
                        .setMpsiCancelIsRefundType(item.getMpsiIsRefundType())
                        .setMpsiCancelRefundMethod(item.getMpsiRefundMethod())
                        .setMpsiCancelRefundReason(item.getMpsiRefundReason());
                smMerchantProductSaleItemCancelDao.insert(itemCancel);
            });
        }
        return orderCancel.getMpsoCancelId().intValue();

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

    @Override
    public boolean upload(List<SmMerchantProductSaleOrderVO> list, UserWithPermissionVO userInfo, String serialNum, String userNo, String termIp) {
        if (userInfo == null) {
            throw new NotExceptException("异步处理订单用户信息数据为空!");
        }
        if (list == null || list.isEmpty()) {
            throw new NotExceptException("门店[" + userInfo.getStoreId() + "]异步处理订单数据为空!");
        }
        log.info("开始异步处理门店[{}]的订单数据", userInfo.getStoreId());
        Collections.sort(list, new Comparator<SmMerchantProductSaleOrderVO>() {
            @Override
            public int compare(SmMerchantProductSaleOrderVO o1, SmMerchantProductSaleOrderVO o2) {
                if(o1.getMpsoCreateTime().isBefore(o2.getMpsoCreateTime())){
                    return -1;
                }else{
                    return 1;
                }
            }
        });
        LocalDateTime modifyTime = LocalDateTime.now();
        //订单编号list
        List<String> orderNos = new ArrayList<>();
        //收银员编号list
        List<String> ubiNos = new ArrayList<>();
        //品项编号list
        List<String> mpsiNos = new ArrayList<>();
        //商品编号list
        ArrayList<String> mpsiMpbiNos = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
//        for (SmMerchantProductSaleOrderVO vo : list) {
            SmMerchantProductSaleOrderVO vo=list.get(i);
            orderNos.add(vo.getMpsoOrderNo());
            ubiNos.add(vo.getMpsoUbiNo());
            if (vo.getItems() != null && !vo.getItems().isEmpty()) {
                for (SmMerchantProductSaleItemVO itemVO : vo.getItems()) {
                    mpsiNos.add(itemVO.getMpsiNo());
                    if(StringUtils.isNotBlank(itemVO.getMpsiMpbiNo())){
                        mpsiMpbiNos.add(itemVO.getMpsiMpbiNo());
                    }
                }
            }
        }
        log.info("mpsiMpbiNos:::::::::::::{}", mpsiMpbiNos);
        //门店id
        Integer storeId = userInfo.getStoreId();
        //已存在订单
        List<SmMerchantProductSaleOrder> oldOrders = merchantProductSaleOrderManager.listByCodeAndStoreId(orderNos, storeId);
        Map<String, SmMerchantProductSaleOrder> orderMap = listToMap(oldOrders, SmMerchantProductSaleOrder::getMpsoOrderNo);
        //用户列表
        List<SimpleUserInfoVO> oldUserData = BaseResultResponse.getResponseDataNotAllowNull(apiUserInfoManager.listByUserNoAndStore(ubiNos, storeId));
        Map<String, SimpleUserInfoVO> userInfoVOMap = listToMap(oldUserData, SimpleUserInfoVO::getUbiUserNo);
        List<PmMerchantProductBasicAndLibVO> productInfos = new ArrayList<>();
        if (!mpsiMpbiNos.isEmpty()) {
            productInfos = BaseResultResponse.getResponseDataNotAllowNull(apiMerchantProductManager.listByNoAndStoreId(mpsiMpbiNos, storeId));
        }
        //商品编号list
        Map<String, PmMerchantProductBasicAndLibVO> productsMap = listToMap(productInfos, PmMerchantProductBasicAndLibVO::getMpbiNo);
        //订单品项
        Map<String, List<SmMerchantProductSaleItem>> saleOrderItemMap = new HashMap<>();
        //批量处理订单
        List<SmMerchantProductSaleOrder> saleOrders = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            SmMerchantProductSaleOrderVO item = list.get(i);
//        for (SmMerchantProductSaleOrderVO item : list) {
            SmMerchantProductSaleOrder saleOrder = orderMap.get(item.getMpsoOrderNo());
            //跳过已存在订单
            if (saleOrder != null) {
                continue;
            }
            saleOrder = new SmMerchantProductSaleOrder();
            UserWithPermissionVO userInfoItem = userInfo;
            SimpleUserInfoVO itemUser = userInfoVOMap.get(item.getMpsoUbiNo());
            String itemUserNo = userNo;
            if (itemUser != null) {
                userInfoItem.setId(itemUser.getUbiId());
                userInfoItem.setJobNo(itemUser.getUbiJobNumber());
                userInfoItem.setStoreId(itemUser.getUbiStore());
                userInfoItem.setUsername(itemUser.getUbiUserName());
                itemUserNo = itemUser.getUbiUserNo();
            }
            //订单中discount重新赋值，赋的值记录整单中所有折扣
            System.out.println("********************现金支付前端传来的飞鸽传书*************************"+item);
            item.setMpsoDiscount(odrDiscountAssign(item.getMpsoActualPrice(),item.getMpsoShouldPrice()));
            RidesOrderVO ridesOrderVO = new RidesOrderVO(item, userInfoItem, serialNum, itemUserNo, termIp, null);
            salesOrderService.initOrder(saleOrder, ridesOrderVO);
            List<SmMerchantProductSaleItem> items = salesOrderService.initOrderProduct(item, saleOrder, productsMap);
            saleOrderItemMap.put(item.getMpsoOrderNo(), items);
            saleOrder.setMpsoOrderStatus(StaticDataDef.SOS_SUCCESSFUL_PAYMENT.getSddId());
            saleOrder.setMpsoIsPayment(true);
//            saleOrder.setMpsoPayTime(LocalDateTime.now());
            saleOrder.setMpsoIsRefund(false);
            saleOrders.add(saleOrder);
        }
        //这里的RidesOrderVO上面不同，这里用来跟新库存推送收银机
        RidesOrderVO ridesOrder = new RidesOrderVO(null, userInfo, serialNum, userNo, termIp, null);
        return salesOrderService.upload(ridesOrder, saleOrders, saleOrderItemMap);
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
//        orderItems.forEach(item -> {
////            List<BmStaticDataDef> results = staticDataDefManager.listByDddId(item.getMpsiUnit());
////            results.forEach(result -> item.setMpsiUnit(result.getSddName()));
//
//            StaticDataDef unitStatic = StaticDataDef.findBySddId(item.getMpsiUnit());
//            item.setMpsiUnit(unitStatic.getSddName());
//        });
        log.info("***************获取折扣的参数****************"+cardCode);
//        int callTimes = 0;
//        log.info("***********获取折扣第"+(callTimes+1)+"次**********");
//        Result<CardInfoToSaasVO> info = superVIPDiscountFeign.discountInfo(cardCode);
//        while (callTimes < RECALL_MAX_TIMES && !STATUS_SUCCESS.equals(info.getCode())){
//            log.info("***********获取折扣第"+(callTimes+2)+"次**********");
//            info = superVIPDiscountFeign.discountInfo(cardCode);
//            callTimes++;
//            try {
//                Thread.sleep(INTERVAL_TIME);
//            } catch (InterruptedException e) {
//                log.error("延时请求折扣接口报错，原因为:", e);
//            }
//        }
        Result<CardInfoToSaasVO> info = superVIPDiscountFeign.discountInfo(cardCode);
        log.info("-----------折扣返回值-------------：***" + info.toString());
        order.setMpsoSmiPhone(info.getData().getMobilePhone());
//        order.setMpsoActualPrice(order.getMpsoActualPrice().multiply(new BigDecimal((info.getData().getDiscount())).divide(new BigDecimal(10000))));
        order.setMpsoRealDiscount(order.getMpsoActualPrice().divide(order.getMpsoShouldPrice(), 2, BigDecimal.ROUND_HALF_EVEN));
        return order;
    }

    @Autowired
    private ISmMerchantProductSaleOrderCancelDao smMerchantProductSaleOrderCancelDao;
    @Autowired
    private ISmMerchantProductSaleItemCancelDao smMerchantProductSaleItemCancelDao;
    @Autowired
    private IBmStaticDataDefManager staticDataDefManager;
    @Autowired
    private ISmMerchantProductSaleOrderManager merchantProductSaleOrderManager;
    @Resource
    private RedisUtil redisUtil;
    @Autowired
    @Qualifier(YLD_ORDER_SAVE_OUT_PUT)
    private MessageChannel orderSaveOutPut;
    @Autowired
    @Qualifier(YLD_ORDER_UPDATE_OUT_PUT)
    private MessageChannel orderUpdateOutPut;
    @Autowired
    @Qualifier(YLD_ORDER_CANCEL_OUT_PUT)
    private MessageChannel orderCancelOutPut;

    @Autowired
    @Qualifier(YLD_ORDER_CANCEL_NEW_OUT_PUT)
    private MessageChannel orderCancelNewOutPut;

    @Autowired
    private ISmMerchantProductSaleItemManager merchantProductSaleItemManager;
    @Autowired
    private ISmMerchantProductSaleOrderManager smMerchantProductSaleOrderManager;
    @Autowired
    private ISmMerchantProductSaleItemManager smMerchantProductSaleItemManager;
    @Autowired
    private RmInStoreRecordOrderManager rmInStoreRecordOrderManager;
    @Autowired
    private StoreAsyncPayManagerV3Impl storeAsyncPayManagerV3;
    @Autowired
    SalesOrderServiceImpl salesOrderService;
    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private MemberServiceImpl memberService;
    @Autowired
    private OwnerServiceImpl ownerService;
    @Autowired
    private IApiMerchantProductManager apiMerchantProductManager;
    @Autowired
    private SuperVIPFeign superVip;

    @Autowired
    private IPaymentCenterManger iPaymentCenterManger;
    @Autowired
    private IApiUserGrantInfoManager apiUserInfoManager;
    @Autowired
    private SuperVIPDiscountFeign superVIPDiscountFeign;

}
