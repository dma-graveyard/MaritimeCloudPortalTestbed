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
package net.maritimecloud.common.cqrs.contract;

/**
 * This interface describes the interface to the ServiceRegitry in terms of Commands and Events. Whereas the commands are explicitly being
 * the published interface, the description of the events are more like a convenience as they in most cases share the properties of the
 * commands.
 * <p>
 * Events may inherit their properties from corresponding commands, and may extend those properties, as would be needed to make room for
 * enrichment.
 * <p>
 * The generator will take each method defined in the interface and create a corresponding class of the same name (capitalized first
 * letter). The class will be supplied with a constructor with the same signature and properties and getters and setters will be defined for
 * each of the arguments using the names of the arguments. The first argument MUST be the aggregate identity, and the names of the arguments
 * will be used for serialization when serializing to JSON.
 * <p>
 * @author Christoffer Børrild
 */
public interface CqrsContract {

}
