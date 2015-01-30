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
package net.maritimecloud.identityregistry.command.user;

import java.util.Objects;
import net.maritimecloud.portal.domain.model.identity.*;
import java.util.UUID;
import java.util.regex.Pattern;
import net.maritimecloud.identityregistry.command.api.ChangeUserEmailAddress;
import net.maritimecloud.identityregistry.command.api.ChangeUserPassword;
import net.maritimecloud.identityregistry.command.api.RegisterUser;
import net.maritimecloud.identityregistry.command.api.ResetPasswordKeyGenerated;
import net.maritimecloud.identityregistry.command.api.UnconfirmedUserEmailAddressSupplied;
import net.maritimecloud.identityregistry.command.api.UserAccountActivated;
import net.maritimecloud.identityregistry.command.api.UserEmailAddressVerified;
import net.maritimecloud.identityregistry.command.api.UserPasswordChanged;
import net.maritimecloud.identityregistry.command.api.UserRegistered;
import net.maritimecloud.identityregistry.command.api.VerifyEmailAddress;
import net.maritimecloud.portal.domain.model.DomainRegistry;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.common.Assert;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;
import org.springframework.stereotype.Component;

/**
 * @author Christoffer Børrild
 */
@Component
public class User extends AbstractAnnotatedAggregateRoot<UserId> {

//    private final EncryptionService encryptionService;
    @AggregateIdentifier
    private UserId userId;

    private String username;
    private String password;
//    private String passwordEncryptionSalt;
    //private Person person;
    private String emailAddress;
    private String activationId;

    private UserRoles userRoles;
    private boolean activated;

    private UnconfirmedEmailAddress unconfirmedEmailAddress;
    private String resetPasswordKey;

    protected User() {
//        encryptionService = DomainRegistry.encryptionService();
        activated = false;
    }

    @CommandHandler
    public User(RegisterUser command) {
        this();
        assertValidEmailAddress(command.getEmailAddress());
        assertValidUsername(command.getPrefferedUsername());
        assertPasswordComply(command.getPrefferedUsername(), command.getPassword());
        String emailVerificationCode = new UnconfirmedEmailAddress(command.getEmailAddress()).activationCode().toString();

        // HACK: FIXME: TODO: 
        // supply hardcoded code in odrer to auto-create users for test and demo without reading mails
        emailVerificationCode = a_HACK_TEST_useStaticVerificationCode(command.getEmailAddress(), emailVerificationCode);

        apply(new UserRegistered(
                command.getUserId(),
                command.getPrefferedUsername(),
                command.getEmailAddress(),
                asEncryptedValue(command.getPassword()),
                emailVerificationCode)
        );
    }

    @CommandHandler
    public void handle(ChangeUserEmailAddress command) {
        assertValidEmailAddress(command.getEmailAddress());
        if (!command.getEmailAddress().equalsIgnoreCase(emailAddress())) {
            String emailVerificationCode = new UnconfirmedEmailAddress(command.getEmailAddress()).activationCode().toString();

            // HACK: FIXME: TODO: 
            // supply hardcoded code in odrer to auto-create users for test and demo without reading mails
            emailVerificationCode = a_HACK_TEST_useStaticVerificationCode(command.getEmailAddress(), emailVerificationCode);

            apply(new UnconfirmedUserEmailAddressSupplied(command.getUserId(), this.username(), command.getEmailAddress(), emailVerificationCode));
        }
    }

    private String a_HACK_TEST_useStaticVerificationCode(String emailAddress, String emailVerificationCode) {
        // HACK: FIXME: TODO:
        // supply hardcoded code in odrer to auto-create users for test and demo without reading mails
        if (emailAddress.contains("@static.demo.dma.dk")) {
            emailVerificationCode = emailAddress.replace("@static.demo.dma.dk", "");
        }
        return emailVerificationCode;
    }

    @CommandHandler
    public void handle(VerifyEmailAddress command) {
        assertValidEmailVerificationCode(command.getEmailAddressVerificationId());
        if (emailAddressAlreadyConfirmed()) {
            return;
        }
        apply(new UserEmailAddressVerified(command.getUserId(), this.username(), unconfirmedEmailAddress.emailAddress));
        if (!isActivated()) {
            apply(new UserAccountActivated(command.getUserId(), this.username()));
        }
    }

    private boolean emailAddressAlreadyConfirmed() {
        return unconfirmedEmailAddress != null && unconfirmedEmailAddress.emailAddress().equals(emailAddress());
    }

    @CommandHandler
    public void handle(ChangeUserPassword command) {
        assertCanChangePassword(command.getCurrentPassword(), command.getChangedPassword());
        apply(new UserPasswordChanged(command.getUserId(), this.username(), asEncryptedValue(command.getChangedPassword())));
    }

    @EventSourcingHandler
    public void on(UserRegistered event) {
        this.userId = event.getUserId();
        this.setUsername(event.getPrefferedUsername());
        this.unconfirmedEmailAddress = new UnconfirmedEmailAddress(event.getEmailAddress(), event.getEmailVerificationCode());
        this.setPassword(event.getObfuscatedPassword());
    }

