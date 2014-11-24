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


/**
 * The Class GetNHSNumberRequest.
 *
 * @author Nick Jones
 * @since 0.1
 */
public class GetNHSNumberRequest extends SMSPRequest {

	private final static Logger logger = LoggerFactory.getLogger(GetNHSNumberRequest.class);

	/** The given name. */
	String givenName;
	
	/** The surname. */
	String surname;
	
	/** The date of birth. */
	String dateOfBirth;
	
	/** The postcode. */
	String postcode;
	
	/** The gender. */
	String gender;
	
	/** The local patient identifier. */
	String localPatientIdentifier;

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
	
	/**
	 * Gets the postcode.
	 *
	 * @return the postcode
	 */
	public String getPostcode() {
		return postcode;
	}
	
	/**
	 * Sets the postcode.
	 *
	 * @param postcode the new postcode
	 */
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getLocalPatientIdentifier() {
		return localPatientIdentifier;
	}

	public void setLocalPatientIdentifier(String localPatientIdentifier) {
		this.localPatientIdentifier = localPatientIdentifier;
	}

	/**
	 * Serialise.
	 *
	 * @return the string
	 */
	public String serialise(){
		if (serialisedMessage!=null){
			return serialisedMessage;
		}
		String XML = "<Message>";
		XML += "<MessageId>"+getSafeString(messageId)+"</MessageId>";
		XML += "<GivenName>"+getSafeString(givenName)+"</GivenName>";
		XML += "<Surname>"+getSafeString(surname)+"</Surname>";
		XML += "<DateOfBirth>"+getSafeString(dateOfBirth)+"</DateOfBirth>";
		XML += "<Postcode>"+getSafeString(postcode)+"</Postcode>";
		XML += "<Gender>"+getSafeString(gender)+"</Gender>";
		XML += "<LocalPatientIdentifier>"+getSafeString(localPatientIdentifier)+"</LocalPatientIdentifier>";
		XML += "</Message>";
		logger.debug(XML);
		
		try {
			serialisedMessage = TransformManager.doTransform(this.getTransformerName(), XML);
		} catch (Exception e) {
			logger.error("Error creating GNN Request.",e);
		}
		
		logger.debug(serialisedMessage);
		return serialisedMessage;
	}
	
	public GetNHSNumberRequest(String conversationId) {
		super(conversationId);
	}

	/**
	 * Instantiates a new GetNHSNumberRequest
	 */
	public GetNHSNumberRequest(){
		super();
	}

}
