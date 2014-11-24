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

package uk.co.inhealthcare.open.smsc.process;

import uk.co.inhealthcare.open.smsc.messages.GetNHSNumberRequest;
import uk.co.inhealthcare.open.smsc.messages.GetPatientDetailsByNHSNumberRequest;
import uk.co.inhealthcare.open.smsc.messages.GetPatientDetailsBySearchRequest;
import uk.co.inhealthcare.open.smsc.messages.GetPatientDetailsRequest;
import uk.co.inhealthcare.open.smsc.messages.VerifyNHSNumberRequest;

public class DemographicUpdate {

	public String getNHSNumber() {
		return nhsNumber;
	}

	public void setNHSNumber(String nhsNumber) {
		this.nhsNumber = nhsNumber;
	}

	public String getNHSNumberStatus() {
		return nhsNumberStatus;
	}

	public void setNHSNumberStatus(String nhsNumberStatus) {
		this.nhsNumberStatus = nhsNumberStatus;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getLocalPatientIdentifier() {
		return localPatientIdentifier;
	}

	public void setLocalPatientIdentifier(String localPatientIdentifier) {
		this.localPatientIdentifier = localPatientIdentifier;
	}

	private String nhsNumber;
	private String nhsNumberStatus;
	private String givenName;
	private String familyName;
	private String dateOfBirth;
	private String gender;
	private String postcode;
	private String localPatientIdentifier;

	public String toXml() {

		String xml = "<DemographicUpdate>" + "<NhsNumber>" + nhsNumber
				+ "</NhsNumber>" + "<NhsNumberStatus>" + nhsNumberStatus
				+ "</NhsNumberStatus>" + "<GivenName>" + givenName
				+ "</GivenName>" + "<FamilyName>" + familyName
				+ "</FamilyName>" + "<DateOfBirth>" + dateOfBirth
				+ "</DateOfBirth>" + "<Gender>" + gender + "</Gender>"
				+ "<Postcode>" + postcode + "</Postcode>"
				+ "<LocalPatientIdentifier>" + localPatientIdentifier
				+ "</LocalPatientIdentifier>" + "</DemographicUpdate>";
		return xml;
	}

	/**
	 * Instantiates a new GetNHSNumberRequest
	 */
	public GetNHSNumberRequest createGetNHSNumberRequest(String conversationId) {
		GetNHSNumberRequest getNHSNumberRequest = new GetNHSNumberRequest(
				conversationId);

		getNHSNumberRequest.setDateOfBirth(this.getDateOfBirth());
		getNHSNumberRequest.setGender(this.getGender());
		getNHSNumberRequest.setGivenName(this.getGivenName());
		getNHSNumberRequest.setSurname(this.getFamilyName());
		getNHSNumberRequest.setPostcode(this.getPostcode());
		getNHSNumberRequest.setLocalPatientIdentifier(this
				.getLocalPatientIdentifier());

		return getNHSNumberRequest;
	}

	/**
	 * Instantiates a new GetPatientDetailsByNHSNumberRequest
	 */
	public GetPatientDetailsByNHSNumberRequest createGetPatientDetailsByNHSNumberRequest(
			String conversationId) {
		GetPatientDetailsByNHSNumberRequest getPatientDetailsByNHSNumberRequest = new GetPatientDetailsByNHSNumberRequest(
				conversationId);

		getPatientDetailsByNHSNumberRequest.setDateOfBirth(this
				.getDateOfBirth());
		getPatientDetailsByNHSNumberRequest.setGivenName(this.getGivenName());
		getPatientDetailsByNHSNumberRequest.setSurname(this.getFamilyName());
		getPatientDetailsByNHSNumberRequest.setNHSNumber(this.getNHSNumber());
		getPatientDetailsByNHSNumberRequest.setLocalPatientIdentifier(this
				.getLocalPatientIdentifier());

		return getPatientDetailsByNHSNumberRequest;
	}

	/**
	 * Instantiates a new GetPatientDetailsBySearchRequest
	 */
	public GetPatientDetailsBySearchRequest createGetPatientDetailsBySearchRequest(
			String conversationId) {
		GetPatientDetailsBySearchRequest getPatientDetailsBySearchRequest = new GetPatientDetailsBySearchRequest(
				conversationId);

		getPatientDetailsBySearchRequest.setDateOfBirth(this.getDateOfBirth());
		getPatientDetailsBySearchRequest.setGender(this.getGender());
		getPatientDetailsBySearchRequest.setGivenName(this.getGivenName());
		getPatientDetailsBySearchRequest.setSurname(this.getFamilyName());
		getPatientDetailsBySearchRequest.setPostcode(this.getPostcode());
		getPatientDetailsBySearchRequest.setLocalPatientIdentifier(this
				.getLocalPatientIdentifier());

		return getPatientDetailsBySearchRequest;

	}

	/**
	 * Instantiates a new GetPatientDetailsBySearchRequest
	 */
	public GetPatientDetailsRequest createGetPatientDetailsRequest(
			String conversationId) {
		GetPatientDetailsRequest getPatientDetailsRequest = new GetPatientDetailsRequest(
				conversationId);

		getPatientDetailsRequest.setNHSNumber(this.getNHSNumber());
		getPatientDetailsRequest.setDateOfBirth(this.getDateOfBirth());
		getPatientDetailsRequest.setGender(this.getGender());
		getPatientDetailsRequest.setGivenName(this.getGivenName());
		getPatientDetailsRequest.setSurname(this.getFamilyName());
		getPatientDetailsRequest.setPostcode(this.getPostcode());
		getPatientDetailsRequest.setLocalPatientIdentifier(this
				.getLocalPatientIdentifier());

		return getPatientDetailsRequest;

	}

	/**
	 * Instantiates a new VerifyNHSNumberRequest
	 */
	public VerifyNHSNumberRequest createVerifyNHSNumberRequest(
			String conversationId) {
		VerifyNHSNumberRequest verifyNHSNumberRequest = new VerifyNHSNumberRequest(
				conversationId);

		verifyNHSNumberRequest.setDateOfBirth(this.getDateOfBirth());
		verifyNHSNumberRequest.setGivenName(this.getGivenName());
		verifyNHSNumberRequest.setSurname(this.getFamilyName());
		verifyNHSNumberRequest.setNHSNumber(this.getNHSNumber());

		return verifyNHSNumberRequest;
	}

}
