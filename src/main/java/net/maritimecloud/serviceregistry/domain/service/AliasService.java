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
package net.maritimecloud.serviceregistry.domain.service;

/**
 *
 * @author Christoffer Børrild
 */
public interface AliasService {

    /**
     * @param aliasGroupId
     * @param alias
     * @return true when the specified alias is known within the alias group
     */
    public boolean isDefined(String aliasGroupId, String alias);

    /**
     * @param aliasGroupId
     * @param alias
     * @param targetIdentifier
     * @return true if the alias within the group points to the designated target
     */
    public boolean isIdentical(String aliasGroupId, String alias, String targetIdentifier);

    /**
     * @param aliasGroupId
     * @param targetIdentifier
     * @return true when the supplied target id is known within the alias group
     */
    public boolean hasTarget(String aliasGroupId, String targetIdentifier);

}
