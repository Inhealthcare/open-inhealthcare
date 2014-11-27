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

import java.util.Locale;
import java.util.UUID;

import uk.co.inhealthcare.open.itk.transport.ITKTransportProperties;

/**
 * Base exception for the ITK Messaging api. Has the capability to wrap
 * standard java exceptions as well as being able to represent more structured
 * errors such as SOAP Faults - e.g. by representing the
 * <code>&lt;itk:ToolkitErrorInfo/&gt;</code> information elements
 * 
 * @see ITKTransportTimeoutException
 * @see ITKCommsException
 * 
 * @author Nick Jones
 */
public class ITKMessagingException extends Exception {
	
	private static final long serialVersionUID = -462421080532710296L;
	
	/**
	 * This is the OID to represent the default code system when raising SOAP faults etc.
	 * OID is <code>2.16.840.1.1138883.2.1.3.2.4.17.268</code>
	 */
	public static final String DEFAULT_ERROR_CODESYSTEM_OID = "2.16.840.1.113883.2.1.3.2.4.17.268";
	
	/**
	 * Default error "code" that indicates that no 
	 * error code is associated with this ITKMessagingException
	 */
	public static final int NO_ERROR_CODE = -1;
	
	/**
	 * Invalid Message - the message structure or content is unrecognised or incorrect
	 */
	public static final int INVALID_MESSAGE_CODE = 1000;
	
	/**
	 * Processing Error (retryable) - a recoverable processing error has been encountered
	 */
	public static final int PROCESSING_ERROR_RETRYABLE_CODE = 2100;
	
	/**
	 * Processing Error (not retryable) - a non-recoverable processing error has been encountered
	 */
	public static final int PROCESSING_ERROR_NOT_RETRYABLE_CODE = 2200;
	
	/**
	 * Access Denied
	 */
	public static final int ACCESS_DENIED_CODE = 3000;
	
	/**
	 * Standard text associated with the {@link #INVALID_MESSAGE_CODE}
	 */
	public static final String INVALID_MESSAGE_TEXT = "Invalid Message - the message structure or content is unrecognised or incorrect";
	
	/**
	 * Standard text associated with the {@link #PROCESSING_ERROR_RETRYABLE_CODE}
	 */
	public static final String PROCESSING_ERROR_RETRYABLE_TEXT = "Processing Error (retryable) - a recoverable processing error has been encountered";
	
	/**
	 * Standard text associated with the {@link #PROCESSING_ERROR_NOT_RETRYABLE_CODE}
	 */
	public static final String PROCESSING_ERROR_NOT_RETRYABLE_TEXT = "Processing Error (not retryable) - a non-recoverable processing error has been encountered";
	
	/**
	 * Standard text associated with the {@link #ACCESS_DENIED_CODE}
	 */
	public static final String ACCESS_DENIED_TEXT = "Access Denied";
	
	/**
	 * Unique error Id used in applications logs to be able to uniquely tie
	 * log messages / exception trace together to aid in diagnostics
	 */
	private String errorId = UUID.randomUUID().toString().toUpperCase();
	
	/**
	 * Error code initialised to {@link #NO_ERROR_CODE}
	 * to indicate that there is no error code
	 */
	private int errorCode = NO_ERROR_CODE;
	
	/**
	 * ITKMessageProperties associated with the exception
	 */
	private ITKMessageProperties relatedMessageProperties;
	
	/**
	 * Transport properties associated with the exception
	 */
	private ITKTransportProperties relatedItkTransportProperties;
	
	/**
	 * Initialise the errorCodeSystem to the {@link #DEFAULT_ERROR_CODESYSTEM_OID}
	 */
	private String errorCodeSystem = DEFAULT_ERROR_CODESYSTEM_OID;
	
	
	/**
	 * Creates an ITKMessagingException with any relevant diagnostic
	 * information about the error - such as the likely cause
	 * 
	 * @param message The diagnostic message about the
	 * the error condition
	 */
	public ITKMessagingException(String message) {
		super(message);
	}

