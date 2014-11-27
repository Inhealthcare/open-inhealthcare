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
package uk.co.inhealthcare.open.itk.source;

import java.io.IOException;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import uk.co.inhealthcare.open.itk.capabilities.AuditException;
import uk.co.inhealthcare.open.itk.capabilities.AuditService;
import uk.co.inhealthcare.open.itk.capabilities.DirectoryOfServices;
import uk.co.inhealthcare.open.itk.capabilities.ITKAuditDetails;
import uk.co.inhealthcare.open.itk.infrastructure.ITKAddress;
import uk.co.inhealthcare.open.itk.infrastructure.ITKCommsException;
import uk.co.inhealthcare.open.itk.infrastructure.ITKIdentity;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagePropertiesImpl;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.payload.ITKMessage;
import uk.co.inhealthcare.open.itk.service.ITKAuditDetailsImpl;
import uk.co.inhealthcare.open.itk.service.ITKService;
import uk.co.inhealthcare.open.itk.service.ITKSimpleAuditImpl;
import uk.co.inhealthcare.open.itk.transport.ITKSender;
import uk.co.inhealthcare.open.itk.transport.ITKTransportRoute;
import uk.co.inhealthcare.open.itk.util.ITKLogFormatter;
import uk.co.inhealthcare.open.itk.util.xml.DomUtils;
import uk.co.inhealthcare.open.itk.util.xml.XPaths;

/**
 * The Class ITKMessageSenderImpl.
 *
 * @author Nick Jones
 */
public class ITKMessageSenderImpl implements ITKMessageSender {
	
	private final static Logger logger = LoggerFactory.getLogger(ITKMessageSenderImpl.class);
	private AuditService auditService = null;
	public void setAuditService(AuditService auditService) {
		this.auditService = auditService;
	}

	private DirectoryOfServices directoryOfServices;

	public void setDirectoryOfServices(DirectoryOfServices directoryOfServices) {
		this.directoryOfServices = directoryOfServices;
	}
	
	private ITKSender itkSenderWS = null;
	public void setItkSenderWS(ITKSender itkSenderWS){
		this.itkSenderWS = itkSenderWS;
	}
	private String toItkTransform = null;
	public void setToItkTransform(String toItkTransform) {
		this.toItkTransform = toItkTransform;
	}
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.source.ITKMessageSender#sendSync(uk.nhs.interoperability.payload.ITKMessage)
	 */
	@Override
	public ITKMessage sendSync(ITKMessage request) throws ITKMessagingException {
		
		// validate that the injected services have been configured
		if (itkSenderWS == null){
			String eMsg = "ITK Sender has not been configured";
			ITKMessagingException nullServiceException = new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, eMsg);
			logger.error(ITKLogFormatter.getFormattedLog(eMsg, request),nullServiceException);
			throw nullServiceException;
		}
		
