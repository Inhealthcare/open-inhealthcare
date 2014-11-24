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
 * The Class GetNHSNumberResponse.
 *
 * @author Nick Jones
 * @since 0.1
 */
public class GetNHSNumberResponse extends SMSPResponse {

	private final static Logger logger = LoggerFactory.getLogger(GetNHSNumberResponse.class);
	private final static String XP_RESPONSECODE = "/hl7:getNHSNumberResponse-v1-0/hl7:value/@code";
	private final static String XP_NHSNUMBER = "/hl7:getNHSNumberResponse-v1-0/hl7:subject/hl7:patient/hl7:id[@root='2.16.840.1.113883.2.1.4.1']/@extension";
	private final static String XP_LOCALID = "/hl7:getNHSNumberResponse-v1-0/hl7:subject/hl7:patient/hl7:id[@root='2.16.840.1.113883.2.1.3.2.4.18.24']/@extension";

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
		
	/** The nhs number. */
	String nhsNumber;
	
	/** The local identifier. */
	String localIdentifier;
	
	public String getLocalIdentifier() {
		return localIdentifier;
	}

	public void setLocalIdentifier(String localIdentifier) {
		this.localIdentifier = localIdentifier;
	}

	/**
	 * Instantiates a new verify nhs number response.
	 *
	 * @param response the response
	 * @throws ITKMessagingException the iTK messaging exception
	 */
	public GetNHSNumberResponse(ITKMessage response) throws ITKMessagingException {

		super(response);
		
		// Build The Response object
		String nhsNumber="";
		String responseCode="";
		String localIdentifier="";

		try {
			Document responseDoc = DomUtils.parse(response.getBusinessPayload());
			responseCode = (String)XPaths.compileXPath(XP_RESPONSECODE)
					.evaluate(responseDoc);
			nhsNumber = (String)XPaths.compileXPath(XP_NHSNUMBER)
					.evaluate(responseDoc);
			localIdentifier = (String)XPaths.compileXPath(XP_LOCALID)
					.evaluate(responseDoc);
		} catch (SAXException e) {
			logger.error("SAXException parsing GetNHSNumberResponse", e);
			throw new ITKMessagingException("SAXException parsing GetNHSNumberResponse");
		} catch (IOException e) {
			logger.error("IOException parsing GetNHSNumberResponse", e);
			throw new ITKMessagingException("IOException parsing GetNHSNumberResponse");
		} catch (ParserConfigurationException e) {
			logger.error("ParserConfigurationException parsing GetNHSNumberResponse", e);
			throw new ITKMessagingException("ParserConfigurationException parsing GetNHSNumberResponse");
		} catch (XPathExpressionException e) {
			logger.error("XPathExpressionException parsing GetNHSNumberResponse", e);
			throw new ITKMessagingException("XPathExpressionException parsing GetNHSNumberResponse");
		}
					
		this.nhsNumber = nhsNumber;
		this.localIdentifier = localIdentifier;
		this.responseCode = responseCode;

		// pick up the JSAT conversationId from the ITK conversationId
		this.conversationId = response.getConversationId();
	}

	/**
	 * Instantiates a new get nhs number response from a response code only - for error scenarios
	 *
	 * @param response the response
	 * @throws ITKMessagingException the iTK messaging exception
	 */
	public GetNHSNumberResponse(String responseCode, String conversationId) {

		super(responseCode,conversationId);

	}

}
