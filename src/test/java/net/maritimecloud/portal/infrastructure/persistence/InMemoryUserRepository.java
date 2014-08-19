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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.maritimecloud.portal.domain.model.identity.Role;
import net.maritimecloud.portal.domain.model.identity.UnknownUserException;
import net.maritimecloud.portal.domain.model.identity.User;
import net.maritimecloud.portal.domain.model.identity.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christoffer Børrild
 */
public class InMemoryUserRepository implements UserRepository, CleanableStore {

    private final Map<String, User> repository;

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryUserRepository.class);

    public InMemoryUserRepository() {
        LOG.warn("\n\n Using InMemoryUserRepository !!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n");
        repository = new HashMap<>();
        initWithDummyUsers();
    }

    private void initWithDummyUsers() {
        add(activate(new User("admin", "test", "admin@dma.dk", Role.ADMIN, Role.USER)));
        add(activate(new User("Tintin", "test", "tintin@dma.org")));
        add(activate(new User("Haddock", "test", "hadock@dma.org")));
    }

    @Override
    public void add(User aUser) {
        String key = keyOf(aUser);
        if (repository().containsKey(key)) {
            throw new IllegalStateException("Duplicate key.");
        }
        repository().put(key, aUser);
    }

    @Override
    public void remove(User aUser) {
        String key = keyOf(aUser);
        repository().remove(key);
    }

    @Override
    public User userWithUsername(String aUsername) {
        for (User user : repository().values()) {
            if (user.username().equalsIgnoreCase(aUsername)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> usersWithUsernameMatching(String usernamePattern) {
        List<User> users = new ArrayList<>();
        repository().values().stream().filter((user) -> (user.username().matches(".*"+usernamePattern+".*"))).forEach((user) -> {
            users.add(user);
        });
        return users;
    }

//    @Override
//    public User userFromAuthenticCredentials(String aUsername, String anEncryptedPassword) {
//
//        for (User get : repository().values()) {
//            if (get.username().equals(aUsername)) {
//                if (get.internalAccessOnlyEncryptedPassword().equals(anEncryptedPassword)) {
//                    return get;
//                }
//            }
//        }
//        return null;
//    }
    
    @Override
    public void clean() {
        repository().clear();
    }

    private String keyOf(User aUser) {
        String key = aUser.username();
        return key;
    }

    private Map<String, User> repository() {
        return repository;
    }

    @Override
    public User get(long userId) throws UnknownUserException {
        for (User user : repository().values()) {
            if(user.id() == userId)
                return user;
        }
        throw new UnknownUserException(userId);
    }

    private User activate(User u) {
        u.generateActivationId();
        u.activate(u.activationId());
        return u;
    }

}
