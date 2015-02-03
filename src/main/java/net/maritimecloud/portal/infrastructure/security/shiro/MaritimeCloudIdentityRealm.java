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

import net.maritimecloud.identityregistry.domain.Identity;
import net.maritimecloud.identityregistry.domain.IdentityService;
import net.maritimecloud.identityregistry.domain.EncryptionService;
import net.maritimecloud.portal.application.ApplicationServiceRegistry;
import net.maritimecloud.identityregistry.domain.Role;
import net.maritimecloud.portal.infrastructure.service.SHA512EncryptionService;
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
 * The users in this realm uses password encryption. Shiro configuration (shiro.ini) must match this encryption algorithm.
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

    private IdentityService identityService() {
        return ApplicationServiceRegistry.identityService();
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
            Identity identity = identityService().findActivatedIdentityByUsername(username);

            if (identity == null) {
                throw new UnknownAccountException("Could not authenticate with given credentials");
            }

            // Create Auth Info
            return new SimpleAuthenticationInfo(
                    identity.userId().identifier(),
                    identity.encryptedPassword(),
                    ByteSource.Util.bytes("salt"), // (not sure if this salt is used at all?)
                    getName()
            );
        } else {
            return null;
        }
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        // Get the userId from the first principal in the collection
        String userIdentifier = (String) getAvailablePrincipal(principals);

        // Lookup user
        Identity identity = identityService().findByUserId(userIdentifier);
        assertUserFound(identity);

        // Add roles to AuthorizationInfo
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRole(Role.USER.name());
        if (identity.username().equalsIgnoreCase("admin")) {
            info.addRole(Role.ADMIN.name());
        }
        //user.userRoles().all().stream().forEach((role) -> {
        //    info.addRole(role.name());
        //});

        return info;
    }

    private void assertUserFound(Identity user) {
        if (user == null) {
            throw new UnknownAccountException("No user found in application registry");
        }
    }

}