    @EventSourcingHandler
    public void on(UnconfirmedUserEmailAddressSupplied event) {
        this.unconfirmedEmailAddress = new UnconfirmedEmailAddress(event.getUnconfirmedEmailAddress(), event.getEmailVerificationCode());
    }

    @EventSourcingHandler
    public void on(UserEmailAddressVerified event) {
        this.setEmailAddress(event.getEmailAddress());
    }

    @EventSourcingHandler
    public void on(UserAccountActivated event) {
        this.activated = true;
    }

    @EventSourcingHandler
    public void on(ResetPasswordKeyGenerated event) {
        this.resetPasswordKey = event.getResetPasswordKey();
    }

    @EventSourcingHandler
    public void on(UserPasswordChanged event) {
        setPassword(event.getObfuscatedChangedPassword());
    }

//    public User(String aUsername, String aPassword /*,Person aPerson*/, String emailAddress, Role... roles) {
//
//        this();
//
//        // TODO: hack? generate id using a UserIdService or something...
//
//        if (roles.length == 0) {
//            userRoles = new UserRoles(Role.USER);
//        } else {
//            userRoles = new UserRoles(roles);
//        }
//
//        // setPerson(aPerson);
//        setUsername(aUsername);
//        protectPassword(aPassword);
//        // aPerson.internalOnlySetUser(this);
//        setEmailAddress(emailAddress);
//        passwordEncryptionSalt = "salt";
//
//        //DomainEventPublisher.instance().publish(new UserRegistered(aUsername, aPerson.name(), aPerson.contactInformation().emailAddress()));
//    }
//
//    /**
//     * @return the application specific unique id of the user
//     */
//    @Override
//    public long id() {
//        //return super.id();
//        return id;
//    }
//
//    //public Person person() {
//    //    return person;
//    //}
//    //public UserDescriptor userDescriptor() {
//    //    return new UserDescriptor(
//    //            username(),
//    //            person().emailAddress().address());
//    //}
//    //
    public String emailAddress() {
        return emailAddress;
    }

//    @Override
//    public int hashCode() {
//        int hashCodeValue = +(45217 * 269) + this.username().hashCode();
//        return hashCodeValue;
//    }
//
//    @Override
//    public String toString() {
//        return "User [username=" + username
//                //+ ", person=" + person 
//                + ", emailAddress=" + emailAddress
//                + "]";
//    }
    private void assertCanChangePassword(String aCurrentPassword, String aChangedPassword) {

        Assert.notEmpty(aCurrentPassword, "Current and new password must be provided.");
        Assert.notEmpty(aChangedPassword, "Current and new password must be provided.");
        assertCurrentPasswordConfirmedOrIsResetPasswordKey(aCurrentPassword, "Current password not confirmed.");
        assertPasswordComply(username(), aChangedPassword);

//        protectPassword(aChangedPassword);
        //DomainEventPublisher.instance().publish(new UserPasswordChanged(userId(), username()));
    }

    private void assertPasswordComply(String username, String aChangedPassword) {

        // TODO: add this extra validation if you do not feel it's a bit to harsh:
        assertPasswordsNotSame(password(), aChangedPassword);
        assertPasswordNotWeak(aChangedPassword);
        assertUsernamePasswordNotSame(username, aChangedPassword);
    }

////    public void changePersonalContactInformation(ContactInformation aContactInformation) {
////        person().changeContactInformation(aContactInformation);
////    }
////
////    public void changePersonalName(FullName aPersonalName) {
////        person().changeName(aPersonalName);
////    }
    private void assertCurrentPasswordConfirmedOrIsResetPasswordKey(String aCurrentPassword, String message) {
        if (isNotResetPasswordKey(aCurrentPassword)) {
            assertCurrentPasswordConfirmed(aCurrentPassword, message);
        }
    }

    private boolean isNotResetPasswordKey(String aCurrentPassword) {
        return !aCurrentPassword.equals(resetPasswordKey);
    }

    private void assertCurrentPasswordConfirmed(String aCurrentPassword, String message) {
        boolean valuesMatch = encryptionService().valuesMatch(aCurrentPassword, internalAccessOnlyEncryptedPassword());
        Assert.isTrue(valuesMatch, message);
    }

    private static EncryptionService encryptionService() {
        return DomainRegistry.encryptionService();
    }

//    @Override
//    public boolean equals(Object anObject) {
//        boolean equalObjects = false;
//
//        if (anObject != null && this.getClass() == anObject.getClass()) {
//            User typedObject = (User) anObject;
//            equalObjects = this.username().equals(typedObject.username());
//        }
//
//        return equalObjects;
//    }
    protected void assertPasswordsNotSame(String aCurrentPassword, String aChangedPassword) {
        if (aCurrentPassword == null) {
            return;
        }
        boolean valuesMatch = encryptionService().valuesMatch(aChangedPassword, aCurrentPassword);
        Assert.isFalse(valuesMatch, "The password is unchanged.");
    }

