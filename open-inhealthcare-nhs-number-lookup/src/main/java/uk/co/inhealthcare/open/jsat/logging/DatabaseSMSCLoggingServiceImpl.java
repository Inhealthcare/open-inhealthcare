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

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import uk.co.inhealthcare.open.smsc.messages.logging.LoggingException;
import uk.co.inhealthcare.open.smsc.messages.logging.SMSCInput;
import uk.co.inhealthcare.open.smsc.messages.logging.SMSCLoggingDBConnectionFactory;
import uk.co.inhealthcare.open.smsc.messages.logging.SMSCOutcome;
import uk.co.inhealthcare.open.smsc.messages.logging.SMSPRequestLog;
import uk.co.inhealthcare.open.smsc.messages.logging.SMSPResponseLog;
import uk.co.inhealthcare.open.smsc.process.DemographicUpdate;

/**
 * The Class ITKSimpleAudit. 
 * This class is a fallback to audit to the current log file when there is no Audit Service defined.
 *
 * @author Nick Jones
 * @since 0.1
 */
public class DatabaseSMSCLoggingServiceImpl implements ProcessLoggingService {

	// private final static Logger logger = LoggerFactory.getLogger(DatabaseSMSCLoggingServiceImpl.class);
	
	/** The Constant DATE_FORMAT. */
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS Z");
	
    private SqlSessionFactory sqlSessionFactory;
	
    public DatabaseSMSCLoggingServiceImpl(){
        sqlSessionFactory = SMSCLoggingDBConnectionFactory.getSqlSessionFactory();
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
   }

	@Override
	public void logSMSCProcessOutcome(String conversationId, String outcome) throws LoggingException {
		String timestamp = DATE_FORMAT.format(Calendar.getInstance().getTime());
		SMSCOutcome outcomeDetails = new SMSCOutcome();
		outcomeDetails.setConversationId(conversationId);
		outcomeDetails.setOutcome(outcome);
		outcomeDetails.setTimestamp(timestamp);
		this.insertSMSCOutcome(outcomeDetails);
	}

	@Override
	public void logSMSCProcessInput(String conversationId, DemographicUpdate demoUpdate) throws LoggingException {
		String timestamp = DATE_FORMAT.format(Calendar.getInstance().getTime());
		SMSCInput input = new SMSCInput();
		input.setConversationId(conversationId);
		input.setRequest(demoUpdate.toXml());
		input.setTimestamp(timestamp);
		this.insertSMSCInput(input);
	}
	
    /**
     * Insert an instance of SMSCInput into the database.
     * @param inputDetails the instance to be persisted.
     */
    private void insertSMSCInput(SMSCInput inputDetails){
 
        SqlSession session = sqlSessionFactory.openSession();
 
        try {
            session.insert("SMSCInput.insert", inputDetails);
            session.commit();
        } finally {
            session.close();
        }
    }		
    /**
     * Insert an instance of SMSCOutcome into the database.
     * @param outcomeDetails the instance to be persisted.
     */
    private void insertSMSCOutcome(SMSCOutcome outcomeDetails){
 
        SqlSession session = sqlSessionFactory.openSession();
 
        try {
            session.insert("SMSCOutcome.insert", outcomeDetails);
            session.commit();
        } finally {
            session.close();
        }
    }		
    /**
     * Insert an instance of SMSPRequest into the database.
     * @param smspRequest the instance to be persisted.
     */
    private void insertSMSPRequest(SMSPRequestLog smspRequest){
 
        SqlSession session = sqlSessionFactory.openSession();
 
        try {
            session.insert("SMSPRequest.insert", smspRequest);
            session.commit();
        } finally {
            session.close();
        }
    }		
    /**
     * Insert an instance of SMSPResponse into the database.
     * @param smspRequest the instance to be persisted.
     */
    private void insertSMSPResponse(SMSPResponseLog smspResponse){
 
        SqlSession session = sqlSessionFactory.openSession();
 
        try {
            session.insert("SMSPResponse.insert", smspResponse);
            session.commit();
        } finally {
            session.close();
        }
    }		

}
