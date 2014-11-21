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
package uk.co.inhealthcare.open.itk.transport;

import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.payload.ITKMessage;
import uk.co.inhealthcare.open.itk.source.ITKMessageSender;

/**
 * Interface that represents the transport neutral interface for sending ITK messages to a remote
 * destination. The interface provides both synchronous and asynchronous operations where
 * <quote>synchronous</quote> and <quote>asynchronous</quote> represent the perspective of the
 * the application rather than necessarily the one or more underlying transport hops.
 * 
 * @author Nick Jones
 * 
 */
public interface ITKSender {

	/**
	 * Operation that allows a business payload and ITK wrappers (e.g. DistributionEnvelope)
	 * to be sent to the destination service/system via the transport specific
	 * implementation.<br/><br/>
	 * 
	 * Expected to be used in conjunction with {@link ITKMessageSender#send(ITKMessage)} for
	 * the specific transport associated with the service
	 * 
	 * @param destination The {@link ITKTransportRoute} providing the transport invocation
	 * path for this operation
	 * 
	 * @param request This is the ITK Wrapped message that is being sent.  
	 * 
	 * @exception ITKMessagingException If the <code>destination</code> is <code>null</null>,
	 * the <code>request</code> is <code>null</null>, or if there is transport timeout or error
	 * or if there is a technical exception invoking the service provider (for instance if a SOAP
	 * fault is returned by the service provider) 
	 */
	public void send(ITKTransportRoute destination, ITKMessage request) throws ITKMessagingException;
	
	/**
	 * Operation that allows a business payload and ITK wrappers (e.g. DistributionEnvelope)
	 * to be sent synchronously to the destination service/system via the transport specific
	 * implementation.<br/><br/>
	 * 
	 * Expected to be used in conjunction with {@link ITKMessageSender#sendSync(ITKMessage)} for
	 * the specific transport associated with the service.
	 * 
	 * @param destination The {@link ITKTransportRoute} providing the transport invocation
	 * path for this operation
	 * 
	 * @param request This is the ITK Wrapped message that is being sent.  
	 * 
	 * @exception ITKMessagingException If the <code>destination</code> is <code>null</null>,
	 * the <code>request</code> is <code>null</null>, or if there is transport timeout or error
	 * or if there is a technical exception invoking the service provider (for instance if a SOAP
	 * fault is returned by the service provider) 
	 */
	public ITKMessage sendSync(ITKTransportRoute destination, ITKMessage request) throws ITKMessagingException;
	
	/**
	 * Operation that allows a business payload and ITK wrappers (e.g. DistributionEnvelope)
	 * to be sent asynchronously to the destination service/system via the transport specific
	 * implementation.<br/><br/>
	 * 
	 * Expected to be used in conjunction with {@link ITKMessageSender#sendAsync(ITKMessage)} for
	 * the specific transport associated with the service.
	 * 
	 * @param destination The {@link ITKTransportRoute} providing the transport invocation
	 * path for this operation
	 * 
	 * @param request This is the ITK Wrapped message that is being sent.  
	 * 
	 * @exception ITKMessagingException If the <code>destination</code> is <code>null</null>,
	 * the <code>request</code> is <code>null</null>, or if there is transport timeout or error
	 * or if there is a technical exception invoking the transport for the first leg
	 * of the routed message's path to the service provider (depending on the deployment landscape this
	 * could be an exception returned by the service provider if there is only one leg for the routed
	 * message) 
	 */
	public void sendAsync(ITKTransportRoute destination, ITKMessage request) throws ITKMessagingException;
	
}
