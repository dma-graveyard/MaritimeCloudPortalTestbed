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

import java.util.List;
import javax.annotation.Resource;
import net.maritimecloud.portal.config.TestConfig;
import net.maritimecloud.portal.domain.model.identity.UserService;
import net.maritimecloud.portal.model.user.User;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfig.class)
@TransactionConfiguration(defaultRollback = true)
@Transactional
@Ignore
public class UserServiceImplTest {

    @Resource
    UserService userService;

    /**
     * Test of createUser method, of class UserServiceImpl.
     */
    @Test
    public void testCreateUser() {
        System.out.println("userService" + userService);
        User user = new User();
        //User user = new User(null, 1, "location", "password", "username");
        user.setUserName("Joe");
        User expResult = null;
        User result = userService.createUser(user);
        assertEquals(user, result);
        assertTrue(user.getId() != 0);
        System.out.println("user.getId()=" + user.getId());
    }

    /**
     * Test of createUser method, of class UserServiceImpl.
     */
    @Test
    public void testAllUsers() {
        System.out.println("userService " + userService);
        User user = new User();
        //User user = new User(null, 1, "location", "password", "username");
        user.setUserName("Joe");
        User expResult = null;

        List<User> allUsers = userService.findUsers();
        assertEquals(11, allUsers.size());
        for (User u : allUsers) {
            assertTrue(u.getId() != 0);
            System.out.println("user.getId()=" + u.getId());
        }
        
        
        allUsers = userService.findUsers();
        assertEquals(11, allUsers.size());
        for (User u : allUsers) {
            assertTrue(u.getId() != 0);
            System.out.println("user.getId()=" + u.getId());
        }
    }

}
