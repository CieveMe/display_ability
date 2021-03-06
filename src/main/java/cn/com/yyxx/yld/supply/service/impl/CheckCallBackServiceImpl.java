package cn.com.yyxx.yld.supply.service.impl;

import cn.com.yyxx.yld.supply.action.BaseResultResponse;
import cn.com.yyxx.yld.supply.config.SocketConfig;
import cn.com.yyxx.yld.supply.config.TobacooConfig;
import cn.com.yyxx.yld.supply.data.vo.*;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleItem;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleOrder;
import cn.com.yyxx.yld.supply.exception.NotExceptException;
import cn.com.yyxx.yld.supply.manager.ISmMerchantProductSaleItemManager;
import cn.com.yyxx.yld.supply.manager.ISmMerchantProductSaleOrderManager;
import cn.com.yyxx.yld.supply.manager.org.ISysSmStoreBasicInfoManager;
import cn.com.yyxx.yld.supply.redis.RedisUtil;
import cn.com.yyxx.yld.supply.staticMap.PayStatusCode;
import cn.com.yyxx.yld.supply.util.PayModeEnum;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static cn.com.yyxx.yld.supply.messaging.pay.OrderUpdateSource.YLD_ORDER_UPDATE_OUT_PUT;
import static cn.com.yyxx.yld.supply.staticMap.StaticDataDef.SOS_SUCCESSFUL_PAYMENT;

/**
 * <p>
 *
 * </p>
 *
 * @author FYX
 * @version 0.16.0
 * @date 2020/12/10 09:43
 **/
@Service
public class CheckCallBackServiceImpl implements ICheckCallBackService {

    private Logger log = LoggerFactory.getLogger(CheckCallBackServiceImpl.class);

