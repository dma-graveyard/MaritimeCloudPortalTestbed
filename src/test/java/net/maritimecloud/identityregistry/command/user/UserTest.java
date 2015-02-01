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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.maritimecloud.identityregistry.command.api.ChangeUserEmailAddress;
import net.maritimecloud.identityregistry.command.api.ChangeUserPassword;
import net.maritimecloud.identityregistry.command.api.RegisterUser;
import net.maritimecloud.identityregistry.command.api.ResetPasswordKeyGenerated;
import net.maritimecloud.identityregistry.command.api.SendResetPasswordInstructions;
import net.maritimecloud.identityregistry.command.api.UnconfirmedUserEmailAddressSupplied;
import net.maritimecloud.identityregistry.command.api.UserAccountActivated;
import net.maritimecloud.identityregistry.command.api.UserEmailAddressVerified;
import net.maritimecloud.identityregistry.command.api.UserPasswordChanged;
import net.maritimecloud.identityregistry.command.api.UserRegistered;
import net.maritimecloud.identityregistry.command.api.VerifyEmailAddress;
import net.maritimecloud.identityregistry.query.internal.InternalUserEntry;
import net.maritimecloud.identityregistry.query.internal.InternalUserQueryRepository;
import net.maritimecloud.portal.domain.model.DomainRegistry;
import org.axonframework.domain.Message;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.axonframework.test.matchers.Matchers;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.allOf;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

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

    @Mock
    InternalUserQueryRepository internalUserQueryRepository;
    
    @Before
    public void setUp() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(User.class);
    }

    @Test
    public void registerUser() {
        fixture.givenNoPriorActivity()
                .when(new RegisterUser(aUserId, A_USERNAME, AN_EMAIL_ADDRESS, A_PASSWORD))
                .expectEventsMatching(
                        aSequenceOf(
                                // note: pass in plain text password here - the matcher needs the original in order to tell of things went well!
                                anEventLike(new UserRegistered(aUserId, A_USERNAME, AN_EMAIL_ADDRESS, A_PASSWORD, AN_EMAIL_ADDRESS_VERIFICATION_CODE))
                        )
                );
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
                .expectEventsMatching(aSequenceOf(
                                // note: we pass in an existing verification code just to make sure it is not reused (see matcher)
                                // ... we expect a new randomly created code 
                                anEventLike(new UnconfirmedUserEmailAddressSupplied(aUserId, A_USERNAME, ANOTHER_EMAIL_ADDRESS, AN_EMAIL_ADDRESS_VERIFICATION_CODE)))
                );
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
                .expectEventsMatching(aSequenceOf(
                                anEventLike(new UserPasswordChanged(aUserId, A_USERNAME, A_CHANGED_PASSWORD)))
                );
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

    @Test
    public void changePasswordShouldFailWhenUsingWrongPassword() {
        fixture.given(new UserRegistered(aUserId, A_USERNAME, AN_EMAIL_ADDRESS, A_PASSWORD_OBFUSCATED, AN_EMAIL_ADDRESS_VERIFICATION_CODE))
                .when(new ChangeUserPassword(aUserId, A_PASSWORD+"THAT_IS_WRONG", A_CHANGED_PASSWORD))
                .expectException(IllegalArgumentException.class);
    }

    @Test
    public void resetPassword() {

        MockitoAnnotations.initMocks(this);
        InternalUserEntry aUserEntry = new InternalUserEntry();
        aUserEntry.setUserId(aUserId.identifier());
        aUserEntry.setUsername(A_USERNAME);
        aUserEntry.setEmailAddress(AN_EMAIL_ADDRESS);

        Mockito.when(internalUserQueryRepository.findByEmailAddressIgnoreCase(any())).thenReturn(aUserEntry);
        UserCommandHandler commandHandler = new UserCommandHandler(fixture.getRepository(), internalUserQueryRepository);
        fixture.registerAnnotatedCommandHandler(commandHandler);

        fixture.given(
                new UserRegistered(aUserId, A_USERNAME, AN_EMAIL_ADDRESS, A_PASSWORD_OBFUSCATED, AN_EMAIL_ADDRESS_VERIFICATION_CODE),
                new UserEmailAddressVerified(aUserId, A_USERNAME, AN_EMAIL_ADDRESS)
        )
                .when(new SendResetPasswordInstructions(AN_EMAIL_ADDRESS))
                .expectEventsMatching(
                        aSequenceOf(
                                anEventLike(new ResetPasswordKeyGenerated(aUserId, A_USERNAME, AN_EMAIL_ADDRESS, "dummy reset code"))
                        )
                );
    }

    // ------------------------------------------------------------------------
    // HELPER MATCHERS
    // ------------------------------------------------------------------------
    @Factory
    public static Matcher<List<? extends Message<?>>> aSequenceOf(Matcher<?>... matchers) {
        Matcher<?>[] terminatedListOfMatchers = Arrays.copyOf(matchers, matchers.length + 1);
        terminatedListOfMatchers[matchers.length] = Matchers.andNoMore();
        return Matchers.payloadsMatching(Matchers.exactSequenceOf(terminatedListOfMatchers));
    }

    @Factory
    public static <T> Matcher<ResetPasswordKeyGenerated> anEventLike(ResetPasswordKeyGenerated event) {
        return allOf(
                instanceOf(ResetPasswordKeyGenerated.class),
                hasProperty("userId", equalTo(event.getUserId())),
                hasProperty("username", equalTo(event.getUsername())),
                hasProperty("emailAddress", equalTo(event.getEmailAddress())),
                hasProperty("resetPasswordKey")
        );
    }

    @Factory
    public static <T> Matcher<UserPasswordChanged> anEventLike(UserPasswordChanged sourceEvent) {
        return new IsUserPasswordChangedEventMatcher(sourceEvent);
    }

    @Factory
    public static <T> Matcher<UserRegistered> anEventLike(UserRegistered sourceEvent) {
        return new IsSameUserRegisteredEventMatcher(sourceEvent);
    }

    @Factory
    public static <T> Matcher<UnconfirmedUserEmailAddressSupplied> anEventLike(UnconfirmedUserEmailAddressSupplied sourceEvent) {
        return new IsSameUnconfirmedUserEmailAddressSuppliedEventMatcher(sourceEvent);
    }

    public static class IsSameUserRegisteredEventMatcher extends TypeSafeMatcher<UserRegistered> {

        private final UserRegistered sourceEvent;

        public IsSameUserRegisteredEventMatcher(UserRegistered sourceEvent) {
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

    public static class IsUserPasswordChangedEventMatcher extends TypeSafeMatcher<UserPasswordChanged> {

        private final UserPasswordChanged sourceEvent;

        public IsUserPasswordChangedEventMatcher(UserPasswordChanged sourceEvent) {
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

    public static class IsSameUnconfirmedUserEmailAddressSuppliedEventMatcher extends TypeSafeMatcher<UnconfirmedUserEmailAddressSupplied> {

        private final UnconfirmedUserEmailAddressSupplied sourceEvent;

        public IsSameUnconfirmedUserEmailAddressSuppliedEventMatcher(UnconfirmedUserEmailAddressSupplied sourceEvent) {
            this.sourceEvent = sourceEvent;
        }

        @Override
        public boolean matchesSafely(UnconfirmedUserEmailAddressSupplied event) {
            return sourceEvent.getUserId().equals(event.getUserId())
                    && sourceEvent.getUsername().equals(event.getUsername())
                    && sourceEvent.getUnconfirmedEmailAddress().equals(event.getUnconfirmedEmailAddress())
                    // *NOTE* silly test that it is not using a predictable verificationcode, like the one from a previous verification
                    && !sourceEvent.getEmailVerificationCode().equals(event.getEmailVerificationCode());
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("UnconfirmedUserEmailAddressSupplied event is not the same");
        }

    }
}
