package net.maritimecloud.portal.config;

import javax.servlet.DispatcherType;
import net.maritimecloud.portal.*;
import net.maritimecloud.portal.application.IdentityApplicationService;
import net.maritimecloud.portal.domain.infrastructure.shiro.ShiroAuthenticationUtil;
import net.maritimecloud.portal.domain.model.identity.UserRepository;
import net.maritimecloud.portal.domain.model.security.AuthenticationUtil;
import net.maritimecloud.portal.infrastructure.persistence.JpaUserRepository;
import net.maritimecloud.portal.resource.LogService;
import net.maritimecloud.portal.resource.SimpleCORSFilter;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ComponentScan(basePackageClasses = {/* rest */ /* domain */ /* persistence */ User.class}) //todo: replace with marker interfaces
@EnableAutoConfiguration
//@EntityScan(basePackageClasses = {User.class}) //todo: replace with marker interfaces
public class ApplicationConfig {

    @Bean
    public ApplicationContextSetup applicationContextSetup() {
        // this will trigger that the application context is propagated all to registries
        return new ApplicationContextSetup();
    }

    @Bean
    public IdentityApplicationService identityApplicationService() {
        return new IdentityApplicationService();
    }

    @Bean
    public UserRepository userRepository() {
        return new JpaUserRepository();
    }

    @Bean
    public ServletRegistrationBean jerseyServlet() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new ServletContainer(), "/rest/*");
        registration.addInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, JerseyConfig.class.getName());
        return registration;
    }

    @Bean
    public FilterRegistrationBean shiroFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        ShiroFilter shiroFilter = new ShiroFilter();
        registrationBean.setFilter(shiroFilter);
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.values());
        registrationBean.setOrder(0);
        return registrationBean;
    }

    @Bean
    public ServletListenerRegistrationBean shiroServletListenerRegistrationBean() {
        ServletListenerRegistrationBean registrationBean = new ServletListenerRegistrationBean();
        EnvironmentLoaderListener environmentLoaderListener = new EnvironmentLoaderListener();
        registrationBean.setListener(environmentLoaderListener);
        registrationBean.setOrder(0);
        return registrationBean;
    }

    @Bean
    public LogService logService() {
        // TODO: introduce interface and implementations
        return new LogService();
    }

    /**
     * @return AuthenticationUtil that bridges the current request to a user in Shiro
     */
    @Bean
    public AuthenticationUtil authenticationUtil() {
        return new ShiroAuthenticationUtil();
    }

    /**
     * Make RESTful web service include CORS access control headers in its responses.
     * <p>
     * This will allow our client to be hosted elsewhere, e.g. from another port.
     * <p>
     * (See http://spring.io/guides/gs/rest-service-cors/)
     */
    @Bean
    public SimpleCORSFilter simpleCORSFilter() {
        return new SimpleCORSFilter();
    }

}
