package cn.com.yyxx.yld.supply.service.impl;

import cn.com.yyxx.yld.supply.data.dto.ApiMapReMsg;
import cn.com.yyxx.yld.supply.data.dto.ProductChange;
import cn.com.yyxx.yld.supply.data.dto.ProductItem;
import cn.com.yyxx.yld.supply.data.vo.RidesOrderCancelVO;
import cn.com.yyxx.yld.supply.data.vo.RidesOrderVO;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleItem;
import cn.com.yyxx.yld.supply.exception.NotExceptException;
import cn.com.yyxx.yld.supply.manager.productv2.IProductV2Manager;
import cn.com.yyxx.yld.supply.service.IProductService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author FYX
 * @version 0.16.0
 * @date 2020/12/03 15:26
 **/
@Service
public class ProductServiceImpl implements IProductService {

    private Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Override
    public void saleChangeProduct(RidesOrderVO ridesOrder,
                                  List<SmMerchantProductSaleItem> productSaleItems) {
        List<ProductItem> productItems = new ArrayList<>();
        List<Long> mpbiIds = new ArrayList<>();
        productSaleItems.forEach((item) -> {
            //临时商品不改变库存
            if ((item.getMpsiIsTemp()!=null&&item.getMpsiIsTemp()) || StringUtils.startsWith(item.getMpsiCode(),"LS")){
            }else{
                if (mpbiIds.contains(item.getMpsiMpbiId())){
                    int index = mpbiIds.indexOf(item.getMpsiMpbiId());
                    ProductItem pi = productItems.get(index);
                    pi.setChangeStokNum(pi.getChangeStokNum().add(item.getMpsiNum().negate()));
                    productItems.set(index,pi);
                }else{
                    ProductItem pi = new ProductItem();
                    pi.setMpbiId(item.getMpsiMpbiId());
                    pi.setChangeStokNum(item.getMpsiNum().negate());
                    productItems.add(pi);
                    mpbiIds.add(item.getMpsiMpbiId());
                }
            }
        });
        if (productItems.size()==0){
            if(productSaleItems.size() > 0){
                log.warn("该订单临时商品不需要变更库存");
            }else {
                log.warn("该订单不需要变更库存");
            }
            return;
        }
        ProductChange productChange = new ProductChange();
        productChange.setTyped("SaleProduct");
        productChange.setJob_no(ridesOrder.getUserInfo().getJobNo() + "");
        productChange.setSerial_num(ridesOrder.getSerialNum() + "");
        productChange.setUser_no(ridesOrder.getUserNo() + "");
        productChange.setCashierName(ridesOrder.getUserInfo().getNickName());
        productChange.setStore_id(ridesOrder.getUserInfo().getStoreId().toString());
        productChange.setProductItems(productItems);
        ApiMapReMsg apiMapReMsg = productV2Manager
            .stockModify(productChange, productChange.getStore_id(), productChange.getSerial_num(),
                productChange.getUser_no());
        log.info("productv2返回参数");
        if ("false".equals(apiMapReMsg.getSuccess())) {
            log.error("售卖商品时,门店[{}]处理订单,更新商家商品库存信息发生错误", productChange.getStore_id());
            throw new NotExceptException(
                "售卖商品时,门店{" + productChange.getStore_id() + "}处理订单[{}],更新商家商品库存信息发生错误{" + apiMapReMsg
                    .getMessage() + "}");
        }

    }

