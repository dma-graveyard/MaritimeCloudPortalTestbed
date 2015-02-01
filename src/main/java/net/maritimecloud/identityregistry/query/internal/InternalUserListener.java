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
package net.maritimecloud.identityregistry.query.internal;

import javax.annotation.Resource;
import net.maritimecloud.identityregistry.command.api.UserAccountActivated;
import net.maritimecloud.identityregistry.command.api.UserEmailAddressVerified;
import net.maritimecloud.identityregistry.command.api.UserPasswordChanged;
import net.maritimecloud.identityregistry.command.api.UserRegistered;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventhandling.annotation.Timestamp;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Christoffer Børrild
 */
@Component
public class InternalUserListener {

    private final static Logger logger = LoggerFactory.getLogger(InternalUserListener.class);

    @Resource
    private InternalUserQueryRepository internalUserQueryRepository;

    public InternalUserListener() {
    }

    public InternalUserListener(InternalUserQueryRepository internalUserQueryRepository) {
        this.internalUserQueryRepository = internalUserQueryRepository;
    }

    @EventHandler
    public void on(UserRegistered event) {
        InternalUserEntry userEntry = new InternalUserEntry();
        userEntry.setUserId(event.getUserId().identifier());
        userEntry.setUsername(event.getPrefferedUsername());
        userEntry.setEmailAddress(event.getEmailAddress());
        userEntry.setEncryptedPassword(event.getObfuscatedPassword());
        userEntry.setActivated(false);
        internalUserQueryRepository.save(userEntry);
    }

    @EventHandler
    public void on(UserAccountActivated event, @Timestamp DateTime dateTime) {
        InternalUserEntry userEntry = internalUserQueryRepository.findOne(event.getUserId().identifier());
        userEntry.setActivated(true);
        internalUserQueryRepository.save(userEntry);
    }

    @EventHandler
    public void on(UserPasswordChanged event) {
        InternalUserEntry userEntry = internalUserQueryRepository.findOne(event.getUserId().identifier());
        userEntry.setEncryptedPassword(event.getObfuscatedChangedPassword());
        internalUserQueryRepository.save(userEntry);
    }

    @EventHandler
    public void on(UserEmailAddressVerified event) {
        InternalUserEntry userEntry = internalUserQueryRepository.findOne(event.getUserId().identifier());
        userEntry.setEmailAddress(event.getEmailAddress());
        internalUserQueryRepository.save(userEntry);
    }

}
