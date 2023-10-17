package com.ruoyi.framework.config

import com.cosium.spring.data.jpa.entity.graph.repository.support.EntityGraphJpaRepositoryFactoryBean
import com.ruoyi.framework.datasource.DynamicDataSource
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@EnableJpaRepositories(
    // entity的包名
    basePackages = [
        "com.ruoyi.system.entity.master"],
    // 创建的entityManagerFactory的方法名
    entityManagerFactoryRef="entityManagerFactoryByMaster",
    // 创建的transactionManager的方法名
    transactionManagerRef="transactionManagerByMaster",
    repositoryFactoryBeanClass = EntityGraphJpaRepositoryFactoryBean::class
)
@EnableTransactionManagement
@Component
class MasterJpaConfig(
    private val hibernateProperties: HibernateProperties,
    private val jpaProperties: JpaProperties,
    // 依赖注入mybatis-plus的动态数据源，他会把各个数据源以数据库名字为key存在map中
    private val dataSource: DataSource
){

    private val packages = arrayOf(
        "com.ruoyi.system.entity.master"
    )


    @Bean
    @Primary
    fun entityManagerFactoryByMaster(build: EntityManagerFactoryBuilder): LocalContainerEntityManagerFactoryBean {
        dataSource as DynamicDataSource
        val properties =
            hibernateProperties.determineHibernateProperties(jpaProperties.properties, HibernateSettings())
        // 获取mybatis-plus中的art数据源
        return build.dataSource(dataSource.resolvedDataSources["MASTER"])
            // entity的包名
            .packages(*packages)
            .properties(properties)
            .build()
    }

    @Bean
    @Primary
    fun transactionManagerByMaster(localContainerEntityManagerFactoryBean: LocalContainerEntityManagerFactoryBean): JpaTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = localContainerEntityManagerFactoryBean.`object`
        return transactionManager
    }

}
