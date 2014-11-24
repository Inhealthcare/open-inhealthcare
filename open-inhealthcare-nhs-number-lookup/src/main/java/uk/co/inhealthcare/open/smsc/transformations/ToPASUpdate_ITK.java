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

package uk.co.inhealthcare.open.smsc.transformations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.inhealthcare.open.jsat.TransformationException;
import uk.co.inhealthcare.open.jsat.utils.HL7Helper;
import uk.co.inhealthcare.open.smsc.messages.GetPatientDetailsResponse;
import uk.co.inhealthcare.open.smsc.transformations.api.ToPASUpdate;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.datatype.CX;
import ca.uhn.hl7v2.model.v24.message.ADT_A05;
import ca.uhn.hl7v2.util.Terser;

/**
 * 
 * @author Nick Jones
 * 
 * This implementation assumes the incoming message is already in ITK format and just
 * updates the NHS Number status and NHS Number accordingly.
 * 
 * The Message Headers are set appropriately
 *
 */
public class ToPASUpdate_ITK implements ToPASUpdate {
	
	private String sendingApplication = "JSAT";
	private String sendingFacility = "";
	private String receivingApplication = "ROUTE";
	private String receivingFacility = "";
	private String messageType = "ADT";
	private String eventType = "A31";
	private String messageStructure = "ADT_A05";
	
	
	public void setSendingApplication(String sendingApplication) {
		this.sendingApplication = sendingApplication;
	}

	public void setSendingFacility(String sendingFacility) {
		this.sendingFacility = sendingFacility;
	}

	public void setReceivingApplication(String receivingApplication) {
		this.receivingApplication = receivingApplication;
	}

	public void setReceivingFacility(String receivingFacility) {
		this.receivingFacility = receivingFacility;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public void setMessageStructure(String messageStructure) {
		this.messageStructure = messageStructure;
	}

	private final static Logger logger = LoggerFactory.getLogger(ToPASUpdate_ITK.class);

	public Message getPASUpdateMessage(ADT_A05 updateMsg, GetPatientDetailsResponse gpdResp, String nhsNumberStatus)
			throws TransformationException
	{
		throw new TransformationException("Not Implemented");
	}

	public Message getPASUpdateMessage(ADT_A05 updateMsg, String nhsNumber, String nhsNumberStatus) 
			throws TransformationException
	{

		try {

			// Set the Message Creation Date/Time
	        Terser terser = new Terser(updateMsg);
			terser.set("/MSH-3",sendingApplication);
			terser.set("/MSH-4",sendingFacility);
			terser.set("/MSH-5",receivingApplication);
			terser.set("/MSH-6",receivingFacility);
			terser.set("/MSH-7", HL7Helper.getCreationDate());
			terser.set("/MSH-9-1",messageType);
			terser.set("/MSH-9-2",eventType);
			terser.set("/MSH-9-3",messageStructure);
			terser.set("/MSH-10", HL7Helper.getMessageId());
				
			updateMsg.getPID().getPid32_IdentityReliabilityCode(0).setValue(nhsNumberStatus);

			int nhsNoPos = findNhsNumberId(updateMsg);
			int idCnt = updateMsg.getPID().getPatientIdentifierList().length;
			
			if (nhsNumber.length()>0){
				// NHS Number required in message
				if (nhsNoPos > -1){
					// If present - update
					buildNhsNumberId(nhsNumber, updateMsg.getPID().getPatientIdentifierList()[nhsNoPos]);
				} else {
					// If not - add to end
					buildNhsNumberId(nhsNumber, updateMsg.getPID().insertPatientIdentifierList(idCnt));
				}
			} else {
				// NHS Number not required in message
				if (nhsNoPos > -1){
					// If present - remove
					updateMsg.getPID().removePatientIdentifierList(nhsNoPos);
				} else {
					// If not - do nothing
				}
			}
			
		} catch (DataTypeException e) {
			logger.error("DataTypeException building PAS Update.",e);
		} catch (HL7Exception e) {
			logger.error("HL7Exception building PAS Update.",e);
		}
		
		return updateMsg;

	}
	private int findNhsNumberId(ADT_A05 updateMsg){
		CX[] patIds = updateMsg.getPID().getPatientIdentifierList();
		int nhsNoPos = -1;
		for (int i=0; i < patIds.length; i++){
			CX patId = patIds[i];
			String assAuth = patId.getAssigningAuthority().getNamespaceID().getValue();
			if ((assAuth!=null) && (assAuth.equalsIgnoreCase("NHS"))){
				nhsNoPos = i;
			}
		}
		return nhsNoPos;
	}
	private void buildNhsNumberId(String nhsNumber, CX nhsNoCX) throws DataTypeException {
		nhsNoCX.getCx1_ID().setValue(nhsNumber);
		nhsNoCX.getCx4_AssigningAuthority().getNamespaceID().setValue("NHS");
	}
}
