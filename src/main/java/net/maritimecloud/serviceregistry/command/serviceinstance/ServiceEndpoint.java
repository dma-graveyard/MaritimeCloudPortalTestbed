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

import java.io.Serializable;
import java.net.URI;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * A ServiceEndpoint constitutes the URI for accessing a ServiceInstance
 * <p>
 * @author Christoffer Børrild
 */
@Embeddable
public class ServiceEndpoint /*extends ValueObject*/ implements Serializable {

    @Column(name = "ENDPOINT_URI", length = 256)
    private URI uri;
    
    protected ServiceEndpoint() {
    }

    public ServiceEndpoint(URI uri) {
        this.uri = uri;
    }

    public URI getUri() {
        return uri;
    }

    protected void setUri(URI uri) {
        this.uri = uri;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.uri);
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
        final ServiceEndpoint other = (ServiceEndpoint) obj;
        return Objects.equals(this.uri, other.uri);
    }

}
