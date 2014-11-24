/**
 * TODO: Add licence for test code - presume proprietary?
 */
package uk.co.inhealthcare.open.smsc.process;

import java.util.ArrayList;

import uk.co.inhealthcare.open.jsat.logging.ProcessLoggingService;
import uk.co.inhealthcare.open.smsc.messages.logging.LoggingException;

public class ProcessLoggingService_Mock implements ProcessLoggingService {

	public final static String SMSP_REQUEST_ERROR = "SMSPREQUESTERROR";
	public final static String SMSP_RESPONSE_ERROR = "SMSPRESPONSEERROR";
	public final static String SMSC_INPUT_ERROR = "SMSCINPUTERROR";
	public final static String SMSC_OUTCOME_ERROR = "SMSCOUTCOMEERROR";

	public final static String FAIL = "FAIL";

	private String smscInputOutcome;
	public void primeSmscInputOutcome(String smscInputOutcome) {
		this.smscInputOutcome = smscInputOutcome;
	}
	private String smscOutcomeOutcome;
	public void primeSmscOutcomeOutcome(String smscOutcomeOutcome) {
		this.smscOutcomeOutcome = smscOutcomeOutcome;
	}
	public ArrayList<String> parm_inputConversationId = new ArrayList<String>();
	public ArrayList<DemographicUpdate> parm_DemographicUpdate = new ArrayList<DemographicUpdate>();

	public ArrayList<String> parm_outcomeConversationId = new ArrayList<String>();
	public ArrayList<String> parm_Outcome = new ArrayList<String>();

	@Override
	public void logSMSCProcessInput(String conversationId, DemographicUpdate demoUpdate) throws LoggingException {
		if ((smscInputOutcome!=null) &&(smscInputOutcome.equals(FAIL))){
			throw new LoggingException(SMSC_INPUT_ERROR);
		}
		parm_inputConversationId.add(conversationId);
		parm_DemographicUpdate.add(demoUpdate);
	}

	@Override
	public void logSMSCProcessOutcome(String conversationId, String outcome) throws LoggingException {
		if ((smscOutcomeOutcome!=null) &&(smscOutcomeOutcome.equals(FAIL))){
			throw new LoggingException(SMSC_OUTCOME_ERROR);
		}
		parm_outcomeConversationId.add(conversationId);
		parm_Outcome.add(outcome);
	}

	
}
