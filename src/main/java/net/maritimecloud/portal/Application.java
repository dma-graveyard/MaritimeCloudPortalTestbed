/* Copyright (c) 2011 Danish Maritime Authority.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.maritimecloud.portal;

//import net.maritimecloud.portal.config.ApplicationConfig;
import javax.annotation.Resource;
import net.maritimecloud.portal.config.ApplicationInMemoryDemoConfig;
import org.axonframework.eventhandling.replay.ReplayingCluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Christoffer Børrild
 */
@Configuration
//@ComponentScan(basePackageClasses = {UserResource.class})
@EnableAutoConfiguration
//@Import(value =  ApplicationConfig.class)
@Import(value = ApplicationInMemoryDemoConfig.class)
public class Application /*extends SpringBootServletInitializer*/ {

    private final static Logger LOG = LoggerFactory.getLogger(Application.class);

    @Resource
    ReplayingCluster replayingCluster;

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);

        rebuild(applicationContext);

    }

    public static void rebuild(ConfigurableApplicationContext applicationContext) throws BeansException {
        
        LOG.info("Rebuilding view repositories from events ...");
        
        ReplayingCluster replayingCluster = applicationContext.getBean(ReplayingCluster.class);
        
        long start = System.currentTimeMillis();
        replayingCluster.startReplay();
        long stop = System.currentTimeMillis();
        
        LOG.info("Completed in {} milliseconds!", stop - start);
    }

//    @Bean
//    public ServletRegistrationBean jerseyServlet() {
//        ServletRegistrationBean registration = new ServletRegistrationBean(new ServletContainer(), "/rest/*");
//        registration.addInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, JerseyConfig.class.getName());
//        return registration;
//    }    
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        return application.sources(Application.class);
//    }
}
