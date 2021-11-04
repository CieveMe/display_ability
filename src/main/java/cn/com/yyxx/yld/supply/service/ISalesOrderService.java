package cn.com.yyxx.yld.supply.service;

import cn.com.yyxx.yld.supply.action.BaseResultResponse;
import cn.com.yyxx.yld.supply.data.vo.*;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleItem;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleOrder;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 销售订单操作接口
 * </p>
 *
 * @author liujun
 * @version 0.17.0
 * @date 2021/01/15 14:39
 **/
public interface ISalesOrderService {

    /**
     * <p>
     * 上传销售记录
     * </p
     *
     * @param ridesOrder       ridesVo
     * @param saleOrders       订单列表
     * @param saleOrderItemMap 订单品项集合
     * @author johnny
     * @since 2020/11/30 20:50
     */

    boolean upload(RidesOrderVO ridesOrder, List<SmMerchantProductSaleOrder> saleOrders, Map<String, List<SmMerchantProductSaleItem>> saleOrderItemMap);

    /**
     * <p>
     * 更新订单
     * </p
     *
     * @param ridesOrder    rides取值
     * @param responsePayVO 支付响应结果
     * @return java.lang.Boolean
     * @author liujun
     * @since 2021/01/15 14:31
     */
    boolean updateOrder(RidesOrderVO ridesOrder, ResponsePayVO responsePayVO);


    /**
     * <p>
     * 更新退款订单
     * </p
     *
     * @param ridesOrder    rides取值
     * @param responsePayVO 支付响应结果
     * @return java.lang.Boolean
     * @author liujun
     * @since 2021/01/15 14:31
     */
    boolean updateCannelOrder(RidesOrderCancelVO ridesOrder, ResponsePayVO responsePayVO);


    /**
     * 新版的退单，单品操作
     *
     * @param ridesOrder
     * @param responsePayVO
     * @return
     */
    boolean updateCannelNewOrder(RidesOrderNewCancelVO ridesOrder, ResponsePayVO responsePayVO);

    /**
     * <p>
     * 保存订单
     * </p
     *
     * @param ridesOrder rides取值
     * @return java.lang.Boolean
     * @author johnny
     * @since 2020/11/30 20:50
     */
    boolean saveOrder(RidesOrderVO ridesOrder);

    /**
     * <p>
     * 初始化订单
     * </p
     *
     * @param saleOrder  销售订单
     * @param ridesOrder rides取值
     * @author liujun
     * @since 2021/01/15 14:31
     */
    void initOrder(SmMerchantProductSaleOrder saleOrder, RidesOrderVO ridesOrder);

    /**
     * <p>
     * 初始化订单详情列表
     * </p
     *
     * @param order       上传订单VO
     * @param saleOrder   销售订单实体
     * @param productsMap 商品集合
     * @return List<SmMerchantProductSaleItem> 销售订单详情实体List
     * @author liujun
     * @since 2021/01/15 14:31
     */
    List<SmMerchantProductSaleItem> initOrderProduct(SmMerchantProductSaleOrderVO order, SmMerchantProductSaleOrder saleOrder, Map<String, PmMerchantProductBasicAndLibVO> productsMap);

    /**
     * <p>
     * 根据支付路由结果，初始化订单支付结果
     * </p
     *
     * @param saleOrder     销售订单实体
     * @param responsePayVO 支付路由返回VO
     * @author liujun
     * @since 2021/01/15 14:31
     */
    void initResultOrder(SmMerchantProductSaleOrder saleOrder, ResponsePayVO responsePayVO);


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
    BaseResultResponse<NewPutInVo> newPutIn(List<PutInStoreItemVO> items, UserWithPermissionVO user, String serialNum, String userNo, String rukuWay);

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
    Boolean oneBuiltIntoTheWarehouse(UserWithPermissionVO user, Long orderIdLong, Boolean isNewRuku,
                                     String serialNum, String userNo);
}
