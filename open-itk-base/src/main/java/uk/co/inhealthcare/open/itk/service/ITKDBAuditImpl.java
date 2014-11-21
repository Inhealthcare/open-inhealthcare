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
package uk.co.inhealthcare.open.itk.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import uk.co.inhealthcare.open.itk.capabilities.AuditException;
import uk.co.inhealthcare.open.itk.capabilities.AuditService;
import uk.co.inhealthcare.open.itk.capabilities.ITKAuditDetails;
import uk.co.inhealthcare.open.itk.capabilities.SOAPAuditDetails;

/**
 * The Class ITKDBAuditImpl 
 *
 * @author Nick Jones
 */
public class ITKDBAuditImpl implements AuditService {

	//private final static Logger logger = LoggerFactory.getLogger(ITKDBAuditImpl.class);
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS Z");
	private static final ITKDBAuditImpl _INSTANCE = new ITKDBAuditImpl();
    private SqlSessionFactory sqlSessionFactory;
	 
    public ITKDBAuditImpl(){
        sqlSessionFactory = AuditDBConnectionFactory.getSqlSessionFactory();
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
	
	@Override
	public void auditITKRequest(ITKAuditDetails auditDetails) throws AuditException {
		String timestamp = DATE_FORMAT.format(Calendar.getInstance().getTime());
		auditDetails.setType(ITKREQUEST);
		auditDetails.setTimestamp(timestamp);
		this.insertITKAudit(auditDetails);
	}

	@Override
	public void auditITKResponse(ITKAuditDetails auditDetails) throws AuditException {
		String timestamp = DATE_FORMAT.format(Calendar.getInstance().getTime());
		auditDetails.setType(ITKRESPONSE);
		auditDetails.setTimestamp(timestamp);
		this.insertITKAudit(auditDetails);
	}
	public void auditSOAPRequest(SOAPAuditDetails auditDetails) throws AuditException {
		String timestamp = DATE_FORMAT.format(Calendar.getInstance().getTime());
		auditDetails.setType(SOAPREQUEST);
		auditDetails.setTimestamp(timestamp);
		this.insertSOAPAudit(auditDetails);
	}

	@Override
	public void auditSOAPResponse(SOAPAuditDetails auditDetails) throws AuditException {
		String timestamp = DATE_FORMAT.format(Calendar.getInstance().getTime());
		auditDetails.setType(SOAPRESPONSE);
		auditDetails.setTimestamp(timestamp);
		this.insertSOAPAudit(auditDetails);
	}

	@Override
	public void auditFailure(ITKAuditDetails auditDetails) throws AuditException {
		String timestamp = DATE_FORMAT.format(Calendar.getInstance().getTime());
		auditDetails.setType(FAILURE);
		auditDetails.setTimestamp(timestamp);
		this.insertITKAudit(auditDetails);
	}

	/**
	 * Gets the single instance of ITKSimpleAudit.
	 *
	 * @return single instance of ITKSimpleAudit
	 */
	public static final AuditService getInstance() {
		return _INSTANCE;
	}
    /**
     * Insert an instance of ITKAuditDetails into the database.
     * @param auditDetails the instance to be persisted.
     */
    private void insertITKAudit(ITKAuditDetails auditDetails){
 
        SqlSession session = sqlSessionFactory.openSession();
 
        try {
            session.insert("ITKAudit.insert", auditDetails);
            session.commit();
        } finally {
            session.close();
        }
    }
    /**
     * Insert an instance of SOAPAuditDetails into the database.
     * @param auditDetails the instance to be persisted.
     */
    private void insertSOAPAudit(SOAPAuditDetails auditDetails){
 
        SqlSession session = sqlSessionFactory.openSession();
 
        try {
            session.insert("SOAPAudit.insert", auditDetails);
            session.commit();
        } finally {
            session.close();
        }
    }

}
