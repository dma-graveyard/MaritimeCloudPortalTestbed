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
package net.maritimecloud.portal.infrastructure.shiro;

import net.maritimecloud.portal.security.AuthenticationException;
import net.maritimecloud.portal.security.AuthenticationUtil;
import net.maritimecloud.portal.security.UserNotLoggedInException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * ShiroAuthenticationUtil integrates to Shiros SecurityUtil.
 * <p>
 * (Corresponds to the Arctic Webs "SubjectImpl")
 * <p>
 * @author Christoffer Børrild
 */
public class ShiroAuthenticationUtil implements AuthenticationUtil {

    @Override
    public String login(String username, String password) throws AuthenticationException {
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        SecurityUtils.getSubject().login(token);
        return (String) SecurityUtils.getSubject().getPrincipal();
    }

    @Override
    public boolean isLoggedIn() {
        return SecurityUtils.getSubject().isAuthenticated();
    }

    @Override
    public String getUserId() throws UserNotLoggedInException {
        if (isLoggedIn()) {
            return (String) SecurityUtils.getSubject().getPrincipal();
        }
        throw new UserNotLoggedInException();
    }

    @Override
    public void logout() {
        SecurityUtils.getSubject().logout();
    }
}
