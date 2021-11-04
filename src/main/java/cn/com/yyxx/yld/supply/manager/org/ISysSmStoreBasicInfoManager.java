package cn.com.yyxx.yld.supply.manager.org;


import cn.com.yyxx.yld.oim.data.sm.vo.SmStoreBasicInfoVO;
import cn.com.yyxx.yld.supply.action.BaseResultResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 后台管理系统Api
 * </p>
 *
 * @author zzc
 * @since 2018/12/28 11:03
 */
@FeignClient(value = "${yld.organization.name}", url = "${yld.organization.url}", fallbackFactory = SysSmStoreBasicInfoManagerSupplyV2FallbackFactory.class)
public interface ISysSmStoreBasicInfoManager {

    /**
     * <p>
     * 根据门店ID集合查询门店信息
     * </P>
     *
     * @param storeIds：门店id集合
     * @return 门店信息
     * @author jiaorui
     * @date 2019/5/23 0:13
     */
    @RequestMapping(value = "/system/store/query_Store_List", method = RequestMethod.PUT)
    BaseResultResponse<List<SmStoreBasicInfoVO>> queryStoreList(@RequestParam(value = "storeIds") List<Integer> storeIds);

    /**
     * <p>
     * 审核
     * </p
     *
     * @param storeId 门店id
     * @param isPass  是否通过
     * @param cdKey   激活码
     * @param message 不通过原因
     * @return cn.com.yyxx.model.core.BaseResultResponse<java.lang.Boolean>
     * @author linmeng
     * @since 2018/12/26 9:10
     */
    @RequestMapping(value = "/system/store/review", method = RequestMethod.PUT)
    BaseResultResponse<Boolean> review(@RequestParam("storeId") Integer storeId,
                                       @RequestParam("isPass") Boolean isPass,
                                       @RequestParam(value = "cdKey", required = false) String cdKey,
                                       @RequestParam(value = "message", required = false) String message);

    /**
     * <p>
     * 注销
     * </p
     *
     * @param storeId 门店id
     * @return cn.com.yyxx.model.core.BaseResultResponse<java.lang.Boolean>
     * @author linmeng
     * @since 2018/12/26 9:54
     */
    @RequestMapping(value = "/system/store/cancel", method = RequestMethod.PUT)
    BaseResultResponse<Boolean> cancel(@RequestParam("storeId") Integer storeId);

    /**
     * <p>
     * 恢复
     * </p>
     *
     * @param storeId 门店id
     * @return cn.com.yyxx.model.core.BaseResultResponse<java.lang.Boolean>
     * @author linmeng
     * @since 2018/12/26 9:54
     */
    @RequestMapping(value = "/system/store/recover", method = RequestMethod.PUT)
    BaseResultResponse<Boolean> recover(@RequestParam("storeId") Integer storeId);

    /**
     * <p>
     * 根据门店编号 查询门店ID
     * </p
     *
     * @param sbiCode 门店编号
     * @return BaseResultResponse<String>
     * @author yangkun
     * @since 2019/10/11 17:34
     */
    @RequestMapping(value = "/system/store/get_sbiId_by_code", method = RequestMethod.GET)
    BaseResultResponse<String> getSbiIdBySbiCode(@RequestParam(value = "sbiCode", required = false) String sbiCode);

    /**
     * <p>
     * 根据门店编号 查询门店信息不关联其他表
     * </p
     *
     * @param sbiCode 门店编号
     * @return BaseResultResponse<SmStoreBasicInfoVO>
     * @author yangkun
     * @since 2019/10/16 23:45
     */
    @RequestMapping(value = "/system/store/get_store_info_by_code", method = RequestMethod.GET)
    BaseResultResponse<SmStoreBasicInfoVO> getStoreInfoBySbiCode(@RequestParam(value = "sbiCode", required = false) String sbiCode);

    /**
     * <p>
     * 根据门店编号 查询门店信息不关联其他表
     * </p
     *
     * @param sbiId 门店ID
     * @return BaseResultResponse<SmStoreBasicInfoVO>
     * @author yangkun
     * @since 2019/10/16 23:45
     */
    @RequestMapping(value = "/system/store/get_store_info_by_id", method = RequestMethod.GET)
    BaseResultResponse<SmStoreBasicInfoVO> getStoreInfoById(@RequestParam(value = "sbiId", required = false) Integer sbiId);

    /**
     * <p>
     * 通过门店id 查询门店省市区中的区id
     * </p>
     *
     * @param sbiId 门店ID
     * @return 门店所属县(区)的id
     * @author yangkun
     * @since 2019/12/4 18:17
     */
    @RequestMapping(value = "/system/store/get_area_id_by_sbi_id", method = RequestMethod.GET)
    BaseResultResponse<Integer> getAreaIdBySbiId(@RequestParam("sbiId") Integer sbiId);

