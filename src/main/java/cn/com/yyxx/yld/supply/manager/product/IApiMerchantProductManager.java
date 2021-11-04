package cn.com.yyxx.yld.supply.manager.product;


import cn.com.yyxx.yld.supply.action.BaseResultResponse;
import cn.com.yyxx.yld.supply.data.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author linmeng
 * @version 1.0
 * @since 2019/2/14 17:13
 */
@FeignClient(value = "${yld.product.info.name}", url = "${yld.product.info.url}", fallbackFactory = ApiMerchantProductManagerFallbackFactory.class)
public interface IApiMerchantProductManager {

    /**
     * <p>
     *  根据商家商品编号list和门店id查询商家商品
     * </p
     * @param nos 商家商品编号list
     * @param storeId 门店id
     * @return cn.com.yyxx.model.core.BaseResultResponse<java.util.List<cn.com.yyxx.yld.pim.data.vo.PmMerchantProductBasicInfoVO>>
     * @author linmeng
     * @since 2019/2/14 16:43
     */
    @RequestMapping(value = "/api/merchant/product/list",method = RequestMethod.POST)
    BaseResultResponse<List<PmMerchantProductBasicAndLibVO>> listByNoAndStoreId(@RequestParam("nos") ArrayList<String> nos, @RequestParam("storeId") Integer storeId);
    /**
     * <p>
     *  根据商家商品ID list和门店id查询商家商品
     * </p
     * @param ids 商家商品编号list
     * @param mpbiSbiId 门店id
     * @return cn.com.yyxx.model.core.BaseResultResponse<java.util.List<cn.com.yyxx.yld.pim.data.vo.PmMerchantProductBasicInfoVO>>
     * @author linmeng
     * @since 2019/2/14 16:43
     */
    @PostMapping(value = "/system/store/product/get_no_by_id")
    BaseResultResponse<List<BasicInfoIdAndNoVO>> getListByMpbiIdAndSbiId(@RequestParam List<Long> ids, @RequestParam Integer mpbiSbiId);
    /**
     * <p>
     *  根据平台商品ID list和门店id查询商家商品
     * </p
     * @param ids 商家商品编号list
     * @param sbiid 门店id
     * @return cn.com.yyxx.model.core.BaseResultResponse<java.util.List<cn.com.yyxx.yld.pim.data.vo.BasicInfoIdAndNoVO>>
     * @author liujun
     * @since 2021 /02/06 17:11
     */
    @PostMapping(value = "/system/store/product/get_no_by_pislid_sbiid")
    BaseResultResponse<List<PmMerchantProductBasicInfoVO>> getListByPislIdAndSbiId(@RequestParam List<Long> ids, @RequestParam Integer sbiid);

    /**
     * 根据商品ID查询商品信息
     *
     * @param pislId 商品ID
     * @return PmProductInfoStandardLibVO
     */
    @GetMapping(value = "/system/product/lib/{pislId}")
    BaseResultResponse<ProductInfoStandardLibVO> getPmProductInfoStandardLibEById(@PathVariable Long pislId);

}
