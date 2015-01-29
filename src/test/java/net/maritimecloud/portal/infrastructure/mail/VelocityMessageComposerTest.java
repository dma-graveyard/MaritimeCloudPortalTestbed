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
import net.maritimecloud.identityregistry.command.api.ResetPasswordKeyGenerated;
import net.maritimecloud.identityregistry.command.api.UserRegistered;
import net.maritimecloud.identityregistry.command.user.UserId;
import net.maritimecloud.portal.config.ApplicationConfig;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class VelocityMessageComposerTest {
    
    private static final UserId someUserId = null;
    
    private VelocityMessageComposer messageComposer;
    
    @Before
    public void setUp() throws IOException {
        messageComposer = new VelocityMessageComposer(new ApplicationConfig().velocityEngine());
        
    }
    
    @Test
    public void signUpActivationMessageShouldBePatchedWithUserInfo() {
        UserRegistered event = new UserRegistered(someUserId, "luke", "luke@skywalker.com", "a secret", "AN_EMAIL_ADDRESS_VERIFICATION_CODE");
        String message = messageComposer.composeSignUpActivationMessage(event);
        assertThat(message, containsString("Dear LUKE"));
        assertThat(message, containsString("activate your account"));
        assertThat(message, containsString("#/users/luke/activate/"+event.getEmailVerificationCode()));
        assertThatAllPlaceholsersHasBeenPatched(message);
    }
    
    @Test
    public void signUpActivationMessageShouldMentionUserAnakin() {
        UserRegistered event = new UserRegistered(someUserId, "anakin", "anakin@skywalker.com", "a secret", "AN_EMAIL_ADDRESS_VERIFICATION_CODE");
        String message = messageComposer.composeSignUpActivationMessage(event);
        assertThat(message, containsString("Dear ANAKIN"));
    }
    
    @Test(expected = IllegalStateException.class)
    public void signUpActivationMessageShouldFailIfUserIsMissingActivationId() {
        UserRegistered eventWithoutConfirmationCode = new UserRegistered(someUserId, "anakin", "anakin@skywalker.com", "a secret", null);
        messageComposer.composeSignUpActivationMessage(eventWithoutConfirmationCode);
    }

    @Test
    public void resetPasswordMessageShouldBePatchedWithUserInfo() {
        ResetPasswordKeyGenerated event = new ResetPasswordKeyGenerated(someUserId, "luke", "luke@skywalker.com", "reset_password_key");
        String message = messageComposer.composeResetPasswordMessage(event);
        assertThat(message, containsString("Dear LUKE"));
        assertThat(message, containsString("reset"));
        assertThat(message, containsString("password"));
        assertThat(message, containsString("#/users/luke/reset/"+event.getResetPasswordKey()));
        assertThatAllPlaceholsersHasBeenPatched(message);
    }
    
    @Test
    public void resetPasswordMessageShouldMentionUserAnakin() {
        ResetPasswordKeyGenerated event = new ResetPasswordKeyGenerated(someUserId, "anakin", "anakin@skywalker.com", "reset_password_key");
        String message = messageComposer.composeResetPasswordMessage(event);
        assertThat(message, containsString("Dear ANAKIN"));
    }
    
    @Test(expected = IllegalStateException.class)
    public void resetPasswordMessageShouldFailIfUserIsMissingActivationId() {
        ResetPasswordKeyGenerated event = new ResetPasswordKeyGenerated(someUserId, "anakin", "anakin@skywalker.com", null);
        messageComposer.composeResetPasswordMessage(event);
    }

    private void assertThatAllPlaceholsersHasBeenPatched(String message) {
        assertThat(message, not(containsString("${")));
    }
}
