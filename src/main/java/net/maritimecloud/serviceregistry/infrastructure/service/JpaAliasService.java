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
package net.maritimecloud.serviceregistry.infrastructure.service;

import java.util.List;
import net.maritimecloud.portal.application.ApplicationServiceRegistry;
import net.maritimecloud.serviceregistry.domain.service.AliasService;
import net.maritimecloud.serviceregistry.query.AliasRegistryEntry;

/**
 *
 * @author Christoffer Børrild
 */
public class JpaAliasService implements AliasService {

    @Override
    public boolean isDefined(String aliasGroupId, String alias) {
        return ApplicationServiceRegistry.aliasRegistryQueryRepository().findByGroupIdAndAlias(aliasGroupId, alias) != null;
    }

    @Override
    public boolean isIdentical(String aliasGroupId, String alias, String targetIdentifier) {
        final String targetId = ApplicationServiceRegistry.aliasRegistryQueryRepository().findByGroupIdAndAlias(aliasGroupId, alias).getTargetId();
        return targetIdentifier.equals(targetId);
    }

    @Override
    public boolean hasTarget(String aliasGroupId, String targetIdentifier) {
        final List<AliasRegistryEntry> findByGroupIdAndTargetId = ApplicationServiceRegistry.aliasRegistryQueryRepository().findByGroupIdAndTargetId(aliasGroupId, targetIdentifier);
        return !findByGroupIdAndTargetId.isEmpty();
    }

}
