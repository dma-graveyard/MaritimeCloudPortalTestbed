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
package net.maritimecloud.portal.domain.model.security;

/**
 * The AuthenticationUtil is a utility that represents the Identity that is currently accessing a resource and offers authentication
 * functionality.
 * <p>
 * A Subject can be logged in and out. To log in, the User must provide a username and a password. While logged in, the Subject can provide
 * the the application specific unique userId of the user that the Subject represents.
 * <p>
 * @author Christoffer Børrild
 */
public interface AuthenticationUtil {

    /**
     * Logins the current user using the supplied username and password.
     * <p>
     * @param username
     * @param password
     * @return the application specific unique userId.
     * @throws net.maritimecloud.portal.domain.model.security.AuthenticationException
     */
    long login(String username, String password) throws AuthenticationException;

    /**
     * @return true if the current user is logged in
     */
    boolean isLoggedIn();

    /**
     * @return the application specific unique userId of the currently logged in user
     * @throws UserNotLoggedInException if the current user is anonymous
     */
    long getUserId() throws UserNotLoggedInException;

    /**
     *
     */
    void logout();

}
