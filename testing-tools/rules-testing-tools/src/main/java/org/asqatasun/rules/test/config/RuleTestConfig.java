/*
 * Asqatasun - Automated webpage assessment
 * Copyright (C) 2008-2015  Asqatasun.org
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
package org.asqatasun.rules.test.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.asqatasun.contentadapter.*;
import org.asqatasun.contentadapter.css.CSSContentAdapterFactoryImpl;
import org.asqatasun.contentadapter.css.ExternalCSSRetriever;
import org.asqatasun.contentadapter.html.HTMLParserFactoryImpl;
import org.asqatasun.contentadapter.util.URLIdentifierFactory;
import org.asqatasun.contentadapter.util.URLIdentifierFactoryImpl;
import org.asqatasun.contentloader.*;
import org.asqatasun.entity.service.audit.*;
import org.asqatasun.rules.test.*;
import org.asqatasun.service.ContentLoaderService;
import org.asqatasun.service.ContentLoaderServiceImpl;
import org.asqatasun.util.factory.DateFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;


/**
 * Created by meskoj on 15/05/16.
 */
@Configuration
@EnableTransactionManagement
@ComponentScan({"org.asqatasun.entity.audit.factory",
                "org.asqatasun.entity.reference.factory",
                "org.asqatasun.entity.subject.factory",
                "org.asqatasun.entity.parameterization.factory",
                "org.asqatasun.entity.service.reference",
                "org.asqatasun.entity.service.parameterization",
                "org.asqatasun.contentadapter.config",
                "org.asqatasun.ruleimplementationloader",
                "org.asqatasun.nomenclatureloader",
                "org.asqatasun.processor",
                "org.asqatasun.consolidator",
                "org.asqatasun.service",
                "org.asqatasun.entity.dao"})
public class RuleTestConfig {

    @Bean
    public ContentDataService getContentDataService() {
        return new ContentDataServiceMock();
    }

    @Bean
    public ProcessRemarkDataService getProcessRemarkDataService() {
        return new ProcessRemarkDataServiceMock();
    }

    @Bean
    public ProcessResultDataService getProcessResultDataService() {
        return new ProcessResultDataServiceImpl();
    }

    @Bean
    public EvidenceDataService getEvidenceDataService() {
        return new EvidenceDataServiceImpl();
    }

    @Bean
    public EvidenceElementDataService getEvidenceElementDataService() {
        return new EvidenceElementDataServiceImpl();
    }

    @Bean
    public PreProcessResultDataService getPreProcessResultDataService() {
        return new PreProcessResultDataServiceMock();
    }

    @Bean
    public ContentLoaderService getContentLoaderService() {
        return new ContentLoaderServiceImpl();
    }

    @Bean
    public DownloaderFactory getDownloaderFactory() {
        return new DownloaderFactoryImpl();
    }

    @Bean
    public DateFactory getDateFactory() {
        return new DateFactoryImpl();
    }

    @Bean
    public ContentLoaderFactory getContentLoaderFactory() {
        return new ContentLoaderFactory() {
            @Override
            public ContentLoader create(ContentDataService contentDataService,
                                        Downloader downloader,
                                        DateFactory dateFactory,
                                        Map<String, String> fileMap) {
                return new ContentLoaderImpl(
                        contentDataService,
                        downloader,
                        dateFactory);
            }
        };
    }

    @Bean
    public ContentsAdapterFactory getContentsAdapterFactory() {
        return new ContentsAdapterFactoryImpl();
    }

    @Bean(name = "urlIdentifierFactory")
    public URLIdentifierFactory getURLIdentifierFactory() {
        return new URLIdentifierFactoryImpl();
    }

    @Bean
    public HTMLCleanerFactory getHTMLCleanerFactory() {
        return new HTMLCleanerFactoryImpl();
    }

    @Bean
    public HTMLParserFactory getHTMLParserFactory() {
        return new HTMLParserFactoryImpl();
    }

    @Bean
    public ContentAdapterFactory geContentAdapterFactory() {
        return new CSSContentAdapterFactoryImpl();
    }

    @Bean
    public ExternalCSSRetriever getExternalCSSRetriever() {
        return new ExternalCSSRetrieverMock();
    }

    @Bean(name = "dataSource")
    DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setUrl("jdbc:hsqldb:mem:mytestdb");
        return dataSource;
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource());
        emf.setPackagesToScan("org.asqatasun.entity");
        final HibernateJpaVendorAdapter hibernateAdapter = new HibernateJpaVendorAdapter();
        hibernateAdapter.setShowSql(false);
        final Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        jpaProperties.put("javax.persistence.schema-generation.database.action", "drop-and-create");

        emf.setJpaProperties(jpaProperties);
        emf.setJpaVendorAdapter(hibernateAdapter);

        return emf;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager(entityManagerFactory().getObject());
    }

}
