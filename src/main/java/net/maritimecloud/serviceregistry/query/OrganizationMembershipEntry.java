/* Copyright 2015 Danish Maritime Authority.
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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Christoffer Børrild
 */
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"organizationId", "userName"})})
public class OrganizationMembershipEntry {

    @Id
    private String membershipId;
    private String organizationId;
    private String username;
    boolean acceptedByUser;
    boolean acceptedByOrganization;
    boolean active;

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAcceptedByUser() {
        return acceptedByUser;
    }

    public void setAcceptedByUser(boolean acceptedByUser) {
        this.acceptedByUser = acceptedByUser;
    }

    public boolean isAcceptedByOrganization() {
        return acceptedByOrganization;
    }

    public void setAcceptedByOrganization(boolean acceptedByOrganization) {
        this.acceptedByOrganization = acceptedByOrganization;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    
}
