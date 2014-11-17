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
import org.springframework.data.annotation.Id;

/**
 *
 * @author Christoffer Børrild
 */
@Entity
public class ServiceInstanceEntry implements Serializable {

    @Id
    @javax.persistence.Id
    private String serviceInstanceIdentifier;
    private String providerIdentifier;
    private String specificationIdentifier;
    private String name;
    private String summary;
    private String coverage; // FIXME: create complex version of coverage instead of json-serialized one

    public String getServiceInstanceIdentifier() {
        return serviceInstanceIdentifier;
    }

    public void setServiceInstanceIdentifier(String serviceInstanceIdentifier) {
        this.serviceInstanceIdentifier = serviceInstanceIdentifier;
    }

    public String getProviderIdentifier() {
        return providerIdentifier;
    }

    public void setProviderIdentifier(String providerIdentifier) {
        this.providerIdentifier = providerIdentifier;
    }

    public String getSpecificationIdentifier() {
        return specificationIdentifier;
    }

    public void setSpecificationIdentifier(String specificationIdentifier) {
        this.specificationIdentifier = specificationIdentifier;
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

    public String getCoverage() {
        return coverage;
    }

    public void setCoverage(String coverage) {
        this.coverage = coverage;
    }

    @Override
    public String toString() {
        return "ServiceInstanceEntry{"
                + "serviceInstanceId=" + serviceInstanceIdentifier
                + ", providerId=" + providerIdentifier
                + ", specificationId=" + specificationIdentifier
                + ", name=" + name
                + ", summary=" + summary
                + ", coverage=" + coverage
                + '}';
    }

}
