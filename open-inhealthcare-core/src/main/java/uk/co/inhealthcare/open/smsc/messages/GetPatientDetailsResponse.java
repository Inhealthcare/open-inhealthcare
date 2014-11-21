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

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.payload.ITKMessage;
import uk.co.inhealthcare.open.itk.transform.TransformManager;
import uk.co.inhealthcare.open.itk.util.xml.DomUtils;
import uk.co.inhealthcare.open.itk.util.xml.XPaths;
import uk.co.inhealthcare.open.smsc.entity.Address;
import uk.co.inhealthcare.open.smsc.entity.Person;

/**
 * The Class GetPatientDetailsResponse. PLACEHOLDER ONLY
 *
 * @author Nick Jones
 * @since 0.1
 */
public class GetPatientDetailsResponse extends SMSPResponse {

	private final static Logger logger = LoggerFactory.getLogger(GetPatientDetailsResponse.class);
	
	private final static String XP_RESPONSECODE = "/Response/ResponseCode";
	private final static String XP_NHSNUMBER = "/Response/NHSNumber";
	private final static String XP_LOCALID = "/Response/LocalIdentifier";
	private final static String XP_TITLE = "/Response/Title";
	private final static String XP_GIVENNAME = "/Response/GivenName";
	private final static String XP_SURNAME = "/Response/Surname";
	private final static String XP_OTHERGIVENNAME = "/Response/OtherGivenName";
	private final static String XP_GENDER = "/Response/Gender";
	private final static String XP_DATEOFBIRTH = "/Response/DateOfBirth";
	private final static String XP_DATEOFDEATH = "/Response/DateOfDeath";
	private final static String XP_PRACTICECODE = "/Response/PracticeCode";
	private final static String XP_PRACTICENAME = "/Response/PracticeName";
	private final static String XP_PRACTICECONTACTTELEPHONENUMBER = "/Response/PracticeContactTelephoneNumber";

	private Person person;
	
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getNhsNumber(){
		return person.getNhsNumber();
	}
	
