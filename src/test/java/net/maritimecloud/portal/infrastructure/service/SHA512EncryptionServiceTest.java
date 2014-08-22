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
package net.maritimecloud.portal.infrastructure.service;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Please notice that the Shiro PasswordService must match this Hashing strategy
 * 
 * @author Christoffer Børrild
 */
public class SHA512EncryptionServiceTest {

    @Test
    public void encryptedValueShouldContainHashType() {
        SHA512EncryptionService service = new SHA512EncryptionService();
        String encryptedValue = service.encryptedValue("secret");
        assertThat(encryptedValue, containsString("SHA-512"));
        // TODO: increase back to 500.000 iterations when out of development phase
        assertThat(encryptedValue, containsString("1000"));
    }

    @Test
    public void encryptedValueMethodShouldNotBeIdempotent() {
        SHA512EncryptionService service = new SHA512EncryptionService();
        String encryptedValue = service.encryptedValue("sharedSecret");
        String anotherEncryptedValue = service.encryptedValue("sharedSecret");
        assertThat(anotherEncryptedValue, not(equalTo(encryptedValue)));
    }

    @Test
    public void shouldMatchEncryptedValueWithItsPlainTextOrigin() {
        SHA512EncryptionService service = new SHA512EncryptionService();
        String encryptedValue = service.encryptedValue("sharedSecret");
        assertTrue(service.valuesMatch("sharedSecret", encryptedValue));
    }

}
