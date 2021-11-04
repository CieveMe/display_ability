package cn.com.yyxx.yld.supply.manager.impl;

import cn.com.yyxx.yld.supply.dao.sm.ISmMerchantProductSaleItemDao;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleItem;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleOrder;
import cn.com.yyxx.yld.supply.manager.ISmMerchantProductSaleItemManager;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 商家商品销售品项表 服务实现类
 * </p>
 *
 * @author hk
 * @since 2019-02-12
 */
@Service
@DS("supply-master")
public class SmMerchantProductSaleItemManagerImpl extends
    ServiceImpl<ISmMerchantProductSaleItemDao, SmMerchantProductSaleItem> implements
    ISmMerchantProductSaleItemManager {

    @Override
    @DS("supply-slave")
    public List<SmMerchantProductSaleItem> listByNos(List<String> nos) {
        if (nos.isEmpty()) {
            return new ArrayList<>();
        }
        return this.baseMapper.listByNos(nos);
    }

    @Override
    @DS("supply-slave")
    public List<SmMerchantProductSaleOrder> getByOrderCancelStatus(String startT, String endT) {
        return this.baseMapper.getByOrderCancelStatus(startT, endT);
    }

    @Override
    @DS("supply-slave")
    public List<SmMerchantProductSaleItem> getByOrderCancelType(String startT, String endT) {
        return this.baseMapper.getByOrderCancelType(startT, endT);
    }

    @Override
    @DS("supply-slave")
    public List<SmMerchantProductSaleItem> listByMpsoId(Long mpsoId) {
        QueryWrapper<SmMerchantProductSaleItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SmMerchantProductSaleItem.MPSI_MPSO_ID, mpsoId);
        return list(queryWrapper);
    }

    @Override
    @DS("supply-slave")
    public Integer getMaxSort() {
        return this.baseMapper.getMaxSort();
    }

}
