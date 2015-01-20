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
import java.util.List;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import net.maritimecloud.portal.application.ApplicationServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
    public Page getActivities(
            @QueryParam("username") String username,
            @QueryParam("organizationIds") List<String> organizationIds,
            @QueryParam("dateTime") long dateTime,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        Pageable pageable = new PageRequest(page, size, new Sort(Sort.Direction.DESC, "dateTime"));
        if (username != null && !username.isEmpty()) {
            return ApplicationServiceRegistry.activityEntryQueryRepository().findByUsername(username, pageable);
        }
        if (organizationIds != null && !organizationIds.isEmpty()) {
            return ApplicationServiceRegistry.activityEntryQueryRepository().findByOrganizationIdIn(organizationIds, pageable);
        }
        return ApplicationServiceRegistry.activityEntryQueryRepository().findByIsPublicTrueAndDateTimeAfter(new Date(dateTime), pageable);
    }

}
