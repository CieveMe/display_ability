package cn.com.yyxx.yld.supply.manager;

import cn.com.yyxx.yld.supply.data.vo.RmInStoreRecordOrderVO;
import cn.com.yyxx.yld.supply.entity.rm.RmInStoreRecordOrder;
import com.baomidou.mybatisplus.extension.service.IService;

public interface RmInStoreRecordOrderManager extends IService<RmInStoreRecordOrder> {


    int addRmInStoreRecordBackId(RmInStoreRecordOrder rmInStoreRecordOrderVO);

}