    /**
     * <p>
     * 通过门店id 查询门店省市区中的区id
     * </p>
     *
     * @param sbiId 门店ID
     * @return 门店所属县(区)的id
     * @author yangkun
     * @since 2019/12/4 18:17
     */
    @RequestMapping(value = "/system/store/get_area_manager_phone_by_sbi_id", method = RequestMethod.GET)
    BaseResultResponse<List<String>> getAreaManagerPhoneBySbiId(@RequestParam("sbiId") Integer sbiId);

    /**
     * <p>
     * 通过门店名称 查询门店id 可能存在重名门店
     * </p>
     *
     * @param sbiFullName 门店全名
     * @return 门店ID集合
     * @author yangkun
     * @since 2019/12/4 18:17
     */
    @RequestMapping(value = "/system/store/get_sbi_id_list_by_sbi_name", method = RequestMethod.GET)
    BaseResultResponse<List<Integer>> getSbiIdListBySbiName(@RequestParam("sbiFullName") String sbiFullName);


    /**
     * <p>
     * 根据id删除门店
     * </p
     *
     * @param id 门店id
     * @return cn.com.yyxx.model.core.BaseResultResponse<java.lang.Boolean>
     * @author linmeng
     * @since 2019/1/4 20:50
     */
    @RequestMapping(value = "/system/store/{id}", method = RequestMethod.DELETE)
    BaseResultResponse<Boolean> logicDeleteById(@PathVariable("id") Integer id);

    /**
     * <p>
     * 根据id集合批量删除门店
     * </p
     *
     * @param ids 门店id集合
     * @return cn.com.yyxx.model.core.BaseResultResponse<java.lang.Boolean>
     * @author linmeng
     * @since 2019/1/4 21:18
     */
    @RequestMapping(value = "/system/store/batch", method = RequestMethod.DELETE)
    BaseResultResponse<Boolean> logicDeleteByIds(@RequestParam("ids") List<Integer> ids);


    /**
     * <p>
     * 根据门店编号查询门店实体
     * </p>
     *
     * @param
     * @return cn.com.yyxx.model.core.BaseResultResponse<cn.com.yyxx.yld.oim.data.sm.vo.SmStoreBasicInfoVO>
     * @author guoyl
     * @since 2019/3/30 21:13
     */
    @RequestMapping(value = "/api/store/basic/basic_info/{sNo}", method = RequestMethod.GET)
    BaseResultResponse<SmStoreBasicInfoVO> getBySbiNoAndSbiIsDelete(@PathVariable("sNo") String sNo);


    /**
     * <p>
     * 数据迁移-上传门店图片到OSS并修改路劲
     * </p>
     *
     * @param
     * @return cn.com.yyxx.model.core.BaseResultResponse<java.lang.Boolean>
     * @throws -----------------------------------
     * @author zhang yang ze
     * @sline 2019/4/29 17:04
     */
    @RequestMapping(value = "/system/store/data_migration_to_modify_pic", method = RequestMethod.GET)
    BaseResultResponse<Boolean> dataMigration2ModifyPic();


    /**
     * <p>
     * 根据门店id集合查询门店信息
     * </P>
     *
     * @param sbiIds 门店id集合
     * @param type   是否匹配
     * @return cn.com.yyxx.model.core.BaseResultResponse<java.util.List       <       cn.com.yyxx.yld.oim.data.sm.vo.StoreBasicInfoVO>>
     * @author jiaorui
     * @date 2019/8/1 23:53
     */
    @RequestMapping(value = "/system/store/list/query_store_by_ids/{type}", method = RequestMethod.POST)
    BaseResultResponse<List<SmStoreBasicInfoVO>> queryStoreByIds(@RequestBody List<Integer> sbiIds, @PathVariable Boolean type);


    /**
     * <p>
     * 根据区域ID查询门店信息
     * </p>
     *
     * @param areaIds  区域ID集
     * @param isDel    是否删除
     * @param isActive 是否激活
     * @return java.util.Set<cn.com.yyxx.yld.oim.data.sm.vo.SmStoreBasicInfoVO>
     * -----------------------------------
     * @author zhang yang ze
     * @since 2019/8/7 16:12
     */
    @RequestMapping(value = "/system/store/find_store_by_area_ids", method = RequestMethod.POST)
    BaseResultResponse<Set<SmStoreBasicInfoVO>> findStoreByAreaIds(@RequestBody Set<Integer> areaIds, @RequestParam("isActive") Boolean isActive, @RequestParam("isDel") Boolean isDel);


    /**
     * <p>
     * 重新发送图片二维码
     * </p
     *
     * @param sbiId 门店id
     * @return java.lang.Boolean
     * @author linmeng
     * @date 2019/9/2 15:37
     */
    @RequestMapping(value = "/system/store/send_image_code/{sbiId}", method = RequestMethod.GET)
    BaseResultResponse<Boolean> sendImageCode(@PathVariable Integer sbiId);

