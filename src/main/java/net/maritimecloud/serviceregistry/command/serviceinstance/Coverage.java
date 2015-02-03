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
import javax.persistence.Column;
import javax.persistence.Embeddable;
import net.maritimecloud.common.domain.ValueObject;

/**
 * A Coverage object is something that has a geographical extend, ie. "covers an area".
 * <p>
 * FIXME: for now this class simply holds a string. This string is intended to hold the JSON representation of the body of the coverage.
 * Later on, we should fully parse and store the object graph.
 * <p>
 * @author Christoffer Børrild
 */
@Embeddable
public class Coverage extends ValueObject {

    @Column(length = 500000)
    String serializedJsonValue;

    public Coverage() {
    }

    public Coverage(String serializedJsonValue) {
        this.serializedJsonValue = serializedJsonValue;
    }

    public String getSerializedJsonValue() {
        return serializedJsonValue;
    }

    public void setSerializedJsonValue(String serializedJsonValue) {
        this.serializedJsonValue = serializedJsonValue;
    }

    @Override
    public String toString() {
        return serializedJsonValue;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.serializedJsonValue);
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
        final Coverage other = (Coverage) obj;
        return Objects.equals(this.serializedJsonValue, other.serializedJsonValue);
    }

}
