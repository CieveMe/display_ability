package cn.com.yyxx.yld.supply.service.sm.impl;

import static cn.com.yyxx.yld.supply.messaging.pay.OrderSaveSource.YLD_ORDER_SAVE_OUT_PUT;

import cn.com.yyxx.yld.supply.action.BaseResultResponse;
import cn.com.yyxx.yld.supply.config.SmwConfig;
import cn.com.yyxx.yld.supply.config.YqUrlConfig;
import cn.com.yyxx.yld.supply.data.vo.BasicInfoIdAndNoVO;
import cn.com.yyxx.yld.supply.data.vo.OrderCallBackVO;
import cn.com.yyxx.yld.supply.data.vo.OwnerOrderVo;
import cn.com.yyxx.yld.supply.data.vo.RidesOrderVO;
import cn.com.yyxx.yld.supply.data.vo.SmMerchantProductSaleItemVO;
import cn.com.yyxx.yld.supply.data.vo.SmMerchantProductSaleOrderVO;
import cn.com.yyxx.yld.supply.data.vo.UserWithPermissionVO;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleOrder;
import cn.com.yyxx.yld.supply.exception.NotExceptException;
import cn.com.yyxx.yld.supply.manager.ISmMerchantProductSaleItemManager;
import cn.com.yyxx.yld.supply.manager.ISmMerchantProductSaleOrderManager;
import cn.com.yyxx.yld.supply.manager.product.IApiMerchantProductManager;
import cn.com.yyxx.yld.supply.manager.user.IApiUserGrantInfoManager;
import cn.com.yyxx.yld.supply.redis.RedisUtil;
import cn.com.yyxx.yld.supply.service.sm.IStoreUploadMerchantProductSaleOrderService;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

/**
 * <p>
 *
 * </p>
 *
 * @author FYX
 * @version 0.16.0
 * @date 2020/12/09 09:56
 **/
@Service
//@Transactional(rollbackFor = Exception.class)
public class StoreUploadMerchantProductSaleOrderServiceImpl implements IStoreUploadMerchantProductSaleOrderService {

    private Logger log = LoggerFactory.getLogger(StoreUploadMerchantProductSaleOrderServiceImpl.class);

