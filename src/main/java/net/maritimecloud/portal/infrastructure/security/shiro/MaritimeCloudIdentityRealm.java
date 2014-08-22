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
package net.maritimecloud.portal.infrastructure.security.shiro;

import net.maritimecloud.portal.application.ApplicationServiceRegistry;
import net.maritimecloud.portal.application.IdentityApplicationService;
import net.maritimecloud.portal.domain.model.identity.UnknownUserException;
import net.maritimecloud.portal.domain.model.identity.User;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

/**
 * Implementation of the Shiro Realm integrating into the MaritimeCloud identity repository.
 * <p>
 * The users in this realm uses password encryption. Shiro configuration must match this encryption algorithm.
 * <p>
 * @author Christoffer Børrild
 * @see EncryptionService
 * @see SHA512EncryptionService
 */
public class MaritimeCloudIdentityRealm extends AuthorizingRealm {

    public static final String REALM = "MaritimeCloudIdentityRealm";

    public MaritimeCloudIdentityRealm() {
        setName(REALM); // This name must match the name in the User class's getPrincipals() method
    }

    private IdentityApplicationService identityApplicationService() {
        return ApplicationServiceRegistry.identityApplicationService();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) {

        if (authenticationToken instanceof UsernamePasswordToken) {
            UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
            String username = usernamePasswordToken.getUsername();
            char[] password = usernamePasswordToken.getPassword();

            if (username == null || username.isEmpty()) {
                throw new AccountException("Null and empty usernames are not allowed by this realm!");
            }
            if (password == null || password.length == 0) {
                throw new AccountException("Null and empty passwords are not allowed by this realm!");
            }

            // Lookup user
            User user = identityApplicationService().user(username);

            if (user == null) {
                throw new UnknownAccountException("Could not authenticate with given credentials");
            }

            // Create Auth Info
            return new SimpleAuthenticationInfo(
                    user.id(),
                    user.internalAccessOnlyEncryptedPassword(),
                    ByteSource.Util.bytes(user.internalAccessOnlyEncryptionSalt()),
                    getName()
            );
        } else {
            return null;
        }
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        try {
            // Get the userId from the first principal in the collection
            long userId = (long) getAvailablePrincipal(principals);

            // Lookup user
            User user = identityApplicationService().user(userId);

            // Create AuthorizationInfo
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

            // Add all roles
            user.userRoles().all().stream().forEach((role) -> {
                info.addRole(role.name());
            });

            return info;
        } catch (UnknownUserException ex) {
            throw new UnknownAccountException("No user found in application registry");
        }
    }

}
