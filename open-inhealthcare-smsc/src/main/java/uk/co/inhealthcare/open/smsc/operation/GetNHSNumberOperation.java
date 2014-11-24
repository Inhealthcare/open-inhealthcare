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

package uk.co.inhealthcare.open.smsc.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.inhealthcare.open.itk.infrastructure.ITKIdentity;
import uk.co.inhealthcare.open.itk.infrastructure.ITKIdentityImpl;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.payload.ITKMessage;
import uk.co.inhealthcare.open.jsat.ConfigurationException;
import uk.co.inhealthcare.open.jsat.ValidationException;
import uk.co.inhealthcare.open.smsc.messages.GetNHSNumberRequest;
import uk.co.inhealthcare.open.smsc.messages.GetNHSNumberResponse;
import uk.co.inhealthcare.open.smsc.messages.SMSPRequest;
import uk.co.inhealthcare.open.smsc.messages.SMSPResponse;
import uk.co.inhealthcare.open.smsc.messages.logging.LoggingException;

/**
 * This class accepts the business information required for a Get NHS Number operation,
 * validates the data and builds an ITK Message representing that request.
 * This is then sent to the provider service using the ITK API. The ITK response is then parsed into
 * a java response object and passed back to the caller.
 * 
 * This is a lightweight class with most of the work being done by the class hierarchy.
 * 
 * @author Nick Jones
 *
 */
public class GetNHSNumberOperation extends SMSCBusinessOperation { 
	
	@SuppressWarnings("unused")
	private final static Logger logger = LoggerFactory.getLogger(GetNHSNumberOperation.class);
	
	/**
	 * Process the Get NHS Number Request
	 * 
	 * @param request the Get NHS Number Request
	 * @return the response from GetNHSNumber as a java object
	 * @throws ValidationException
	 * @throws ConfigurationException
	 * @throws LoggingException
	 */
	public GetNHSNumberResponse process(GetNHSNumberRequest request) 
			throws ValidationException, ConfigurationException, LoggingException {

		return (GetNHSNumberResponse) processCommon(request);
		
	}

	@Override
	protected void validate(SMSPRequest request) throws ValidationException {
		GetNHSNumberRequest gnnRequest = (GetNHSNumberRequest) request;
		validateDateOfBirth(gnnRequest.getDateOfBirth(),TRACE); 
		validateSurname(gnnRequest.getSurname(),TRACE);
		validateGivenName(gnnRequest.getGivenName(),TRACE);
		validateGender(gnnRequest.getGender(),TRACE);
		validatePostcode(gnnRequest.getPostcode(),TRACE);
	}

	@Override
	protected void setPatientIdentities(SMSPRequest smspRequest, ITKMessage itkMessage)
			throws ValidationException {

		GetNHSNumberRequest gnnRequest = (GetNHSNumberRequest) smspRequest;
		if ((gnnRequest.getLocalPatientIdentifier()==null) || (gnnRequest.getLocalPatientIdentifier().isEmpty()) ){
			// no patient id to add
		} else {	
			itkMessage.getMessageProperties().addPatientIdentity(
					new ITKIdentityImpl(gnnRequest.getLocalPatientIdentifier(),ITKIdentity.LOCAL_PATIENT_TYPE));
		}
	}

	@Override
	protected SMSPResponse marshallResponse(ITKMessage response) throws ITKMessagingException {
		return new GetNHSNumberResponse(response);
		
	}
	
	@Override
	protected SMSPResponse marshallResponse(String responseCode, String conversationId) {
		return new GetNHSNumberResponse(responseCode, conversationId);
		
	}

}
