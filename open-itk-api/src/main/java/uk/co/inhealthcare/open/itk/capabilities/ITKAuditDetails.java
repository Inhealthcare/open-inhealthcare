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
 * Interface representing the pertinent information for auditing an ITK Message.
 * 
 * @author Nick Jones
 *
 */
public interface ITKAuditDetails extends AuditDetails {
	
	// Message Ids
	public String getTrackingId();
	public String getPayloadId();
	
	// Patient Ids
	public String getNhsNumber();
	public String getLocalPatientId();
	
	// Audit Ids
	public String getLocalAuditId();
	public String getSpineUserId();
	public String getSpineRoleProfileId();
	public String getSpineRoleId();

	public String getSenderAddress();

	// Service Id
	public String getServiceId();
	public String getProfileId();
	
	// SETTERS
	// Message Ids
	public void setConversationId(String value);
	public void setTrackingId(String value);
	public void setPayloadId(String value);

	// Patient Ids
	public void setNhsNumber(String value);
	public void setLocalPatientId(String value);
	
	// Audit Ids
	public void setLocalAuditId(String value);
	public void setSpineUserId(String value);
	public void setSpineRoleProfileId(String value);
	public void setSpineRoleId(String value);

	public void setSenderAddress(String value);

	// Service Id
	public void setServiceId(String value);
	public void setProfileId(String value);

}
