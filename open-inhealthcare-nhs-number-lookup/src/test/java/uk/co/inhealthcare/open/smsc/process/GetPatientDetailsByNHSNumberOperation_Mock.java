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

import uk.co.inhealthcare.open.jsat.ConfigurationException;
import uk.co.inhealthcare.open.jsat.ValidationException;
import uk.co.inhealthcare.open.smsc.entity.Person;
import uk.co.inhealthcare.open.smsc.messages.GetPatientDetailsByNHSNumberRequest;
import uk.co.inhealthcare.open.smsc.messages.GetPatientDetailsResponse;
import uk.co.inhealthcare.open.smsc.messages.logging.LoggingException;
import uk.co.inhealthcare.open.smsc.operation.GetPatientDetailsByNHSNumberOperation;

/**
 * (MOCK) This class returns a GetNHSNumberResponse using the information primed in the service object.
 * 
 * @author Nick Jones
 *
 */
public class GetPatientDetailsByNHSNumberOperation_Mock extends GetPatientDetailsByNHSNumberOperation { 
	
	public final static String VALIDATION_EXCEPTION = "VEX";
	public final static String CONFIGURATION_EXCEPTION = "CEX";
	public final static String LOGGING_EXCEPTION = "LEX";

	private String responseCode = "SMSP-0000";
	private String nhsNumber = "9999345201";
	private String exceptionType = "";
	private boolean verifiedIndicator = true;
			
	public void primeResponse(String responseCode, String nhsNumber, boolean verifiedIndicator, String exceptionType){
		this.responseCode = responseCode;
		this.nhsNumber = nhsNumber;
		this.verifiedIndicator = verifiedIndicator;
		this.exceptionType = exceptionType;
	}
	
	/**
	 * (MOCK) Process the Get Patient Details By NHS Number Request
	 * 
	 * @param request the Get Patient Details By NHS Number Request
	 * @return the response from GetPatientDetailsByNHSNumber as a java object
	 * @throws ValidationException
	 * @throws ConfigurationException
	 * @throws LoggingException
	 */
	public GetPatientDetailsResponse process(GetPatientDetailsByNHSNumberRequest request) 
			throws ValidationException, ConfigurationException, LoggingException {

		if (exceptionType.equals(VALIDATION_EXCEPTION)) throw new ValidationException("Primed");
		if (exceptionType.equals(CONFIGURATION_EXCEPTION)) throw new ConfigurationException("Primed");
		if (exceptionType.equals(LOGGING_EXCEPTION)) throw new LoggingException("Primed");

		GetPatientDetailsResponse response = new GetPatientDetailsResponse(responseCode,request.getConversationId());
		Person person = new Person();
		person.setNhsNumber(this.nhsNumber);
		response.setPerson(person);
		return response;
		
	}

}
