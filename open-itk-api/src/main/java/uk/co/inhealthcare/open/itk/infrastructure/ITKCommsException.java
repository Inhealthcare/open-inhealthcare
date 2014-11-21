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


/**
 * An exception to report any underlying transport / communication errors.
 * 
 * @see ITKTransportTimeoutException
 * 
 * @author Nick Jones
 */
public class ITKCommsException extends ITKMessagingException {

	private static final long serialVersionUID = 6454181113703383977L;

	/**
	 * Creates an ITKCommsException with any relevant diagnostic
	 * information about the error - such as the likely cause
	 * 
	 * @param message The diagnostic message about the error condition
	 */
	public ITKCommsException(String message) {
		super(message);
	}

	/**
	 * Creates an ITKCommsException that wraps an underlying
	 * <code>Throwable</code> which has been encountered
	 * when sending/receiving a message
	 * 
	 * @param cause The <code>Throwable</code> containing the root cause
	 */
	public ITKCommsException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates an ITKCommsException that wraps an underlying
	 * <code>Throwable</code> which has been encountered
	 * when sending/receiving a message
	 * 
	 * @param message The diagnostic message about the error condition
	 * @param cause The <code>Throwable</code> containing the root cause
	 */
	public ITKCommsException(String message, Throwable cause) {
		super(message, cause);
	}

}
