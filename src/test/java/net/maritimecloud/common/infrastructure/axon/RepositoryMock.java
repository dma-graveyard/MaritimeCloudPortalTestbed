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
package net.maritimecloud.common.infrastructure.axon;

import org.axonframework.repository.Repository;

/**
 * When testing a CommandHandler that uses multiple (event-) repositories this mock can be used to mimic those 'lookup'-repositories that
 * will not contain the aggregate that changes state.
 * <p>
 * (As suggested in https://groups.google.com/forum/#!topic/axonframework/4Fc26pCtpWI)
 * <p>
 * @author Christoffer Børrild
 */
public class RepositoryMock<T> implements Repository<T> {

    T aggregate;

    public RepositoryMock() {
    }

    public RepositoryMock(T aggregate) {
        this.aggregate = aggregate;
    }

    @Override
    public T load(Object aggregateIdentifier, Long expectedVersion) {
        System.out.println("load " + aggregateIdentifier + " expectedVersion: " + expectedVersion);
        return aggregate();
    }

    @Override
    public T load(Object aggregateIdentifier) {
        System.out.println("load " + aggregateIdentifier);
        return aggregate();
    }

    @Override
    public void add(T aggregate) {
        System.out.println("add " + aggregate);
        this.aggregate = aggregate;
    }

    public T aggregate() {
        return aggregate;
    }

    public void setAggregate(T aggregate) {
        this.aggregate = aggregate;
    }

}
