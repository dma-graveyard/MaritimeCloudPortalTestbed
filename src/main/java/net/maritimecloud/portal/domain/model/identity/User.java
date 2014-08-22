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
package net.maritimecloud.portal.domain.model.identity;

import java.util.UUID;
import java.util.regex.Pattern;
import net.maritimecloud.portal.domain.model.ConcurrencySafeEntity;
import net.maritimecloud.portal.domain.model.DomainRegistry;

/**
 * @author Christoffer Børrild
 */
public class User extends ConcurrencySafeEntity {

    private static final long serialVersionUID = 1L;

    private final EncryptionService encryptionService;

    private String username;
    private String password;
    private String passwordEncryptionSalt;
    //private Person person;
    private String emailAddress;
    private String activationId;

    private static long idSequence = 1;
    // overrides IndentifiedDomainObject id
    private long id;
    private UserRoles userRoles;
    private boolean active;

    protected User() {
        super();
        encryptionService = DomainRegistry.encryptionService();
    }

    public User(String aUsername, String aPassword /*,Person aPerson*/, String emailAddress, Role... roles) {

        this();

        // TODO: hack? generate id using a UserIdService or something...
        id = idSequence++;

        if (roles.length == 0) {
            userRoles = new UserRoles(Role.USER);
        } else {
            userRoles = new UserRoles(roles);
        }

        // setPerson(aPerson);
        setUsername(aUsername);
        protectPassword(aPassword);
        // aPerson.internalOnlySetUser(this);
        setEmailAddress(emailAddress);
        passwordEncryptionSalt = "salt";

        //DomainEventPublisher.instance().publish(new UserRegistered(aUsername, aPerson.name(), aPerson.contactInformation().emailAddress()));
    }

    /**
     * @return the application specific unique id of the user
     */
    @Override
    public long id() {
        //return super.id();
        return id;
    }

    //public Person person() {
    //    return person;
    //}
    //public UserDescriptor userDescriptor() {
    //    return new UserDescriptor(
    //            username(),
    //            person().emailAddress().address());
    //}
    //
    public String username() {
        return username;
    }

    public String emailAddress() {
        return emailAddress;
    }

    @Override
    public int hashCode() {
        int hashCodeValue = +(45217 * 269) + this.username().hashCode();
        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "User [username=" + username
                //+ ", person=" + person 
                + ", emailAddress=" + emailAddress
                + "]";
    }

    public void changePassword(String aCurrentPassword, String aChangedPassword) {

        assertArgumentNotEmpty(aCurrentPassword, "Current and new password must be provided.");
        assertCurrentPasswordConfirmedOrIsConfirmationId(aCurrentPassword, "Current password not confirmed.");

        protectPassword(aChangedPassword);

        //DomainEventPublisher.instance().publish(new UserPasswordChanged(userId(), username()));
    }

//    public void changePersonalContactInformation(ContactInformation aContactInformation) {
//        person().changeContactInformation(aContactInformation);
//    }
//
//    public void changePersonalName(FullName aPersonalName) {
//        person().changeName(aPersonalName);
//    }
    private void assertCurrentPasswordConfirmedOrIsConfirmationId(String aCurrentPassword, String message) {
        if (isNotResetPasswordConfirmationId(aCurrentPassword)) {
            assertCurrentPasswordConfirmed(aCurrentPassword, message);
        }
    }

    private boolean isNotResetPasswordConfirmationId(String aCurrentPassword) {
        return !aCurrentPassword.equals(activationId());
    }

    private void assertCurrentPasswordConfirmed(String aCurrentPassword, String message) {
        boolean valuesMatch = encryptionService.valuesMatch(aCurrentPassword, internalAccessOnlyEncryptedPassword());
        assertArgumentTrue(valuesMatch, message);
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            User typedObject = (User) anObject;
            equalObjects = this.username().equals(typedObject.username());
        }

        return equalObjects;
    }

    protected void assertPasswordsNotSame(String aCurrentPassword, String aChangedPassword) {
        if (aCurrentPassword == null) {
            return;
        }
        boolean valuesMatch = encryptionService.valuesMatch(aChangedPassword, aCurrentPassword);
        assertArgumentFalse(valuesMatch, "The password is unchanged.");
    }

    protected void assertPasswordNotWeak(String aPlainTextPassword) {
        assertArgumentFalse(DomainRegistry.passwordService().isWeak(aPlainTextPassword), "The password must be stronger.");
    }

    protected void assertUsernamePasswordNotSame(String aPlainTextPassword) {
        assertArgumentNotEquals(username(), aPlainTextPassword, "The username and password must not be the same.");
    }

    public String internalAccessOnlyEncryptedPassword() {
        return password();
    }

    protected String password() {
        return password;
    }

    protected void setPassword(String aPassword) {
        this.password = aPassword;
    }

    //protected void setPerson(Person aPerson) {
    //    assertArgumentNotNull(aPerson, "The person is required.");
    //
    //    this.person = aPerson;
    //}
    //
    protected void protectPassword(String aChangedPassword) {

        assertPasswordsNotSame(password(), aChangedPassword);
        assertPasswordNotWeak(aChangedPassword);
        assertUsernamePasswordNotSame(aChangedPassword);

        setPassword(asEncryptedValue(aChangedPassword));
    }

    private String asEncryptedValue(String aPlainTextPassword) {
        String encryptedValue = encryptionService.encryptedValue(aPlainTextPassword);
        return encryptedValue;
    }

//    protected GroupMember toGroupMember() {
//        GroupMember groupMember =
//            new GroupMember(
//                    username(),
//                    GroupMemberType.User);
//
//        return groupMember;
//    }
    protected void setUsername(String aUsername) {
        assertArgumentNotEmpty(aUsername, "The username is required.");
        assertArgumentLength(aUsername, 3, 250, "The username must be 3 to 250 characters.");

        this.username = aUsername;
    }

    public void setEmailAddress(String anEmailAddress) {
        assertArgumentNotEmpty(anEmailAddress, "The email address is required.");
        assertArgumentLength(anEmailAddress, 1, 100, "Email address must be 100 characters or less.");
        assertArgumentTrue(
                Pattern.matches("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*", anEmailAddress),
                "Email address format is invalid.");

        this.emailAddress = anEmailAddress;
    }

    public String internalAccessOnlyEncryptionSalt() {
        return passwordEncryptionSalt;
    }

    public UserRoles userRoles() {
        return userRoles;
    }

    public String activationId() {
        return activationId;
    }

    /**
     * Generates a random activationId. The id must be used in order to activate the user account 
     * but may also be used for other purposes, like reset password confirmation (TODO: change that!)
     */
    public void generateActivationId() {
        activationId = UUID.randomUUID().toString();
    }

    /**
     * Method that will mark the User (Account) as active if the supplied id match the stored activationId
     * @param anActivationId 
     */
    public void activate(String anActivationId) {
        if (activationId() != null && activationId().equals(anActivationId)) {
            activate();
        }
    }

    private void activate() {
        active = true;
    }

    public boolean isActive() {
        return active;
    }

}
