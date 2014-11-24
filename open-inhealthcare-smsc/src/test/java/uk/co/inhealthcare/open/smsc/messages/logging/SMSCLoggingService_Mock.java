/**
 * TODO: Add licence for test code - presume proprietary?
 */
package uk.co.inhealthcare.open.smsc.messages.logging;

import java.util.ArrayList;

import uk.co.inhealthcare.open.smsc.messages.SMSPRequest;
import uk.co.inhealthcare.open.smsc.messages.SMSPResponse;

public class SMSCLoggingService_Mock implements SMSCLoggingService {

	public final static String SMSP_REQUEST_ERROR = "SMSPREQUESTERROR";
	public final static String SMSP_RESPONSE_ERROR = "SMSPRESPONSEERROR";
	public final static String SMSC_INPUT_ERROR = "SMSCINPUTERROR";
	public final static String SMSC_OUTCOME_ERROR = "SMSCOUTCOMEERROR";

	public final static String FAIL = "FAIL";
	private String smspResponseOutcome;

	public void primeSmspResponseOutcome(String smspResponseOutcome) {
		this.smspResponseOutcome = smspResponseOutcome;
	}

	private String smspRequestOutcome;

	public void primeSmspRequestOutcome(String smspRequestOutcome) {
		this.smspRequestOutcome = smspRequestOutcome;
	}

	private String smscInputOutcome;

	public void primeSmscInputOutcome(String smscInputOutcome) {
		this.smscInputOutcome = smscInputOutcome;
	}

	private String smscOutcomeOutcome;

	public void primeSmscOutcomeOutcome(String smscOutcomeOutcome) {
		this.smscOutcomeOutcome = smscOutcomeOutcome;
	}

	public ArrayList<String> parm_inputConversationId = new ArrayList<String>();

	public ArrayList<String> parm_outcomeConversationId = new ArrayList<String>();
	public ArrayList<String> parm_Outcome = new ArrayList<String>();

	public ArrayList<SMSPRequest> parm_SMSPRequest = new ArrayList<SMSPRequest>();
	public ArrayList<SMSPResponse> parm_SMSPResponse = new ArrayList<SMSPResponse>();

	@Override
	public void logSMSPRequest(SMSPRequest request) throws LoggingException {
		if ((smspRequestOutcome != null) && (smspRequestOutcome.equals(FAIL))) {
			throw new LoggingException(SMSP_REQUEST_ERROR);
		}
		parm_SMSPRequest.add(request);
	}

	@Override
	public void logSMSPResponse(SMSPResponse response) throws LoggingException {
		if ((smspResponseOutcome != null) && (smspResponseOutcome.equals(FAIL))) {
			throw new LoggingException(SMSP_RESPONSE_ERROR);
		}
		parm_SMSPResponse.add(response);
	}

}