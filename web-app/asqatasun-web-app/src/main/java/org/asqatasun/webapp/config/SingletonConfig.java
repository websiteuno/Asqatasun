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
import org.asqatasun.webapp.command.factory.*;
import org.asqatasun.webapp.exception.TgolHandlerExceptionResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
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
import org.springframework.web.servlet.view.ResourceBundleViewResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by meskoj on 16/05/16.
 */
@Configuration
public class SingletonConfig  {

    @Bean (name = "createContractCommandFactory")
    static CreateContractCommandFactory createContractCommandFactory() {
        return CreateContractCommandFactory.getInstance();
    }

    @Bean (name = "changeTestWeightCommandFactory")
    static ChangeTestWeightCommandFactory changeTestWeightCommandFactory() {
        return ChangeTestWeightCommandFactory.getInstance();
    }

    @Bean (name = "createUserCommandFactory")
    static CreateUserCommandFactory createUserCommandFactory() {
        return CreateUserCommandFactory.getInstance();
    }

    @Bean (name = "auditSetUpCommandFactory")
    static AuditSetUpCommandFactory auditSetUpCommandFactory() {
        return AuditSetUpCommandFactory.getInstance();
    }

    @Bean (name = "AuditResultSortCommandFactory")
    static AuditResultSortCommandFactory auditResultSortCommandFactory() {
        return AuditResultSortCommandFactory.getInstance();
    }
}
