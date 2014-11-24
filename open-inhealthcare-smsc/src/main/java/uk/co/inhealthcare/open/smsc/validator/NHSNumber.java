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

package uk.co.inhealthcare.open.smsc.validator;

import java.util.ArrayList;
import java.util.List;

/**
 * To ensure the validity of an NHS number 
 * 
 * Definition taken from <a href="http://www.datadictionary.nhs.uk/version2/data_dictionary/data_field_notes/n/nhs_number_de.asp?shownav=0">here</a>
 * @author Nick Jones
 *
 */
public class NHSNumber {
	private final static int NHS_NUM_LENGTH = 10;
	private String nhsNumber;
	private List<String> validationErrors = new ArrayList<>();
	
	public NHSNumber(String nhsNumber){
		this.nhsNumber = nhsNumber;
	}
	
	public boolean isValid(){
		boolean isValid = true;
		if (null == nhsNumber){
			validationErrors.add("NHS Number not present");
			return false;
		}
		
		if (NHS_NUM_LENGTH != nhsNumber.length()){
			validationErrors.add("NHS Number must be " + NHS_NUM_LENGTH + " digits in length");
			return false;
		}

		Long nhs = null;
		try {
			nhs = Long.parseLong(nhsNumber);
		} catch (NumberFormatException e) {
			validationErrors.add("NHS Number not numeric");
			return false;
		}
		
		if (0 > nhs){
			validationErrors.add("NHS Number cannot be a negative number");
			return false;
		}
		
		int checkDigit = getMod11CheckDigit();
		
		if (10 == checkDigit ){
			validationErrors.add("NHS Number has Invalid Check Digit");
			return false;

		}

		if (Character.getNumericValue(nhsNumber.charAt(NHS_NUM_LENGTH-1)) != checkDigit ){
			validationErrors.add("NHS Number has Invalid Check Digit");
			return false;

		}

		return isValid;
	}
	

	public List<String> getValidationErrors(){
		return validationErrors;
	}

	
	private int getMod11CheckDigit() {
		// By the time this method is called, we know we are dealing with a 
		// correctly formatted NHS Number, so it's safe to not handle any runtime
		// exceptions when moving between string and int etc.
		
		int tmpTotal = 0;
		for ( int i = 0, j=nhsNumber.length(); i < nhsNumber.length() - 1; i++, j--){
			tmpTotal += Character.getNumericValue(nhsNumber.charAt(i)) * j;
		}
		
		int remainder = tmpTotal % 11;
		
		int checkDigit = 11 - remainder;
		
		if ( 11 == checkDigit ){
			checkDigit = 0;
		}
		
		return checkDigit;
	}

}
