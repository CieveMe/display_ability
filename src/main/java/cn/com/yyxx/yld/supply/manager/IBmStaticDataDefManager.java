package cn.com.yyxx.yld.supply.manager;

import cn.com.yyxx.yld.supply.entity.bm.BmStaticDataDef;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hk
 * @since 2018-12-20
 */
public interface IBmStaticDataDefManager extends IService<BmStaticDataDef> {

    /**
     * <p>
     * 根据字典code查询相关静态数据
     * </p>
     *
     * @param sddId 字典code
     * @author zzc
     * @since 2018/12/27 9:19
     */
    List<BmStaticDataDef> listByDddId(String sddId);
}
