package cn.com.yyxx.yld.supply.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class Swagger2Configuration {

    @Bean
    public Docket createStoreApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            //分组名称
            .groupName("store")
            .select()
            .apis(RequestHandlerSelectors.basePackage("cn.com.yyxx.yld.pim.action.store"))
            .paths(PathSelectors.ant("/store/**"))
            .build()
            ;
    }

    @Bean
    public Docket createKeeperApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            //分组名称
            .groupName("keeper")
            .select()
            .apis(RequestHandlerSelectors.basePackage("cn.com.yyxx.yld.pim.action.keeper"))
            .paths(PathSelectors.ant("/keeper/**"))
            .build()
            ;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("盈立多 - 商品信息模块 - API")
            .description("盈立多 - 商品信息模块 - API")
            .termsOfServiceUrl("http://www.yingliduo.cn")
            .contact(new Contact("垣源信息", "http://www.yyxx.com.cn", "service@yyxx.com.cn"))
            .version("1.0")
            .build();
    }
}
