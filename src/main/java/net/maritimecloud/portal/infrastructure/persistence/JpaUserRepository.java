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
package net.maritimecloud.portal.infrastructure.persistence;

import java.util.List;
import net.maritimecloud.portal.domain.model.identity.UnknownUserException;
import net.maritimecloud.portal.domain.model.identity.User;
import net.maritimecloud.portal.domain.model.identity.UserRepository;

/**
 * @author Christoffer Børrild
 */
public class JpaUserRepository implements UserRepository {

    @Override
    public void add(User aUser) {
        throw new UnsupportedOperationException("JpaUserRepository is not supported yet.");
    }

    @Override
    public void remove(User aUser) {
        throw new UnsupportedOperationException("JpaUserRepository is not supported yet.");
    }

    @Override
    public User userWithUsername(String aUsername) {
        throw new UnsupportedOperationException("JpaUserRepository is not supported yet.");
    }

    @Override
    public List<User> usersWithUsernameMatching(String usernamePattern) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public User get(long userId) throws UnknownUserException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
