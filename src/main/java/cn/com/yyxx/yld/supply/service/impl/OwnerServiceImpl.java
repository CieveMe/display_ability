package cn.com.yyxx.yld.supply.service.impl;

import cn.com.yyxx.yld.supply.action.BaseResultResponse;
import cn.com.yyxx.yld.supply.data.qo.WxMpTemplateMessageQo;
import cn.com.yyxx.yld.supply.data.vo.OwnerOrderVo;
import cn.com.yyxx.yld.supply.data.vo.SmStoreBasicInfoVO;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleOrder;
import cn.com.yyxx.yld.supply.exception.NotExceptException;
import cn.com.yyxx.yld.supply.manager.org.ISysSmStoreBasicInfoManager;
import cn.com.yyxx.yld.supply.manager.user.IApiUserGrantInfoManager;
import cn.com.yyxx.yld.supply.service.IOwnerService;
import cn.com.yyxx.yld.supply.service.sm.IStoreUploadMerchantProductSaleOrderService;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jodd.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户积分处理
 * </p>
 *
 * @author hz
 * @version 0.17.0
 * @date 2021/01/13 21:26
 **/
@Service
public class OwnerServiceImpl implements IOwnerService {
    private Logger log = LoggerFactory.getLogger(OwnerServiceImpl.class);
    private static final String AMOUNT_UNIT = "元";
    private static final String PAY_SUCCESS_TITLE = "您有一笔支付成功的交易";
    private static final String PAY_REFUND_TITLE = "您有一笔退款成功的交易";
    private static final String PAY_REFUND_TITLE_ITEM = "您有一笔单品退款成功的交易";
    private static final String SEND_URL = "http://owner:17200/common/send/template/message";

    @Override
    public void sendMessageToOwner(SmMerchantProductSaleOrder saleOrder, Integer typeId , Integer cancelOrderData , String refundMethod) {
        //查询是否关闭到账卡片推送，默认开启
        BaseResultResponse<cn.com.yyxx.yld.oim.data.sm.vo.SmStoreBasicInfoVO> storeInfoById = storeBasicInfoManager.getStoreInfoById(saleOrder.getMpsoSbiId());
        SmStoreBasicInfoVO vo=new SmStoreBasicInfoVO();
        BeanUtils.copyProperties(storeInfoById.getData(),vo);
        BaseResultResponse<SmStoreBasicInfoVO> res=new BaseResultResponse();
        res.setMessage(storeInfoById.getMessage());
        res.setData(vo);
        res.setSuccess(storeInfoById.getSuccess());
        res.setType(storeInfoById.getType());
        if (res.getSuccess()) {
            String sendwxmsg = res.getData().getSendwxmsg();
            JSONObject json = JSONUtil.parseObj(sendwxmsg);
            String paysend = json.getStr("paysend");
            //关闭到账推送
            if ("false".equals(paysend)) {
                log.warn("门店{}到账卡片发送已关闭",saleOrder.getMpsoSbiId());
                return;
            }
        }
        //查询店主微信openId
        List<Integer> storeIdList = Collections.singletonList(saleOrder.getMpsoSbiId());
        Map<Integer, String> map = BaseResultResponse.getResponseDataNotAllowNull(iApiUserGrantInfoManager.listByStoreIdsAndTypeCode(storeIdList, StaticDataDef.UOLT_OAUTH2_WX.getSddCode()));
        String ugiOnly = map.get(saleOrder.getMpsoSbiId());
//        无店主openid
        if (StringUtil.isBlank(ugiOnly)) {
            log.error("无门店{}店主的微信openId",saleOrder.getMpsoSbiId());
            return;
        }
//        组装微信消息模板
        WxMpTemplateMessageQo wxMpTemplateMessageQo = initWxTempMessageQo(ugiOnly, saleOrder, typeId,cancelOrderData,refundMethod);
//        发送消息
        sendToOwner(wxMpTemplateMessageQo);
    }


    private OwnerOrderVo queryOwnerOrderVo(Integer mpsoSbiId) {
        if (mpsoSbiId == null) {
            throw new NotExceptException("无法获取门店ID");
        }
        return iStoreUploadMerchantProductSaleOrderService.queryOwnerOrderVo(mpsoSbiId);
    }

