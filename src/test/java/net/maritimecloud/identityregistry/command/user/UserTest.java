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
package net.maritimecloud.identityregistry.command.user;

import java.util.UUID;
import net.maritimecloud.identityregistry.command.api.ChangeUserEmailAddress;
import net.maritimecloud.identityregistry.command.api.ChangeUserPassword;
import net.maritimecloud.identityregistry.command.api.RegisterUser;
import net.maritimecloud.identityregistry.command.api.UnconfirmedUserEmailAddressSupplied;
import net.maritimecloud.identityregistry.command.api.UserAccountActivated;
import net.maritimecloud.identityregistry.command.api.UserEmailAddressVerified;
import net.maritimecloud.identityregistry.command.api.UserPasswordChanged;
import net.maritimecloud.identityregistry.command.api.UserRegistered;
import net.maritimecloud.identityregistry.command.api.VerifyEmailAddress;
import net.maritimecloud.portal.domain.model.DomainRegistry;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.axonframework.test.matchers.Matchers;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Christoffer Børrild
 */
public class UserTest {

    private FixtureConfiguration<User> fixture;

    private static final String A_USERNAME = "A_USERNAME";
    private static final String A_TOO_SHORT_USERNAME = "UN";
    private static final String A_PASSWORD = "A_PASSWORD";
    private static final String A_PASSWORD_OBFUSCATED = "$shiro1$SHA-512$1000$7OBI6Fc/9eYGe0CpprAjJA==$8SO4YOxn8NozJ8H4DOFpjZPUDTnp3JqbnVHIC5h2ya7x2LPB0exWsTQ0Rb0HOrxWhsFqf6M1+S/bQZ70FPlm4g==";
    private static final String A_CHANGED_PASSWORD = "A_CHANGED_PASSWORD";
    private static final String A_CHANGED_PASSWORD_OBFUSCATED = "$shiro1$SHA-512$1000$BnBwaRWsOVgucFfAqmXVuQ==$sFL4P1rf3n6XrL9VSRvJ5D3T7X+254jeIf4KfNSxslnqy1Rw3D5va52EMViYMX4rK1fxi0nBlWV6LDsEmQezWw==";
    private static final String AN_EMAIL_ADDRESS = "A@VALID.EMAIL";
    private static final String ANOTHER_EMAIL_ADDRESS = "ANOTHER@VALID.EMAIL";
    private static final String AN_INVALID_EMAIL = "AN_INVALID_EMAIL";
    private static final String AN_EMAIL_ADDRESS_VERIFICATION_CODE = UUID.randomUUID().toString();
    private static final String ANOTHER_EMAIL_ADDRESS_VERIFICATION_CODE = UUID.randomUUID().toString();
    private static final UserId aUserId = new UserId(UUID.randomUUID().toString());

