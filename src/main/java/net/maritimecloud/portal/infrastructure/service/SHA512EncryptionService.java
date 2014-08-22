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

import net.maritimecloud.portal.domain.model.identity.EncryptionService;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.crypto.hash.DefaultHashService;

/**
 *
 * @author Christoffer Børrild
 */
public class SHA512EncryptionService implements EncryptionService {

    DefaultPasswordService passwordService;

    public SHA512EncryptionService() {
        passwordService = new DefaultPasswordService();
        ((DefaultHashService) passwordService.getHashService()).setHashAlgorithmName("SHA-512");
    }

    @Override
    public String encryptedValue(String aPlainTextValue) {
        return passwordService.encryptPassword(aPlainTextValue);
    }

    @Override
    public boolean valuesMatch(String aPlainTextValue, String aSavedEncryptedTextValue) {
        return passwordService.passwordsMatch(aPlainTextValue, aSavedEncryptedTextValue);
    }

}
