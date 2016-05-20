package org.asqatasun.webapp;

/**
 * Created by meskoj on 19/05/16.
 */
import org.asqatasun.persistence.config.PersistenceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

@SpringBootApplication
//@EnableAutoConfiguration
@ImportResource("classpath:conf/tgol-service.xml")
public class AsqatasunWebappApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AsqatasunWebappApplication.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(AsqatasunWebappApplication.class, args);
    }

}
