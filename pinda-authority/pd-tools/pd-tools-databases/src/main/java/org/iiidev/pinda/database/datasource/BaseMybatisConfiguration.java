package org.iiidev.pinda.database.datasource;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.iiidev.pinda.base.id.IdGenerate;
import org.iiidev.pinda.base.id.SnowflakeIdGenerate;
import org.iiidev.pinda.database.mybatis.typehandler.FullLikeTypeHandler;
import org.iiidev.pinda.database.mybatis.typehandler.LeftLikeTypeHandler;
import org.iiidev.pinda.database.mybatis.typehandler.RightLikeTypeHandler;
import org.iiidev.pinda.database.parsers.TenantWebMvcConfigurer;
import org.iiidev.pinda.database.properties.DatabaseProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Mybatis 常用重用拦截器
 *
 */
public abstract class BaseMybatisConfiguration {
    private final DatabaseProperties databaseProperties;

    public BaseMybatisConfiguration(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }

    /**
     * 分页插件，自动识别数据库类型
     */
    /* @Bean
    public PaginationInnerInterceptor paginationInterceptor() {
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        List<ISqlParser> sqlParserList = new ArrayList<>();

        if (this.databaseProperties.getIsBlockAttack()) {
            // 攻击 SQL 阻断解析器 加入解析链, Mybatisplus新版分页插件已经不支持了，放到下面 MybatisPlus插件中
            sqlParserList.add(new BlockAttackSqlParser());
        }

        paginationInnerInterceptor.setSqlParserList(sqlParserList);
        return paginationInnerInterceptor;
    } */

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        if (this.databaseProperties.getIsBlockAttack()) {
            // 攻击 SQL 阻断解析器 加入解析链, MybatisPlus新版分页插件已经不支持了，放到下面 MybatisPlus插件中
            mybatisPlusInterceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
            mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        }
        return mybatisPlusInterceptor;
    }


    /**
     * Mybatis Plus 注入器
     *
     * @param idGenerate
     * @return
     */
    @Bean("myMetaObjectHandler")
    public MetaObjectHandler getMyMetaObjectHandler(@Qualifier("snowflakeIdGenerate") IdGenerate<Long> idGenerate) {
        return new MyMetaObjectHandler(idGenerate);
    }

    /**
     * id生成 机器码， 单机配置1即可。 集群部署，每个实例自增1即可。
     *
     * @param machineCode
     * @return
     */
    @Bean("snowflakeIdGenerate")
    public IdGenerate getIdGenerate(@Value("${id-generator.machine-code:1}") Long machineCode) {
        return new SnowflakeIdGenerate(machineCode);
    }

    /**
     * 租户信息拦截器
     *
     * @return
     */
    @Bean
    @ConditionalOnProperty(name = "pinda.database.isMultiTenant", havingValue = "true", matchIfMissing = true)
    public TenantWebMvcConfigurer getTenantWebMvcConfigurer() {
        return new TenantWebMvcConfigurer();
    }

    /**
     * Mybatis 自定义的类型处理器
     * 用于左模糊查询时使用
     * <p>
     * eg：
     * and name like #{name,typeHandler=leftLike}
     *
     * @return
     */
    @Bean
    public LeftLikeTypeHandler getLeftLikeTypeHandler() {
        return new LeftLikeTypeHandler();
    }

    /**
     * Mybatis 自定义的类型处理器
     * 用于右模糊查询时使用
     * <p>
     * eg：
     * and name like #{name,typeHandler=rightLike}
     *
     * @return
     */
    @Bean
    public RightLikeTypeHandler getRightLikeTypeHandler() {
        return new RightLikeTypeHandler();
    }

    /**
     * Mybatis 自定义的类型处理器
     * 用于全模糊查询时使用
     * <p>
     * eg：
     * and name like #{name,typeHandler=fullLike}
     *
     * @return
     */
    @Bean
    public FullLikeTypeHandler getFullLikeTypeHandler() {
        return new FullLikeTypeHandler();
    }
}
