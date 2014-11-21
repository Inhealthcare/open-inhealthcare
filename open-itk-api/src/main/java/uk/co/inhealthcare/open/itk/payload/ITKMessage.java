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
package uk.co.inhealthcare.open.itk.payload;

import uk.co.inhealthcare.open.itk.infrastructure.ITKMessageProperties;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.transport.ITKTransportProperties;

/**
 * Interface representing the characteristics of an ITK message being transmitted via the
 * ITK message transport and distribution layers (i.e. this API).
 *  
 * As well as providing a standard way of setting / obtaining
 * the business payload (i.e. the business message itself) it 
 * also provides a standardised set of meta-data for the message
 * too via the {@link ITKMessageProperties}.
 * 
 * @author Nick Jones
 * 
 */
public interface ITKMessage {


	/**
	 * Obtains the conversationId associated with this message.
	 * This is used to relate all audit entries together for a request/response conversation.
	 * 
	 * @return the String conversation Id
	 */
	public String getConversationId();

	/**
	 * Obtains the <code>ITKTransportProperties</code> associated with the message
	 * 
	 * @return ITKTransportProperties
	 */
	public ITKTransportProperties getTransportProperties();
	
	/**
	 * Set the <code>ITKTransportProperties</code> associated with the message
	 * 
	 * @param itkTransportProperties 
	 */
	public void setTransportProperties(ITKTransportProperties itkTransportProperties);

	/**
	 * Obtains the {@link ITKMessageProperties} associated with this
	 * message. These properties provide relevant addressing and meta-data
	 * about the message.
	 * 
	 * @return the ITKMessageProperties associated with the message or
	 * <code>null</code> if no ITKMessageProprties have been set.
	 */
	public ITKMessageProperties getMessageProperties();
	
	/**
	 * Sets the {@link ITKMessageProperties} associated with this
	 * message. These properties provide relevant addressing and meta-data
	 * about the message.
	 * 
	 * @param itkMessageProperties the ITKMessageProperties associated with the message.
	 */
	public void setMessageProperties(ITKMessageProperties itkMessageProperties);
	
	/**
	 * Sets the business payload portion of the message to be transmitted via
	 * the ITK transport and distribution infrastructure.<br/><br/>
	 * 
	 * <b>Note:</b> those (ADT v2 pipe and hat) messages that ITK requires
	 * to be base64 encoded when being transmitted must not need to be pre-encoded - i.e.
	 * they should be supplied in plain text. It is the responsibility of the implementation
	 * to ensure that appropriate encoding/decoding is performed behind the scenes.<br/><br/>
	 * 
	 * <b>Note: </b> for version 0.1 the signature of this method has been defined as a String
	 * to keep implementation simple. As a consequence implementations should ensure that payloads
	 * are provided as UTF-16 encoded strings. 
	 *  
	 * @param businessPayload A string representation of the business payload message
	 */
	public void setBusinessPayload(String businessPayload);
	
	/**
	 * Obtains the business payload portion of the message to be transmitted via
	 * the ITK transport and distribution infrastructure.<br/><br/>
	 * 
	 * <b>Note:</b> those (ADT v2 pipe and hat) messages that ITK requires
	 * to be base64 encoded when being transmitted will not be encoded  - i.e.
	 * they will be supplied in plain text. It is the responsibility of the implementation
	 * to ensure that appropriate encoding/decoding is performed behind the scenes.<br/><br/>
	 * 
	 * @return for version 0.1 the return type of this method has been defined as a String
	 * to keep implementation simple. As a consequence the business payload will be returned as a
	 * UTF-16 encoded string
	 */
	public String getBusinessPayload();
	
	/**
	 * Builds the full ITKMessage including any wrappers
	 * 
	 * @throws ITKMessagingException If there is an error serialising the
	 * full ITKMessage
	 */
	public void buildFullMessage(String templateName) throws ITKMessagingException;

	public String getFullMessage() ;
	
	/**
	 * Indicates whether this ITKMessage instance is
	 * in response to a request message such as a query
	 * 
	 * @return <code>true</code> if this message is a
	 * response, <code>false</code> otherwise.
	 */
	public boolean isResponse();
	
	/**
	 * Returns true if this message is a base64 encoded message
	 * 
	 * @return boolean true if the messages is base64 encoded
	 */
	public boolean isBase64() ;

	/**
	 * Indicates if this is a base64 encoded message
	 * 
	 * @param isBase64
	 */
	public void setBase64(boolean isBase64) ;

	/**
	 * Returns the mime type of the message
	 * 
	 * @return Mime type of the message
	 */
	public String getMimeType() ;

	/**
	 * Sets the mime type
	 * 
	 * @param mimeType
	 */
	public void setMimeType(String mimeType) ;


}