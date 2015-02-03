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
package net.maritimecloud.common.resource;

/**
 * Helper class to work with JSON command strings
 * <p>
 * @author Christoffer Børrild
 */
public final class JsonCommandHelper {

    private static final String anyvalue = ".*?";
    private static final String aColon = "\\s*:\\s*";
    private static final String aQuote = "\"";
    private static final String aLeftCurlyBracket = "\\s*\\{\\s*";
    private static final String aRightCurlyBracket = "\\s*\\}\\s*";

    private JsonCommandHelper() {
    }

    /**
     * Rewrites an identity property value in a JSON command string
     * <p>
     * @param commandJSON the json command string
     * @param propertyName the name of the identity property, eg. "OrganizationId"
     * @param value the replacing value
     * @return
     */
    public static String overwriteIdentity(String commandJSON, String propertyName, String value) {

        return commandJSON.replaceAll(
                aQuote + propertyName + aQuote + aColon + aLeftCurlyBracket + aQuote + "identifier" + aQuote + aColon + aQuote + anyvalue + aQuote + aRightCurlyBracket,
                "\"" + propertyName + "\":\\{\"identifier\":\"" + value + "\"\\}");
    }

    public static boolean identityIsEmpty(String commandJSON, String propertyName) {

        return commandJSON.replaceAll(
                aQuote + propertyName + aQuote + aColon + aLeftCurlyBracket + aQuote + "identifier" + aQuote + aColon + aQuote + anyvalue + aQuote + aRightCurlyBracket,
                "\"" + propertyName + "\":\\{\"identifier\":\"" + "" + "\"\\}").equals(commandJSON);
    }

}
