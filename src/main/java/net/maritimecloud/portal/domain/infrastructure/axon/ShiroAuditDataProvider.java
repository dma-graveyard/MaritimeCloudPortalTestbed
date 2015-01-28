/* Copyright 2015 Danish Maritime Authority.
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
package net.maritimecloud.portal.domain.infrastructure.axon;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.maritimecloud.identityregistry.query.internal.InternalUserEntry;
import net.maritimecloud.identityregistry.query.internal.InternalUserQueryRepository;
import net.maritimecloud.portal.application.ApplicationServiceRegistry;
import net.maritimecloud.portal.config.AxonConfig;
import net.maritimecloud.portal.domain.model.DomainRegistry;
import net.maritimecloud.portal.domain.model.security.UserNotLoggedInException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.subject.Subject;
import org.axonframework.auditing.AuditDataProvider;
import org.axonframework.commandhandling.CommandMessage;

/**
 * AuditDataProvider that provides user-information obtained from Shiro
 * <p>
 * @author Christoffer Børrild
 */
public class ShiroAuditDataProvider implements AuditDataProvider {

    private InternalUserQueryRepository internalUserQueryRepository() {
        return DomainRegistry.internalUserQueryRepository();
    }

    @Override
    public Map<String, Object> provideAuditDataFor(CommandMessage<?> command) {
        Map<String, Object> metaData = new HashMap<>();

        try {
            final Subject subject = SecurityUtils.getSubject();
            String userHost = subject.getSession().getHost();
            metaData.put(UserMetaData.USER_HOST, userHost);

            String userId = (String) ApplicationServiceRegistry.authenticationUtil().getUserId();
            metaData.put(UserMetaData.USERID, userId);

            //User user = ApplicationServiceRegistry.identityApplicationService().user(userId);
            InternalUserEntry user = internalUserQueryRepository().findByUsername(userId);
            if (user != null) {
                metaData.put(UserMetaData.USERNAME, user.getUsername());
            } else {
                Logger.getLogger(AxonConfig.class.getName()).log(Level.WARNING, "Unknown user with userId {} from host " + metaData.get(UserMetaData.USER_HOST), metaData.get(UserMetaData.USERID));
                metaData.put(UserMetaData.USERNAME, "Anonymous");
            }
        } catch (UnavailableSecurityManagerException ex) {
            Logger.getLogger(AxonConfig.class.getName()).log(Level.WARNING, null, ex);
            throw ex;
        } catch (UserNotLoggedInException ex) {
            Logger.getLogger(AxonConfig.class.getName()).log(Level.FINE, "Anonymous access from host {}", metaData.get(UserMetaData.USER_HOST));
            metaData.put(UserMetaData.USERNAME, "Anonymous");
        }

        return metaData;
    }

}
