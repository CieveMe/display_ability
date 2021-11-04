package cn.com.yyxx.yld.supply.manager.impl;

import cn.com.yyxx.yld.supply.dao.rm.IRmInStoreRecordItemDao;
import cn.com.yyxx.yld.supply.entity.rm.RmInStoreRecordItem;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p></p>
 *
 * @author haoch
 * @version 1.0.0
 * @program yld-supply-chain-v2
 * @description
 * @date 2021-02-02 14:19
 * @since 0.1.0
 **/
@Service
@DS("supply-master")
public class RmInStoreRecordItemImpl extends
    ServiceImpl<IRmInStoreRecordItemDao, RmInStoreRecordItem> implements
        IService<RmInStoreRecordItem> {

}
