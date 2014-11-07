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
package net.maritimecloud.serviceregistry.servicespecification;

import java.util.Objects;
import net.maritimecloud.portal.domain.model.ValueObject;

/**
 *
 * @author Christoffer Børrild
 */
public class ServiceSpecificationId extends ValueObject {

    private String identifier;

    public ServiceSpecificationId(String anIdentifier) {
        this();
        this.setIdentifier(anIdentifier);
    }

    public ServiceSpecificationId(ServiceSpecificationId serviceSpecificationId) {
        this(serviceSpecificationId.identifier());
    }

    protected ServiceSpecificationId() {
        super();
    }

    public String identifier() {
        return this.identifier;
    }

    private void setIdentifier(String anIdentifier) {
        this.assertArgumentNotEmpty(anIdentifier, "The serviceSpecification identity is required.");

        //FIXME: add rule that check the general identity format rule dashes but no underscore (or viceversa?!?) etc...
        //this.assertArgumentLength(anId, 30, "The identity must be 30 characters or less.");
        this.identifier = anIdentifier;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.identifier);
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
        final ServiceSpecificationId other = (ServiceSpecificationId) obj;
        return Objects.equals(this.identifier, other.identifier);
    }

    @Override
    public String toString() {
        return "ServiceSpecificationId [id=" + identifier + "]";
    }

}
