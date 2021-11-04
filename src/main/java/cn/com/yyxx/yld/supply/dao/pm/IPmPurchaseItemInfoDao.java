package cn.com.yyxx.yld.supply.dao.pm;

import cn.com.yyxx.yld.supply.entity.pm.PmPurchaseItemInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IPmPurchaseItemInfoDao extends BaseMapper<PmPurchaseItemInfo> {

    /***
     * <P>
     *     根据采购订单Id查询订单详情
     * </P>
     * @author hz
     * @date 2021/2/6 17:38
     * @param poiId 采购订单Id
     * @return java.util.List<cn.com.yyxx.yld.supply.entity.pm.PmPurchaseItemInfo>
     * @since
     */
    List<PmPurchaseItemInfo> findListByPoiId(@Param("poiId") Long poiId);
}
