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

import java.io.IOException;
import net.maritimecloud.common.cqrs.contract.SourceGenerator;

/**
 * Run the Main method of this class in order to re-build Command and Events specified in CqrsContracts. This generator will generate source
 * files into the "Generated Sources" folder.
 * <p>
 * The Generator cannot be build without a valid, compilable source base. This bootstrap-problem forms a classic "Hen and the Egg"-problem,
 * since the generated sources are needed to obtain a valid source base.
 * <p>
 * Until the bootstrap-problem is solved, you will need to put the generated classes under version-control, eg. by copying them up into the
 * source-folder, and delete the rest. Otherwise it will be impossible to rebuild from scratch.
 * <p>
 * To solve the problem the generator should use a parser that can run independently of whether the entire code base is in a valid state or
 * not, e.g an ANTLR-based parser.
 * <p>
 * @author Christoffer Børrild
 */
public class ServiceRegistryCqrsApiSourceGenerator {

    public static void main(String[] args) throws IOException {

        System.out.println("sourceGenerator running");
        String target = "./src/main/java/";
        /*
         SourceGenerator sourceGenerator = new SourceGenerator(ServiceRegistryContract.class);
         */
        SourceGenerator sourceGenerator = new SourceGenerator(ServiceRegistryContract.class, target);
        sourceGenerator.generate();

    }

}
