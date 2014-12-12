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
package net.maritimecloud.cqrs.tool;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation that marks an interface method as an DSL specification of a CQRS Event object
 * <p>
 * Methods marked with this annotation will be used as templates for generating a corresponding CQRS Event Objects.
 * <p>
 * @author Christoffer Børrild
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.TYPE})
public @interface Event {

    /**
     * Add a class name of a class that this class should extend (other method must have same signature!!! ...for now)
     * <p>
     * Useful when several class should share a super type.
     * <p>
     * TODO: in the future the generator should allow shared signature, where common types will be delegated to super, and only the
     * remainder is managed by the class itself.
     * <p>
     * @return name of the class to extend
     */
    String[] extend() default {};
}
