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

package uk.co.inhealthcare.open.smsc.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import uk.co.inhealthcare.open.jsat.AlertException;
import uk.co.inhealthcare.open.jsat.ConfigurationException;
import uk.co.inhealthcare.open.jsat.GeneralException;
import uk.co.inhealthcare.open.jsat.TransformationException;
import uk.co.inhealthcare.open.jsat.ValidationException;
import uk.co.inhealthcare.open.jsat.hl7.HL7MessageHandler;
import uk.co.inhealthcare.open.smsc.messages.GetPatientDetailsRequest;
import uk.co.inhealthcare.open.smsc.messages.SMSPResponse;
import uk.co.inhealthcare.open.smsc.messages.logging.LoggingException;
import uk.co.inhealthcare.open.smsc.operation.GetPatientDetailsOperation;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;

/**
 * Process class to examine the in-bound new registration message and use configuration to
 * decide if an SMSP call is required. If so then this process knows which SMSP function
 * to use acts upon the response.
 *
 * NOTE: Much of this is common with DemographicUpdateProcess and could therefore be re-factored. 
 * 		 Re-factoring has been left until more processes are built to discover what is truly generic.
 * 
 * @author Nick Jones
 *
 */
@HL7MessageHandler(triggerEvent = "A28")
@Component
public class NewRegistrationProcessUnified extends SMSCProcess {

	private final static Logger logger = LoggerFactory.getLogger(NewRegistrationProcessUnified.class);
	
	// INJECTED DEPENDENCIES
	private GetPatientDetailsOperation getPatientDetailsOperation;
	//
	
	public NewRegistrationProcessUnified(){
		logger.debug("Creating NewRegistration Processor.");
	}
	
