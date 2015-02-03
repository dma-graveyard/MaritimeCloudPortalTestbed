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
package net.maritimecloud.identityregistry.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Christoffer Børrild
 */
public class UserRoles {

    Set<Role> roles = new HashSet<>();

    public UserRoles(Role... roles) {
        //assertNotNull(roles);
        if (roles == null) {
            throw new IllegalStateException("Roles cannot be null");
        }
        if (roles.length == 0) {
            throw new IllegalStateException("Roles cannot be empty. Must at least contain one role!");
        }

        this.roles.addAll(Arrays.asList(roles));
    }

    public Set<Role> all() {
        return roles;
    }

}
