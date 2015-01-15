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
package net.maritimecloud.portal.infrastructure.security.shiro;

import java.io.IOException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jesper Tejlgaard
 * @author Christoffer Børrild
 */
public class McpAuthorizationFilter extends RolesAuthorizationFilter {

    Logger logger = LoggerFactory.getLogger(McpAuthorizationFilter.class);

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue)
            throws IOException {
        logger.debug("Access denied: {}, {}", WebUtils.toHttp(request).getRequestURI(), mappedValue);

        Subject subject = getSubject(request, response);

        HttpServletResponse httpResp = WebUtils.toHttp(response);
        httpResp.setContentType("application/json");
        if (subject.getPrincipal() == null) {
            httpResp.sendError(HttpServletResponse.SC_UNAUTHORIZED);

//            PrintWriter writer = new PrintWriter(response.getOutputStream());
//            Util.writeJson(writer, new Error(AuthCode.UNAUTHENTICATED, "User not logged in"));
        } else {
            httpResp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
//            Util.writeJson(response.getWriter(), new Error(AuthCode.UNAUTHORIZED,
//                    "User is logged in, but does not have necessary permissions"));
        }
        return false;
    }
}
