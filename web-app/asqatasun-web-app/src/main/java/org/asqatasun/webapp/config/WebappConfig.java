/*
 * Asqatasun - Automated webpage assessment
 * Copyright (C) 2008-2016  Asqatasun.org
 *
 * This file is part of Asqatasun.
 *
 * Asqatasun is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact us by mail: asqatasun AT asqatasun DOT org
 */
package org.asqatasun.webapp.config;

import org.apache.commons.lang3.StringUtils;
import org.asqatasun.webapp.exception.TgolHandlerExceptionResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.ResourceBundleViewResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by meskoj on 16/05/16.
 */
@Configuration
@EnableTransactionManagement
@PropertySources({
        @PropertySource("classpath:asqatasun-private.properties"),
        @PropertySource("${confDir}/asqatasun.properties"),
        @PropertySource("${confDir}/ESAPI.properties")
})
@EnableWebMvc
@ComponentScan({
        "org.asqatasun.persistence.config",
        "org.asqatasun.entity",
        "org.asqatasun.entity.dao",
        "org.asqatasun.entity.service",
        "org.asqatasun.service",
        "org.asqatasun.webapp.entity.factory",
        "org.asqatasun.webapp.entity.service",
        "org.asqatasun.webapp.entity.dao",
        "org.asqatasun.webapp.entity.decorator",
        "org.asqatasun.webapp.statistics",
        "org.asqatasun.webapp.orchestrator",
        "org.asqatasun.webapp.validator",
        "org.asqatasun.webapp.ui.form.menu",
        "org.asqatasun.contentadapter",
        "org.asqatasun.contentloader",
        "org.asqatasun.scenarioloader",
        "org.asqatasun.processor",
        "org.asqatasun.consolidator",
        "org.asqatasun.analyser",
        "org.asqatasun.webapp.statistics",
        "org.asqatasun.security",
        "org.asqatasun.nomenclatureloader",
        "org.asqatasun.ruleimplementationloader",
        "org.asqatasun.webapp.security",
        "org.asqatasun.emailsender",
        "org.asqatasun.webapp.highlighter"
})
public class WebappConfig extends WebMvcConfigurerAdapter {

    @Value("${asqatasunVersion}")
    String asqatasunVersion;

    @Value("${enable-account-settings}")
    boolean enableAccountSettings;

    @Bean (name = "propertySourcesPlaceholderConfigurer")
    static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean (name = "propertiesFactoryBean")
    public PropertiesFactoryBean propertiesFactoryBean() {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        Properties propertiesMaps = new Properties();
        propertiesMaps.setProperty("asqatasunVersion", asqatasunVersion);
        propertiesMaps.setProperty("enableAccountSettings", Boolean.toString(enableAccountSettings));
        propertiesFactoryBean.setProperties(propertiesMaps);
        return propertiesFactoryBean;
    }

    @Bean(name = "messageSource")
    public ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource() {
        ClassLoader cl = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        Resource[] resources = new Resource[0];

        // load dynamically all i18 files. They have to be in the classpath under an i18n folder.
        try {
            resources = resolver.getResources("classpath*:i18n/*I18N.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> baseNames = new ArrayList<>();

        for (Resource resource : resources){
            baseNames.add("classpath:i18n/"+ StringUtils.remove(resource.getFilename(), ".properties"));
        }

        ReloadableResourceBundleMessageSource bundleMessageSource =
                new ReloadableResourceBundleMessageSource();
        bundleMessageSource.setDefaultEncoding("UTF-8");
        bundleMessageSource.setBasenames(baseNames.toArray(new String[baseNames.size()]));
        return bundleMessageSource;
    }

    @Bean(name = "viewResolver")
    public ViewResolver getViewResolver(){
        ResourceBundleViewResolver resolver = new ResourceBundleViewResolver();
        resolver.setBasename("view");
        return resolver;
    }

    @Bean
    public SimpleMappingExceptionResolver createSimpleMappingExceptionResolver() {
        SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
        Properties errorMaps = new Properties();
        errorMaps.setProperty("org.asqatasun.webapp.exception.ForbiddenUserException", "access-denied");
        errorMaps.setProperty("org.asqatasun.webapp.exception.ForbiddenPageException", "access-denied");
        errorMaps.setProperty("org.asqatasun.webapp.exception.ForbiddenAuditException", "access-denied");
        errorMaps.setProperty("org.asqatasun.webapp.exception.LostInSpaceException", "oups");
        errorMaps.setProperty("org.asqatasun.webapp.exception.KrashAuditException","oups");
        errorMaps.setProperty("org.springframework.web.bind.MissingServletRequestParameterException","access-denied");
        errorMaps.setProperty("org.springframework.web.method.annotation.support.MethodArgumentNotValidException","access-denied");
        resolver.setExceptionMappings(errorMaps);
        Properties statusCodeMaps = new Properties();
        statusCodeMaps.setProperty("access-denied", "403");
        statusCodeMaps.setProperty("oups", "200");
        resolver.setStatusCodes(statusCodeMaps);
        resolver.setDefaultStatusCode(404);
        return resolver;
    }

    @Bean (name="multipartResolver")
    public CommonsMultipartResolver commonsMultipartResolver() {
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        // the maximum file size in bytes 104857600 = 100Mbytes
        commonsMultipartResolver.setMaxUploadSize(104857600l);
        return commonsMultipartResolver;
    }

    @Bean
    public TgolHandlerExceptionResolver tgolHandlerExceptionResolver() {
        return new TgolHandlerExceptionResolver();
    }

    @Bean
    public AnnotationMethodHandlerExceptionResolver annotationMethodHandlerExceptionResolver() {
        return new AnnotationMethodHandlerExceptionResolver();
    }

    @Bean
    public ResponseStatusExceptionResolver responseStatusExceptionResolver() {
        return new ResponseStatusExceptionResolver();
    }

    /* Localization section is started */

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor(){
        LocaleChangeInterceptor localeChangeInterceptor=new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    @Bean(name = "localeResolver")
    public LocaleResolver getLocaleResolver(){
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setCookieName("lang");
        return localeResolver;
    }

    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(10);
        threadPoolTaskExecutor.setDaemon(false);
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(false);
        threadPoolTaskExecutor.setMaxPoolSize(50);
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(false);
        return threadPoolTaskExecutor;
    }

    @Bean(name="conversionService")
    public ConversionService conversionService() {
        return new DefaultConversionService();
    }

}