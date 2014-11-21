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

 package uk.co.inhealthcare.open.smsc.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.inhealthcare.open.itk.transform.TransformManager;
import uk.co.inhealthcare.open.smsc.canonical.DemographicUpdate;

/**
 * The Class VerifyNHSNumberRequest.
 *
 * @author Nick Jones
 * @since 0.1
 */
public class GetPatientDetailsByNHSNumberRequest extends SMSPRequest {

	private final static Logger logger = LoggerFactory.getLogger(GetPatientDetailsByNHSNumberRequest.class);

	/**
	 * Gets the nHS number.
	 *
	 * @return the nHS number
	 */
	public String getNHSNumber() {
		return nhsNumber;
	}
	
	/**
	 * Sets the nHS number.
	 *
	 * @param nhsNumber the new nHS number
	 */
	public void setNHSNumber(String nhsNumber) {
		this.nhsNumber = nhsNumber;
	}
	
	/**
	 * Gets the given name.
	 *
	 * @return the given name
	 */
	public String getGivenName() {
		return givenName;
	}
	
	/**
	 * Sets the given name.
	 *
	 * @param givenName the new given name
	 */
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	
	/**
	 * Gets the surname.
	 *
	 * @return the surname
	 */
	public String getSurname() {
		return surname;
	}
	
	/**
	 * Sets the surname.
	 *
	 * @param surname the new surname
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	/**
	 * Gets the date of birth.
	 *
	 * @return the date of birth
	 */
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	
	/**
	 * Sets the date of birth.
	 *
	 * @param dateOfBirth the new date of birth
	 */
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public String getLocalPatientIdentifier() {
		return localPatientIdentifier;
	}

	public void setLocalPatientIdentifier(String localPatientIdentifier) {
		this.localPatientIdentifier = localPatientIdentifier;
	}

	
	/** The nhs number. */
	String nhsNumber;
	
	/** The given name. */
	String givenName;
	
	/** The surname. */
	String surname;
	
	/** The date of birth. */
	String dateOfBirth;

	/** The local patient identifier. */
	String localPatientIdentifier;

	
	/**
	 * Serialise.
	 *
	 * @return the string
	 */
	public String serialise(){
		String XML = "<Message>";
		XML += "<MessageId>"+getSafeString(messageId)+"</MessageId>";
		XML += "<NHSNumber>"+getSafeString(nhsNumber)+"</NHSNumber>";
		XML += "<GivenName>"+getSafeString(givenName)+"</GivenName>";
		XML += "<Surname>"+getSafeString(surname)+"</Surname>";
		XML += "<DateOfBirth>"+getSafeString(dateOfBirth)+"</DateOfBirth>";
		XML += "<LocalPatientIdentifier>"+getSafeString(localPatientIdentifier)+"</LocalPatientIdentifier>";
		XML += "</Message>";
		logger.debug(XML);

		String serialisedMessage ="";
		try {
			serialisedMessage = TransformManager.doTransform(this.getTransformerName(), XML);
		} catch (Exception e) {
			logger.error("Error creating GPDBNN Request.",e);
		}

		logger.debug(serialisedMessage);
		return serialisedMessage;
	}
	
	/**
	 * Instantiates a new GetPatientDetailsByNHSNumberRequest
	 */
	public GetPatientDetailsByNHSNumberRequest(DemographicUpdate update, String conversationId){
		super(conversationId);

		this.dateOfBirth = update.getDateOfBirth();
		this.givenName = update.getGivenName();
		this.surname = update.getFamilyName();
		this.nhsNumber = update.getNHSNumber();
		this.localPatientIdentifier = update.getLocalPatientIdentifier();
	}

	/**
	 * Instantiates a new GetPatientDetailsByNHSNumberRequest.
	 */
	public GetPatientDetailsByNHSNumberRequest(){
		super();
	}

}
