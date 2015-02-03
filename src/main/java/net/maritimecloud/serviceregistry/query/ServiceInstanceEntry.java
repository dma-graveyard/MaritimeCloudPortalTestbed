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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import net.maritimecloud.serviceregistry.infrastructure.jackson.CoverageSerializer;
import net.maritimecloud.serviceregistry.command.serviceinstance.Coverage;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceEndpoint;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceType;

/**
 *
 * @author Christoffer Børrild
 */
@Entity
public class ServiceInstanceEntry implements Serializable {

    @Id
    @Column(length = 256)
    private String serviceInstanceId;
    private String providerId;
    private String specificationId;
    private String primaryAlias;
    private String name;
    @Column(length = 1000)
    private String summary;
    @JsonSerialize(using = CoverageSerializer.class)
    @Embedded
    private Coverage coverage; // FIXME: create complex version of coverage instead of json-serialized one
    @ElementCollection(fetch = FetchType.EAGER) // FIXME: introduce a separate view with endpoints-per-instance!
    private final List<ServiceEndpoint> endpoints;
    private ServiceType specificationServiceType;

    public ServiceInstanceEntry() {
        this.endpoints = new ArrayList<>();
    }

    public String getServiceInstanceId() {
        return serviceInstanceId;
    }

    public void setServiceInstanceId(String serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getSpecificationId() {
        return specificationId;
    }

    public void setSpecificationId(String specificationId) {
        this.specificationId = specificationId;
    }

    public String getPrimaryAlias() {
        return primaryAlias;
    }

    public void setPrimaryAlias(String primaryAlias) {
        this.primaryAlias = primaryAlias;
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

    @JsonSerialize(using = CoverageSerializer.class)
    public Coverage getCoverage() {
        return coverage;
    }

    @JsonSerialize(using = CoverageSerializer.class)
    public void setCoverage(Coverage coverage) {
        this.coverage = coverage;
    }

    public List<ServiceEndpoint> getEndpoints() {
        return endpoints;
    }

    public ServiceType getSpecificationServiceType() {
        return specificationServiceType;
    }

    public void setSpecificationServiceType(ServiceType serviceType) {
        this.specificationServiceType = serviceType;
    }

    @Override
    public String toString() {
        return "ServiceInstanceEntry{"
                + "serviceInstanceId=" + serviceInstanceId
                + ", providerId=" + providerId
                + ", specificationId=" + specificationId
                + ", name=" + name
                + ", summary=" + summary
                + ", coverage=" + coverage
                + ", endpoints=" + endpoints
                + ", specificationServiceType=" + specificationServiceType
                + '}';
    }

    void addEndpoint(ServiceEndpoint serviceEndpoint) {
        getEndpoints().add(serviceEndpoint);
    }

    void removeEndpoint(ServiceEndpoint serviceEndpoint) {
        getEndpoints().remove(serviceEndpoint);
    }

}