		// Validate that the parms are present
		if (request == null){
			String eMsg = "Request is null";
			ITKMessagingException nullParmsException = new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, eMsg);
			logger.error(ITKLogFormatter.getFormattedLog(eMsg, request),nullParmsException);
			throw nullParmsException;
		}
		if (request.getMessageProperties() == null){
			String eMsg = "Message Properties are null";
			ITKMessagingException nullParmsException = new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, eMsg);
			logger.error(ITKLogFormatter.getFormattedLog(eMsg, request),nullParmsException);
			throw nullParmsException;
		}
		if (request.getMessageProperties().getServiceId() == null){
			String eMsg = "Service Id is null";
			ITKMessagingException nullParmsException = new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, eMsg);
			logger.error(ITKLogFormatter.getFormattedLog(eMsg, request),nullParmsException);
			throw nullParmsException;
		}
		if (request.getMessageProperties().getProfileId() == null){
			String eMsg = "Profile Id is null";
			ITKMessagingException nullParmsException = new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, eMsg);
			logger.error(ITKLogFormatter.getFormattedLog(eMsg, request),nullParmsException);
			throw nullParmsException;
		}
		if (request.getMessageProperties().getFromAddress() == null || 
				request.getMessageProperties().getFromAddress().getURI() == null){
			String eMsg = "From Address is null";
			ITKMessagingException nullParmsException = new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, eMsg);
			logger.error(ITKLogFormatter.getFormattedLog(eMsg, request),nullParmsException);
			throw nullParmsException;
		}

		// Get the ITKService from the DirectoryOfService
		String serviceId = request.getMessageProperties().getServiceId();
		ITKService service = directoryOfServices.getService(serviceId);

		if (!service.supportsSync()){
			String fLog = ITKLogFormatter.getFormattedLog("Invalid service call. Service does not support Synchronous invocation.", request); 
			logger.error(fLog);
			throw new ITKMessagingException("Service " + service.getServiceId() + " can not be called Synchronously");
		}

		ITKTransportRoute route = getRoute(request);

		ITKSender sender = getSender(route, request);

		ITKMessage message = buildMessage(request, route, service);

		auditRequest(message);
		
		logger.trace("Sending via configured transport: " + message.getFullMessage());
		
		ITKMessage response = null;
		try {
			response = sender.sendSync(route, message);
		} catch (ITKMessagingException itkex){
			// Ensure lower level exceptions are logged with key ITK attributes, then re-throw
			logger.error(ITKLogFormatter.getFormattedLog(itkex.getMessage(), request));
			throw itkex;
		}
		
		buildResponse(response,request);
		if (!directoryOfServices.isServiceProfileSupported(response.getMessageProperties().getProfileId())){
			String fLog = ITKLogFormatter.getFormattedLog("Invalid response profile received. Request:", request); 
			logger.error(fLog);
			fLog = ITKLogFormatter.getFormattedLog("Invalid response profile received. Response:"+response.getMessageProperties().getProfileId(), response); 
			logger.error(fLog);
			throw new ITKMessagingException("ProfileId not supported");
		}
		auditResponse(response);
		
		return response;

	}
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.source.ITKMessageSender#send(uk.nhs.interoperability.payload.ITKMessage)
	 * Not implemented in this version (not required for SMSP)
	 */
	@Override
	public void send(ITKMessage request) throws ITKMessagingException {

		ITKMessagingException notImplementedException = 
				new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "Send Not Yet Implemented");
		throw notImplementedException;
		
	}
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.source.ITKMessageSender#sendAsync(uk.nhs.interoperability.payload.ITKMessage)
	 * Not implemented in this version (not required for SMSP)
	 */
	@Override
	public void sendAsync(ITKMessage request) throws ITKMessagingException {

		ITKMessagingException notImplementedException = 
				new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "Async Not Yet Implemented");
		throw notImplementedException;
		
	}

	/**
	 * Gets the route.
	 *
	 * @param request the request
	 * @return the route
	 * @throws ITKMessagingException the iTK messaging exception
	 */
	private ITKTransportRoute getRoute(ITKMessage message) throws ITKMessagingException {

		ITKTransportRoute route ;
		String serviceId = message.getMessageProperties().getServiceId();
		ITKAddress toAddress = message.getMessageProperties().getToAddress();
		
		// Resolve Destination Service in terms of a TransportRoute
		route = directoryOfServices.resolveDestination(serviceId, toAddress);

		logger.debug("Found transport route:" + route);

		return route;
	}
	
	/**
	 * Builds the message. Adds in service properties then generates the Full ITK Message
	 *
	 * @param request the request
	 * @param route the route
	 * @param service the service
	 * @return the iTK message
	 * @throws ITKMessagingException 
	 */
	private ITKMessage buildMessage(ITKMessage request, ITKTransportRoute route, ITKService service) throws ITKMessagingException{

		logger.trace("adding service attributes");
		
		if (service.isBase64()) {
			String b64DocText = DatatypeConverter.printBase64Binary(request.getBusinessPayload().getBytes());
			request.setBusinessPayload(b64DocText);
		}
		request.setBase64(service.isBase64());
		request.setMimeType(service.getMimeType());
		request.buildFullMessage(toItkTransform);
		return request;
	}
	
	/**
	 * Gets the sender.
	 *
	 * @param route the route
	 * @return the sender
	 * @throws ITKMessagingException the iTK messaging exception
	 */
	private ITKSender getSender(ITKTransportRoute route, ITKMessage request) throws ITKMessagingException {
		
		ITKSender sender = null;
		// Resolve the sender implementation according to the transport type
		if (route.getTransportType().equals(ITKTransportRoute.HTTP_WS)){
			sender = itkSenderWS;
		}

		if (sender == null){
			logger.error(ITKLogFormatter.getFormattedLog("No Transport Sender Found", request));
			throw new ITKMessagingException("No transport implementation found for configured route");
		}

		return sender;
	}


	/**
	 * Builds the response.
	 *
	 * @param response the response
	 * @throws ITKMessagingException the iTK messaging exception
	 */
	private void buildResponse(ITKMessage response, ITKMessage request) throws ITKMessagingException {
		
		try {
			
			Document responseDoc = DomUtils.parse(response.getBusinessPayload());
			String responsePayloadName = responseDoc.getDocumentElement().getLocalName();
			logger.trace("Received:"+responsePayloadName);
			
			if (responsePayloadName.equalsIgnoreCase("DistributionEnvelope")){
				logger.trace("Processing DistributionEnvelope");
	
				//Extract message properties from the response
				response.setMessageProperties(ITKMessagePropertiesImpl.build(responseDoc));
	
				// Check plain payload
				String mimetype = (String)XPaths.ITK_FIRST_MIMETYPE_XPATH.evaluate(responseDoc);
				
				if (mimetype.equalsIgnoreCase("text/plain")){
					// Get the payload as a text node
					String payload = (String)XPaths.ITK_FIRST_PAYLOAD_TEXT_XPATH.evaluate(responseDoc);
					response.setBusinessPayload(payload);
					
					// Check base64 encoding
					String base64 = (String)XPaths.ITK_FIRST_BASE64_XPATH.evaluate(responseDoc);
					if (base64.equalsIgnoreCase("true")){
						byte[] payloadBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(payload);
						response.setBusinessPayload(new String(payloadBytes));
					}
					
				} else {
					// Get the first payload from the distribution envelope
					Document businessPayloadDocument = DomUtils.createDocumentFromNode((Node)XPaths.ITK_FIRST_PAYLOAD_XPATH.evaluate(responseDoc, XPathConstants.NODE));
					
					if (businessPayloadDocument == null){
						// exception
						ITKMessagingException unknownResponseException = new ITKMessagingException(
								ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "No payload in Distribution Envelope");
						logger.error(ITKLogFormatter.getFormattedLog("Unknown response type. First payload in DE is null.", request),unknownResponseException);
						throw unknownResponseException;
					}
					
					//Show the extracted payload in the trace log
					String businessPayloadName = businessPayloadDocument.getDocumentElement().getLocalName();
					logger.trace("Business Payload Name is:"+businessPayloadName);
					response.setBusinessPayload(DomUtils.serialiseToXML(businessPayloadDocument));
				}
				
		
			} else {
				ITKMessagingException unknownResponseException = new ITKMessagingException(
						ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "Neither DistributionEnvelope nor SimpleMessageResponse Found");
				logger.error(ITKLogFormatter.getFormattedLog("Unknown response type - No Distribution Envelope.", request),unknownResponseException);
				throw unknownResponseException;
				
			}
			
		} catch (SAXException se) {
			logger.error(ITKLogFormatter.getFormattedLog("SAXException processing response from Transport", request),se);
			throw new ITKCommsException("XML Error Processing ITK Response");
		} catch (IOException ioe) {
			logger.error(ITKLogFormatter.getFormattedLog("IOException on Transport", request),ioe);
			throw new ITKCommsException("Transport error sending ITK Message");
		} catch (ParserConfigurationException pce) {
			logger.error(ITKLogFormatter.getFormattedLog("ParseConfigurationException on Transport", request),pce);
			throw new ITKCommsException("XML Configuration Error Processing ITK Response");
		} catch (XPathExpressionException xpe) {
			logger.error(ITKLogFormatter.getFormattedLog("XPathExpressionException reading payload on Transport Response", request),xpe);
			throw new ITKCommsException("No Payload found in ITK Response");
		}
		
		return ;
	}
	
	private void auditRequest(ITKMessage message) throws ITKMessagingException {
		
		// Fallback to default audit service if required
		if (auditService==null){
			auditService = ITKSimpleAuditImpl.getInstance();
		}
		ITKAuditDetails aud = new ITKAuditDetailsImpl();
		aud.setConversationId(message.getConversationId());
		aud.setTrackingId(message.getMessageProperties().getTrackingId());
		aud.setPayloadId(message.getMessageProperties().getItkPayloadId()); 
		
		// Patient IDs 
		aud.setLocalPatientId(message.getMessageProperties().getPatientIdentityByType(ITKIdentity.LOCAL_PATIENT_TYPE));
		aud.setNhsNumber(message.getMessageProperties().getPatientIdentityByType(ITKIdentity.NHS_NUMBER_TYPE));
		
		// Get the Audit Identities by type
		aud.setLocalAuditId(message.getMessageProperties().getAuditIdentityByType(ITKIdentity.DEFAULT_IDENTITY_TYPE));
		aud.setSpineUserId(message.getMessageProperties().getAuditIdentityByType(ITKIdentity.SPINE_UUID_TYPE));
		aud.setSpineRoleProfileId(message.getMessageProperties().getAuditIdentityByType(ITKIdentity.SPINE_ROLE_PROFILE_TYPE));
		aud.setSpineRoleId(message.getMessageProperties().getAuditIdentityByType(ITKIdentity.SPINE_ROLE_TYPE));
		
		aud.setSenderAddress(message.getMessageProperties().getFromAddress().getURI());
		aud.setServiceId(message.getMessageProperties().getServiceId());
		aud.setProfileId(message.getMessageProperties().getProfileId());

		try {
			auditService.auditITKRequest(aud);
		} catch (AuditException e) {
			logger.error(ITKLogFormatter.getFormattedLog("Error auditing request.", message),e);
			ITKMessagingException unknownResponseException = new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_RETRYABLE_CODE, "Error Auditing ITK Request.");
			throw unknownResponseException;
		}
		
	}
	
	private void auditResponse(ITKMessage message) throws ITKMessagingException {
		
		// Fallback to default audit service if required
		if (auditService==null){
			auditService = ITKSimpleAuditImpl.getInstance();
		}
		ITKAuditDetails aud = new ITKAuditDetailsImpl();
		aud.setConversationId(message.getConversationId());
		aud.setTrackingId(message.getMessageProperties().getTrackingId());
		aud.setPayloadId(message.getMessageProperties().getItkPayloadId()); 
		
		// Patient IDs 
		aud.setLocalPatientId(message.getMessageProperties().getPatientIdentityByType(ITKIdentity.LOCAL_PATIENT_TYPE));
		aud.setNhsNumber(message.getMessageProperties().getPatientIdentityByType(ITKIdentity.NHS_NUMBER_TYPE));
		
		// Get the Audit Identities by type
		aud.setLocalAuditId(message.getMessageProperties().getAuditIdentityByType(ITKIdentity.DEFAULT_IDENTITY_TYPE));
		aud.setSpineUserId(message.getMessageProperties().getAuditIdentityByType(ITKIdentity.SPINE_UUID_TYPE));
		aud.setSpineRoleProfileId(message.getMessageProperties().getAuditIdentityByType(ITKIdentity.SPINE_ROLE_PROFILE_TYPE));
		aud.setSpineRoleId(message.getMessageProperties().getAuditIdentityByType(ITKIdentity.SPINE_ROLE_TYPE));
		
		aud.setSenderAddress(message.getMessageProperties().getFromAddress().getURI());
		aud.setServiceId(message.getMessageProperties().getServiceId());
		aud.setProfileId(message.getMessageProperties().getProfileId());

		try {
			auditService.auditITKResponse(aud);
		} catch (AuditException e) {
			logger.error(ITKLogFormatter.getFormattedLog("Error auditing response.", message),e);
			ITKMessagingException unknownResponseException = new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_RETRYABLE_CODE, "Error Auditing ITK Response.");
			throw unknownResponseException;
		}
		
	}

}
