package cn.com.yyxx.yld.supply.service.impl;

import cn.com.yyxx.yld.supply.action.BaseResultResponse;
import cn.com.yyxx.yld.supply.dao.sm.ISmMerchantProductSaleItemCancelDao;
import cn.com.yyxx.yld.supply.dao.sm.ISmMerchantProductSaleOrderCancelDao;
import cn.com.yyxx.yld.supply.data.dto.*;
import cn.com.yyxx.yld.supply.data.vo.*;
import cn.com.yyxx.yld.supply.entity.pm.PmPurchaseItemInfo;
import cn.com.yyxx.yld.supply.entity.pm.PmPurchaseOrderInfo;
import cn.com.yyxx.yld.supply.entity.rm.RmInStoreRecordItem;
import cn.com.yyxx.yld.supply.entity.rm.RmInStoreRecordOrder;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleItem;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleItemCancel;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleOrder;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleOrderCancel;
import cn.com.yyxx.yld.supply.exception.NotExceptException;
import cn.com.yyxx.yld.supply.manager.*;
import cn.com.yyxx.yld.supply.manager.org.ISysSmStoreBasicInfoManager;
import cn.com.yyxx.yld.supply.manager.product.IApiMerchantProductManager;
import cn.com.yyxx.yld.supply.manager.productv2.IProductV2Manager;
import cn.com.yyxx.yld.supply.redis.RedisUtil;
import cn.com.yyxx.yld.supply.service.ISalesOrderService;
import cn.com.yyxx.yld.supply.staticMap.PayStatusCode;
import cn.com.yyxx.yld.supply.staticMap.StaticDataDef;
import cn.com.yyxx.yld.supply.staticMap.UnitStaticDataDef;
import cn.com.yyxx.yld.supply.util.ConvertUtil;
import cn.com.yyxx.yld.supply.util.MoneyUtil;
import cn.com.yyxx.yld.supply.util.TimeUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 销售订单操作实现
 * </p>
 *
 * @author liujun
 * @version 0.17.0
 * @date 2021/01/15 14:39
 **/
@Service
public class SalesOrderServiceImpl implements ISalesOrderService {

    //退货退款
    private static final Integer RETURN_AND_REFUND = 1;
    //仅退款
    private static final Integer ONLY_REFUND = 2;
    // 退款方法：1 原路返回 2 现金退款
    private static final String REFUND_BY_CASH = "2";
    private Logger log = LoggerFactory.getLogger(SalesOrderServiceImpl.class);

    @Override
    public boolean upload(RidesOrderVO ridesOrder, List<SmMerchantProductSaleOrder> saleOrders, Map<String, List<SmMerchantProductSaleItem>> saleOrderItemMap) {
        if (saleOrders.isEmpty()) {
            log.warn("需要上传的销售订单为空");
            return true;
        }
        //批量保存订单
        boolean save = merchantProductSaleOrderManager.saveOrUpdateBatch(saleOrders);
        if (save) {
//            for (SmMerchantProductSaleOrder saleOrderItem : saleOrders) {
            for (int i = 0; i < saleOrders.size(); i++) {
                List<SmMerchantProductSaleItem> items = new ArrayList<>();
                List<SmMerchantProductSaleItem> saleItems = saleOrderItemMap.get(saleOrders.get(i).getMpsoOrderNo());
                for (int j = 0; j < saleItems.size(); j++) {
//                for (SmMerchantProductSaleItem item : saleItems) {
                    saleItems.get(j).setMpsiMpsoId(saleOrders.get(i).getMpsoId());
                    items.add(saleItems.get(j));
                }
                saleOrders.get(i).setItems(saleItems);

                //变更库存
                try {
                    if (!items.isEmpty()) {
                        merchantProductSaleItemManager.saveOrUpdateBatch(items);
                    }
                    productService.saleChangeProduct(ridesOrder, items);
                } catch (Exception e) {
                    log.error("上传离线订单变更库存失败，订单号{}", saleOrders,e);
                }
            }

            //发送到账卡片
//            try {
//                ownerService.sendMessageToOwner(saleOrders);
//            } catch (Exception e) {
//                log.error("[支付成功]到账卡片发送失败，订单号{}", saleOrders.getMpsoOrderNo(), e);
//            }

            //发送市民网积分消息
            for (SmMerchantProductSaleOrder saleOrderItem : saleOrders) {
                try {
                    memberService.sendCashOrderSuccessMessage(saleOrderItem, saleOrderItem.getItems());
                } catch (Exception e) {
                    log.error("退款扣除积分发送失败，订单号{}", saleOrderItem.getMpsoOrderNo(),e);
                }
            }
        }
        return save;

    }

