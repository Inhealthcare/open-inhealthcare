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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.inhealthcare.open.itk.capabilities.AuditException;
import uk.co.inhealthcare.open.itk.capabilities.AuditService;
import uk.co.inhealthcare.open.itk.capabilities.ITKAuditDetails;
import uk.co.inhealthcare.open.itk.capabilities.SOAPAuditDetails;

/**
 * The Class ITKSimpleAudit. 
 * This class is a fallback to audit to the current log file when there is no Audit Service defined.
 *
 * @author Nick Jones
 */
public class ITKSimpleAuditImpl implements AuditService {

	private final static Logger logger = LoggerFactory.getLogger(ITKSimpleAuditImpl.class);
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS Z");
	private static final ITKSimpleAuditImpl _INSTANCE = new ITKSimpleAuditImpl();
	
    public ITKSimpleAuditImpl(){
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

	private void auditITKWrite(String auditType, ITKAuditDetails auditDetails) throws AuditException {
		
		logger.warn("Auditing using default ITKSimpleAudit. Consider implementing an ITK Audit Service");
		logger.info(DATE_FORMAT.format(Calendar.getInstance().getTime()) + " [AUDIT:"+ auditType + "] " + 
				"[CONVID]"+auditDetails.getConversationId()+
				"[ITK:TRKID]"+auditDetails.getTrackingId()+
				"[ITK:PAYLID]"+auditDetails.getPayloadId()+
				"[ITK:SVCID]"+auditDetails.getServiceId()+
				"[ITK:PRFID]"+auditDetails.getProfileId()+
				"[PAT:NHSNO]"+auditDetails.getNhsNumber()+
				"[PAT:LOCAL]"+auditDetails.getLocalPatientId()+
				"[AUD:LOCAL]"+auditDetails.getLocalAuditId()+
				"[AUD:SPUSRID]"+auditDetails.getSpineUserId()+
				"[AUD:SPRPID]"+auditDetails.getSpineRoleProfileId()+
				"[AUD:SPRLID]"+auditDetails.getSpineRoleId()+
				"[AUD:SDRADDR]"+auditDetails.getSenderAddress()+
				"[STATUS]"+auditDetails.getStatus()+
				"");
	}
	
	private void auditSOAPWrite(String auditType, SOAPAuditDetails auditDetails) throws AuditException {
		
		logger.warn("Auditing using default ITKSimpleAudit. Consider implementing an ITK Audit Service");
		logger.info(DATE_FORMAT.format(Calendar.getInstance().getTime()) + " [AUDIT:"+ auditType + "] " + 
				"[CONVID]"+auditDetails.getConversationId()+
				"[TRANS:MSGID]"+auditDetails.getMessageId()+
				"[TRANS:CREATE]"+auditDetails.getCreationTime()+
				"[TRANS:TO]"+auditDetails.getTo()+
				"[TRANS:ACTION]"+auditDetails.getAction()+
				"[TRANS:USER]"+auditDetails.getUserId()+
				"[STATUS]"+auditDetails.getStatus()+
				"");
	}
	@Override
	public void auditITKRequest(ITKAuditDetails auditDetails) throws AuditException {
		this.auditITKWrite(ITKREQUEST, auditDetails);
	}

	@Override
	public void auditITKResponse(ITKAuditDetails auditDetails) throws AuditException {
		this.auditITKWrite(ITKRESPONSE, auditDetails);
	}
	public void auditSOAPRequest(SOAPAuditDetails auditDetails) throws AuditException {
		
		this.auditSOAPWrite(SOAPREQUEST, auditDetails);
	}

	@Override
	public void auditSOAPResponse(SOAPAuditDetails auditDetails) throws AuditException {
		this.auditSOAPWrite(SOAPRESPONSE, auditDetails);
	}

	@Override
	public void auditFailure(ITKAuditDetails auditDetails) throws AuditException {
		this.auditITKWrite(FAILURE, auditDetails);
	}

	/**
	 * Gets the single instance of ITKSimpleAudit.
	 *
	 * @return single instance of ITKSimpleAudit
	 */
	public static final AuditService getInstance() {
		return _INSTANCE;
	}

}
