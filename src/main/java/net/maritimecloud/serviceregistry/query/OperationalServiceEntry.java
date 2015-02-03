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
package net.maritimecloud.serviceregistry.query;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Christoffer Børrild
 */
@Entity
public class OperationalServiceEntry implements Serializable {

    @Id
    private String operationalServiceId;
    private String ownerId;
    private String name;
    private String summary;

    public OperationalServiceEntry() {
    }

    public OperationalServiceEntry(String operationalServiceId, String ownerId, String name, String summary) {
        this.operationalServiceId = operationalServiceId;
        this.ownerId = ownerId;
        this.name = name;
        this.summary = summary;
    }

    public String getOperationalServiceId() {
        return operationalServiceId;
    }

    public void setOperationalServiceId(String operationalServiceId) {
        this.operationalServiceId = operationalServiceId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "OperationalServiceEntry{"
                + "operationalServiceId=" + operationalServiceId
                + ", ownerId=" + ownerId
                + ", name=" + name
                + ", summary=" + summary
                + '}';
    }

}
