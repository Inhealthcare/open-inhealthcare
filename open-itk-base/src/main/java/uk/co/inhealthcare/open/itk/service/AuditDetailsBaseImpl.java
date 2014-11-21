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

import uk.co.inhealthcare.open.itk.capabilities.AuditDetails;

public abstract class AuditDetailsBaseImpl implements AuditDetails {

	@Override
	public String getConversationId() {
		return conversationId;
	}
	@Override
	public String getTimestamp() {
		return timestamp;
	}
	@Override
	public String getType() {
		return type;
	}
	@Override
	public String getStatus() {
			return status;
	}
	@Override
	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}
	@Override
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public void setStatus(String status) {
		this.status = status;
	}
	private String conversationId;
	private String timestamp;
	private String type;
	private String status;
	
	
}
