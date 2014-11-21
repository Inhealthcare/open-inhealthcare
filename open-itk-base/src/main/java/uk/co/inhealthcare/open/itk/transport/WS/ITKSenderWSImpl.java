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

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import uk.co.inhealthcare.open.itk.capabilities.AuditException;
import uk.co.inhealthcare.open.itk.capabilities.AuditService;
import uk.co.inhealthcare.open.itk.capabilities.SOAPAuditDetails;
import uk.co.inhealthcare.open.itk.infrastructure.ITKCommsException;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.infrastructure.ITKUnavailableException;
import uk.co.inhealthcare.open.itk.payload.ITKMessage;
import uk.co.inhealthcare.open.itk.payload.ITKSimpleMessageImpl;
import uk.co.inhealthcare.open.itk.service.ITKSimpleAuditImpl;
import uk.co.inhealthcare.open.itk.service.SOAPAuditDetailsImpl;
import uk.co.inhealthcare.open.itk.transport.ITKSender;
import uk.co.inhealthcare.open.itk.transport.ITKTransportProperties;
import uk.co.inhealthcare.open.itk.transport.ITKTransportPropertiesImpl;
import uk.co.inhealthcare.open.itk.transport.ITKTransportRoute;
import uk.co.inhealthcare.open.itk.transport.ITKTransportSender;
import uk.co.inhealthcare.open.itk.util.xml.DomUtils;
import uk.co.inhealthcare.open.itk.util.xml.XPaths;

/**
 * The Class ITKSenderWSImpl.
 *
 * @author Nick Jones
 */
public class ITKSenderWSImpl implements ITKSender {

	private final static Logger logger = LoggerFactory.getLogger(ITKSenderWSImpl.class);

	private String soapUsername = "";
	
	/**
	 * 
	 * @param soapUsername
	 */
	public void setSoapUsername(String soapUsername) {
		this.soapUsername = soapUsername;
	}

	private String soapFromAddress = "";
	/**
	 * 
	 * @param soapFromAddress
	 */
	public void setSoapFromAddress(String soapFromAddress) {
		this.soapFromAddress = soapFromAddress;
	}

	private String toSoapTransform = "";
	/**
	 * 
	 * @param toSoapTransform
	 */
	public void setToSoapTransform(String toSoapTransform) {
		this.toSoapTransform = toSoapTransform;
	}

	private AuditService auditService = null;
	/**
	 * 
	 * @param auditService
	 */
	public void setAuditService(AuditService auditService) {
		this.auditService = auditService;
	}

	private ITKTransportSender transportService = null;
	/**
	 * 
	 * @param transportService
	 */
	public void setTransportService(ITKTransportSender transportService) {
		this.transportService = transportService;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKSender#sendSync(uk.nhs.interoperability.transport.ITKTransportRoute, uk.nhs.interoperability.payload.ITKMessage)
	 */
	@Override
	public ITKMessage sendSync(ITKTransportRoute destination, ITKMessage request) throws ITKMessagingException {

		if (destination == null){
			ITKMessagingException nullParmsException = new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "Destination is null.");
			logger.error("Destination is null:",nullParmsException);
			throw nullParmsException;
		}
		if (request == null){
			ITKMessagingException nullParmsException = new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "Request is null.");
			logger.error("Request is null:",nullParmsException);
			throw nullParmsException;
		}

		// Add the soap wrappers
		WSSOAPMessageImpl message = new WSSOAPMessageImpl(destination, request, WSSOAPMessageImpl.SYNCREQ);

		configureMessage(message);
		
		// Fallback to default audit service if required
		if (auditService==null){
			auditService = ITKSimpleAuditImpl.getInstance();
		}

		auditSOAPRequest(message);
		
		String responsePayloadString = null;
		ITKTransportProperties responseTransportProperties = null;

