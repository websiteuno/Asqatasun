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

import org.asqatasun.webapp.util.webapp.ExposablePropertyPlaceholderConfigurer;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by meskoj on 16/05/16.
 */
@Configuration
@EnableTransactionManagement
@ComponentScan({
        "org.asqatasun.webapp.persistence.config",
        "org.asqatasun.entity",
        "org.asqatasun.entity.dao",
        "org.asqatasun.entity.service",
        "org.asqatasun.service",
        "org.asqatasun.webapp.entity.factory",
        "org.asqatasun.webapp.entity.service",
        "org.asqatasun.webapp.entity.dao",
        "org.asqatasun.webapp.entity.decorator",
        "org.asqatasun.webapp.orchestrator",
        "org.asqatasun.webapp.validator",
        "org.asqatasun.contentadapter",
        "org.asqatasun.contentloader",
        "org.asqatasun.scenarioloader",
        "org.asqatasun.processor",
        "org.asqatasun.consolidator",
        "org.asqatasun.analyser",
        "org.asqatasun.nomenclatureloader",
        "org.asqatasun.ruleimplementationloader",
        "org.asqatasun.webapp.security",
        "org.asqatasun.emailsender",
        "org.asqatasun.webapp.presentation.highlighter"
})
@PropertySources({
    @PropertySource("${confDir}/asqatasun.properties"),
    @PropertySource("${confDir}/ESAPI.properties")
})
public class WebappConfig {

    @Bean
    static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public PropertyPlaceholderConfigurer placeHolderConfigurer() {
        ExposablePropertyPlaceholderConfigurer exposablePropertyPlaceholderConfigurer =
                new ExposablePropertyPlaceholderConfigurer();
        exposablePropertyPlaceholderConfigurer.setFileEncoding("UTF-8");
        return exposablePropertyPlaceholderConfigurer;
    }

}
