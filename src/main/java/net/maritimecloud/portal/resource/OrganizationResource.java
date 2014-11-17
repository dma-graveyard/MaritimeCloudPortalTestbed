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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import net.maritimecloud.portal.application.ApplicationServiceRegistry;
import static net.maritimecloud.serviceregistry.command.RestCommandUtil.readCommand;
import static net.maritimecloud.serviceregistry.command.RestCommandUtil.resolveCommandClass;
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

//    public static final Map<String, Class> commandRegistry = new HashMap<>();
//
//    static {
//        commandRegistry.put(CreateOrganizationCommand.class.getCanonicalName(), CreateOrganizationCommand.class);
//        commandRegistry.put(CreateOrganizationCommand.class.getSimpleName(), CreateOrganizationCommand.class);
//
//        commandRegistry.put(CreateOrganizationCommand.class.getCanonicalName(), CreateOrganizationCommand.class);
//        commandRegistry.put(CreateOrganizationCommand.class.getSimpleName(), CreateOrganizationCommand.class);
//    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";domain-model=*Command")
    @Produces(MediaType.APPLICATION_JSON)
    public void mappedCommand(
            @HeaderParam("Content-type") String contentType,
            @QueryParam("command") @DefaultValue("") String queryCommandName,
            String commandJSON) {
        try {
            Object command = readCommand(commandJSON, resolveCommandClass(contentType, queryCommandName));
            ApplicationServiceRegistry.commandGateway().sendAndWait(command);
        } catch (IOException ex) {
            throw new WebApplicationException("Error occured when reading command!");
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<OrganizationEntry> getOrganizations(@QueryParam("namePattern") @DefaultValue("") String usernamePattern) {

        LOG.info("getOrganizations");

        Iterable<OrganizationEntry> all = ApplicationServiceRegistry.organizationQueryRepository().findAll();
        List<OrganizationEntry> organizationEntries = new ArrayList<>();

        for (OrganizationEntry organizationEntry : all) {
            organizationEntries.add(organizationEntry);
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("instance")
    public List<ServiceInstanceEntry> getInstances(@QueryParam("namePattern") @DefaultValue("") String usernamePattern) {

        Iterable<ServiceInstanceEntry> all = ApplicationServiceRegistry.serviceInstanceQueryRepository().findAll();
        List<ServiceInstanceEntry> serviceInstanceEntries = new ArrayList<>();

        for (ServiceInstanceEntry entry : all) {
            serviceInstanceEntries.add(entry);
        }

        return serviceInstanceEntries;
    }

//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON+";domain-model=CreateInventoryItemCommand")
//    public void createOrganization(
//            @QueryParam("namePattern") @DefaultValue("") String usernamePattern,
//            @Context  HttpServletRequest request
//    ) {
//
//        CreateOrganizationCommand createOrganizationCommand
//                = new CreateOrganizationCommand(new OrganizationId("OrganizationId_" + usernamePattern), "A_NAME", "A_SUMMARY");
//
//        LOG.info("create organization: " + createOrganizationCommand);
//        LOG.info("create organization: " + request);
//        LOG.info("create organization: " + request.getContentType());
//
//        ApplicationServiceRegistry.commandGateway().sendAndWait(createOrganizationCommand);
//
//        //return "OK";
//    }
//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON+";domain-model=CreateInventoryItemCommand")
//    public void createOrganization(
//            JsonCommandMessage command,
//            @QueryParam("namePattern") @DefaultValue("") String usernamePattern,
//            @Context  HttpServletRequest request
//    ) {
//        LOG.info("command: " + command);
//        LOG.info("command: " + command.getClass().getCanonicalName());
//        LOG.info("create organization II: " + request.getContentType());
//        
//        // write a generic "command parser" that will create a command based on a name (domain-model)
//        // and the list of coresponding constructors variables
//        
//    }
//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON+";domain-model=CreateInventoryItemCommand")
//    public void createOrganization(
//            String command,
//            @QueryParam("namePattern") @DefaultValue("") String usernamePattern,
//            @Context  HttpServletRequest request
//    ) {
//        LOG.info("command: " + command);
//        LOG.info("command: " + command.getClass().getCanonicalName());
// //       LOG.info("create organization II: " + request.getContentType());
//        
//        // write a generic "command parser" that will create a command based on a name (domain-model)
//        // and the list of coresponding constructors variables
//        
//    }
//    @GET
//    @Path("cmd")
//    @Produces(MediaType.APPLICATION_JSON)
//    public CreateOrganizationCommand createOrganization(@QueryParam("namePattern") @DefaultValue("") String usernamePattern) {
//
//        CreateOrganizationCommand createOrganizationCommand 
//                = new CreateOrganizationCommand(new OrganizationId("OrganizationId_"+usernamePattern), "A_NAME", "A_SUMMARY");
//        
//        LOG.info("create organization: "+usernamePattern/*createOrganizationCommand*/);
//        
//        //ApplicationServiceRegistry.commandGateway().sendAndWait(createOrganizationCommand);
//        return createOrganizationCommand;
//    }
//    private Class resolveCommandClass(String contentType, String queryCommandName) {
//        return resolveCommandClass(resolveCommandName(contentType, queryCommandName));
//    }
//
//    private Class resolveCommandClass(String commandName) {
//        Class commandClass = commandRegistry.get(commandName);
//        Assert.notNull(commandName, "Unable to resolve name to a command-class: " + commandName);
//        return commandClass;
//    }
//
//    private String resolveCommandName(String contentType, String queryCommandName) {
//
//        String domainModel = contentType.replaceFirst(MediaType.APPLICATION_JSON + ";domain-model=", "");
//        LOG.debug("domain model = " + domainModel);
//
//        String commandName = queryCommandName.isEmpty() ? domainModel : queryCommandName;
//        LOG.debug("command name: " + commandName);
//
//        return commandName;
//    }
//
//    private Object readCommand(String fromJSON, Class toTargetClass) throws IOException {
//        ObjectMapper mapper = new ObjectMapper();
//        return mapper.readValue(fromJSON, toTargetClass);
//    }
}
