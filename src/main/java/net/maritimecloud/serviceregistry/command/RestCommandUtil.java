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
package net.maritimecloud.serviceregistry.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import net.maritimecloud.serviceregistry.command.organization.CreateOrganizationCommand;
import org.axonframework.common.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Christoffer Børrild
 */
public class RestCommandUtil {

    private static final Logger LOG = LoggerFactory.getLogger(RestCommandUtil.class);
    
    public static final Map<String, Class> commandRegistry = new HashMap<>();

    static {
        commandRegistry.put(CreateOrganizationCommand.class.getCanonicalName(), CreateOrganizationCommand.class);
        commandRegistry.put(CreateOrganizationCommand.class.getSimpleName(), CreateOrganizationCommand.class);

        commandRegistry.put(CreateOrganizationCommand.class.getCanonicalName(), CreateOrganizationCommand.class);
        commandRegistry.put(CreateOrganizationCommand.class.getSimpleName(), CreateOrganizationCommand.class);
    }
    

    public static Class resolveCommandClass(String contentType, String queryCommandName) {
        return resolveCommandClass(resolveCommandName(contentType, queryCommandName));
    }

    private static Class resolveCommandClass(String commandName) {
        Class commandClass = commandRegistry.get(commandName);
        Assert.notNull(commandName, "Unable to resolve name to a command-class: " + commandName);
        return commandClass;
    }

    public static String resolveCommandName(String contentType, String queryCommandName) {

        String domainModel = contentType.replaceFirst(MediaType.APPLICATION_JSON + ";domain-model=", "");
        LOG.debug("domain model = " + domainModel);

        String commandName = queryCommandName.isEmpty() ? domainModel : queryCommandName;
        LOG.debug("command name: " + commandName);

        return commandName;
    }

    public static Object readCommand(String fromJSON, Class toTargetClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(fromJSON, toTargetClass);
    }

}
