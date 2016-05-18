package org.asqatasun.rest.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asqatasun.rest.jackson.CustomJacksonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by meskoj on 18/05/16.
 */
@Configuration
public class AppConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AppConfig.class);

    @Value("${confDir:}")
    private String confDir;

    @Autowired
    CustomJacksonModule customModule;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(customModule);
        converter.setObjectMapper(objectMapper);
        return converter;
    }

    @Bean
    public InitializingBean loadLogbackConfiguration() {
        return () -> {
            Path fileLocation = FileSystems.getDefault().getPath(confDir + "/conf/logback.xml");
            if (Files.exists(fileLocation)) {
                LOG.info("A logback configuration is available in {}", fileLocation.toString());
                JoranConfigurator configurator = new JoranConfigurator();
                configurator.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
                configurator.doConfigure(fileLocation.toAbsolutePath().toString());
            }
            else {
                LOG.info("No logback configuration file found at {}, using default (cf src/main/resources in web project)", fileLocation.toString());
            }
        };
    }
}
