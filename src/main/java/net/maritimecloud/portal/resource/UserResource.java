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
package net.maritimecloud.portal.resource;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;
import net.maritimecloud.portal.application.ApplicationServiceRegistry;
import net.maritimecloud.portal.application.IdentityApplicationService;
import net.maritimecloud.portal.domain.model.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christoffer Børrild
 */
@Path("/users")
public class UserResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserResource.class);

    protected IdentityApplicationService identityApplicationService() {
        return ApplicationServiceRegistry.identityApplicationService();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(UserDTO aUser, @Context UriInfo uriInfo) {
        LOG.warn("Called createUser with user " + aUser);
        User newUser = identityApplicationService().registerUser(aUser.getUsername(), aUser.getPassword(), aUser.getEmailAddress());
        return createResponseWithNewUserAndUri(uriInfo, newUser);
    }

    private Response createResponseWithNewUserAndUri(UriInfo uriInfo, User user) throws UriBuilderException, IllegalArgumentException {
        return Response.created(uriOf(uriInfo, user)).entity(toDto(user)).build();
    }

    private URI uriOf(UriInfo uriInfo, User user) throws UriBuilderException, IllegalArgumentException {
        return uriInfo.getAbsolutePathBuilder().path(user.username()).build();
    }

    @POST
    @Path("{username}/activate/{activationId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public UserAccountActivatedDTO activateAccount(@PathParam("username") String aUsername, @PathParam("activationId") String activationId, @Context UriInfo uriInfo) {
        LOG.warn("Called activate account with user " + aUsername + " " + activationId + " " + uriInfo);

        boolean activated = identityApplicationService().activate(aUsername, activationId);
        return new UserAccountActivatedDTO(activated);
    }

    @GET
    @Path("{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO getUser(@PathParam("username") String aUsername) {
        LOG.debug("Called getUser with username " + aUsername);

        LOG.warn("TODO: We should probably not expose this service publicly. Only logged in users should be able to get user info!!! ");

        User user = identityApplicationService().user(aUsername);

        if (user == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return toDto(user);
    }

    @GET
    @Path("{username}/exist")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO getUsernameExist(@PathParam("username") String username) {
        return usernameExist(username) ? UserDTO.USERNAME_EXIST : UserDTO.USERNAME_IS_UNKNOWN;
    }

    private boolean usernameExist(String username) {
        return identityApplicationService().user(username) != null;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserDTO> getUsers(
            @QueryParam("usernamePattern") @DefaultValue("") String usernamePattern
    ) {
        try {
            List<UserDTO> users = new ArrayList<>();
            List<User> matchingUsers = identityApplicationService().usersWithUsernameMatching(usernamePattern);
            for (User user : matchingUsers) {
                users.add(toDto(user));
            }
            return users;
        } catch (Throwable e) {
            System.out.println("e:" + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GET
    @Path("/hello")
    @Produces(MediaType.APPLICATION_JSON)
    public String getHelloWorld() {
        return "hej";
    }

    private UserDTO toDto(User user) {
        return new UserDTO(user.username(), null, user.emailAddress(), user.isActive());
    }

    public static class UserDTO {

        private String username;
        private String password;
        private String emailAddress;
        private Boolean usernameExist;
        private boolean active;

        public static final UserDTO USERNAME_EXIST = new UserDTO(true);
        public static final UserDTO USERNAME_IS_UNKNOWN = new UserDTO(false);

        public UserDTO() {
        }

        public UserDTO(boolean usernameExist) {
            this.usernameExist = usernameExist;
        }

        public UserDTO(String username, String password, String emailAddress, boolean isActive) {
            this.username = username;
            this.password = password;
            this.emailAddress = emailAddress;
            this.active = isActive;
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

        public Boolean getUsernameExist() {
            return usernameExist;
        }

        public void setUsernameExist(Boolean usernameExist) {
            this.usernameExist = usernameExist;
        }

        public void setActive(boolean isActive) {
            this.active = isActive;
        }

        public boolean isActive() {
            return active;
        }

        @Override
        public String toString() {
            return "UserDTO{" + "username=" + username + ", password=" + password + ", emailAddress=" + emailAddress + ", usernameExist=" + usernameExist + ", active=" + active + '}';
        }

    }

    public static class UserAccountActivatedDTO {

        private boolean accountActivated;

        public UserAccountActivatedDTO() {
        }

        public UserAccountActivatedDTO(boolean accountActivated) {
            this.accountActivated = accountActivated;
        }

        public void setAccountActivated(boolean accountActivated) {
            this.accountActivated = accountActivated;
        }

        public boolean getAccountActivated() {
            return accountActivated;
        }
    }
}
