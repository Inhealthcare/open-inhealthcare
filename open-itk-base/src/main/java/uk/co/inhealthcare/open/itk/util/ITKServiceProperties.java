/*
	   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package uk.co.inhealthcare.open.itk.util;

import java.io.IOException;
import java.util.Properties;

/**
 * The Class ITKServiceProperties.
 *
 * @author Nick Jones
 */
public class ITKServiceProperties {
    
	/** The props. */
	private static Properties props = new Properties();
	static {
		try {
			props.load(ITKServiceProperties.class.getResourceAsStream("/service.properties"));
			
		} catch (IOException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Gets the property.
	 *
	 * @param propertyName the property name
	 * @return the property
	 */
	public static String getProperty(String propertyName){
		return props.getProperty(propertyName);
	}

}