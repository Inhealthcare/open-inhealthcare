package uk.co.inhealthcare.open.itk.transport;

import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.util.xml.XPaths;

/**
 * The Class ITKTransportPropertiesImpl.
 *
 * @author Nick Jones
 */
public class ITKTransportPropertiesImpl implements ITKTransportProperties {
	
	private final static Logger logger = LoggerFactory.getLogger(ITKTransportPropertiesImpl.class);
	
	private String transportFrom;
	private String transportTo;
	private String transportRelatesTo;
	private String transportReplyTo;
	private String transportFaultTo;
	private String transportAction;
	private String transportMessageId;
	private String transportUsername;
	private String transportCreatedTime;
	private String transportExpiresTime;
	/**
	 * Instantiates a new iTK transport properties impl.
	 *
	 */
	public ITKTransportPropertiesImpl() {
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKTransportProperties#getTransportFrom()
	 */
	@Override
	public String getTransportFrom() {
		return transportFrom;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKTransportProperties#setTransportFrom(java.lang.String)
	 */
	@Override
	public void setTransportFrom(String transportFrom) {
		this.transportFrom = transportFrom;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKTransportProperties#getTransportTo()
	 */
	@Override
	public String getTransportTo() {
		return transportTo;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKTransportProperties#setTransportTo(java.lang.String)
	 */
	@Override
	public void setTransportTo(String transportTo) {
		this.transportTo = transportTo;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKTransportProperties#getTransportRelatesTo()
	 */
	@Override
	public String getTransportRelatesTo() {
		return transportRelatesTo;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKTransportProperties#setTransportRelatesTo(java.lang.String)
	 */
	@Override
	public void setTransportRelatesTo(String transportRelatesTo) {
		this.transportRelatesTo = transportRelatesTo;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKTransportProperties#getTransportReplyTo()
	 */
	@Override
	public String getTransportReplyTo() {
		return transportReplyTo;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKTransportProperties#setTransportReplyTo(java.lang.String)
	 */
	@Override
	public void setTransportReplyTo(String transportReplyTo) {
		this.transportReplyTo = transportReplyTo;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKTransportProperties#getTransportFaultTo()
	 */
	@Override
	public String getTransportFaultTo() {
		return transportFaultTo;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKTransportProperties#setTransportFaultTo(java.lang.String)
	 */
	@Override
	public void setTransportFaultTo(String transportFaultTo) {
		this.transportFaultTo = transportFaultTo;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKTransportProperties#getTransportAction()
	 */
	@Override
	public String getTransportAction() {
		return transportAction;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKTransportProperties#setTransportAction(java.lang.String)
	 */
	@Override
	public void setTransportAction(String transportAction) {
		this.transportAction = transportAction;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKTransportProperties#getTransportMessageId()
	 */
	@Override
	public String getTransportMessageId() {
		return transportMessageId;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKTransportProperties#setTransportMessageId(java.lang.String)
	 */
	@Override
	public void setTransportMessageId(String transportMessageId) {
		this.transportMessageId = transportMessageId;
	}
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKTransportProperties#getTransportCreated(java.lang.String)
	 */
	@Override
	public String getTransportCreatedTime() {
		return transportCreatedTime;
	}

	public void setTransportCreatedTime(String transportCreatedTime) {
		this.transportCreatedTime = transportCreatedTime;
	}

	public String getTransportExpiresTime() {
		return transportExpiresTime;
	}

	public void setTransportExpiresTime(String transportExpiresTime) {
		this.transportExpiresTime = transportExpiresTime;
	}

	@Override
	public String getTransportUsername() {
		return transportUsername;
	}

	@Override
	public void setTransportUsername(String transportUsername) {
		this.transportUsername = transportUsername;
	}


	
	/**
	 * Builds the from soap.
	 *
	 * @param doc the doc
	 * @return the iTK transport properties
	 * @throws ITKMessagingException the iTK messaging exception
	 */
	public static ITKTransportProperties buildFromSoap(Document soapDocument) throws ITKMessagingException {

		if (soapDocument == null) {
			String eMsg = "SOAP Document not provided";
			logger.error(eMsg);
			throw new ITKMessagingException(null, ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, eMsg);
		}
		
		//Construct an empty itkTransportProperties
		ITKTransportProperties itkTransportProperties = new ITKTransportPropertiesImpl();
		
		try {
			//Extract some key properties from the SOAP envelope

			String transportMessageId = XPaths.WSA_MSGID_XPATH.evaluate(soapDocument);
			String transportAction = XPaths.WSA_ACTION_XPATH.evaluate(soapDocument);
			
			String transportTo = XPaths.WSA_TO_XPATH.evaluate(soapDocument);
			String transportFrom = XPaths.WSA_FROM_XPATH.evaluate(soapDocument);

			String transportReplyTo = XPaths.WSA_REPLY_TO_XPATH.evaluate(soapDocument);
			String transportFaultTo = XPaths.WSA_FAULT_TO_XPATH.evaluate(soapDocument);

			String transportRelatesTo = XPaths.WSA_RELATES_TO_XPATH.evaluate(soapDocument);
			String transportUsername = XPaths.WSA_SECURITY_USERNAME_XPATH.evaluate(soapDocument);

			String transportCreatedTime = XPaths.WSA_SECURITY_CREATED_XPATH.evaluate(soapDocument);
			String transportExpiresTime = XPaths.WSA_SECURITY_EXPIRES_XPATH.evaluate(soapDocument);

			/*
			 * Set the WSA properties
			 * (not part of interface specification but useful internally within reference implementation)
			 */
			itkTransportProperties.setTransportMessageId(transportMessageId);
			itkTransportProperties.setTransportAction(transportAction);

			itkTransportProperties.setTransportTo(transportTo);
			itkTransportProperties.setTransportFrom(transportFrom);

			itkTransportProperties.setTransportReplyTo(transportReplyTo);
			itkTransportProperties.setTransportFaultTo(transportFaultTo);

			itkTransportProperties.setTransportRelatesTo(transportRelatesTo);
			
			itkTransportProperties.setTransportUsername(transportUsername);
			itkTransportProperties.setTransportCreatedTime(transportCreatedTime);
			itkTransportProperties.setTransportExpiresTime(transportExpiresTime);
			
			return itkTransportProperties;
			
		} catch (XPathExpressionException e) {
			throw new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "Could not extract values from request", e);
		} 
	}
	
}