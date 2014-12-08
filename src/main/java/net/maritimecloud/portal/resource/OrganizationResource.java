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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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
import javax.ws.rs.core.MediaType;
import net.maritimecloud.portal.application.ApplicationServiceRegistry;
import static net.maritimecloud.portal.resource.GenericCommandResource.APPLICATION_JSON_CQRS_COMMAND;
import static net.maritimecloud.portal.resource.GenericCommandResource.sendAndWait;
import net.maritimecloud.serviceregistry.command.CommandRegistry;
import net.maritimecloud.serviceregistry.command.organization.ChangeOrganizationNameAndSummaryCommand;
import net.maritimecloud.serviceregistry.command.organization.CreateOrganizationCommand;
import net.maritimecloud.serviceregistry.command.organization.OrganizationId;
import net.maritimecloud.serviceregistry.command.organization.PrepareServiceSpecificationCommand;
import net.maritimecloud.serviceregistry.command.organization.ProvideServiceInstanceCommand;
import net.maritimecloud.serviceregistry.command.api.AddServiceInstanceEndpointCommand;
import net.maritimecloud.serviceregistry.command.serviceinstance.ChangeServiceInstanceCoverageCommand;
import net.maritimecloud.serviceregistry.command.api.ChangeServiceInstanceNameAndSummaryCommand;
import net.maritimecloud.serviceregistry.command.serviceinstance.Coverage;
import net.maritimecloud.serviceregistry.command.api.RemoveServiceInstanceEndpointCommand;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceId;
import net.maritimecloud.serviceregistry.command.servicespecification.ChangeServiceSpecificationNameAndSummaryCommand;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecificationId;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceType;
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
@Path("/api/organization")
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
            CreateOrganizationCommand.class,
            PrepareServiceSpecificationCommand.class,
            ProvideServiceInstanceCommand.class
    );
    private static final CommandRegistry putCommandsRegistry = new CommandRegistry(
            ChangeOrganizationNameAndSummaryCommand.class,
            ChangeServiceSpecificationNameAndSummaryCommand.class,
            ChangeServiceInstanceNameAndSummaryCommand.class,
            ChangeServiceInstanceCoverageCommand.class,
            AddServiceInstanceEndpointCommand.class,
            RemoveServiceInstanceEndpointCommand.class
    );
    private static final CommandRegistry deleteCommandsRegistry = new CommandRegistry(
            RemoveServiceInstanceEndpointCommand.class
    );
    private static final CommandRegistry patchCommandsRegistry = new CommandRegistry();

    @POST
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    public void organizationPostCommand(@HeaderParam("Content-type") String contentType, @QueryParam("command") @DefaultValue("") String queryCommandName, String commandJSON) {
        LOG.info("POST command: " + commandJSON);
        simulateLack();
        GenericCommandResource.sendAndWait(contentType, queryCommandName, postCommandsRegistry, commandJSON);
    }

    @PUT
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    public void organizationPutCommand(@HeaderParam("Content-type") String contentType, @QueryParam("command") @DefaultValue("") String queryCommandName, String commandJSON) {
        LOG.info("PUT command: " + commandJSON);
        sendAndWait(contentType, queryCommandName, putCommandsRegistry, commandJSON);
    }

    @POST
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{organizationId}/service-instance")
    public void serviceInstancePostCommand(@HeaderParam("Content-type") String contentType, @QueryParam("command") @DefaultValue("") String queryCommandName, String commandJSON) {
        LOG.info("POST command: " + commandJSON);
        simulateLack();
        GenericCommandResource.sendAndWait(contentType, queryCommandName, postCommandsRegistry, commandJSON);
    }

    @PUT
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{organizationId}/service-instance/{serviceInstanceId}")
    public void serviceInstancePutCommand(@HeaderParam("Content-type") String contentType, @QueryParam("command") @DefaultValue("") String queryCommandName, String commandJSON) {
        LOG.info("Service Instance PUT command: " + commandJSON);
        simulateLack();
        GenericCommandResource.sendAndWait(contentType, queryCommandName, putCommandsRegistry, commandJSON);
    }

    @DELETE
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{organizationId}/service-instance/{serviceInstanceId}")
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
    @Path("{organizationId}")
    public OrganizationEntry getOrganization(@PathParam("organizationId") String organizationId) {
        return ApplicationServiceRegistry.organizationQueryRepository().findOne(organizationId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("specification")
    public List<ServiceSpecificationEntry> getSpecifications(@QueryParam("namePattern") @DefaultValue("") String usernamePattern) {

        Iterable<ServiceSpecificationEntry> all = ApplicationServiceRegistry.serviceSpecificationQueryRepository().findAll();
        List<ServiceSpecificationEntry> serviceSpecificationEntries = new ArrayList<>();

        for (ServiceSpecificationEntry organizationEntry : all) {
            serviceSpecificationEntries.add(organizationEntry);
        }

        return serviceSpecificationEntries;
    }

    // SERVICE INSTANCE ------------------------------------------------
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{organizationId}/service-specification")
    public List<ServiceSpecificationEntry> queryServiceSpecifications(
            @PathParam("organizationId") String organizationId,
            @QueryParam("namePattern") @DefaultValue("") String usernamePattern
    ) {
        return ApplicationServiceRegistry.serviceSpecificationQueryRepository().findByOwnerId(organizationId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{organizationId}/service-specification/{serviceSpecificationId}")
    public ServiceSpecificationEntry getServiceSpecification(
            @PathParam("serviceSpecificationId") String serviceSpecificationId
    ) {
        return ApplicationServiceRegistry.serviceSpecificationQueryRepository().findOne(serviceSpecificationId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{organizationId}/service-instance")
    public List<ServiceInstanceEntry> queryServiceInstances(
            @PathParam("organizationId") String organizationId,
            @QueryParam("namePattern") @DefaultValue("") String usernamePattern
    ) {

        return ApplicationServiceRegistry.serviceInstanceQueryRepository().findByProviderId(organizationId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{organizationId}/service-instance/{serviceInstanceId}")
    public ServiceInstanceEntry getServiceInstance(
            @PathParam("serviceInstanceId") String serviceInstanceId
    ) {
        return ApplicationServiceRegistry.serviceInstanceQueryRepository().findOne(serviceInstanceId);
    }

    // HACK - RANDOMIZER
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("randomizer")
    public void randomizer(
            @QueryParam("ss") @DefaultValue("0") int ssAmount,
            @QueryParam("si") @DefaultValue("1") int siAmount
    ) {
        randomizeServiceSpecification(ssAmount);
        randomizeServiceInstances(siAmount);
    }

    private void randomizeServiceSpecification(int amount) {
        Iterable<ServiceSpecificationEntry> serviceSpecificationEntrys = ApplicationServiceRegistry.serviceSpecificationQueryRepository().findAll();
        ArrayList<ServiceSpecificationEntry> l = new ArrayList<>();
        for (ServiceSpecificationEntry specificationEntry : serviceSpecificationEntrys) {
            l.add(specificationEntry);
        }
        
        if (l.isEmpty())
            return;
        
        for (int i = 0; i < amount; i++) {
            Object command = new PrepareServiceSpecificationCommand(
                    new OrganizationId(random(l).getOwnerId()),
                    new ServiceSpecificationId("SS-" + i + "-" + UUID.randomUUID()),
                    
                    random(Arrays.asList(ServiceType.values())),
                    random(l).getName() + "-" + i,
                    random(l).getSummary() + "-" + i);
            ApplicationServiceRegistry.commandGateway().sendAndWait(command);
        }
    }

    private void randomizeServiceInstances(int siAmount) {
        Iterable<ServiceInstanceEntry> serviceInstances = ApplicationServiceRegistry.serviceInstanceQueryRepository().findAll();
        ArrayList<ServiceInstanceEntry> l = new ArrayList<>();
        for (ServiceInstanceEntry serviceInstance : serviceInstances) {
            l.add(serviceInstance);
        }
        
        if (l.isEmpty())
            return;
        
        for (int i = 0; i < siAmount; i++) {
            Object command = new ProvideServiceInstanceCommand(
                    new OrganizationId(random(l).getProviderId()),
                    new ServiceSpecificationId(random(l).getSpecificationId()),
                    new ServiceInstanceId("rand-" + i + "-" + UUID.randomUUID()),
                    random(l).getName() + "-" + i,
                    random(l).getSummary() + "-" + i,
                    randomCoverage());
            ApplicationServiceRegistry.commandGateway().sendAndWait(command);
        }
    }

    private <T> T random(List<T> l) {
        return l.get((int) (Math.random() * (l.size() - 1)));
    }

    private Coverage randomCoverage() {
        return new Coverage("[{"
                + "\"type\": \"circle\","
                + "\"center-latitude\": "+(80 - Math.random() * 160)+","
                + "\"center-longitude\": "+(180 - Math.random() * 360)+","
                + "\"radius\": "+(450000 - Math.random() * 22000)+ "}]");
    }

}
