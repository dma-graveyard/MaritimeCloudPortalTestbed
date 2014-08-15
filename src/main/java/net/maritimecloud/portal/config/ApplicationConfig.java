package net.maritimecloud.portal.config;

import java.io.IOException;
import java.util.Properties;
import javax.servlet.DispatcherType;
import net.maritimecloud.portal.*;
import net.maritimecloud.portal.application.IdentityApplicationService;
import net.maritimecloud.portal.domain.infrastructure.shiro.ShiroAuthenticationUtil;
import net.maritimecloud.portal.domain.model.identity.UserRepository;
import net.maritimecloud.portal.domain.model.security.AuthenticationUtil;
import net.maritimecloud.portal.infrastructure.mail.MailAdapter;
import net.maritimecloud.portal.infrastructure.mail.MailService;
import net.maritimecloud.portal.infrastructure.mail.SmtpMailAdapter;
import net.maritimecloud.portal.infrastructure.mail.VelocityMessageComposer;
import net.maritimecloud.portal.infrastructure.persistence.JpaUserRepository;
import net.maritimecloud.portal.resource.LogService;
import net.maritimecloud.portal.resource.SimpleCORSFilter;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.apache.velocity.app.VelocityEngine;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;

@Configuration
//@ComponentScan(basePackageClasses = {/* rest */ /* domain */ /* persistence */ User.class}) //todo: replace with marker interfaces
@EnableAutoConfiguration
//@EntityScan(basePackageClasses = {User.class}) //todo: replace with marker interfaces
public class ApplicationConfig {

    private static final Logger LOG = LoggerFactory.getLogger(LogService.class);

    @Autowired
    Environment env;

    @Bean
    public ApplicationContextSetup applicationContextSetup() {
        // this will trigger that the application context is propagated to all registries
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

    @Bean
    public JavaMailSender mailSender() throws IOException {
        Properties props = filterProperties(loadPropertiesFromClasspath("/application.properties"), "mail.");
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setJavaMailProperties(props);
        mailSender.setPassword(getMailPassword(props));
        return mailSender;
    }

    /**
     * <code>
     * NOTE (for Mac Os X users): 
     * Set the system environment variable from a terminal with 'launchctl':
     * --------------------------
     *   > launchctl
     *   >> setenv mail.smtp.password mySecret
     *   >> exit
     * --------------------------
     * (remember to restart your IDE and terminals for the change to be applied)
     * <code>
     */
    private String getMailPassword(Properties props) {
        return unObfuscate(env.getProperty("mail.smtp.password", props.getProperty("mail.smtp.secret")));
    }

    private Properties loadPropertiesFromClasspath(String file) throws IOException {
        LOG.info("Loading properties from classpath: " + file);
        Properties props = new Properties();
        props.load(this.getClass().getResourceAsStream(file));
        return props;
    }

    private Properties filterProperties(Properties props, String filterString) {
        Properties mailProps = new Properties();
        props.forEach((key, value) -> {
            if (key.toString().startsWith(filterString)) {
                mailProps.put(key, value);
            }
        });
        if (LOG.isDebugEnabled()) {
            props.list(System.out);
        }
        return mailProps;
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
        return new SmtpMailAdapter(mailSender());
    }

    @Bean
    public MailService mailService() throws IOException {
        return new MailService(new VelocityMessageComposer(velocityEngine()), mailAdapter());
    }

    private String unObfuscate(String property) {
        // TODO: not using any obfuscation, please implement. Use e.g. Acegy
        return property;
    }

}
