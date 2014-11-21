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

import uk.co.inhealthcare.open.itk.capabilities.SOAPAuditDetails;

public class SOAPAuditDetailsImpl extends AuditDetailsBaseImpl 
								 implements SOAPAuditDetails {

	private String messageId;
	private String creationTime;
	private String to;
	private String action;
	private String userId;
	
	@Override
	public String getMessageId() {
		return messageId;
	}
	@Override
	public String getCreationTime() {
		return creationTime;
	}
	@Override
	public String getTo() {
		return to;
	}
	@Override
	public String getAction() {
		return action;
	}
	@Override
	public String getUserId() {
		return userId;
	}
	@Override
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	@Override
	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}
	@Override
	public void setTo(String to) {
		this.to = to;
	}
	@Override
	public void setAction(String action) {
		this.action = action;
	}
	@Override
	public void setUserId(String userId) {
		this.userId = userId;
	}

}