	protected String processDetail(DemographicUpdate update, MessageProperties props) 
			throws HL7Exception, ConfigurationException, LoggingException, GeneralException, TransformationException, AlertException {

		String outcome = "";
		
		// Get process control configuration
		boolean alertOnValidationError = this.getLookupBoolean(SMSP_ALERT_ON_VALIDATION_ERROR, props.getTriggerEvent() ,"Y");
		
		// Is any process required
		boolean smspRequired = this.getLookupBoolean(SMSP_REQUIRED_FOR_NHS_STATUS, update.getNHSNumberStatus().toUpperCase(),"N");
		if (smspRequired){
			// Update is required
			logger.debug("SMSP lookup is required."+update.getNHSNumberStatus().toUpperCase());
		} else {
			// No Update is required
			logger.info("No SMSP lookup required. NHS Number status is :"+update.getNHSNumberStatus());
			return NO_SMSP_LOOKUP_REQUIRED;
		}

		// 2 Call the SMSP service
		SMSPResponse smspResponse = null;
		try {
			// No NHS Number provided - perform Get Patient Details By Search
			logger.debug("Performing Get Patient Details By Search");
			GetPatientDetailsRequest getPatientDetailsReq = update
					.createGetPatientDetailsRequest(props.getConversationId());
			getPatientDetailsReq.setLocalAuditId(this.localAuditId);
			smspResponse = getPatientDetailsOperation.process(getPatientDetailsReq);
		} catch (ValidationException valex) {
			// If validation fails, Log the error, send the alert and return
			logger.info("Invalid Request:[ConvId]"+props.getConversationId()+"[Message]"+props.getEncodedMSH()+"[Error]"+valex.getMessage());

			// Subject to configuration, raise an alert on this validation error
			if (alertOnValidationError){
				technicalAlerter.send(props.getConversationId(), "SMSC Validation Error:",valex.getMessage(),props.getEncodedMSH(),
						this.getClass().getSimpleName(),update.toXml());
			}

			return valex.getMessage();
		}
		
		logger.debug("Response Code:"+smspResponse.getResponseCode());
		
		// 3 - Check Business Alert Config
		boolean businessAlertRequired = this.getLookupBoolean(SMSP_RESPONSE_CODE_BUS_ALERT+"."+props.getTriggerEvent(),smspResponse.getResponseCode(),"N");
		logger.trace("Is Business Alert required for response code/trigger event:"+businessAlertRequired);
		if (!businessAlertRequired){
			businessAlertRequired = this.getLookupBoolean(SMSP_RESPONSE_CODE_BUS_ALERT,smspResponse.getResponseCode(),"N");
			logger.trace("Is business Alert is required for response code:"+smspResponse.getResponseCode()+":"+businessAlertRequired);
		}
		
		// 4 - Raise Business Alert
		if (businessAlertRequired){
			String alertMessage = this.getLookup(SMSP_RESPONSE_CODE_DESC, smspResponse.getResponseCode(),smspResponse.getResponseCode()+"[Description Not Found]");
			businessAlerter.send(props.getConversationId(), "SMSP Alert:",alertMessage,props.getEncodedMSH(),
					this.getClass().getSimpleName(),update.toXml());
			logger.debug("Sent business alert message:"+alertMessage);
		}
		
		// 5 - Check Technical Alert Config
		boolean technicalAlertRequired = this.getLookupBoolean(SMSP_RESPONSE_CODE_TECH_ALERT, smspResponse.getResponseCode(),"N");
		logger.trace("Is Technical Alert is required:"+technicalAlertRequired);

		// 6 - Raise Technical Alert
		if (technicalAlertRequired){
			String alertMessage = this.getLookup(SMSP_RESPONSE_CODE_DESC, smspResponse.getResponseCode(),smspResponse.getResponseCode()+"[Description Not Found]");
			technicalAlerter.send(props.getConversationId(), "SMSP Response Code triggered an alert:",alertMessage,props.getEncodedMSH(),
					this.getClass().getSimpleName(),update.toXml());
			logger.debug("Sent technical alert message:"+alertMessage);
		}
		
		// 7 - Update PAS
		// Create an A31 (based on the incoming message) and forward to a new channel
		String updatedNSTSCode = this.getLookup(SMSP_RESPONSE_TO_NSTS, smspResponse.getResponseCode());
		if (updatedNSTSCode!=null){
			logger.trace("Sending update to PAS:"+updatedNSTSCode);

			String updatedNhsNumber = smspResponse.getNhsNumber();
			
			boolean retainNhsNumber = this.getLookupBoolean(SMSP_RETAIN_NHS_NUMBER, updatedNSTSCode, "N");
			if (retainNhsNumber){
				logger.trace("NHS Number retained due to configuration.");
				updatedNhsNumber = update.getNHSNumber();
			}
			
			// Get PAS Update Message
			Message pasMsg = transformOut.getPASUpdateMessage(props.getInboundMessage(), updatedNhsNumber, updatedNSTSCode);
			hl7Sender.process(pasMsg);
			outcome = "[NewNHSStatusCode]"+updatedNSTSCode+"[NHSNumber]"+updatedNhsNumber;
			
		} else {
			outcome = "PAS Update not required for :"+smspResponse.getResponseCode();
		}

		logger.trace(outcome);
		
		return outcome;
		
	}

	protected void checkProcessConfiguration() throws ConfigurationException {
		if (transformIn == null) raiseConfigurationException(TRANSFORMERIN_NOT_CONFIGURED);
		if (transformOut == null) raiseConfigurationException(TRANSFORMEROUT_NOT_CONFIGURED);
		if (hl7Sender == null) raiseConfigurationException(HL7SENDER_NOT_CONFIGURED);
		if (businessAlerter == null) raiseConfigurationException(BUSALERT_NOT_CONFIGURED);
	}

	public void setGetPatientDetailsOperation(GetPatientDetailsOperation getPatientDetailsOperation) {
		logger.debug("Setting GetPatientDetailsByNHSNumberOperation to:"+getPatientDetailsOperation);
		this.getPatientDetailsOperation = getPatientDetailsOperation;
	}
}
