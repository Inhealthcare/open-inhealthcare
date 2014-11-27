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

package uk.co.inhealthcare.open.smsc.operation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.infrastructure.ITKUnavailableException;
import uk.co.inhealthcare.open.itk.payload.ITKMessage;
import uk.co.inhealthcare.open.jsat.ConfigurationException;
import uk.co.inhealthcare.open.jsat.ValidationException;
import uk.co.inhealthcare.open.jsat.operation.ITKBusinessOperation;
import uk.co.inhealthcare.open.smsc.messages.SMSPRequest;
import uk.co.inhealthcare.open.smsc.messages.SMSPResponse;
import uk.co.inhealthcare.open.smsc.messages.logging.LoggingException;
import uk.co.inhealthcare.open.smsc.messages.logging.SMSCLoggingService;
import uk.co.inhealthcare.open.smsc.messages.logging.SimpleSMSCLoggingServiceImpl;
import uk.co.inhealthcare.open.smsc.validator.NHSNumber;

// CONSIDER : Consider removing dependency on ITKBusinessOperation and JSATComponent. 
//            Stops clean reuse outside of JSAT.
//            Though the only real impact is naming - e.g. packages and the use of JSAT.properties.

/**
 * This abstract class provides support for all SMSC client operation classes.
 * The primary functions are:
 * 1) Common process for all operations 
 * 2) Data validation functions
 * 3) Configuration validation (to ensure components are properly configured). Common ITK Configuration is passed
 *    up to the ITKBusinessOperation superclass.
 * 
 * @author Nick Jones
 *
 */
public abstract class SMSCBusinessOperation extends ITKBusinessOperation { 
	
	private final static Logger logger = LoggerFactory.getLogger(SMSCBusinessOperation.class);

	public final static String SMSCLOGGER_NOT_CONFIGURED = "SMSC Logger not configured";
	public final static String ERR_REQUEST_NOT_PROVIDED = "Request not provided";


	public final static String NHSERROR_PREFIX = "NHS Number Format invalid. ";
	public final static String DOBERROR_PREFIX = "Date Of Birth Format invalid. ";
	public final static String SURNAMEERROR_PREFIX = "Invalid Surname. ";
	public final static String GIVENNAMEERROR_PREFIX = "Invalid Given Name. ";
	public final static String GENDERERROR_PREFIX = "Invalid Gender. ";
	public final static String POSTCODEERROR_PREFIX = "Invalid Postcode. ";

	protected final static int TRACE = 1;
	protected final static int VERIFY = 2;
	
	protected SMSCLoggingService smscLogger = new SimpleSMSCLoggingServiceImpl();

	public void setSmscLogger(SMSCLoggingService smscLogger) {
		this.smscLogger = smscLogger;
	}
	
	protected void checkSMSCConfiguration() throws ConfigurationException {
		super.checkITKConfiguration();
		if (smscLogger == null) raiseConfigurationException(SMSCLOGGER_NOT_CONFIGURED);
		
	}

	protected void validateNHSNumber(String nhsNumber, int type) throws ValidationException
	{

		NHSNumber nhsNo = new NHSNumber(nhsNumber);
		if (nhsNo.isValid()){
			logger.debug("NHS Number is valid. ["+nhsNumber+"]");
		} else {
			raiseValidationException(NHSERROR_PREFIX+"["+nhsNumber+"]");
		}
	}

