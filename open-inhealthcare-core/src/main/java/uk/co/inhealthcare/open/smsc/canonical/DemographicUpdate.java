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

package uk.co.inhealthcare.open.smsc.canonical;

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

public String toXml(){

	String xml = "<DemographicUpdate>"
				 +"<NhsNumber>"+nhsNumber+"</NhsNumber>"
				 +"<NhsNumberStatus>"+nhsNumberStatus+"</NhsNumberStatus>"
				 +"<GivenName>"+givenName+"</GivenName>"
				 +"<FamilyName>"+familyName+"</FamilyName>"
				 +"<DateOfBirth>"+dateOfBirth+"</DateOfBirth>"
				 +"<Gender>"+gender+"</Gender>"
				 +"<Postcode>"+postcode+"</Postcode>"
				 +"<LocalPatientIdentifier>"+localPatientIdentifier+"</LocalPatientIdentifier>"
				 +"</DemographicUpdate>";
	return xml;
}

}
