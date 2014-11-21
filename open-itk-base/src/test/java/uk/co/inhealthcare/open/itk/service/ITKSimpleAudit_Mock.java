package uk.co.inhealthcare.open.itk.service;
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


import java.util.ArrayList;

import uk.co.inhealthcare.open.itk.capabilities.AuditDetails;
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
public class ITKSimpleAudit_Mock implements AuditService {

	private static final ITKSimpleAudit_Mock _INSTANCE = new ITKSimpleAudit_Mock();
	
	private String responseType = "OK";
	private final static String FAIL = "FAIL_";

	public final static String FAIL_ITKREQUEST = "FAIL_ITKREQUEST";
	public final static String FAIL_ITKRESPONSE = "FAIL_ITKRESPONSE";
	public final static String FAIL_SOAPREQUEST = "FAIL_SOAPREQUEST";
	public final static String FAIL_SOAPRESPONSE = "FAIL_SOAPRESPONSE";
	public final static String FAIL_FAILURE = "FAIL_FAILURE";

	public void primeResponse(String responseType) {
		this.responseType = responseType;
	}
	
	public ArrayList<String> parm_requestAuditType = new ArrayList<String>();
	public ArrayList<AuditDetails> parm_requestAuditDetails = new ArrayList<AuditDetails>();
	private void auditRequestWrite(String auditType, AuditDetails auditDetails) throws AuditException {
		if (responseType.equals(FAIL+auditType)) throw new AuditException("PRIMED TO FAIL");
		parm_requestAuditType.add(auditType);
		parm_requestAuditDetails.add(auditDetails);
	}
	
	@Override
	public void auditITKRequest(ITKAuditDetails auditDetails) throws AuditException {
		this.auditRequestWrite(ITKREQUEST, auditDetails);
	}

	@Override
	public void auditITKResponse(ITKAuditDetails auditDetails) throws AuditException {
		this.auditRequestWrite(ITKRESPONSE, auditDetails);
	}
	
	public void auditSOAPRequest(SOAPAuditDetails auditDetails) throws AuditException {
		this.auditRequestWrite(SOAPREQUEST, auditDetails);
	}

	@Override
	public void auditSOAPResponse(SOAPAuditDetails auditDetails) throws AuditException {
		this.auditRequestWrite(SOAPRESPONSE, auditDetails);
	}

	@Override
	public void auditFailure(ITKAuditDetails auditDetails) throws AuditException {
		this.auditRequestWrite(FAILURE, auditDetails);
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
