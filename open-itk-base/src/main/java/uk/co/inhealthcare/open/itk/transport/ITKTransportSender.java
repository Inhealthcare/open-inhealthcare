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

import java.util.Map;

import org.w3c.dom.Document;

import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.payload.ITKMessage;

/**
 * Interface that represents the transport neutral interface for sending ITK messages to a remote
 * destination. 
 * 
 * @author Nick Jones
 * 
 */
public interface ITKTransportSender {

	/**
	 * Operation that sends a message over a physical transport and marshalls the response into an XML Document
	 * or throws an appropriate exception
	 * 
	 * @param message This is ITKMessage to be sent  

	 * @param destination The {@link ITKTransportRoute} providing the transport invocation
	 * path for this operation
	 * 
	 * @param transportProperties A set of properties required by the physical transport.
	 * 
	 * @exception ITKMessagingException If the <code>destination</code> is <code>null</null>,
	 * the <code>request</code> is <code>null</null>, or if there is transport timeout or error
	 * or if there is a technical exception invoking the service provider (for instance if a SOAP
	 * fault is returned by the service provider) 
	 */
	public Document transportSend(ITKMessage message, ITKTransportRoute destination, Map <String,String> transportProperties)
			throws ITKMessagingException;
	
}
