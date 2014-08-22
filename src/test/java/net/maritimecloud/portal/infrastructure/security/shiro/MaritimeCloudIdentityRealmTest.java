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
package net.maritimecloud.portal.infrastructure.security.shiro;

import net.maritimecloud.portal.application.ApplicationServiceRegistry;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Christoffer Børrild
 */
public class MaritimeCloudIdentityRealmTest {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSomeMethod() {
        // ApplicationServiceRegistry.identityApplicationService();

        MaritimeCloudIdentityRealm realm = new MaritimeCloudIdentityRealm();

        String submittedPlaintextPassword = "secret";

        DefaultPasswordService passwordService = new DefaultPasswordService();
        
        ((DefaultHashService) passwordService.getHashService()).setHashAlgorithmName("SHA-512");
        
        String encryptedValue = passwordService.encryptPassword(submittedPlaintextPassword);

        System.out.println("" + encryptedValue);

    }

}