    /***
     * <P>
     *     组装微信消息模板
     * </P>
     * @author hz
     * @date 2021/1/14 10:59
     * @param ugiOnly openid
     * @param saleOrder 销售订单
     * @return cn.com.yyxx.yld.supply.data.qo.WxMpTemplateMessageQo
     * @since 2021/01/13 21:26
     */
    private WxMpTemplateMessageQo initWxTempMessageQo(String ugiOnly, SmMerchantProductSaleOrder saleOrder, Integer typeId, Integer cancelOrderData, String refundMethod) {
        WxMpTemplateMessageQo messageQo = new WxMpTemplateMessageQo();
        messageQo.setToUser(ugiOnly);
        messageQo.setUrl("/member/mp/order/code_skip/" + saleOrder.getMpsoSbiId() + "/" + saleOrder.getMpsoOrderNo()+ "/" + cancelOrderData + "/" +refundMethod+ "/" +1);
        //交易成功  添加信息:  您有一笔支付成功的交易 今天第几笔  交易总额  现金算不算在内
        //判断退款为单品退款 或 整单退款 和 支付成功 typeId 0:支付成功  1:单品退款   2:整单退款
        if (typeId == 0) {
            OwnerOrderVo vo = queryOwnerOrderVo(saleOrder.getMpsoSbiId());
            if (vo == null) {
                messageQo.getData().put("first", PAY_SUCCESS_TITLE);
                log.error("当前门店没有支付成功的交易!!!");
            }
            String num = vo.getMpsoPaymentNum().toString();
            String price = vo.getPrice().toString();
            String onlineNum = vo.getOnlineCustomer().toString();
            String cashNum = vo.getCashCustomer().toString();
            String onlinePrice = vo.getOnlineTurnover().toString();
            String cashPrice = vo.getCashTurnover().toString();
            String superVipNum = vo.getSupervipCustomer().toString();
            String superVipPrice = vo.getSupervipTurnover().toString();
            String title = saleOrder.getMpsoStoreName()+"：\n";
            if (!"0".equals(superVipNum)){
                title += "今日第"+num+"笔收款(线上"+onlineNum+"笔，现金"+cashNum+"笔，超级会员"+superVipNum+"笔)\n共计￥"+price+"(线上￥"+onlinePrice+"，现金￥"+cashPrice+"，超级会员￥"+superVipPrice+"（7天后可提现）)";
            }else {
                title += "今日第"+num+"笔收款(线上"+onlineNum+"笔，现金"+cashNum+"笔)\n共计￥"+price+"(线上￥"+onlinePrice+"，现金￥"+cashPrice+")";
            }
            log.info("支付发送到账卡片话术-----------------------"+title);
            messageQo.setTemplateId(wxMpTemplateMessageProperties.getPayResult());
            messageQo.getData().put("first", title);
            messageQo.getData().put("keyword1", saleOrder.getMpsoSingleNo());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String time = formatter.format(saleOrder.getMpsoOrderTime());
            messageQo.getData().put("keyword2", time);
            StaticDataDef chanelDate = StaticDataDef.findBySddId(saleOrder.getMpsoScanCodeChannel());
            if (chanelDate == StaticDataDef.UNKNOW) {
                chanelDate = StaticDataDef.SSY_OTHER;
            }
            StaticDataDef payMethod = StaticDataDef.findBySddId(saleOrder.getMpsoPaymentMethod());
            if (payMethod == StaticDataDef.UNKNOW) {
                payMethod = StaticDataDef.SSY_OTHER;
            }
            messageQo.getData().put("keyword3", payMethod.getSddName() + " " + chanelDate.getSddName());
            messageQo.getData().put("keyword4", saleOrder.getMpsoActualPrice() + AMOUNT_UNIT);

            return messageQo;
        } else {
            //退款模板
            messageQo.setTemplateId(wxMpTemplateMessageProperties.getRefundResult());
            messageQo.getData().put("keyword1", saleOrder.getMpsoSingleNo());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String time = formatter.format(saleOrder.getMpsoRefundTime());
            messageQo.getData().put("keyword5", time);
            StaticDataDef chanelDate = StaticDataDef.findBySddId(saleOrder.getMpsoScanCodeChannel());
            if (chanelDate == StaticDataDef.UNKNOW) {
                chanelDate = StaticDataDef.SSY_OTHER;
            }
            StaticDataDef payMethod = StaticDataDef.findBySddId(saleOrder.getMpsoPaymentMethod());
            if (payMethod == StaticDataDef.UNKNOW) {
                payMethod = StaticDataDef.SSY_OTHER;
            }
            messageQo.getData().put("keyword3", payMethod.getSddName() + " " + chanelDate.getSddName());
            messageQo.getData().put("keyword2", saleOrder.getMpsoStoreName());
            if (typeId == 1) {
                messageQo.setUrl("/member/mp/order/code_skip/cancel_new/" + saleOrder.getMpsoSbiId() + "/" + saleOrder.getMpsoOrderNo() + "/" + cancelOrderData+ "/" +refundMethod+ "/" +1);
                messageQo.getData().put("first", PAY_REFUND_TITLE_ITEM);
                messageQo.getData().put("keyword4", saleOrder.getMpsoRealPrice().subtract(saleOrder.getMpsoActualPrice()).setScale(2,BigDecimal.ROUND_HALF_UP) + AMOUNT_UNIT);
            } else if (typeId == 2) {
                messageQo.setTemplateId(wxMpTemplateMessageProperties.getRefundResult());
                messageQo.getData().put("first", PAY_REFUND_TITLE);
                messageQo.getData().put("keyword4", saleOrder.getMpsoActualPrice() + AMOUNT_UNIT);
            }
            return messageQo;
        }
    }

    /***
     * <P>
     *     发送消息
     * </P>
     * @author hz
     * @date 2021/1/14 10:59
     * @param messageQo
     * @return cn.com.yyxx.model.core.BaseResultResponse<java.lang.Boolean>
     * @since
     */
    private void sendToOwner(WxMpTemplateMessageQo messageQo) {
        String body = com.alibaba.fastjson.JSONObject.toJSONString(messageQo);
        String response = HttpSend.httpRequest(SEND_URL, "POST", com.alibaba.fastjson.JSONObject.toJSONString(messageQo), null);
        if (response.length() <= 0) {
            log.warn("发送到账卡片异常,{},{}", body, response);
            return;
        }
        try {
            log.debug("返回结果:{}", response);
            BaseResultResponse resultResponse = objectMapper.readValue(response, BaseResultResponse.class);
            if (!resultResponse.getSuccess()) {
                log.warn("发送到账卡片失败,{}", body);
            }
        } catch (IOException e) {
            log.error("发送到账卡片转换异常{},{}", body, response,e);
        }
    }

    @Autowired
    private IStoreUploadMerchantProductSaleOrderService iStoreUploadMerchantProductSaleOrderService;
    @Autowired
    private ISysSmStoreBasicInfoManager storeBasicInfoManager;
    @Autowired
    private IApiUserGrantInfoManager iApiUserGrantInfoManager;
    @Autowired
    private WxMpTemplateMessageProperties wxMpTemplateMessageProperties;
    @Autowired
    private ObjectMapper objectMapper;
}
