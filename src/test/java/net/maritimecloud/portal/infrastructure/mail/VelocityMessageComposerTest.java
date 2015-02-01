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
import net.maritimecloud.identityregistry.command.user.UserId;
import net.maritimecloud.portal.config.ApplicationConfig;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class VelocityMessageComposerTest {
    
    private static final String AN_EMAIL_ADDRESS_VERIFICATION_CODE = "AN_EMAIL_ADDRESS_VERIFICATION_CODE";
    
    private VelocityMessageComposer messageComposer;
    
    @Before
    public void setUp() throws IOException {
        messageComposer = new VelocityMessageComposer(new ApplicationConfig().velocityEngine());
        
    }
    
    @Test
    public void signUpActivationMessageShouldBePatchedWithUserInfo() {
        String message = messageComposer.composeSignUpActivationMessage("luke", AN_EMAIL_ADDRESS_VERIFICATION_CODE);
        assertThat(message, containsString("Dear LUKE"));
        assertThat(message, containsString("activate your account"));
        assertThat(message, containsString("#/users/luke/activate/"+AN_EMAIL_ADDRESS_VERIFICATION_CODE));
        assertThatAllPlaceholsersHasBeenPatched(message);
    }
    
    @Test
    public void signUpActivationMessageShouldMentionUserAnakin() {
        String message = messageComposer.composeSignUpActivationMessage("anakin", AN_EMAIL_ADDRESS_VERIFICATION_CODE);
        assertThat(message, containsString("Dear ANAKIN"));
    }
    
    @Test(expected = IllegalStateException.class)
    public void signUpActivationMessageShouldFailIfUserIsMissingActivationId() {
        messageComposer.composeSignUpActivationMessage("anakin", null);
    }

    @Test
    public void resetPasswordMessageShouldBePatchedWithUserInfo() {
        String message = messageComposer.composeResetPasswordMessage("luke", A_RESET_PASSWORD_KEY);
        assertThat(message, containsString("Dear LUKE"));
        assertThat(message, containsString("reset"));
        assertThat(message, containsString("password"));
        assertThat(message, containsString("#/users/luke/reset/"+A_RESET_PASSWORD_KEY));
        assertThatAllPlaceholsersHasBeenPatched(message);
    }
    private static final String A_RESET_PASSWORD_KEY = "reset_password_key";
    
    @Test
    public void resetPasswordMessageShouldMentionUserAnakin() {
        String message = messageComposer.composeResetPasswordMessage("anakin", A_RESET_PASSWORD_KEY);
        assertThat(message, containsString("Dear ANAKIN"));
    }
    
    @Test(expected = IllegalStateException.class)
    public void resetPasswordMessageShouldFailIfUserIsMissingActivationId() {
        messageComposer.composeResetPasswordMessage("a username", null);
    }

    private void assertThatAllPlaceholsersHasBeenPatched(String message) {
        assertThat(message, not(containsString("${")));
    }
}
