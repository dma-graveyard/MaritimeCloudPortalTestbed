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
package net.maritimecloud.identityregistry.resource;

import javax.ws.rs.NotFoundException;
import net.maritimecloud.common.infrastructure.axon.CommonFixture;
import net.maritimecloud.identityregistry.query.UserEntry;
import net.maritimecloud.identityregistry.query.UserQueryRepository;
import net.maritimecloud.portal.application.ApplicationServiceRegistry;
import net.maritimecloud.portal.resource.ResourceTest;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 *
 * @author Christoffer Børrild
 */
public class UserResourceTest extends ResourceTest {

    /**
     * user resource path
     */
    private static final String API_USERS = "api/users";

    UserQueryRepository userQueryRepository;

    @Test
    public void createUser() throws JSONException {
//        UserDTO aNewUser = new net.maritimecloud.portal.resource.UserResource.UserDTO("Luke", "aPassword", "luke@skywalker.com", true);
//        final String actual = target(API_USERS).request().post(json(aNewUser), String.class);
//        System.out.println("actual: " + actual);
//        String expected = asJson(
//                "username", aNewUser.getUsername(),
//                "emailAddress", aNewUser.getEmailAddress()
//        );
//        JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
    }

    @Test
    public void listOfUsers() throws JSONException {
        // Given an existing user

        // ...insert users in view model
        UserEntry aUser = new UserEntry();
        aUser.setUsername(CommonFixture.A_NAME);
        aUser.setEmailAddress("an@eamil.com");
        aUser.setUserId("A_USER_ID");
        aUser.setIsActivated(true);
        ApplicationServiceRegistry.userQueryRepository().save(aUser);

        // When I query for a list of users matching the existing username
        final String actual = target(API_USERS).queryParam("usernamePattern", aUser.getUsername()).request().get(String.class);

        // I should get a page object with a content containng a list with a user matching the existing user
        //String expected = asJson("emailAddress", aUser.getEmailAddress(), "username", aUser.getUsername());
        String expected = "{"
                + "\"totalElements\":1,"
                + "\"totalPages\":1,"
                + "\"size\":10,"
                + "\"number\":0,"
                + "\"content\":"
                + "[{"
                + "\"userId\":\"A_USER_ID\","
                + "\"username\":\"a name\","
                + "\"emailAddress\":\"an@eamil.com\","
                + "\"isActivated\":true,"
                + "\"activatedSince\":null"
                + "}],"
                + "\"sort\":[{\"direction\":\"DESC\","
                + "\"property\":\"username\","
                + "\"ignoreCase\":false,"
                + "\"nullHandling\":\"NATIVE\","
                + "\"ascending\":false}],"
                + "\"first\":true,"
                + "\"numberOfElements\":1,"
                + "\"last\":true}";
        System.out.println("actual:" + actual);
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
    }

    @Test(expected = NotFoundException.class)
    public void singleUser() throws JSONException {
        // Given a non-existing user
        // When I navigate to the users subpath 
        target(API_USERS).path("anUnknownUser").request().get(String.class);
        // Then should throw a resource not found exception
    }
}
