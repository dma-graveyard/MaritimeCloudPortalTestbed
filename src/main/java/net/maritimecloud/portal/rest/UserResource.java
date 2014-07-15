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
package net.maritimecloud.portal.rest;

import net.maritimecloud.portal.domain.model.identity.UserService;
import java.net.URI;
import java.util.List;
import javax.annotation.Resource;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;
import net.maritimecloud.portal.application.ApplicationServiceRegistry;
import net.maritimecloud.portal.application.IdentityApplicationService;
import net.maritimecloud.portal.model.user.User;
import org.springframework.stereotype.Component;

/**
 * @author Christoffer Børrild
 * @deprecated
 */
@Path("/old/users")
@Component
public class UserResource {

    @Resource
    UserService userService;
    
    protected IdentityApplicationService identityApplicationService() {
        return ApplicationServiceRegistry.identityApplicationService();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createUser(User user, @Context UriInfo uriInfo) {
        User newUser = userService.createUser(user);
        return createResponseWithNewUserAndUri(uriInfo, newUser);
    }

    private Response createResponseWithNewUserAndUri(UriInfo uriInfo, User user) throws UriBuilderException, IllegalArgumentException {
        return Response.created(uriOf(uriInfo, user)).entity(user).build();
    }

    private URI uriOf(UriInfo uriInfo, User user) throws UriBuilderException, IllegalArgumentException {
        return uriInfo.getAbsolutePathBuilder().path(Long.toString(user.getId())).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getUsers() {
        try {
            return userService.findUsers();
        } catch (Throwable e) {
            System.out.println("e:" + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GET
    @Path("{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(@PathParam("id") long id) {
        
        System.out.println("Called getUser with id " + id);
        
        User user = userService.findUserWithId(id);
        
        if (user == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        
        return user;
    }

}
