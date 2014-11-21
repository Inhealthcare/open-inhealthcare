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
package uk.co.inhealthcare.open.itk.transport.WS;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import uk.co.inhealthcare.open.itk.capabilities.AuditService;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.payload.ITKMessage;
import uk.co.inhealthcare.open.itk.payload.ITKSimpleMessageImpl;
import uk.co.inhealthcare.open.itk.transport.ITKSender;
import uk.co.inhealthcare.open.itk.transport.ITKTransportRoute;
import uk.co.inhealthcare.open.itk.transport.ITKTransportSender;

/**
 * The Class ITKSenderWSImpl.
 *
 * @author Nick Jones
 */
public class ITKSenderWSImpl_Mock implements ITKSender {

	private String responseFile = "HappyDayDE.xml";
	
	public void primeResponse(String responseFile){
		this.responseFile = responseFile;
	}
	
	public void setSoapUsername(String soapUsername) {
	}
	public void setSoapFromAddress(String soapFromAddress) {
	}
	public void setToSoapTransform(String toSoapTransform) {
	}
	public void setAuditService(AuditService auditService) {
	}
	public void setTransportService(ITKTransportSender transportService) {
	}

	public ITKTransportRoute parm_destination;
	public ITKMessage parm_request;
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKSender#sendSync(uk.nhs.interoperability.transport.ITKTransportRoute, uk.nhs.interoperability.payload.ITKMessage)
	 */
	@Override
	public ITKMessage sendSync(ITKTransportRoute destination, ITKMessage request) throws ITKMessagingException {

		// Store the parms to be available to any JUNITS
		parm_destination = destination;
		parm_request = request;

		// Construct the Response message
		ITKMessage response = new ITKSimpleMessageImpl(request.getConversationId());
		String responseString;
		try {
			responseString = readInput(this.responseFile);
		} catch (IOException e) {
			throw new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "Error Reading Test File.");
		}
		response.setBusinessPayload(responseString);
		
		return response;
	}
	
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKSender#sendAysnc(uk.nhs.interoperability.transport.ITKTransportRoute, uk.nhs.interoperability.payload.ITKMessage)
	 * Not implemented in this version (not required for SMSP)
	 */
	@Override
	public void sendAsync(ITKTransportRoute destination, ITKMessage request) throws ITKMessagingException {

		ITKMessagingException notImplementedException = 
				new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "Not Yet Implemented");
		throw notImplementedException;
	
	}
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKSender#send(uk.nhs.interoperability.transport.ITKTransportRoute, uk.nhs.interoperability.payload.ITKMessage)
	 * Not implemented in this version (not required for SMSP)
	 */
	@Override
	public void send(ITKTransportRoute destination, ITKMessage request)	throws ITKMessagingException {

		ITKMessagingException notImplementedException = 
				new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "Not Yet Implemented");
		throw notImplementedException;
		
	}
	private String readInput(String testFileName) throws IOException {
		
	    InputStream tis = ITKSenderWSImpl_Mock.class.getResourceAsStream(testFileName);

		BufferedInputStream bis = new BufferedInputStream(tis);
		byte[] contents = new byte[1024];
		int bytesRead = 0;
		String responseString = "";
		while ((bytesRead = bis.read(contents)) != -1) {
			responseString = responseString + new String(contents, 0, bytesRead);
		}
		bis.close();
		
		return responseString ;
	}

}
