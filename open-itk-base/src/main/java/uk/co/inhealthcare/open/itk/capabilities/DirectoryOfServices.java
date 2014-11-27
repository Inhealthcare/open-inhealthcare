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
package uk.co.inhealthcare.open.itk.capabilities;

import uk.co.inhealthcare.open.itk.infrastructure.ITKAddress;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.service.ITKService;
import uk.co.inhealthcare.open.itk.transport.ITKTransportRoute;

/**
 * An abstraction for an endpoint resolution directory.
 * 
 * This indirection allows for either a simple local directory or a remote directory to be used, using
 * ITK standard addressing
 * 
 * @author Nick Jones
 * 
 */
public interface DirectoryOfServices {

	/**
	 * Resolve the <code>ITKTransportRoute</code> for the supplied <code>service</code>
	 * and logical destination <code>address</code>. 
	 * 
	 * @param serviceId The serviceId representing the ITK service that is being requested
	 * e.g. <code>urn:nhs-itk:services:201005:transferPatient-v1-0</code>
	 * @param address The logical address of the destination e.g. <code>urn:nhs-uk:addressing:ods:TESTORGS:ORGB</code>
	 */
	public ITKTransportRoute resolveDestination(String serviceId, ITKAddress address) throws ITKMessagingException ;

	/**
	 * Get the <code>ITKService</code> for the supplied <code>serviceId</code>
	 * 
	 * @param serviceId The ITK Service Id representing the ITK service that is being requested
	 * e.g. <code>urn:nhs-itk:services:201005:transferPatient-v1-0</code>
	 */
	public ITKService getService(String serviceId) throws ITKMessagingException ;
	
	/**
	 * Check the profile id
	 *
	 * @param profileId the profile id
	 * @return a boolean indicating if the profile is supported or not
	 */
	public boolean isServiceProfileSupported(String profileId);

}
