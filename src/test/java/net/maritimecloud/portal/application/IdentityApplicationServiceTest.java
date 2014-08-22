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
package net.maritimecloud.portal.application;

import net.maritimecloud.portal.domain.model.identity.User;
import net.maritimecloud.portal.domain.model.identity.UserRepository;
import net.maritimecloud.portal.infrastructure.mail.MailService;
import net.maritimecloud.portal.resource.LogService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import org.springframework.context.ApplicationContext;

/**
 * @author Christoffer Børrild
 */
public class IdentityApplicationServiceTest {

    @Mock
    User aUser;

    @Mock
    MailService mailService;

    @Mock
    UserRepository userRepository;

    @Mock
    LogService logService;

    @Mock
    ApplicationContext applicationContext;

    IdentityApplicationService identityApplicationService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Setup emulated spring application context
        new SpringContextBasedRegistry().setApplicationContext(applicationContext);
        when(applicationContext.getBean("userRepository")).thenReturn(userRepository);
        when(applicationContext.getBean("logService")).thenReturn(logService);
        when(applicationContext.getBean("mailService")).thenReturn(mailService);

        identityApplicationService = new IdentityApplicationService();
    }

    @Test
    public void shouldSendResetPasswordMessageWhenEmailIsKnown() {

        // Given an email of an existing user
        String email = "a@b.c";
        when(userRepository.userWithEmail(email)).thenReturn(aUser);
        // When I call the method
        identityApplicationService.sendResetPasswordMessage(email);
        // Then a confirmation id is prepared for the user
        verify(aUser).generateActivationId();
        // And an email is sent out
        verify(mailService).sendResetPasswordMessage(aUser);

    }

    @Test
    public void sendResetPasswordMessageShouldFailWhenEmailIsUnknown() {

        // Given an unknown email
        String email = "a@b.c";
        when(userRepository.userWithEmail(email)).thenReturn(null);
        // When I call the method
        identityApplicationService.sendResetPasswordMessage(email);
        // Then an error message is logged
        verify(logService).sendResetPasswordMessageFailedUserOfEmailNotFound(email);
        // And nothing else happens
        verify(aUser, never()).generateActivationId();
        verify(mailService, never()).sendResetPasswordMessage(aUser);

    }

    @Test
    public void resetPasswordShouldUpdateUserPassword() {

        // Given a username, a veirificationcode and a new password
        String aUsername = "aUser";
        String aVerificationCode = "aCode";
        String aNewPassword = "aNewPassword";

        when(userRepository.userWithUsername(aUsername)).thenReturn(aUser);
        
        // When I call the method
        identityApplicationService.resetPassword(aUsername, aVerificationCode, aNewPassword);
        
        // Then an error message is logged
        verify(aUser).changePassword(aVerificationCode, aNewPassword);

    }
    
}
