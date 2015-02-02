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

import javax.annotation.Resource;
import net.maritimecloud.identityregistry.command.api.ChangeUserPassword;
import net.maritimecloud.identityregistry.command.api.ResetPasswordKeyGenerated;
import net.maritimecloud.identityregistry.command.api.UserPasswordChanged;
import net.maritimecloud.portal.application.ApplicationServiceRegistry;
import net.maritimecloud.portal.domain.infrastructure.axon.NoReplayedEvents;
import net.maritimecloud.portal.infrastructure.mail.MailService;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.axonframework.saga.annotation.EndSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;

/**
 *
 * @author Christoffer Børrild
 */
@NoReplayedEvents
public class ResetPasswordSaga extends AbstractAnnotatedSaga {

    @Resource
    private transient CommandGateway commandGateway;
    
    private MailService mailService() {
        return ApplicationServiceRegistry.mailService();
    }

    public CommandGateway getCommandGateway() {
        return commandGateway;
    }

    public void setCommandGateway(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "userId")
    public void handle(ResetPasswordKeyGenerated event) {

        System.out.println("User " + event.getUsername() + " has requested to reset password using " + event.getEmailAddress() + ".");

        // compose and send out welcome and confirm email
        System.out.println("Sending out reset password instruction email with the reset password key: " + event.getResetPasswordKey());
        
        mailService().sendResetPasswordMessage(event.getEmailAddress(), event.getUsername(), event.getResetPasswordKey());
        
        // HACK: FIXME: TODO: 
        // auto-confirm users that fulfil some criteria
        autoResetTestUsersPassword_HACK(event.getUserId(), event.getEmailAddress(), event.getResetPasswordKey());
    }

    private String autoResetTestUsersPassword_HACK(UserId userId, String emailAddress, String resetPasswordKey) {
        // HACK: FIXME: TODO:
        // auto generate ResetPasswordCommand in odrer to auto-reset users password in test and demo without reading mails
        if (emailAddress.endsWith("@auto.demo.dma.dk")) {
            System.out.println("HACK for auto.demo.dma.dk dmoain: auto-reset password to 'reset' for user "+ userId);
            commandGateway.send(new ChangeUserPassword(userId, resetPasswordKey, "reset"));
        }
        return resetPasswordKey;
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "userId")
    public void handle(UserPasswordChanged event) {
        System.out.println("User " + event.getUsername() + " password has been changed.");
        //System.out.println("Sending out a 'Users password was reset' notification email to: " + event.getUsername());
    }

    // FIXME TODO: add en expire saga trigger to end unanswered saga instances!!!
    //@EndSaga
    //...
}
