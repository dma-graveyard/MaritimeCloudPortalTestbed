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
package net.maritimecloud.identityregistry.command.user;

import java.util.UUID;
import javax.annotation.Resource;
import net.maritimecloud.identityregistry.command.api.SendResetPasswordInstructions;
import net.maritimecloud.identityregistry.query.internal.InternalUserEntry;
import net.maritimecloud.identityregistry.query.internal.InternalUserQueryRepository;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.AggregateNotFoundException;
import org.axonframework.repository.Repository;
import org.springframework.stereotype.Component;

/**
 * @author Christoffer Børrild
 */
@Component
public class UserCommandHandler {

    @Resource
    private Repository<User> userAggregateRepository;

    @Resource
    InternalUserQueryRepository internalUserQueryRepository;

    public UserCommandHandler() {
    }

    // (for dependency injection when testing)
    public UserCommandHandler(Repository<User> userAggregateRepository, InternalUserQueryRepository internalUserQueryRepository) {
        this.userAggregateRepository = userAggregateRepository;
        this.internalUserQueryRepository = internalUserQueryRepository;
    }

    @CommandHandler
    public void handle(SendResetPasswordInstructions command) {

        try {
            // lookup userId by email
            InternalUserEntry userView = internalUserQueryRepository.findByEmailAddressIgnoreCase(command.getEmailAddress());

            if (userView == null) {
                // silently ignore errors like user not found 
                return;
            }

            // lookup user aggregate
            UserId userId = new UserId(userView.getUserId());
            User user = userAggregateRepository.load(userId);
            if (!user.isDeleted()) {
                user.registerResetPasswordKey(generateResetPasswordKey());
            }

        } catch (AggregateNotFoundException e) {
            // we silently ignore errors like user not found 
        }

    }

    private String generateResetPasswordKey() {
        return UUID.randomUUID().toString();
    }

}
