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
import net.maritimecloud.portal.config.ApplicationConfig;
import net.maritimecloud.portal.domain.model.identity.Role;
import net.maritimecloud.portal.domain.model.identity.User;
import static net.maritimecloud.portal.domain.model.identity.UserBuilder.aUser;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class VelocityMessageComposerTest {
    
    private static final User USER_LUKE   = aUser().whoIsActivated().with().name("luke").and().email("luke@skywalker.com").build();
    private static final User USER_ANAKIN = aUser().whoIsActivated().build();
    private static final User AN_INACTIVE_USER = aUser().build();
    
    private VelocityMessageComposer messageComposer;
    
    @Before
    public void setUp() throws IOException {
        messageComposer = new VelocityMessageComposer(new ApplicationConfig().velocityEngine());
    }
    
    @Test
    public void signUpActivationMessageShouldBePatchedWithUserInfo() {
        String message = messageComposer.composeSignUpActivationMessage(USER_LUKE);
        assertThat(message, containsString("Dear LUKE"));
        assertThat(message, containsString("activate your account"));
        assertThat(message, containsString("#/users/luke/activate/"+USER_LUKE.activationId()));
        assertThatAllPlaceholsersHasBeenPatched(message);
    }
    
    @Test
    public void signUpActivationMessageShouldMentionUserAnakin() {
        String message = messageComposer.composeSignUpActivationMessage(USER_ANAKIN);
        assertThat(message, containsString("Dear ANAKIN"));
    }
    
    @Test(expected = IllegalStateException.class)
    public void signUpActivationMessageShouldFailIfUserIsMissingActivationId() {
        messageComposer.composeSignUpActivationMessage(AN_INACTIVE_USER);
    }

    @Test
    public void resetPasswordMessageShouldBePatchedWithUserInfo() {
        String message = messageComposer.composeResetPasswordMessage(USER_LUKE);
        assertThat(message, containsString("Dear LUKE"));
        assertThat(message, containsString("reset"));
        assertThat(message, containsString("password"));
        assertThat(message, containsString("#/users/luke/reset/"+USER_LUKE.activationId()));
        assertThatAllPlaceholsersHasBeenPatched(message);
    }
    
    @Test
    public void resetPasswordMessageShouldMentionUserAnakin() {
        String message = messageComposer.composeResetPasswordMessage(USER_ANAKIN);
        assertThat(message, containsString("Dear ANAKIN"));
    }
    
    @Test(expected = IllegalStateException.class)
    public void resetPasswordMessageShouldFailIfUserIsMissingActivationId() {
        messageComposer.composeResetPasswordMessage(AN_INACTIVE_USER);
    }

    private void assertThatAllPlaceholsersHasBeenPatched(String message) {
        assertThat(message, not(containsString("${")));
    }
}
