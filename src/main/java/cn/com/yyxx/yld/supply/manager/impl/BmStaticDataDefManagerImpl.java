package cn.com.yyxx.yld.supply.manager.impl;

import cn.com.yyxx.yld.supply.dao.bm.IBmStaticDataDefDao;
import cn.com.yyxx.yld.supply.entity.bm.BmStaticDataDef;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hk
 * @since 2018-12-20
 */
@Service
public class BmStaticDataDefManagerImpl extends ServiceImpl<IBmStaticDataDefDao, BmStaticDataDef> implements IBmStaticDataDefManager {
    @Override
    public List<BmStaticDataDef> listByDddId(String sddId) {
        return this.baseMapper.listByDddId(sddId, false);
    }

}