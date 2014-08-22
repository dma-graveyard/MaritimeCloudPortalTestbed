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
package net.maritimecloud.portal.domain.model.identity;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class UserTest {

    User aUser;

    @Before
    public void setUp() {
        aUser = UserBuilder.aUser().build();
    }

    @Test
    public void shouldInitiallyBeInactive() {
        assertFalse(aUser.isActive());
    }

    @Test
    public void shouldBeAbleToActivateAccount() {

        // Given a new user
        // Then activationId is null 
        // (...because in future we might activate users differently hence we will not store unused ids)
        assertNull(aUser.activationId());
        // And user account is not activated
        assertFalse(aUser.isActive());

        // When we generate a new activation id
        aUser.generateActivationId();
        // Then activationId is no longer null 
        assertNotNull(aUser.activationId());

        // When we use the id to activate the account
        aUser.activate(aUser.activationId());
        // Then the user account is active
        assertTrue(aUser.isActive());
    }

    @Test
    public void shouldNotBeAbleToActivateAccountWithWrongKey() {

        // Given a new user ready to activate
        assertFalse(aUser.isActive());
        aUser.generateActivationId();
        assertNotNull(aUser.activationId());

        // When we use a wrong id to activate the account
        aUser.activate(aUser.activationId() + "WRONG");
        // Then the user account is still inactive
        assertFalse(aUser.isActive());
    }

    @Test
    public void shouldBeAbleToChangePassword() {

        // Given an activated user with an existing password
        aUser = UserBuilder.aUser().whoIsActivated().and().with().password("aSecret").build();
        String originalEncryptedPassword = aUser.password();
        // When I change the password
        aUser.changePassword("aSecret", "aNewSecret");
        // Then the password has changed
        String newEncryptedPassword = aUser.password();
        assertNotEquals("Encrypted password should have been changed", originalEncryptedPassword, newEncryptedPassword);

    }

    @Test(expected = IllegalArgumentException.class)
    public void changePasswordWithInvalidPasswordShouldFail() {

        // Given an activated user with an existing password
        aUser = UserBuilder.aUser().whoIsActivated().and().with().password("aSecret").build();
        // When I try to change the password
        aUser.changePassword("aWrongSecret", "aNewSecret");
        // Then it fails
        
    }

    @Test
    public void shouldAllowToChangePasswordWithConfirmationCode() {

        // Given an activated user with an existing password
        aUser = UserBuilder.aUser().whoIsActivated().and().with().password("aSecret").build();
        String originalEncryptedPassword = aUser.password();
        // And an activationId used for confirmation
        String confirmationId = aUser.activationId();
        // When I change the password and supply the confirmationId as original password
        aUser.changePassword(confirmationId, "aNewSecret");
        // Then the password has changed
        String newEncryptedPassword = aUser.password();
        assertNotEquals("Encrypted password should have been changed", originalEncryptedPassword, newEncryptedPassword);
        
    }

}
