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
package net.maritimecloud.portal.config;

import java.io.IOException;
import javax.annotation.Resource;
import net.maritimecloud.portal.infrastructure.mail.Mail;
import net.maritimecloud.portal.infrastructure.mail.MailAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author Christoffer Børrild
 */
@Configuration
@Import(value = {ApplicationConfig.class})
public class ApplicationInMemoryDemoConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationInMemoryDemoConfig.class);

    @Resource
    Environment env;

    @Resource
    JavaMailSender mailSender;

    @Bean
    @ConditionalOnProperty(prefix = "spring.mail", name = "password", havingValue = "false", matchIfMissing = true)
    public MailAdapter mailAdapter() throws IOException {
        LOG.info("No Mail sender configured - echoing to console. Make sure spring.mail.password is set "/*+ env.getProperty("spring.mail.password")*/);
        return (Mail mail) -> {
            System.out.println("Send (dummy mail adapter): " + mail);
        };
    }

}
