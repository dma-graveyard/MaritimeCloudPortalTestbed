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

public class UserTest {
    
    @Test
    public void shouldInitiallyBeInactive() {
        User aUser = UserBuilder.aUser().build();
        assertFalse(aUser.isActive());
    }
    
    @Test
    public void shouldBeAbleToActivateAccount() {
    
        // Given a new user
        User aUser = UserBuilder.aUser().build();
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
        User aUser = UserBuilder.aUser().build();
        assertFalse(aUser.isActive());
        aUser.generateActivationId();
        assertNotNull(aUser.activationId());

        // When we use a wrong id to activate the account
        aUser.activate(aUser.activationId()+"WRONG");
        // Then the user account is still inactive
        assertFalse(aUser.isActive());
    }
    
}
