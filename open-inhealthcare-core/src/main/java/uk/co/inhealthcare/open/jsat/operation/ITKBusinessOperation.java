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

package uk.co.inhealthcare.open.jsat.operation;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.inhealthcare.open.itk.infrastructure.ITKAddressImpl;
import uk.co.inhealthcare.open.itk.infrastructure.ITKIdentity;
import uk.co.inhealthcare.open.itk.infrastructure.ITKIdentityImpl;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessageProperties;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagePropertiesImpl;
import uk.co.inhealthcare.open.itk.payload.ITKMessage;
import uk.co.inhealthcare.open.itk.payload.ITKSimpleMessageImpl;
import uk.co.inhealthcare.open.itk.source.ITKMessageSender;
import uk.co.inhealthcare.open.jsat.ConfigurationException;
import uk.co.inhealthcare.open.jsat.ITKRequest;
import uk.co.inhealthcare.open.jsat.JSATComponent;

/**
 * This abstract class provides support for all ITK client operation classes.
 * The primary functions are:
 * 1) To hold configuration parameters
 * 2) Configuration validation
 * 3) Build the ITK Message from business objects
 * 
 * @author Nick Jones
 *
 */
public abstract class ITKBusinessOperation extends JSATComponent {

	private final static Logger logger = LoggerFactory.getLogger(ITKBusinessOperation.class);
	
	public final static String ITKMESSAGESENDER_NOT_CONFIGURED = "ITKMessageSender not configured";
	public final static String SERVICEPROVIDER_NOT_CONFIGURED = "Service Provider not configured";
	public final static String SERVICEID_NOT_CONFIGURED = "Service Id not configured";
	public final static String PROFILEID_NOT_CONFIGURED = "Profile Id not configured";
	public final static String SENDER_NOT_CONFIGURED = "Sender not configured";
	public final static String TOPAYLOADTRANSFORM_NOT_CONFIGURED = "To Payload Transform not configured";

	public void setServiceProvider(String serviceProvider) {
		this.serviceProvider = serviceProvider;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public void setToPayloadTransform(String toPayloadTransform) {
		this.toPayloadTransform = toPayloadTransform;
	}
	public void setItkMessageSender(ITKMessageSender itkMessageSender){
		this.itkMessageSender = itkMessageSender;
	}

	protected String serviceProvider;
	protected String serviceId;
	protected String profileId;
	protected String sender;
	protected String toPayloadTransform;
	protected ITKMessageSender itkMessageSender;

	protected void checkITKConfiguration() throws ConfigurationException {
		if (itkMessageSender == null) raiseConfigurationException(ITKMESSAGESENDER_NOT_CONFIGURED);
		if (serviceProvider == null) raiseConfigurationException(SERVICEPROVIDER_NOT_CONFIGURED);
		if (serviceId == null) raiseConfigurationException(SERVICEID_NOT_CONFIGURED);
		if (profileId == null) raiseConfigurationException(PROFILEID_NOT_CONFIGURED);
		if (sender == null) raiseConfigurationException(SENDER_NOT_CONFIGURED);
		if (toPayloadTransform == null) raiseConfigurationException(TOPAYLOADTRANSFORM_NOT_CONFIGURED);
	}

	protected void raiseConfigurationException(String message) throws ConfigurationException {
		logger.error(message);
		throw new ConfigurationException(message);
	}
	
	private ArrayList<ITKIdentity> getAuthors(ITKRequest request){
		
		ArrayList<ITKIdentity> authors = new ArrayList<ITKIdentity>();
		
		if (request.getLocalAuditId() != null){
			authors.add(new ITKIdentityImpl(request.getLocalAuditId()));
		}
	
		if (request.getSpineRoleProfileId() != null){
			authors.add(new ITKIdentityImpl(request.getSpineRoleProfileId(),ITKIdentity.SPINE_ROLE_PROFILE_TYPE));
		}
		if (request.getSpineUserId() != null){
			authors.add(new ITKIdentityImpl(request.getSpineUserId(),ITKIdentity.SPINE_UUID_TYPE));
		}
		if (request.getSpineRoleId() != null){
			authors.add(new ITKIdentityImpl(request.getSpineRoleId(),ITKIdentity.SPINE_ROLE_TYPE));
		}
		return authors;
	}
	
	protected ITKMessage buildITKRequest(ITKRequest request){
		// Create the message
		ITKMessage msg = new ITKSimpleMessageImpl(request.getConversationId());
		request.setTransformerName(this.toPayloadTransform);
		msg.setBusinessPayload(request.serialise());
		logger.info("*** Payload:"+msg.getBusinessPayload());

		// Build the message properties.
		ITKMessageProperties mp = new ITKMessagePropertiesImpl();
		mp.setAuditIdentities(this.getAuthors(request));
		mp.setFromAddress(new ITKAddressImpl(this.sender));
		mp.setToAddress(new ITKAddressImpl(this.serviceProvider));
		mp.setServiceId(this.serviceId);
		mp.setProfileId(this.profileId);

		// Add the properties to the message
		msg.setMessageProperties(mp);
		
		return msg;

	}

}
