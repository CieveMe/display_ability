package cn.com.yyxx.yld.supply.dao.bm;

import cn.com.yyxx.yld.supply.entity.bm.BmStaticDataDef;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author hk
 * @since 2018-12-20
 */
public interface IBmStaticDataDefDao extends BaseMapper<BmStaticDataDef> {


    /**
     * <p>
     * 根据字典编码查询相关静态数据
     * </p>
     *
     * @param sddId  字典编码
     * @param isDelete 是否删除
     * @author zzc
     * @since 2018/12/27 9:19
     */
    List<BmStaticDataDef> listByDddId(@Param("sddId") String sddId, @Param("isDelete") boolean isDelete);

}