    protected void assertPasswordNotWeak(String aPlainTextPassword) {
        Assert.isFalse(DomainRegistry.passwordService().isWeak(aPlainTextPassword), "The password must be stronger.");
    }

    protected void assertUsernamePasswordNotSame(String username, String aPlainTextPassword) {
        assertArgumentNotEquals(username, aPlainTextPassword, "The username and password must not be the same.");
    }

    private void assertArgumentNotEquals(Object anObject1, Object anObject2, String aMessage) {
        if (anObject1.equals(anObject2)) {
            throw new IllegalArgumentException(aMessage);
        }
    }

    private String internalAccessOnlyEncryptedPassword() {
        return password();
    }

    private String username() {
        return username;
    }

    private void setUsername(String aUsername) {
        this.username = aUsername;
    }

    private String password() {
        return password;
    }

    protected void setPassword(String aPassword) {
        this.password = aPassword;
    }

    private void setEmailAddress(String anEmailAddress) {
        this.emailAddress = anEmailAddress;
    }

//    //protected void setPerson(Person aPerson) {
//    //    assertArgumentNotNull(aPerson, "The person is required.");
//    //
//    //    this.person = aPerson;
//    //}
//    //
//    private void protectPassword(String aChangedPassword) {
//        setPassword(asEncryptedValue(aChangedPassword));
//    }
    private String asEncryptedValue(String aPlainTextPassword) {
        String encryptedValue = encryptionService().encryptedValue(aPlainTextPassword);
        return encryptedValue;
    }

////    protected GroupMember toGroupMember() {
////        GroupMember groupMember =
////            new GroupMember(
////                    username(),
////                    GroupMemberType.User);
////
////        return groupMember;
////    }
    private void assertValidUsername(String aUsername) {
        Assert.notEmpty(aUsername, "The username is required.");
        assertArgumentLength(aUsername, 3, 250, "The username must be 3 to 250 characters.");
        // TODO: check a domain service to see if the username is already taken
        // ...command.getUsername()
        // ...or perhaps add a saga for that!?!
    }

    private void assertArgumentLength(String aString, int aMinimum, int aMaximum, String aMessage) {
        int length = aString.trim().length();
        if (length < aMinimum || length > aMaximum) {
            throw new IllegalArgumentException(aMessage);
        }
    }

    private void assertValidEmailAddress(String anEmailAddress) {
        Assert.notEmpty(anEmailAddress, "The email address is required.");
        assertArgumentLength(anEmailAddress, 1, 100, "Email address must be 100 characters or less.");
        Assert.isTrue(
                Pattern.matches("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*", anEmailAddress),
                "Email address format is invalid.");
    }

//    public String internalAccessOnlyEncryptionSalt() {
//        return passwordEncryptionSalt;
//    }
//
//    public UserRoles userRoles() {
//        return userRoles;
//    }
    private String activationId() {
        return activationId;
    }

    private void assertValidEmailVerificationCode(String emailAddressVerificationCode) {
        if (!unconfirmedEmailAddress.match(emailAddressVerificationCode)) {
            throw new IllegalArgumentException("Unknown or deprecated emailAddress Verification Code");
        }

    }

//    /**
//     * Generates a random activationId. The id must be used in order to activate the user account 
//     * but may also be used for other purposes, like reset password confirmation (TODO: change that!)
//     */
//    public void generateActivationId() {
//        activationId = UUID.randomUUID().toString();
//    }
//
//    /**
//     * Method that will mark the User (Account) as active if the supplied id match the stored activationId
//     * @param anActivationId 
//     */
//    public void activate(String anActivationId) {
//        if (activationId() != null && activationId().equals(anActivationId)) {
//            activate();
//        }
//    }
//
//    private void activate() {
//        active = true;
//    }
    public boolean isActivated() {
        return activated;
    }

    public void registerResetPasswordKey(String resetPasswordKey) {
        apply(new ResetPasswordKeyGenerated(userId, username, emailAddress, resetPasswordKey));
    }

    private static class UnconfirmedEmailAddress {

        private final String emailAddress;
        private final UUID activationCode;

        public UnconfirmedEmailAddress(String emailAddress, String activationCode) {
            this.emailAddress = emailAddress;
            this.activationCode = UUID.fromString(activationCode);
        }

        public UnconfirmedEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
            this.activationCode = UUID.randomUUID();
        }

        public String emailAddress() {
            return emailAddress;
        }

        public UUID activationCode() {
            return activationCode;
        }

        public boolean match(String activationCode) {
            return this.activationCode.toString().equals(activationCode);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 59 * hash + Objects.hashCode(this.emailAddress);
            hash = 59 * hash + Objects.hashCode(this.activationCode);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final UnconfirmedEmailAddress other = (UnconfirmedEmailAddress) obj;
            if (!Objects.equals(this.emailAddress, other.emailAddress)) {
                return false;
            }
            if (!Objects.equals(this.activationCode, other.activationCode)) {
                return false;
            }
            return true;
        }

    }
}
