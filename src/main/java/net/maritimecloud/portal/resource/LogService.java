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

import net.maritimecloud.portal.domain.model.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Application specific logging Service for reporting significant application events to a general logging mechanism. 
 *  
 * @author Christoffer Børrild
 */
public class LogService {

    private static final Logger LOG = LoggerFactory.getLogger(LogService.class);
    
    
    void reportUserLoggedIn(User user) {
        LOG.info("User {} logged in", user.username());
    }

    void reportWrongUsernamePassword(String username) {
        LOG.info("User {} not logged in (wrong username / password)", username);
    }

    void reportDebugError(String current_user_is_not_authenticated_, Exception e) {
        LOG.info("Current user is not authenticated: ", e);
    }

    void reportUserLoggingOut() {
        LOG.info("User logged out");
    }
    
}
