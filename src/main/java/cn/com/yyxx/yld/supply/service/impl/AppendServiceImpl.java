package cn.com.yyxx.yld.supply.service.impl;

import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleItem;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleOrder;
import cn.com.yyxx.yld.supply.manager.ISmMerchantProductSaleItemManager;
import cn.com.yyxx.yld.supply.manager.impl.SmMerchantProductSaleItemManagerImpl;
import cn.com.yyxx.yld.supply.service.IAppendService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author hz
 * @version 0.1.0
 * @date 2021/07/06 18:07
 **/
@Service
public class AppendServiceImpl implements IAppendService {

    //退货退款
    private static final Integer RETURN_AND_REFUND = 1;
    //仅退款
    private static final Integer ONLY_REFUND = 2;
    private Logger log = LoggerFactory.getLogger(AppendServiceImpl.class);

    @Override
    public Boolean itemAppend(String startT, String endT) {
        //查询出 order已整单退款但item没改变"退款状态"的item
        List<SmMerchantProductSaleOrder> oldCancelItemRefundList = smMerchantProductSaleItemManager.getByOrderCancelStatus(startT, endT);
        //lambda 增加退款品项 修改原item退款状态和退款类型
        //整单退款 增加item退款条目
        if (oldCancelItemRefundList != null) {
            //定义新增退款item的容器
            List<SmMerchantProductSaleItem> cancelItemList = new ArrayList<>();
            //定义要更新已经退款item的容器
            List<SmMerchantProductSaleItem> updateOldItemList = new ArrayList<>();
            //更新item的退款类型
            oldCancelItemRefundList.forEach(order -> {
                for (int i = 0; i<order.getItems().size();i++){
                    SmMerchantProductSaleItem cancelItem = new SmMerchantProductSaleItem();
                    //新增退款item
                    SmMerchantProductSaleItem item = order.getItems().get(i);
                    BeanUtil.copyProperties(item, cancelItem);
                    cancelItem.setMpsiIsRefund(true);
                    item.setMpsiIsRefund(true);
                    //旧得整单退款补充item退款记录，退款类型一律为“退货退款”
                    cancelItem.setMpsiIsRefundType(RETURN_AND_REFUND);
                    item.setMpsiIsRefundType(RETURN_AND_REFUND);
                    //修改原item的订单状态 0 --> 1
                    updateOldItemList.add(item);

                    cancelItem.setMpsiNum(order.getItems().get(i).getMpsiNum().negate());
                    cancelItem.setMpsiSubTotal(order.getItems().get(i).getMpsiSubTotal().negate());
                    cancelItem.setMpsiCreateTime(order.getMpsoRefundTime());
                    cancelItem.setMpsiModifyTime(order.getMpsoRefundTime());
                    cancelItem.setMpsiNo(IdUtil.fastSimpleUUID());
                    cancelItem.setMpsiId(null);
                    cancelItemList.add(cancelItem);
                }
            });
            //补充整单退款的item项
            boolean appendBool = merchantProductSaleItemManager.saveBatch(cancelItemList);
            //修改原item的订单状态 0 --> 1
            boolean updateOldBool = merchantProductSaleItemManager.updateBatchById(updateOldItemList);
            return appendBool && updateOldBool;
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean itemUpdate(String startT, String endT) {
            // 修改原item的退款类型
            //定义修改原有item的容器
            List<SmMerchantProductSaleItem> oldItemList = new ArrayList<>();
            //查询出 order已整单退款item“已退款"但”退款状态"未更改的item
            List<SmMerchantProductSaleItem> oldCancelItemRefundTypeList = smMerchantProductSaleItemManager.getByOrderCancelType(startT, endT);
            oldCancelItemRefundTypeList.forEach(item -> {
                //仅退款判断 item的数量等于0就为退款中的“仅退款”
//                if (item.getMpsiNum().compareTo(BigDecimal.ZERO) > 0){
                    item.setMpsiIsRefundType(RETURN_AND_REFUND);
//                }else{
//                    item.setMpsiIsRefundType(ONLY_REFUND);
//                }
                oldItemList.add(item);
            });
            //修改退款商品item的“退款类型”
            //补充和修改都都成功 返回true
            return merchantProductSaleItemManager.updateBatchById(oldItemList);
    }

    @Autowired
    SalesOrderServiceImpl salesOrderService;
    @Autowired
    private SmMerchantProductSaleItemManagerImpl smMerchantProductSaleItemManager;
    @Autowired
    private ISmMerchantProductSaleItemManager merchantProductSaleItemManager;
}
