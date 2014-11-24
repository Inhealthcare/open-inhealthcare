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

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.inhealthcare.open.jsat.AlertException;
import uk.co.inhealthcare.open.jsat.ConfigurationException;
import uk.co.inhealthcare.open.jsat.GeneralException;
import uk.co.inhealthcare.open.jsat.JSATComponent;
import uk.co.inhealthcare.open.jsat.TransformationException;
import uk.co.inhealthcare.open.jsat.logging.ProcessLoggingService;
import uk.co.inhealthcare.open.jsat.services.EmailSender;
import uk.co.inhealthcare.open.jsat.services.HL7Sender;
import uk.co.inhealthcare.open.smsc.messages.logging.LoggingException;
import uk.co.inhealthcare.open.smsc.messages.logging.SMSCLoggingService;
import uk.co.inhealthcare.open.smsc.transformations.api.A05ToDemographicUpdate;
import uk.co.inhealthcare.open.smsc.transformations.api.ToPASUpdate;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v24.message.ADT_A05;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.parser.EncodingCharacters;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * Abstract class for all SMSC processors
 * This class provides the main process method which receives HAPI HL7 messages from the message handling container, extracts 
 * key message parameters and passes control to the concrete implementation.
 * 
 * This class also provides the standard service setters for the common services:
 * - SMSC Logger
 * - Inbound Transformer
 * - Outbound Transformer
 * - HL7 Sender
 * - Technical Alerter
 * - Business Alerter
 *  
 * @author Nick Jones
 *
 */

public abstract class SMSCProcess extends JSATComponent {

	private final static Logger logger = LoggerFactory.getLogger(SMSCProcess.class);
	public final static String SMSCLOGGER_NOT_CONFIGURED = "SMSC Logger not configured";
	public final static String TECHALERT_NOT_CONFIGURED = "Technical Alerter not configured";
	public final static String BUSALERT_NOT_CONFIGURED = "Business Alerter not configured";
	public final static String TRANSFORMERIN_NOT_CONFIGURED = "Transformer In not configured";
	public final static String TRANSFORMEROUT_NOT_CONFIGURED = "Transformer Out not configured";
	public final static String HL7SENDER_NOT_CONFIGURED = "HL7 Sender not configured";
	public final static String LOCALAUDITID_NOT_CONFIGURED = "Local Audit Id not configured";
	
	public final static String ERR_REQUEST_NOT_PROVIDED = "Request not provided";
	public final static String ERR_UNABLE_TO_WRITE_SMSCLOG = "Failure writing to the SMSC Logger";
	public final static String ERR_UNABLE_TO_SEND_TECHALERT = "Failure sending a technical alert";
	
	public final static String NO_SMSP_LOOKUP_REQUIRED = "No SMSP Lookup Required";
	
	protected A05ToDemographicUpdate transformIn;
	protected ToPASUpdate transformOut;
	protected String localAuditId;
	protected HL7Sender hl7Sender; 
	protected EmailSender technicalAlerter;
	protected EmailSender businessAlerter;
	protected SMSCLoggingService smscLogger; 
	protected ProcessLoggingService processLoggingService;

	public void setProcessLoggingService(
			ProcessLoggingService processLoggingService) {
		this.processLoggingService = processLoggingService;
	}
	
	public void setSmscLogger(SMSCLoggingService smscLogger) {
		this.smscLogger = smscLogger;
	}

	public void setTransformIn(A05ToDemographicUpdate transformIn) {
		logger.debug("Setting TransformIn to:"+transformIn);
		this.transformIn = transformIn;
	}
	public void setTransformOut(ToPASUpdate transformOut) {
		logger.debug("Setting TransformOut to:"+transformOut);
		this.transformOut = transformOut;
	}
	public void setLocalAuditId(String localAuditId) {
		this.localAuditId = localAuditId;
	}
	public void setHl7Sender(HL7Sender hl7Sender) {
		this.hl7Sender = hl7Sender;
	}
	public void setTechnicalAlerter(EmailSender emailSender) {
		this.technicalAlerter = emailSender;
	}
	public void setBusinessAlerter(EmailSender emailSender) {
		this.businessAlerter = emailSender;
	}

	/**
	 * Process the inbound HL7 message as follows:
	 * - Generate the JSAT conversation id
	 * - Validate the component is fully configured and that a message has been passed
	 * - Extract key message attributes into a properties object
	 * - Transform the inbound message into a business object
	 * - Log the inbound message
	 * - Call the concrete implementation
	 * - Log the outcome
	 * - Manage exceptions 
	 * 
	 * @param hl7Msg
	 */
	public void process(ADT_A05 hl7Msg) {

		DemographicUpdate update = null;
		MessageProperties props = null; 
		String conversationId;
		
		try {

			conversationId = UUID.randomUUID().toString().toUpperCase();
			
			validate(hl7Msg, conversationId);

			props = getMessageProperties(hl7Msg, conversationId);
			
			// Get Canonical message from HL7 message
			// NOTE: May need to be more generic as more processors are added but keep simple for now
			try {
				update = transformIn.getDemographicUpdate(hl7Msg);
			} catch (TransformationException e) {
				logAndAlertFailure(conversationId, "Inbound Transformation Error", e.getMessage(), "","");
				return;
			} 
			
			try {
				processLoggingService.logSMSCProcessInput(conversationId,
						update);
			} catch (LoggingException e) {
				logAndAlertFailure(conversationId, "Error logging inbound message", e.getMessage(), "","");
				return;
			}
			
		} catch (GeneralException genEx) {
			logger.error("SMSC Process ending due to error on inbound:"+genEx.getMessage());
			return;
		}
		
		try{
			mainProcess(update,props);
		} catch (GeneralException genEx){
			logger.error("SMSC Process ending due to error processing message:"+genEx.getMessage());
		}
	}
	private void mainProcess(DemographicUpdate update, MessageProperties props) throws GeneralException {
		
		String outcome;
		try {
			outcome = processDetail(update, props);
			processLoggingService.logSMSCProcessOutcome(
					props.getConversationId(), outcome);
		} catch (HL7Exception e) {
			logAndAlertFailure(props.getConversationId(), "HL7 Error sending PAS update:", e.getMessage(), props.getEncodedMSH(),update.toXml());
			throw new GeneralException(e.getMessage());
		} catch (ConfigurationException e) {
			logAndAlertFailure(props.getConversationId(), "Configuration error:", e.getMessage(), props.getEncodedMSH(),update.toXml());
			throw new GeneralException(e.getMessage());
		} catch (LoggingException e) {
			alertFailure(props.getConversationId(), "SMSC Logging Error:", e.getMessage(), props.getEncodedMSH(),update.toXml());
			throw new GeneralException(e.getMessage());
		} catch (TransformationException e) {
			logAndAlertFailure(props.getConversationId(), "Error creating PAS update:", e.getMessage(), props.getEncodedMSH(),update.toXml());
			throw new GeneralException(e.getMessage());
		} catch (AlertException e) {
			logAndAlertFailure(props.getConversationId(), "Error Sending Alert:", e.getMessage(), props.getEncodedMSH(),update.toXml());
			throw new GeneralException(e.getMessage());
		}
		
	}

