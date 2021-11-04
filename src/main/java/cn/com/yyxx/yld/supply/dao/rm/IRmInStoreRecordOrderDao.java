package cn.com.yyxx.yld.supply.dao.rm;

import cn.com.yyxx.yld.supply.data.vo.RmInStoreRecordOrderVO;
import cn.com.yyxx.yld.supply.entity.rm.RmInStoreRecordOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface IRmInStoreRecordOrderDao extends BaseMapper<RmInStoreRecordOrder> {


    int addRmInStoreRecordBackId(
        @Param("rmInStoreRecordOrderVO") RmInStoreRecordOrder rmInStoreRecordOrderVO);

}
