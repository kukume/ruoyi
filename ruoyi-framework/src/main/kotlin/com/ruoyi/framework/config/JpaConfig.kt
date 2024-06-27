package com.ruoyi.framework.config

import com.cosium.spring.data.jpa.entity.graph.repository.support.EntityGraphJpaRepositoryFactoryBean
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module
import com.ruoyi.common.pojo.JpaPackageName
import com.ruoyi.common.utils.SecurityUtils
import com.ruoyi.framework.datasource.DynamicDataSource
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.*
import javax.sql.DataSource

@EnableJpaRepositories(
    // entity的包名
    basePackages = [
        "com.ruoyi.*.entity.master"
   ],
    // 创建的entityManagerFactory的方法名
    entityManagerFactoryRef="entityManagerFactoryByMaster",
    // 创建的transactionManager的方法名
    transactionManagerRef="transactionManagerByMaster",
    repositoryFactoryBeanClass = EntityGraphJpaRepositoryFactoryBean::class
)
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableTransactionManagement
@Component
class MasterJpaConfig(
    private val hibernateProperties: HibernateProperties,
    private val jpaProperties: JpaProperties,
    private val dataSource: DataSource
){


    @Bean
    @Primary
    fun entityManagerFactoryByMaster(build: EntityManagerFactoryBuilder): LocalContainerEntityManagerFactoryBean {
        dataSource as DynamicDataSource
        val properties =
            hibernateProperties.determineHibernateProperties(jpaProperties.properties, HibernateSettings())
        properties["hibernate.transaction.jta.platform"] = "org.springframework.boot.orm.jpa.hibernate.SpringJtaPlatform"
        // 获取mybatis-plus中的art数据源
        return build.dataSource(dataSource.resolvedDataSources["MASTER"])
            // entity的包名
            .packages(*JpaPackageName.entityPackageNameArray)
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

@Component("auditorAware")
class AuditorConfig: AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> {
        return try {
            Optional.of(SecurityUtils.getUsername())
        } catch (e: Exception) {
            Optional.of("system")
        }
    }
}

@Component
class RegisterConfig {
    @Bean
    fun hibernate6Module(): Hibernate6Module {
        return Hibernate6Module()
    }
}