    @Before
    public void setUp() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(User.class);
    }

    @Test
    public void registerUser() {
        fixture.givenNoPriorActivity()
                .when(new RegisterUser(aUserId, A_USERNAME, AN_EMAIL_ADDRESS, A_PASSWORD))
                .expectEventsMatching(
                        Matchers.payloadsMatching(
                                Matchers.exactSequenceOf(
                                        // note: pass in plain text password here - the matcher needs the original in order to tell of things went well!
                                        isSameUserRegisteredEvent(new UserRegistered(aUserId, A_USERNAME, AN_EMAIL_ADDRESS, A_PASSWORD, AN_EMAIL_ADDRESS_VERIFICATION_CODE)),
                                        Matchers.andNoMore()
                                )
                        ));
    }

    @Test
    public void verifyEmailAddressShouldActivateUserAccountOnFirstOccurence() {
        fixture.given(new UserRegistered(aUserId, A_USERNAME, AN_EMAIL_ADDRESS, A_PASSWORD_OBFUSCATED, AN_EMAIL_ADDRESS_VERIFICATION_CODE))
                .when(new VerifyEmailAddress(aUserId, AN_EMAIL_ADDRESS_VERIFICATION_CODE))
                .expectEvents(
                        new UserEmailAddressVerified(aUserId, A_USERNAME, AN_EMAIL_ADDRESS),
                        new UserAccountActivated(aUserId, A_USERNAME)
                );
    }

    @Test
    public void verifyEmailAddressShouldBeIgnoredIfRepeated() {
        fixture.given(
                new UserRegistered(aUserId, A_USERNAME, AN_EMAIL_ADDRESS, A_PASSWORD_OBFUSCATED, AN_EMAIL_ADDRESS_VERIFICATION_CODE),
                new UserEmailAddressVerified(aUserId, A_USERNAME, AN_EMAIL_ADDRESS)
        )
                .when(new VerifyEmailAddress(aUserId, AN_EMAIL_ADDRESS_VERIFICATION_CODE))
                .expectEvents();
    }

    @Test
    public void changeUserEmailAddress() {
        fixture.given(
                new UserRegistered(aUserId, A_USERNAME, AN_EMAIL_ADDRESS, A_PASSWORD_OBFUSCATED, AN_EMAIL_ADDRESS_VERIFICATION_CODE),
                new UserEmailAddressVerified(aUserId, A_USERNAME, AN_EMAIL_ADDRESS)
        )
                .when(new ChangeUserEmailAddress(aUserId, ANOTHER_EMAIL_ADDRESS))
                .expectEventsMatching(
                        Matchers.payloadsMatching(
                                Matchers.exactSequenceOf(
                                        // note: we pass in an existing verification code just to make sure it is not reused (see matcher)
                                        // ... we expect a new randomly created code 
                                        isSameUnconfirmedUserEmailAddressSuppliedEvent(
                        new UnconfirmedUserEmailAddressSupplied(aUserId, A_USERNAME, ANOTHER_EMAIL_ADDRESS, AN_EMAIL_ADDRESS_VERIFICATION_CODE)
                                        ),
                                        Matchers.andNoMore()
                                )
                        ));
    }

    @Test
    public void verifyEmailAddressShouldFailOnInvalidCode() {
        fixture.given(new UserRegistered(aUserId, A_USERNAME, AN_EMAIL_ADDRESS, A_PASSWORD_OBFUSCATED, AN_EMAIL_ADDRESS_VERIFICATION_CODE))
                .when(new VerifyEmailAddress(aUserId, ANOTHER_EMAIL_ADDRESS_VERIFICATION_CODE))
                .expectException(IllegalArgumentException.class);
    }

    @Test
    public void registerUserShouldFailOnInvalidEmail() {
        fixture.givenNoPriorActivity()
                .when(new RegisterUser(aUserId, A_USERNAME, AN_INVALID_EMAIL, A_PASSWORD))
                .expectException(IllegalArgumentException.class);
    }

    @Test
    public void registerUserShouldFailOnTooShortUsername() {
        fixture.givenNoPriorActivity()
                .when(new RegisterUser(aUserId, A_TOO_SHORT_USERNAME, AN_INVALID_EMAIL, A_PASSWORD))
                .expectException(IllegalArgumentException.class);
    }

    @Test
    public void changePassword() {
        fixture.given(new UserRegistered(aUserId, A_USERNAME, AN_EMAIL_ADDRESS, A_PASSWORD_OBFUSCATED, AN_EMAIL_ADDRESS_VERIFICATION_CODE))
                .when(new ChangeUserPassword(aUserId, A_PASSWORD, A_CHANGED_PASSWORD))
                //.expectEvents(new UserPasswordChanged(aUserId, A_USERNAME, A_CHANGED_PASSWORD_OBFUSCATED));
                // we have to use a customized matcher since we cannot reproduce to the same obfuscated password
                .expectEventsMatching(
                        Matchers.payloadsMatching(
                                Matchers.exactSequenceOf(
                                        // note: pass in plain text password here - the matcher needs the original in order to compare with obfuscated password
                                        isUserPasswordChangedEvent(new UserPasswordChanged(aUserId, A_USERNAME, A_CHANGED_PASSWORD)),
                                        Matchers.andNoMore()
                                )
                        ));
    }

    @Test
    public void changePasswordShouldFailWhenEqualToUsername() {
        fixture.given(new UserRegistered(aUserId, A_USERNAME, AN_EMAIL_ADDRESS, A_PASSWORD_OBFUSCATED, AN_EMAIL_ADDRESS_VERIFICATION_CODE))
                .when(new ChangeUserPassword(aUserId, A_PASSWORD, "A_USERNAME"))
                .expectException(IllegalArgumentException.class);
    }

    @Test
    public void changePasswordShouldFailWhenEqualToOldPassword() {
        fixture.given(new UserRegistered(aUserId, A_USERNAME, AN_EMAIL_ADDRESS, A_PASSWORD_OBFUSCATED, AN_EMAIL_ADDRESS_VERIFICATION_CODE))
                .when(new ChangeUserPassword(aUserId, A_PASSWORD, A_PASSWORD))
                .expectException(IllegalArgumentException.class);
    }

    // ------------------------------------------------------------------------
    // HELPER MATCHERS
    // ------------------------------------------------------------------------
    @Factory
    public static <T> Matcher<UserPasswordChanged> isUserPasswordChangedEvent(UserPasswordChanged sourceEvent) {
        return new IsUserPasswordChangedEvent(sourceEvent);
    }

    @Factory
    public static <T> Matcher<UserRegistered> isSameUserRegisteredEvent(UserRegistered sourceEvent) {
        return new IsSameUserRegisteredEvent(sourceEvent);
    }

    @Factory
    public static <T> Matcher<UnconfirmedUserEmailAddressSupplied> isSameUnconfirmedUserEmailAddressSuppliedEvent(UnconfirmedUserEmailAddressSupplied sourceEvent) {
        return new IsSameUnconfirmedUserEmailAddressSuppliedEvent(sourceEvent);
    }

    public static class IsSameUserRegisteredEvent extends TypeSafeMatcher<UserRegistered> {

        private UserRegistered sourceEvent;

        public IsSameUserRegisteredEvent(UserRegistered sourceEvent) {
            this.sourceEvent = sourceEvent;
        }

        @Override
        public boolean matchesSafely(UserRegistered event) {
            return sourceEvent.getPrefferedUsername().equals(event.getPrefferedUsername())
                    && sourceEvent.getEmailAddress().equals(event.getEmailAddress())
                    && sourceEvent.getUserId().equals(event.getUserId())
                    // *NOTE* the password of the sourceEvent MUST be plaintext!!!
                    && DomainRegistry.encryptionService().valuesMatch(sourceEvent.getObfuscatedPassword(), event.getObfuscatedPassword());
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("UserRegistered event is not the same");
        }

    }

    public static class IsUserPasswordChangedEvent extends TypeSafeMatcher<UserPasswordChanged> {

        private UserPasswordChanged sourceEvent;

        public IsUserPasswordChangedEvent(UserPasswordChanged sourceEvent) {
            this.sourceEvent = sourceEvent;
        }

        @Override
        public boolean matchesSafely(UserPasswordChanged event) {
            return sourceEvent.getUserId().equals(event.getUserId())
                    && sourceEvent.getUsername().equals(event.getUsername())
                    // *NOTE* the password of the sourceEvent MUST be plaintext!!!
                    && DomainRegistry.encryptionService().valuesMatch(sourceEvent.getObfuscatedChangedPassword(), event.getObfuscatedChangedPassword());
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("UserPasswordChanged event is not the same");
        }

    }

    public static class IsSameUnconfirmedUserEmailAddressSuppliedEvent extends TypeSafeMatcher<UnconfirmedUserEmailAddressSupplied> {

        private UnconfirmedUserEmailAddressSupplied sourceEvent;

        public IsSameUnconfirmedUserEmailAddressSuppliedEvent(UnconfirmedUserEmailAddressSupplied sourceEvent) {
            this.sourceEvent = sourceEvent;
        }

        @Override
        public boolean matchesSafely(UnconfirmedUserEmailAddressSupplied event) {
            return sourceEvent.getUserId().equals(event.getUserId())
                    && sourceEvent.getUsername().equals(event.getUsername())
                    && sourceEvent.getUnconfirmedEmailAddress().equals(event.getUnconfirmedEmailAddress())
                    // *NOTE* silly test that it is not using a predictable verificationcaode, like the one from a previous verification
                    && !sourceEvent.getEmailVerificationCode().equals(event.getEmailVerificationCode());
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("UnconfirmedUserEmailAddressSupplied event is not the same");
        }

    }
}
