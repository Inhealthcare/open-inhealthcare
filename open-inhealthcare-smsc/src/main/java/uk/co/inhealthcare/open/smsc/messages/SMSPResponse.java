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
package uk.co.inhealthcare.open.smsc.messages;

import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.payload.ITKMessage;
import uk.co.inhealthcare.open.jsat.ITKResponse;

/**
 * The Class SMSCResponse.
 *
 * @author Nick Jones
 * @since 0.1
 */
public abstract class SMSPResponse extends ITKResponse {

	public SMSPResponse(ITKMessage response) throws ITKMessagingException {
		
		if (response==null || response.getBusinessPayload() == null){
			throw new ITKMessagingException("No payload returned from service");
		}

		// Store the raw response
		this.responsePayload = response.getBusinessPayload();
		
		// Copy the conversationId to the SMSP response
		this.conversationId = response.getConversationId();
		
	}
	
	public SMSPResponse(String responseCode, String conversationId){
		// Build The Error Response
		this.responseCode = responseCode;
		this.conversationId = conversationId;
		
	}
	/** The response code. */
	protected String responseCode;

	/**
	 * Gets the response code.
	 *
	 * @return the response code
	 */
	public String getResponseCode() {
		return responseCode;
	}
	
	/**
	 * Sets the response code.
	 *
	 * @param responseCode the new response code
	 */
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String serialise(){
		return responsePayload;
	}
	
	public abstract String getNhsNumber();
	
}
