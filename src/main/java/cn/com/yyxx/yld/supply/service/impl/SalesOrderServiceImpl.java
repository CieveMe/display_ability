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
import cn.com.yyxx.yld.supply.util.ConvertUtil;
import cn.com.yyxx.yld.supply.util.MoneyUtil;
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
 * ????????????????????????
 * </p>
 *
 * @author hz
 * @version 0.17.0
 * @date 2021/01/15 14:39
 **/
@Service
public class SalesOrderServiceImpl implements ISalesOrderService {

    //????????????
    private static final Integer RETURN_AND_REFUND = 1;
    //?????????
    private static final Integer ONLY_REFUND = 2;
    // ???????????????1 ???????????? 2 ????????????
    private static final String REFUND_BY_CASH = "2";
    private Logger log = LoggerFactory.getLogger(SalesOrderServiceImpl.class);

    @Override
    public boolean upload(RidesOrderVO ridesOrder, List<SmMerchantProductSaleOrder> saleOrders, Map<String, List<SmMerchantProductSaleItem>> saleOrderItemMap) {
        if (saleOrders.isEmpty()) {
            log.warn("?????????????????????????????????");
            return true;
        }
        //??????????????????
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

                //????????????
                try {
                    if (!items.isEmpty()) {
                        merchantProductSaleItemManager.saveOrUpdateBatch(items);
                    }
                    productService.saleChangeProduct(ridesOrder, items);
                } catch (Exception e) {
                    log.error("????????????????????????????????????????????????{}", saleOrders,e);
                }
            }

            //??????????????????
//            try {
//                ownerService.sendMessageToOwner(saleOrders);
//            } catch (Exception e) {
//                log.error("[????????????]????????????????????????????????????{}", saleOrders.getMpsoOrderNo(), e);
//            }

