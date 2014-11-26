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
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceType;
import org.springframework.data.annotation.Id;

/**
 * @author Christoffer Børrild
 */
@Entity
public class ServiceSpecificationEntry implements Serializable {

    @Id
    @javax.persistence.Id
    private String serviceSpecificationIdentifier;
    private String ownerIdentifier;
    private ServiceType serviceType;
    private String name;
    private String summary;

    public String getServiceSpecificationIdentifier() {
        return serviceSpecificationIdentifier;
    }

    public void setServiceSpecificationIdentifier(String identifier) {
        this.serviceSpecificationIdentifier = identifier;
    }

    public String getOwnerIdentifier() {
        return ownerIdentifier;
    }

    public void setOwnerIdentifier(String ownerIdentifier) {
        this.ownerIdentifier = ownerIdentifier;
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
                + "serviceSpecificationIdentifier=" + serviceSpecificationIdentifier
                + ", ownerIdentifier=" + ownerIdentifier
                + ", serviceType=" + serviceType
                + ", name=" + name
                + ", summary=" + summary
                + '}';
    }

}
