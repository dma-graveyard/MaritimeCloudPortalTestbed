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

import java.util.List;
import javax.ws.rs.WebApplicationException;
import net.maritimecloud.portal.application.ApplicationServiceRegistry;
import net.maritimecloud.serviceregistry.command.organization.OrganizationId;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceId;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecificationId;
import net.maritimecloud.serviceregistry.query.AliasRegistryEntry;
import net.maritimecloud.serviceregistry.query.ServiceInstanceEntry;
import net.maritimecloud.serviceregistry.query.ServiceSpecificationEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to assist with resolving resources based on id or aliases
 * 
 * @author Christoffer Børrild
 */
public class ResourceResolver {
    private static final Logger LOG = LoggerFactory.getLogger(ResourceResolver.class);

    public static String resolveOrganizationIdOrFail(String organizationAliasOrId) {
        String organizationId = resolveOrganizationId(organizationAliasOrId);
        assertNotNull(organizationId, "Unable to find Organization based on key=" + organizationAliasOrId);
        return organizationAliasOrId;
    }

    public static ServiceSpecificationEntry resolveServiceSpecification(String organizationId, String serviceSpecificationAliasOrId) {
        String serviceSpecificationId = resolveServiceSpecificationId(organizationId, serviceSpecificationAliasOrId);
        ServiceSpecificationEntry serviceSpecification = lookupServiceSpecification(serviceSpecificationId);
        assertBelongsToOrganization(organizationId, serviceSpecification);
        return serviceSpecification;
    }

    public static String resolveServiceSpecificationId(String organizationId, String serviceSpecificationAliasOrId) {
        AliasRegistryEntry aliasEntry = lookupServiceSpecificationAliasEntry(organizationId, serviceSpecificationAliasOrId);
        return chooseIdFrom(aliasEntry, serviceSpecificationAliasOrId);
    }

    public static ServiceInstanceEntry resolveServiceInstance(String organizationId, String serviceInstanceAliasOrId) {
        String serviceInstanceId = resolveServiceInstanceId(organizationId, serviceInstanceAliasOrId);
        ServiceInstanceEntry serviceInstance = lookupServiceInstance(serviceInstanceId);
        assertBelongsToOrganization(organizationId, serviceInstance);
        return serviceInstance;
    }

    public static ServiceInstanceEntry lookupServiceInstance(String serviceInstanceId) {
        final ServiceInstanceEntry entry = ApplicationServiceRegistry.serviceInstanceQueryRepository().findOne(serviceInstanceId);
        assertNotNull(entry, "Unable to find service instance based on serviceInstanceId=" + serviceInstanceId);
        return entry;
    }

    private static ServiceSpecificationEntry lookupServiceSpecification(String serviceSpecificationId) {
        final ServiceSpecificationEntry entry = ApplicationServiceRegistry.serviceSpecificationQueryRepository().findOne(serviceSpecificationId);
        assertNotNull(entry, "Unable to find service specification based on serviceInstanceId=" + serviceSpecificationId);
        return entry;
    }

    private static String resolveOrganizationId(String organizationAliasOrId) {
        AliasRegistryEntry aliasEntry = lookupOrganizationAliasEntry(organizationAliasOrId);
        return chooseIdFrom(aliasEntry, organizationAliasOrId);
    }

    private static String resolveServiceInstanceId(String organizationId, String serviceInstanceAliasOrId) {
        AliasRegistryEntry aliasEntry = lookupServiceInstanceAliasEntry(organizationId, serviceInstanceAliasOrId);
        return chooseIdFrom(aliasEntry, serviceInstanceAliasOrId);
    }

    private static AliasRegistryEntry lookupOrganizationAliasEntry(String organizationAliasOrId) {
        return lookupAlias(AliasRegistryEntry.USER_ORGANIZATION_GROUP, OrganizationId.class, organizationAliasOrId);
    }

    private static AliasRegistryEntry lookupServiceInstanceAliasEntry(String organizationId, String serviceInstanceAliasOrId) {
        return lookupAlias(organizationId, ServiceInstanceId.class, serviceInstanceAliasOrId);
    }

    private static AliasRegistryEntry lookupServiceSpecificationAliasEntry(String organizationId, String serviceSpecificationAliasOrId) {
        return lookupAlias(organizationId, ServiceSpecificationId.class, serviceSpecificationAliasOrId);
    }

    private static AliasRegistryEntry lookupAlias(String groupId, Class type, String alias) {
        return ApplicationServiceRegistry.aliasRegistryQueryRepository().findByGroupIdAndTypeNameAndAlias(groupId, type.getName(), alias);
    }

    private static String chooseIdFrom(AliasRegistryEntry aliasEntry, String serviceSpecificationAliasOrId) {
        return aliasEntry != null ? aliasEntry.getTargetId() : serviceSpecificationAliasOrId;
    }

    private static void assertBelongsToOrganization(String organizationId, ServiceInstanceEntry serviceInstance) {
        if (!serviceInstance.getProviderId().equals(organizationId)) {
            assertNotNull(null, "service instance is not own by organization");
        }
    }

    private static void assertBelongsToOrganization(String organizationId, ServiceSpecificationEntry serviceSpecificationEntry) {
        if (!serviceSpecificationEntry.getOwnerId().equals(organizationId)) {
            assertNotNull(null, "service specification is not own by organization");
        }
    }

    private static void assertNotNull(Object objectToTestForNull, String message) throws WebApplicationException {
        if (objectToTestForNull == null) {
            LOG.warn("Objct not found. {}", message);
            throw new WebApplicationException(message, 404);
        }
    }

    static List<AliasRegistryEntry> queryServiceinstanceAliases(ServiceInstanceEntry sie) {
        return ApplicationServiceRegistry.aliasRegistryQueryRepository().findByGroupIdAndTypeNameAndTargetId(sie.getProviderId(), ServiceInstanceId.class.getName(), sie.getServiceInstanceId());
    }

}