	protected void validateDateOfBirth(String dateOfBirth, int type) throws ValidationException
	{
		if ( (dateOfBirth==null) || (dateOfBirth.isEmpty()) ){
			raiseValidationException(DOBERROR_PREFIX+"Must be provided.");
		}
		
		// VERIFY - full YYYYMMDD only
		if (type==VERIFY){

			// Check YYYYMMDD
			boolean valid = false;
			DateFormat df = new SimpleDateFormat("yyyyMMdd");
			df.setLenient(false);
			if (!valid && (dateOfBirth.length()==8) ){
				try {
					df.parse(dateOfBirth);
					valid = true;
				} catch (ParseException | NullPointerException e) {
					// Not YYYYMMDD
				}
			}
			if (!valid){
				raiseValidationException(DOBERROR_PREFIX+"Should be YYYYMMDD. ["+dateOfBirth+"]");
			} else {
				logger.debug("Date of birth is Valid. ["+dateOfBirth+"]");
			}
			
		}
		// TRACE - YYYYMMDD / YYYYMM / YYYY 
		if (type==TRACE){
			boolean valid = false;
			DateFormat df = new SimpleDateFormat("yyyyMMdd");
			df.setLenient(false);
			
			// Try YYYYMMDD
			if (!valid && (dateOfBirth.length()==8) ){
				try {
					df.parse(dateOfBirth);
					valid = true;
				} catch (ParseException | NullPointerException e) {
					// Not YYYYMMDD
				}
			}

			// Try YYYYMM
			if (!valid && (dateOfBirth.length()==6) ){
				df = new SimpleDateFormat("yyyyMM");
				df.setLenient(false);
				
				try {
					df.parse(dateOfBirth);
					valid = true;
				} catch (ParseException | NullPointerException e) {
					// Not YYYYMM
				}
			
			}
			// Try YYYY
			if (!valid && (dateOfBirth.length()==4) ){
				df = new SimpleDateFormat("yyyy");
				df.setLenient(false);
				
				try {
					df.parse(dateOfBirth);
					valid = true;
				} catch (ParseException | NullPointerException e) {
					// Not YYYY
				}
			
			}
			if (!valid){
				raiseValidationException(DOBERROR_PREFIX+"Should be one of YYYYMMDD / YYYYMM / YYYY. ["+dateOfBirth+"]");
			} else {
				logger.debug("Date of birth is Valid. ["+dateOfBirth+"]");
			}
		}
	}
	
	protected void validateGivenName(String givenName, int type) throws ValidationException
	{
		if (type == VERIFY) {
			// Given Name is optional for VERIFY, but if present, must be at least 1 character long
			
			if ( (givenName == null) || (givenName.isEmpty() ) ) {
				// OK - Optional
			} else {
				int start = givenName.indexOf("*");
				if (start > 0){
					raiseValidationException(GIVENNAMEERROR_PREFIX+"Contains a wildcard. ["+givenName+"]");
				}
			}
			logger.debug("Given Name is valid. ["+givenName+"]");
		}
		
		if (type == TRACE) {
			// givenName is optional for TRACE
			if ((givenName != null) && (givenName.length()>0)) {
	
				// If present givenName can contain a wildcard ("*") - but only after two valid characters
				int start = givenName.indexOf("*");
				if (start>0 && start < 2){
					raiseValidationException(GIVENNAMEERROR_PREFIX+"Contains a wildcard before character 3. ["+givenName+"]");
				}
			}

			logger.debug("givenName is valid. ["+givenName+"]");
		}

	}
	
	protected void validateSurname(String surname, int type) throws ValidationException
	{
		if (type == VERIFY) {
			// SURNAME is optional, but if present, must be at least
			// 3 characters long and should not contain wildcards
			// TODO: Configure to allow 2 characters
			if ( (surname == null) || (surname.isEmpty() ) ) {
				// OK - Optional
			} else {
				int start = surname.indexOf("*");
				if (start > 0){
					raiseValidationException(SURNAMEERROR_PREFIX+"Contains a wildcard. ["+surname+"]");
				}
				if ( surname != null && surname.length() < 3 ){
					raiseValidationException(SURNAMEERROR_PREFIX+"Must be at least 3 characters long if provided. ["+surname+"]");
				}
			}
			
			logger.debug("Surname is valid. ["+surname+"]");
		}

		if (type == TRACE) {
			// SURNAME is mandatory. Wildcards must be preceded by at least 2 characters
			if ( (surname==null) || (surname.isEmpty()) ){
				raiseValidationException(SURNAMEERROR_PREFIX+"Mandatory for a trace.");
			}

			// Surname can contain a wildcard ("*") - but only after two valid characters
			int start = surname.indexOf("*");
			if (start>0 && start < 2){
				raiseValidationException(SURNAMEERROR_PREFIX+"Contains a wildcard before character 3. ["+surname+"]");
			}

			logger.debug("Surname is valid. ["+surname+"]");
		}
	}
	
