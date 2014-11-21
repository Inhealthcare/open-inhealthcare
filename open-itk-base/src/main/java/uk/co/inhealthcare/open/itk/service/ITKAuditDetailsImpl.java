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

import uk.co.inhealthcare.open.itk.capabilities.ITKAuditDetails;

public class ITKAuditDetailsImpl extends AuditDetailsBaseImpl 
								 implements ITKAuditDetails {

	@Override 
	public String getTrackingId() {
		return trackingId;
	}
	@Override 
	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}
	@Override 
	public String getPayloadId() {
		return payloadId;
	}
	@Override 
	public void setPayloadId(String payloadId) {
		this.payloadId = payloadId;
	}
	@Override 
	public String getNhsNumber() {
		return nhsNumber;
	}
	@Override 
	public void setNhsNumber(String nhsNumber) {
		this.nhsNumber = nhsNumber;
	}
	@Override 
	public String getLocalPatientId() {
		return localPatientId;
	}
	@Override 
	public void setLocalPatientId(String localPatientId) {
		this.localPatientId = localPatientId;
	}
	@Override 
	public String getLocalAuditId() {
		return localAuditId;
	}
	@Override 
	public void setLocalAuditId(String localAuditId) {
		this.localAuditId = localAuditId;
	}
	@Override 
	public String getSpineUserId() {
		return spineUserId;
	}
	@Override 
	public void setSpineUserId(String spineUserId) {
		this.spineUserId = spineUserId;
	}
	@Override 
	public String getSpineRoleProfileId() {
		return spineRoleProfileId;
	}
	@Override 
	public void setSpineRoleProfileId(String spineRoleProfileId) {
		this.spineRoleProfileId = spineRoleProfileId;
	}
	@Override 
	public String getSpineRoleId() {
		return spineRoleId;
	}
	@Override 
	public void setSpineRoleId(String spineRoleId) {
		this.spineRoleId = spineRoleId;
	}
	@Override 
	public String getSenderAddress() {
		return senderAddress;
	}
	@Override 
	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}
	@Override 
	public String getServiceId() {
		return serviceId;
	}
	@Override 
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	@Override 
	public String getProfileId() {
		return profileId;
	}
	@Override 
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	private String trackingId;
	private String payloadId;
	private String nhsNumber;
	private String localPatientId;
	private String localAuditId;
	private String spineUserId;
	private String spineRoleProfileId;
	private String spineRoleId;
	private String senderAddress;
	private String serviceId;
	private String profileId;

}
