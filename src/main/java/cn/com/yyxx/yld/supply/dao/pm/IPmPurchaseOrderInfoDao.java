package cn.com.yyxx.yld.supply.dao.pm;


import cn.com.yyxx.yld.supply.entity.pm.PmPurchaseOrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface IPmPurchaseOrderInfoDao extends BaseMapper<PmPurchaseOrderInfo> {

    /***
     * <P>
     *     根据Id查询采购订单
     * </P>
     * @author hz
     * @date 2021/2/6 17:27
     * @param poiId ID
    * @param poiIsIn 是否入库
    * @param poiIsEnabled 是否开启
     * @return cn.com.yyxx.yld.supply.entity.pm.PmPurchaseOrderInfo
     * @since
     */
    PmPurchaseOrderInfo findOrderById(@Param("poiId") Long poiId, @Param("poiIsIn") Boolean poiIsIn,
        @Param("poiIsEnabled") Boolean poiIsEnabled);
}
