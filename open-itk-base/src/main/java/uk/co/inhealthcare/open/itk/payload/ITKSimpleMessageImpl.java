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

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.inhealthcare.open.itk.infrastructure.ITKMessageProperties;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.transform.TransformManager;
import uk.co.inhealthcare.open.itk.transport.ITKTransportProperties;

/**
 * The Class SimpleMessage.
 *
 * @author Nick Jones
 */
public class ITKSimpleMessageImpl implements ITKMessage {

	private final static String DEFAULT_TEMPLATE = "ToDistributionEnvelope.xslt";
	
	private final static Logger logger = LoggerFactory.getLogger(ITKSimpleMessageImpl.class);

	protected String conversationId;
	protected ITKMessageProperties messageProperties;
	protected ITKTransportProperties transportProperties;
	protected String businessPayload;
	/** The Full ITK wrapped message. */
	protected String fullMessage;
	protected boolean isResponse;
	private boolean isBase64;
	private String mimeType;

	/**
	 * Instantiates a new simple message.
	 */
	public ITKSimpleMessageImpl() {
		this.conversationId = UUID.randomUUID().toString().toUpperCase();
		logger.trace("New ITKMessage created. ConversationId generated:"+this.conversationId);
	}
	public ITKSimpleMessageImpl(String conversationId) {
		this.conversationId = conversationId;
		logger.trace("New ITKMessage created with conversationId provided:"+this.conversationId);
	}

	public void buildFullMessage(String templateName) throws ITKMessagingException {
		
		if ((this.messageProperties == null)) {
			String eMsg = "Message Properties not set";
			logger.error(eMsg);
			throw new ITKMessagingException(null, ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, eMsg);
		}
		
		if ((this.messageProperties.getServiceId() == null)) {
			String eMsg =  "Service Id not set";
			logger.error(eMsg);
			throw new ITKMessagingException(this.messageProperties, ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, eMsg);
		}
		
		if ((templateName == null)||(templateName.isEmpty())) {
			logger.warn("No template name provided - using default");
			templateName = DEFAULT_TEMPLATE;
		}
		
		String deXML = "<ITKMessage>";
		deXML += "<Service>"+this.messageProperties.getServiceId()+"</Service>";
		deXML += "<TrackingId>" + this.messageProperties.getTrackingId() + "</TrackingId>";
		
		// From Address is optional
		if (this.messageProperties.getFromAddress()!=null){
			deXML += "<Sender>"+this.messageProperties.getFromAddress().getURI()+"</Sender>";
			deXML += "<SenderType>"+this.messageProperties.getFromAddress().getType()+"</SenderType>";
		}

		// To Address is optional
		// CONSTRAINT: this implementation only allows one "To" address
		if (this.messageProperties.getToAddress()!=null){
			deXML += "<Recipient>"+this.messageProperties.getToAddress().getURI()+"</Recipient>";
			deXML += "<RecipientType>"+this.messageProperties.getToAddress().getType()+"</RecipientType>";
		}
		
		deXML += "<Authors>";
		for (int i=0;i<this.messageProperties.getAuditIdentities().size();i++){
			deXML += "<Author>";
				deXML += "<URI>"+this.messageProperties.getAuditIdentities().get(i).getURI()+"</URI>";
				deXML += "<Type>"+this.messageProperties.getAuditIdentities().get(i).getType()+"</Type>";
			deXML += "</Author>";
		}
		deXML += "</Authors>";
		deXML += "<Manifest id=\""+this.messageProperties.getItkPayloadId()+
					"\" type=\""+this.getMimeType()+"\""+
				    " profileid=\""+this.messageProperties.getProfileId()+"\" ";
		if (this.isBase64()){
			deXML += " base64=\"true\" ";
		}
		deXML += " />";
		//Add handling specifications if set
		if (!this.getMessageProperties().getHandlingSpecifications().isEmpty()) {
			deXML += "<HandlingSpecs>";
			for (Map.Entry<String, String> entry: this.getMessageProperties().getHandlingSpecifications().entrySet()) {
				//Add handling specifications
				deXML += "<Spec key=\"" + entry.getKey() + "\" value=\"" + entry.getValue() + "\"/>";
			}
			deXML += "</HandlingSpecs>";
		}
		deXML += "<Payload id=\""+this.messageProperties.getItkPayloadId()+
				"\">"+this.getBusinessPayload()+"</Payload>";
		deXML += "</ITKMessage>";
		logger.trace("DEXML:"+deXML);
		this.fullMessage = TransformManager.doTransform(templateName, deXML);
		
	}
	public String getConversationId() {
		return conversationId;
	}
	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public String getFullMessage() {
		return fullMessage;
	}

	public boolean isBase64() {
		return isBase64;
	}

	public void setBase64(boolean isBase64) {
		this.isBase64 = isBase64;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.payload.ITKMessage#getBusinessPayload()
	 */
	public String getBusinessPayload() {
		return businessPayload;
	}
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.payload.ITKMessage#setBusinessPayload(java.lang.String)
	 */
	public void setBusinessPayload(String businessPayload) {
		this.businessPayload = businessPayload;
	}
	

	public ITKTransportProperties getTransportProperties() {
		return transportProperties;
	}
	public void setTransportProperties(ITKTransportProperties transportProperties) {
		this.transportProperties = transportProperties;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.payload.ITKMessage#getMessageProperties()
	 */
	public ITKMessageProperties getMessageProperties() {
		return messageProperties;
	}
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.payload.ITKMessage#setMessageProperties(uk.nhs.interoperability.infrastructure.ITKMessageProperties)
	 */
	public void setMessageProperties(ITKMessageProperties messageProperties) {
		this.messageProperties = messageProperties;
	}
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.payload.ITKMessage#isResponse()
	 */
	@Override
	public boolean isResponse() {
		return this.isResponse;
	}
	
	/**
	 * Sets the checks if is reponse.
	 *
	 * @param isReponse the new checks if is reponse
	 */
	public void setIsReponse(boolean isReponse) {
		this.isResponse = isReponse;
	}


}
