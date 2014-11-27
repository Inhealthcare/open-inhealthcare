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
package uk.co.inhealthcare.open.itk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.inhealthcare.open.itk.capabilities.DirectoryOfServices;
import uk.co.inhealthcare.open.itk.infrastructure.ITKAddress;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.infrastructure.ITKServiceImpl;
import uk.co.inhealthcare.open.itk.transport.ITKTransportRoute;
import uk.co.inhealthcare.open.itk.transport.ITKTransportRouteImpl;
import uk.co.inhealthcare.open.itk.util.ITKDirectoryProperties;
import uk.co.inhealthcare.open.itk.util.ITKServiceProperties;

/**
 * The Class ITKSimpleDOS.
 *
 * @author Nick Jones
 */
public class ITKSimpleDOSImpl implements DirectoryOfServices {

	private final static Logger logger = LoggerFactory.getLogger(ITKSimpleDOSImpl.class);

	private static final String IS_SUPPORTED = "supported";
	private static final String PROFILE = "profile";
	private static final String SUPPORTS_ASYNC = "supportsAsync";
	private static final String SUPPORTS_SYNC = "supportsSync";
	private static final String IS_BASE64 = "isBase64";
	private static final String MIME_TYPE = "mimeType";
	private static final String CHANNELID = "channelid";
	private static final String TIME_TO_LIVE = "TimeToLive";
	private static final String TRANSPORT_TIMEOUT = "TransportTimeout";
	private static final String EXCEPTION_TO = "ExceptionTo";
	private static final String DEFAULT = "DEFAULT";
	private static final String REPLY_TO = "ReplyTo";
	private static final String ROUTE_TYPE = "RouteType";
	private static final String PHYSICAL_DESTINATION = "PhysicalDestination";

	private ITKDirectoryProperties itkDirectoryProperties;
	private ITKServiceProperties itkServiceProperties;

	public void setItkDirectoryProperties(
			ITKDirectoryProperties itkDirectoryProperties) {
		this.itkDirectoryProperties = itkDirectoryProperties;
	}