	/**
	 * Instantiates a new Get Patient Details response.
	 *
	 * @param response the response
	 * @throws ITKMessagingException the iTK messaging exception
	 */
	public GetPatientDetailsResponse(ITKMessage response) throws ITKMessagingException {
		
		super(response);
		logger.trace("START: Parsing Response");

		// Create the Canonical form response
		String canonicalMessage ="";
		String tName = "xslt/smsc/FromGetPatientDetailsResponse.xslt";
		try {
			canonicalMessage = TransformManager.doTransform(tName, response.getBusinessPayload());
		} catch (Exception e) {
			logger.error("Error creating GPD Response.",e);
		}
		logger.debug(canonicalMessage);

		// Extract data from the canonical response
		person = new Person();
		try {
			Document responseDoc = DomUtils.parse(canonicalMessage);
			
			responseCode = (String)XPaths.compileXPath(XP_RESPONSECODE).evaluate(responseDoc);
			
			person.setNhsNumber((String)XPaths.compileXPath(XP_NHSNUMBER).evaluate(responseDoc));
			person.setLocalIdentifier((String)XPaths.compileXPath(XP_LOCALID).evaluate(responseDoc));
			person.setTitle((String)XPaths.compileXPath(XP_TITLE).evaluate(responseDoc));
			person.setSurname((String)XPaths.compileXPath(XP_SURNAME).evaluate(responseDoc));
			person.setGivenName((String)XPaths.compileXPath(XP_GIVENNAME).evaluate(responseDoc));
			person.setOtherGivenName((String)XPaths.compileXPath(XP_OTHERGIVENNAME).evaluate(responseDoc));
			person.setDateOfBirth((String)XPaths.compileXPath(XP_DATEOFBIRTH).evaluate(responseDoc));
			person.setDateOfDeath((String)XPaths.compileXPath(XP_DATEOFDEATH).evaluate(responseDoc));
			person.setGender((String)XPaths.compileXPath(XP_GENDER).evaluate(responseDoc));
			person.setPracticeCode((String)XPaths.compileXPath(XP_PRACTICECODE).evaluate(responseDoc));
			person.setPracticeName((String)XPaths.compileXPath(XP_PRACTICENAME).evaluate(responseDoc));
			person.setPracticeContactTelephoneNumber((String)XPaths.compileXPath(XP_PRACTICECONTACTTELEPHONENUMBER).evaluate(responseDoc));
			
			Node address = (Node)XPaths.compileXPath("/Response/Address").evaluate(responseDoc, XPathConstants.NODE);
			if (address!=null){
				person.setAddress(new Address());
				person.getAddress().setLine1((String)XPaths.compileXPath("/Response/Address/AddressLine1").evaluate(responseDoc));
				person.getAddress().setLine2((String)XPaths.compileXPath("/Response/Address/AddressLine2").evaluate(responseDoc));
				person.getAddress().setLine3((String)XPaths.compileXPath("/Response/Address/AddressLine3").evaluate(responseDoc));
				person.getAddress().setLine4((String)XPaths.compileXPath("/Response/Address/AddressLine4").evaluate(responseDoc));
				person.getAddress().setLine5((String)XPaths.compileXPath("/Response/Address/AddressLine5").evaluate(responseDoc));
				person.getAddress().setPostcode((String)XPaths.compileXPath("/Response/Address/Postcode").evaluate(responseDoc));
			}
			
			Node tAddress = (Node)XPaths.compileXPath("/Response/TemporaryAddress").evaluate(responseDoc, XPathConstants.NODE);
			if (tAddress!=null){
				person.setTemporaryAddress(new Address());
				person.getTemporaryAddress().setLine1((String)XPaths.compileXPath("/Response/TemporaryAddress/AddressLine1").evaluate(responseDoc));
				person.getTemporaryAddress().setLine2((String)XPaths.compileXPath("/Response/TemporaryAddress/AddressLine2").evaluate(responseDoc));
				person.getTemporaryAddress().setLine3((String)XPaths.compileXPath("/Response/TemporaryAddress/AddressLine3").evaluate(responseDoc));
				person.getTemporaryAddress().setLine4((String)XPaths.compileXPath("/Response/TemporaryAddress/AddressLine4").evaluate(responseDoc));
				person.getTemporaryAddress().setLine5((String)XPaths.compileXPath("/Response/TemporaryAddress/AddressLine5").evaluate(responseDoc));
				person.getTemporaryAddress().setPostcode((String)XPaths.compileXPath("/Response/TemporaryAddress/Postcode").evaluate(responseDoc));
			}

			Node cAddress = (Node)XPaths.compileXPath("/Response/CorrespondenceAddress").evaluate(responseDoc, XPathConstants.NODE);
			if (cAddress!=null){
				person.setCorrespondenceAddress(new Address());
				person.getCorrespondenceAddress().setLine1((String)XPaths.compileXPath("/Response/CorrespondenceAddress/AddressLine1").evaluate(responseDoc));
				person.getCorrespondenceAddress().setLine2((String)XPaths.compileXPath("/Response/CorrespondenceAddress/AddressLine2").evaluate(responseDoc));
				person.getCorrespondenceAddress().setLine3((String)XPaths.compileXPath("/Response/CorrespondenceAddress/AddressLine3").evaluate(responseDoc));
				person.getCorrespondenceAddress().setLine4((String)XPaths.compileXPath("/Response/CorrespondenceAddress/AddressLine4").evaluate(responseDoc));
				person.getCorrespondenceAddress().setLine5((String)XPaths.compileXPath("/Response/CorrespondenceAddress/AddressLine5").evaluate(responseDoc));
				person.getCorrespondenceAddress().setPostcode((String)XPaths.compileXPath("/Response/CorrespondenceAddress/Postcode").evaluate(responseDoc));
			}
			
			Node pAddress = (Node)XPaths.compileXPath("/Response/PracticeAddress").evaluate(responseDoc, XPathConstants.NODE);
			if (pAddress!=null){
				person.setPracticeAddress(new Address());
				person.getPracticeAddress().setLine1((String)XPaths.compileXPath("/Response/PracticeAddress/AddressLine1").evaluate(responseDoc));
				person.getPracticeAddress().setLine2((String)XPaths.compileXPath("/Response/PracticeAddress/AddressLine2").evaluate(responseDoc));
				person.getPracticeAddress().setLine3((String)XPaths.compileXPath("/Response/PracticeAddress/AddressLine3").evaluate(responseDoc));
				person.getPracticeAddress().setLine4((String)XPaths.compileXPath("/Response/PracticeAddress/AddressLine4").evaluate(responseDoc));
				person.getPracticeAddress().setLine5((String)XPaths.compileXPath("/Response/PracticeAddress/AddressLine5").evaluate(responseDoc));
				person.getPracticeAddress().setPostcode((String)XPaths.compileXPath("/Response/PracticeAddress/Postcode").evaluate(responseDoc));
			}

		} catch (SAXException e) {
			logger.error("SAXException parsing GetPatientDetailsResponse", e);
			throw new ITKMessagingException("SAXException parsing GetPatientDetailsResponse");
		} catch (IOException e) {
			logger.error("IOException parsing GetPatientDetailsResponse", e);
			throw new ITKMessagingException("IOException parsing GetPatientDetailsResponse");
		} catch (ParserConfigurationException e) {
			logger.error("ParserConfigurationException parsing GetPatientDetailsResponse", e);
			throw new ITKMessagingException("ParserConfigurationException parsing GetPatientDetailsResponse");
		} catch (XPathExpressionException e) {
			logger.error("XPathExpressionException parsing GetPatientDetailsResponse", e);
			throw new ITKMessagingException("XPathExpressionException parsing GetPatientDetailsResponse");
		}
					
		logger.trace("ENDED: Parsing Response");
	}

	/**
	 * Instantiates a new get nhs number response from a response code only - for error scenarios
	 *
	 * @param response the response
	 * @throws ITKMessagingException the iTK messaging exception
	 */
	public GetPatientDetailsResponse(String responseCode, String conversationId) {

		super(responseCode,conversationId);

	}

}
