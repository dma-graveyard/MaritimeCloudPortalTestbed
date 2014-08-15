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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.io.IOException;
import javax.mail.MessagingException;
import net.maritimecloud.portal.domain.model.identity.Role;
import net.maritimecloud.portal.domain.model.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MailServiceTest {

    private static final String A_MESSAGE = "a message";
    private MailService mailService;
    private User user;
    @Mock
    private MessageComposer messageComposer;
    @Mock
    private MailAdapter mailAdapter;
    @Captor
    private ArgumentCaptor<Mail> mailCaptor;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        when(messageComposer.composeSignUpActivationMessage(any())).thenReturn(A_MESSAGE);
        mailService = new MailService(messageComposer, mailAdapter);
        user = new User("luke", "aSecret", "luke@skywalker.com", Role.USER);
    }

    @Test
    public void sendSignUpActivationMessageShouldContainUserInfo() throws MessagingException, Exception {
        // Given
        // When
        mailService.sendSignUpActivationMessage(user);
        // Then
        verify(messageComposer).composeSignUpActivationMessage(user);
        verify(mailAdapter).send(mailCaptor.capture());
        Mail mail = mailCaptor.getValue();
        assertThat(mail.getRecipients(), equalToIgnoringWhiteSpace("luke@skywalker.com"));
        assertThat(mail.getSubject(), containsString("activation"));
        assertThat(mail.getMessage(), equalTo(A_MESSAGE));
    }

    @Test
    public void sendSignUpActivationMessageShouldUseUserRecipient() throws MessagingException, Exception {
        // Given
        user = new User("anakin", "aDarkSecret", "anakin@skywalker.com", Role.USER);
        // When
        mailService.sendSignUpActivationMessage(user);
        // Then
        verify(messageComposer).composeSignUpActivationMessage(user);
        verify(mailAdapter).send(mailCaptor.capture());
        Mail mail = mailCaptor.getValue();
        assertThat(mail.getRecipients(), equalToIgnoringWhiteSpace("anakin@skywalker.com"));
    }

}
