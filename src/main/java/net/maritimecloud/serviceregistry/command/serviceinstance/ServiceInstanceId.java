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
package net.maritimecloud.serviceregistry.command.serviceinstance;

import java.util.Objects;
import net.maritimecloud.portal.domain.model.ValueObject;

/**
 *
 * @author Christoffer Børrild
 */
public class ServiceInstanceId extends ValueObject {

    private String identifier;

    public ServiceInstanceId(String anIdentifier) {
        this();
        this.setIdentifier(anIdentifier);
    }

    public ServiceInstanceId(ServiceInstanceId serviceInstanceId) {
        this(serviceInstanceId.identifier());
    }

    protected ServiceInstanceId() {
        super();
    }

    public String identifier() {
        return this.identifier;
    }

    private void setIdentifier(String anIdentifier) {
        this.assertArgumentNotEmpty(anIdentifier, "The serviceInstance identity is required.");

        //FIXME: add rule that check the general identity format rule dashes but no underscore (or viceversa?!?) etc...
        //this.assertArgumentLength(anId, 30, "The identity must be 30 characters or less.");
        this.identifier = anIdentifier;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.identifier);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ServiceInstanceId other = (ServiceInstanceId) obj;
        return Objects.equals(this.identifier, other.identifier);
    }

    @Override
    public String toString() {
        return "ServiceInstanceId [id=" + identifier + "]";
    }

}
