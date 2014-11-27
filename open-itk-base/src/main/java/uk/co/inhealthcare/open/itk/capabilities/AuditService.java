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
package uk.co.inhealthcare.open.itk.capabilities;


/**
 * 
 * ITK requires clear auditing of messages to provide an end to end audit trail. 
 * This service provides a simple API to the ITK implementation allowing message creation and receipt to be audited
 * at the ITK and Transport layers (currently just SOAP)
 * 
 * @author Nick Jones
 * 
 */
public interface AuditService {
	
	public final static String ITKREQUEST = "ITKREQUEST";
	public final static String ITKRESPONSE = "ITKRESPONSE";
	public final static String SOAPREQUEST = "SOAPREQUEST";
	public final static String SOAPRESPONSE = "SOAPRESPONSE";
	public final static String FAILURE = "FAILURE";
	
	/**
	 * 
	 * Audit the ITK Message being sent
	 * 
	 * @param auditDetails
	 * @throws AuditException
	 */
	public void auditITKRequest(ITKAuditDetails auditDetails) throws AuditException;
	
	/**
	 * 
	 * Audit the ITK Response
	 * 
	 * @param auditDetails
	 * @throws AuditException
	 */
	public void auditITKResponse(ITKAuditDetails auditDetails) throws AuditException;
	
	/**
	 * 
	 * Audit the SOAP Request
	 * 
	 * @param auditDetails
	 * @throws AuditException
	 */
	public void auditSOAPRequest(SOAPAuditDetails auditDetails) throws AuditException;
	
	/**
	 * 
	 * Audit the SOAP Response
	 * 
	 * @param auditDetails
	 * @throws AuditException
	 */
	public void auditSOAPResponse(SOAPAuditDetails auditDetails) throws AuditException;
	
	/**
	 * 
	 * Audit a Failure
	 * 
	 * @param auditDetails
	 * @throws AuditException
	 */
	public void auditFailure(ITKAuditDetails auditDetails) throws AuditException;

}
