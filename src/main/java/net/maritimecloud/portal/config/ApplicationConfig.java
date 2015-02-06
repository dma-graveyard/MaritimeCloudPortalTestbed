package net.maritimecloud.portal.config;

import java.io.IOException;
import java.util.Properties;
import javax.annotation.Resource;
import javax.servlet.DispatcherType;
import net.maritimecloud.portal.*;
import net.maritimecloud.portal.infrastructure.shiro.ShiroAuthenticationUtil;
import net.maritimecloud.identityregistry.domain.EncryptionService;
import net.maritimecloud.portal.security.AuthenticationUtil;
import net.maritimecloud.portal.infrastructure.mail.MailAdapter;
import net.maritimecloud.portal.infrastructure.mail.MailService;
import net.maritimecloud.portal.infrastructure.mail.SmtpMailAdapter;
import net.maritimecloud.portal.infrastructure.mail.VelocityMessageComposer;
import net.maritimecloud.portal.infrastructure.service.SHA512EncryptionService;
import net.maritimecloud.portal.resource.LogService;
import net.maritimecloud.common.resource.SimpleCORSFilter;
import net.maritimecloud.identityregistry.domain.IdentityService;
import net.maritimecloud.serviceregistry.domain.service.AliasService;
import net.maritimecloud.serviceregistry.infrastructure.service.JpaAliasService;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.apache.velocity.app.VelocityEngine;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;

@Configuration
@Import(value = {AxonConfig.class, JpaConfig.class})
public class ApplicationConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationConfig.class);

    @Value(value = "${baseUrl:localhost:8080}")
    private String baseUrl;

    @Value(value = "${spaUrl:${baseUrl}/app/index.html}")
    private String spaUrl;

    @Resource
    private Environment env;

    @Resource
    private ApplicationContextSetup applicationContextSetup;

    @Resource
    private JavaMailSender mailSender;

    @Bean
    public ApplicationContextSetup applicationContextSetup() {
        // this will trigger that the application context is propagated to all registries
        return new ApplicationContextSetup();
    }

    @Bean
    public ServletRegistrationBean jerseyServlet() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new ServletContainer(), "/rest/*");
        registration.addInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, JerseyConfig.class.getName());
        return registration;
    }

    @Bean
    public EncryptionService encryptionService() {
        return new SHA512EncryptionService();
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
     * (See http://spring.io/guides/gs/rest-service-cors/ )
     * <p>
     * @return a SimpleCORSFilter
     */
    @Bean
    public SimpleCORSFilter simpleCORSFilter() {
        return new SimpleCORSFilter();
    }

    @Bean
    public VelocityEngine velocityEngine() throws IOException {
        Properties props = new Properties();
        props.put("resource.loader", "class");
        props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        VelocityEngineFactoryBean velocityEngineFactoryBean = new VelocityEngineFactoryBean();
        velocityEngineFactoryBean.setVelocityProperties(props);
        VelocityEngine velocityEngine = velocityEngineFactoryBean.createVelocityEngine();
        return velocityEngine;
    }

    @Bean
    public MailAdapter mailAdapter() throws IOException {
        LOG.info("Using Mail sender with host '" + env.getProperty("spring.mail.host") + "' and user '" + env.getProperty("spring.mail.user") + "'");
        LOG.info("************* Links to maritime cloud use this base URL: " + baseUrl);
        LOG.info("************* Links to maritime cloud use this spa URL: " + spaUrl);
        return new SmtpMailAdapter(mailSender);
    }

    @Bean
    public MailService mailService() throws IOException {
        return new MailService(new VelocityMessageComposer(velocityEngine(), spaUrl), mailAdapter());
    }

    @Bean
    public AliasService aliasService() throws IOException {
        return new JpaAliasService();
    }

    @Bean
    public IdentityService identityService() throws IOException {
        return new IdentityService();
    }

}
