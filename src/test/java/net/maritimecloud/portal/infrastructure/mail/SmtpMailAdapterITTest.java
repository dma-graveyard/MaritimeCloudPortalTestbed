/* Copyright 2014 Danish Maritime Authority.
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
package net.maritimecloud.portal.infrastructure.mail;

import java.io.IOException;
import java.util.Date;
import net.maritimecloud.portal.config.ApplicationTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Integration test that will send out a test email (if activated).
 * 
 * @author Christoffer Børrild
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestConfig.class})
public class SmtpMailAdapterITTest {

    private static final Logger LOG = LoggerFactory.getLogger(SmtpMailAdapterITTest.class);

    @Autowired
    MailAdapter mailAdapter;

    @Test
    public void shouldSendTestMailToDeveloperIfConfigured() throws IOException {

        if (System.getenv("mail.smtp.password") == null) {
            LOG.warn("SmtpMailAdapter test skipped - (because 'mail.smtp.password' is not set)");
            //echoEnvironmentVariables();
            return;
        }

        LOG.warn("Sending a test-email (because 'mail.smtp.password' is set)");

        Mail mail = new Mail("", "Maritime Cloud Portal test mail", "Unit test build at " + new Date());
        mailAdapter.send(mail);

    }

    private void echoEnvironmentVariables() {
        System.getenv().forEach((k, v) -> System.out.println(k + "=" + v));
    }

}
