package cn.com.yyxx.yld.supply.manager;

import cn.com.yyxx.yld.supply.entity.pm.PmPurchaseItemInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PmPurchaseItemInfoManager extends IService<PmPurchaseItemInfo> {

    List<PmPurchaseItemInfo> findListByPoiId(@Param("poiId") Long poiId);
}
