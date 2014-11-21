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
package uk.co.inhealthcare.open.itk.source;

import uk.co.inhealthcare.open.itk.infrastructure.ITKMessageProperties;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.payload.ITKMessage;

/**
 * Interface that represents the application entry point for sending ITK messages to a remote
 * Destination. The interface provides both synchronous and asynchronous operations where
 * <quote>synchronous</quote> and <quote>asynchronous</quote> represent the perspective of the
 * the application rather than necessarily the one or more underlying transport hops.
 * 
 * @see ITKMessageConsumer
 * @see ITKCallbackHandler
 * 
 * @author Nick Jones
 * 
 */
public interface ITKMessageSender {

	/**
	 * Operation that allows a business payload to be sent to the
	 * destination service/system with no response expected (a.k.a. "fire and forget").
	 * 
	 * @param request    This is the business payload being sent which 
	 * must include an appropriately populated {@link ITKMessageProperties}. 
	 *  
	 * @exception ITKMessagingException If the addressing and message meta-data
	 * properties are not populated, the payload is null/blank or if there is a
	 * technical exception invoking the service provider,
	 * first node in the distribution (for instance if a SOAP 
	 * fault is returned by the service provider) 
	 */
	public void send(ITKMessage request) throws ITKMessagingException;

	/**
	 * Operation that allows a business payload to be sent synchronously to the
	 * destination service/system returning the appropriate business response.
	 * 
	 * @param request    This is the business payload being sent which 
	 * must include an appropriately populated {@link ITKMessageProperties}.  
	 * 
	 * @exception ITKMessagingException If the addressing and message meta-data
	 * properties are not populated, the payload is null/blank or if there is a
	 * technical exception invoking the service provider (for instance if a SOAP
	 * fault is returned by the service provider) 
	 */
	public ITKMessage sendSync(ITKMessage request) throws ITKMessagingException;

	/**
	 * Operation that allows a business payload to be sent asynchronously to the
	 * destination service/system.
	 * No object is returned but transport/protocol errors may be thrown
	 * The response will be handled by a separate process {@link ITKCallbackHandler#onMessage(ITKMessage)}
	 * 
	 * @param businessPayload    This is the business payload being sent which 
	 * must include an appropriately populated {@link ITKMessageProperties}.  
	 * 
	 * @exception ITKMessagingException If the addressing and message meta-data
	 * properties are not populated, the payload is
	 * null/blank or if there is a technical exception invoking the service provider
	 * such as underlying transport errors or timeouts (for instance if a SOAP fault
	 * is returned synchronously by the service provider) 
	 */
	public void sendAsync(ITKMessage businessPayload) throws ITKMessagingException;


}
