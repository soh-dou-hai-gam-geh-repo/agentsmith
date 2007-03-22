/*
 * SmithLoader - Loads an agent at runtime (after jvm start up)
 * Copyright (C) 2007 Federico Fissore
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.fridrik.agent;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

/**
 * SmithLoader loads a Smith agent after the jvm start up. This class is
 * experimental. See {@link #hotStart(String, String, String, int)} for more
 * details.
 * 
 * @author Federico Fissore (federico@fsfe.org)
 */
public class SmithLoader {

	/**
	 * Asks the jvm to load the Smith agent specified by the absolute path
	 * parameter and telling the agent to monitor the specified folder. This class
	 * is experimental as it needs a sort of low-level interaction with the
	 * operating system. The following assumptions are done:
	 * <ul>
	 * <li>The Process Identifier (PID of the target jvm) is a integer </li>
	 * <li>The latest started jvm is the one we want to plug the agent into and
	 * it has the greatest PID</li>
	 * </ul>
	 * If any of these assumptions are NOT valid for your operating system, please
	 * report is ASAP and contribute to the project
	 * 
	 * @param pathToSmithJar
	 *          the absolute path to the Smith jar
	 * @param classesFolder
	 *          the absolute path of the folder to watch
	 * @param jarFolder
	 *          the absolute path of the folder to watch
	 * @param monitorPeriod
	 *          the period between a class folder check and one another. Must be
	 *          greter than 500 to be accepted
	 * @throws Exception
	 *           if something goes wrong
	 */
	public static void hotStart(String pathToSmithJar, String classesFolder,
			String jarFolder, int monitorPeriod) throws Exception {
		List<VirtualMachineDescriptor> vmds = new LinkedList<VirtualMachineDescriptor>(
				VirtualMachine.list());
		Collections.sort(vmds, new Comparator<VirtualMachineDescriptor>() {

			public int compare(VirtualMachineDescriptor arg0,
					VirtualMachineDescriptor arg1) {
				return Integer.valueOf(arg1.id()).compareTo(Integer.valueOf(arg0.id()));
			}

		});

		SmithArgs args = new SmithArgs(classesFolder, jarFolder, monitorPeriod);

		VirtualMachine vm = VirtualMachine.attach(vmds.get(0));
		vm.loadAgent(pathToSmithJar, args.toString());
	}

}
