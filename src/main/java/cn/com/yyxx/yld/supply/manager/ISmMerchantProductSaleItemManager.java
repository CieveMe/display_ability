package cn.com.yyxx.yld.supply.manager;

import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleItem;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleOrder;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 商家商品销售品项表 服务类
 * </p>
 *
 * @author hk
 * @since 2019-02-12
 */
public interface ISmMerchantProductSaleItemManager extends IService<SmMerchantProductSaleItem> {

    /**
     * <p>
     * 根据品项编号list查询品项信息
     * </p
     *
     * @param nos 品项编号list
     * @return cn.com.yyxx.yld.scm.entity.sm.SmMerchantProductSaleItem
     * @author hz
     * @since 2019/2/14 15:44
     */
    List<SmMerchantProductSaleItem> listByNos(List<String> nos);

    /**
     * 询出 order已整单退款但item没改变"退款状态"的item
     * @param startT 开始时间
     * @param endT 结束时间
     */
    List<SmMerchantProductSaleOrder> getByOrderCancelStatus(String startT, String endT);

    /**
     * 查询出 order已整单退款item“已退款"但”退款状态"未更改的item
     * @param startT 开始时间
     * @param endT 结束时间
     */
    List<SmMerchantProductSaleItem> getByOrderCancelType(String startT, String endT);

    /**
     * <p>
     * 根据订单id查询明细列表
     * </p
     *
     * @param mpsoId 订单id
     * @return java.util.List<cn.com.yyxx.yld.scm.entity.sm.SmMerchantProductSaleItem>
     * @author hz
     * @date 2019/9/23 14:54
     */
    List<SmMerchantProductSaleItem> listByMpsoId(Long mpsoId);


    Integer getMaxSort();
}