            //???????????????????????????
            for (SmMerchantProductSaleOrder saleOrderItem : saleOrders) {
                try {
                    memberService.sendCashOrderSuccessMessage(saleOrderItem, saleOrderItem.getItems());
                } catch (Exception e) {
                    log.error("??????????????????????????????????????????{}", saleOrderItem.getMpsoOrderNo(),e);
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
            //????????????
            initOrder(saleOrder, ridesOrder);
            //?????????????????????
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
                log.info("*****************???????????? ????????????*****************" + mpsiMpbiNos);
                productInfos = BaseResultResponse.getResponseDataNotAllowNull(apiMerchantProductManager.listByNoAndStoreId(mpsiMpbiNos, saleOrder.getMpsoSbiId()));
            }
            //????????????list
            Map<String, PmMerchantProductBasicAndLibVO> productsMap = productInfos.stream().collect(Collectors.toMap(PmMerchantProductBasicAndLibVO::getMpbiNo, temp -> temp));
            items = initOrderProduct(order, saleOrder, productsMap);
            //?????????????????????
            initResultOrder(saleOrder, responsePayVO);
            //????????????
            save = smMerchantProductSaleOrderManager.save(saleOrder);
            //????????????.
            if (save) {
                if (!items.isEmpty()) {
                    SmMerchantProductSaleOrder finalSaleOrder = saleOrder;
                    items.forEach((item) -> {
                        item.setMpsiMpsoId(finalSaleOrder.getMpsoId());
                    });
                    //????????????????????????
                    merchantProductSaleItemManager.saveBatch(items);
                }
                log.info("??????[{}]????????????, ??????????????????{}???", saleOrder.getMpsoId(), items.size());
            }
        } else {
            if (StringUtil.isNotBlank(order.getPayCode())) {
                saleOrder.setPayCode(order.getPayCode());
            }
            //?????????????????????
            initResultOrder(saleOrder, responsePayVO);
            //??????
            save = smMerchantProductSaleOrderManager.updateById(saleOrder);
            items = merchantProductSaleItemManager.listByMpsoId(saleOrder.getMpsoId());
        }
        //????????????
        if (PayStatusCode.SUCCESS.getCode().equals(responsePayVO.getPayStatus())) {
            //????????????
            try {
                productService.saleChangeProduct(ridesOrder, items);
            } catch (Exception e) {
                log.error("??????????????????????????????{}", saleOrder.getMpsoOrderNo(),e);
            }
            QueryWrapper<SmMerchantProductSaleItem> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("mpsi_mpso_id",saleOrder.getMpsoId());
            queryWrapper.eq("mpsi_is_delete",0);
            List<SmMerchantProductSaleItem> saleAllItems = smMerchantProductSaleItemManager.list(queryWrapper);
            Integer orderCancelId = saveProductSaleOrderCancelData(saleOrder,saleAllItems);
            //??????????????????????????? ????????????????????????
            try {
                //????????????????????????????????? ?????????????????????????????????????????????????????????????????????????????????
                log.debug("======================??????????????????????????????????????????????????????==============================");
                initVipResultOrder(saleOrder, ridesOrder);
                log.debug("=================================??????????????????=============================================");
                //?????????????????????code??????name
                items.forEach(item -> {
                    log.debug("????????????????????????sddId:{}", item.getMpsiUnit());
                    log.info("????????????????????????sddId:{}", item.getMpsiUnit());
                    UnitStaticDataDef unitStatic = UnitStaticDataDef.findBySddName(item.getMpsiUnit());
                    log.debug("??????????????????????????????:{}", unitStatic);
                    log.info("??????????????????????????????:{}", unitStatic);
                    item.setMpsiUnit(unitStatic.getSddName());
                });
                log.debug("========================????????????????????????????????????????????????????????????????????????================================");
                memberService.sendOrderSuccessMessage(saleOrder, items, responsePayVO);
            } catch (Exception e) {
                log.error("??????????????????????????????????????????{}", saleOrder.getMpsoOrderNo(), e);
            }
            //????????????????????????
            String redisKey = genSuccessOrderSendMessageKey(saleOrder.getMpsoSingleNo());
            log.info("????????????????????????????????????????????????redis???key{}",redisKey);
            if (!redisUtil.setIfAbsent(redisKey,"1",1)) {
                log.warn("1??????????????????????????????,???????????????:{}", JSON.toJSONString(saleOrder));
            } else {
                try {
                    Integer typeId = 0;
                    ownerService.sendMessageToOwner(saleOrder, typeId,orderCancelId,"0");
                    //??????????????????
                    log.info("????????????,???????????????????????????,???????????????:{}", JSON.toJSONString(saleOrder));
                } catch (Exception e) {
                    log.error("[????????????]????????????????????????????????????{}", saleOrder.getMpsoOrderNo(), e);
                }
            }
        }
        return save;
    }

    /**
     * ?????????????????????????????????????????????????????????,2??????????????????????????????
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
            //??????????????????
            saleOrder.setPayCode(orderInfo.getPayCode());
            //??????????????????
            if (orderInfo.getVipDiscount() != null) {
                saleOrder.setDiscount(new BigDecimal(orderInfo.getVipDiscount()).multiply(new BigDecimal(100)).longValue());
            } else {
                saleOrder.setDiscount(new BigDecimal(10000).longValue());
            }
            //????????????
            long shouldPriceLong = orderInfo.getMpsoShouldPrice().multiply(new BigDecimal(100)).longValue();
            //????????????
            long actualPriceLong = orderInfo.getMpsoActualPrice().multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
            //???????????????
            long discountAmount = shouldPriceLong - actualPriceLong;
            //????????????????????? ????????????????????????(???????????????????????????????????????????????????????????????????????????)
            saleOrder.setDiscountAmount(discountAmount);
            //????????????????????????
            if (orderInfo.getVipAmount() == null) {
                orderInfo.setVipAmount("0");
            }
            Long vipAmount = new BigDecimal(orderInfo.getVipAmount()).multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).longValue();

            saleOrder.setMemberDiscountAmount(vipAmount);
            //??????????????????
            long amountRatioLong = 0;
            if (orderInfo.getMpsoAmountRatio() != null) {
                amountRatioLong = orderInfo.getMpsoAmountRatio().multiply(new BigDecimal(100)).longValue();
            }
            //???????????????????????????????????????????????????????????????
            Long storeDiscountAmount = discountAmount - amountRatioLong - vipAmount;
            if (storeDiscountAmount == null) {
                storeDiscountAmount = 0L;
            }
            saleOrder.setStoreDiscountAmount(storeDiscountAmount);
        } catch (Exception e) {
            log.error("???????????????????????????????????????????????????{}", JSONObject.toJSONString(ridesOrder), e);
        }
        log.debug("=======================????????????????????????==========================");
    }

    /**
     * ??????????????????????????????
     *
     * @param ridesOrder
     * @param responsePayVO
     * @return
     */
    @Override
    public boolean updateCannelNewOrder(RidesOrderNewCancelVO ridesOrder, ResponsePayVO responsePayVO) {
        //???????????????????????????????????????????????????????????????????????????????????????

        System.out.println("   ????????????????????????   " + responsePayVO.isOk());

        if (responsePayVO.isOk()) {
            LocalDateTime dateTime = LocalDateTime.now();
            SmMerchantProductSaleOrder saleOrder = ridesOrder.getOrder();
            saleOrder.setMpsoModifyTime(dateTime);
            saleOrder.setMpsoRefundTime(dateTime);
            createFailOrder(saleOrder, responsePayVO.getErrInfo(), StaticDataDef.SOS_REFUND_SUCCESSFUL);
            if (saleOrder != null) {
                ///?????????????????????????????????
                List<SmMerchantProductSaleItem> items = merchantProductSaleItemManager.listByMpsoId(saleOrder.getMpsoId());
                //??????????????????????????????????????????
                Double num = 0.00;
                for (int i = 0; i < items.size(); i++) {
                    num += items.get(i).getMpsiNum().doubleValue();
                }
                if (num > -0.0000001 && num < 0.000001) {
                    //???????????? ???????????????
                    saleOrder.setMpsoIsRefund(true);
                    boolean b = smMerchantProductSaleOrderManager.updateById(saleOrder);//??????????????????
                }
                //??????????????????????????????????????????????????????????????????????????????

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
                //??????????????????
                try {
                    if (items1.get(0).getMpsiIsRefundType() == 1){
                        productService.saleNewRefundProduct(ridesOrder1, items1);//????????????
                    }
                } catch (Exception e) {
                    log.error("??????????????????????????????????????????{}", saleOrder.getMpsoOrderNo(),e);
                }
                //??????????????????
                try {
                    log.info("   saleOrder   " + saleOrder);
                    Integer typeId = 1;
                    ownerService.sendMessageToOwner(saleOrder, typeId , ridesOrder.getCancelOrderData(),"2");
                } catch (Exception e) {
                    log.error("??????????????????????????????????????????{}", saleOrder.getMpsoOrderNo(), e);
                }
                //???????????????????????????
                try {
                    memberService.sendCancelMessage(saleOrder.getMpsoSingleNo(), REFUND_BY_CASH, items1);
                } catch (Exception e) {
                    log.error("??????????????????????????????????????????{}", saleOrder.getMpsoOrderNo(), e);
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean updateCannelOrder(RidesOrderCancelVO ridesOrder, ResponsePayVO responsePayVO) {
        //???????????????????????????????????????????????????????????????????????????????????????
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

            //???????????? ??????item????????????????????????item??????????????????????????????
            //????????????item?????????
            List<SmMerchantProductSaleItem> oldItemList = new ArrayList<>();
            //????????????item?????????
            List<SmMerchantProductSaleItem> cancelItemList = new ArrayList<>();

            //??????item???????????????

            saleOrder.getItems().forEach(item -> {
                if (!item.getMpsiIsRefund()) {
                    //????????????item
                    item.setMpsiIsRefund(true);
                    //???????????????
                    if (ridesOrder.getIsOnlyRefund() == null || "false".equals(ridesOrder.getIsOnlyRefund())) {
                        item.setMpsiIsRefundType(RETURN_AND_REFUND);
                    } else {
                        item.setMpsiIsRefundType(ONLY_REFUND);
                    }
                    oldItemList.add(item);

                    //????????????item
                    SmMerchantProductSaleItem cancelItem = new SmMerchantProductSaleItem();
                    BeanUtil.copyProperties(item, cancelItem);
                    cancelItem.setMpsiIsRefund(true);
                    //???????????????
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

            //??????????????????order
            boolean orderUpdateBool = smMerchantProductSaleOrderManager.updateById(saleOrder);
            //??????????????????item
            boolean itemUpdateBool = merchantProductSaleItemManager.updateBatchById(oldItemList);
            //?????????????????????????????? item
            boolean cancelItemAddBool = merchantProductSaleItemManager.saveBatch(cancelItemList);

            if (orderUpdateBool && itemUpdateBool && cancelItemAddBool) {
//                List<SmMerchantProductSaleItem> items = merchantProductSaleItemManager.listByMpsoId(saleOrder.getMpsoId());
                //????????????????????????
                if (ridesOrder.getIsOnlyRefund() == null || "false".equals(ridesOrder.getIsOnlyRefund())) {
                    //??????????????????
                    try {
                        //??????cancelItemList???????????????????????????????????????????????????????????? ????????????????????????????????????
                        cancelItemList.forEach(item -> item.setMpsiNum(item.getMpsiNum().negate()));
                        //????????????
                        productService.saleRefundProduct(ridesOrder, cancelItemList);
                    } catch (Exception e) {
                        log.error("??????????????????????????????????????????{}", saleOrder.getMpsoOrderNo(),e);
                    }
                }
                //??????????????????????????????????????????,?????????????????????????????????
                QueryWrapper<SmMerchantProductSaleItem> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("mpsi_mpso_id",saleOrder.getMpsoId());
                queryWrapper.eq("mpsi_is_delete",0);
                List<SmMerchantProductSaleItem> saleAllItems = smMerchantProductSaleItemManager.list(queryWrapper);
                Integer orderCancelId = saveProductSaleOrderCancelData(saleOrder,saleAllItems);
                //??????????????????
                try {
                    log.info("   saleOrder   " + saleOrder);
                    Integer typeId = 2;
                    ownerService.sendMessageToOwner(saleOrder, typeId,orderCancelId,ridesOrder.getRefundMethod());
                } catch (Exception e) {
                    log.error("??????????????????????????????????????????{}", saleOrder.getMpsoOrderNo(), e);
                }
                //???????????????????????????
                try {
//                    memberService.sendCancelMessage(saleOrder.getMpsoSingleNo(), ridesOrder.getRefundMethod());
                    memberService.sendCancelMessage(saleOrder.getMpsoSingleNo(), ridesOrder.getRefundMethod(),saleOrder.getItems());
                } catch (Exception e) {
                    log.error("??????????????????????????????????????????{}", saleOrder.getMpsoOrderNo(), e);
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
        //????????????
        initOrder(saleOrder, ridesOrder);
        //?????????????????????
        ArrayList<String> mpsiMpbiNos = new ArrayList<>();
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            order.getItems().forEach((item) -> {
                log.info("***************???????????????****************" + item.getMpsiMpbiNo());
                if (StringUtils.isNotBlank(item.getMpsiMpbiNo())) {
                    mpsiMpbiNos.add(item.getMpsiMpbiNo());
                }
            });
        }
        //-----------------------------------------------------------------
        List<PmMerchantProductBasicAndLibVO> productInfos = new ArrayList<>();
        log.info("****************???????????????*******************" + JSON.toJSONString(mpsiMpbiNos));
        if (!mpsiMpbiNos.isEmpty()) {
            log.info("****************???????????????*******************" + JSON.toJSONString(mpsiMpbiNos));
            productInfos = BaseResultResponse.getResponseDataNotAllowNull(apiMerchantProductManager.listByNoAndStoreId(mpsiMpbiNos, saleOrder.getMpsoSbiId()));
        }
        //????????????list
        Map<String, PmMerchantProductBasicAndLibVO> productsMap = productInfos.stream().collect(Collectors.toMap(PmMerchantProductBasicAndLibVO::getMpbiNo, temp -> temp));
        List<SmMerchantProductSaleItem> items = initOrderProduct(order, saleOrder, productsMap);
        //????????????
        log.debug("==============???????????????==================" + saleOrder);
        boolean save = smMerchantProductSaleOrderManager.save(saleOrder);
        //????????????.????????????????????????
        if (save) {
            if (!items.isEmpty()) {
                items.forEach((item) -> {
                    item.setMpsiMpsoId(saleOrder.getMpsoId());
                });
                //????????????????????????
                merchantProductSaleItemManager.saveBatch(items);
            }
            log.info("??????[{}]????????????, ??????????????????{}???", saleOrder.getMpsoId(), items.size());
        }
        return save;
    }

    @Override
    public void initOrder(SmMerchantProductSaleOrder saleOrder, RidesOrderVO ridesOrder) {
        UserWithPermissionVO userInfo = ridesOrder.getUserInfo();
        BeanUtil.copyProperties(ridesOrder.getOrder(), saleOrder);
        //????????????
        //??????id
        saleOrder.setMpsoSbiId(userInfo.getStoreId());
        saleOrder.setMpsoSbiCode(ridesOrder.getOrder().getSbiCode());
        saleOrder.setMpsoStoreName(userInfo.getStoreName());
        //?????????id
        saleOrder.setMpsoSbiProvinceId(userInfo.getSbiProvinceId());
        //?????????id
        saleOrder.setMpsoSbiCityId(userInfo.getSbiCityId());
        //?????????id
        saleOrder.setMpsoSbiAreaId(userInfo.getSbiAreaId());
        //????????????
        saleOrder.setMpsoSbiType(userInfo.getSbiChildType());

        //???????????????
        saleOrder.setMpsoUbiId(userInfo.getId());
        if (saleOrder.getMpsoCashier() == null) {
            saleOrder.setMpsoCashier(userInfo.getRealName());
        }

        //????????????
        //??????id
        saleOrder.setMpsoSdcId(userInfo.getClientId());
        //????????????
        saleOrder.setMpsoClientName(userInfo.getClientName());
        //????????????
        saleOrder.setMpsoModifyTime(LocalDateTime.now());
        //?????????????????????null,??????????????????
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
            //?????????
            BigDecimal costAccount = BigDecimal.ZERO;
            log.info("************????????????????????????***************" + order);
            for (SmMerchantProductSaleItemVO item : order.getItems()) {
                //--??????-----------
                SmMerchantProductSaleItem oldItem = new SmMerchantProductSaleItem();
                //????????????
                BigDecimal offset = item.getMpsiNum();
                BeanUtil.copyProperties(item, oldItem);
                log.info("************???????????????????????????***********" + oldItem);
                if (StringUtils.isNotBlank(oldItem.getMpsiBarCodes()) && !"null".equals(oldItem.getMpsiBarCodes()) && "LS".equals(oldItem.getMpsiBarCodes().substring(0, 2))) {
                    //?????????????????????
                    oldItem.setMpsiIsTemp(true);
                    //??????id
                    oldItem.setMpsiMpbiId(onlyNum(saleOrder.getMpsoSbiId()));
                    //????????????id
                    oldItem.setMpsiPislId(onlyNum(saleOrder.getMpsoSbiId()));
                    //??????id ????????? 9999
                    oldItem.setMpsiPciId(9999);
                    //????????????id
                    oldItem.setMpsiFirstPciId(9999);
                    //??????????????????
                    oldItem.setMpsiFirstPciName("?????????");
                    //????????????
                    oldItem.setMpsiBarCodes("");
                } else {
                    PmMerchantProductBasicAndLibVO productBasicInfoVO = productsMap.get(item.getMpsiMpbiNo());
                    if (productBasicInfoVO == null) {
                        log.warn("??????[{}]????????????,??????????????????{}????????????id????????????,???????????????????????????", saleOrder.getMpsoSbiId(), item.getMpsiMpbiNo());
                        continue;
                    }
                    //?????????????????????
                    oldItem.setMpsiIsTemp(false);
                    //??????id
                    oldItem.setMpsiMpbiId(productBasicInfoVO.getMpbiId());
                    //????????????id
                    oldItem.setMpsiPislId(productBasicInfoVO.getPislId());
                    //????????????
                    oldItem.setMpsiModifyTime(LocalDateTime.now());
                    //??????
                    BigDecimal mpbiCostPrice = productBasicInfoVO.getMpbiCostPrice();
                    if (mpbiCostPrice == null || BigDecimal.ZERO.compareTo(mpbiCostPrice) == 0) {
                        mpbiCostPrice = BigDecimal.ZERO.compareTo(productBasicInfoVO.getPislSuggestedCostPrice()) == 0 ? productBasicInfoVO.getMpbiRetailPrice().multiply(BigDecimal.valueOf(0.8)) : productBasicInfoVO.getPislSuggestedCostPrice();
                    }
                    oldItem.setMpsiWholesalePrice(mpbiCostPrice);
                    //??????id
                    oldItem.setMpsiPciId(productBasicInfoVO.getPislCategory());
                    //????????????id
                    oldItem.setMpsiFirstPciId(getCategoryByLevel(1, productBasicInfoVO.getPciParentIds()));
                    //??????????????????
                    oldItem.setMpsiFirstPciName(getCategoryNameByLevel(1, productBasicInfoVO.getPciParentNames()));
                    //????????????id
                    oldItem.setMpsiSecondPciId(getCategoryByLevel(2, productBasicInfoVO.getPciParentIds()));
                    //??????????????????
                    oldItem.setMpsiSecondPciName(getCategoryNameByLevel(2, productBasicInfoVO.getPciParentNames()));
                    // ??????code
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
                //???????????????????????????
                log.debug("????????????-{},??????{}", oldItem, offset);
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
        //???????????????
//        saleOrder.setMpsoPayOrderId(responsePayVO.getThreeOrderNo());
        saleOrder.setMpsoPayOrderId(responsePayVO.getTradeNo());
        //????????????ID
        if (responsePayVO.getOpenid() != null && !"null".equals(responsePayVO.getOpenid())) {
            saleOrder.setMpsoThirdBuyerId(responsePayVO.getOpenid());
        }
        //TODO ????????????????????????????????????????????????????????????????????????
        //??????????????????
//        if (thirdBuyerName != null && !"null".equals(thirdBuyerName)) {
//            saleOrder.setMpsoThirdBuyerName(thirdBuyerName);
//        }
        if ("null".equals(saleOrder.getMpsoSmiPhone())) {
            saleOrder.setMpsoSmiPhone(null);
        }
        //????????????
        BigDecimal payAmount = MoneyUtil.fenToYuan(responsePayVO.getAmount());
        saleOrder.setMpsoActualPayPrice(payAmount);
        PayStatusCode payStatus = PayStatusCode.queryByCode(responsePayVO.getPayStatus());
        switch (payStatus) {
            //4.1?????????????????????????????????????????????Rides,????????????...
            case SUCCESS:
                saleOrder.setMpsoIsPayment(true);
                //????????????
                saleOrder.setMpsoOrderStatus(StaticDataDef.SOS_SUCCESSFUL_PAYMENT.getSddId());
                log.debug("??????????????????,?????????:{}", saleOrder.getMpsoOrderNo());
                break;
            //4.2?????????????????????Rides???
            case NEED_PASSWORD:
                saleOrder.setMpsoIsPayment(false);
                saleOrder.setMpsoOrderStatus(StaticDataDef.SOS_WAITING_PAYMENT.getSddId());
                log.debug("???????????????,????????????????????????,?????????:{}", saleOrder.getMpsoOrderNo());
                break;
            //4.3?????????????????????????????????????????????Rides
            case FAIL:
            case BACK_OUT:
                log.debug("??????????????????,?????????:{},??????:{}", saleOrder.getMpsoOrderNo(), responsePayVO.getErrInfo());
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
     * ????????????
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
        // ????????????===========================
        if (user == null) {
            throw new NotExceptException("????????????????????????");
        }
        if (items == null || items.size() <= 0) {
            throw new NotExceptException("????????????????????????!");
        }
        //  ??????: =================================================
        //  1.??????????????????
        //  2.??????????????????
        //  3.??????(????????????????????????)
        //  4.??????????????????
        //=====================1.??????????????????  rm_in_store_record_order ????????????===========================
        LocalDateTime localDateTime = LocalDateTime.now();
        RmInStoreRecordOrder InStoreOrderVo = new RmInStoreRecordOrder();
        ArrayList<String> mpbiNoList = new ArrayList<>();  // ??????mpbiNo???list  ?????????????????? = null

        //  ????????????????????????????????????????????????
        for (PutInStoreItemVO item : items) {
            mpbiNoList.add(item.getProductCode());
        }
        BaseResultResponse<List<PmMerchantProductBasicAndLibVO>> ProductList = apiMerchantProductManager.listByNoAndStoreId(mpbiNoList, user.getStoreId());

        //  ?????? key:??????code value:PmMerchantProductBasicAndLibVO ???map
        Map<String, PmMerchantProductBasicAndLibVO> productMap = getProductMap(items, ProductList);
        //??????????????????????????????
        createRminStoreOrder(InStoreOrderVo, localDateTime, user, items, productMap);
//        productMap.clear(); //?????? map

        //  ?????????????????????????????? ?????????id
        rmInStoreRecordOrderManager.addRmInStoreRecordBackId(InStoreOrderVo);
        int backId = InStoreOrderVo.getIsroId();
        System.out.println("*******************backId:" + backId);
        //====================================1.?????????????????? rm_in_store_record_order ???????????? End========================================


        //=====================================2.??????????????????   rm_in_store_record_item==================================================
        List<RmInStoreRecordItem> recordItemList = new ArrayList<>();
        List<ProductItem> productItemList = new ArrayList<>();
        // ????????????????????????   ??????list??? ????????????
        createInStoreRecord(backId, productMap, items, localDateTime, recordItemList, productItemList);
        rmInStoreRecordItemManager.saveBatch(recordItemList);
        //=====================================2.?????????????????? End==================================================


        //=====================================3.??????(????????????????????????)  pm_merchant_product_basic_info====================================
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
        //???????????? ?????? ?????????????????????
        ApiMapReMsg apiMapReMsg = productV2Manager.stockModify(productChange, user.getStoreId() + "", serialNum, userNo);

        ProductChange proChange = objectMapper.convertValue(apiMapReMsg.getData(), ProductChange.class);
        if ("true".equals(apiMapReMsg.getSuccess())) {
            System.out.println("-----productV2???????????????-------" + proChange);
            List<ProductItem> productItemsList = proChange.getProductItems();

            // ??????????????????mpbiNO????????????mpbiNO??????
            // ???????????????????????????????????? retun
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
//     * ???????????? for?????????????????????
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
//        // ????????????===========================
//        if (items == null || items.size() <= 0) {
//            throw new NotExceptException("????????????????????????!");
//        }
//        if (storeId == null) {
//            throw new NotExceptException("??????id????????????!");
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
//            throw new NotExceptException("????????????????????????!");
//        }
//        //  ??????: =================================================
//        //  1.??????????????????
//        //  2.??????????????????
//        //  3.??????(????????????????????????)
//        //  4.??????????????????
//        //=====================1.??????????????????  rm_in_store_record_order ????????????===========================
//        LocalDateTime localDateTime = LocalDateTime.now();
//        RmInStoreRecordOrder InStoreOrderVo = new RmInStoreRecordOrder();
//        List<String> mpbiNoList = new ArrayList<>();  // ??????mpbiNo???list  ?????????????????? = null
//
//        //  ????????????????????????????????????????????????
//        for (PutInStoreItemVO item : items) {
//            mpbiNoList.add(item.getProductCode());
//        }
//        BaseResultResponse<List<PmMerchantProductBasicAndLibVO>> ProductList = apiMerchantProductManager.listByNoAndStoreId(mpbiNoList, Integer.parseInt(storeId));
//
//        //  ?????? key:??????code value:PmMerchantProductBasicAndLibVO ???map
//        Map<String, PmMerchantProductBasicAndLibVO> productMap = getProductMap(items, ProductList);
//        //??????????????????????????????
//        createRminStoreOrderForPhone(InStoreOrderVo, localDateTime, storeId, ubiJobNumber, items, productMap, ubiRealName, ubiStoreName,sbiCode,ubiId);
////        productMap.clear(); //?????? map
//
//        //  ?????????????????????????????? ?????????id
//        rmInStoreRecordOrderManager.addRmInStoreRecordBackId(InStoreOrderVo);
//        int backId = InStoreOrderVo.getIsroId();
//        System.out.println("*******************backId:" + backId);
//        //====================================1.?????????????????? rm_in_store_record_order ???????????? End========================================
//
//
//        //=====================================2.??????????????????   rm_in_store_record_item==================================================
//        List<RmInStoreRecordItem> recordItemList = new ArrayList<>();
//        List<ProductItem> productItemList = new ArrayList<>();
//        // ????????????????????????   ??????list??? ????????????
//        createInStoreRecord(backId, productMap, items, localDateTime, recordItemList, productItemList);
//        rmInStoreRecordItemManager.saveBatch(recordItemList);
//        //=====================================2.?????????????????? End==================================================
//
//
//        //=====================================3.??????(????????????????????????)  pm_merchant_product_basic_info====================================
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
//        //???????????? ?????? ?????????????????????   ???????????????????????????  ?????????
//        ApiMapReMsg apiMapReMsg = productV2Manager.stockModify(productChange, storeId, "", userNo);
//
//        ProductChange proChange = objectMapper.convertValue(apiMapReMsg.getData(), ProductChange.class);
//        if ("true".equals(apiMapReMsg.getSuccess())) {
//            System.out.println("-----productV2???????????????-------" + proChange);
//            List<ProductItem> productItemsList = proChange.getProductItems();
//
//            // ??????????????????mpbiNO????????????mpbiNO??????
//            // ???????????????????????????????????? retun
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
     * ??????????????????
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

        //??????????????????
        PmPurchaseOrderInfo order = pmPurchaseOrderInfoManager.findOrderById(orderIdLong, false, true);
        //????????????????????????
        if (order == null) {
            throw new NotExceptException("?????????????????????,????????????????????????");
        }
        List<PmPurchaseItemInfo> currInfoList = purchaseItemInfoManager.findListByPoiId(order.getPoiId());
        if (currInfoList.isEmpty()) {
            throw new NotExceptException("????????????????????????");
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
        // ????????????

        //  ??????mpbiid ??????list ???product?????????????????? ??????mpbiNo
        List<PutInStoreItemVO> putInList = new ArrayList<>();
        List<Long> pislIdList = new ArrayList<>();
        for (PmPurchaseItemInfo pmPurchaseItemInfo : infoList) {
            pislIdList.add(pmPurchaseItemInfo.getPiiMpbiId());
        }
        List<PmMerchantProductBasicInfoVO> productList = new ArrayList<>();
        if (!pislIdList.isEmpty()) {
            productList = BaseResultResponse.getResponseDataNotAllowNull(apiMerchantProductManager.getListByPislIdAndSbiId(pislIdList, user.getStoreId()));
        }
        log.info("productList?????????{}", productList.size());
        Map<Long, PmMerchantProductBasicInfoVO> productsMap = new HashMap<>();
        for (PmMerchantProductBasicInfoVO merchantProductBasicInfoVO : productList) {
            productsMap.put(merchantProductBasicInfoVO.getMpbiPislId(), merchantProductBasicInfoVO);
        }
//                productList.stream().collect(Collectors.toMap(PmMerchantProductBasicInfoVO::getMpbiPislId, temp -> temp));
        List<ProductItem> newProduct = new ArrayList<>();
        List<Long> newProductIds = new ArrayList<>();
        //???????????????????????????
        for (PmPurchaseItemInfo pmPurchaseItemInfo : infoList) {
            PmMerchantProductBasicInfoVO pmMerchantProductBasicInfoVO = productsMap.get(pmPurchaseItemInfo.getPiiMpbiId());
            log.info("??????????????????{}", pmMerchantProductBasicInfoVO);
            if (pmMerchantProductBasicInfoVO == null) {
                if (newProductIds.contains(pmPurchaseItemInfo.getPiiMpbiId())) {
                    continue;
                }
                log.info("newProduct??????");
                ProductItem newProductItem = new ProductItem();
                StockProductInfoLibDTO libDTO = new StockProductInfoLibDTO();
                ProductInfoStandardLibVO pmProductInfoStandardLibVO =null;
                try {
                    pmProductInfoStandardLibVO = BaseResultResponse.getResponseDataNotAllowNull(
                        apiMerchantProductManager.getPmProductInfoStandardLibEById(pmPurchaseItemInfo.getPiiMpbiId()));
                }catch (NotExceptException e){
                    log.error("??????yld-product-info?????????ID???{}????????????????????????",pmPurchaseItemInfo.getPiiMpbiId(),e);
                    throw new NotExceptException("????????????ID???"+pmPurchaseItemInfo.getPiiMpbiId()+"?????????????????????????????????????????????????????????");
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
        log.info("newProduct????????????{}", newProduct.size());
        //????????????????????????
        if (newProduct.size() > 0) {
            log.info("??????????????????");
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
                    log.error("???????????????????????????????????????id{},??????pislId{}", user.getStoreId(), currItem.getMpbiPislId());
                    throw new NotExceptException("????????????");
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
        throw new NotExceptException("????????????");

    }


    /**
     * ??????key:??????code value:???????????????map
     *
     * @param items
     * @return
     */
    public Map<String, PmMerchantProductBasicAndLibVO> getProductMap(List<PutInStoreItemVO> items, BaseResultResponse<List<PmMerchantProductBasicAndLibVO>> ProductList) {


        List<PmMerchantProductBasicAndLibVO> productListData = ProductList.getData(); // ????????????

        Map<String, PmMerchantProductBasicAndLibVO> productMap = new HashMap<>();  // key:??????code value:????????????    ????????????????????????
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
     * ??????????????????
     *
     * @param InStoreOrderVo
     * @param localDateTime
     * @param user
     * @param items
     * @return
     */
    public void createRminStoreOrder(RmInStoreRecordOrder InStoreOrderVo, LocalDateTime localDateTime, UserWithPermissionVO user, List<PutInStoreItemVO> items, Map<String, PmMerchantProductBasicAndLibVO> productMap) {


        // ==============??????????????????=======
        //????????????
        BigDecimal totalPrice = BigDecimal.ZERO;
        //???????????????
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
//        productMap.clear(); //?????? map

        InStoreOrderVo.setIsroCode(IdUtil.fastSimpleUUID());
        InStoreOrderVo.setIsroCreateTime(localDateTime);
        InStoreOrderVo.setIsroInType("1f011c98bb614c738e758b87bd53fffc"); //  ????????????
        InStoreOrderVo.setIsroIsDelete(false);
        InStoreOrderVo.setIsroModifyTime(localDateTime);
        InStoreOrderVo.setIsroNo(TimeUtil.timeId());
        InStoreOrderVo.setIsroOperUserId(user.getId());
        InStoreOrderVo.setIsroOperUserName(user.getRealName());
        InStoreOrderVo.setIsroOperUserNum(user.getJobNum());
        InStoreOrderVo.setIsroRealPrice(totalPrice);  //????????????
        InStoreOrderVo.setIsroRowNum(items.size());
        InStoreOrderVo.setIsroSbiCode(user.getStoreNo());
        InStoreOrderVo.setIsroSbiFullName(user.getStoreName());
        InStoreOrderVo.setIsroSbiId(user.getStoreId());
        InStoreOrderVo.setIsroSdcClientName(user.getClientName());
        InStoreOrderVo.setIsroSdcId(user.getClientId());
        InStoreOrderVo.setIsroSort(1); //????????????  ????????????????????????null  ??????1
        InStoreOrderVo.setIsroTerminalType("2aa55c8a279546688f65f4ceee19bca4"); //?????????
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
//            recordItem.setIsriCarCodes() ????????????
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
            recordItem.setIsriWarehouseNumber(inRecorditem.getNumber()); //????????????
            recordItem.setIsriInventoryNumber(productItem.getMpbiStockNumber().add(inRecorditem.getNumber())); //????????????
            if (recordItem.getIsriCostPrice() == null) {
                recordItem.setIsriCostPrice(new BigDecimal(0.0));
                log.info("??????????????????null??????????????????0,???????????????recordItem:{}", JSON.toJSONString(recordItem));
            }
            recordItem.setIsriAccount(inRecorditem.getNumber().multiply(recordItem.getIsriCostPrice()));
            recordItem.setIsriCreateTime(localDateTime);
            recordItem.setIsriModifyTime(localDateTime);
            recordItem.setIsriIsDelete(false);
            recordItem.setIsriSort(1); // ?????? ?????????1
            recordItemList.add(recordItem);
            mProductItem.setMpbiId(productItem.getMpbiId());
            mProductItem.setMpbiNo(inRecorditem.getProductCode());
            mProductItem.setChangeStokNum(inRecorditem.getNumber());
            productItemList.add(mProductItem);
        }


    }


    private Integer saveProductSaleOrderCancelData(SmMerchantProductSaleOrder oldOrder, List<SmMerchantProductSaleItem> saleAllItems) {

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
     * ??????????????????
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
//        // ==============??????????????????=======
//        //????????????
//        BigDecimal totalPrice = BigDecimal.ZERO;
//        //???????????????
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
////        productMap.clear(); //?????? map
//
//        InStoreOrderVo.setIsroCode(IdUtil.fastSimpleUUID());
//        InStoreOrderVo.setIsroCreateTime(localDateTime);
//        InStoreOrderVo.setIsroInType("1f011c98bb614c738e758b87bd53fffc"); //  ????????????
//        InStoreOrderVo.setIsroIsDelete(false);
//        InStoreOrderVo.setIsroModifyTime(localDateTime);
//        InStoreOrderVo.setIsroNo(TimeUtil.timeId());
//        InStoreOrderVo.setIsroOperUserId(Long.valueOf(ubiId));
//        InStoreOrderVo.setIsroOperUserName(ubiRealName);
//        InStoreOrderVo.setIsroOperUserNum(ubiJobNumber);
//        InStoreOrderVo.setIsroRealPrice(totalPrice);  //????????????
//        InStoreOrderVo.setIsroRowNum(items.size());
//        InStoreOrderVo.setIsroSbiCode(sbiCode);
//        InStoreOrderVo.setIsroSbiFullName(ubiStoreName);
//        InStoreOrderVo.setIsroSbiId(Integer.valueOf(storeId));
////        InStoreOrderVo.setIsroSdcClientName(user.getClientName());
////        InStoreOrderVo.setIsroSdcId(user.getClientId());
//        InStoreOrderVo.setIsroSort(1); //????????????  ????????????????????????null  ??????1
//        InStoreOrderVo.setIsroTerminalType("a7c9f61b438946699fd0a53eeb885555"); //?????????
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
