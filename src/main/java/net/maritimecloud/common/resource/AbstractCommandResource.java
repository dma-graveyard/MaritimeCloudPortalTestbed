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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.maritimecloud.common.cqrs.Command;
import net.maritimecloud.common.cqrs.CommandRegistry;
import static net.maritimecloud.common.resource.RestCommandUtil.readCommand;
import static net.maritimecloud.common.resource.RestCommandUtil.resolveCommandName;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Christoffer Børrild
 */
public abstract class AbstractCommandResource {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractCommandResource.class);

    public static final String APPLICATION_JSON_CQRS_COMMAND = MediaType.APPLICATION_JSON + ";domain-model=*Command";

    protected void sendAndWait(String contentType, String queryCommandName, String commandJSON, Class... classes) {
        sendAndWait(contentType, queryCommandName, new CommandRegistry(classes), commandJSON);
    }

    protected void sendAndWait(String contentType, String queryCommandName, CommandRegistry commandRegistry, String commandJSON) throws WebApplicationException {
        try {
            LOG.info("Received command: Cmd={}{}", contentType, queryCommandName);
            LOG.info("JSON: {}", commandJSON);
            Class commandClass = commandRegistry.resolve(resolveCommandName(contentType, queryCommandName));
            Object command = readCommand(commandJSON, commandClass);
            sendAndWait((Command) command);

        } catch (WebApplicationException ex) {
            throw ex;
        } catch (Throwable ex) {
            LOG.error("Error occured when reading command!", ex);
            throw new WebApplicationException("Error occured when reading command!", ex);
        }
    }

    protected void sendAndWait(Command command) throws WebApplicationException {
        try {
            commandGateway().sendAndWait(command);
        } catch (CommandExecutionException e) {
            if (e.getCause() instanceof IllegalArgumentException) {
                throw new WebApplicationException("Illegal Argument", e, 400);
            } else {
                LOG.error("Error occured when reading command!", e);
                throw new WebApplicationException("Error occured when reading command!", e);
            }
        } catch (Throwable ex) {
            LOG.error("Error occured when reading command!", ex);
            throw new WebApplicationException("Error occured when reading command!", ex);
        }
    }

    protected abstract CommandGateway commandGateway();

    protected void requiresRoles(String... roles) {
        try {
            SecurityUtils.getSubject().checkRoles(roles);
        } catch (AuthorizationException authorizationException) {
            throw new WebApplicationException(authorizationException, Response.Status.UNAUTHORIZED);
        }
    }

    protected void assertUserRole(String role, String resolveCommandName, Class... commandTypes) {
        if (matchCommandType(resolveCommandName, commandTypes)) {
            requiresRoles("USER");
        }
    }

    protected boolean matchCommandType(String resolveCommandName, Class... commandTypes) {
        for (Class commandType : commandTypes) {
            if (commandType.getSimpleName().equals(resolveCommandName)) {
                return true;
            }
        }
        return false;
    }
    
}
