package cn.com.yyxx.yld.supply.data.vo;

import lombok.Data;

import java.util.List;

/**
 * <p></p>
 *
 * @author haoch
 * @version 1.0.0
 * @program yld-supply-chain-v2
 * @description
 * @date 2021-01-30 11:18
 * @since 0.1.0
 **/
@Data
public class NewPutInVo {

    private String rukuNo;

    private List<PutInStoreItemVO> putInStoreItemVOList;

}
