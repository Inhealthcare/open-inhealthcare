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
package uk.co.inhealthcare.open.itk.transport;

import uk.co.inhealthcare.open.itk.transport.ITKTransportRoute;

/**
 * The Class ITKTransportRouteImpl.
 *
 * @author Nick Jones
 */
public class ITKTransportRouteImpl implements ITKTransportRoute {

	private String transportType;
	private String physicalAddress;
	private String replyToAddress;
	private String exceptionToAddress;
	private int timeToLive;
	private int transportTimeout;
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKTransportRoute#getTransportType()
	 */
	@Override
	public String getTransportType() {
		return transportType;
	}
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKTransportRoute#getTransportTimeout()
	 */
	@Override
	public int getTransportTimeout() {
		return transportTimeout;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKTransportRoute#getPhysicalAddress()
	 */
	@Override
	public String getPhysicalAddress() {
		return physicalAddress;
	}
	
	/**
	 * Instantiates a new iTK transport route impl.
	 *
	 * @param type the type
	 * @param address the address
	 */
	public ITKTransportRouteImpl(String type, String address) {
		this.transportType = type;
		this.physicalAddress = address;
	}
	
	/**
	 * Instantiates a new iTK transport route impl.
	 *
	 * @param type the type
	 * @param address the address
	 * @param replyTo the reply to
	 * @param exceptionTo the exception to
	 * @param destinationType the destination type
	 * @param timeToLive the time to live
	 */
	public ITKTransportRouteImpl(String type, String address, String replyTo, 
								 String exceptionTo, int timeToLive, int transportTimeout){
		this.transportType = type;
		this.physicalAddress = address;
		this.replyToAddress = replyTo;
		this.exceptionToAddress = exceptionTo;
		this.timeToLive = timeToLive;
		this.transportTimeout = transportTimeout;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKTransportRoute#getReplyToAddress()
	 */
	@Override
	public String getReplyToAddress() {
		return replyToAddress;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKTransportRoute#getExceptionToAddress()
	 */
	@Override
	public String getExceptionToAddress() {
		return exceptionToAddress;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.transport.ITKTransportRoute#getTimeToLive()
	 */
	@Override
	public int getTimeToLive() {
		return timeToLive;
	}
	
}
