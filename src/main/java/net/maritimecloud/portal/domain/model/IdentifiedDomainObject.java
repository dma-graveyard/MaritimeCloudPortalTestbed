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
package net.maritimecloud.portal.domain.model;

import net.maritimecloud.common.ddd.AssertionConcern;
import java.io.Serializable;

/**
 * @author Christoffer Børrild
 */
public class IdentifiedDomainObject extends AssertionConcern implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    protected IdentifiedDomainObject() {
        super();

        this.setId(-1);
    }

    protected long id() {
        return this.id;
    }

    private void setId(long anId) {
        this.id = anId;
    }
}