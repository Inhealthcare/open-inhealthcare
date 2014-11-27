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


/**
 * Interface representing the properties of a physical
 * (transport specific) destination.
 * 
 * @author Nick Jones
 * 
 */
public interface ITKTransportRoute {
	
	// Transport Types
	/**
	 * Indicates an unknown transport type
	 */
	public static final String UNKNOWN = "UN";
	
	/**
	 * Constant to indicate an HTTP SOAP/Web Service
	 * ITKTransportRoute
	 */
	public static final String HTTP_WS = "WS";

	/**
	 * Obtains the transport type for this
	 * ITKTransportRoute instance
	 * 
	 * @return the transport type. Can be one of
	 * 
	 * {@link ITKTransportRoute#HTTP_WS} or {@link ITKTransportRoute#UNKNOWN}
	 */
	public String getTransportType();
	
	/**
	 * Obtains the transport specific physical
	 * destination address
	 * 
	 * @return A string representation of the
	 * transport specific destination address 
	 * for this ITKTransportRoute
	 */
	public String getPhysicalAddress();

	/**
	 * Obtains the address for any asynchronous replies
	 * 
	 * @return the address for any asynchronous replies
	 */
	public String getReplyToAddress();
	
	/**
	 * Obtains the address for sending an exceptions
	 * that are reported asynchronously
	 * 
	 * @return the address for asynchronous exceptions
	 */
	public String getExceptionToAddress(); 
	
	/**
	 * Obtains the time to live for any messages
	 * being sent via this ITKTransportRoute
	 * 
	 * @return The time to live (in Seconds)
	 */
	public int getTimeToLive();

	/**
	 * Obtains the transport timeout for messages
	 * being sent via this ITKTransportRoute
	 * 
	 * @return The transport timeout (in milliseconds)
	 */
	public int getTransportTimeout();
}