    @Override
    public void payCallBack(HttpServletRequest request) {
        ResponsePayVO responsePay;
        try {
            String body = HttpUtil
                    .getString(request.getInputStream(), StandardCharsets.UTF_8, true);
            responsePay = objectMapper.readValue(body, ResponsePayVO.class);
            if (StrUtil.isBlank(responsePay.getOrderId())) {
                throw new NotExceptException("???????????????????????????!");
            }
            if (responsePay.getStoreId() == null) {
                throw new NotExceptException("???????????????ID??????!");
            }
            log.info("????????????????????????, ??????:{}", body);
        } catch (IOException e) {
            log.error("????????????????????????:{}", e.getMessage());
            throw new NotExceptException("????????????????????????!");
        }
        //1???Rides??????
        Object ridesOrderObject = redisUtil.get(responsePay.getOrderId());
        if (ridesOrderObject == null) {
            log.warn("?????????????????????rides?????????????????????????????????{},?????????????????????????????????", responsePay.getOrderId());
            return;
        }
        RidesOrderVO ridesOrder = JSON.parseObject(ridesOrderObject.toString(), RidesOrderVO.class);
        responsePay.setOrderNo(ridesOrder.getOrder().getMpsoOrderNo());
        responsePay.setOrderId(ridesOrder.getOrder().getMpsoSingleNo());
        //2?????????????????????
        orderUpdateOutPut.send(MessageBuilder.withPayload(JSONObject.toJSONString(responsePay)).build());
        //3?????????????????????
        PayStatusCode payStatus = PayStatusCode.queryByCode(responsePay.getPayStatus());
        //???????????????????????????
        if (PayStatusCode.NEED_PASSWORD == payStatus) {
            return;
        }
        BaseResultResponse<Object> resultResponse = new BaseResultResponse<>();
        createResponse(resultResponse, responsePay);
        try {
            String username = ridesOrder.getOrder().getSbiCode() + "-" + ridesOrder.getSerialNum();
            HttpRequest sendClient = HttpUtil.createRequest(Method.POST,
                    "http://" + socketConfig.getHttp() + "" + socketConfig.getUrl());
            sendClient.timeout(30 * 1000);
//            String __s = JSONUtil.toJsonStr(resultResponse);
            String __s = JSON.toJSONString(resultResponse);
            Message msg = new Message();
            ChannelUser cu = new ChannelUser();
            cu.setName(username);
            try {
                msg.setBody(java.net.URLEncoder.encode(__s, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            msg.setCmd("SUPPLY_PAY_NOTIFICATION");
            msg.setCode("0x0003");
            msg.setReceive(cu);

//            log.info("????????????????????????????????????=====" + JSONUtil.toJsonStr(msg));
            log.info("????????????????????????????????????=====" + JSON.toJSONString(msg));
//            sendClient.body(JSONUtil.toJsonStr(msg));
            sendClient.body(JSON.toJSONString(msg));
            HttpResponse execute = sendClient.execute();
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if (!execute.isOk()) {
                throw new NotExceptException("????????????, ??????:{}", execute.getStatus());
            }
        } catch (Exception e) {
            log.info("????????????:" + e.getMessage());
            e.printStackTrace();
        }

        if (tobacooConfig.getEnable()){
            //??????app??????
            log.info("++++++++++??????app??????start.sh++++++++++");
            try {
                HttpRequest sendClient = HttpUtil.createRequest(Method.POST,
                        "http://" + tobacooConfig.getHttp() + "" + tobacooConfig.getUrl());
                sendClient.timeout(30 * 1000);
                String returnPay = JSON.toJSONString(resultResponse);
                TobacooMessage tobacooMsg = new TobacooMessage();
//            tobacooMsg.setType();
//            tobacooMsg.setUserid();
//            tobacooMsg.setIp(item);
                //???????????????????????????
                BaseResultResponse<List<String>> managerPhone = iSysSmStoreBasicInfoManager.getAreaManagerPhoneBySbiId(Integer.parseInt(ridesOrder.getOrder().getSbiCode()));
                if (managerPhone == null || managerPhone.getData() == null || managerPhone.getData().isEmpty()){
                    log.info("??????????????????");
                    tobacooMsg.setUsername("???????????????");
                }else{
                    managerPhone.getData().forEach(item->{
                        tobacooMsg.setUsername(item);
                    });
                }
                tobacooMsg.setGroup(false);
                tobacooMsg.setTitle("????????????");
                try {
                    tobacooMsg.setMessage(java.net.URLEncoder.encode(returnPay, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                log.info("??????APP????????????????????????=====" + JSON.toJSONString(tobacooMsg));
                sendClient.body(JSON.toJSONString(tobacooMsg));
                HttpRequestUtil.sendPost("http://" + tobacooConfig.getHttp() + "" + tobacooConfig.getUrl(), JSON.toJSONString(tobacooMsg));
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//            if (!execute.isOk()) {
//                throw new NotExceptException("??????app??????????????????, ??????:{}", execute.getStatus());
//            }
            } catch (Exception e) {
                log.info("??????app????????????:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    @Override
    public void cancelBack(HttpServletRequest request) {
        log.info("*******************????????????????????????**********************");

//        ResponsePayVO responsePay;
//        try {
//            String body = HttpUtil
//                    .getString(request.getInputStream(), StandardCharsets.UTF_8, true);
//            responsePay = objectMapper.readValue(body, ResponsePayVO.class);
//            if (StrUtil.isBlank(responsePay.getOrderId())) {
//                throw new NotExceptException("???????????????????????????!");
//            }
//            if (StrUtil.isBlank(responsePay.getPayStatus())) {
//                throw new NotExceptException("???????????????????????????!");
//            }
//            if (responsePay.getAmount() == null) {
//                throw new NotExceptException("???????????????????????????!");
//            }
//            log.info("????????????????????????, ??????:{}", body);
//        } catch (IOException e) {
//            log.error("????????????????????????:{}", e.getMessage());
//            throw new NotExceptException("????????????????????????!");
//        }
//        //1???Rides??????
//        Object ridesOrderObject = redisUtil.get(responsePay.getOrderId());
//        if (ridesOrderObject == null) {
//            log.error("?????????????????????rides?????????????????????????????????{},?????????????????????????????????", responsePay.getOrderId());
//            return;
//        }
//        RidesOrderVO ridesOrder = JSON.parseObject(ridesOrderObject.toString(), RidesOrderVO.class);
//        responsePay.setOrderNo(ridesOrder.getOrder().getMpsoOrderNo());
//        responsePay.setOrderId(ridesOrder.getOrder().getMpsoSingleNo());
//        //2?????????????????????
//        orderUpdateOutPut.send(MessageBuilder.withPayload(JSONObject.toJSONString(responsePay)).build());
    }

    @Override
    public Boolean vipPayCallBack(ResponsePayVO responsePay) {
        //??????????????????
        //?????????POS??????????????????????????????????????????????????????
        //??????????????????
        if (responsePay.getOrderNo() == null || "".equals(responsePay.getOrderNo())
                || responsePay.getAmount() == null || "".equals(responsePay.getAmount())
                || responsePay.getPayStatus() == null || "".equals(responsePay.getPayStatus())) {
            return false;
        }
        log.info("??????????????????????????????:{}",responsePay);
//        ??????????????????"????????????"????????????
        responsePay.setPayMode(PayModeEnum.SUPER_VIP.getCode());
        //Redis??????
        String ridesOrderObject = redisUtil.get(responsePay.getOrderNo()).toString();
        if (ridesOrderObject == null) {
            log.warn("?????????????????????rides?????????????????????????????????{},?????????????????????????????????", responsePay.getOrderId());
            return false;
        }
        orderUpdateOutPut.send(MessageBuilder.withPayload(JSONObject.toJSONString(responsePay)).build());
        //??????????????????
        PayStatusCode payStatus = PayStatusCode.queryByCode(responsePay.getPayStatus());
        //???????????????????????????
        if (PayStatusCode.NEED_PASSWORD == payStatus) {
            return false;
        }
        BaseResultResponse<Object> resultResponse = new BaseResultResponse<>();
        RidesOrderVO ridesOrder = JSON.parseObject(ridesOrderObject, RidesOrderVO.class);
        //?????????POS??????????????????????????????????????????
        responsePay.setPhone(ridesOrder.getOrder().getMpsoSmiPhone());
        createResponse(resultResponse, responsePay);
        try {
            String username = ridesOrder.getOrder().getSbiCode() + "-" + ridesOrder.getSerialNum();
            HttpRequest sendClient = HttpUtil.createRequest(Method.POST, "http://" + socketConfig.getHttp() + "" + socketConfig.getUrl());
            sendClient.timeout(30 * 1000);
//            String __s = JSONUtil.toJsonStr(resultResponse);
            String __s = JSON.toJSONString(resultResponse);
            Message msg = new Message();
            ChannelUser cu = new ChannelUser();
            cu.setName(username);
            try {
                log.info("=================????????????????????????================="+__s);
                msg.setBody(java.net.URLEncoder.encode(__s, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            msg.setCmd("SUPPLY_PAY_NOTIFICATION");
            msg.setCode("0x0003");
            msg.setReceive(cu);

//            sendClient.body(JSONUtil.toJsonStr(msg));
            sendClient.body(JSON.toJSONString(msg));
            HttpResponse execute = sendClient.execute();
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if (!execute.isOk()) {
                throw new NotExceptException("????????????, ??????:{}", execute.getStatus());
            }
        } catch (Exception e) {
            log.info("????????????:" + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void openIdCallBack(HttpServletRequest request) {
        log.info("openId??????????????????");
        ResponsePayVO responsePay;
        try {
            String body = HttpUtil.getString(request.getInputStream(), StandardCharsets.UTF_8, true);
            responsePay = objectMapper.readValue(body, ResponsePayVO.class);
            log.info("??????openId????????????, ??????:{}", body);
        } catch (IOException e) {
            log.error("??????openId????????????:{}", e.getMessage());
            throw new NotExceptException("????????????????????????!");
        }
        if (StrUtil.isBlank(responsePay.getOrderId())) {
            throw new NotExceptException("???????????????????????????!");
        }
        if (responsePay.getStoreId() == null) {
            throw new NotExceptException("???????????????ID??????!");
        }
        if (responsePay.getOpenid() == null) {
            throw new NotExceptException("????????????????????????{}", responsePay.getOrderId());
        }
        SmMerchantProductSaleOrder oldOrder = smMerchantProductSaleOrderManager.getByOrderNo(responsePay.getOrderId());
        if (oldOrder == null) {
            throw new NotExceptException("??????????????????{}", responsePay.getOrderId());
        }
        if ((oldOrder.getMpsoThirdBuyerId() + "").length() > 5) {
//            log.info("??????[{}]???openId{}????????????,???????????????!", responsePay.getStoreId(), JSONUtil.toJsonStr(oldOrder));
            log.info("??????[{}]???openId{}????????????,???????????????!", responsePay.getStoreId(), JSON.toJSONString(oldOrder));
            return;
        }
        SmMerchantProductSaleOrder newOrder = new SmMerchantProductSaleOrder();
        newOrder.setMpsoId(oldOrder.getMpsoId());
        newOrder.setMpsoThirdBuyerId(responsePay.getOpenid());
        newOrder.setMpsoModifyTime(LocalDateTime.now());
        log.info(newOrder.toString());
        smMerchantProductSaleOrderManager.updateById(newOrder);
        if (SOS_SUCCESSFUL_PAYMENT.getSddId().equals(oldOrder.getMpsoOrderStatus())) {
            List<SmMerchantProductSaleItem> items = merchantProductSaleItemManager.listByMpsoId(oldOrder.getMpsoId());
            //???????????????????????????
            try {
                memberService.sendCashOrderSuccessMessage(oldOrder, items);
            } catch (Exception e) {
                log.error("????????????????????????????????????{}", oldOrder.getMpsoOrderNo(),e);
            }
        } else {
            log.warn("??????????????????????????????,??????????????????[{}]????????????openId!", oldOrder.getMpsoId());
        }
    }

    private void createResponse(BaseResultResponse<Object> resultResponse, ResponsePayVO responsePay) {
        PaySuccessVO data = new PaySuccessVO();
        BeanUtil.copyProperties(responsePay, data);
        StaticDataDef bySddCode = StaticDataDef.findBySddCode(responsePay.getPayMode());
        if (bySddCode != null) {
            data.setPayMode(bySddCode.getSddExt1());
        }
        resultResponse.setSuccess(true);
        resultResponse.setMessage(responsePay.getErrInfo());
        resultResponse.setData(data);
    }

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ISmMerchantProductSaleOrderManager smMerchantProductSaleOrderManager;
    @Autowired
    private ISmMerchantProductSaleItemManager merchantProductSaleItemManager;
    @Autowired
    private SocketConfig socketConfig;
    @Autowired
    private TobacooConfig tobacooConfig;
    @Resource
    private RedisUtil redisUtil;
    @Autowired
    @Qualifier(YLD_ORDER_UPDATE_OUT_PUT)
    private MessageChannel orderUpdateOutPut;
    @Autowired
    private MemberServiceImpl memberService;
    @Autowired
    private SalesOrderServiceImpl salesOrderService;
    @Autowired
    private ISysSmStoreBasicInfoManager iSysSmStoreBasicInfoManager;
}
