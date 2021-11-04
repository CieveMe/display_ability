package cn.com.yyxx.yld.supply.manager.impl;

import cn.com.yyxx.yld.supply.dao.sm.ISmMerchantProductSaleOrderDao;
import cn.com.yyxx.yld.supply.data.dto.ProductSaleOrderPayDTO;
import cn.com.yyxx.yld.supply.data.vo.OwnerOrderVo;
import cn.com.yyxx.yld.supply.data.vo.SmMerchantProductSaleOrderVO;
import cn.com.yyxx.yld.supply.data.vo.SmSaleOrderForOwnerVO;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleOrder;
import cn.com.yyxx.yld.supply.manager.ISmMerchantProductSaleOrderManager;
import cn.com.yyxx.yld.supply.util.LocalDateTimeUtils;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 商家商品销售订单表 服务实现类
 * </p>
 *
 * @author hk
 * @since 2019-02-12
 */
@Service
@DS("supply-master")
public class SmMerchantProductSaleOrderManagerImpl extends
    ServiceImpl<ISmMerchantProductSaleOrderDao, SmMerchantProductSaleOrder> implements
    ISmMerchantProductSaleOrderManager {

    @Autowired
    private ISmMerchantProductSaleOrderDao iSmMerchantProductSaleOrderDao;

    @Override
    public OwnerOrderVo queryOwnerOrder(Integer mpsoSbiId) {
        String startT = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).toString();
        String endT = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).toString();
        return iSmMerchantProductSaleOrderDao.queryOwnerOrderVo(mpsoSbiId,startT,endT);
    }

    @Override
    @DS("supply-slave")
    public List<SmMerchantProductSaleOrder> listByCodeAndStoreId(List<String> codes,
        Integer storeId) {
        if (codes.isEmpty()) {
            return new ArrayList<>();
        }
        return this.baseMapper.listByCodeAndStoreId(codes, storeId);
    }

    @Override
    @DS("supply-slave")
    public ProductSaleOrderPayDTO getByOrderNoAndSbiId(String orderNo, Integer sbiId) {
        return this.baseMapper.getByOrderNoAndSbiId(orderNo, sbiId);
    }

    @Override
    @DS("supply-slave")
    public SmMerchantProductSaleOrder getByOrderNoAndSbiId(String orderNo, Integer sbiId,
        Boolean isDelete) {
        QueryWrapper<SmMerchantProductSaleOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SmMerchantProductSaleOrder.MPSO_ORDER_NO, orderNo);

        if (sbiId != null) {
            queryWrapper.eq(SmMerchantProductSaleOrder.MPSO_SBI_ID, sbiId);
        }
        if (isDelete != null) {
            queryWrapper.eq(SmMerchantProductSaleOrder.MPSO_IS_DELETE, isDelete);
        }
        return getOne(queryWrapper);
    }

    @Override
    @DS("supply-slave")
    public SmMerchantProductSaleOrder getByOrderNo(String orderNo) {
//        QueryWrapper<SmMerchantProductSaleOrder> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq(SmMerchantProductSaleOrder.MPSO_ORDER_NO, orderNo);
//        return getOne(queryWrapper);
        return this.baseMapper.getByOrderNo(orderNo);
    }

    @Override
    @DS("supply-slave")
    public SmMerchantProductSaleOrderVO getOrderByNo(Integer storeId, String orderNo) {
        return this.baseMapper.getOrderByNo(storeId,orderNo);
    }

    @Override
    @DS("supply-slave")
    public Integer getMaxSortByStoreId(Integer storeId) {
        return this.baseMapper.getMaxSortByStoreId(storeId);
    }

    @Override
    public Boolean deleteOrder(String orderNo, Integer storeId) {
        return this.baseMapper.deleteOrder(orderNo,storeId);
    }

    @Override
    @DS("supply-slave")
    public SmSaleOrderForOwnerVO getOrderForOwner(Integer sbiId, String orderNo) {
        Assert.notNull(sbiId, "门店ID为空，查询异常");
        Assert.notNull(orderNo, "订单编号为空，查询异常");
        SmSaleOrderForOwnerVO vo = this.baseMapper.getOrderForOwner(sbiId, orderNo);
        if (ObjectUtil.isNotNull(vo) && vo.getMpsoOrderTime() != null) {
            vo.setOrderTime(LocalDateTimeUtils.formatDateTimeToStrDefault(vo.getMpsoOrderTime()));
        }
        return vo;
    }

    @Override
    public boolean updateById(SmMerchantProductSaleOrder smMerchantProductSaleOrder){
        return this.baseMapper.updateById(smMerchantProductSaleOrder)>0;
    }
}
