package cn.com.yyxx.yld.supply.service.impl;

import cn.com.yyxx.model.core.BaseResultResponse;
import cn.com.yyxx.yld.oim.data.sm.vo.SmStoreBasicInfoVO;
import cn.com.yyxx.yld.oim.manager.second.api.http.IHttpPushMessageManager;
import cn.com.yyxx.yld.supply.config.SmwConfig;
import cn.com.yyxx.yld.supply.data.dto.AddPointDTO;
import cn.com.yyxx.yld.supply.data.dto.AddPointItemDTO;
import cn.com.yyxx.yld.supply.data.vo.ResponsePayVO;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleItem;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleOrder;
import cn.com.yyxx.yld.supply.service.IMemberService;
import cn.com.yyxx.yld.supply.util.ConvertUtil;
import cn.com.yyxx.yld.supply.util.PayModeEnum;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static cn.com.yyxx.yld.supply.messaging.AddPointSource.YLD_ADD_POINT_OUT_PUT;

/**
 * <p>
 *     用户积分处理
 * </p>
 *
 * @author hz
 * @version 0.17.0
 * @date 2021/01/13 21:26
 **/
@Service
public class MemberServiceImpl implements IMemberService {

    private Logger log = LoggerFactory.getLogger(MemberServiceImpl.class);

    @Override
    public void sendCashOrderSuccessMessage(SmMerchantProductSaleOrder saleOrder,
                                            List<SmMerchantProductSaleItem> productSaleItems) {

        if (saleOrder != null && (StringUtils.isNotBlank(saleOrder.getMpsoThirdBuyerId())||saleOrder.getMpsoSmiPhone()!=null)) {
            saleOrder.setItems(productSaleItems);
            saleOrder.setMpsoPayOrderId(null);
            log.debug("订单:{},开始发送异步积分消息，openid:{}", saleOrder.getMpsoOrderNo(), saleOrder.getMpsoThirdBuyerId());
            log.info("supplyv2发送到member消费 同步订单到市民网的内容是"+saleOrder);
            //这里发送到mq,由member消费
            boolean result = memberOutput.send(MessageBuilder.withPayload(createAddPointDTO(saleOrder)).build());
            if (result) {
                log.info("订单{}，发送异步积分消息成功!", saleOrder.getMpsoOrderNo());
            } else {
                log.info("发送异步积分消息失败!");
            }
        } else {
            log.warn("发送异步积分消息失败,订单{}没有openId或会员手机号!", saleOrder.getMpsoOrderNo());
            log.info("三方卖家ID为{}", saleOrder.getMpsoThirdBuyerId());
            log.info("会员电话为{}", saleOrder.getMpsoSmiPhone());
        }

    }

    @Override
    public void sendOrderSuccessMessage(SmMerchantProductSaleOrder saleOrder,
                                        List<SmMerchantProductSaleItem> productSaleItems, ResponsePayVO responsePayVO) {

        if (saleOrder != null && (StringUtils.isNotBlank(saleOrder.getMpsoThirdBuyerId())||saleOrder.getMpsoSmiPhone()!=null)) {
            saleOrder.setItems(productSaleItems);
            saleOrder.setMpsoPayOrderId(null);
            log.debug("订单:{},开始发送异步积分消息，openid:{}", saleOrder.getMpsoOrderNo(), saleOrder.getMpsoThirdBuyerId());
            log.info("supplyv2发送到member消费 同步订单到市民网的内容是"+saleOrder);
            //这里发送到mq,由member消费
            boolean result = memberOutput.send(MessageBuilder.withPayload(createAddPointDTO(saleOrder, responsePayVO)).build());
            if (result) {
                log.info("订单{}，发送异步积分消息成功!", saleOrder.getMpsoOrderNo());
            } else {
                log.info("发送异步积分消息失败!");
            }
        } else {
            log.warn("发送异步积分消息失败,订单{}没有openId或会员手机号!", saleOrder.getMpsoOrderNo());
            log.info("三方卖家ID为{}", saleOrder.getMpsoThirdBuyerId());
            log.info("会员电话为{}", saleOrder.getMpsoSmiPhone());
        }

    }


    @Override
//    public void sendCancelMessage(String singleNo, String refundMethod) {
    public void sendCancelMessage(String singleNo, String refundMethod, List<SmMerchantProductSaleItem> items) {
        List<String> listProductNos = new ArrayList<>();
        String strProductNos = "";
        if (items != null){
            items.forEach(item->{
                listProductNos.add(item.getMpsiMpbiId()+"");
            });
            strProductNos = StringUtils.join(listProductNos, ",");
        }
        String url = smwConfig.getHost().replaceAll("ORDERNO", singleNo) + "?refundType="+refundMethod + "&productNos ="+strProductNos;
        log.info("=====================积分地址加参数========================="+url);
        log.info("请求地址:{}", url);
        HttpRequest request = HttpUtil.createRequest(Method.POST, url);
        request.timeout(60000);
//        request.body(encrypt);
        HttpResponse execute = request.execute();
        log.info("返回参数:{}", execute);
    }

    private AddPointDTO createAddPointDTO(SmMerchantProductSaleOrder order, ResponsePayVO responsePayVO) {
        AddPointDTO dto = new AddPointDTO();
        BeanUtil.copyProperties(order, dto);
        List<AddPointItemDTO> items = ConvertUtil.convertList(order.getItems(), AddPointItemDTO.class);
        dto.setItems(items);
//        BaseResultResponse<SmStoreBasicInfoVO> sbiInfo = HttpPushMessageManager.getVoById(order.getMpsoSbiId());
        BaseResultResponse<Boolean> sbiIsMarketBool = HttpPushMessageManager.isMarket(order.getMpsoSbiCode());
        log.info("================门店类型是否是菜场:{}",sbiIsMarketBool.getData());
        Integer sbiType;
        if (!sbiIsMarketBool.getData()){
            sbiType = 1;
        }else {
            sbiType = 3;
        }
        dto.setPlace(sbiType);
        //扫码方式
        PayModeEnum payModeEnum = PayModeEnum.getByCode(responsePayVO.getPayMode());
        dto.setMpsoPaymentMode(payModeEnum.getValue());
        log.info("==========给member生产的消息======给member生产的消息===给member生产的消息==================="+dto);
        return dto;
    }

    private AddPointDTO createAddPointDTO(SmMerchantProductSaleOrder order) {
        AddPointDTO dto = new AddPointDTO();
        BeanUtil.copyProperties(order, dto);
        List<AddPointItemDTO> items = ConvertUtil.convertList(order.getItems(), AddPointItemDTO.class);
        dto.setItems(items);
        BaseResultResponse<SmStoreBasicInfoVO> sbiInfo = HttpPushMessageManager.getVoById(order.getMpsoSbiId());
        log.info("================门店类型获取:{}",sbiInfo);
        Integer sbiType = -1;
        if ("普通门店".equals(sbiInfo.getData().getSbiCategoryName())){
            sbiType = 1;
        }else if ("智慧菜场".equals(sbiInfo.getData().getSbiCategoryName())){
            sbiType = 3;
        }
        dto.setPlace(sbiType);
        log.info("==========给member生产的消息======给member生产的消息===给member生产的消息==================="+dto);
        return dto;
    }


    @Autowired
    @Qualifier(YLD_ADD_POINT_OUT_PUT)
    private MessageChannel memberOutput;

    @Autowired
    SmwConfig smwConfig;

    @Autowired
    private IHttpPushMessageManager HttpPushMessageManager;
}
