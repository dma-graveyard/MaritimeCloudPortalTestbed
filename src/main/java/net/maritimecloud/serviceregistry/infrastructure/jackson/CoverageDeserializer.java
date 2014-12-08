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
package net.maritimecloud.serviceregistry.infrastructure.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.Arrays;
import net.maritimecloud.serviceregistry.command.serviceinstance.Coverage;

/**
 * Simpleminded "re-serialization" of the Coverage object.
 * <p>
 * For now we keep the coverage object as a serialized JSON object on the serve side. Hence we have to keep the JSON serialization as is,
 * but this is not easy when all that Jackson provides is a tokenizer. There is no direct access to the underlying string, hence we have to
 * rebuild it from the tokens delivered by jackson.
 * <p>
 * @author Christoffer Børrild
 */
public class CoverageDeserializer extends JsonDeserializer<Coverage> {

    private JsonToken previousToken = null;

    @Override
    public Coverage deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        int arrayCount = 0;
        String text = "";
        previousToken = null;

        while (parser.hasCurrentToken()) {
            String token = parser.getText();

            final JsonToken currentToken = parser.getCurrentToken();
            switch (currentToken) {

                case START_ARRAY:
                    arrayCount++;
                case START_OBJECT:
                case VALUE_FALSE:
                case VALUE_NULL:
                case VALUE_TRUE:
                case VALUE_NUMBER_FLOAT:
                case VALUE_NUMBER_INT:
                    text += prependCommaUnless(JsonToken.START_ARRAY, JsonToken.START_OBJECT, JsonToken.FIELD_NAME);
                    text += token;
                    break;

                case FIELD_NAME:
                    text += prependCommaUnless(JsonToken.START_ARRAY, JsonToken.START_OBJECT, JsonToken.FIELD_NAME);
                    text += "\"" + token + "\":";
                    break;

                case VALUE_STRING:
                    text += prependCommaUnless(JsonToken.START_ARRAY, JsonToken.START_OBJECT, JsonToken.FIELD_NAME);
                    text += "\"" + token + "\"";
                    break;

                case END_ARRAY:
                    arrayCount--;
                case END_OBJECT:
                    text += token;
                    break;

                case NOT_AVAILABLE:

            }

            if (arrayCount == 0) {
                final Coverage coverage = new Coverage(text);
                //System.out.println("\n deserialize to: " + text);
                return coverage;
            }

            //System.out.println("" + token);
            parser.nextToken();

            previousToken = currentToken;
        }
        throw new RuntimeException("Parser reach an end unexpectedly !?!? " + text);
    }

    private String prependCommaUnless(JsonToken... jsonTokens) {
        return previousToken == null || Arrays.asList(jsonTokens).contains(previousToken) ? "" : ",";
    }

}
