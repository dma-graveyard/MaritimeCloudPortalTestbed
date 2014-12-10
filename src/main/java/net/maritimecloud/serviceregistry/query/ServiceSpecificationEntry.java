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
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceType;

/**
 * @author Christoffer Børrild
 */
@Entity
public class ServiceSpecificationEntry implements Serializable {

    @Id
    private String serviceSpecificationId;
    private String ownerId;
    private String name;
    private String summary;
    private ServiceType serviceType;

    public String getServiceSpecificationId() {
        return serviceSpecificationId;
    }

    public void setServiceSpecificationId(String serviceSpecificationId) {
        this.serviceSpecificationId = serviceSpecificationId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
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
        return "ServiceSpecificationEntry{"
                + "serviceSpecificationId=" + serviceSpecificationId
                + ", ownerId=" + ownerId
                + ", serviceType=" + serviceType
                + ", name=" + name
                + ", summary=" + summary
                + '}';
    }

}
