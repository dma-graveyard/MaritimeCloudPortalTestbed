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
import javax.mail.MessagingException;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

public class MailServiceTest {

    private static final String A_MESSAGE = "a message";
    private MailService mailService;
    @Mock
    private MessageComposer messageComposer;
    @Mock
    private MailAdapter mailAdapter;
    @Captor
    private ArgumentCaptor<Mail> mailCaptor;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        mailService = new MailService(messageComposer, mailAdapter);
    }

    @Test
    public void testSendSignUpActivationMessage() throws MessagingException, Exception {
        when(messageComposer.composeSignUpActivationMessage(any(), any())).thenReturn(A_MESSAGE);
        // When
        mailService.sendSignUpActivationMessage("an@email.com", "a_username", "a_verification_code");
        // Then
        verify(messageComposer).composeSignUpActivationMessage("a_username", "a_verification_code");
        verify(mailAdapter).send(mailCaptor.capture());
        Mail mail = mailCaptor.getValue();
        assertThat(mail.getRecipients(), equalToIgnoringWhiteSpace("an@email.com"));
        assertThat(mail.getSubject(), containsString("Account"));
        assertThat(mail.getSubject(), containsString("activation"));
        assertThat(mail.getMessage(), equalTo(A_MESSAGE));
    }

//TODO: fix test        
//    @Test
//    public void testSendResetPasswordMessage() throws MessagingException, Exception {
//        when(messageComposer.composeResetPasswordMessage(any())).thenReturn(A_MESSAGE);
//        // When
//        mailService.sendResetPasswordMessage(aUser);
//        // Then
//        verify(messageComposer).composeResetPasswordMessage(aUser);
//        verify(mailAdapter).send(mailCaptor.capture());
//        Mail mail = mailCaptor.getValue();
//        assertThat(mail.getRecipients(), equalToIgnoringWhiteSpace(aUser.emailAddress()));
//        assertThat(mail.getSubject(), containsString("Password"));
//        assertThat(mail.getSubject(), containsString("reset"));
//        assertThat(mail.getMessage(), equalTo(A_MESSAGE));
//    }
    
}
