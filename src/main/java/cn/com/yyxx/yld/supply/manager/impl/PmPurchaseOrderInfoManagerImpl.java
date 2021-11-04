package cn.com.yyxx.yld.supply.manager.impl;

import cn.com.yyxx.yld.supply.dao.pm.IPmPurchaseOrderInfoDao;
import cn.com.yyxx.yld.supply.entity.pm.PmPurchaseOrderInfo;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p></p>
 *
 * @author haoch
 * @version 1.0.0
 * @program yld-supply-chain-v2
 * @description
 * @date 2021-02-03 16:23
 * @since 0.1.0
 **/
@Service
@DS("supply-master")
public class PmPurchaseOrderInfoManagerImpl extends
    ServiceImpl<IPmPurchaseOrderInfoDao, PmPurchaseOrderInfo> implements
    IPmPurchaseOrderInfoManager {


    @Override
    @DS("supply-slave")
    public PmPurchaseOrderInfo findOrderById(Long poiId, Boolean poiIsIn, Boolean poiIsEnabled) {
        return baseMapper.findOrderById(poiId,poiIsIn,poiIsEnabled);
    }
}
