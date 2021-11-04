package cn.com.yyxx.yld.supply.manager;

import cn.com.yyxx.yld.supply.data.dto.ProductSaleOrderPayDTO;
import cn.com.yyxx.yld.supply.data.vo.OwnerOrderVo;
import cn.com.yyxx.yld.supply.data.vo.SmMerchantProductSaleOrderVO;
import cn.com.yyxx.yld.supply.data.vo.SmSaleOrderForOwnerVO;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleOrder;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商家商品销售订单表 服务类
 * </p>
 *
 * @author hk
 * @since 2019-02-12
 */
public interface ISmMerchantProductSaleOrderManager extends IService<SmMerchantProductSaleOrder> {


    OwnerOrderVo queryOwnerOrder(Integer mpsoSbiId);

    /**
     * <p>
     * 根据编码list和门店id查询订单
     * </p
     *
     * @param codes   编码list
     * @param storeId 门店id
     * @return java.util.List<cn.com.yyxx.yld.scm.entity.sm.SmMerchantProductSaleOrder>
     * @author hz
     * @since 2019/2/14 10:46
     */
    List<SmMerchantProductSaleOrder> listByCodeAndStoreId(List<String> codes, Integer storeId);

    /**
     * <p>
     * 根据订单编号和门店id查询订单状态
     * </p
     *
     * @param orderNo 订单编号
     * @param sbiId   门店id
     * @return cn.com.yyxx.yld.scm.data.pay.ProductSaleOrderPayDTO
     * @author hz
     * @date 2019/9/20 17:48
     */
    ProductSaleOrderPayDTO getByOrderNoAndSbiId(String orderNo, Integer sbiId);

    /**
     * <p>
     * 根据订单编号和门店id查询订单
     * </p
     *
     * @param orderNo  订单编号
     * @param sbiId    门店id
     * @param isDelete 是否删除
     * @return cn.com.yyxx.yld.scm.entity.sm.SmMerchantProductSaleOrder
     * @author hz
     * @date 2019/9/19 15:35
     */
    SmMerchantProductSaleOrder getByOrderNoAndSbiId(String orderNo, Integer sbiId, Boolean isDelete);

    /**
     * 根据orderno进行查询
     *
     * @param orderNo
     * @return
     */
    SmMerchantProductSaleOrder getByOrderNo(String orderNo);

    /**
     * <p>根据订单编号查询订单和详情信息</p>
     *
     * @param storeId 门店ID
     * @param orderNo 订单编号
     * @return cn.com.yyxx.yld.supply.data.vo.SmMerchantProductSaleOrderVO
     * @author FYX
     * @date 2020/12/9 9:52
     * @since 0.16.0
     */
    SmMerchantProductSaleOrderVO getOrderByNo(Integer storeId, String orderNo);

    /**
     * <p>
     * 获取最大排序
     * </p>
     *
     * @param storeId 门店ID
     * @return java.lang.Integer
     * @author FYX
     * @date 2020/12/10 12:13
     * @since 0.16.0
     */
    Integer getMaxSortByStoreId(Integer storeId);

    /**
     * <p>
     * 根据订单编号删除订单
     * </p>
     *
     * @param orderNo
     * @param storeId
     * @return java.lang.Boolean
     * @author FYX
     * @date 2020/12/11 10:35
     * @since 0.16.0
     */
    Boolean deleteOrder(String orderNo, Integer storeId);

    /**
     * <p>
     * 为owner查询订单信息
     * </p>
     *
     * @param sbiId   门店ID
     * @param orderNo 订单编码
     * @return SmSaleOrderForOwnerVO
     * @author guoyl
     * @date 2020/5/5 11:10
     * @since 1.0.0
     */
    SmSaleOrderForOwnerVO getOrderForOwner(Integer sbiId, String orderNo);

    /**
     * <p>
     * 根据订单实体中ID修改订单
     * </p>
     *
     * @param smMerchantProductSaleOrder 订单实体
     * @return SmSaleOrderForOwnerVO
     * @author guoyl
     * @date 2020/5/5 11:10
     * @since 1.0.0
     */
    @Override
    boolean updateById(SmMerchantProductSaleOrder smMerchantProductSaleOrder);

}
