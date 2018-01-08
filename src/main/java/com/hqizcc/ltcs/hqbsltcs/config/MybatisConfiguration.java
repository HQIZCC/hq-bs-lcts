package com.hqizcc.ltcs.hqbsltcs.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * mybatis 配置数据源
 */
@Configuration
@EnableTransactionManagement
public class MybatisConfiguration implements EnvironmentAware {

    private RelaxedPropertyResolver propertyResolver;

    private String driveClassName;
    private String url;
    private String username;
    private String password;
    private String xmlLocation;
    private String typeAliasesPackage;

    // druid参数
    private String filters;
    private String maxActive;
    private String initialSize;
    private String maxWait;
    private String minIdle;
    private String timeBetweenEvictionRunsMillis;
    private String minEvictableIdleTimeMillis;
    private String validationQuery;
    private String testWhileIdle;
    private String testOnBorrow;
    private String testOnReturn;
    private String poolPreparedStatements;
    private String maxOpenPreparedStatements;


    @Bean
    public DataSource druidDataSource(){
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setDriverClassName(StringUtils.isEmpty(driveClassName) ? "com.mysql.jdbc.Driver" : driveClassName);
        druidDataSource.setMaxActive(StringUtils.isEmpty(maxActive) ? 10 : Integer.parseInt(maxActive));
        druidDataSource.setInitialSize(StringUtils.isEmpty(initialSize) ? 1 : Integer.parseInt(initialSize));
        druidDataSource.setMaxWait(StringUtils.isEmpty(maxWait) ? 6000 : Integer.parseInt(maxWait));
        druidDataSource.setMinIdle(StringUtils.isEmpty(minIdle) ? 3 : Integer.parseInt(minIdle));
        druidDataSource.setTimeBetweenEvictionRunsMillis(StringUtils.isEmpty(timeBetweenEvictionRunsMillis) ?
                60000 : Integer.parseInt(timeBetweenEvictionRunsMillis));
        druidDataSource.setMinEvictableIdleTimeMillis(StringUtils.isEmpty(minEvictableIdleTimeMillis) ?
                300000 : Integer.parseInt(minEvictableIdleTimeMillis));
        druidDataSource.setValidationQuery(StringUtils.isEmpty(validationQuery) ? "select 'x'" : validationQuery);
        druidDataSource.setTestWhileIdle(StringUtils.isEmpty(testWhileIdle) ? true : Boolean.parseBoolean(testWhileIdle));
        druidDataSource.setTestOnBorrow(StringUtils.isEmpty(testOnBorrow) ? false : Boolean.parseBoolean(testOnBorrow));
        druidDataSource.setTestOnReturn(StringUtils.isEmpty(testOnReturn) ? false : Boolean.parseBoolean(testOnReturn));
        druidDataSource.setPoolPreparedStatements(StringUtils.isEmpty(poolPreparedStatements) ? true : Boolean.parseBoolean(poolPreparedStatements));
        druidDataSource.setMaxOpenPreparedStatements(StringUtils.isEmpty(maxOpenPreparedStatements) ? 20 : Integer.parseInt(maxOpenPreparedStatements));

        try {
            druidDataSource.setFilters(StringUtils.isEmpty(filters) ? "stat, wall" : filters);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return druidDataSource;
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean(DataSource dataSource){
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        if (!StringUtils.isEmpty(typeAliasesPackage)){
            bean.setTypeAliasesPackage(typeAliasesPackage);
        }

        // 分页插件
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("returnPageInfo", "check");
        properties.setProperty("params", "count=countSql");
        pageHelper.setProperties(properties);
        // 添加xml目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Interceptor[] plugins = new Interceptor[]{
                pageHelper
        };
        bean.setPlugins(plugins);
        try {
            bean.setMapperLocations(resolver.getResources(xmlLocation));
            return bean.getObject();
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, null);
        this.url = propertyResolver.getProperty("spring.datasource.url");
        this.username= propertyResolver.getProperty("spring.datasource.username");
        this.password = propertyResolver.getProperty("spring.datasource.password");
        this.driveClassName = propertyResolver.getProperty("spring.datasource.driver-class-name");
        this.filters = propertyResolver.getProperty("spring.datasource.filters");
        this.maxActive = propertyResolver.getProperty("spring.datasource.maxActive");
        this.initialSize = propertyResolver.getProperty("spring.datasource.initialSize");
        this.maxWait = propertyResolver.getProperty("spring.datasource.maxWait");
        this.minIdle = propertyResolver.getProperty("spring.datasource.minIdle");
        this.timeBetweenEvictionRunsMillis = propertyResolver.getProperty("spring.datasource.timeBetweenEvictionRunsMillis");
        this.minEvictableIdleTimeMillis = propertyResolver.getProperty("spring.datasource.minEvictableIdleTimeMillis");
        this.validationQuery = propertyResolver.getProperty("spring.datasource.validationQuery");
        this.testWhileIdle = propertyResolver.getProperty("spring.datasource.testWhileIdle");
        this.testOnBorrow = propertyResolver.getProperty("spring.datasource.testOnBorrow");
        this.testOnReturn = propertyResolver.getProperty("spring.datasource.testOnReturn");
        this.poolPreparedStatements = propertyResolver.getProperty("spring.datasource.poolPreparedStatements");
        this.maxOpenPreparedStatements = propertyResolver.getProperty("spring.datasource.maxOpenPreparedStatements");
        this.typeAliasesPackage = propertyResolver.getProperty("mybatis.typeAliasesPackage");
        this.xmlLocation = propertyResolver.getProperty("mybatis.xmlLocation");
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

}






























