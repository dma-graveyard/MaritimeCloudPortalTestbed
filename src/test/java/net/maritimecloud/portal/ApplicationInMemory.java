package net.maritimecloud.portal;

import net.maritimecloud.portal.config.ApplicationTestConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration
@Import(value = {ApplicationTestConfig.class})
public class ApplicationInMemory {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationInMemory.class, args);
    }

}
