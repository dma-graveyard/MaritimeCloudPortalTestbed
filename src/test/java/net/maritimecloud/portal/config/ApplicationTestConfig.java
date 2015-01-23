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
import net.maritimecloud.portal.domain.model.identity.UserRepository;
import net.maritimecloud.portal.infrastructure.mail.Mail;
import net.maritimecloud.portal.infrastructure.mail.MailAdapter;
import net.maritimecloud.portal.infrastructure.persistence.InMemoryUserRepository;
import org.axonframework.auditing.AuditDataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * @author Christoffer Børrild
 */
@Configuration
@Import(value = {ApplicationConfig.class})
public class ApplicationTestConfig {

    @Bean
    public UserRepository userRepository() {
        return new InMemoryUserRepository();
    }

    @Bean
    public MailAdapter mailAdapter() throws IOException {
        return (Mail mail) -> {
            System.out.println("Send (dummy mail adapter): " + mail);
        };
    }

    @Bean
    public JavaMailSender mailSender() throws IOException {
        // Dumy MailSender in order to satisfie dependencies - not currently used
        return new JavaMailSenderImpl();
    }

    @Bean
    public AuditDataProvider auditDataProvider() {
        return new IntergrationTestDummyAuditDataProvider();
    }

}
