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
package uk.co.inhealthcare.open.itk.service;

import uk.co.inhealthcare.open.itk.capabilities.DirectoryOfServices;

/**
 * Definition of a fully resolved ITKService including the supported pattern, mime type
 * and whether or not the payload should be base64 encoded.
 * 
 * @see DirectoryOfServices#getService(String)
 * 
 * @author Nick Jones
 * 
 */
public interface ITKService {

	/**
	 * Obtains the service identifier associated with this ITKService object
	 * 
	 * @return the ITK service identifier
	 * - e.g. <code>urn:nhs-itk:services:201005:transferPatient-v1-0</code>
	 */
	public String getServiceId();
	
	/**
	 * Determines whether or not this service supports synchronous invocation.
	 * 
	 * @return <code>true</code> if the service supports synchronous invocation
	 * 
	 */
	public boolean supportsSync();
	
	/**
	 * Determines whether or not this service supports an asynchronous invocation.
	 * 
	 * @return <code>true</code> if the service supports asynchronous invocation
	 * 
	 */
	public boolean supportsAsync();
	
	/**
	 * Determines whether this service expects the business payload data to be base64 encoded.
	 * 
	 * Note: In ITK all ADT v2 pipe and hat messages are required to base64 encoded during transmission
	 * 
	 * @return <code>true</code> if the service defines that the payload data
	 * should be base64 encoding during transmission; <code>false</code> otherwise
	 * 
	 */
	public boolean isBase64();
	
	/**
	 * Specifies the appropriate mime type for this service
	 * 
	 * @return The String representing the Mime type that should be set when transmitting ITKMessages on this service
	 * - e.g. <code>text/xml</code>
	 * 
	 */
	public String getMimeType();
}
