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

package net.maritimecloud.portal.resource;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Christoffer Børrild
 */
public class AuthenticationResourceTest extends ResourceTest {
    
        AuthenticationResource authenticationResource;
    
    @Before
    public void setup(){
         authenticationResource = new AuthenticationResource();
    }
    
    @Test
    public void testSendResetPasswordInstructions() {
        
        // Given a known user
        //final User aUser = UserBuilder.aUser().build();
        //userRepository().add(aUser);
        
        // When user requests to get reset password instrcutions sent 
        //AuthenticationResource.CredentialsDTO credentials = new AuthenticationResource.CredentialsDTO();
        //credentials.setEmailAddress(aUser.emailAddress());

        //authenticationResource.sendResetPasswordInstructions(credentials);
        
        // Then an email is sent 
        // (but we do not know how to test that from here)
        
    }
    
    @Test
    public void testResetPassword() {

//TODO: rebuild with json command!
        
        //// Given a known user
        //final User aUser = UserBuilder.aUser().with().password("aSecret").whoIsActivated().build();
        //userRepository().add(aUser);
        //String originalEncryptedPassword = aUser.internalAccessOnlyEncryptedPassword();
        //
        //// When user requests to reset password
        //AuthenticationResource.CredentialsDTO credentials = new AuthenticationResource.CredentialsDTO();
        //credentials.setUsername(aUser.username());
        //credentials.setVerificationId(aUser.activationId());
        //credentials.setPassword("aNewSecret");
        //
        //authenticationResource.resetPassword(credentials);
        //
        //// Then password has changed
        //assertThat(aUser.internalAccessOnlyEncryptedPassword(), not(equals(originalEncryptedPassword)));
        
    }

    
}
