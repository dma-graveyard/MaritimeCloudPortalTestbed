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
package net.maritimecloud.serviceregistry.query;

import javax.annotation.Resource;
import net.maritimecloud.serviceregistry.command.api.OrganizationAliasAdded;
import net.maritimecloud.serviceregistry.command.api.OrganizationAliasRemoved;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceAliasAdded;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceAliasRemoved;
import net.maritimecloud.serviceregistry.command.organization.OrganizationId;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceId;
import net.maritimecloud.serviceregistry.domain.service.AliasGroups;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Christoffer Børrild
 */
@Component
public class AliasRegistryListener {

    private final static Logger logger = LoggerFactory.getLogger(AliasRegistryListener.class);

    @Resource
    private AliasRegistryQueryRepository aliasRegistryQueryRepository;

    public AliasRegistryListener() {
    }

    public AliasRegistryListener(AliasRegistryQueryRepository aliasRegistryQueryRepository) {
        this.aliasRegistryQueryRepository = aliasRegistryQueryRepository;
    }

    @EventHandler
    public void on(ServiceInstanceAliasAdded event) {
        AliasRegistryEntry entry = new AliasRegistryEntry(
                event.getOrganizationId().identifier(),
                ServiceInstanceId.class.getName(),
                event.getAlias(),
                event.getServiceInstanceId().identifier()
        );
        save(entry);
    }

    @EventHandler
    public void on(ServiceInstanceAliasRemoved event) {
        AliasRegistryEntry aliasEntry = aliasRegistryQueryRepository.findByGroupIdAndTypeNameAndAlias(
                event.getOrganizationId().identifier(),
                ServiceInstanceId.class.getName(),
                event.getAlias()
        );
        aliasRegistryQueryRepository.delete(aliasEntry);
    }

    @EventHandler
    public void on(OrganizationAliasAdded event) {
        AliasRegistryEntry entry = new AliasRegistryEntry(
                AliasGroups.USERS_AND_ORGANIZATIONS.name(),
                OrganizationId.class.getName(),
                event.getAlias(),
                event.getOrganizationId().identifier()
        );
        save(entry);
    }

    @EventHandler
    public void on(OrganizationAliasRemoved event) {

        Iterable<AliasRegistryEntry> findAll = aliasRegistryQueryRepository.findAll();
        for (AliasRegistryEntry entry : findAll) {
            System.out.println("" + entry);

        }

        AliasRegistryEntry aliasEntry = aliasRegistryQueryRepository.findByGroupIdAndTypeNameAndAlias(
                AliasGroups.USERS_AND_ORGANIZATIONS.name(),
                OrganizationId.class.getName(),
                event.getAlias()
        );
        aliasRegistryQueryRepository.delete(aliasEntry);
    }

    private void save(AliasRegistryEntry entry) {
        aliasRegistryQueryRepository.save(entry);
    }

}
