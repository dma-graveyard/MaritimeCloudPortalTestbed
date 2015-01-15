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
package net.maritimecloud.portal.resource;

import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.maritimecloud.portal.application.ApplicationServiceRegistry;
import net.maritimecloud.portal.application.IdentityApplicationService;
import net.maritimecloud.portal.domain.model.identity.UnknownUserException;
import net.maritimecloud.portal.domain.model.identity.User;
import net.maritimecloud.portal.domain.model.security.AuthenticationException;
import net.maritimecloud.portal.domain.model.security.AuthenticationUtil;
import net.maritimecloud.portal.domain.model.security.UserNotLoggedInException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service resource for authenticating a user and getting associated roles etc...
 * <p>
 * @author Christoffer Børrild
 */
@Path("/authentication")
public class AuthenticationResource {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationResource.class);

    protected AuthenticationUtil authenticationUtil() {
        return ApplicationServiceRegistry.authenticationUtil();
    }

    protected IdentityApplicationService identityApplicationService() {
        return ApplicationServiceRegistry.identityApplicationService();
    }

    protected LogService logService() {
        return ApplicationServiceRegistry.logService();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SubjectDTO login(CredentialsDTO credentials) {

        assertCredentialsNotNull(credentials);

        // TODO HACK: temporary hack to test error 
        if (credentials.getUsername().equalsIgnoreCase("error")) {
            throw new UserNotAuthenticated();
        }

        return tryLogin(credentials);
    }

    private SubjectDTO tryLogin(CredentialsDTO credentials) throws UserNotAuthenticated {
        try {
            return doLogin(credentials);
        } catch (AuthenticationException e) {
            reportWrongUsernamePassword(credentials);
            throw new UserNotAuthenticated();
        }
    }

    private SubjectDTO doLogin(CredentialsDTO credentials) throws AuthenticationException {
        authenticationUtil().login(credentials.username, credentials.password);
        reportUserLoggedIn(credentials.username);
        return currentSubject();
    }

    private void assertCredentialsNotNull(CredentialsDTO credentials) throws IllegalArgumentException {
        if (credentials == null) {
            throw new IllegalArgumentException();
        }

        if (credentials.getUsername() == null) {
            throw new IllegalArgumentException();
        }
    }

    @POST
    @Path("/logout")
    public void logout() {
        logService().reportUserLoggingOut();
        authenticationUtil().logout();
    }

    @POST
    @Path("/sendforgot")
    @Consumes(MediaType.APPLICATION_JSON)
    public void sendResetPasswordInstructions(CredentialsDTO credentials) {
        LOG.debug("Send reset password instructions email to " + credentials.getEmailAddress());
        identityApplicationService().sendResetPasswordMessage(credentials.getEmailAddress());
    }

    @POST
    @Path("/reset")
    @Consumes(MediaType.APPLICATION_JSON)
    public void resetPassword(CredentialsDTO credentials) {
        identityApplicationService().resetPassword(credentials.getUsername(), credentials.getVerificationId(), credentials.getPassword());
    }

    private void reportWrongUsernamePassword(CredentialsDTO credentials) {
        LOG.debug("User {} not logged in (wrong username / password)", credentials.username);
        logService().reportWrongUsernamePassword(credentials.username);
    }

    private void reportUserLoggedIn(String username) {
        LOG.debug("User {} logged in", username);
        logService().reportUserLoggedIn(username);
    }

    private void reportCurrentSubjectNotAuthenticated(java.lang.Exception e) {
        LOG.debug("Current user is not authenticated: ", e);
        logService().reportDebugError("Current user is not authenticated: ", e);
    }

    @GET
    @Path("/sink.html")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String sinkSubmitsFromPasswordManagers() {
        // dummy sink to consume "form submits" from browser password managers
        return "";
    }

    @POST
    @Path("/sink.html")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String sinkSubmitsFromPasswordManagersPost() {
        // dummy sink to consume "form submits" from browser password managers
        return "";
    }

    @GET
    @Path("/currentsubject")
    @Produces(MediaType.APPLICATION_JSON)
    public SubjectDTO currentSubject() {

        try {
            long userId = authenticationUtil().getUserId();
            User user = identityApplicationService().user(userId);
            return createSubject(user);
        } catch (UserNotLoggedInException | UnknownUserException e) {
            reportCurrentSubjectNotAuthenticated(e);
            throw new UserNotAuthenticated();
        }
    }

    private SubjectDTO createSubject(User user) {
        return new SubjectDTO(user.username(), extractRolenamesAsArrayOfStrings(user));
    }

    private String[] extractRolenamesAsArrayOfStrings(User user) {
        Set<String> roles = user.userRoles().all().stream().map(role -> role.name()).collect(Collectors.toSet());
        return roles.toArray(new String[0]);
    }

    /**
     * This is the representation of the logged in user as sent back to the clients
     */
    public static class SubjectDTO {

        private String username;
        private String[] roles;

        public SubjectDTO() {
        }

        public SubjectDTO(String username, String[] roles) {
            this.username = username;
            this.roles = roles;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String[] getRoles() {
            return roles;
        }

        public void setRoles(String[] roles) {
            this.roles = roles;
        }
    }

    public static class CredentialsDTO {

        String username;
        String password;
        String emailAddress;
        String verificationId;

        public CredentialsDTO() {
        }

        public CredentialsDTO(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmailAddress() {
            return emailAddress;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public String getVerificationId() {
            return verificationId;
        }

        public void setVerificationId(String verificationId) {
            this.verificationId = verificationId;
        }

        @Override
        public String toString() {
            return "CredentialsDTO{" + "username=" + username + ", password=" + password + '}';
        }

    }

    public static class UserNotAuthenticated extends WebApplicationException {

        public UserNotAuthenticated() {
            super(Response.Status.UNAUTHORIZED);
        }
    }

}
