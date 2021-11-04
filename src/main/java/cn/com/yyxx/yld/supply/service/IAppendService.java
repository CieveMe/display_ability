package cn.com.yyxx.yld.supply.service;

/**
 * <p>
 *
 * </p>
 *
 * @author hz
 * @version 0.1.0
 * @date 2021/07/06 17:29:56
 **/
public interface IAppendService {

    /***
     * <P>
     *     整单退款补齐品项(item)退款数据
     * </P>
     * @author hz
     * @date 2021/07/06 17:30:29
     * @param startT 开始时间
     * @param endT 结束时间
     * @return java.lang.Boolean
     * @since 0.17.0
     */
    Boolean itemAppend(String startT, String endT);

    /***
     * <P>
     *     单品退款更新品项(item)退款类型
     * </P>
     * @author hz
     * @date 2021/07/06 17:30:29
     * @param startT 开始时间
     * @param endT 结束时间
     * @return java.lang.Boolean
     * @since 0.17.0
     */
    Boolean itemUpdate(String startT, String endT);

}
