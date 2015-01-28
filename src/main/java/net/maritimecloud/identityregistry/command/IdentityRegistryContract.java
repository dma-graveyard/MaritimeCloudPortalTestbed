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
package net.maritimecloud.identityregistry.command;

import net.maritimecloud.common.cqrs.contract.CqrsContract;
import net.maritimecloud.common.cqrs.contract.Event;
import net.maritimecloud.common.cqrs.contract.Command;
import net.maritimecloud.identityregistry.command.user.UserId;

/**
 * Identity Registry currently only caters for User accounts.
 * <p>
 * A User is the registration of user contact information like username, email, password, firstname, lastname and perhaps some roles.
 * <p>
 * This interface describes the interface to the ServiceRegitry in terms of Commands and Events. Whereas the commands are explicitly being
 * the published interface, the description of the events are more like a convenience as they in most cases share the properties of the
 * commands.
 * <p>
 * Events may inherit their properties from corresponding commands, and may extend those properties, as would be needed to make room for
 * enrichment.
 * <p>
 * <p>
 * @see CommandEventSourceGenerator
 * @author Christoffer Børrild
 */
public interface IdentityRegistryContract extends CqrsContract {

    @Command
    void RegisterUser(UserId userId, String prefferedUsername, String emailAddress, String password);

    @Command
    void ChangeUserEmailAddress(UserId userId, String emailAddress);

    @Command
    void VerifyEmailAddress(UserId userId, String emailAddressVerificationId);

    @Command
    void ChangeUserPassword(UserId userId, String currentPassword, String changedPassword);

//    @Command
//    void ChangeUserContactName(UserId userId, String firstname, String lastname);

    // ------------------------------------------------------------------------
    // EVENTS
    // ------------------------------------------------------------------------
    @Event
    void UserRegistered(UserId userId, String prefferedUsername, String emailAddress, String obfuscatedPassword, String emailVerificationCode);

    @Event
    void UnconfirmedUserEmailAddressSupplied(UserId userId, String username, String unconfirmedEmailAddress, String emailVerificationCode);

    @Event
    void UserEmailAddressVerified(UserId userId, String username, String emailAddress);

    @Event
    void UserAccountActivated(UserId userId, String username);

    @Event
    void UserPasswordChanged(UserId userId, String username, String obfuscatedChangedPassword);

}
