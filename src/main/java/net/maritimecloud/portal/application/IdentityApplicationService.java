/* Copyright (c) 2011 Danish Maritime Authority.
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
package net.maritimecloud.portal.application;

import net.maritimecloud.portal.domain.model.DomainRegistry;
import net.maritimecloud.portal.domain.model.identity.UnknownUserException;
import java.util.List;
import javax.transaction.Transactional;
import net.maritimecloud.portal.domain.model.AssertionConcern;
import net.maritimecloud.portal.domain.model.identity.User;
import net.maritimecloud.portal.domain.model.identity.UserRepository;
import net.maritimecloud.portal.infrastructure.mail.MailService;
import net.maritimecloud.portal.resource.LogService;

/**
 * @author Christoffer Børrild
 */
public class IdentityApplicationService extends AssertionConcern {

    private UserRepository userRepository() {
        return DomainRegistry.userRepository();
    }

    private MailService mailService() {
        return ApplicationServiceRegistry.mailService();
    }

    protected LogService logService() {
        return ApplicationServiceRegistry.logService();
    }

    @Transactional
    public User registerUser(String username, String password, String emailAddress) {
        User newUser = new User(username, password, emailAddress);
        newUser.generateActivationId();
        userRepository().add(newUser);
        mailService().sendSignUpActivationMessage(newUser);
        return newUser;
    }

    @Transactional//(readOnly = true)
    public User user(String aUsername) {
        return userRepository().userWithUsername(aUsername);
    }

    public List<User> usersWithUsernameMatching(String usernamePattern) {
        return userRepository().usersWithUsernameMatching(usernamePattern);
    }

    public User user(long userId) throws UnknownUserException {
        return userRepository().get(userId);
    }

    public boolean activate(String aUsername, String activationId) {
        User aUser = user(aUsername);
        if (aUser == null) {
            logService().activateAccountFailedUserNotFound(aUsername);
            return false;
        }
        aUser.activate(activationId);

        if (!aUser.isActive()) {
            logService().activateAccountFailed(aUsername, activationId, aUser.activationId());
        }

        logService().activateAccountSucceded(aUsername);
        return aUser.isActive();
    }

    public void sendResetPasswordMessage(String emailAddress) {
    
        assertArgumentNotEmpty(emailAddress, "A valid email address must be provided");
    
        // Find user
        User aUser = userRepository().userWithEmail(emailAddress);

        // Report missing user
        if (aUser == null) {
            logService().sendResetPasswordMessageFailedUserOfEmailNotFound(emailAddress);
            return;
        }

        // Generate a new confirmation id to be used for reset password
        aUser.generateActivationId();

        mailService().sendResetPasswordMessage(aUser);
    }

    public void resetPassword(String aUsername, String aVerificationCode, String aNewPassword) {

        assertArgumentNotEmpty(aUsername, "A username must be provided");
        assertArgumentNotEmpty(aVerificationCode, "A verificationcode must be provided");
        assertArgumentNotEmpty(aNewPassword, "A new password must be provided");

        user(aUsername).changePassword(aVerificationCode, aNewPassword);
    }

}