    private BigDecimal odrDiscountAssign(BigDecimal actualPrice, BigDecimal shouldPrice){
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
    public OwnerOrderVo queryOwnerOrderVo(Integer mpsoSbiId) {
        return iSmMerchantProductSaleOrderManager.queryOwnerOrder(mpsoSbiId);
    }


    @Override
    public SmMerchantProductSaleOrderVO getOrderByNo(String orderNo, UserWithPermissionVO user) {
        if (ObjectUtil.isNull(user.getStoreId())) {
            throw new NotExceptException("无法获取门店ID");
        }
        if (StrUtil.isEmpty(orderNo)) {
            throw new NotExceptException("无法获取订单编号");
        }
        Integer storeId = user.getStoreId();
        SmMerchantProductSaleOrderVO orderByNo = manager.getOrderByNo(storeId, orderNo);
        List<SmMerchantProductSaleItemVO> items = orderByNo.getItems();
        List<Long> collect = items.stream().map(SmMerchantProductSaleItemVO::getMpsiMpbiId)
            .collect(Collectors.toList());
        List<BasicInfoIdAndNoVO> response = BaseResultResponse
            .getResponseDataNotAllowNull(basicInfoManager.getListByMpbiIdAndSbiId(collect, storeId));
        if (collect.size() != response.size()) {
            throw new NotExceptException("查询商品编号和商品数量不符");
        }
        Map<Long, BasicInfoIdAndNoVO> map = response.stream()
            .collect(Collectors.toMap(BasicInfoIdAndNoVO::getMpbiId, Function.identity()));
        items.forEach(b -> {
            BasicInfoIdAndNoVO vo = map.get(b.getMpsiMpbiId());
            b.setMpsiMpbiNo(vo.getMpbiNo());
            b.setMpsiMpsoNo(orderByNo.getMpsoOrderNo());
        });

        return orderByNo;
    }

    @Override
    public OrderCallBackVO saveOrder(SmMerchantProductSaleOrderVO order, UserWithPermissionVO userInfo,
        String serialNum, String userNo, String termIp,String hsm) {

        log.info("C扫B上传订单---->{}", order);
        OrderCallBackVO backVO = new OrderCallBackVO();
        SmMerchantProductSaleOrder one = manager.getOne(new QueryWrapper<SmMerchantProductSaleOrder>()
            .eq(SmMerchantProductSaleOrder.MPSO_SBI_ID, userInfo.getStoreId())
            .eq(SmMerchantProductSaleOrder.MPSO_ORDER_NO, order.getMpsoOrderNo())
            .eq(SmMerchantProductSaleOrder.MPSO_IS_DELETE, false));
        if (!ObjectUtil.isNull(one)) {
            backVO.setMessage("该单号已存在一笔订单");
            backVO.setStatus(false);
            return backVO;
        }
        //1、Rides取值
        Object ridesOrderObject = redisUtil.get(order.getMpsoOrderNo());
        if (ridesOrderObject != null) {
            backVO.setMessage("该单号已存在一笔订单");
            backVO.setStatus(false);
            return backVO;
        }
        //组装异步所需VO
        RidesOrderVO ridesOrderVO = new RidesOrderVO(order, userInfo, serialNum, userNo, termIp, "scanC_B");
        //订单中discount重新赋值，赋的值记录整单中所有折扣
        order.setMpsoDiscount(odrDiscountAssign(order.getMpsoActualPrice(),order.getMpsoShouldPrice()));
        //2、存储Rides 暂时不设置超时时间，丢单后可以补回
        boolean redisStatus = redisUtil.set(order.getMpsoOrderNo(), JSONObject.toJSONString(ridesOrderVO));
        if (!redisStatus) {
            log.error("C扫B订单存储失败,订单号:{},订单:{}", order.getMpsoOrderNo(), order.toString());
            throw new NotExceptException("订单存储失败!");
        }
        //3、开始异步保存订单
        boolean mqstatus = orderSaveOutPut.send(MessageBuilder.withPayload(order.getMpsoOrderNo()).build());
        if(StringUtils.isNotBlank(hsm)){
            //如果是好柿民的标志，返回好柿民的支付地址给二维码生成
            backVO.setUrl(smwConfig.getUrl()+order.getMpsoOrderNo());
            log.info("好柿民扫码回调请求地址： {}",backVO.getUrl());
        }else{
            backVO.setUrl(yqUrlConfig.getUrl() + order.getMpsoOrderNo());
        }
        backVO.setStatus(true);
        return backVO;
//        //订单操作
//        SmMerchantProductSaleOrder saleOrder = new SmMerchantProductSaleOrder();
//        BeanUtil.copyProperties(vo, saleOrder);
////        List<String> ubiNos = new ArrayList<>();
////        ubiNos.add(vo.getMpsoUbiNo());
////        List<SimpleUserInfoVO> oldUserData = BaseResultResponse.getResponseDataNotAllowNull(apiUserInfoManager.listByUserNoAndStore(ubiNos, userInfo.getStoreId()));
////        if (ObjectUtil.isNull(oldUserData)){
////           backVO.setMessage("获取用户详情信息为空");
////           backVO.setStatus(false);
////           return backVO;
////        }
//
//        //门店id
//        saleOrder.setMpsoSbiId(userInfo.getStoreId());
//        //门店名称
//        saleOrder.setMpsoStoreName(userInfo.getStoreName());
//        //门店省id
//        saleOrder.setMpsoSbiProvinceId(userInfo.getSbiProvinceId());
//        //门店市id
//        saleOrder.setMpsoSbiCityId(userInfo.getSbiCityId());
//        //门店县id
//        saleOrder.setMpsoSbiAreaId(userInfo.getSbiAreaId());
//        //门店类型
//        saleOrder.setMpsoSbiType(userInfo.getSbiChildType());
//        //收银员id
//        saleOrder.setMpsoUbiId(userInfo.getId());
//        //如果应收金额为null,赋为实际金额
//        if (saleOrder.getMpsoTotalPrice() == null) {
//            saleOrder.setMpsoTotalPrice(saleOrder.getMpsoRealPrice());
//        }
//        //设备id
//        saleOrder.setMpsoSdcId(userInfo.getClientId());
//        //门店编号
//        saleOrder.setMpsoSbiCode(userInfo.getStoreNo());
//        saleOrder.setMpsoCashier(userInfo.getRealName());
//        saleOrder.setMpsoCashierNo(userInfo.getJobNo().toString());
//        saleOrder.setMpsoIsDelete(false);
//        saleOrder.setMpsoIsRefund(false);
//        LocalDateTime localDateTime = LocalDateTime.now();
//        saleOrder.setMpsoOrderTime(localDateTime);
//        saleOrder.setMpsoPayTime(localDateTime);
//        saleOrder.setMpsoCreateTime(localDateTime);
//        saleOrder.setMpsoModifyTime(localDateTime);
//        saleOrder.setMpsoSort(1);
//        //需要把收银端的IP保存到这里
////        saleOrder.setMpsoRemark(termIp);
//        saleOrder.setTermIp(termIp);
//        saleOrder.setScanType("scanC_B");
//        //订单品细操作
//        List<SmMerchantProductSaleItemVO> items = vo.getItems();
//        if (ObjectUtil.isNull(items)) {
//            backVO.setMessage("订单没有品细项!");
//            backVO.setStatus(false);
//            return backVO;
//        }
//        List<String> collect = items.stream().map(SmMerchantProductSaleItemVO::getMpsiMpbiNo).collect(Collectors.toList());
//        List<PmMerchantProductBasicAndLibVO> productInfos = BaseResultResponse.getResponseDataNotAllowNull(basicInfoManager.listByNoAndStoreId(collect, userInfo.getStoreId()));
//        Map<String, PmMerchantProductBasicAndLibVO> productsMap = listToMap(productInfos, PmMerchantProductBasicAndLibVO::getMpbiNo);
//
//
//        BigDecimal costAccount = BigDecimal.ZERO;
//        List<SmMerchantProductSaleItem> items1 = new ArrayList<>();
//        for (SmMerchantProductSaleItemVO a : items) {
//            SmMerchantProductSaleItem item = new SmMerchantProductSaleItem();
//            BeanUtil.copyProperties(a, item);
//            PmMerchantProductBasicAndLibVO productBasicInfoVO = productsMap.get(a.getMpsiMpbiNo());
//            if (productBasicInfoVO == null) {
//                log.warn("门店[{}]同步订单,根据商品编号{}查询商品id发生错误,没有查询到商品信息", userInfo.getStoreId(), a.getMpsiMpbiNo());
//                backVO.setMessage("门店[" + userInfo.getStoreId() + "]同步订单,根据商品编号{" + a.getMpsiMpbiNo() + "}查询商品id发生错误,没有查询到商品信息!");
//                backVO.setStatus(false);
//                return backVO;
//            }
//            item.setMpsiIsDelete(false);
//            item.setMpsiIsRefund(false);
//            //商品id
//            item.setMpsiMpbiId(productBasicInfoVO.getMpbiId());
//
//            //标准商品id
//            item.setMpsiPislId(productBasicInfoVO.getPislId());
//            //修改时间
//            item.setMpsiCreateTime(localDateTime);
//            item.setMpsiModifyTime(localDateTime);
//            //进价
//            BigDecimal mpbiCostPrice = productBasicInfoVO.getMpbiCostPrice();
//            if (mpbiCostPrice == null || BigDecimal.ZERO.compareTo(mpbiCostPrice) == 0) {
//                mpbiCostPrice = BigDecimal.ZERO.compareTo(productBasicInfoVO.getPislSuggestedCostPrice()) == 0 ? productBasicInfoVO.getMpbiRetailPrice().multiply(BigDecimal.valueOf(0.8)) : productBasicInfoVO.getPislSuggestedCostPrice();
//            }
//            item.setMpsiWholesalePrice(mpbiCostPrice);
//            //分类id
//            item.setMpsiPciId(productBasicInfoVO.getPislCategory());
//            //一级分类id
//            item.setMpsiFirstPciId(getCategoryByLevel(1, productBasicInfoVO.getPciParentIds()));
//            //一级分类名称
//            item.setMpsiFirstPciName(getCategoryNameByLevel(1, productBasicInfoVO.getPciParentNames()));
//            //二级分类id
//            item.setMpsiSecondPciId(getCategoryByLevel(2, productBasicInfoVO.getPciParentIds()));
//            //二级分类名称
//            item.setMpsiSecondPciName(getCategoryNameByLevel(2, productBasicInfoVO.getPciParentNames()));
//            item.setMpsiSort(1);
//            costAccount = costAccount.add(item.getMpsiWholesalePrice().multiply(item.getMpsiNum()));
//            items1.add(item);
//        }
//
//        saleOrder.setMpsoGrossProfit(saleOrder.getMpsoActualPrice().subtract(costAccount));
//
//        boolean save = manager.save(saleOrder);
//        if (!save) {
//            backVO.setMessage("保存订单失败请核实数据!");
//            backVO.setStatus(false);
//            return backVO;
//        }
//
//        //插入完成后返回主键ID
//        items1.forEach(b -> {
//            b.setMpsiMpsoId(saleOrder.getMpsoId());
//        });
//        boolean b = itemManager.saveBatch(items1);
//        if (!b) {
//            backVO.setMessage("保存订单品细失败请核实数据!");
//            backVO.setStatus(false);
//            return backVO;
//        }
//
//        backVO.setUrl(yqUrlConfig.getUrl() + saleOrder.getMpsoOrderNo());
//        backVO.setStatus(true);
//        return backVO;
    }

    @Override
    public Boolean deleteOrder(String orderNo, UserWithPermissionVO userInfo) {

        Integer storeId = userInfo.getStoreId();
        if (ObjectUtil.isNull(storeId)) {
            throw new NotExceptException("无法获取门店ID!");
        }
        Object ridesOrderObject = redisUtil.get(orderNo);
        if (ridesOrderObject != null) {
            redisUtil.del(orderNo);
        }
        SmMerchantProductSaleOrder one = manager
            .getOne(new QueryWrapper<SmMerchantProductSaleOrder>().eq(SmMerchantProductSaleOrder.MPSO_SBI_ID, storeId)
                .eq(SmMerchantProductSaleOrder.MPSO_ORDER_NO, orderNo));

        if (ObjectUtil.isNull(one)) {
            log.warn("订单不存在");
            return true;
        }
        if (one.getMpsoIsDelete()) {
            return true;
        }
        one.setMpsoIsDelete(true);
        boolean aBoolean = manager.updateById(one);
        return aBoolean;
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

    @Autowired
    private ISmMerchantProductSaleOrderManager iSmMerchantProductSaleOrderManager;
    @Autowired
    private IApiMerchantProductManager basicInfoManager;
    @Autowired
    private ISmMerchantProductSaleOrderManager manager;
    @Autowired
    private IApiUserGrantInfoManager apiUserInfoManager;
    @Autowired
    private ISmMerchantProductSaleItemManager itemManager;
    @Autowired
    private YqUrlConfig yqUrlConfig;
    @Autowired
    private SmwConfig smwConfig;
    @Resource
    private RedisUtil redisUtil;
    @Autowired
    @Qualifier(YLD_ORDER_SAVE_OUT_PUT)
    private MessageChannel orderSaveOutPut;
}
