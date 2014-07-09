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

import java.util.Arrays;
import java.util.regex.Pattern;
import net.maritimecloud.portal.domain.model.ConcurrencySafeEntity;

/**
 * @author Christoffer Børrild
 */
public class User extends ConcurrencySafeEntity {

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String passwordEncryptionSalt;
//    private Person person;
    private String emailAddress;

    private static long idSequence = 1;
    // overrides IndentifiedDomainObject id
    private long id;
    private UserRoles userRoles;

    /**
     * @return the application specific unique id of the user
     */
    @Override
    public long id() {
//        return super.id();
        return id;
    }

    public void changePassword(String aCurrentPassword, String aChangedPassword) {
        this.assertArgumentNotEmpty(
                aCurrentPassword,
                "Current and new password must be provided.");

        this.assertArgumentEquals(
                this.password(),
                this.asEncryptedValue(aCurrentPassword),
                "Current password not confirmed.");

        this.protectPassword(aCurrentPassword, aChangedPassword);

//        DomainEventPublisher
//            .instance()
//            .publish(new UserPasswordChanged(
//                    this.tenantId(),
//                    this.username()));
    }

//    public void changePersonalContactInformation(ContactInformation aContactInformation) {
//        this.person().changeContactInformation(aContactInformation);
//    }
//
//    public void changePersonalName(FullName aPersonalName) {
//        this.person().changeName(aPersonalName);
//    }
//    public Person person() {
//        return this.person;
//    }
//    public UserDescriptor userDescriptor() {
//        return new UserDescriptor(
//                this.username(),
//                this.person().emailAddress().address());
//    }
    public String username() {
        return this.username;
    }

    public String emailAddress() {
        return emailAddress;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            User typedObject = (User) anObject;
            equalObjects
                    = this.username().equals(typedObject.username());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue
                = +(45217 * 269)
                + this.username().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "User [username=" + username
                //+ ", person=" + person 
                + ", emailAddress=" + emailAddress
                + "]";
    }

    public User(String aUsername, String aPassword /*,Person aPerson*/, String emailAddress, Role... roles) {

        this();

        // TODO: hack? generate id using a UserIdService or something...
        this.id = idSequence++;

        if (roles.length == 0) {
            this.userRoles = new UserRoles(Role.USER);
        } else {
            this.userRoles = new UserRoles(roles);
        }

//        this.setPerson(aPerson);
        this.setUsername(aUsername);

        this.protectPassword("", aPassword);

//        aPerson.internalOnlySetUser(this);
        this.setEmailAddress(emailAddress);

        this.passwordEncryptionSalt = "salt";
//        DomainEventPublisher
//            .instance()
//            .publish(new UserRegistered(
//                    aUsername,
//                    aPerson.name(),
//                    aPerson.contactInformation().emailAddress()));
    }

    protected User() {
        super();
    }

    protected String asEncryptedValue(String aPlainTextPassword) {
        System.out.println("WARNING: Using unencrypted passwords!!! TODO - fix this !!!");
        String encryptedValue = aPlainTextPassword;
//        String encryptedValue =
//            DomainRegistry
//                .encryptionService()
//                .encryptedValue(aPlainTextPassword);
//
        return encryptedValue;
    }

    protected void assertPasswordsNotSame(String aCurrentPassword, String aChangedPassword) {
        this.assertArgumentNotEquals(
                aCurrentPassword,
                aChangedPassword,
                "The password is unchanged.");
    }

    protected void assertPasswordNotWeak(String aPlainTextPassword) {
//        this.assertArgumentFalse(
//                DomainRegistry.passwordService().isWeak(aPlainTextPassword),
//                "The password must be stronger.");
    }

    protected void assertUsernamePasswordNotSame(String aPlainTextPassword) {
        this.assertArgumentNotEquals(
                this.username(),
                aPlainTextPassword,
                "The username and password must not be the same.");
    }

    public String internalAccessOnlyEncryptedPassword() {
        return this.password();
    }

    protected String password() {
        return this.password;
    }

    protected void setPassword(String aPassword) {
        this.password = aPassword;
    }

//    protected void setPerson(Person aPerson) {
//        this.assertArgumentNotNull(aPerson, "The person is required.");
//
//        this.person = aPerson;
//    }
    protected void protectPassword(String aCurrentPassword, String aChangedPassword) {
        this.assertPasswordsNotSame(aCurrentPassword, aChangedPassword);

        this.assertPasswordNotWeak(aChangedPassword);

        this.assertUsernamePasswordNotSame(aChangedPassword);

        this.setPassword(this.asEncryptedValue(aChangedPassword));
    }

//    protected GroupMember toGroupMember() {
//        GroupMember groupMember =
//            new GroupMember(
//                    this.username(),
//                    GroupMemberType.User);
//
//        return groupMember;
//    }
    protected void setUsername(String aUsername) {
        this.assertArgumentNotEmpty(aUsername, "The username is required.");
        this.assertArgumentLength(aUsername, 3, 250, "The username must be 3 to 250 characters.");

        this.username = aUsername;
    }

    public void setEmailAddress(String anEmailAddress) {
        this.assertArgumentNotEmpty(anEmailAddress, "The email address is required.");
        this.assertArgumentLength(anEmailAddress, 1, 100, "Email address must be 100 characters or less.");
        this.assertArgumentTrue(
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

}
