package net.maritimecloud.portal;

import net.maritimecloud.portal.config.ApplicationConfig;
import net.maritimecloud.portal.config.TestConfig;
import net.maritimecloud.portal.domain.model.identity.UserRepository;
import net.maritimecloud.portal.infrastructure.persistence.InMemoryUserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration
@Import(value = {ApplicationConfig.class, TestConfig.class})
public class ApplicationInMemory /*extends SpringBootServletInitializer*/ {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationInMemory.class, args);
    }

    @Bean
    public UserRepository userRepository() {
        return new InMemoryUserRepository();
    }

}
