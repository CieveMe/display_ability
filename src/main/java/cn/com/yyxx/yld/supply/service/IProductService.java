package cn.com.yyxx.yld.supply.service;

import cn.com.yyxx.yld.supply.data.vo.RidesOrderCancelVO;
import cn.com.yyxx.yld.supply.data.vo.RidesOrderVO;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleItem;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author FYX
 * @version 0.16.0
 * @date 2020/12/03 15:25
 **/
public interface IProductService {

    /**
     * <p>
     * 销售变更库存 </p
     *
     * @param ridesOrder rides值
     * @param productSaleItems 售卖商品子订单
     * @author johnny
     * @since 2020/11/30 20:50
     */
    void saleChangeProduct(RidesOrderVO ridesOrder,
                           List<SmMerchantProductSaleItem> productSaleItems);

    /**
     * <p>
     * 销售退款库存 </p
     *
     * @param ridesOrder redis 值
     * @param productSaleItems 售卖商品子订单
     * @author johnny
     * @since 2020/11/30 20:50
     */
    void saleRefundProduct(RidesOrderCancelVO ridesOrder,
                           List<SmMerchantProductSaleItem> productSaleItems);

    /**
     * <p>
     * new销售退款库存 </p
     *
     * @param ridesOrder redis 值
     * @param productSaleItems 售卖商品子订单
     * @author johnny
     * @since 2020/11/30 20:50
     */
    void saleNewRefundProduct(RidesOrderCancelVO ridesOrder,
                              List<SmMerchantProductSaleItem> productSaleItems);

}
