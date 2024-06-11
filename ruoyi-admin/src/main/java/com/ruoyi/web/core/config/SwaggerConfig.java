package com.ruoyi.web.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ruoyi.common.config.RuoYiConfig;

/**
 * Swagger2的接口配置
 * 
 * @author ruoyi
 */
@Configuration
public class SwaggerConfig
{
    /** 系统基础配置 */
    @Autowired
    private RuoYiConfig ruoyiConfig;

    /**
     * 添加摘要信息
     */
    @Bean
    public OpenAPI apiInfo()
    {
        var info = new Info()
                .title("标题：若依管理系统_接口文档")
                .description("描述：用于管理集团旗下公司的人员信息,具体包括XXX,XXX模块...")
                .version(ruoyiConfig.getVersion())
                .contact(new Contact()
                        .name(ruoyiConfig.getName())
                        .url("<Your url>")
                        .email("<Your email>")
                )
                .license(new License()
                        .name("Apache 2.0")
                        .url("http://www.apache.org/licenses/LICENSE-2.0.txt")
                );
        SecurityScheme securityScheme = new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.APIKEY)
                .scheme("basic")//固定写法
                .in(SecurityScheme.In.HEADER)
                .description("请求头");
        Components components = new Components()
                .addSecuritySchemes("Authorization", securityScheme);
        return new OpenAPI()
                .info(info)
                .components(components)
                .externalDocs(new ExternalDocumentation()
                        .description("SpringDoc Wiki Documentation")
                        .url("https://springdoc.org/v2"));
    }

    @Bean
    public GroupedOpenApi publicApi(){
        return GroupedOpenApi.builder()
                .group("common")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.addSecurityItem(new SecurityRequirement().addList("Authorization"));
                    return operation;
                })
                .pathsToMatch("/**")
                .packagesToScan("com.ruoyi")
                .build();
    }
}
