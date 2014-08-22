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
package net.maritimecloud.portal.domain.model;

import net.maritimecloud.portal.domain.model.identity.PasswordService;
import net.maritimecloud.portal.application.SpringContextBasedRegistry;
import net.maritimecloud.portal.domain.model.identity.EncryptionService;
import net.maritimecloud.portal.domain.model.identity.UserRepository;
import net.maritimecloud.portal.infrastructure.service.SHA512EncryptionService;

/**
 *
 * @author Christoffer Børrild
 */
public class DomainRegistry extends SpringContextBasedRegistry {

    public static UserRepository userRepository() {
        return (UserRepository) get("userRepository");
    }

    public static EncryptionService encryptionService() {
        return new SHA512EncryptionService();
    }

    public static PasswordService passwordService() {
        return new PasswordService();
    }

}
