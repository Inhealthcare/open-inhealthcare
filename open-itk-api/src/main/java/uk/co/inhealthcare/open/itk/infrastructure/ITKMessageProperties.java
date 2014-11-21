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
import java.util.Map;

import uk.co.inhealthcare.open.itk.payload.ITKMessage;

/**
 * Interface that represents the addressing, audit and other meta-data information
 * associated with the ITK Message. This interface allows these properties to be
 * passed between the application and transport layers.
 * 
 * @see ITKMessage
 * 
 * @author Nick Jones
 * 
 */
public interface ITKMessageProperties {
	
	/**
	 * The handling specification key for the property that allows the message originator
	 * to specify whether or not they would like a business Acknowledgement.</br/><br/>
	 * 
	 * Permissible values for this handling specification are the Strings <code>true</code>
	 * or <code>false</code>
	 */
	public static final String BUSINESS_ACK_HANDLING_SPECIFICATION_KEY = "urn:nhs:itk:ns:201005:ackrequested";
	
	/**
	 * The handling specification key for the property that allows the message originator
	 * to explicitly specify the interaction id of the message.<br/><br/>
	 * 
	 * The value for this handling specification should be the interaction id of the message - 
	 * e.g. <code>urn:nhs-itk:interaction:verifyNHSNumberRequest-v1-0</code>
	 */
	public static final String INTERACTION_HANDLING_SPECIFICATION_KEY = "urn:nhs-itk:ns:201005:interaction";
	
	/**
	 * Obtains the originators ITK address
	 * @return and ITKAddress object with the address details of the message originator
	 */
	public ITKAddress getFromAddress();
	
	/**
	 * Sets the originators address for this message. 
	 * @param fromAddress the ITKAddress object with the address details of the message
	 * originator
	 */
	public void setFromAddress(ITKAddress fromAddress);

	/**
	 * Obtains the destination address for the associated ITK Message.<br/><br/>
	 * <b>Note</b> Whilst the ITK Specifications allow for multiple recipients
	 * to be specified this version of the ITK API only supports a single recipient
	 * to be defined as this satisfies the majority of anticipated use-cases.
	 * 
	 * @return The ITKAddress object containing the address details of the
	 * intended recipient
	 */
	public ITKAddress getToAddress();
	
	/**
	 * Sets the destination address for the associated ITK Message.<br/><br/>
	 * <b>Note</b> Whilst the ITK Specifications allow for multiple recipients
	 * to be specified this version of the ITK API only supports a single recipient
	 * to be defined as this satisfies the majority of anticipated use-cases.
	 * 
	 * @param toAddress the ITKAddress object containing the address details of the
	 * intended recipient
	 */
	public void setToAddress(ITKAddress toAddress);
	
	/**
	 * Returns all audit identities of the ITK message originator
	 * @return An ITKIdentity object containing the audit id
	 */
	public ArrayList<ITKIdentity> getAuditIdentities();
	
	/**
	 * Returns the audit identity of the ITK message originator of a given type
	 * @return ITKIdentity object containing the audit id
	 */
	public String getAuditIdentityByType(String identityType);
	
	/**
	 * adds an audit identities of the message originator
	 * @param auditIdentity an ITKIdentity object containing the audit id
	 */
	public void addAuditIdentity(ITKIdentity auditIdentity);

	/**
	 * Sets the audit identities of the message originator
	 * @param auditIdentities an array of ITKIdentity objects containing the audit id
	 */
	public void setAuditIdentities(ArrayList<ITKIdentity> auditIdentities);

	/**
	 * Returns all identities of the Patient
	 * @return ITKIdentity object containing the patient id
	 */
	public ArrayList<ITKIdentity> getPatientIdentities();
	
	/**
	 * Returns the patient identifier for the subject of the ITK message (of a given type)
	 * @return ITKIdentity object containing the patient id
	 */
	public String getPatientIdentityByType(String identityType);
	
	/**
	 * adds a patient identity
	 * @param auditIdentity ITKIdentity object containing the patient id
	 */
	public void addPatientIdentity(ITKIdentity auditIdentity);

	/**
	 * Sets the patient identities of the message subject
	 * @param patientIdentities ITKIdentity object containing the patient id
	 */
	public void setPatientIdentities(ArrayList<ITKIdentity> patientIdentities);

	/**
	 * ITK message service associated with the message
	 * @return a String identifying the ITK Service Id
	 */
	public String getServiceId();
	
	/**
	 * Sets the ITK message serviceId associated with this message
	 * @param serviceId The String identifying the ITK Service (from ITK Domain Message Specifications)
	 */
	public void setServiceId(String serviceId);

	/**
	 * Obtains the itk payloadId associated with this
	 * message. This itk payload message Id is 
	 * used for tying the manifest to the payload.
	 * 
	 * @return The UUID associated with the ITK Message payload
	 */
	public String getItkPayloadId();
	
	/**
	 * Sets the ITK payloadId associated with this
	 * message. This itk payload message Id is 
	 * used for tying the manifest to the payload.
	 * @param itkPayloadId The UUID associated with the
	 * ITK Message payload
	 */
	public void setItkPayloadId(String itkPayloadId);