	public void setItkServiceProperties(
			ITKServiceProperties itkServiceProperties) {
		this.itkServiceProperties = itkServiceProperties;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.capabilities.DirectoryOfServices#resolveDestination(java.lang.String, uk.nhs.interoperability.infrastructure.ITKAddress)
	 */
	@Override
	public ITKTransportRoute resolveDestination(String serviceId, ITKAddress address) throws ITKMessagingException {
		
		if ((serviceId == null)||(serviceId.isEmpty())) {
			String eMsg = "ServiceId not provided";
			logger.error(eMsg);
			throw new ITKMessagingException(null, ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, eMsg);
		}
		if ( (address == null) || (address.getURI() ==null) || (address.getURI().isEmpty()) ) {
			String eMsg = "Destination Address not provided";
			logger.error(eMsg);
			throw new ITKMessagingException(null, ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, eMsg);
		}

		ITKTransportRoute route = null;
		
		String svc = serviceId;
		String add = address.getURI();
		String channelKey = svc + "." + add + "." + CHANNELID;
		logger.trace("Channel Key:"+channelKey);
		
		String channelId = itkDirectoryProperties.getProperty(channelKey);
		
		if (null==channelId){
			String eMsg = "Route not found for:"+channelKey;
			logger.error(eMsg);
			throw new ITKMessagingException(null, ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, eMsg);
		} else {
			logger.trace("Channel Id:"+channelId);
			String physicalDestination = getDirectoryProperty(channelId,PHYSICAL_DESTINATION);
			String routeType = getDirectoryProperty(channelId,ROUTE_TYPE);
			String replyTo = getDirectoryProperty(channelId,REPLY_TO);
			String exceptionTo = getDirectoryProperty(channelId,EXCEPTION_TO);
			String timeToLive = getDirectoryProperty(channelId,TIME_TO_LIVE);
			String transportTimeout = getDirectoryProperty(channelId,TRANSPORT_TIMEOUT);

			// time to live
			int ttl = 30*60; // failsafe time to live of 30 minutes
			if (null!=timeToLive){
				ttl = Integer.parseInt(timeToLive);
			}
			// transport timeout
			int tt = 30000; // failsafe timeout of 30 seconds
			if (null!=transportTimeout){
				tt = Integer.parseInt(transportTimeout);
			}
			route = new ITKTransportRouteImpl(routeType,physicalDestination,
											  replyTo,exceptionTo,ttl,tt);
		}
		
		return route;
	}

	/**
	 * Gets the directory property.
	 *
	 * @param channelId the channel id
	 * @param propertyName the property name
	 * @return the directory property
	 */
	private String getDirectoryProperty(String channelId, String propertyName){
		String propertyValue = itkDirectoryProperties.getProperty(channelId
				+ "." + propertyName);
		if (propertyValue==null){
			propertyValue = itkDirectoryProperties.getProperty(DEFAULT + "."
					+ propertyName);
		}
		if (propertyValue==null){
			propertyValue = "";
		}
		return propertyValue;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.capabilities.DirectoryOfServices#getService(java.lang.String)
	 */
	public ITKService getService(String serviceId) throws ITKMessagingException {

		if ((serviceId == null)||(serviceId.isEmpty())) {
			throw new ITKMessagingException(null, ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "ServiceId not provided");
		}
		
		ITKServiceImpl service = new ITKServiceImpl(serviceId);

		boolean isSupported = getServiceBooleanProperty(serviceId, IS_SUPPORTED);
		if (!isSupported) {
			throw new ITKMessagingException(null, ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "Service not supported"+serviceId);
		}
		service.setBase64(getServiceBooleanProperty(serviceId, IS_BASE64));
		service.setSupportsSync(getServiceBooleanProperty(serviceId, SUPPORTS_SYNC));
		service.setSupportsAsync(getServiceBooleanProperty(serviceId, SUPPORTS_ASYNC));
		service.setMimeType(getServiceProperty(serviceId, MIME_TYPE));

		return service;
	}
	/**
	 * Helper method to check the profile id
	 *
	 * @param profileId the profile id
	 * @return a boolean indicating if the profile is supported or not
	 */
	public boolean isServiceProfileSupported(String profileId){
		String profileKey = PROFILE+"."+profileId+"."+IS_SUPPORTED;
		String supported = itkServiceProperties.getProperty(profileKey);
		if ((supported!=null) &&(supported.equalsIgnoreCase("Y"))){
			return true;
		} else {	
			return false;
		}
	}
	
	/**
	 * Gets the service property.
	 *
	 * @param serviceId the service id
	 * @param propertyName the property name
	 * @return the service property
	 */
	private String getServiceProperty(String serviceId, String propertyName){
	
		String propertyValue = itkServiceProperties.getProperty(serviceId + "."
				+ propertyName);
		if (propertyValue==null){
			propertyValue = itkServiceProperties.getProperty(DEFAULT + "."
					+ propertyName);
		}
		if (propertyValue==null){
			propertyValue = "";
		}
		return propertyValue;
	}
	
	/**
	 * Gets the service boolean property.
	 *
	 * @param serviceId the service id
	 * @param propertyName the property name
	 * @return the service boolean property
	 */
	private boolean getServiceBooleanProperty(String serviceId, String propertyName){
		
		boolean propertyValue = false;
		if (!propertyName.equals("")){

			String serviceProperty = itkServiceProperties.getProperty(serviceId
					+ "." + propertyName);
			
			if (null!=serviceProperty){
				if (serviceProperty.equals("Y")){
					propertyValue = true;
				}
			} else {
				serviceProperty = itkServiceProperties.getProperty("DEFAULT."
						+ propertyName);
				
			}
		}
		return propertyValue;
	}
}
