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
import uk.co.inhealthcare.open.smsc.canonical.DemographicUpdate;
import uk.co.inhealthcare.open.smsc.transformations.api.A05ToDemographicUpdate;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v24.datatype.CX;
import ca.uhn.hl7v2.model.v24.datatype.XPN;
import ca.uhn.hl7v2.model.v24.message.ADT_A05;
import ca.uhn.hl7v2.util.Terser;

public class A05ToDemographicUpdate_ITK implements A05ToDemographicUpdate {
	
	private final static Logger logger = LoggerFactory.getLogger(A05ToDemographicUpdate_ITK.class);

	public DemographicUpdate getDemographicUpdate(ADT_A05 updateMsg) throws TransformationException {
		
		DemographicUpdate dto = new DemographicUpdate();
		
		// Data can be extracted using either the API or Terser - both shown to illustrate
        XPN patientName = updateMsg.getPID().getPatientName()[0];
        String familyName = safeString(patientName.getFamilyName().getSurname().getValue());
        String givenName = safeString(patientName.getGivenName().getValue());
        String dateOfBirth = safeString(updateMsg.getPID().getDateTimeOfBirth().getTimeOfAnEvent().getValue());
        String gender = safeString(updateMsg.getPID().getAdministrativeSex().getValue());
        Terser terser = new Terser(updateMsg);
        try {
			dto.setPostcode(safeString(terser.get("/PID-11-5")));
			dto.setNHSNumberStatus(safeString(terser.get("/PID-32")));
		} catch (HL7Exception e) {
			logger.error("TERSER ERROR reading postcode.",e);
			throw new TransformationException("Error extracting information from HL7."+e.getMessage());
		}

        // TRANSFORM THE GENDER - EXAMPLE OF TRUST CONFIG
        if (gender.equalsIgnoreCase("M")) gender="1";
        if (gender.equalsIgnoreCase("F")) gender="2";
        if (gender.equalsIgnoreCase("O")) gender="0";
        if (gender.equalsIgnoreCase("U")) gender="9";
        //
        
        // Other than specific data translations - don't validate the request. The SMSP client will validate and reject if appropriate.
        
		dto.setDateOfBirth(dateOfBirth);
		dto.setGivenName(givenName);
		dto.setFamilyName(familyName);
		dto.setGender(gender);
		dto.setNHSNumber(getNhsNumber(updateMsg));

		dto.setLocalPatientIdentifier(getLocalId(updateMsg));
		
		return dto;

	}
	private String safeString(String input){
		if (input==null) return "";
		return input;
	}
	private String getLocalId(ADT_A05 updateMsg){
        int idIdx=0;
        String localId = "";
        int pidCnt = updateMsg.getPID().getPatientIdentifierList().length;
        while (pidCnt > idIdx){
        	CX patId = updateMsg.getPID().getPatientIdentifierList(idIdx);
        	try {
            	if (patId.getCx4_AssigningAuthority().getNamespaceID().getValue().equals("RXX")){
            		localId = updateMsg.getPID().getPatientIdentifierList()[idIdx].getCx1_ID().getValue();
            	}
        	} catch (NullPointerException npex){
        		// Unexpected HL7 format - don't raise an error here - the SMSP client will validate and reject if appropriate
        	}
        	idIdx ++;
        }
        return localId;
		
	}
	private String getNhsNumber(ADT_A05 updateMsg){
        int idIdx=0;
        String nhsNumber = "";
        int pidCnt = updateMsg.getPID().getPatientIdentifierList().length;
        while (pidCnt > idIdx){
        	CX patId = updateMsg.getPID().getPatientIdentifierList(idIdx);
        	try {
	        	if (patId.getCx4_AssigningAuthority().getNamespaceID().getValue().equals("NHS")){
	        		nhsNumber = updateMsg.getPID().getPatientIdentifierList()[idIdx].getCx1_ID().getValue();
	        	}
        	} catch (NullPointerException npex){
        		// Unexpected HL7 format - don't raise an error here - the SMSP client will validate and reject if appropriate
        	}
        	idIdx ++;
        }
        return nhsNumber;
		
	}

}
