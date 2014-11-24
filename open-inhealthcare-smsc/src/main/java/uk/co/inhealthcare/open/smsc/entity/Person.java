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

package uk.co.inhealthcare.open.smsc.entity;

import java.util.List;

public class Person {
	
	private String  title;
	private String  surname;
	private String  givenName;
	private String  otherGivenName;
	private String  dateOfBirth;
	private String  nhsNumber;
	private String  dateOfDeath;
	private String  gender;
	private Address address;
	private Address temporaryAddress;
	private Address  correspondenceAddress;
	private List<Telecom> telecoms;
	private String  practiceCode;
	private String  practiceName;
	private Address practiceAddress;
	private String  practiceContactTelephoneNumber;
	private String  localIdentifier;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getOtherGivenName() {
		return otherGivenName;
	}

	public void setOtherGivenName(String otherGivenName) {
		this.otherGivenName = otherGivenName;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getNhsNumber() {
		return nhsNumber;
	}

	public void setNhsNumber(String nhsNumber) {
		this.nhsNumber = nhsNumber;
	}

	public String getDateOfDeath() {
		return dateOfDeath;
	}

	public void setDateOfDeath(String dateOfDeath) {
		this.dateOfDeath = dateOfDeath;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Address getTemporaryAddress() {
		return temporaryAddress;
	}

	public void setTemporaryAddress(Address temporaryAddress) {
		this.temporaryAddress = temporaryAddress;
	}

	public Address getCorrespondenceAddress() {
		return correspondenceAddress;
	}

	public void setCorrespondenceAddress(Address correspondenceAddress) {
		this.correspondenceAddress = correspondenceAddress;
	}

	public List<Telecom> getTelecoms() {
		return telecoms;
	}

	public void setTelecoms(List<Telecom> telecoms) {
		this.telecoms = telecoms;
	}

	public String getPracticeCode() {
		return practiceCode;
	}

	public void setPracticeCode(String practiceCode) {
		this.practiceCode = practiceCode;
	}

	public String getPracticeName() {
		return practiceName;
	}

	public void setPracticeName(String practiceName) {
		this.practiceName = practiceName;
	}

	public Address getPracticeAddress() {
		return practiceAddress;
	}

	public void setPracticeAddress(Address practiceAddress) {
		this.practiceAddress = practiceAddress;
	}

	public String getPracticeContactTelephoneNumber() {
		return practiceContactTelephoneNumber;
	}

	public void setPracticeContactTelephoneNumber(
			String practiceContactTelephoneNumber) {
		this.practiceContactTelephoneNumber = practiceContactTelephoneNumber;
	}

	public String getLocalIdentifier() {
		return localIdentifier;
	}

	public void setLocalIdentifier(String localIdentifier) {
		this.localIdentifier = localIdentifier;
	};
}
