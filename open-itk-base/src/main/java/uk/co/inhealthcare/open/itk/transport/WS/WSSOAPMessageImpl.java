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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.inhealthcare.open.itk.infrastructure.ITKMessageProperties;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.payload.ITKMessage;
import uk.co.inhealthcare.open.itk.payload.ITKSimpleMessageImpl;
import uk.co.inhealthcare.open.itk.transform.TransformManager;
import uk.co.inhealthcare.open.itk.transport.ITKTransportPropertiesImpl;
import uk.co.inhealthcare.open.itk.transport.ITKTransportRoute;

/**
 * The Class WSSOAPMessageImpl. 
 *
 * @author Nick Jones
 */
public class WSSOAPMessageImpl extends ITKSimpleMessageImpl {

	private final static Logger logger = LoggerFactory.getLogger(WSSOAPMessageImpl.class);

	/** The Constant SYNCREQ. */
	public static final String SYNCREQ = "SYNCREQ";
	
	/** The Constant SYNCRESP. */
	public static final String SYNCRESP = "SYNCRESP";
	
	/** The Constant ASYNCREQ. */
	public static final String ASYNCREQ = "ASYNCREQ";
	
	/** The Constant ASYNCRESP. */
	public static final String ASYNCRESP = "ASYNCRESP";
	
	private static final SimpleDateFormat DATE_FORMAT = getDateFormat();
	private Calendar created;
	private Calendar expires;
	private int ttl;
	private String msgType;

	private static SimpleDateFormat getDateFormat(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		// ITK-REQUIREMENT: MSCA-AUD-07
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf;
	}
	/**
	 * Gets the created date
	 *
	 * @return the created
	 */
	public Calendar getCreated() {
		return created;
	}
	
	/**
	 * Gets the created date string.
	 *
	 * @return the created date string
	 */
	public String getCreatedDateString() {
		return DATE_FORMAT.format(this.created.getTime());
	}

	/**
	 * Gets the expiry date string.
	 *
	 * @return the expiry date string
	 */
	public String getExpiryDateString() {
		if (null==expires){
			return "";
		} else {
			return DATE_FORMAT.format(this.expires.getTime());
		}
	}

	/**
	 * Gets the expires date/time
	 *
	 * @return the expires
	 */
	public Calendar getExpires() {
		return expires;
	}

	/**
	 * Gets the msg type.
	 *
	 * @return the msg type
	 */
	public String getMsgType() {
		return msgType;
	}

	/**
	 * Sets the msg type.
	 *
	 * @param msgType the new msg type
	 */
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	
	/**
	 * Gets the time to live.
	 *
	 * @return the time to live
	 */
	public int getTimeToLive() {
		return ttl;
	}

	/**
	 * Sets the time to live.
	 *
	 * @param ttl the new time to live
	 */
	public void setTimeToLive(int ttl) {
		this.ttl = ttl;
		expires = (Calendar)created.clone();
		expires.add(Calendar.SECOND,ttl);
	}
	
	/**
	 * Instantiates a new wSSOAP message impl.
	 *
	 * @param destination the destination
	 * @param itkMessage the itk message
	 * @param messageType the message type
	 * @throws ITKMessagingException the iTK messaging exception
	 */
	public WSSOAPMessageImpl(ITKTransportRoute destination, ITKMessage itkMessage, String messageType) throws ITKMessagingException {
		
		super(itkMessage.getConversationId());
		
		// Validate parms
		if (itkMessage == null) {
			throw new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "Could not add soap wrappers as message was null");
		}
		if (destination == null) {
			throw new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "Could not add soap wrappers as destination was null");
		}
		if (messageType == null) {
			throw new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "Could not add soap wrappers as messageType was null");
		}
		ITKMessageProperties itkMessageProperties = itkMessage.getMessageProperties();
		if (itkMessageProperties == null) {
			throw new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "Could not add soap wrappers as the message did not contain message properties");
		}

		//Initialise all variables
		this.created = Calendar.getInstance();
		this.setMsgType(messageType);
		this.setTimeToLive(destination.getTimeToLive());

		// Copy the payload and message properties from the ITK message
		this.setMessageProperties(itkMessageProperties);
		this.setBusinessPayload(itkMessage.getFullMessage());
		
		// Set the transport properties
		UUID soapMessageId = UUID.randomUUID();
		this.transportProperties = new ITKTransportPropertiesImpl();
		this.transportProperties.setTransportAction(itkMessageProperties.getServiceId());
		this.transportProperties.setTransportMessageId(soapMessageId.toString().toUpperCase());
		this.transportProperties.setTransportTo(destination.getPhysicalAddress());
		this.transportProperties.setTransportReplyTo(destination.getReplyToAddress());
		this.transportProperties.setTransportFaultTo(destination.getExceptionToAddress());
		
	}

	/**
	 * Build Full Message. Creates a canonical object with all the SOAP information in
	 * then transforms to a full soap message
	 *
	 */
	@Override
	public void buildFullMessage(String templateName) throws ITKMessagingException{
		String soapXML = "<SOAPMessage>";
		soapXML += "<MessageType>"+msgType+"</MessageType>";
		soapXML += "<MessageId>"+transportProperties.getTransportMessageId()+"</MessageId>";
		soapXML += "<Action>"+transportProperties.getTransportAction()+"</Action>";
		soapXML += "<From>"+transportProperties.getTransportFrom()+"</From>";
		soapXML += "<To>"+transportProperties.getTransportTo()+"</To>";
		soapXML += "<FaultTo>"+transportProperties.getTransportFaultTo()+"</FaultTo>";
		soapXML += "<ReplyTo>"+transportProperties.getTransportReplyTo()+"</ReplyTo>";
		soapXML += "<Username>"+transportProperties.getTransportUsername()+"</Username>";
		soapXML += "<Created>"+this.getCreatedDateString()+"</Created>";
		soapXML += "<Expires>"+this.getExpiryDateString()+"</Expires>";
		soapXML += "<Payload>" + this.businessPayload + "</Payload>";
		soapXML += "</SOAPMessage>";

		logger.trace("SOAPXML:"+soapXML);
		this.fullMessage = TransformManager.doTransform(templateName, soapXML);
		logger.trace(this.fullMessage);

	}
	
}
