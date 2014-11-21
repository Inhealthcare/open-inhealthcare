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
import uk.co.inhealthcare.open.smsc.messages.GetPatientDetailsRequest;
import uk.co.inhealthcare.open.smsc.messages.GetPatientDetailsResponse;
import uk.co.inhealthcare.open.smsc.messages.SMSPRequest;
import uk.co.inhealthcare.open.smsc.messages.SMSPResponse;
import uk.co.inhealthcare.open.smsc.messages.logging.LoggingException;

/**
 * This class accepts the business information required for a Get Patient Details Operation,
 * validates the data and builds an ITK Message representing that request.
 * This is then sent to the provider service using the ITK API. The ITK response is then parsed into
 * a java response object and passed back to the caller.
 * 
 * This is a lightweight class with most of the work being done by the class hierarchy.
 * 
 * @author Nick Jones
 *
 */
public class GetPatientDetailsOperation extends SMSCBusinessOperation { 
	
	@SuppressWarnings("unused")
	private final static Logger logger = LoggerFactory.getLogger(GetPatientDetailsOperation.class);

	/**
	 * Process the GetPatientDetails Request
	 * 
	 * @param request the GetPatientDetails
	 * @return the response from GetPatientDetails as a java object
	 * @throws ValidationException
	 * @throws ConfigurationException
	 * @throws LoggingException
	 */
	public GetPatientDetailsResponse process(GetPatientDetailsRequest request) 
			throws ValidationException, ConfigurationException, LoggingException {
		return (GetPatientDetailsResponse) processCommon(request);
	}

	@Override
	protected void validate(SMSPRequest smspRequest) throws ValidationException {
		GetPatientDetailsRequest gpdRequest = (GetPatientDetailsRequest) smspRequest;
		if ( (gpdRequest.getNHSNumber()==null) || (gpdRequest.getNHSNumber().isEmpty()) ){
			validateDateOfBirth(gpdRequest.getDateOfBirth(),TRACE); 
			validateSurname(gpdRequest.getSurname(),TRACE);
			validateGivenName(gpdRequest.getGivenName(),TRACE);
			validateGender(gpdRequest.getGender(),TRACE);
			validatePostcode(gpdRequest.getPostcode(),TRACE);
		} else {
			validateNHSNumber(gpdRequest.getNHSNumber(),VERIFY);
			validateDateOfBirth(gpdRequest.getDateOfBirth(),VERIFY); 
			validateSurname(gpdRequest.getSurname(),VERIFY);
			validateGivenName(gpdRequest.getGivenName(),VERIFY);
		}
	}

	@Override
	protected void setPatientIdentities(SMSPRequest smspRequest, ITKMessage itkMessage)
			throws ValidationException {
		GetPatientDetailsRequest gpdRequest = (GetPatientDetailsRequest) smspRequest;
		if ( (gpdRequest.getNHSNumber()==null) || (gpdRequest.getNHSNumber().isEmpty()) ){
			if ((gpdRequest.getLocalPatientIdentifier()==null) || (gpdRequest.getLocalPatientIdentifier().isEmpty()) ){
				// no patient id to add
			} else {	
				itkMessage.getMessageProperties().addPatientIdentity(
						new ITKIdentityImpl(gpdRequest.getLocalPatientIdentifier(),ITKIdentity.LOCAL_PATIENT_TYPE));
			}
		} else {
			itkMessage.getMessageProperties().addPatientIdentity(
					new ITKIdentityImpl(gpdRequest.getNHSNumber(),ITKIdentity.NHS_NUMBER_TYPE));
		}
	}

	@Override
	protected SMSPResponse marshallResponse(ITKMessage response) throws ITKMessagingException {
		return new GetPatientDetailsResponse(response);
	}
	
	@Override
	protected SMSPResponse marshallResponse(String responseCode, String conversationId) {
		return new GetPatientDetailsResponse(responseCode, conversationId);
	}

}
