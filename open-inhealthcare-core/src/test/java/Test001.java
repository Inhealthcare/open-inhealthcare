import java.util.UUID;

import uk.co.inhealthcare.open.smsc.canonical.DemographicUpdate;
import uk.co.inhealthcare.open.smsc.messages.GetNHSNumberRequest;
import uk.co.inhealthcare.open.smsc.messages.GetNHSNumberResponse;
import uk.co.inhealthcare.open.smsc.messages.SMSPRequest;
import uk.co.inhealthcare.open.smsc.messages.SMSPResponse;
import uk.co.inhealthcare.open.smsc.messages.logging.DatabaseSMSCLoggingServiceImpl;
import uk.co.inhealthcare.open.smsc.messages.logging.LoggingException;
import uk.co.inhealthcare.open.smsc.messages.logging.SMSCLoggingService;

public class Test001 {

	public static void main(String[] args) {
		DemographicUpdate demoUpdate = new DemographicUpdate();
		demoUpdate.setDateOfBirth("19800101");
		demoUpdate.setFamilyName("JONES");
		String conversationId = UUID.randomUUID().toString().toUpperCase();

		GetNHSNumberRequest smspRequest = new GetNHSNumberRequest();
		smspRequest.setDateOfBirth("19820831");
		SMSPResponse smspResponse = new GetNHSNumberResponse("OK", conversationId);

		SMSCLoggingService logger = new DatabaseSMSCLoggingServiceImpl(); 
		try {
			logger.logSMSCProcessInput(conversationId, demoUpdate);
			logger.logSMSCProcessOutcome(conversationId, "OK");
			logger.logSMSPRequest(smspRequest);
			logger.logSMSPResponse(smspResponse);
		} catch (LoggingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
