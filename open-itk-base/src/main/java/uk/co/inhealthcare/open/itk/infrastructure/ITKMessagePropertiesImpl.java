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
package uk.co.inhealthcare.open.itk.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

import uk.co.inhealthcare.open.itk.util.xml.XPaths;

/**
 * The Class ITKMessagePropertiesImpl.
 *
 * @author Nick Jones
 */
public class ITKMessagePropertiesImpl implements ITKMessageProperties {
	
	public ITKMessagePropertiesImpl(){
		super();
		// TrackingId and PayloadId are only meaningfull within the ITK world and will normally be set automatically.
		this.trackingId = UUID.randomUUID().toString().toUpperCase();
		this.itkPayloadId = UUID.randomUUID().toString().toUpperCase();
	}

	//private final static Logger logger = LoggerFactory.getLogger(ITKMessagePropertiesImpl.class);

	private ITKAddress fromAddress;
	private ITKAddress toAddress;
	private ArrayList<ITKIdentity> auditIdentities = new ArrayList<ITKIdentity>();
	private ArrayList<ITKIdentity> patientIdentities = new ArrayList<ITKIdentity>();
	private String serviceId; 
	private String itkPayloadId;
	private String profileId;
	private String trackingId;
	
	//Create a map for handling specifications and make sure they are populated
	private Map<String, String> handlingSpecification = new HashMap<String, String>();

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.infrastructure.ITKMessageProperties#getTrackingId()
	 */
	public String getTrackingId() {
		return trackingId;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.infrastructure.ITKMessageProperties#setTrackingId()
	 */
	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.infrastructure.ITKMessageProperties#getFromAddress()
	 */
	public ITKAddress getFromAddress() {
		return fromAddress;
	}
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.infrastructure.ITKMessageProperties#setFromAddress(uk.nhs.interoperability.infrastructure.ITKAddress)
	 */
	public void setFromAddress(ITKAddress fromAddress) {
		this.fromAddress = fromAddress;
	}
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.infrastructure.ITKMessageProperties#getToAddress()
	 */
	public ITKAddress getToAddress() {
		return toAddress;
	}
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.infrastructure.ITKMessageProperties#setToAddress(uk.nhs.interoperability.infrastructure.ITKAddress)
	 */
	public void setToAddress(ITKAddress toAddress) {
		this.toAddress = toAddress;
	}
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.infrastructure.ITKMessageProperties#getAuditIdentities()
	 */
	public ArrayList<ITKIdentity> getAuditIdentities() {
		return auditIdentities;
	}
	
	public String getAuditIdentityByType(String identityType) {
		for (int i = 0; i< auditIdentities.size();i++){
			if (auditIdentities.get(i).getType().equalsIgnoreCase(identityType)){
				return auditIdentities.get(i).getURI();
			}
		}
		return null;
	}

	public void addAuditIdentity(ITKIdentity auditIdentity) {
		this.auditIdentities.add(auditIdentity);
	}

	public void setAuditIdentities(ArrayList<ITKIdentity> auditIdentities) {
		this.auditIdentities = auditIdentities;
	}
	
	// Patient Identities

	public ArrayList<ITKIdentity> getPatientIdentities() {
		return patientIdentities;
	}
	
	public String getPatientIdentityByType(String identityType) {
		for (int i = 0; i< patientIdentities.size();i++){
			if (patientIdentities.get(i).getType().equalsIgnoreCase(identityType)){
				return patientIdentities.get(i).getURI();
			}
		}
		return null;
	}

	public void addPatientIdentity(ITKIdentity patientIdentity) {
		this.patientIdentities.add(patientIdentity);
	}

	public void setPatientIdentities(ArrayList<ITKIdentity> patientIdentities) {
		this.patientIdentities = patientIdentities;
	}

	
	//
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.infrastructure.ITKMessageProperties#getServiceId()
	 */
	public String getServiceId() {
		return serviceId;
	}
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.infrastructure.ITKMessageProperties#setServiceId(java.lang.String)
	 */
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.infrastructure.ITKMessageProperties#getProfileId()
	 */
	public String getProfileId() {
		return profileId;
	}
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.infrastructure.ITKMessageProperties#setProfileId(java.lang.String)
	 */
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.infrastructure.ITKMessageProperties#getHandlingSpecifications()
	 */
	@Override
	public Map<String, String> getHandlingSpecifications() {
		return this.handlingSpecification;
	}
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.infrastructure.ITKMessageProperties#addHandlingSpecification(java.lang.String, java.lang.String)
	 */
	@Override
	public void addHandlingSpecification(String key, String value) {
		this.handlingSpecification.put(key, value);
	}
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.infrastructure.ITKMessageProperties#getHandlingSpecification(java.lang.String)
	 */
	@Override
	public String getHandlingSpecification(String key) {
		
		String handlingSpec = null;
		
		if (this.handlingSpecification != null){
			handlingSpec =  this.handlingSpecification.get(key);
		}
		return handlingSpec;
	}
	
	/**
	 * Builds the ITKMessageProperties from the distributionEnvelope
	 *
	 * @param distributionEnvelope the distribution envelope
	 * @return the iTK message properties
	 * @throws ITKMessagingException the iTK messaging exception
	 */
	public static ITKMessageProperties build(Document distributionEnvelope) throws ITKMessagingException {
		//Construct an empty itkMessageProperties
		ITKMessagePropertiesImpl itkMessageProperties = new ITKMessagePropertiesImpl();
		
		try {
			
			Double payloadCount = (Double)XPaths.ITK_PAYLOAD_COUNT_XPATH.evaluate(distributionEnvelope, XPathConstants.NUMBER);
			
			//For now throw an exception if we encounter anything other than a single payload
			// LIMITATION - Only supports one multiple payload
			if (payloadCount != 1) {
				throw new ITKMessagingException(itkMessageProperties, ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "The ITK reference implementation currently only supports a single payload within a distribution envelope");
			}
			
			String itkService = XPaths.ITK_SERVICE_XPATH.evaluate(distributionEnvelope);
			
			// LIMITATION - Only supports one audit id
			String itkAuditIdentityURI = XPaths.ITK_AUDIT_IDENTITY_URI_XPATH.evaluate(distributionEnvelope);
			String itkAuditIdentityType = XPaths.ITK_AUDIT_IDENTITY_TYPE_XPATH.evaluate(distributionEnvelope);
			String itkFromAddressUri = XPaths.ITK_FROM_ADDRESS_URI_XPATH.evaluate(distributionEnvelope);
			String itkFromAddressType = XPaths.ITK_FROM_ADDRESS_TYPE_XPATH.evaluate(distributionEnvelope);
			String itkToAddressUri = XPaths.ITK_FIRST_TO_ADDRESS_URI_XPATH.evaluate(distributionEnvelope);
			String itkToAddressType = XPaths.ITK_FIRST_TO_ADDRESS_TYPE_XPATH.evaluate(distributionEnvelope);
			String itkTrackingId = XPaths.ITK_TRACKING_ID_XPATH.evaluate(distributionEnvelope);
			String itkProfileId = XPaths.ITK_PROFILE_ID_XPATH.evaluate(distributionEnvelope);
			String itkPayloadId = XPaths.ITK_FIRST_PAYLOAD_ID_XPATH.evaluate(distributionEnvelope);
			String businessAckHandlingSpecification = XPaths.ITK_BUSINESS_ACK_HANDLING_SPECIFICATIONS_XPATH.evaluate(distributionEnvelope);
			
			if (itkPayloadId.toUpperCase().startsWith("UUID_")){
				itkMessageProperties.setItkPayloadId(itkPayloadId.substring(5));
			} else {
				itkMessageProperties.setItkPayloadId(itkPayloadId);
			}

			if (itkToAddressType.length()==0){
				itkMessageProperties.setToAddress(new ITKAddressImpl(itkToAddressUri));
			} else {
				itkMessageProperties.setToAddress(new ITKAddressImpl(itkToAddressUri,itkToAddressType));
			}
			
			if (itkFromAddressType.length()==0){
				itkMessageProperties.setFromAddress(new ITKAddressImpl(itkFromAddressUri));
			} else {
				itkMessageProperties.setFromAddress(new ITKAddressImpl(itkFromAddressUri,itkFromAddressType));
			}
			
			if (itkAuditIdentityType.length()==0){
				itkMessageProperties.addAuditIdentity(new ITKIdentityImpl(itkAuditIdentityURI));
			} else {
				itkMessageProperties.addAuditIdentity(new ITKIdentityImpl(itkAuditIdentityURI, itkAuditIdentityType));
			}
			
			itkMessageProperties.setServiceId(itkService);
			itkMessageProperties.setProfileId(itkProfileId);
			itkMessageProperties.setTrackingId(itkTrackingId);
			itkMessageProperties.addHandlingSpecification(ITKMessageProperties.BUSINESS_ACK_HANDLING_SPECIFICATION_KEY, businessAckHandlingSpecification);

			return itkMessageProperties;
			
		} catch (XPathExpressionException e) {
			throw new ITKMessagingException(itkMessageProperties, ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "Could not extract values from request", e);
		}
	}

	@Override
	public String getItkPayloadId() {
		return itkPayloadId;
	}

	@Override
	public void setItkPayloadId(String itkPayloadId) {
		this.itkPayloadId = itkPayloadId;
	}

}