    /**
     * <p>
     * 发送返回商户修改链接
     * </p
     *
     * @param sbiId 门店id
     * @return java.lang.Boolean
     * @author linmeng
     * @date 2019/9/4 18:20
     */
    @RequestMapping(value = "/system/store/send_mch_edit_info/{sbiId}", method = RequestMethod.GET)
    BaseResultResponse<Boolean> sendMchEditInfo(@PathVariable Integer sbiId);

    /**
     * <p>
     * 发送完善资料链接
     * </p
     *
     * @param sbiId 门店id
     * @return java.lang.Boolean
     * @author jiaorui
     * @date 2019/9/5 4:20
     */
    @RequestMapping(value = "/system/store/send_perfect_information/{sbiId}", method = RequestMethod.GET)
    BaseResultResponse<Boolean> sendPerfectInformation(@PathVariable Integer sbiId);

    /**
     * <p>
     * 提交进件
     * </p>
     *
     * @param id 门店ID
     * @return boolean
     * -----------------------------------
     * @author zhang yang ze
     * @date 2019/9/9 14:31
     */
    @RequestMapping(value = "/system/store/sign/submit/{id}", method = RequestMethod.GET)
    BaseResultResponse<Boolean> signSubmit(@PathVariable("id") Integer id);


    /**
     * <p>
     * 再次进件
     * </p
     *
     * @param sbiId 门店ID
     * @return cn.com.yyxx.model.core.BaseResultResponse<java.lang.Boolean>
     * @author linmeng
     * @date 2019/11/25 14:45
     */
    @RequestMapping(value = "/system/store/again/sign/in/{sbiId}", method = RequestMethod.GET)
    BaseResultResponse<Boolean> againSignIn(@PathVariable("sbiId") Integer sbiId);

    /**
     * <p>
     * 银商进件查询
     * </p
     *
     * @param ssiId 服务ID
     * @return cn.com.yyxx.model.core.BaseResultResponse<java.lang.Boolean>
     * @author linmeng
     * @date 2019/11/26 9:20
     */
    @RequestMapping(value = "/system/store/sign/query/nmrs/status/{ssiId}", method = RequestMethod.GET)
    BaseResultResponse<Boolean> queryNmrsStatus(@PathVariable("ssiId") Integer ssiId);

    /**
     * <p>
     * 重新执行任务
     * </p
     *
     * @param jobId 任务ID
     * @return String
     * @author linmeng
     * @date 2019/11/29 16:26
     */
    @RequestMapping(value = "/system/store/flowable/re/execute/job/{jobId}", method = RequestMethod.GET)
    BaseResultResponse<String> reExecuteJob(@PathVariable String jobId);


    /**
     * <p>
     * 国通星驿进件查询
     * </p
     *
     * @param ssiId 服务ID
     * @return cn.com.yyxx.model.core.BaseResultResponse<java.lang.Boolean>
     * @author johnny
     * @date 2020/02/29 9:20
     */
    @GetMapping(value = "/api/postar/flow_able/query_postar_status/{ssiId}")
    BaseResultResponse<Boolean> queryPostarStatus(@PathVariable Integer ssiId);


    /**
     * <p>
     * 批量生成市民网推广二维码
     * </p>
     *
     * @param id  登录用户ID
     * @param ids 　门店ID集合
     * @return cn.com.yyxx.model.core.BaseResultResponse<java.lang.String>
     * @author zhang yang ze
     * @date 2020-03-11 15:38
     * @since 0.11.0
     */
    @PostMapping("/system/qr/code/gen_qr_code_batch/{id}")
    BaseResultResponse<String> genQrCodeBatch(@PathVariable("id") Long id, @RequestBody Set<Integer> ids);

    /**
     * <p>
     * 发送推广码
     * </p
     *
     * @param sbiId 门店id
     * @return java.lang.Boolean
     * @author johnny
     * @date 2020/03/09 11:46
     */
    @GetMapping(value = "/system/store/send_citizen_promo_code/{sbiId}")
    BaseResultResponse<Boolean> sendCitizenPromoCode(@PathVariable Integer sbiId);

    /**
     * <p>
     * 查询门店数量
     * </p>
     *
     * @return java.lang.Integer
     * @author johnny
     * @date 2020-03-20 17:57
     * @since 0.11.0
     */
    @GetMapping(value = "/api/store/basic/query_count")
    BaseResultResponse<Integer> queryCount();

    /**
     * 修改发送信息
     */
    @GetMapping(value = "/api/store/basic/changeStoreFlag/{storeId}")
    BaseResultResponse<Boolean> changeStoreFlag(@PathVariable("storeId") Integer storeId);

}
