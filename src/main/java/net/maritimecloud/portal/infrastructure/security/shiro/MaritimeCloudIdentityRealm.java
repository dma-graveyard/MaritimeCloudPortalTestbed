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

import net.maritimecloud.identityregistry.query.internal.InternalUserEntry;
import net.maritimecloud.identityregistry.query.internal.InternalUserQueryRepository;
import net.maritimecloud.portal.domain.model.DomainRegistry;
import net.maritimecloud.portal.domain.model.identity.EncryptionService;
import net.maritimecloud.portal.domain.model.identity.Role;
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

    private InternalUserQueryRepository internalUserQueryRepository() {
        return DomainRegistry.internalUserQueryRepository();
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
            //User user = identityApplicationService().user(username);
            InternalUserEntry user = internalUserQueryRepository().findByUsername(username);

            if (user == null || !user.isActivated()) {
                throw new UnknownAccountException("Could not authenticate with given credentials");
            }

            // Create Auth Info
            return new SimpleAuthenticationInfo(
                    user.getUserId(),
                    user.getEncryptedPassword(),
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
        InternalUserEntry user = internalUserQueryRepository().findOne(userIdentifier);
        assertUserFound(user);

        // Create AuthorizationInfo
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        // Add all roles
        info.addRole(Role.USER.name());
        if (user.getUsername().equalsIgnoreCase("admin")) {
            info.addRole(Role.ADMIN.name());
        }
        //user.userRoles().all().stream().forEach((role) -> {
        //    info.addRole(role.name());
        //});

        return info;
    }

    private void assertUserFound(InternalUserEntry user) {
        if (user == null) {
            throw new UnknownAccountException("No user found in application registry");
        }
    }

}
