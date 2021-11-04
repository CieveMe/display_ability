package cn.com.yyxx.yld.supply.manager.productv2;

import cn.com.yyxx.yld.supply.data.dto.ApiMapReMsg;
import cn.com.yyxx.yld.supply.data.dto.ProductChange;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${yld.productv2.info.name}", url = "${yld.productv2.info.url}", fallbackFactory = ProductV2ManagerFallbackFactory.class)
public interface IProductV2Manager {

    /**
     * @param productChange
     * @return
     */
    @PostMapping(value = "/common/stock/modify")
    ApiMapReMsg stockModify(@RequestBody ProductChange productChange,
        @RequestParam String store_id,
        @RequestParam String serial_num,
        @RequestParam String user_no);


}
