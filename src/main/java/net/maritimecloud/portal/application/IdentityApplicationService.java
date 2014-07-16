/* Copyright (c) 2011 Danish Maritime Authority.
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
package net.maritimecloud.portal.application;

import net.maritimecloud.portal.domain.model.DomainRegistry;
import net.maritimecloud.portal.domain.model.identity.UnknownUserException;
import java.util.List;
import javax.transaction.Transactional;
import net.maritimecloud.portal.domain.model.identity.User;
import net.maritimecloud.portal.domain.model.identity.UserRepository;

/**
 * @author Christoffer Børrild
 */
public class IdentityApplicationService {

    private UserRepository userRepository() {
        return DomainRegistry.userRepository();
    }

    @Transactional
    public User registerUser(String username, String password, String emailAddress) {
        // TODO HACK: Using hardcoded password
        User newUser = new User(username, "password", emailAddress);
        userRepository().add(newUser);
        return newUser;
    }

    @Transactional//(readOnly = true)
    public User user(String aUsername) {
        return userRepository().userWithUsername(aUsername);
    }

    public List<User> usersWithUsernameMatching(String usernamePattern) {
        return userRepository().usersWithUsernameMatching(usernamePattern);
    }

    public User user(long userId) throws UnknownUserException {
        return userRepository().get(userId);
    }

}