	/**
	 * Creates an ITKMessagingException that wraps an underlying
	 * <code>Throwable</code> which has been encountered
	 * when sending/receiving a message
	 * 
	 * @param cause The <code>Throwable</code> containing
	 * the root cause
	 */
	public ITKMessagingException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Creates an ITKMessagingException with any relevant diagnostic
	 * information about the error - such as the likely cause. An
	 * error code associated with the exception indicates the
	 * characteristics of the underlying issue - e.g. whether or not
	 * it is retryable or not {@link #getErrorCode()}.<br/><br/>
	 * 
	 * Note: by default the <code>errorCode</code> is assumed to be
	 * a code associated with the
	 * {@link #DEFAULT_ERROR_CODESYSTEM_OID} codeSystem
	 * 
	 * @param errorCode The appropriate errorCode associated
	 * with the exception - {@link #INVALID_MESSAGE_CODE};
	 * {@link #PROCESSING_ERROR_RETRYABLE_CODE}; 
	 * {@link #PROCESSING_ERROR_NOT_RETRYABLE_CODE} and
	 * {@link #ACCESS_DENIED_CODE}
	 * 
	 * @param message The diagnostic message about the
	 * the error condition
	 */
	public ITKMessagingException(int errorCode, String message) {
		super(message);
		this.setErrorCode(errorCode);
	}
	
	/**
	 * Creates an ITKMessagingException that wraps an underlying
	 * <code>Throwable</code> which has been encountered
	 * when sending/receiving a message
	 * 
	 * @param message The diagnostic message about the
	 * the error condition
	 * 
	 * @param cause The <code>Throwable</code> containing
	 * the root cause
	 */
	public ITKMessagingException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Creates an ITKMessagingException that wraps an underlying
	 * <code>Throwable</code> which has been encountered
	 * when sending/receiving a message<br/><br/>
	 * 
	 * Note: by default the <code>errorCode</code> is assumed to be
	 * a code associated with the
	 * {@link #DEFAULT_ERROR_CODESYSTEM_OID} codeSystem
	 * 
	 * @param errorCode The appropriate errorCode associated
	 * with the exception - {@link #INVALID_MESSAGE_CODE};
	 * {@link #PROCESSING_ERROR_RETRYABLE_CODE}; 
	 * {@link #PROCESSING_ERROR_NOT_RETRYABLE_CODE} and
	 * {@link #ACCESS_DENIED_CODE}
	 * 
	 * @param message The diagnostic message about the
	 * the error condition
	 * 
	 * @param cause The <code>Throwable</code> containing
	 * the root cause 
	 */
	public ITKMessagingException(int errorCode, String message, Throwable cause) {
		super(message, cause);
		this.setErrorCode(errorCode);
	}
	
	public ITKMessagingException(ITKTransportProperties itkTransportProperties, ITKMessageProperties itkMessageProperties, int errorCode, String message) {
		super(message);
		this.setRelatedMessageProperties(itkMessageProperties);
		this.setErrorCode(errorCode);
		this.setRelatedItkTransportProperties(itkTransportProperties);
	}
	
	public ITKMessagingException(ITKTransportProperties itkTransportProperties, ITKMessageProperties itkMessageProperties, int errorCode, String message, Throwable cause) {
		super(message, cause);
		this.setRelatedMessageProperties(itkMessageProperties);
		this.setErrorCode(errorCode);
		this.setRelatedItkTransportProperties(itkTransportProperties);
	}
	
	public ITKMessagingException(ITKMessageProperties itkMessageProperties, int errorCode, String arg0) {
		super(arg0);
		this.setRelatedMessageProperties(itkMessageProperties);
		this.setErrorCode(errorCode);
	}
	
	public ITKMessagingException(ITKMessageProperties itkMessageProperties, int errorCode, String message, Throwable cause) {
		super(message, cause);
		this.setRelatedMessageProperties(itkMessageProperties);
		this.setErrorCode(errorCode);
	}

	/**
	 * Obtains the <code>errorCode</code> associated with
	 * the ITKMessagingException.
	 * 
	 * Note: by default the <code>errorCode</code> is assumed to be
	 * a code associated with the
	 * {@link #DEFAULT_ERROR_CODESYSTEM_OID} codeSystem
	 * 
	 * @return the <code>errorCode</code> associated
	 * with the exception and appropriate to the 
	 * {@link #getErrorCodeSystem()}}. If no error code
	 * has been defined {@link #NO_ERROR_CODE} (the int -1)
	 * is returned
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * Sets the <code>errorCode</code> associated with
	 * the ITKMessagingException.
	 * 
	 * Note: by default the <code>errorCode</code> is assumed to be
	 * a code associated with the
	 * {@link #DEFAULT_ERROR_CODESYSTEM_OID} codeSystem. To use
	 * and alternative system set the <code>errorCodeSystem</code>
	 * via the {@link #setErrorCodeSystem(String)} method
	 * 
	 * @param errorCode The appropriate errorCode associated
	 * with the exception - {@link #INVALID_MESSAGE_CODE};
	 * {@link #PROCESSING_ERROR_RETRYABLE_CODE}; 
	 * {@link #PROCESSING_ERROR_NOT_RETRYABLE_CODE} and
	 * {@link #ACCESS_DENIED_CODE}
	 */
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Obtains the <code>errorCodeSystem</code> for any
	 * <code>errorCode</code> associated with the
	 * ITKMessagingException. This is only relevant if the
	 * {@link #getErrorCode()} returns anything other than
	 * {@link #NO_ERROR_CODE}
	 * 
	 * @return The <code>errorCodeSystem</code> associated
	 * with the <code>errorCode</code> for with the
	 * ITKMessagingException. The default error code system
	 * is indicated by the 
	 * {@link #DEFAULT_ERROR_CODESYSTEM_OID}
	 */
	public String getErrorCodeSystem() {
		return errorCodeSystem;
	}

	/**
	 * Sets the <code>errorCodeSystem</code> for any
	 * <code>errorCode</code> associated with the
	 * ITKMessagingException. This is only relevant if the
	 * {@link #setErrorCode(int)} (or an appropriate constructor)
	 * is used to set the <code>errorCode</code> to something
	 * other than {@link #NO_ERROR_CODE}
	 * 
	 * @param errorCodeSystem The <code>errorCodeSystem</code> associated
	 * with the <code>errorCode</code> for with the
	 * ITKMessagingException. The default error code system
	 * is indicated by the 
	 * {@link #DEFAULT_ERROR_CODESYSTEM_OID}
	 */
	public void setErrorCodeSystem(String errorCodeSystem) {
		this.errorCodeSystem = errorCodeSystem;
	}

	public ITKMessageProperties getRelatedMessageProperties() {
		return relatedMessageProperties;
	}

	public void setRelatedMessageProperties(ITKMessageProperties relatedMessageProperties) {
		this.relatedMessageProperties = relatedMessageProperties;
	}

	/**
	 * Obtains the unique errorId for the ITKMessagingException
	 * The <code>errorId</code> is typically expected to be 
	 * used in applications logs to uniquely tie
	 * log messages / exception traces together to aid in diagnostics
	 * 
	 * @return the <code>errorId</code> associated with this
	 * ITKMessagingException.
	 */
	public String getErrorId() {
		return errorId;
	}
	
	/**
	 * Obtains the unique errorId for the ITKMessagingException
	 * The <code>errorId</code> is typically expected to be 
	 * used in applications logs to uniquely tie
	 * log messages / exception traces together to aid in diagnostics
	 * 
	 * @param errorId the <code>errorId</code> associated with this
	 * ITKMessagingException. 
	 */
	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}
	
	/**
	 * Obtains the standard (human readable message) associated with the
	 * ITKMessagingException. The text also contains the <code>errorId</code>
	 * to ensure it is represented in logs/stack traces
	 */
	@Override
	public String getMessage() {
		return super.getMessage().startsWith("[" + this.errorId) ? super.getMessage() : "[" + this.errorId + "] " + super.getMessage();
	}
	
	/**
	 * Obtains the standard (human readable message) associated with the
	 * ITKMessagingException in the language for the {@link Locale} where it
	 * localised messages are available.
	 * 
	 * The text also contains the <code>errorId</code>
	 * to ensure it is represented in logs/stack traces
	 */
	@Override
	public String getLocalizedMessage() {
		return super.getLocalizedMessage().startsWith("[" + this.errorId) ? super.getLocalizedMessage() : "[" + this.errorId + "] " + super.getLocalizedMessage();
	}
	
	/**
	 * Convenience method to decode any <code>errorCode</code>
	 * from the {@link #DEFAULT_ERROR_CODESYSTEM_OID}.
	 * 
	 * @return The appropriate error text. If the <code>codeSystem</code>
	 * is not the {@link #DEFAULT_ERROR_CODESYSTEM_OID} returns the 
	 * {@link #getMessage()} text. 
	 */
	public String decodeErrorCode() {
		if (this.errorCodeSystem.equals(DEFAULT_ERROR_CODESYSTEM_OID)) {
			switch (this.getErrorCode()) {
				case INVALID_MESSAGE_CODE: return INVALID_MESSAGE_TEXT;
				case PROCESSING_ERROR_RETRYABLE_CODE: return PROCESSING_ERROR_RETRYABLE_TEXT;
				case PROCESSING_ERROR_NOT_RETRYABLE_CODE: return PROCESSING_ERROR_NOT_RETRYABLE_TEXT;
				case ACCESS_DENIED_CODE: return ACCESS_DENIED_TEXT;
			}
		}
		//By default return the description of the error
		return "Error code system " + this.getErrorCodeSystem() + " not known - error text was " + getMessage();
	}

	public ITKTransportProperties getRelatedItkTransportProperties() {
		return relatedItkTransportProperties;
	} 

	public void setRelatedItkTransportProperties(
			ITKTransportProperties relatedItkTransportProperties) {
		this.relatedItkTransportProperties = relatedItkTransportProperties;
	}

}
