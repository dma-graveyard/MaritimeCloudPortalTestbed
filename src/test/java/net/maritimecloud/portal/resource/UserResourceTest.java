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

import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import static javax.ws.rs.client.Entity.json;
import net.maritimecloud.portal.domain.model.DomainRegistry;
import net.maritimecloud.portal.domain.model.identity.User;
import static net.maritimecloud.portal.resource.UserResource.UserDTO;
import org.junit.Ignore;

/**
 * @author Christoffer Børrild
 */
@Ignore
public class UserResourceTest extends ResourceTest {

    /**
     * user resource path
     */
    private static final String USERS = "users";

    @Test
    public void createUser() throws JSONException {
        UserDTO aNewUser = new UserDTO("Luke", "aPassword", "luke@skywalker.com", true);
        final String actual = target(USERS).request().post(json(aNewUser), String.class);
        System.out.println("actual: " + actual);
        String expected = asJson(
                "username", aNewUser.getUsername(),
                "emailAddress", aNewUser.getEmailAddress()
        );
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
    }

    @Test
    public void listOfUsers() throws JSONException {
        // Given an existing user
        User aUser = this.aUser();
        DomainRegistry.userRepository().add(aUser);
        // When I query for a list of users matching the existing username
        final String actual = target(USERS).queryParam("usernamePattern", aUser.username()).request().get(String.class);
        // I should get a list with one member matching the existing user
        String expected = array(asJson("emailAddress", aUser.emailAddress(), "username", aUser.username()));
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
    }

    @Test
    public void singleUser() throws JSONException {
        // Given an existing user
        User aUser = this.aUser();
        DomainRegistry.userRepository().add(aUser);
        // When I navigate to the users subpath 
        final String actual = target(USERS).path(aUser.username()).request().get(String.class);
        // then I should get the user information
        String expected = asJson("emailAddress", aUser.emailAddress(), "username", aUser.username());
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
    }
}
