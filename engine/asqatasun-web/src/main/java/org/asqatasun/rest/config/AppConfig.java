package org.asqatasun.rest.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asqatasun.analyser.AnalyserFactory;
import org.asqatasun.analyser.AnalyserFactoryImpl;
import org.asqatasun.consolidator.ConsolidatorFactory;
import org.asqatasun.consolidator.ConsolidatorFactoryImpl;
import org.asqatasun.contentadapter.*;
import org.asqatasun.contentadapter.css.CSSContentAdapterFactory;
import org.asqatasun.contentadapter.css.CSSContentAdapterFactoryImpl;
import org.asqatasun.contentadapter.css.ExternalCSSRetriever;
import org.asqatasun.contentadapter.css.ExternalCSSRetrieverImpl;
import org.asqatasun.contentadapter.html.HTMLCleanerFactoryImpl;
import org.asqatasun.contentadapter.html.HTMLParserFactoryImpl;
import org.asqatasun.contentadapter.util.URLIdentifierFactory;
import org.asqatasun.contentadapter.util.URLIdentifierFactoryImpl;
import org.asqatasun.contentloader.*;
import org.asqatasun.nomenclatureloader.NomenclatureLoader;
import org.asqatasun.nomenclatureloader.NomenclatureLoaderImpl;
import org.asqatasun.processor.ProcessorFactory;
import org.asqatasun.processor.ProcessorFactoryImpl;
import org.asqatasun.rest.jackson.CustomJacksonModule;
import org.asqatasun.ruleimplementationloader.RuleImplementationLoaderFactory;
import org.asqatasun.ruleimplementationloader.RuleImplementationLoaderFactoryImpl;
import org.asqatasun.scenarioloader.ScenarioLoaderFactory;
import org.asqatasun.scenarioloader.ScenarioLoaderFactoryImpl;
import org.asqatasun.service.*;
import org.asqatasun.service.command.factory.AuditCommandFactory;
import org.asqatasun.service.command.factory.AuditCommandFactoryImpl;
import org.asqatasun.util.factory.DateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

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

    @Bean
    public AuditService auditService() {
        return new AuditServiceImpl();
    }

    @Bean
    public AuditServiceThreadFactory auditServiceThreadFactory() {
        return new AuditServiceThreadFactoryImpl();
    }

    @Bean
    public AuditCommandFactory auditCommandFactory() {
        return new AuditCommandFactoryImpl();
    }

    @Bean
    public AuditServiceThreadQueue auditServiceThreadQueue() {
        return new AuditServiceThreadQueueImpl();
    }
    @Bean
    public ContentLoaderService contentLoaderService() {
        return new ContentLoaderServiceImpl();
    }

    @Bean
    public ContentLoaderFactory contentLoaderFactory() {
        return new ContentLoaderFactoryImpl();
    }

    @Bean
    public DownloaderFactory downloaderFactory() {
        return new DownloaderFactoryImpl();
    }

    @Bean
    public ScenarioLoaderService scenarioLoaderService() {
        return new ScenarioLoaderServiceImpl();
    }

    @Bean
    public ScenarioLoaderFactory scenarioLoaderFactory() {
        return new ScenarioLoaderFactoryImpl();
    }

    @Bean
    public ContentAdapterService contentAdapterService() {
        return new ContentAdapterServiceImpl();
    }

    @Bean
    public ContentsAdapterFactory contentsAdapterFactory() {
        return new ContentsAdapterFactoryImpl();
    }

    @Bean
    public HTMLCleanerFactory htmlCleanerFactory(){
        return new HTMLCleanerFactoryImpl();
    }

    @Bean
    public HTMLParserFactory htmlParserFactory(){
        return new HTMLParserFactoryImpl();
    }

    @Bean
    public URLIdentifierFactory urlIdentifierFactory() {
        return new URLIdentifierFactoryImpl();
    }

    @Bean(name="adaptationListener")
    public ExternalCSSRetriever externalCSSRetriever() {
        return new ExternalCSSRetrieverImpl();
    }

    @Bean
    public CSSContentAdapterFactory cssContentAdapterFactory() {
        return new CSSContentAdapterFactoryImpl();
    }

//    @Bean(name = "xmlizeVoter")
//    public AdaptationActionVoter adaptationActionVoter() {
//        return new AdaptationActionVoterImpl();
//    }

    @Bean
    public ProcessorService processorService() {
        return new ProcessorServiceImpl();
    }

    @Bean
    public ProcessorFactory processorFactory() {
        return new ProcessorFactoryImpl();
    }

    @Bean
    public NomenclatureLoaderService nomenclatureLoaderService() {
        return new NomenclatureLoaderServiceImpl();
    }

    @Bean
    public NomenclatureLoader nomenclatureLoader() {
        return new NomenclatureLoaderImpl();
    }

    @Bean
    public RuleImplementationLoaderService ruleImplementationLoaderService() {
        return new RuleImplementationLoaderServiceImpl();
    }

    @Bean
    public RuleImplementationLoaderFactory ruleImplementationLoaderFactory() {
        return new RuleImplementationLoaderFactoryImpl();
    }

    @Bean
    public ConsolidatorService consolidatorService() {
        return new ConsolidatorServiceImpl();
    }

    @Bean
    public ConsolidatorFactory consolidatorFactory() {
        return new ConsolidatorFactoryImpl();
    }

    @Bean
    public AnalyserService analyserService() {
        return new AnalyserServiceImpl();
    }

    @Bean
    public AnalyserFactory analyserFactory() {
        return new AnalyserFactoryImpl();
    }

    @Bean
    public DateFactory dateFactory() {
        return new DateFactoryImpl();
    }
}
