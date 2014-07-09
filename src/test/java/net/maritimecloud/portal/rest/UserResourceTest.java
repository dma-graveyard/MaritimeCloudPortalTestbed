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

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import net.maritimecloud.portal.JerseyConfig;
import net.maritimecloud.portal.config.TestConfig;
import net.maritimecloud.portal.model.user.User;
import org.glassfish.jersey.test.JerseyTest;
import org.json.JSONException;
import org.junit.Ignore;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Christoffer Børrild
 */
public class UserResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        ApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class);
        return new JerseyConfig().property("contextConfig", context);
    }

    @Test
    @Ignore
    public void createUser() throws JSONException {
        User aNewUser = new User();
        aNewUser.setUserName("a fine new user");
        Entity aNewUserEntity = Entity.entity(aNewUser, MediaType.APPLICATION_JSON);
        final String actual = target("users").request().post(aNewUserEntity, String.class);
        System.out.println("actual: "+actual);
        String expected = "{"
                + "'userName': 'a fine new user'"
                + "}";
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
    }

    @Test
    @Ignore
    public void listOfUsers() throws JSONException {
        final String actual = target("users").request().get(String.class);
        String expected = "["
                + "{'userName': 'test'}"
                + "]";
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
    }

    @Test
    @Ignore
    public void singleUser() throws JSONException {
        final String actual = target("users").path("1").request().get(String.class);
        String expected = "{'userName': 'test1'}";
        System.out.println("Actual: " + actual);
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
    }
}
