package cn.com.yyxx.yld.supply.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * MyBatisPlus配置类
 * @author hk
 * @date 2018-06-07 11:09
 */
@Configuration
@MapperScan("cn.com.yyxx.yld.supply.dao")
public class MybatisPlusConfig {



	/***
	 * plus 的性能优化
	 * @return
     */
	@Bean
	@Profile({"dev","test"})
	public PerformanceInterceptor performanceInterceptor() {
		PerformanceInterceptor performanceInterceptor=new PerformanceInterceptor();
		/*<!-- SQL 执行性能分析，开发环境使用，线上不推荐。 maxTime 指的是 sql 最大执行时长 -->*/
		performanceInterceptor.setMaxTime(1000000);
		/*<!--SQL是否格式化 默认false-->*/
		performanceInterceptor.setFormat(true);
		return performanceInterceptor;
	}

	/**
	 *	 mybatis-plus分页插件
	 */
	@Bean
	public PaginationInterceptor paginationInterceptor() {
		PaginationInterceptor page = new PaginationInterceptor();
		page.setDialectType(DbType.MYSQL.getDb());
		return page;
	}

//	@Bean
//	public ISqlInjector sqlRunnerInjector() {
//		return new LogicSqlInjector();
//	}


}
