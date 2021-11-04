package cn.com.yyxx.yld.supply.service.impl;

import cn.com.yyxx.yld.supply.data.vo.StaticDataDefVO;
import cn.com.yyxx.yld.supply.staticMap.BasicCacheConstant;
import cn.com.yyxx.yld.supply.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 静态数据业务处理类
 *
 * @author hk
 * @since 2018-12-13 15:38
 **/
@Service
@Transactional(rollbackFor = Exception.class)
@CacheConfig(cacheNames = BasicCacheConstant.CACHE_STATIC_DATA_DEF)
public class BmStaticDataDefServiceImpl implements IBmStaticDataDefService {

    @Override
    public List<StaticDataDefVO> listByDddCode(String sddId) {
        return ConvertUtil.convertList(sddManager.listByDddId(sddId), StaticDataDefVO.class);
    }

    @Autowired
    private IBmStaticDataDefManager sddManager;
}
