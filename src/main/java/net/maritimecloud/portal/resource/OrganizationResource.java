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

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import net.maritimecloud.portal.application.ApplicationServiceRegistry;
import static net.maritimecloud.portal.resource.GenericCommandResource.APPLICATION_JSON_CQRS_COMMAND;
import static net.maritimecloud.portal.resource.GenericCommandResource.sendAndWait;
import net.maritimecloud.serviceregistry.command.CommandRegistry;
import net.maritimecloud.serviceregistry.command.api.AddServiceInstanceAlias;
import net.maritimecloud.serviceregistry.command.api.ChangeOrganizationNameAndSummary;
import net.maritimecloud.serviceregistry.command.api.CreateOrganization;
import net.maritimecloud.serviceregistry.command.api.PrepareServiceSpecification;
import net.maritimecloud.serviceregistry.command.api.ProvideServiceInstance;
import net.maritimecloud.serviceregistry.command.api.AddServiceInstanceEndpoint;
import net.maritimecloud.serviceregistry.command.serviceinstance.ChangeServiceInstanceCoverage;
import net.maritimecloud.serviceregistry.command.api.ChangeServiceInstanceNameAndSummary;
import net.maritimecloud.serviceregistry.command.api.RemoveServiceInstanceEndpoint;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceId;
import net.maritimecloud.serviceregistry.command.api.ChangeServiceSpecificationNameAndSummary;
import net.maritimecloud.serviceregistry.command.organization.OrganizationId;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecificationId;
import net.maritimecloud.serviceregistry.query.AliasRegistryEntry;
import net.maritimecloud.serviceregistry.query.OrganizationEntry;
import net.maritimecloud.serviceregistry.query.OrganizationQueryRepository;
import net.maritimecloud.serviceregistry.query.ServiceInstanceEntry;
import net.maritimecloud.serviceregistry.query.ServiceSpecificationEntry;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Christoffer Børrild
 */
