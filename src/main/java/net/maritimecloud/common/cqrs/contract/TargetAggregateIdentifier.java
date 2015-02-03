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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation that marks a parameter as being the identifier of the target aggregate.
 * <p>
 * Using this annotation on a parameter in a method the Contract interface will make this parameter marked with the AXON
 * TargetAggregateIdentifier in the resulting event or command class. If omitting this annotation, the first parameter of the method will be
 * used as default.
 * <p>
 * @author Christoffer Børrild
 * @see org.axonframework.commandhandling.annotation.TargetAggregateIdentifier
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.PARAMETER})
public @interface TargetAggregateIdentifier {

}
