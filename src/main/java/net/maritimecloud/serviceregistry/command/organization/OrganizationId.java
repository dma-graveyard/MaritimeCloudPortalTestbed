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
package net.maritimecloud.serviceregistry.command.organization;

import net.maritimecloud.portal.domain.model.ValueObject;

/**
 *
 * @author Christoffer Børrild
 */
public class OrganizationId extends ValueObject {

    private String identifier;

    public OrganizationId(String anId) {
        this();
        this.setIdentifier(anId);
    }

    public OrganizationId(OrganizationId anOrganizationId) {
        this(anOrganizationId.identifier());
    }

    protected OrganizationId() {
        super();
    }

    public String identifier() {
        return this.identifier;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            OrganizationId typedObject = (OrganizationId) anObject;
            equalObjects = this.identifier().equals(typedObject.identifier());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (2785 * 5)
            + this.identifier().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "OrganizationId [id=" + identifier + "]";
    }

    private void setIdentifier(String anIdentifier) {
        this.assertArgumentNotEmpty(anIdentifier, "The organization identity is required.");
        
        //FIXME: add rule that check the general identity format rule dashes but no underscore (or viceversa?!?) etc...
        
        //this.assertArgumentLength(anId, 30, "The organization identity must be 30 characters or less.");
        this.identifier = anIdentifier;
    }    
    
    
}
