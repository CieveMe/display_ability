package cn.com.yyxx.yld.supply.manager.impl;

import cn.com.yyxx.yld.supply.dao.rm.IRmInStoreRecordOrderDao;
import cn.com.yyxx.yld.supply.data.vo.RmInStoreRecordOrderVO;
import cn.com.yyxx.yld.supply.entity.rm.RmInStoreRecordOrder;
import cn.com.yyxx.yld.supply.manager.RmInStoreRecordOrderManager;
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
 * @date 2021-02-01 11:38
 * @since 0.1.0
 **/
@Service
@DS("supply-master")
public class RmInStoreRecordOrderImpl extends
    ServiceImpl<IRmInStoreRecordOrderDao, RmInStoreRecordOrder> implements
    RmInStoreRecordOrderManager {

    @Override
    @DS("supply-master")
    public int addRmInStoreRecordBackId(RmInStoreRecordOrder rmInStoreRecordOrderVO) {
        return this.baseMapper.addRmInStoreRecordBackId(rmInStoreRecordOrderVO);
    }

}
