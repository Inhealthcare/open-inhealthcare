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
package uk.co.inhealthcare.open.jsat.logging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.inhealthcare.open.smsc.messages.logging.LoggingException;
import uk.co.inhealthcare.open.smsc.process.DemographicUpdate;

/**
 * The Class ITKSimpleAudit. 
 * This class is a fallback to audit to the current log file when there is no Audit Service defined.
 *
 * @author Nick Jones
 * @since 0.1
 */
public class SimpleSMSCLoggingServiceImpl implements ProcessLoggingService {

	private final static Logger logger = LoggerFactory.getLogger(SimpleSMSCLoggingServiceImpl.class);
	
	/** The Constant DATE_FORMAT. */
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS Z");
	
    public SimpleSMSCLoggingServiceImpl(){
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
    
	public void logSMSCProcessOutcome(String conversationId, String outcome) throws LoggingException {
		
		logger.info("SMSP Logging using default SimpleSMSCLoggingServiceImpl. Consider implementing a database implementation");
		
		String logMessage = DATE_FORMAT.format(Calendar.getInstance().getTime()) + 
							"[CONVID]"+conversationId+
						    "[PROCESS]"+outcome+
						    "";
		logger.info(logMessage);
	
	}

	@Override
	public void logSMSCProcessInput(String conversationId, DemographicUpdate demoUpdate) throws LoggingException {
		
		logger.info("SMSP Logging using default SimpleSMSCLoggingServiceImpl. Consider implementing a database implementation");
		
		String logMessage = DATE_FORMAT.format(Calendar.getInstance().getTime()) + 
							"[CONVID]"+conversationId+
							"[DEMOUPDATE]"+demoUpdate.toXml()+
						    "";
		logger.info(logMessage);
		
	}
		

}
