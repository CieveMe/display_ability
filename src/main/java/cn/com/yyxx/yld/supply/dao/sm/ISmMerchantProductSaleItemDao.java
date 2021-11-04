package cn.com.yyxx.yld.supply.dao.sm;

import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleItem;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 商家商品销售品项表 Mapper 接口
 * </p>
 *
 * @author hk
 * @since 2019-02-12
 */
public interface ISmMerchantProductSaleItemDao extends BaseMapper<SmMerchantProductSaleItem> {

    /**
     * <p>
     * 根据品项编号list查询品项信息 </p
     *
     * @param nos 品项编号list
     * @return cn.com.yyxx.yld.scm.entity.sm.SmMerchantProductSaleItem
     * @author linmeng
     * @since 2019/2/14 15:44
     */
    List<SmMerchantProductSaleItem> listByNos(@Param("nos") List<String> nos);

    Integer getMaxSort();

    /**
     * <p>
     * 根据时间范围 查出order已整单退款但item没改变"退款状态"的item
     * </p
     * @param startT 开始时间
     * @param endT 结束时间
     * @return cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleItem
     */
    List<SmMerchantProductSaleOrder> getByOrderCancelStatus(String startT, String endT);

    /**
     * <p>
     * 根据时间范围 查出order已整单退款但item没改变"退款状态"的item
     * </p
     * @param startT 开始时间
     * @param endT 结束时间
     * @return cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleItem
     */
    List<SmMerchantProductSaleItem> getByOrderCancelType(String startT, String endT);
}