		try {

			// Set the transport properties
			Map <String, String> httpProperties = new HashMap<String,String>();
			httpProperties.put("SOAPAction", request.getMessageProperties().getServiceId());
			Document responseDoc = this.transportService.transportSend(message, destination, httpProperties);
			
			if (responseDoc == null) {
				auditSOAPFailure(request.getConversationId(),"202");
				// No responseDoc means the call received a 202 - this is an exception for Synchronous.
				ITKMessagingException unknownResponseException = new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "HTTP Acknowledgement only received (202).");
				logger.error("Unknown response type",unknownResponseException);
				throw unknownResponseException;
				
			} else {
				
				// Pretty print the response message
				logger.trace(DomUtils.serialiseToXML(responseDoc, DomUtils.PRETTY_PRINT));

				// Extract the SOAP Body Content
				Document businessPayloadDocument = DomUtils.createDocumentFromNode((Node)XPaths.SOAP_BODY_CONTENT_XPATH.evaluate(responseDoc, XPathConstants.NODE));

				if (businessPayloadDocument == null){
					// exception
					auditSOAPFailure(request.getConversationId(),"NOSOAPBODY");
					ITKMessagingException unknownResponseException = new ITKMessagingException(
							ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "No payload in SOAP Body");
					logger.error("Unknown response type",unknownResponseException);
					throw unknownResponseException;
				}
				responseTransportProperties = ITKTransportPropertiesImpl.buildFromSoap(responseDoc);

				responsePayloadString = DomUtils.serialiseToXML(businessPayloadDocument);

			}
		} catch (ParserConfigurationException pce) {
			auditSOAPFailure(request.getConversationId(),"FAIL:PARSE");
			logger.error("ParseConfigurationException on WS-CALL", pce);
			throw new ITKCommsException("XML Configuration Error Processing ITK Response");
		} catch (XPathExpressionException xpe) {
			auditSOAPFailure(request.getConversationId(),"FAIL:XPATH");
			logger.error("XPathExpressionException reading payload on WS Response", xpe);
			throw new ITKCommsException("No Payload found in ITK Response");
		} catch (ITKSOAPException spe){
			auditSOAPFailure(request.getConversationId(),"FAIL:SOAP"+spe.getErrorCode());
			throw spe;
		} catch (ITKCommsException ce){
			auditSOAPFailure(request.getConversationId(),"FAIL:COMMS");
			throw ce;
		} catch (ITKUnavailableException ue){
			// ITK Busy Tone - audit and throw
			auditSOAPFailure(request.getConversationId(),"FAIL:BUSY");
			throw ue;
		} catch (Throwable t){
			// cat
			auditSOAPFailure(request.getConversationId(),"FAIL");
			throw t;
		}

		// Construct the Response message
		ITKMessage response = new ITKSimpleMessageImpl(request.getConversationId());
		response.setBusinessPayload(responsePayloadString);
		response.setTransportProperties(responseTransportProperties);
		
		// Audit the constructed response
		auditSOAPResponse(response);
		
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

	private void auditSOAPRequest(WSSOAPMessageImpl message) throws ITKMessagingException {
		
		// Fallback to default audit service if required
		if (auditService==null){
			auditService = ITKSimpleAuditImpl.getInstance();
		}
		SOAPAuditDetails aud = new SOAPAuditDetailsImpl();
		aud.setConversationId(message.getConversationId());
		aud.setMessageId(message.getTransportProperties().getTransportMessageId());
		aud.setCreationTime(message.getCreatedDateString());
		aud.setTo(message.getTransportProperties().getTransportTo());
		aud.setAction(message.getTransportProperties().getTransportAction());
		aud.setUserId(message.getTransportProperties().getTransportUsername());
		
		try {
			auditService.auditSOAPRequest(aud);
		} catch (AuditException e) {
			logger.error("Error auditing request.",e);
			ITKMessagingException unknownResponseException = new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_RETRYABLE_CODE, "Error Auditing SOAP Request.");
			throw unknownResponseException;
		}
		
	}
	
	private void auditSOAPResponse(ITKMessage message) throws ITKMessagingException {
		
		// Fallback to default audit service if required
		if (auditService==null){
			auditService = ITKSimpleAuditImpl.getInstance();
		}
		SOAPAuditDetails aud = new SOAPAuditDetailsImpl();
		aud.setConversationId(message.getConversationId());
		aud.setMessageId(message.getTransportProperties().getTransportMessageId());
		aud.setAction(message.getTransportProperties().getTransportAction());
		aud.setStatus("OK");

		try {
			auditService.auditSOAPResponse(aud);
		} catch (AuditException e) {
			logger.error("Error auditing response.",e);
			ITKMessagingException unknownResponseException = new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_RETRYABLE_CODE, "Error Auditing SOAP Response.");
			throw unknownResponseException;
		}
		
	}
	
	private void auditSOAPFailure(String conversationId, String status) throws ITKMessagingException {
		
		// Fallback to default audit service if required
		if (auditService==null){
			auditService = ITKSimpleAuditImpl.getInstance();
		}
		SOAPAuditDetails aud = new SOAPAuditDetailsImpl();
		aud.setConversationId(conversationId);
		aud.setStatus(status);

		try {
			auditService.auditSOAPResponse(aud);
		} catch (AuditException e) {
			logger.error("Error auditing response.",e);
			ITKMessagingException unknownResponseException = new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_RETRYABLE_CODE, "Error Auditing ITK Request.");
			throw unknownResponseException;
		}
		
	}

	private void configureMessage(WSSOAPMessageImpl message) throws ITKMessagingException{
		message.getTransportProperties().setTransportFrom(soapFromAddress);
		message.getTransportProperties().setTransportUsername(soapUsername);
		message.buildFullMessage(toSoapTransform);	
	}
	
}