	protected void validateGender(String gender, int type) throws ValidationException
	{
		
		// Gender is only present on a trace request
		if (type==TRACE){
			if ( (gender==null) || (gender.isEmpty()) ){
				raiseValidationException(GENDERERROR_PREFIX+"Mandatory for a trace.");
			}
			
			boolean validGender = getLookupBoolean(SMSP_GENDER, gender, "N");
			if (!validGender){
				raiseValidationException(GENDERERROR_PREFIX+"Not a valid value. ["+gender+"]");
			}

			logger.debug("gender is valid ["+gender+"]");
		}
	}

	protected void validatePostcode(String postcode, int type) throws ValidationException
	{
		// postcode is only present on a trace request
		if (type==TRACE){
			if ((postcode != null) && (postcode.length()>0)) {
				// If postcode is entered then it must be valid.
				// maximum of 8 characters
				if (postcode.length()>8){
					raiseValidationException(POSTCODEERROR_PREFIX+"Longer than 8 characters. ["+postcode+"]");
				}

				// Postcode can contain wildcards ("*") - only after two valid characters
				int start = postcode.indexOf("*");
				if (start>0 && start < 2){
					raiseValidationException(POSTCODEERROR_PREFIX+"Contains a wildcard before character 3. ["+postcode+"]");
				}
				
				// Postcode must be alphanumeric (including a space)
				if (!postcode.matches("[A-Za-z0-9\\* ]+")){
					raiseValidationException(POSTCODEERROR_PREFIX+"Not alphanumeric. ["+postcode+"]");
				}
				// Check that the string contains exactly one space? 
				int spPos = postcode.indexOf(" ");
				if (spPos > 0 ) {
					spPos = postcode.indexOf(" ",spPos+1);
					if (spPos > 0 ){
						raiseValidationException(POSTCODEERROR_PREFIX+"Contains more than one space. ["+postcode+"]");
					}
				}
			}
			logger.debug("postcode is valid. ["+postcode+"]");
		}
	}

	private void raiseValidationException(String message) throws ValidationException {
		logger.info(message);
		throw new ValidationException(message);
	}
	protected SMSPResponse processCommon(SMSPRequest request) 
			throws ValidationException, ConfigurationException, LoggingException {

		// validate that the component is properly configured
		checkSMSCConfiguration();
		
		// validate that the request is present
		if (request == null){
			raiseConfigurationException(ERR_REQUEST_NOT_PROVIDED);
		}

		logger.info("*** SpineMiniServicesClient: Starting "+request.getClass().getSimpleName());
		
		// Log the request (requires the transformer name to be set)
		request.setTransformerName(this.toPayloadTransform);
		this.smscLogger.logSMSPRequest(request);
		
		SMSPResponse response = null;
		
		// Validate the request
		validate(request);
		
		// Create the message
		ITKMessage itkRequest = buildITKRequest(request);
		
		// Set the patient identities
		setPatientIdentities(request,itkRequest);
		
		// Send the message and handle the response
		try {
			ITKMessage resp = itkMessageSender.sendSync(itkRequest); 
			response = marshallResponse(resp);
			logger.trace("Received response from GetNHSNumber call:"+response.getResponseCode());
			
		} catch (ITKUnavailableException e) {
			// A Busy Tone was received from the SMSP - just set response code to BUSY (aligns with Ensemble SMSC)
			logger.error("Busy Tone received sending ITK Message");
			response = marshallResponse("BUSY", request.getConversationId());

		} catch (ITKMessagingException e) {
			logger.error("Error Sending ITK Message",e);
			// A serious exception occurred - just set response code to FAILED (aligns with Ensemble SMSC)
			response = marshallResponse("FAILED", request.getConversationId());
		}
		
		// Log the response
		this.smscLogger.logSMSPResponse(response);

		logger.info("*** SpineMiniServicesClient: Ending "+request.getClass().getSimpleName());
		
		return response;
	}
	
	protected abstract void validate(SMSPRequest smspRequest) throws ValidationException ;
	protected abstract void setPatientIdentities(SMSPRequest smspRequest, ITKMessage itkMessage) throws ValidationException ;
	protected abstract SMSPResponse marshallResponse(ITKMessage response) throws ITKMessagingException ;
	protected abstract SMSPResponse marshallResponse(String responseCode, String conversationId);

}
