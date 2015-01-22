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

import net.maritimecloud.portal.config.*;
import java.io.IOException;
import javax.annotation.Resource;
import net.maritimecloud.portal.domain.model.identity.UserRepository;
import net.maritimecloud.portal.infrastructure.mail.Mail;
import net.maritimecloud.portal.infrastructure.mail.MailAdapter;
import net.maritimecloud.portal.infrastructure.mail.SmtpMailAdapter;
import net.maritimecloud.portal.infrastructure.persistence.InMemoryUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author Christoffer Børrild
 */
@Configuration
@Import(value = {ApplicationConfig.class})
public class ApplicationInMemoryDemoConfig {
    
    @Autowired
    Environment env;
    
    @Resource
    MailSender mailSender;

    @Bean
    public UserRepository userRepository() {
        return new InMemoryUserRepository();
    }

    @Bean
    public MailAdapter mailAdapter() throws IOException {
        
        if (hasSmtpPassword()) {
            return new SmtpMailAdapter((JavaMailSender) mailSender);
        } else {
            // Fallback when no smtp password has been supplied
            return (Mail mail) -> {
                System.out.println("Send (dummy mail adapter): " + mail);
            };
        }
    }

    private boolean hasSmtpPassword() {
        return env.getProperty("mail.smtp.password") != null;
    }

}
