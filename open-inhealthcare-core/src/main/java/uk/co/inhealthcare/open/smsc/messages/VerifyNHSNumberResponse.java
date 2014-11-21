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
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.payload.ITKMessage;
import uk.co.inhealthcare.open.itk.util.xml.DomUtils;
import uk.co.inhealthcare.open.itk.util.xml.XPaths;


/**
 * The Class VerifyNHSNumberResponse.
 *
 * @author Nick Jones
 * @since 0.1
 */
public class VerifyNHSNumberResponse extends SMSPResponse {
	
	private final static Logger logger = LoggerFactory.getLogger(GetNHSNumberRequest.class);
	private final static String XP_RESPONSECODE = "/hl7:verifyNHSNumberResponse-v1-0/hl7:value/@code";
	private final static String XP_NHSNUMBER = "/hl7:verifyNHSNumberResponse-v1-0/hl7:component/hl7:validIdentifier/hl7:subject/hl7:patient/hl7:id/@extension";
	private final static String XP_VERIFIED = "/hl7:verifyNHSNumberResponse-v1-0/hl7:component/hl7:validIdentifier/hl7:value/@value";

	/**
	 * Gets the nhs number.
	 *
	 * @return the nhs number
	 */
	public String getNhsNumber() {
		return nhsNumber;
	}
	
	/**
	 * Sets the nhs number.
	 *
	 * @param nhsNumber the new nhs number
	 */
	public void setNhsNumber(String nhsNumber) {
		this.nhsNumber = nhsNumber;
	}
	
	/**
	 * Gets the verified indicator.
	 *
	 * @return the verified indicator
	 */
	public boolean getVerifiedIndicator() {
		return verifiedIndicator;
	}
	
	/**
	 * Sets the verified indicator.
	 *
	 * @param verifiedIndicator the new verified indicator
	 */
	public void setVerifiedIndicator(boolean verifiedIndicator) {
		this.verifiedIndicator = verifiedIndicator;
	}
	
	/** The nhs number. */
	String nhsNumber;
	
	/** The verified indicator. */
	boolean verifiedIndicator;
	
	/**
	 * Instantiates a new verify nhs number response.
	 *
	 * @param response the response
	 * @throws ITKMessagingException the iTK messaging exception
	 */
	public VerifyNHSNumberResponse(ITKMessage response) throws ITKMessagingException {

		super(response);

		// Build The Response
		String verified="";
		String nhsNumber="";
		String responseCode="";
		try {
			Document responseDoc = DomUtils.parse(response.getBusinessPayload());
			responseCode = (String)XPaths.compileXPath(XP_RESPONSECODE)
					.evaluate(responseDoc);
			verified = (String)XPaths.compileXPath(XP_VERIFIED)
					.evaluate(responseDoc);
			nhsNumber = (String)XPaths.compileXPath(XP_NHSNUMBER)
					.evaluate(responseDoc);
		} catch (SAXException e) {
			logger.error("SAXException parsing VerifyNHSNumberResponse", e);
			throw new ITKMessagingException("SAXException parsing VerifyNHSNumberResponse");
		} catch (IOException e) {
			logger.error("IOException parsing VerifyNHSNumberResponse", e);
			throw new ITKMessagingException("IOException parsing VerifyNHSNumberResponse");
		} catch (ParserConfigurationException e) {
			logger.error("ParserConfigurationException parsing VerifyNHSNumberResponse", e);
			throw new ITKMessagingException("ParserConfigurationException parsing VerifyNHSNumberResponse");
		} catch (XPathExpressionException e) {
			logger.error("XPathExpressionException parsing VerifyNHSNumberResponse", e);
			throw new ITKMessagingException("XPathExpressionException parsing VerifyNHSNumberResponse");
		}
		
		if (verified.equalsIgnoreCase("TRUE")){
			this.verifiedIndicator = true;
		} else {
			this.verifiedIndicator = false;
		}
			
		this.nhsNumber = nhsNumber;
		this.responseCode = responseCode;
		
	}

	/**
	 * Instantiates a new verify nhs number response from a response code only - for error scenarios
	 *
	 * @param response the response
	 * @throws ITKMessagingException the iTK messaging exception
	 */
	public VerifyNHSNumberResponse(String responseCode, String conversationId) {

		super(responseCode,conversationId);

	}
}
