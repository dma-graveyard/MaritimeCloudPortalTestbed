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

import java.util.Date;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

public class SmtpMailAdapter implements MailAdapter {

    private final JavaMailSender mailSender;

    public SmtpMailAdapter(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(Mail mail) {

        if (recipientIsOnIgnoreList(mail)) {
            System.out.println("Skipping send out of mail to " + mail.getRecipients());
            System.out.println("Mail Content: \n" + mail.getMessage());
            return;
        }

        mailSender.send(createMessagePreperator(mail));
    }

    private MimeMessagePreparator createMessagePreperator(Mail mail) {
        MimeMessagePreparator preparator = (MimeMessage mimeMessage) -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage);

            // TODO: remove this filter line before going to prod
            if (mail.getRecipients().contains("boerrild")) {
                message.setTo(mail.getRecipients());
            }

            message.setBcc("christoffer.boerrild@gmail.com");
            message.setFrom(new InternetAddress("maritime_cloud_portal@boerrild.dk"));
            message.setSubject(mail.getSubject());
            message.setSentDate(new Date());
            message.setText(mail.getMessage(), true);
        };
        return preparator;
    }

    private boolean recipientIsOnIgnoreList(Mail mail) {
        return recipientIsOnIgnoreList(mail.getRecipients());
    }

    private boolean recipientIsOnIgnoreList(String recipients) {
        return recipients.contains("demo.dma.dk");
    }
}