	/**
	 * Obtains the profileId associated with the ITKMessage.
	 * The profile Id represents the set of rules used to
	 * construct the message and provides a single String
	 * that aides with message/document version management as well
	 * as allowing message/document consumers to understand whether
	 * or not they are capable of handling the associated ITK Message
	 * 
	 * @return The profile id (e.g. <code>nhs-en:profile:nonCodedCDADocument-v2-0</code>)
	 */
	public String getProfileId();
	
	/**
	 * Sets the profileId associated with the ITKMessage.
	 * The profile Id represents the set of rules used to
	 * construct the message and provides a single String
	 * that aides with message/document version management as well
	 * as allowing message/document consumers to understand whether
	 * or not they are capable of handling the associated ITK Message
	 * 
	 * @param profileId The profile id (e.g. <code>nhs-en:profile:nonCodedCDADocument-v2-0</code>)
	 */
	public void setProfileId(String profileId);
	
	/**
	 * Obtains the tracking id associated with the ITKMessage
	 * when sending the message between two or more endpoints.
	 * The tracking id should not have meaning beyond the original
	 * ITK transmission and associated acknowledgements. In
	 * particular the trackingId is not expected to be an id directly
	 * represented in the business payload such as a CDA document
	 * instance id. 
	 * The primary purpose of the tracking Id is for operational
	 * reasons (i.e. allows entries in log file across different
	 * servers and nodes to be tied together) and for correlation
	 * of asynchronous acknowledgements such as ITK Infrastructure
	 * acknowledgements (see {@link ITKAckDetails#getTrackingRef()})
	 * and ITK Business Acknowledgements.
	 * 
	 * @return a String with the UUID represent the tracking Id
	 */
	public String getTrackingId();
	
	/**
	 * Sets the tracking id associated with the ITKMessage
	 * prior to sending the message between two or more endpoints.
	 * The tracking id should not have meaning beyond the original
	 * ITK transmission and associated acknowledgements. In
	 * particular the trackingId is not expected to be an id directly
	 * represented in the business payload such as a CDA document
	 * instance id. 
	 * The primary purpose of the tracking Id is for operational
	 * reasons (i.e. allows entries in log file across different
	 * servers and nodes to be tied together) and for correlation
	 * of asynchronous acknowledgements such as ITK Infrastructure
	 * acknowledgements (see {@link ITKAckDetails#getTrackingRef()})
	 * and ITK Business Acknowledgements.
	 * 
	 * @param trackingId a String UUID representing the tracking Id
	 * for this transmission. Note if the message is resent a new tracking
	 * Id should be created
	 */
	public void setTrackingId(String trackingId);
	
	/**
	 * Obtains the Map of the handling specifications associated with the
	 * {@link ITKMessage}.<br/><br/>
	 * 
	 * Handling specifications provide an extensible
	 * mechanism for the message originator to attach special processing instructions
	 * with the message.
	 * The current ITK specifications provide two built-in handling specifications
	 * - one for requesting a business acknowledgement message
	 * ({@link #BUSINESS_ACK_HANDLING_SPECIFICATION_KEY}), and one to explicitly
	 * associated an interaction Id with the message
	 * ({@link #INTERACTION_HANDLING_SPECIFICATION_KEY}).	 
	 * 
	 * @return The {@link Map} of all handling specifications associated
	 * with the message. For each entry in the Map contains both the key
	 * (e.g. <code>urn:nhs:itk:ns:201005:ackrequested</code>) and the
	 * associated value. If no handling specifications are associated
	 * with the message then an empty map is returned.  
	 */
	public Map<String, String> getHandlingSpecifications();
	
	/**
	 * 
	 * Allows the message originator to add a handling specification to the
	 * associated {@link ITKMessage}.<br/><br/>
	 * 
	 * Handling specifications provide an extensible
	 * mechanism for the message originator to attach special processing instructions
	 * with the message.
	 * The current ITK specifications provide two built-in handling specifications
	 * - one for requesting a business acknowledgement message
	 * ({@link #BUSINESS_ACK_HANDLING_SPECIFICATION_KEY}), and one to explicitly
	 * associated an interaction Id with the message
	 * ({@link #INTERACTION_HANDLING_SPECIFICATION_KEY}).	 
	 * 
	 * @param key The handling specification key - for instance
	 * {@link #BUSINESS_ACK_HANDLING_SPECIFICATION_KEY}
	 * 
	 * @param value The appropriate <code>value</code> for the handling specification
	 */
	public void addHandlingSpecification(String key, String value);
	
	/**
	 * Convenience method that allows the value of the handling specification
	 * to be obtained directly.
	 * 
	 * @param key The handling specification key - for instance {@link #BUSINESS_ACK_HANDLING_SPECIFICATION_KEY}
	 * @return The <code>value</code> associated with the provided key or <code>null</code> if the
	 * handling specification indicated by the key was not present
	 */
	public String getHandlingSpecification(String key);

}