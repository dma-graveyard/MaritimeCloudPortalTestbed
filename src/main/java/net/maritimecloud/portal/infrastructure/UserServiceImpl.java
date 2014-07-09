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
package net.maritimecloud.portal.infrastructure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import net.maritimecloud.portal.domain.model.identity.UserService;
import net.maritimecloud.portal.model.user.User;
import org.springframework.stereotype.Component;

/**
 * @author Christoffer Børrild
 * @deprecated
 */
@Component
public class UserServiceImpl implements UserService {

    @Resource
    private EntityManager entityManager;
    
    private Map<String, User> users = new HashMap<>();
    private static long id = 0;

    @Override
    public User createUser(User user) {
        entityManager.persist(user);
        entityManager.flush();
//        users.put(user.getUserName(), user);
        System.out.println("Created user " + user.getId());
        return user;
    }

    @Override
    public User findUserWithId(long id) {
        initDummyUsers();
        return entityManager.find(User.class, id);
//        return users.get("User"+id);
    }

    @Override
    public List<User> findUsers() {
        initDummyUsers();
        System.out.println("findUsers()");
        return entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
//        return new LinkedList<>(users.values());
    }

    private static boolean initialized = false;

    private void initDummyUsers() {
        if (initialized) {
            return;
        }
        createAndPersistUser("test");
        for (int i = 0; i < 10; i++) {
            createAndPersistUser("User" + i);
        }
        initialized = true;
    }

    private User createAndPersistUser(String userName) {
        User user = UserFactory.create(userName);
        createUser(user);
        return user;
    }
}
