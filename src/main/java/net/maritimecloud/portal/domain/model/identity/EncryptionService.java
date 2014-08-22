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
package net.maritimecloud.portal.domain.model.identity;

/**
 * Service that provides an encryption strategy for encrypting/hashing passwords
 * <p>
 * @author Christoffer Børrild
 */
public interface EncryptionService {

    /**
     * Applies the configured encryption/hashing algorithm to the supplied text Note that subsequent calls of the implementation of this
     * method may not necessary yield the same result.
     * <p>
     * @param aPlainTextValue
     * @return A hashed or encrypted value that can subsequently be matched against the original planTextValue by using valuesMatch
     * @see #valuesMatch(String, String)
     */
    public String encryptedValue(String aPlainTextValue);

    /**
     * Compares the supplied plainTextValue with its encrypted/hashed counterpart to assert if they are related
     * <p>
     * @param aPlainTextValue
     * @param aSavedEncryptedTextValue
     * @return true if the savedEncryptedTextValue could origin from the plain text value.
     */
    public boolean valuesMatch(String aPlainTextValue, String aSavedEncryptedTextValue);

}
