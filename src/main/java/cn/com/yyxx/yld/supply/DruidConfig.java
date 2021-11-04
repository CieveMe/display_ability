//package cn.com.yyxx.yld.supply;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.alibaba.druid.wall.WallConfig;
//import com.alibaba.druid.wall.WallFilter;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DatabaseDriver;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//
///**
// * <p></p>
// *
// * @author liujun
// * @version 1.0.0
// * @program yld-product-v2
// * @description 配置druid filter
// * @date 2020-11-06 14:21
// * @since 0.1.0
// **/
//@Configuration
//public class DruidConfig {
//    @ConditionalOnClass(DruidDataSource.class)
//    @ConditionalOnProperty(name = "spring.datasource.type",havingValue = "com.alibaba.druid.pool.DruidDataSource",matchIfMissing = true)
//    static class Druid extends DruidConfig{
//        @Bean
//        @Profile("default")
//        @ConfigurationProperties("spring.datasource.druid")
//        public DruidDataSource dataSource(DataSourceProperties properties){
//            DruidDataSource druidDataSource = (DruidDataSource) properties.initializeDataSourceBuilder()
//                    .type(DruidDataSource.class).build();
//            DatabaseDriver databaseDriver = DatabaseDriver.fromJdbcUrl(properties.determineUrl());
//            String validationQuery = databaseDriver.getValidationQuery();
//            if(validationQuery != null){
//                druidDataSource.setValidationQuery(validationQuery);
//            }
//            return druidDataSource;
//        }
//
//        @Bean
//        public WallFilter wallFilter(){
//            WallFilter wallFilter = new WallFilter();
//            wallFilter.setConfig(wallConfig());
//            return wallFilter;
//        }
//
//        @Bean
//        public WallConfig wallConfig(){
//            WallConfig wallConfig = new WallConfig();
//            wallConfig.setMultiStatementAllow(true);//允许一次执行多条语句
//            wallConfig.setNoneBaseStatementAllow(true);//允许一次执行多条语句
//            return wallConfig;
//        }
//
//
//    }
//}

