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

import net.maritimecloud.portal.model.user.User;

/**
 * @author Christoffer Børrild
 */
public class UserFactory {

    public static User create(String userName) {
        User user = new User();
        user.setUserName(userName);
        user.setPassword("pazzwørd");
        user.setLocation("LA:545325.9876.9876-LO:987695432.9.987677");
        return user;
    }

    static User create(int i, String userName) {
        User user = create(userName);
        return user;
    }
}