    @Override
    public void saleRefundProduct(RidesOrderCancelVO ridesOrder,
                                  List<SmMerchantProductSaleItem> productSaleItems) {


        List<ProductItem> productItems = new ArrayList<>();
        productSaleItems.forEach((item) -> {
            //临时商品不改变库存
            if ((item.getMpsiIsTemp()!=null&&item.getMpsiIsTemp()) || StringUtils.startsWith(item.getMpsiCode(),"LS")){

            }else {
                //过滤掉退款的item
//            if (!item.getMpsiIsRefund()){
                ProductItem pi = new ProductItem();
                pi.setMpbiId(item.getMpsiMpbiId());
                pi.setChangeStokNum(item.getMpsiNum());
                pi.setMpbiStockNumber(item.getMpsiNum());
                productItems.add(pi);
            }
//            }
        });
        if (productItems.size()==0){
            if(productSaleItems.size() > 0){
                log.warn("该订单临时商品不需要变更库存");
            }else {
                log.warn("该订单不需要变更库存");
            }
            return;
        }

        ProductChange productChange = new ProductChange();
        productChange.setTyped("Refund");
        productChange.setJob_no(ridesOrder.getUserInfo().getJobNo() + "");
        productChange.setSerial_num(ridesOrder.getSerialNum() + "");
        productChange.setUser_no(ridesOrder.getUserNo() + "");
        productChange.setCashierName(ridesOrder.getUserInfo().getNickName());
        productChange.setStore_id(ridesOrder.getUserInfo().getStoreId().toString());
        productChange.setProductItems(productItems);

        ApiMapReMsg apiMapReMsg = productV2Manager
            .stockModify(productChange, productChange.getStore_id(), productChange.getSerial_num(),
                productChange.getUser_no());

        log.info("productv2返回参数");
        if ("false".equals(apiMapReMsg.getSuccess())) {
            log.error("退款变更商品库存时,门店[{}]处理订单,更新商家商品库存信息发生错误", productChange.getStore_id());
            throw new NotExceptException(
                "退款变更商品库存时,门店{" + productChange.getStore_id() + "}处理订单[{}],更新商家商品库存信息发生错误{" + apiMapReMsg
                    .getMessage() + "}");
        }

    }

    @Override
    public void saleNewRefundProduct(RidesOrderCancelVO ridesOrder,
                                     List<SmMerchantProductSaleItem> productSaleItems) {


        List<ProductItem> productItems = new ArrayList<>();
        productSaleItems.forEach((item) -> {
            //临时商品不改变库存
            if ((item.getMpsiIsTemp()!=null&&item.getMpsiIsTemp()) || StringUtils.startsWith(item.getMpsiCode(),"LS")){

            }else {
                ProductItem pi = new ProductItem();
                pi.setMpbiId(item.getMpsiMpbiId());
                pi.setChangeStokNum(item.getMpsiNum());
                pi.setMpbiStockNumber(item.getMpsiNum());
                productItems.add(pi);
            }
        });
        if (productItems.size()==0){
            if(productSaleItems.size() > 0){
                log.warn("该订单临时商品不需要变更库存");
            }else {
                log.warn("该订单不需要变更库存");
            }
            return;
        }

        ProductChange productChange = new ProductChange();
        productChange.setTyped("Refund");
        productChange.setJob_no(ridesOrder.getUserInfo().getJobNo() + "");
        productChange.setSerial_num(ridesOrder.getSerialNum() + "");
        productChange.setUser_no(ridesOrder.getUserNo() + "");
        productChange.setCashierName(ridesOrder.getUserInfo().getNickName());
        productChange.setStore_id(ridesOrder.getUserInfo().getStoreId().toString());
        productChange.setProductItems(productItems);

        ApiMapReMsg apiMapReMsg = productV2Manager
                .stockModify(productChange, productChange.getStore_id(), productChange.getSerial_num(),
                        productChange.getUser_no());

        log.info("productv2返回参数");
        if ("false".equals(apiMapReMsg.getSuccess())) {
            log.error("单品退款变更库存时,门店[{}]处理订单,更新商家商品库存信息发生错误", productChange.getStore_id());
            throw new NotExceptException(
                    "单品退款变更库存时,门店{" + productChange.getStore_id() + "}处理订单[{}],更新商家商品库存信息发生错误{" + apiMapReMsg
                            .getMessage() + "}");
        }

    }

    @Autowired
    private IProductV2Manager productV2Manager;
}
