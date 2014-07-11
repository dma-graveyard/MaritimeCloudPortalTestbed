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
        return Response.created(uriOf(uriInfo, user)).entity(user).build();
    }

    private URI uriOf(UriInfo uriInfo, User user) throws UriBuilderException, IllegalArgumentException {
        return uriInfo.getAbsolutePathBuilder().path(user.username()).build();
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
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserDTO> getUsers(@QueryParam("usernamePattern") @DefaultValue("") String usernamePattern) {
        try {
            LOG.warn("TODO: Returning hardcoded list of users for test purposes only! " + usernamePattern);
            List<UserDTO> users = new ArrayList<>();
            List<User> usersRename = identityApplicationService().usersWithUsernameMatching(usernamePattern);
            for (User user : usersRename) {
                users.add(toDto(user));
            }
            
            users.add(getUser("Tintin"));
            users.add(toDto(identityApplicationService().user("Haddock")));
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
        return new UserDTO(user.username(), null, user.emailAddress());
    }

    public static class UserDTO {

        private String username;
        private String password;
        private String emailAddress;

        public UserDTO() {
        }

        public UserDTO(String username, String password, String emailAddress) {
            this.username = username;
            this.password = password;
            this.emailAddress = emailAddress;
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

        @Override
        public String toString() {
            return "UserDTO{" + "username=" + username + ", password=" + password + ", emailAddress=" + emailAddress + '}';
        }
               
        
    }
}