@Path("/api")
public class OrganizationResource {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizationResource.class);

    @Resource
    protected OrganizationQueryRepository organizationQueryRepository;

    @Resource
    protected CommandGateway commandGateway;

    // -------------------------------------------------------
    // Commands
    // -------------------------------------------------------
    private static final CommandRegistry postCommandsRegistry = new CommandRegistry(
            CreateOrganization.class,
            PrepareServiceSpecification.class,
            ProvideServiceInstance.class
    );
    private static final CommandRegistry putCommandsRegistry = new CommandRegistry(
            ChangeOrganizationNameAndSummary.class,
            ChangeServiceSpecificationNameAndSummary.class,
            ChangeServiceInstanceNameAndSummary.class,
            ChangeServiceInstanceCoverage.class,
            AddServiceInstanceEndpoint.class,
            AddServiceInstanceAlias.class,
            RemoveServiceInstanceEndpoint.class
    );
    private static final CommandRegistry deleteCommandsRegistry = new CommandRegistry(
            RemoveServiceInstanceEndpoint.class
    );
    private static final CommandRegistry patchCommandsRegistry = new CommandRegistry();

    @POST
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("organization")
    public void organizationPostCommand(@HeaderParam("Content-type") String contentType, @QueryParam("command") @DefaultValue("") String queryCommandName, String commandJSON) {
        LOG.info("POST command: " + commandJSON);
        simulateLack();
        GenericCommandResource.sendAndWait(contentType, queryCommandName, postCommandsRegistry, commandJSON);
    }

    @PUT
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("organization")
    public void organizationPutCommand(@HeaderParam("Content-type") String contentType, @QueryParam("command") @DefaultValue("") String queryCommandName, String commandJSON) {
        LOG.info("PUT command: " + commandJSON);
        sendAndWait(contentType, queryCommandName, putCommandsRegistry, commandJSON);
    }

    @POST
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("organization/{organizationId}/service-instance")
    public void serviceInstancePostCommand(@HeaderParam("Content-type") String contentType, @QueryParam("command") @DefaultValue("") String queryCommandName, String commandJSON) {
        LOG.info("POST command: " + commandJSON);
        simulateLack();
        GenericCommandResource.sendAndWait(contentType, queryCommandName, postCommandsRegistry, commandJSON);
    }

    @PUT
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("organization/{organizationId}/service-instance/{serviceInstanceId}")
    public void serviceInstancePutCommand(@HeaderParam("Content-type") String contentType, @QueryParam("command") @DefaultValue("") String queryCommandName, String commandJSON) {
        LOG.info("Service Instance PUT command: " + commandJSON);
        simulateLack();
        GenericCommandResource.sendAndWait(contentType, queryCommandName, putCommandsRegistry, commandJSON);
    }

    @DELETE
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("organization/{organizationId}/service-instance/{serviceInstanceId}")
    public void serviceInstanceDeleteCommand(@HeaderParam("Content-type") String contentType, @QueryParam("command") @DefaultValue("") String queryCommandName, String commandJSON) {
        LOG.info("Service Instance DELETE command: " + commandJSON);
        simulateLack();
        GenericCommandResource.sendAndWait(contentType, queryCommandName, deleteCommandsRegistry, commandJSON);
    }

    private void simulateLack() {
//        try {
//            Thread.sleep(10);
//        } catch (InterruptedException ex) {
//            java.util.logging.Logger.getLogger(OrganizationResource.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    // -------------------------------------------------------
    // Queries
    // -------------------------------------------------------
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("organization")
    public List<OrganizationEntry> getOrganizations(
            @QueryParam("member") @DefaultValue("") String memberUID,
            @QueryParam("namePattern") @DefaultValue("") String organizationNamePattern
    ) {

        LOG.info("getOrganizations");

        Iterable<OrganizationEntry> all = ApplicationServiceRegistry.organizationQueryRepository().findAll();
        List<OrganizationEntry> organizationEntries = new ArrayList<>();

        for (OrganizationEntry organizationEntry : all) {

            // TODO: create a mapping between organizations and members
            if (memberUID.isEmpty() || organizationEntry.getSummary().contains(memberUID)) {
                organizationEntries.add(organizationEntry);
            }

        }

        return organizationEntries;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("organization/{organizationId}")
    public OrganizationEntry getOrganization(@PathParam("organizationId") String organizationId) {
        return ApplicationServiceRegistry.organizationQueryRepository().findOne(organizationId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("organization/specification")
    public List<ServiceSpecificationEntry> getSpecifications(@QueryParam("namePattern") @DefaultValue("") String usernamePattern) {

        Iterable<ServiceSpecificationEntry> all = ApplicationServiceRegistry.serviceSpecificationQueryRepository().findAll();
        List<ServiceSpecificationEntry> serviceSpecificationEntries = new ArrayList<>();

        for (ServiceSpecificationEntry organizationEntry : all) {
            serviceSpecificationEntries.add(organizationEntry);
        }

        return serviceSpecificationEntries;
    }

    // ------------------------------------------------------------------------
    // SERVICE SPECIFICATIONS
    // ------------------------------------------------------------------------
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("organization/{organizationId}/service-specification")
    public List<ServiceSpecificationEntry> queryServiceSpecifications(
            @PathParam("organizationId") String organizationId,
            @QueryParam("namePattern") @DefaultValue("") String usernamePattern
    ) {
        return ApplicationServiceRegistry.serviceSpecificationQueryRepository().findByOwnerId(organizationId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("organization/{organizationId}/service-specification/{serviceSpecificationId}")
    public ServiceSpecificationEntry getServiceSpecification(
            @PathParam("serviceSpecificationId") String serviceSpecificationId
    ) {
        return ApplicationServiceRegistry.serviceSpecificationQueryRepository().findOne(serviceSpecificationId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationAliasOrId}/ss/{serviceSpecificationAliasOrId}")
    public ServiceSpecificationEntry getServiceSpecificationByAlias(
            @PathParam("organizationAliasOrId") String organizationAliasOrId,
            @PathParam("serviceSpecificationAliasOrId") String serviceSpecificationAliasOrId
    ) {
        String organizationId = resolveOrganizationIdOrFail(organizationAliasOrId);
        return resolveServiceSpecification(organizationId, serviceSpecificationAliasOrId);
    }

    private ServiceSpecificationEntry resolveServiceSpecification(String organizationId, String serviceSpecificationAliasOrId) {
        String serviceSpecificationId = resolveServiceSpecificationId(organizationId, serviceSpecificationAliasOrId);
        ServiceSpecificationEntry serviceSpecification = lookupServiceSpecification(serviceSpecificationId);
        assertBelongsToOrganization(organizationId, serviceSpecification);
        return serviceSpecification;
    }

    private String resolveServiceSpecificationId(String organizationId, String serviceSpecificationAliasOrId) {
        AliasRegistryEntry aliasEntry = lookupServiceSpecificationAliasEntry(organizationId, serviceSpecificationAliasOrId);
        return chooseIdFrom(aliasEntry, serviceSpecificationAliasOrId);
    }

    private ServiceSpecificationEntry lookupServiceSpecification(String serviceSpecificationId) {
        final ServiceSpecificationEntry entry = ApplicationServiceRegistry.serviceSpecificationQueryRepository().findOne(serviceSpecificationId);
        throwResourceNotFoundExceptionIfNull(entry, "Unable to find service specification based on serviceInstanceId=" + serviceSpecificationId);
        return entry;
    }

    // ------------------------------------------------------------------------
    // SERVICE INSTANCES
    // ------------------------------------------------------------------------
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("organization/{organizationId}/service-instance")
    public List<ServiceInstanceEntry> queryServiceInstances(
            @PathParam("organizationId") String organizationId,
            @QueryParam("namePattern") @DefaultValue("") String usernamePattern
    ) {
        return ApplicationServiceRegistry.serviceInstanceQueryRepository().findByProviderId(organizationId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("organization/{organizationId}/service-instance/{serviceInstanceId}")
    public ServiceInstanceEntry getServiceInstance(@PathParam("serviceInstanceId") String serviceInstanceId) {
        return lookupServiceInstance(serviceInstanceId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationAliasOrId}/si/{serviceInstanceAliasOrId}")
    public ServiceInstanceEntry getServiceInstanceByAlias(
            @PathParam("organizationAliasOrId") String organizationAliasOrId,
            @PathParam("serviceInstanceAliasOrId") String serviceInstanceAliasOrId
    ) {
        System.out.println("ALL: " + ApplicationServiceRegistry.aliasRegistryQueryRepository().findAll());
        String organizationId = resolveOrganizationIdOrFail(organizationAliasOrId);
        return resolveServiceInstance(organizationId, serviceInstanceAliasOrId);
    }

    private String resolveOrganizationIdOrFail(String organizationAliasOrId) {
        String organizationId = resolveOrganizationId(organizationAliasOrId);
        throwResourceNotFoundExceptionIfNull(organizationId, "Unable to find Organization based on key=" + organizationAliasOrId);
        return organizationAliasOrId;
    }

    private String resolveOrganizationId(String organizationAliasOrId) {
        AliasRegistryEntry aliasEntry = lookupOrganizationAliasEntry(organizationAliasOrId);
        return chooseIdFrom(aliasEntry, organizationAliasOrId);
    }

    private ServiceInstanceEntry resolveServiceInstance(String organizationId, String serviceInstanceAliasOrId) {
        String serviceInstanceId = resolveServiceInstanceId(organizationId, serviceInstanceAliasOrId);
        ServiceInstanceEntry serviceInstance = lookupServiceInstance(serviceInstanceId);
        assertBelongsToOrganization(organizationId, serviceInstance);
        return serviceInstance;
    }

    private String resolveServiceInstanceId(String organizationId, String serviceInstanceAliasOrId) {
        AliasRegistryEntry aliasEntry = lookupServiceInstanceAliasEntry(organizationId, serviceInstanceAliasOrId);
        return chooseIdFrom(aliasEntry, serviceInstanceAliasOrId);
    }

    private AliasRegistryEntry lookupOrganizationAliasEntry(String organizationAliasOrId) {
        return lookupAlias(AliasRegistryEntry.USER_ORGANIZATION_GROUP, OrganizationId.class, organizationAliasOrId);
    }

    private AliasRegistryEntry lookupServiceInstanceAliasEntry(String organizationId, String serviceInstanceAliasOrId) {
        return lookupAlias(organizationId, ServiceInstanceId.class, serviceInstanceAliasOrId);
    }

    private AliasRegistryEntry lookupServiceSpecificationAliasEntry(String organizationId, String serviceSpecificationAliasOrId) {
        return lookupAlias(organizationId, ServiceSpecificationId.class, serviceSpecificationAliasOrId);
    }

    private AliasRegistryEntry lookupAlias(String groupId, Class type, String alias) {
        return ApplicationServiceRegistry.aliasRegistryQueryRepository().findByGroupIdAndTypeNameAndAlias(groupId, type.getName(), alias);
    }

    private static String chooseIdFrom(AliasRegistryEntry aliasEntry, String serviceSpecificationAliasOrId) {
        return aliasEntry != null ? aliasEntry.getTargetId() : serviceSpecificationAliasOrId;
    }

    private ServiceInstanceEntry lookupServiceInstance(String serviceInstanceId) {
        final ServiceInstanceEntry entry = ApplicationServiceRegistry.serviceInstanceQueryRepository().findOne(serviceInstanceId);
        throwResourceNotFoundExceptionIfNull(entry, "Unable to find service instance based on serviceInstanceId=" + serviceInstanceId);
        return entry;
    }

    private void assertBelongsToOrganization(String organizationId, ServiceInstanceEntry serviceInstance) {
        if (!serviceInstance.getProviderId().equals(organizationId)) {
            throwResourceNotFoundExceptionIfNull(null, "service instance is not own by organization");
        }
    }

    private void assertBelongsToOrganization(String organizationId, ServiceSpecificationEntry serviceSpecificationEntry) {
        if (!serviceSpecificationEntry.getOwnerId().equals(organizationId)) {
            throwResourceNotFoundExceptionIfNull(null, "service specification is not own by organization");
        }
    }

    private void throwResourceNotFoundExceptionIfNull(Object objectToTestForNull, String message) throws WebApplicationException {
        if (objectToTestForNull == null) {
            throw new WebApplicationException(message, 404);
        }
    }

}