    @Override
    public boolean updateOrder(RidesOrderVO ridesOrder, ResponsePayVO responsePayVO) {
        SmMerchantProductSaleOrderVO order = ridesOrder.getOrder();
        SmMerchantProductSaleOrder saleOrder = smMerchantProductSaleOrderManager.getByOrderNoAndSbiId(order.getMpsoOrderNo(), ridesOrder.getUserInfo().getStoreId(), false);
        boolean save;
        List<SmMerchantProductSaleItem> items;
        if (saleOrder == null) {
            saleOrder = new SmMerchantProductSaleOrder();
            //初始订单
            initOrder(saleOrder, ridesOrder);
            //初始化订单商品
            ArrayList<String> mpsiMpbiNos = new ArrayList<>();
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                order.getItems().forEach((item) -> {
                    if (StringUtils.isNotBlank(item.getMpsiMpbiNo())) {
                        mpsiMpbiNos.add(item.getMpsiMpbiNo());
                    }
                });
            }
            //-----------------------------------------------------------------
            List<PmMerchantProductBasicAndLibVO> productInfos = new ArrayList<>();
            if (!mpsiMpbiNos.isEmpty()) {
                log.info("*****************连接服务 商品编号*****************" + mpsiMpbiNos);
                productInfos = BaseResultResponse.getResponseDataNotAllowNull(apiMerchantProductManager.listByNoAndStoreId(mpsiMpbiNos, saleOrder.getMpsoSbiId()));
            }
            //商品编号list
            Map<String, PmMerchantProductBasicAndLibVO> productsMap = productInfos.stream().collect(Collectors.toMap(PmMerchantProductBasicAndLibVO::getMpbiNo, temp -> temp));
            items = initOrderProduct(order, saleOrder, productsMap);
            //初始化支付结果
            initResultOrder(saleOrder, responsePayVO);
            //保存订单
            save = smMerchantProductSaleOrderManager.save(saleOrder);
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
            }
        } else {
            if (StringUtil.isNotBlank(order.getPayCode())) {
                saleOrder.setPayCode(order.getPayCode());
            }
            //初始化支付结果
            initResultOrder(saleOrder, responsePayVO);
            //修改
            save = smMerchantProductSaleOrderManager.updateById(saleOrder);
            items = merchantProductSaleItemManager.listByMpsoId(saleOrder.getMpsoId());
        }
        //支付成功
        if (PayStatusCode.SUCCESS.getCode().equals(responsePayVO.getPayStatus())) {
            //变更库存
            try {
                productService.saleChangeProduct(ridesOrder, items);
            } catch (Exception e) {
                log.error("变更库存失败，订单号{}", saleOrder.getMpsoOrderNo(),e);
            }
            QueryWrapper<SmMerchantProductSaleItem> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("mpsi_mpso_id",saleOrder.getMpsoId());
            queryWrapper.eq("mpsi_is_delete",0);
            List<SmMerchantProductSaleItem> saleAllItems = smMerchantProductSaleItemManager.list(queryWrapper);
            Integer orderCancelId = saveProductSaleOrderCancelData(saleOrder,saleAllItems);
            //发送市民网积分消息 同步超级会员订单
            try {
                //市民网订单同步接口增加 “总优惠金额”、“门店优惠金额”、“超级会员优惠金额”
                log.debug("======================开始增加字段开始增加字段开始增加字段==============================");
                initVipResultOrder(saleOrder, ridesOrder);
                log.debug("=================================增加字段结束=============================================");
                //单位用枚举类由code转为name
                items.forEach(item -> {
                    log.debug("单位转换前的静态sddId:{}", item.getMpsiUnit());
                    log.info("单位转换前的静态sddId:{}", item.getMpsiUnit());
                    UnitStaticDataDef unitStatic = UnitStaticDataDef.findBySddName(item.getMpsiUnit());
                    log.debug("单位转换后的文字名称:{}", unitStatic);
                    log.info("单位转换后的文字名称:{}", unitStatic);
                    item.setMpsiUnit(unitStatic.getSddName());
                });
                log.debug("========================消息队列生产塞入生产塞入生产塞入生产塞入生产塞入================================");
                memberService.sendOrderSuccessMessage(saleOrder, items, responsePayVO);
            } catch (Exception e) {
                log.error("退款扣除积分发送失败，订单号{}", saleOrder.getMpsoOrderNo(), e);
            }
            //防止重复处理订单
            String redisKey = genSuccessOrderSendMessageKey(saleOrder.getMpsoSingleNo());
            log.info("在线支付防止重复发送到账卡片存入redis的key{}",redisKey);
            if (!redisUtil.setIfAbsent(redisKey,"1",1)) {
                log.warn("1秒内不能重复处理订单,订单信息为:{}", JSON.toJSONString(saleOrder));
            } else {
                try {
                    Integer typeId = 0;
                    ownerService.sendMessageToOwner(saleOrder, typeId,orderCancelId,"0");
                    //发送到账卡片
                    log.info("订单成功,发送给用户到账卡片,订单信息为:{}", JSON.toJSONString(saleOrder));
                } catch (Exception e) {
                    log.error("[支付成功]到账卡片发送失败，订单号{}", saleOrder.getMpsoOrderNo(), e);
                }
            }
        }
        return save;
    }

    /**
     * 为了防止成功订单的到账卡片冲给你付发送,2分钟后才允许再次发送
     *
     * @param mpsoId
     * @return java.lang.String
     * @author ZhangShengYi
     * @date 2021/7/15 16:06
     */
    private String genSuccessOrderSendMessageKey(String mpsoId) {
        return "SUCCESS_ORDER_SEND_MESSAGE:" + mpsoId;
    }

    private void initVipResultOrder(SmMerchantProductSaleOrder saleOrder, RidesOrderVO ridesOrder) {
        try {
            SmMerchantProductSaleOrderVO orderInfo = ridesOrder.getOrder();
            //超级会员卡号
            saleOrder.setPayCode(orderInfo.getPayCode());
            //超级会员折扣
            if (orderInfo.getVipDiscount() != null) {
                saleOrder.setDiscount(new BigDecimal(orderInfo.getVipDiscount()).multiply(new BigDecimal(100)).longValue());
            } else {
                saleOrder.setDiscount(new BigDecimal(10000).longValue());
            }
            //应付金额
            long shouldPriceLong = orderInfo.getMpsoShouldPrice().multiply(new BigDecimal(100)).longValue();
            //实付金额
            long actualPriceLong = orderInfo.getMpsoActualPrice().multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
            //总优惠金额
            long discountAmount = shouldPriceLong - actualPriceLong;
            //优惠金额（分） 原价减去实际金额(包含门店各种优惠：门店折扣、积分抵扣、各种改价等等)
            saleOrder.setDiscountAmount(discountAmount);
            //超级会员优惠金额
            if (orderInfo.getVipAmount() == null) {
                orderInfo.setVipAmount("0");
            }
            Long vipAmount = new BigDecimal(orderInfo.getVipAmount()).multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).longValue();

            saleOrder.setMemberDiscountAmount(vipAmount);
            //积分抵扣金额
            long amountRatioLong = 0;
            if (orderInfo.getMpsoAmountRatio() != null) {
                amountRatioLong = orderInfo.getMpsoAmountRatio().multiply(new BigDecimal(100)).longValue();
            }
            //门店折扣优惠金额（不包含门店积分抵扣金额）
            Long storeDiscountAmount = discountAmount - amountRatioLong - vipAmount;
            if (storeDiscountAmount == null) {
                storeDiscountAmount = 0L;
            }
            saleOrder.setStoreDiscountAmount(storeDiscountAmount);
        } catch (Exception e) {
            log.error("同步订单增加字段赋值失败，参数信息{}", JSONObject.toJSONString(ridesOrder), e);
        }
        log.debug("=======================增加字段赋值结束==========================");
    }

    /**
     * 新版的退单，单品操作
     *
     * @param ridesOrder
     * @param responsePayVO
     * @return
     */
    @Override
    public boolean updateCannelNewOrder(RidesOrderNewCancelVO ridesOrder, ResponsePayVO responsePayVO) {
        //调用方法之前已经查询过订单所以这里为了速度不做订单查询处理

        System.out.println("   新版的退单进来了   " + responsePayVO.isOk());

        if (responsePayVO.isOk()) {
            LocalDateTime dateTime = LocalDateTime.now();
            SmMerchantProductSaleOrder saleOrder = ridesOrder.getOrder();
            saleOrder.setMpsoModifyTime(dateTime);
            saleOrder.setMpsoRefundTime(dateTime);
            createFailOrder(saleOrder, responsePayVO.getErrInfo(), StaticDataDef.SOS_REFUND_SUCCESSFUL);
            if (saleOrder != null) {
                ///以前的是查询了全部库存
                List<SmMerchantProductSaleItem> items = merchantProductSaleItemManager.listByMpsoId(saleOrder.getMpsoId());
                //判断该订单下商品是否都已退款
                Double num = 0.00;
                for (int i = 0; i < items.size(); i++) {
                    num += items.get(i).getMpsiNum().doubleValue();
                }
                if (num > -0.0000001 && num < 0.000001) {
                    //设置订单 为退款订单
                    saleOrder.setMpsoIsRefund(true);
                    boolean b = smMerchantProductSaleOrderManager.updateById(saleOrder);//设置退款完成
                }
                //目前的是还需要查询全部的单。然后和当前的数据进行合并

                List<OrderProductCancelItemVO> cancelorderitems = ridesOrder.getCancelorder().getItems();
                List<SmMerchantProductSaleItem> items1 = new ArrayList<>();
                for (OrderProductCancelItemVO ocv : cancelorderitems) {
                    for (SmMerchantProductSaleItem saleItem : items) {
                        //System.out.println(ocv.getMpsiNo().equals(saleItem.getMpsiNo()));
                        if (ocv.getMpsiNo().equals(saleItem.getMpsiNo())) {
                                saleItem.setMpsiNum(ocv.getMpsiNum());
                                items1.add(saleItem);
                        }
                    }
                }

                RidesOrderCancelVO ridesOrder1 = new RidesOrderCancelVO();
                ridesOrder1.setOrder(ridesOrder.getOrder());
//                ridesOrder1.setCancelorderitems(ridesOrder.getCancelorderitems());
                ridesOrder1.setScanType(ridesOrder.getScanType());
                ridesOrder1.setSerialNum(ridesOrder.getSerialNum());
                ridesOrder1.setTermIp(ridesOrder.getTermIp());
                ridesOrder1.setUserInfo(ridesOrder.getUserInfo());
                ridesOrder1.setUserNo(ridesOrder.getUserNo());
                //异步变更库存
                try {
                    if (items1.get(0).getMpsiIsRefundType() == 1){
                        productService.saleNewRefundProduct(ridesOrder1, items1);//退款了啊
                    }
                } catch (Exception e) {
                    log.error("退款订单变更库存失败，订单号{}", saleOrder.getMpsoOrderNo(),e);
                }
                //发送到账卡片
                try {
                    log.info("   saleOrder   " + saleOrder);
                    Integer typeId = 1;
                    ownerService.sendMessageToOwner(saleOrder, typeId , ridesOrder.getCancelOrderData(),"2");
                } catch (Exception e) {
                    log.error("退款到账卡片发送失败，订单号{}", saleOrder.getMpsoOrderNo(), e);
                }
                //发送市民网积分消息
                try {
                    memberService.sendCancelMessage(saleOrder.getMpsoSingleNo(), REFUND_BY_CASH, items1);
                } catch (Exception e) {
                    log.error("退款扣除积分发送失败，订单号{}", saleOrder.getMpsoOrderNo(), e);
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean updateCannelOrder(RidesOrderCancelVO ridesOrder, ResponsePayVO responsePayVO) {
        //调用方法之前已经查询过订单所以这里为了速度不做订单查询处理
//        if (responsePayVO.isOk()) {
        if ("0001".equals(responsePayVO.getPayStatus())) {
            LocalDateTime dateTime = LocalDateTime.now();
            SmMerchantProductSaleOrder saleOrder = ridesOrder.getOrder();
            saleOrder.setMpsoModifyTime(dateTime);
            saleOrder.setMpsoRefundTime(dateTime);
            createFailOrder(saleOrder, responsePayVO.getErrInfo(), StaticDataDef.SOS_REFUND_SUCCESSFUL);

            saleOrder.setMpsoIsRefund(true);
            saleOrder.setMpsoRefundMethod(Integer.valueOf(ridesOrder.getRefundMethod()));
            saleOrder.setMpsoRefundReason(ridesOrder.getRefundReason());
            saleOrder.setMpsoIsRefundType(ridesOrder.getIsOnlyRefund().equals("true") ? 2 : 1 );

            //整单退款 增加item退款条目并修改原item的退款状态和退款类型
            //修改原有item的容器
            List<SmMerchantProductSaleItem> oldItemList = new ArrayList<>();
            //新增退款item的容器
            List<SmMerchantProductSaleItem> cancelItemList = new ArrayList<>();

            //更新item的退款类型

            saleOrder.getItems().forEach(item -> {
                if (!item.getMpsiIsRefund()) {
                    //修改原有item
                    item.setMpsiIsRefund(true);
                    //仅退款判断
                    if (ridesOrder.getIsOnlyRefund() == null || "false".equals(ridesOrder.getIsOnlyRefund())) {
                        item.setMpsiIsRefundType(RETURN_AND_REFUND);
                    } else {
                        item.setMpsiIsRefundType(ONLY_REFUND);
                    }
                    oldItemList.add(item);

                    //新增退款item
                    SmMerchantProductSaleItem cancelItem = new SmMerchantProductSaleItem();
                    BeanUtil.copyProperties(item, cancelItem);
                    cancelItem.setMpsiIsRefund(true);
                    //仅退款判断
                    if (ridesOrder.getIsOnlyRefund() == null || "false".equals(ridesOrder.getIsOnlyRefund())) {
                        cancelItem.setMpsiIsRefundType(RETURN_AND_REFUND);
                        cancelItem.setMpsiNum(item.getMpsiNum().negate());
                    } else {
                        cancelItem.setMpsiIsRefundType(ONLY_REFUND);
                        cancelItem.setMpsiNum(BigDecimal.ZERO);
                    }
                    cancelItem.setMpsiSubTotal(item.getMpsiSubTotal().negate());
                    cancelItem.setMpsiCreateTime(LocalDateTime.now());
                    cancelItem.setMpsiModifyTime(LocalDateTime.now());
                    cancelItem.setMpsiNo(IdUtil.fastSimpleUUID());
                    cancelItem.setMpsiId(null);
                    cancelItemList.add(cancelItem);
                }
            });

            //修改退款订单order
            boolean orderUpdateBool = smMerchantProductSaleOrderManager.updateById(saleOrder);
            //修改退款商品item
            boolean itemUpdateBool = merchantProductSaleItemManager.updateBatchById(oldItemList);
            //新增退款退货商品信息 item
            boolean cancelItemAddBool = merchantProductSaleItemManager.saveBatch(cancelItemList);

            if (orderUpdateBool && itemUpdateBool && cancelItemAddBool) {
//                List<SmMerchantProductSaleItem> items = merchantProductSaleItemManager.listByMpsoId(saleOrder.getMpsoId());
                //仅退款不更改库存
                if (ridesOrder.getIsOnlyRefund() == null || "false".equals(ridesOrder.getIsOnlyRefund())) {
                    //异步变更库存
                    try {
                        //由于cancelItemList中个数是负的，退款的库存操作数量应为整数 所以这里是更改数量的操作
                        cancelItemList.forEach(item -> item.setMpsiNum(item.getMpsiNum().negate()));
                        //库存操作
                        productService.saleRefundProduct(ridesOrder, cancelItemList);
                    } catch (Exception e) {
                        log.error("退款订单变更库存失败，订单号{}", saleOrder.getMpsoOrderNo(),e);
                    }
                }
                //将本次单品退款的记录存在表中,方便发送到账卡片时查询
                QueryWrapper<SmMerchantProductSaleItem> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("mpsi_mpso_id",saleOrder.getMpsoId());
                queryWrapper.eq("mpsi_is_delete",0);
                List<SmMerchantProductSaleItem> saleAllItems = smMerchantProductSaleItemManager.list(queryWrapper);
                Integer orderCancelId = saveProductSaleOrderCancelData(saleOrder,saleAllItems);
                //发送到账卡片
                try {
                    log.info("   saleOrder   " + saleOrder);
                    Integer typeId = 2;
                    ownerService.sendMessageToOwner(saleOrder, typeId,orderCancelId,ridesOrder.getRefundMethod());
                } catch (Exception e) {
                    log.error("退款到账卡片发送失败，订单号{}", saleOrder.getMpsoOrderNo(), e);
                }
                //发送市民网积分消息
                try {
//                    memberService.sendCancelMessage(saleOrder.getMpsoSingleNo(), ridesOrder.getRefundMethod());
                    memberService.sendCancelMessage(saleOrder.getMpsoSingleNo(), ridesOrder.getRefundMethod(),saleOrder.getItems());
                } catch (Exception e) {
                    log.error("退款扣除积分发送失败，订单号{}", saleOrder.getMpsoOrderNo(), e);
                }
                return true;
            }
        }

        return false;
    }


    private void createFailOrder(SmMerchantProductSaleOrder updateOrder, String errInfo, StaticDataDef statusCode) {
        updateOrder.setMpsoOrderStatus(statusCode.getSddId());
        updateOrder.setMpsoIsPayment(false);
        updateOrder.setMpsoDescription(errInfo);
    }

    @Override
    public boolean saveOrder(RidesOrderVO ridesOrder) {
        SmMerchantProductSaleOrder saleOrder = new SmMerchantProductSaleOrder();
        SmMerchantProductSaleOrderVO order = ridesOrder.getOrder();
        //初始订单
        initOrder(saleOrder, ridesOrder);
        //初始化订单商品
        ArrayList<String> mpsiMpbiNos = new ArrayList<>();
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            order.getItems().forEach((item) -> {
                log.info("***************往进塞的号****************" + item.getMpsiMpbiNo());
                if (StringUtils.isNotBlank(item.getMpsiMpbiNo())) {
                    mpsiMpbiNos.add(item.getMpsiMpbiNo());
                }
            });
        }
        //-----------------------------------------------------------------
        List<PmMerchantProductBasicAndLibVO> productInfos = new ArrayList<>();
        log.info("****************外面商品号*******************" + JSON.toJSONString(mpsiMpbiNos));
        if (!mpsiMpbiNos.isEmpty()) {
            log.info("****************判不为空后*******************" + JSON.toJSONString(mpsiMpbiNos));
            productInfos = BaseResultResponse.getResponseDataNotAllowNull(apiMerchantProductManager.listByNoAndStoreId(mpsiMpbiNos, saleOrder.getMpsoSbiId()));
        }
        //商品编号list
        Map<String, PmMerchantProductBasicAndLibVO> productsMap = productInfos.stream().collect(Collectors.toMap(PmMerchantProductBasicAndLibVO::getMpbiNo, temp -> temp));
        List<SmMerchantProductSaleItem> items = initOrderProduct(order, saleOrder, productsMap);
        //保存订单
        log.debug("==============保存的订单==================" + saleOrder);
        boolean save = smMerchantProductSaleOrderManager.save(saleOrder);
        //保存完成.批量保存订单详情
        if (save) {
            if (!items.isEmpty()) {
                items.forEach((item) -> {
                    item.setMpsiMpsoId(saleOrder.getMpsoId());
                });
                //批量保存销售明细
                merchantProductSaleItemManager.saveBatch(items);
            }
            log.info("订单[{}]首次保存, 明细项共保存{}个", saleOrder.getMpsoId(), items.size());
        }
        return save;
    }

    @Override
    public void initOrder(SmMerchantProductSaleOrder saleOrder, RidesOrderVO ridesOrder) {
        UserWithPermissionVO userInfo = ridesOrder.getUserInfo();
        BeanUtil.copyProperties(ridesOrder.getOrder(), saleOrder);
        //门店信息
        //门店id
        saleOrder.setMpsoSbiId(userInfo.getStoreId());
        saleOrder.setMpsoSbiCode(ridesOrder.getOrder().getSbiCode());
        saleOrder.setMpsoStoreName(userInfo.getStoreName());
        //门店省id
        saleOrder.setMpsoSbiProvinceId(userInfo.getSbiProvinceId());
        //门店市id
        saleOrder.setMpsoSbiCityId(userInfo.getSbiCityId());
        //门店县id
        saleOrder.setMpsoSbiAreaId(userInfo.getSbiAreaId());
        //门店类型
        saleOrder.setMpsoSbiType(userInfo.getSbiChildType());

        //收银员信息
        saleOrder.setMpsoUbiId(userInfo.getId());
        if (saleOrder.getMpsoCashier() == null) {
            saleOrder.setMpsoCashier(userInfo.getRealName());
        }

        //设备信息
        //设备id
        saleOrder.setMpsoSdcId(userInfo.getClientId());
        //设备名称
        saleOrder.setMpsoClientName(userInfo.getClientName());
        //修改时间
        saleOrder.setMpsoModifyTime(LocalDateTime.now());
        //如果应收金额为null,赋为实际金额
        if (saleOrder.getMpsoTotalPrice() == null) {
            saleOrder.setMpsoTotalPrice(saleOrder.getMpsoRealPrice());
        }
        saleOrder.setMpsoIsDelete(false);
        saleOrder.setTermIp(ridesOrder.getTermIp());
        saleOrder.setScanType(ridesOrder.getScanType());
        saleOrder.setMpsoIsPayment(false);
        saleOrder.setMpsoIsRefund(false);
    }

    private long onlyNum(Integer sbiId) {
        String num = String.valueOf(System.currentTimeMillis()).substring(4);
        num = sbiId + num;
        return Long.parseLong(num);
    }

    @Override
    public List<SmMerchantProductSaleItem> initOrderProduct(SmMerchantProductSaleOrderVO order, SmMerchantProductSaleOrder saleOrder, Map<String, PmMerchantProductBasicAndLibVO> productsMap) {
        List<SmMerchantProductSaleItem> items = new ArrayList<>();
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            //总成本
            BigDecimal costAccount = BigDecimal.ZERO;
            log.info("************前端传送订单打印***************" + order);
            for (SmMerchantProductSaleItemVO item : order.getItems()) {
                //--构建-----------
                SmMerchantProductSaleItem oldItem = new SmMerchantProductSaleItem();
                //库存变量
                BigDecimal offset = item.getMpsiNum();
                BeanUtil.copyProperties(item, oldItem);
                log.info("************订单赋值后循环打印***********" + oldItem);
                if (StringUtils.isNotBlank(oldItem.getMpsiBarCodes()) && !"null".equals(oldItem.getMpsiBarCodes()) && "LS".equals(oldItem.getMpsiBarCodes().substring(0, 2))) {
                    //是否为临时商品
                    oldItem.setMpsiIsTemp(true);
                    //商品id
                    oldItem.setMpsiMpbiId(onlyNum(saleOrder.getMpsoSbiId()));
                    //标准商品id
                    oldItem.setMpsiPislId(onlyNum(saleOrder.getMpsoSbiId()));
                    //分类id 待分类 9999
                    oldItem.setMpsiPciId(9999);
                    //一级分类id
                    oldItem.setMpsiFirstPciId(9999);
                    //一级分类名称
                    oldItem.setMpsiFirstPciName("待分类");
                    //商品条码
                    oldItem.setMpsiBarCodes("");
                } else {
                    PmMerchantProductBasicAndLibVO productBasicInfoVO = productsMap.get(item.getMpsiMpbiNo());
                    if (productBasicInfoVO == null) {
                        log.warn("门店[{}]同步订单,根据商品编号{}查询商品id发生错误,没有查询到商品信息", saleOrder.getMpsoSbiId(), item.getMpsiMpbiNo());
                        continue;
                    }
                    //是否为临时商品
                    oldItem.setMpsiIsTemp(false);
                    //商品id
                    oldItem.setMpsiMpbiId(productBasicInfoVO.getMpbiId());
                    //标准商品id
                    oldItem.setMpsiPislId(productBasicInfoVO.getPislId());
                    //修改时间
                    oldItem.setMpsiModifyTime(LocalDateTime.now());
                    //进价
                    BigDecimal mpbiCostPrice = productBasicInfoVO.getMpbiCostPrice();
                    if (mpbiCostPrice == null || BigDecimal.ZERO.compareTo(mpbiCostPrice) == 0) {
                        mpbiCostPrice = BigDecimal.ZERO.compareTo(productBasicInfoVO.getPislSuggestedCostPrice()) == 0 ? productBasicInfoVO.getMpbiRetailPrice().multiply(BigDecimal.valueOf(0.8)) : productBasicInfoVO.getPislSuggestedCostPrice();
                    }
                    oldItem.setMpsiWholesalePrice(mpbiCostPrice);
                    //分类id
                    oldItem.setMpsiPciId(productBasicInfoVO.getPislCategory());
                    //一级分类id
                    oldItem.setMpsiFirstPciId(getCategoryByLevel(1, productBasicInfoVO.getPciParentIds()));
                    //一级分类名称
                    oldItem.setMpsiFirstPciName(getCategoryNameByLevel(1, productBasicInfoVO.getPciParentNames()));
                    //二级分类id
                    oldItem.setMpsiSecondPciId(getCategoryByLevel(2, productBasicInfoVO.getPciParentIds()));
                    //二级分类名称
                    oldItem.setMpsiSecondPciName(getCategoryNameByLevel(2, productBasicInfoVO.getPciParentNames()));
                    // 商品code
                    if (StringUtils.isBlank(oldItem.getMpsiCode()) || "null".equals(oldItem.getMpsiCode())) {
                        oldItem.setMpsiCode(null);
                    }
                    if (StringUtils.isBlank(oldItem.getMpsiBarCodes()) || "null".equals(oldItem.getMpsiBarCodes())) {
                        oldItem.setMpsiBarCodes(null);
                    }
                }
                items.add(oldItem);
                if (StringUtils.isNotBlank(oldItem.getMpsiBarCodes()) && !"null".equals(oldItem.getMpsiBarCodes()) && "LS".equals(oldItem.getMpsiBarCodes().substring(0, 2))) {
                    costAccount = costAccount.add(oldItem.getMpsiNowPrice().multiply(oldItem.getMpsiNum()));
                } else {
                    costAccount = costAccount.add(oldItem.getMpsiWholesalePrice().multiply(oldItem.getMpsiNum()));
                }
                //商品修改库存并记录
                log.debug("销售记录-{},数量{}", oldItem, offset);
            }
            saleOrder.setMpsoGrossProfit(saleOrder.getMpsoActualPrice().subtract(costAccount));

        } else {
            saleOrder.setMpsoGrossProfit(saleOrder.getMpsoActualPrice());
        }
        return items;
    }

    @Override
    public void initResultOrder(SmMerchantProductSaleOrder saleOrder, ResponsePayVO responsePayVO) {
//        try {
//            saleOrder.setMpsoPayTime(LocalDateTime.parse(responsePayVO.getReTime(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
//        } catch (Exception e) {
//            saleOrder.setMpsoPayTime(LocalDateTime.now());
//        }
        saleOrder.setMpsoPayTime(LocalDateTime.now());
        if (StrUtil.isNotBlank(responsePayVO.getPayMode())) {
            StaticDataDef staticDataDef = StaticDataDef.findBySddCode(responsePayVO.getPayMode());
            if (staticDataDef.equals(StaticDataDef.UNKNOW)) {
                saleOrder.setMpsoScanCodeChannel(StaticDataDef.SSY_OTHER.getSddId());
            } else {
                saleOrder.setMpsoScanCodeChannel(staticDataDef.getSddId());
            }
        }
        //支付订单号
//        saleOrder.setMpsoPayOrderId(responsePayVO.getThreeOrderNo());
        saleOrder.setMpsoPayOrderId(responsePayVO.getTradeNo());
        //三方买家ID
        if (responsePayVO.getOpenid() != null && !"null".equals(responsePayVO.getOpenid())) {
            saleOrder.setMpsoThirdBuyerId(responsePayVO.getOpenid());
        }
        //TODO 这里应该查询会员名称，但微服务没接口，等有时间补
        //三方买家名称
//        if (thirdBuyerName != null && !"null".equals(thirdBuyerName)) {
//            saleOrder.setMpsoThirdBuyerName(thirdBuyerName);
//        }
        if ("null".equals(saleOrder.getMpsoSmiPhone())) {
            saleOrder.setMpsoSmiPhone(null);
        }
        //支付金额
        BigDecimal payAmount = MoneyUtil.fenToYuan(responsePayVO.getAmount());
        saleOrder.setMpsoActualPayPrice(payAmount);
        PayStatusCode payStatus = PayStatusCode.queryByCode(responsePayVO.getPayStatus());
        switch (payStatus) {
            //4.1、支付成功，异步保存订单，清除Rides,修改库存...
            case SUCCESS:
                saleOrder.setMpsoIsPayment(true);
                //支付渠道
                saleOrder.setMpsoOrderStatus(StaticDataDef.SOS_SUCCESSFUL_PAYMENT.getSddId());
                log.debug("订单支付成功,订单号:{}", saleOrder.getMpsoOrderNo());
                break;
            //4.2、支付中，修改Rides值
            case NEED_PASSWORD:
                saleOrder.setMpsoIsPayment(false);
                saleOrder.setMpsoOrderStatus(StaticDataDef.SOS_WAITING_PAYMENT.getSddId());
                log.debug("订单支付中,等待用户输入密码,订单号:{}", saleOrder.getMpsoOrderNo());
                break;
            //4.3、支付失败，异步保存订单，清除Rides
            case FAIL:
            case BACK_OUT:
                log.debug("订单支付失败,订单号:{},原因:{}", saleOrder.getMpsoOrderNo(), responsePayVO.getErrInfo());
                saleOrder.setMpsoIsPayment(false);
                saleOrder.setMpsoOrderStatus(StaticDataDef.SOS_FAILURE_TO_PAY.getSddId());
                break;
            default:
                break;
        }

    }


    private AddPointDTO createAddPointDTO(SmMerchantProductSaleOrder order) {
        AddPointDTO dto = new AddPointDTO();
        BeanUtil.copyProperties(order, dto);
        List<AddPointItemDTO> items = ConvertUtil.convertList(order.getItems(), AddPointItemDTO.class);
        dto.setItems(items);
        return dto;
    }

    private <T, R> Map<R, T> listToMap(List<T> oldOrders, Function<T, R> function) {
        return oldOrders.stream().collect(Collectors.toMap(function, Function.identity(), (temp1, temp2) -> temp1));
    }

    private Integer getCategoryByLevel(Integer level, String parentIds) {
        if (StrUtil.isBlank(parentIds)) {
            return null;
        }
        String[] split = parentIds.split("-");
        if (level > split.length || level < 1) {
            return null;
        }
        String temp = split[level - 1];
        return Integer.parseInt(temp);
    }

    private String getCategoryNameByLevel(Integer level, String parentNames) {
        if (StrUtil.isBlank(parentNames)) {
            return null;
        }
        String[] split = parentNames.split(" - ");
        if (level > split.length || level < 1) {
            return null;
        }
        return split[level - 1];
    }


    /**
     * 新版入库
     *
     * @param items
     * @param user
     * @param serialNum
     * @param userNo
     * @param rukuWay
     * @return
     */
    @Override
    public BaseResultResponse<NewPutInVo> newPutIn(List<PutInStoreItemVO> items, UserWithPermissionVO user, String serialNum, String userNo, String rukuWay) {
        // 参数校验===========================
        if (user == null) {
            throw new NotExceptException("用户登陆信息有误");
        }
        if (items == null || items.size() <= 0) {
            throw new NotExceptException("入库明细不能为空!");
        }
        //  步骤: =================================================
        //  1.生成入库记录
        //  2.入库记录详情
        //  3.入库(变更商家商品信息)
        //  4.增加变更记录
        //=====================1.生成入库记录  rm_in_store_record_order 入库订单===========================
        LocalDateTime localDateTime = LocalDateTime.now();
        RmInStoreRecordOrder InStoreOrderVo = new RmInStoreRecordOrder();
        ArrayList<String> mpbiNoList = new ArrayList<>();  // 产品mpbiNo的list  用完记得清空 = null

        //  从商家商品库和平台标准库查出商品
        for (PutInStoreItemVO item : items) {
            mpbiNoList.add(item.getProductCode());
        }
        BaseResultResponse<List<PmMerchantProductBasicAndLibVO>> ProductList = apiMerchantProductManager.listByNoAndStoreId(mpbiNoList, user.getStoreId());

        //  生成 key:商品code value:PmMerchantProductBasicAndLibVO 的map
        Map<String, PmMerchantProductBasicAndLibVO> productMap = getProductMap(items, ProductList);
        //拼接生成入库单据实体
        createRminStoreOrder(InStoreOrderVo, localDateTime, user, items, productMap);
//        productMap.clear(); //清掉 map

        //  将生成的入库记录存入 并返回id
        rmInStoreRecordOrderManager.addRmInStoreRecordBackId(InStoreOrderVo);
        int backId = InStoreOrderVo.getIsroId();
        System.out.println("*******************backId:" + backId);
        //====================================1.生成入库记录 rm_in_store_record_order 入库订单 End========================================


        //=====================================2.入库记录详情   rm_in_store_record_item==================================================
        List<RmInStoreRecordItem> recordItemList = new ArrayList<>();
        List<ProductItem> productItemList = new ArrayList<>();
        // 生成入库记录详情   放到list里 批量存入
        createInStoreRecord(backId, productMap, items, localDateTime, recordItemList, productItemList);
        rmInStoreRecordItemManager.saveBatch(recordItemList);
        //=====================================2.入库记录详情 End==================================================


        //=====================================3.入库(变更商家商品信息)  pm_merchant_product_basic_info====================================
        ProductChange productChange = new ProductChange();
        productChange.setStore_id(user.getStoreId() + "");
        productChange.setSerial_num(serialNum);
        productChange.setUser_no(userNo);
        productChange.setJob_no(user.getJobNo() + "");
        if ("AddStock".equals(rukuWay)) {
            productChange.setTyped("AddStock");
        } else {
            productChange.setTyped("AddTobaccoStock");
        }
        productChange.setProductItems(productItemList);
        //调用方法 入库 并生成变更记录
        ApiMapReMsg apiMapReMsg = productV2Manager.stockModify(productChange, user.getStoreId() + "", serialNum, userNo);

        ProductChange proChange = objectMapper.convertValue(apiMapReMsg.getData(), ProductChange.class);
        if ("true".equals(apiMapReMsg.getSuccess())) {
            System.out.println("-----productV2返回的参数-------" + proChange);
            List<ProductItem> productItemsList = proChange.getProductItems();

            // 如果返回值的mpbiNO和入参的mpbiNO一样
            // 就把返回值的库存数塞进去 retun
            for (ProductItem productItem : productItemsList) {
                for (PutInStoreItemVO item : items) {
                    if (productItem.getMpbiNo().equals(item.getProductCode())) {
                        item.setNumber(productItem.getMpbiStockNumber());
                    }
                }
            }
            NewPutInVo newPutInVo = new NewPutInVo();
            newPutInVo.setPutInStoreItemVOList(items);
            newPutInVo.setRukuNo(InStoreOrderVo.getIsroCode());
            BaseResultResponse<NewPutInVo> base = new BaseResultResponse();
            base.setData(newPutInVo);
            base.setSuccess(true);
            base.setMessage("success");
            base.setType("SUPPLY_STOCK_PRODUCT_REPERTORY_IN_PUT");
            return base;
        } else {
            throw new NotExceptException(apiMapReMsg.getMessage());
        }
    }

//    /**
//     * 新版入库 for索证索票手机端
//     *
//     * @param items
//     * @param storeId
//     * @param ubiJobNumber
//     * @param userNo
//     * @param rukuWay
//     * @return
//     */
//    @Override
//    public BaseResultResponse<NewPutInVo> newPutInForPhone(List<PutInStoreItemVO> items, String storeId, String ubiJobNumber, String userNo, String rukuWay, String ubiRealName, String ubiStoreName,String ubiId) {
//        // 参数校验===========================
//        if (items == null || items.size() <= 0) {
//            throw new NotExceptException("入库明细不能为空!");
//        }
//        if (storeId == null) {
//            throw new NotExceptException("门店id不能为空!");
//        }
//
////        ArrayList<Integer> storeList = new ArrayList<>();
////        storeList.add(Integer.valueOf(storeId));
//        BaseResultResponse<SmStoreBasicInfoVO> listBaseResultResponse = sysSmStoreBasicInfoManager.getStoreInfoById(Integer.valueOf(storeId));
//        String sbiCode = "";
//        if (listBaseResultResponse.getSuccess() && listBaseResultResponse.getData() != null) {
//            SmStoreBasicInfoVO listBaseResultResponseData = listBaseResultResponse.getData();
//            if (listBaseResultResponseData!=null) {
//                sbiCode = listBaseResultResponseData.getSbiCode();
//            }
//        } else {
//            throw new NotExceptException("查询门店信息为空!");
//        }
//        //  步骤: =================================================
//        //  1.生成入库记录
//        //  2.入库记录详情
//        //  3.入库(变更商家商品信息)
//        //  4.增加变更记录
//        //=====================1.生成入库记录  rm_in_store_record_order 入库订单===========================
//        LocalDateTime localDateTime = LocalDateTime.now();
//        RmInStoreRecordOrder InStoreOrderVo = new RmInStoreRecordOrder();
//        List<String> mpbiNoList = new ArrayList<>();  // 产品mpbiNo的list  用完记得清空 = null
//
//        //  从商家商品库和平台标准库查出商品
//        for (PutInStoreItemVO item : items) {
//            mpbiNoList.add(item.getProductCode());
//        }
//        BaseResultResponse<List<PmMerchantProductBasicAndLibVO>> ProductList = apiMerchantProductManager.listByNoAndStoreId(mpbiNoList, Integer.parseInt(storeId));
//
//        //  生成 key:商品code value:PmMerchantProductBasicAndLibVO 的map
//        Map<String, PmMerchantProductBasicAndLibVO> productMap = getProductMap(items, ProductList);
//        //拼接生成入库单据实体
//        createRminStoreOrderForPhone(InStoreOrderVo, localDateTime, storeId, ubiJobNumber, items, productMap, ubiRealName, ubiStoreName,sbiCode,ubiId);
////        productMap.clear(); //清掉 map
//
//        //  将生成的入库记录存入 并返回id
//        rmInStoreRecordOrderManager.addRmInStoreRecordBackId(InStoreOrderVo);
//        int backId = InStoreOrderVo.getIsroId();
//        System.out.println("*******************backId:" + backId);
//        //====================================1.生成入库记录 rm_in_store_record_order 入库订单 End========================================
//
//
//        //=====================================2.入库记录详情   rm_in_store_record_item==================================================
//        List<RmInStoreRecordItem> recordItemList = new ArrayList<>();
//        List<ProductItem> productItemList = new ArrayList<>();
//        // 生成入库记录详情   放到list里 批量存入
//        createInStoreRecord(backId, productMap, items, localDateTime, recordItemList, productItemList);
//        rmInStoreRecordItemManager.saveBatch(recordItemList);
//        //=====================================2.入库记录详情 End==================================================
//
//
//        //=====================================3.入库(变更商家商品信息)  pm_merchant_product_basic_info====================================
//        ProductChange productChange = new ProductChange();
//        productChange.setStore_id(storeId);
////        productChange.setSerial_num(serialNum);
//        productChange.setUser_no(userNo);
//        productChange.setJob_no(ubiJobNumber);
//        if ("AddStock".equals(rukuWay)) {
//            productChange.setTyped("AddStock");
//        } else {
//            productChange.setTyped("AddTobaccoStock");
//        }
//        productChange.setProductItems(productItemList);
//        //调用方法 入库 并生成变更记录   手机端入库无序列号  传空了
//        ApiMapReMsg apiMapReMsg = productV2Manager.stockModify(productChange, storeId, "", userNo);
//
//        ProductChange proChange = objectMapper.convertValue(apiMapReMsg.getData(), ProductChange.class);
//        if ("true".equals(apiMapReMsg.getSuccess())) {
//            System.out.println("-----productV2返回的参数-------" + proChange);
//            List<ProductItem> productItemsList = proChange.getProductItems();
//
//            // 如果返回值的mpbiNO和入参的mpbiNO一样
//            // 就把返回值的库存数塞进去 retun
//            for (ProductItem productItem : productItemsList) {
//                for (PutInStoreItemVO item : items) {
//                    if (productItem.getMpbiNo().equals(item.getProductCode())) {
//                        item.setNumber(productItem.getMpbiStockNumber());
//                    }
//                }
//            }
//            NewPutInVo newPutInVo = new NewPutInVo();
//            newPutInVo.setPutInStoreItemVOList(items);
//            newPutInVo.setRukuNo(InStoreOrderVo.getIsroCode());
//            BaseResultResponse<NewPutInVo> base = new BaseResultResponse();
//            base.setData(newPutInVo);
//            base.setSuccess(true);
//            base.setMessage("success");
//            base.setType("SUPPLY_STOCK_PRODUCT_REPERTORY_IN_PUT");
//            return base;
//        } else {
//            throw new NotExceptException(apiMapReMsg.getMessage());
//        }
//    }


    /**
     * 烟草一键入库
     *
     * @param user
     * @param orderIdLong
     * @param isNewRuku
     * @param serialNum
     * @param userNo
     * @return
     */
    @Override
    public Boolean oneBuiltIntoTheWarehouse(UserWithPermissionVO user, Long orderIdLong, Boolean isNewRuku, String serialNum, String userNo) {

        //获取烟草订单
        PmPurchaseOrderInfo order = pmPurchaseOrderInfoManager.findOrderById(orderIdLong, false, true);
        //获取烟草订单详情
        if (order == null) {
            throw new NotExceptException("未找到烟草订单,或该订单已入库。");
        }
        List<PmPurchaseItemInfo> currInfoList = purchaseItemInfoManager.findListByPoiId(order.getPoiId());
        if (currInfoList.isEmpty()) {
            throw new NotExceptException("烟草订单详情为空");
        }
        List<PmPurchaseItemInfo> infoList = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        for (PmPurchaseItemInfo currItem : currInfoList) {
            if (ids.contains(currItem.getPiiMpbiId())) {
                int index = 0;
                for (int i = 0; i < ids.size(); i++) {
                    if (ids.get(i).equals(currItem.getPiiMpbiId())) {
                        index = i;
                        break;
                    }
                }
                PmPurchaseItemInfo currInfo = infoList.get(index);
                currInfo.setPiiNum(currInfo.getPiiNum().add(currItem.getPiiNum()));
                infoList.set(index, currInfo);
            } else {
                infoList.add(currItem);
                ids.add(currItem.getPiiMpbiId());
            }

        }
        // 构建参数

        //  查出mpbiid 组成list 去product查出商品信息 获取mpbiNo
        List<PutInStoreItemVO> putInList = new ArrayList<>();
        List<Long> pislIdList = new ArrayList<>();
        for (PmPurchaseItemInfo pmPurchaseItemInfo : infoList) {
            pislIdList.add(pmPurchaseItemInfo.getPiiMpbiId());
        }
        List<PmMerchantProductBasicInfoVO> productList = new ArrayList<>();
        if (!pislIdList.isEmpty()) {
            productList = BaseResultResponse.getResponseDataNotAllowNull(apiMerchantProductManager.getListByPislIdAndSbiId(pislIdList, user.getStoreId()));
        }
        log.info("productList长度：{}", productList.size());
        Map<Long, PmMerchantProductBasicInfoVO> productsMap = new HashMap<>();
        for (PmMerchantProductBasicInfoVO merchantProductBasicInfoVO : productList) {
            productsMap.put(merchantProductBasicInfoVO.getMpbiPislId(), merchantProductBasicInfoVO);
        }
//                productList.stream().collect(Collectors.toMap(PmMerchantProductBasicInfoVO::getMpbiPislId, temp -> temp));
        List<ProductItem> newProduct = new ArrayList<>();
        List<Long> newProductIds = new ArrayList<>();
        //判断有没有新增商品
        for (PmPurchaseItemInfo pmPurchaseItemInfo : infoList) {
            PmMerchantProductBasicInfoVO pmMerchantProductBasicInfoVO = productsMap.get(pmPurchaseItemInfo.getPiiMpbiId());
            log.info("获取商品信息{}", pmMerchantProductBasicInfoVO);
            if (pmMerchantProductBasicInfoVO == null) {
                if (newProductIds.contains(pmPurchaseItemInfo.getPiiMpbiId())) {
                    continue;
                }
                log.info("newProduct添加");
                ProductItem newProductItem = new ProductItem();
                StockProductInfoLibDTO libDTO = new StockProductInfoLibDTO();
                ProductInfoStandardLibVO pmProductInfoStandardLibVO =null;
                try {
                    pmProductInfoStandardLibVO = BaseResultResponse.getResponseDataNotAllowNull(
                        apiMerchantProductManager.getPmProductInfoStandardLibEById(pmPurchaseItemInfo.getPiiMpbiId()));
                }catch (NotExceptException e){
                    log.error("查询yld-product-info，商品ID为{}，未找到对应记录",pmPurchaseItemInfo.getPiiMpbiId(),e);
                    throw new NotExceptException("查询商品ID为"+pmPurchaseItemInfo.getPiiMpbiId()+"的商品，未找到对应记录，请联系商务经理");
                }
                BeanUtil.copyProperties(pmProductInfoStandardLibVO, libDTO);
                newProductItem.setLib(libDTO);
                newProductItem.setMpbiPislId(pmPurchaseItemInfo.getPiiMpbiId());
                newProductItem.setMpbiRetailPrice(pmPurchaseItemInfo.getPiiRetailPrice());
                newProductItem.setMpbiSbiId(user.getStoreId());
                newProductItem.setMpbiStockNumber(BigDecimal.ZERO);
                newProductItem.setMpbiWholesalePrice(pmPurchaseItemInfo.getPiiWholesalePrice());
                newProductItem.setMpbiNo(IdUtil.fastSimpleUUID());
                newProduct.add(newProductItem);
                newProductIds.add(pmPurchaseItemInfo.getPiiMpbiId());
            } else {
                PutInStoreItemVO putinvo = new PutInStoreItemVO();
                putinvo.setProductCode(pmMerchantProductBasicInfoVO.getMpbiNo());
                putinvo.setNumber(pmPurchaseItemInfo.getPiiNum());
                putInList.add(putinvo);
            }
        }
        log.info("newProduct的长度：{}", newProduct.size());
        //有需要新增的商品
        if (newProduct.size() > 0) {
            log.info("烟草进入新增");
            ProductChange productChange = new ProductChange();
            productChange.setStore_id(user.getStoreId() + "");
            productChange.setSerial_num(serialNum);
            productChange.setUser_no(userNo);
            productChange.setJob_no(user.getJobNo() + "");
            productChange.setTyped("AddProduct");
            for (ProductItem currItem : newProduct) {
                List<ProductItem> currList = new ArrayList<>();
                currList.add(currItem);
                productChange.setProductItems(currList);
                ApiMapReMsg apiMapReMsg = productV2Manager.stockModify(productChange, user.getStoreId() + "", serialNum, userNo);
                if (!"true".equals(apiMapReMsg.getSuccess())) {
                    log.error("烟草入库商品添加失败，门店id{},商品pislId{}", user.getStoreId(), currItem.getMpbiPislId());
                    throw new NotExceptException("入库失败");
                }
            }

//            ApiMapReMsg apiMapReMsg = productV2Manager.stockModify(productChange, user.getStoreId() + "", serialNum, userNo);
//            if ("true".equals(apiMapReMsg.getSuccess())) {
            productList = BaseResultResponse.getResponseDataNotAllowNull(apiMerchantProductManager.getListByPislIdAndSbiId(pislIdList, user.getStoreId()));
            for (PmMerchantProductBasicInfoVO merchantProductBasicInfoVO : productList) {
                productsMap.put(merchantProductBasicInfoVO.getMpbiPislId(), merchantProductBasicInfoVO);
            }
            putInList = new ArrayList<>();
            for (PmPurchaseItemInfo pmPurchaseItemInfo : infoList) {
                PmMerchantProductBasicInfoVO pmMerchantProductBasicInfoVO = productsMap.get(pmPurchaseItemInfo.getPiiMpbiId());
                PutInStoreItemVO putinvo = new PutInStoreItemVO();
                putinvo.setProductCode(pmMerchantProductBasicInfoVO.getMpbiNo());
                putinvo.setNumber(pmPurchaseItemInfo.getPiiNum());
                putInList.add(putinvo);
            }
//            }
        }

        BaseResultResponse<NewPutInVo> addTobaccoStock = newPutIn(putInList, user, serialNum, userNo, "AddTobaccoStock");
        if (addTobaccoStock.getSuccess()) {
            order.setPoiIsIn(true);
            order.updateById();
            return true;
        }
        throw new NotExceptException("入库失败");

    }


    /**
     * 生成key:产品code value:产品价格的map
     *
     * @param items
     * @return
     */
    public Map<String, PmMerchantProductBasicAndLibVO> getProductMap(List<PutInStoreItemVO> items, BaseResultResponse<List<PmMerchantProductBasicAndLibVO>> ProductList) {


        List<PmMerchantProductBasicAndLibVO> productListData = ProductList.getData(); // 商品信息

        Map<String, PmMerchantProductBasicAndLibVO> productMap = new HashMap<>();  // key:商品code value:商品价格    使用完也记得清掉
        for (PutInStoreItemVO item : items) {
            for (PmMerchantProductBasicAndLibVO productListDatum : productListData) {
                if (item.getProductCode().equals(productListDatum.getMpbiNo())) {
                    productMap.put(item.getProductCode(), productListDatum);
                }
            }
        }
        return productMap;
    }

    /**
     * 拼接入库单据
     *
     * @param InStoreOrderVo
     * @param localDateTime
     * @param user
     * @param items
     * @return
     */
    public void createRminStoreOrder(RmInStoreRecordOrder InStoreOrderVo, LocalDateTime localDateTime, UserWithPermissionVO user, List<PutInStoreItemVO> items, Map<String, PmMerchantProductBasicAndLibVO> productMap) {


        // ==============计算总进货额=======
        //总进货额
        BigDecimal totalPrice = BigDecimal.ZERO;
        //商品总件数
        BigDecimal totalNum = BigDecimal.ZERO;


        for (PutInStoreItemVO item : items) {
            BigDecimal oneProductPrice;
            if (productMap.get(item.getProductCode()).getMpbiCostPrice() != null) {
                oneProductPrice = productMap.get(item.getProductCode()).getMpbiCostPrice();
            } else {
                oneProductPrice = productMap.get(item.getProductCode()).getPislSuggestedCostPrice();
            }
            BigDecimal oneProductTotalPrice = item.getNumber().multiply(oneProductPrice);
            totalNum = totalNum.add(item.getNumber());
            totalPrice = totalPrice.add(oneProductTotalPrice);
        }
//        productMap.clear(); //清掉 map

        InStoreOrderVo.setIsroCode(IdUtil.fastSimpleUUID());
        InStoreOrderVo.setIsroCreateTime(localDateTime);
        InStoreOrderVo.setIsroInType("1f011c98bb614c738e758b87bd53fffc"); //  普通入库
        InStoreOrderVo.setIsroIsDelete(false);
        InStoreOrderVo.setIsroModifyTime(localDateTime);
        InStoreOrderVo.setIsroNo(TimeUtil.timeId());
        InStoreOrderVo.setIsroOperUserId(user.getId());
        InStoreOrderVo.setIsroOperUserName(user.getRealName());
        InStoreOrderVo.setIsroOperUserNum(user.getJobNum());
        InStoreOrderVo.setIsroRealPrice(totalPrice);  //总进货额
        InStoreOrderVo.setIsroRowNum(items.size());
        InStoreOrderVo.setIsroSbiCode(user.getStoreNo());
        InStoreOrderVo.setIsroSbiFullName(user.getStoreName());
        InStoreOrderVo.setIsroSbiId(user.getStoreId());
        InStoreOrderVo.setIsroSdcClientName(user.getClientName());
        InStoreOrderVo.setIsroSdcId(user.getClientId());
        InStoreOrderVo.setIsroSort(1); //字段没用  但是还设了不可为null  全传1
        InStoreOrderVo.setIsroTerminalType("2aa55c8a279546688f65f4ceee19bca4"); //收银端
        InStoreOrderVo.setIsroTime(localDateTime);
        InStoreOrderVo.setIsroTotalNum(totalNum);
    }

    public void createInStoreRecord(int backId, Map<String, PmMerchantProductBasicAndLibVO> productMap, List<PutInStoreItemVO> items, LocalDateTime localDateTime, List<RmInStoreRecordItem> recordItemList, List<ProductItem> productItemList) {

        for (PutInStoreItemVO inRecorditem : items) {
            RmInStoreRecordItem recordItem = new RmInStoreRecordItem();
            ProductItem mProductItem = new ProductItem();
            PmMerchantProductBasicAndLibVO productItem = productMap.get(inRecorditem.getProductCode());
            recordItem.setIsriCode((IdUtil.fastSimpleUUID()));
            recordItem.setIsriIsroId(backId);
            recordItem.setIsriMpbiId(productItem.getMpbiId());
            recordItem.setIsriBarCode(productItem.getPislBarCode());
//            recordItem.setIsriCarCodes() 有待研究
            recordItem.setIsriName(productItem.getPislName());
            recordItem.setIsriShortPinyin(productItem.getPislShortPinyin());
            recordItem.setIsriShortCode(productItem.getMpbiShortCode());
            recordItem.setIsriUnit(productItem.getPislUnit());
            recordItem.setIsriSize(productItem.getPislSize());
            recordItem.setIsriCategory(productItem.getPciParentNames());
            recordItem.setIsriWholesalePrice(productItem.getMpbiWholesalePrice());
            recordItem.setIsriRetailPrice(productItem.getMpbiRetailPrice());
            recordItem.setIsriCostPrice(productItem.getMpbiCostPrice());
            recordItem.setIsriOriginaliStockNumber(productItem.getMpbiStockNumber());
            recordItem.setIsriWarehouseNumber(inRecorditem.getNumber()); //入库数量
            recordItem.setIsriInventoryNumber(productItem.getMpbiStockNumber().add(inRecorditem.getNumber())); //新库存数
            if (recordItem.getIsriCostPrice() == null) {
                recordItem.setIsriCostPrice(new BigDecimal(0.0));
                log.info("成本价数据有null，已经赋值为0,商品信息，recordItem:{}", JSON.toJSONString(recordItem));
            }
            recordItem.setIsriAccount(inRecorditem.getNumber().multiply(recordItem.getIsriCostPrice()));
            recordItem.setIsriCreateTime(localDateTime);
            recordItem.setIsriModifyTime(localDateTime);
            recordItem.setIsriIsDelete(false);
            recordItem.setIsriSort(1); // 没用 全部传1
            recordItemList.add(recordItem);
            mProductItem.setMpbiId(productItem.getMpbiId());
            mProductItem.setMpbiNo(inRecorditem.getProductCode());
            mProductItem.setChangeStokNum(inRecorditem.getNumber());
            productItemList.add(mProductItem);
        }


    }


    private Integer saveProductSaleOrderCancelData(SmMerchantProductSaleOrder oldOrder,List<SmMerchantProductSaleItem> saleAllItems) {

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
        return orderCancel.getMpsoCancelId().intValue();

    }


    /**
     * 拼接入库单据
     *
     * @param InStoreOrderVo
     * @param localDateTime
     * @param items
     * @return
     */
//    public void createRminStoreOrderForPhone(
//            RmInStoreRecordOrder InStoreOrderVo,
//            LocalDateTime localDateTime,
//            String storeId, String ubiJobNumber,
//            List<PutInStoreItemVO> items,
//            Map<String, PmMerchantProductBasicAndLibVO> productMap,
//            String ubiRealName,
//            String ubiStoreName,
//            String sbiCode,
//            String ubiId) {
//
//
//        // ==============计算总进货额=======
//        //总进货额
//        BigDecimal totalPrice = BigDecimal.ZERO;
//        //商品总件数
//        BigDecimal totalNum = BigDecimal.ZERO;
//
//
//        for (PutInStoreItemVO item : items) {
//            BigDecimal oneProductPrice;
//            if (productMap.get(item.getProductCode()).getMpbiCostPrice() != null) {
//                oneProductPrice = productMap.get(item.getProductCode()).getMpbiCostPrice();
//            } else {
//                oneProductPrice = productMap.get(item.getProductCode()).getPislSuggestedCostPrice();
//            }
//            BigDecimal oneProductTotalPrice = item.getNumber().multiply(oneProductPrice);
//            totalNum = totalNum.add(item.getNumber());
//            totalPrice = totalPrice.add(oneProductTotalPrice);
//        }
////        productMap.clear(); //清掉 map
//
//        InStoreOrderVo.setIsroCode(IdUtil.fastSimpleUUID());
//        InStoreOrderVo.setIsroCreateTime(localDateTime);
//        InStoreOrderVo.setIsroInType("1f011c98bb614c738e758b87bd53fffc"); //  普通入库
//        InStoreOrderVo.setIsroIsDelete(false);
//        InStoreOrderVo.setIsroModifyTime(localDateTime);
//        InStoreOrderVo.setIsroNo(TimeUtil.timeId());
//        InStoreOrderVo.setIsroOperUserId(Long.valueOf(ubiId));
//        InStoreOrderVo.setIsroOperUserName(ubiRealName);
//        InStoreOrderVo.setIsroOperUserNum(ubiJobNumber);
//        InStoreOrderVo.setIsroRealPrice(totalPrice);  //总进货额
//        InStoreOrderVo.setIsroRowNum(items.size());
//        InStoreOrderVo.setIsroSbiCode(sbiCode);
//        InStoreOrderVo.setIsroSbiFullName(ubiStoreName);
//        InStoreOrderVo.setIsroSbiId(Integer.valueOf(storeId));
////        InStoreOrderVo.setIsroSdcClientName(user.getClientName());
////        InStoreOrderVo.setIsroSdcId(user.getClientId());
//        InStoreOrderVo.setIsroSort(1); //字段没用  但是还设了不可为null  全传1
//        InStoreOrderVo.setIsroTerminalType("a7c9f61b438946699fd0a53eeb885555"); //掌柜端
//        InStoreOrderVo.setIsroTime(localDateTime);
//        InStoreOrderVo.setIsroTotalNum(totalNum);
//    }

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ISmMerchantProductSaleOrderCancelDao smMerchantProductSaleOrderCancelDao;
    @Autowired
    private ISmMerchantProductSaleItemCancelDao smMerchantProductSaleItemCancelDao;
    @Autowired
    private ISmMerchantProductSaleOrderManager merchantProductSaleOrderManager;
    @Autowired
    private ISmMerchantProductSaleItemManager smMerchantProductSaleItemManager;
    @Autowired
    private ISmMerchantProductSaleItemManager merchantProductSaleItemManager;
    @Autowired
    private IApiMerchantProductManager apiMerchantProductManager;
    @Autowired
    private IProductV2Manager productV2Manager;
    @Autowired
    private ISmMerchantProductSaleOrderManager smMerchantProductSaleOrderManager;
    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private MemberServiceImpl memberService;
    @Autowired
    private OwnerServiceImpl ownerService;
    @Autowired
    private RmInStoreRecordOrderManager rmInStoreRecordOrderManager;
    @Autowired
    private RmInStoreRecordItemManager rmInStoreRecordItemManager;
    @Autowired
    private PmPurchaseItemInfoManager purchaseItemInfoManager;
    @Autowired
    private IPmPurchaseOrderInfoManager pmPurchaseOrderInfoManager;
    @Autowired
    private ISysSmStoreBasicInfoManager sysSmStoreBasicInfoManager;
    @Autowired
    private RedisUtil redisUtil;

}
