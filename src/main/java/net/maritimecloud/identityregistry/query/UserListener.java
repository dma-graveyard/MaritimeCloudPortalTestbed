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
package net.maritimecloud.identityregistry.query;

import javax.annotation.Resource;
import net.maritimecloud.identityregistry.command.api.UserAccountActivated;
import net.maritimecloud.identityregistry.command.api.UserEmailAddressVerified;
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
public class UserListener {

    private final static Logger logger = LoggerFactory.getLogger(UserListener.class);

    @Resource
    private UserQueryRepository userQueryRepository;

    public UserListener() {
    }

    public UserListener(UserQueryRepository userQueryRepository) {
        this.userQueryRepository = userQueryRepository;
    }

    public void setOrganizationQueryRepository(UserQueryRepository userQueryRepository) {
        this.userQueryRepository = userQueryRepository;
    }

    @EventHandler
    public void on(UserRegistered event) {
        System.out.println("USER REGISTERED !!!!!!!!!!!!!!!!!!!!!! "+event);
        UserEntry userEntry = new UserEntry();
        userEntry.setUserId(event.getUserId().identifier());
        userEntry.setUsername(event.getPrefferedUsername());
        userEntry.setIsActivated(false);
        userQueryRepository.save(userEntry);
    }

    @EventHandler
    public void on(UserEmailAddressVerified event) {
        UserEntry userEntry = userQueryRepository.findOne(event.getUserId().identifier());
        userEntry.setEmailAddress(event.getEmailAddress());
        userQueryRepository.save(userEntry);
    }

    @EventHandler
    public void on(UserAccountActivated event, @Timestamp DateTime dateTime) {
        UserEntry userEntry = userQueryRepository.findOne(event.getUserId().identifier());
        userEntry.setIsActivated(true);
        userEntry.setActivatedSince(dateTime.toDate());
        userQueryRepository.save(userEntry);
    }

}
