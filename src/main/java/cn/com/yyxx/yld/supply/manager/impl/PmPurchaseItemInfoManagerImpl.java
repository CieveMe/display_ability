package cn.com.yyxx.yld.supply.manager.impl;

import cn.com.yyxx.yld.supply.dao.pm.IPmPurchaseItemInfoDao;
import cn.com.yyxx.yld.supply.entity.pm.PmPurchaseItemInfo;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p></p>
 *
 * @author haoch
 * @version 1.0.0
 * @program yld-supply-chain-v2
 * @description
 * @date 2021-02-03 16:39
 * @since 0.1.0
 **/
@Service
@DS("supply-master")
public class PmPurchaseItemInfoManagerImpl extends
    ServiceImpl<IPmPurchaseItemInfoDao, PmPurchaseItemInfo> implements PmPurchaseItemInfoManager {

    @Override
    @DS("supply-slave")
    public List<PmPurchaseItemInfo> findListByPoiId(Long poiId){
        return baseMapper.findListByPoiId(poiId);
    }
}
