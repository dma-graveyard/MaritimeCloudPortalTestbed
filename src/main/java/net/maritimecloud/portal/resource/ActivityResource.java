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

import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import net.maritimecloud.portal.application.ApplicationServiceRegistry;
import net.maritimecloud.serviceregistry.query.ActivityEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AlmanacResource resembles the publicly available API part of the RegistryService.
 * <p>
 * This resource should be accessible by all users.
 * <p>
 * @author Christoffer Børrild
 */
@Path("/api/activity")
public class ActivityResource {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Iterable<ActivityEntry> getOrganizationMembers(
            @QueryParam("username") String username,
            @QueryParam("organizationId") String organizationId,
            @QueryParam("dateTime") Date dateTime
    ) {
        if (!username.isEmpty()) {
            return ApplicationServiceRegistry.activityEntryQueryRepository().findByUsername(username);
        }
        if (!organizationId.isEmpty()) {
            return ApplicationServiceRegistry.activityEntryQueryRepository().findByOrganizationId(organizationId);
        }
        return ApplicationServiceRegistry.activityEntryQueryRepository().findByDateTime(dateTime);
    }

}
