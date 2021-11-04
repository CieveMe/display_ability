package cn.com.yyxx.yld.supply.dao.sm;


import cn.com.yyxx.yld.supply.data.dto.ProductSaleOrderPayDTO;
import cn.com.yyxx.yld.supply.data.vo.OwnerOrderVo;
import cn.com.yyxx.yld.supply.data.vo.SmMerchantProductSaleOrderVO;
import cn.com.yyxx.yld.supply.data.vo.SmSaleOrderForOwnerVO;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 * 商家商品销售订单表 Mapper 接口
 * </p>
 *
 * @author hk
 * @since 2019-02-12
 */
@Mapper
public interface ISmMerchantProductSaleOrderDao extends BaseMapper<SmMerchantProductSaleOrder> {



    /**
     *
     * 发送到账卡片时查询今天的第几笔,金额是多少
     * @param mpsoSbiId
     * @author ZhangShengYi
     * @date 2021/7/7 10:14
     * @return cn.com.yyxx.yld.supply.data.vo.OwnerOrderVo
     */
    OwnerOrderVo queryOwnerOrderVo(@Param("mpsoSbiId") Integer mpsoSbiId,@Param("startT") String startT,@Param("endT") String endT);

    /**
     * <p>
     * 根据编码list和门店id查询订单
     * </p
     *
     * @param codes   编码list
     * @param storeId 门店id
     * @return java.util.List<cn.com.yyxx.yld.scm.entity.sm.SmMerchantProductSaleOrder>
     * @author linmeng
     * @since 2019/2/14 10:46
     */
    List<SmMerchantProductSaleOrder> listByCodeAndStoreId(@Param("codes") List<String> codes, @Param("storeId") Integer storeId);

    /**
     * <p>
     * 根据订单编号和门店id查询订单状态
     * </p
     *
     * @param orderNo 订单编号
     * @param sbiId   门店id
     * @return cn.com.yyxx.yld.scm.data.pay.ProductSaleOrderPayDTO
     * @author linmeng
     * @date 2019/9/20 17:48
     */
    ProductSaleOrderPayDTO getByOrderNoAndSbiId(@Param("orderNo") String orderNo, @Param("sbiId") Integer sbiId);


    SmMerchantProductSaleOrder getByOrderNo(@Param("orderNo") String orderNo);


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
    SmMerchantProductSaleOrderVO getOrderByNo(@Param("storeId") Integer storeId, @Param("orderNo") String orderNo);

    /**
     * <p>
     * 获取最大排序
     * </p>
     *
     * @param
     * @return java.lang.Integer
     * @author FYX
     * @date 2020/12/10 12:07
     * @since 0.16.0
     */
    Integer getMaxSortByStoreId(@Param("storeId") Integer storeId);

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
    Boolean deleteOrder(@Param("orderNo") String orderNo, @Param("storeId") Integer storeId);

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
    SmSaleOrderForOwnerVO getOrderForOwner(@Param("sbiId") Integer sbiId, @Param("orderNo") String orderNo);
}
