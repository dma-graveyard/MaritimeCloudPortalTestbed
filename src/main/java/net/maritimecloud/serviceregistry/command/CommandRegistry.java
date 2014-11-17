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
package net.maritimecloud.serviceregistry.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.axonframework.common.Assert;

/**
 *
 * @author Christoffer Børrild
 */
public class CommandRegistry implements CommandClassResolver {

    private final Map<String, Class> commandClassRegistry = new HashMap<>();
    boolean packageMode = false;

    public CommandRegistry(Class... classes) {
        for (Class aClass : classes) {
            register(aClass);
        }
    }

    public CommandRegistry(boolean packageMode, Class... packageClasses) {
        this(packageClasses);
        this.packageMode = packageMode;
    }

    @Override
    public Class resolve(String commandName) {
        Class commandClass = resolveToRegisteredClass(commandName);
        if (packageMode && commandClass == null) {
            commandClass = resolveFromPackage(commandName);
        }
        if (commandClass == null) {
            throw new IllegalStateException("Unable to resolve command name to class: " + commandName);
        }
        return commandClass;
    }

    private Class resolveToRegisteredClass(String commandName) {
        return commandClassRegistry.get(commandName);
    }

    private Class resolveFromPackage(String commandName) {

        // iterates all registred class to see if they can find a resource of that name 
        // (hence, if a class in same packages match the name)
        for (Map.Entry<String, Class> entrySet : entries()) {
            Class aClass = entrySet.getValue();
            try {
                return aClass.getClassLoader().loadClass(aClass.getPackage().getName().concat(".").concat(commandName));
            } catch (ClassNotFoundException ex) {
            }
        }

        Class commandClass = commandClassRegistry.get(commandName);
        Assert.notNull(commandName, "Unable to resolve command name to class: " + commandName);
        return commandClass;
    }

    public Set<Map.Entry<String, Class>> entries() {
        return commandClassRegistry.entrySet();
    }

    public void register(Class aClass) {
        commandClassRegistry.put(aClass.getSimpleName(), aClass);
        commandClassRegistry.put(aClass.getCanonicalName(), aClass);
    }

}
