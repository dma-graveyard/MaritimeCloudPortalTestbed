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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christoffer Børrild
 */
public class JsonCommandHelperTest {

    @Test
    public void testRewriteIdentity() {

        String commandJSON = "{"
                + "\"organizationId\" :  { \"identifier\" : \"AN_ORG_ID\"   },"
                + "\"primaryAlias\":\"AN_ALIAS\","
                + "\"name\":\"A_NAME\","
                + "\"summary\":\"A_SUMMARY\","
                + "\"url\":\"http://a.url\""
                + "}";
        String expect = "{"
                + "\"organizationId\":{\"identifier\":\"ANOTHER_ORG_ID\"},"
                + "\"primaryAlias\":\"AN_ALIAS\","
                + "\"name\":\"A_NAME\","
                + "\"summary\":\"A_SUMMARY\","
                + "\"url\":\"http://a.url\""
                + "}";

        String propertyName = "organizationId";
        String value = "ANOTHER_ORG_ID";

        String result = JsonCommandHelper.overwriteIdentity(commandJSON, propertyName, value);
        assertEquals(expect, result);
    }
}
