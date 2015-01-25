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
package net.maritimecloud.common.ddd;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Abstract, generic, JSON serializable (with Jackson) class that constitutes a string based indentifier of a domain object.
 * <p>
 * Subclasses should just add constructors to supply the identifier and a protected default constructor for serialization purposes.
 * <p>
 * @author Christoffer Børrild
 * @param <T>
 */
public abstract class DomainIdentifier<T extends DomainIdentifier> extends ValueObject {

    private String identifier;

    @JsonCreator
    public DomainIdentifier(@JsonProperty("identifier") String anId) {
        this();
        this.setIdentifier(anId);
    }

    public DomainIdentifier(T aDomainId) {
        this(aDomainId.identifier());
    }

    protected DomainIdentifier() {
        super();
    }

    @JsonProperty("identifier")
    public String identifier() {
        return this.identifier;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            T typedObject = (T) anObject;
            equalObjects = this.identifier().equals(typedObject.identifier());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue
                = +(2785 * 5)
                + this.identifier().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return String.format("%1s [id=" + identifier + "]", getAggregateName());
    }

    private void setIdentifier(String anIdentifier) {
        this.assertArgumentNotEmpty(anIdentifier, String.format("An identifier for the %1s is required.", getAggregateName()));

        //FIXME: add rule that check the general identity format rule dashes but no underscore (or viceversa?!?) etc...
        //this.assertArgumentLength(anId, 30, "The identity must be 30 characters or less.");
        this.identifier = anIdentifier;
    }

    protected String getAggregateName() {
        return getClass().getSimpleName();
    }

}