	private void validate(ADT_A05 hl7Msg, String conversationId) throws GeneralException {
		
		try {
			checkSMSCConfiguration();

			// validate that the request is present
			if (hl7Msg == null){
				logAndAlertFailure(conversationId, ERR_REQUEST_NOT_PROVIDED, ERR_REQUEST_NOT_PROVIDED, "","");
				throw new GeneralException(ERR_REQUEST_NOT_PROVIDED);
			}
			
		} catch (ConfigurationException confEx) {
			logAndAlertFailure(conversationId, "Configuration Error", confEx.getMessage(), "","");
			throw new GeneralException("CONFIGURATION ERROR");
		}
		
	}
	
	private MessageProperties getMessageProperties(ADT_A05 hl7Msg, String conversationId) throws GeneralException {

		String encodedMSH = "";
		MessageProperties props = new MessageProperties();

		// Get common Message Properties
		MSH msh;
		try {
			msh = (MSH) hl7Msg.get("MSH");
			EncodingCharacters enc = new EncodingCharacters(hl7Msg.getFieldSeparatorValue(), hl7Msg.getEncodingCharactersValue());
			encodedMSH = PipeParser.encode(msh, enc);
	
			props.setConversationId(conversationId);
	
			props.setMessageId(msh.getMessageControlID().getValue());
			props.setEncodedMSH(encodedMSH);
			props.setTriggerEvent(msh.getMsh9_MessageType().getTriggerEvent().getValue());
			props.setInboundMessage(hl7Msg);
			logger.debug("Received:"+props.getMessageId()+":triggerEvent:"+props.getTriggerEvent());
			
		} catch (HL7Exception e) {
			logger.error("HL7 Exception parsing HL7 inbound message.",e);
			throw new GeneralException("HL7INEXCEPTION");
		}
		
		return props;
		
	}

	private void logAndAlertFailure(String conversationId, String headline, String message, 
			String technicalContext, String businessContext) throws GeneralException {
		
		logFailure(conversationId, message);
		alertFailure(conversationId, headline, message, technicalContext, businessContext);
		
	}
	
	private void alertFailure(String conversationId, String headline, String message, 
			String technicalContext, String businessContext) throws GeneralException {
		
		// if possible, send an alert
		if (technicalAlerter != null) {
			// There is no business or technical context for the message at this point
			try {
				technicalAlerter.send(conversationId,headline,message,technicalContext,
						this.getClass().getSimpleName(),businessContext);
			} catch (AlertException alertEx) {
				logger.error(ERR_UNABLE_TO_WRITE_SMSCLOG);
				throw new GeneralException(ERR_UNABLE_TO_WRITE_SMSCLOG);
			}
		} else {
			logger.error("Unable to send technical alert - not configured");
		}
		
	}
	private void logFailure(String conversationId, String message) throws GeneralException {
		
		if (smscLogger != null) {
			try {
				processLoggingService.logSMSCProcessOutcome(conversationId,
						message);
			} catch (LoggingException logEx) {
				logger.error(ERR_UNABLE_TO_WRITE_SMSCLOG);
				throw new GeneralException(ERR_UNABLE_TO_WRITE_SMSCLOG);
			}
		} else {
			logger.error("Unable to write SMSC Log - not configured");
		}
		
	}
	
	protected void checkSMSCConfiguration() throws ConfigurationException {
		if (smscLogger == null) raiseConfigurationException(SMSCLOGGER_NOT_CONFIGURED);
		if (technicalAlerter == null) raiseConfigurationException(TECHALERT_NOT_CONFIGURED);
		if (localAuditId == null) raiseConfigurationException(LOCALAUDITID_NOT_CONFIGURED);
		if (transformIn == null) raiseConfigurationException(TRANSFORMERIN_NOT_CONFIGURED);

		// Some properties may be optional - depends on the processor
		checkProcessConfiguration();
	}
	
	protected void raiseConfigurationException(String message) throws ConfigurationException {
		logger.error(message);
		throw new ConfigurationException(message);
	}

	protected abstract String processDetail(DemographicUpdate update, MessageProperties props) 
			throws HL7Exception, ConfigurationException, LoggingException, GeneralException, TransformationException, AlertException ;
	
	protected abstract void checkProcessConfiguration() throws ConfigurationException ;
	
}
