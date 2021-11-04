package cn.com.yyxx.yld.supply.manager;

import cn.com.yyxx.yld.supply.entity.pm.PmPurchaseOrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

public interface IPmPurchaseOrderInfoManager extends IService<PmPurchaseOrderInfo> {

    PmPurchaseOrderInfo findOrderById(Long poiId, Boolean poiIsIn, Boolean poiIsEnabled);
}
