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
package net.maritimecloud.identityregistry.resource;

import java.util.Date;
import net.maritimecloud.identityregistry.query.UserEntry;
import net.maritimecloud.identityregistry.query.internal.InternalUserEntry;

/**
 *
 * @author Christoffer Børrild
 */
public class UserBuilder {

    private String name;
    private String password;
    private String email;
    private boolean isActivated;

    public static UserBuilder aUser() {
        return new UserBuilder();
    }

    public UserBuilder with() {
        return this;
    }

    public UserBuilder and() {
        return this;
    }

    public UserBuilder name(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder password(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder whoIsActivated() {
        isActivated = true;
        return this;
    }

    public UserEntry build() {
        UserEntry u = new UserEntry();
        u.setUsername(name != null ? name : "anakin");
        //u.setPassword(password != null ? password : "aDarkSecret");
        u.setEmailAddress(email != null ? email : "anakin@skywalker.ds");
        u.setIsActivated(isActivated);
        u.setActivatedSince(new Date(0));
        return u;
    }

    public InternalUserEntry buildInternal() {
        InternalUserEntry u = new InternalUserEntry();
        u.setUsername(name != null ? name : "anakin");
        u.setEncryptedPassword(password != null ? password : "aDarkSecret");
        u.setEmailAddress(email != null ? email : "anakin@skywalker.ds");
        u.setActivated(isActivated);
        return u;
    }

}
