package cn.com.yyxx.yld.supply.manager.productv2;


import cn.com.yyxx.yld.supply.data.dto.ApiMapReMsg;
import cn.com.yyxx.yld.supply.data.dto.ProductChange;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class ProductV2ManagerFallbackFactory implements FallbackFactory<IProductV2Manager> {

    private Logger log = LoggerFactory.getLogger(ProductV2ManagerFallbackFactory.class);
    @Override
    public IProductV2Manager create(Throwable throwable) {
        return new IProductV2Manager() {
            @Override
            public ApiMapReMsg stockModify(ProductChange productChange,
                String store_id,
                String serial_num,
                String user_no) {

                log.warn("ProductChange  v2 修改库存");
                return new ApiMapReMsg("false", " ProductChange  v2 修改库存 ");
            }
        };
    }

}

