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
package org.asqatasun.rest.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by meskoj on 17/05/16.
 */
@Configuration
@PropertySources({
        @PropertySource("classpath:default-hibernate.properties"),
        @PropertySource("classpath:flyway.properties")
})
public class PersistenceConfig {
    public static final String HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE = "hibernate.cache.use_second_level_cache";
    public static final String HIBERNATE_CACHE_USE_QUERY_CACHE = "hibernate.cache.use_query_cache";
    public static final String HIBERNATE_CACHE_REGION_FACTORY_CLASS = "hibernate.cache.region.factory_class";
    public static final String HIBERNATE_DIALECT = "hibernate.dialect";

    @Value("${jpa.showSql}")
    private boolean hibernateShowSql;

    @Value("${hibernate.cache.use_second_level_cache}")
    private boolean hibernateUse2ndLevelQueryCache;

    @Value("${hibernate.cache.use_query_cache}")
    private boolean hibernateUseQueryCache;

    @Value("${hibernate.cache.region.factory_class}")
    private String hibernateRegionFactory;

    @Value("${hibernate.dialect:org.asqatasun.dialect.AsqatasunMySQL5InnoDBDialect}")
    private String hibernateDialect;

    @Value("${flyway.migrationPrefix}")
    private String flywayMigrationPrefix;

    @Value("${flyway.locationAutomatedCommon}")
    private String flywayLocationAutomatedCommon;
//
//    @Value("${flyway.locationAutomatedMainOnly}")
//    private String flywayLocationAutomatedMainOnly;

    @Value("${jdbc.user:asqatasun}")
    private String databaseUser;

    @Value("${jdbc.password:asqatasun}")
    private String databasePassword;

    //    @Value("${database.url:jdbc:postgresql://localhost:5432/asqatasun}")
    @Value("${jdbc.url:jdbc:mysql://localhost:3306/asqatasun}")
    private String databaseURL;

    //    @Value("${jdbc.driverClassName:org.postgresql.Driver}")
    @Value("${jdbc.driverClassName:com.mysql.jdbc.Driver}")
    private String driverClassName;

    @Bean
    DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUsername(databaseUser);
        dataSource.setPassword(databasePassword);
        dataSource.setUrl(databaseURL + "?autoReconnect=true");
        return dataSource;
    }

    @Bean(name="flyway", initMethod = "migrate")
    public Flyway dbInitialization() {
        final Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource());
        flyway.setSqlMigrationPrefix(flywayMigrationPrefix);
        flyway.setLocations(flywayLocationAutomatedCommon);
        return flyway;
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource());
        emf.setPackagesToScan("org.asqatasun.entity");
        final HibernateJpaVendorAdapter hibernateAdapter = new HibernateJpaVendorAdapter();
        hibernateAdapter.setShowSql(hibernateShowSql);
        final Properties jpaProperties = new Properties();
        jpaProperties.put(HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE, hibernateUse2ndLevelQueryCache);
        jpaProperties.put(HIBERNATE_CACHE_USE_QUERY_CACHE, hibernateUseQueryCache);
        jpaProperties.put(HIBERNATE_CACHE_REGION_FACTORY_CLASS, hibernateRegionFactory);
        jpaProperties.put(HIBERNATE_DIALECT, hibernateDialect);
//        jpaProperties.put(PERSISTENCE_XML_LOCATION_KEY, persistenceXmlLocation);

        emf.setJpaProperties(jpaProperties);
        emf.setJpaVendorAdapter(hibernateAdapter);

        return emf;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager(entityManagerFactory().getObject());
    }

}
