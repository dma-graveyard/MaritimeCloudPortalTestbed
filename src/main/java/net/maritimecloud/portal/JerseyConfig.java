/* Copyright (c) 2011 Danish Maritime Authority.
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
package net.maritimecloud.portal;

import net.maritimecloud.portal.resource.AlmanacResource;
import net.maritimecloud.portal.resource.OrganizationResource;
import net.maritimecloud.portal.resource.AuthenticationResource;
import net.maritimecloud.portal.resource.GenericCommandResource;
import net.maritimecloud.portal.resource.UniqueIdResource;
import net.maritimecloud.portal.resource.UserResource;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.web.filter.RequestContextFilter;

/**
 * @author Christoffer Børrild
 */
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        System.out.println("Initializing JerseyConfig");
        register(RequestContextFilter.class);

        // TODO: scanning does not appear to work with java jar launcher (as opposed to 'mvn springboot:run' which does) !?
        //       Explicitly register all resources here instead :-(
        //packages("net.maritimecloud.portal.resource");
        register(AlmanacResource.class);
        register(AuthenticationResource.class);
        register(GenericCommandResource.class);
        register(OrganizationResource.class);
        register(UniqueIdResource.class);
        register(UserResource.class);
        register(LoggingFilter.class);
    }
}
