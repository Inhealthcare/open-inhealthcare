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

package uk.co.inhealthcare.open.jsat;

import java.util.UUID;

public abstract class ITKRequest extends JSATMessage {

	public String getMessageId() {
		return messageId;
	}

	public String getLocalAuditId() {
		return localAuditId;
	}

	public void setLocalAuditId(String localAuditId) {
		this.localAuditId = localAuditId;
	}

	public String getSpineUserId() {
		return spineUserId;
	}

	public void setSpineUserId(String spineUserId) {
		this.spineUserId = spineUserId;
	}

	public String getSpineRoleId() {
		return spineRoleId;
	}

	public void setSpineRoleId(String spineRoleId) {
		this.spineRoleId = spineRoleId;
	}

	public String getSpineRoleProfileId() {
		return spineRoleProfileId;
	}

	public void setSpineRoleProfileId(String spineRoleProfileId) {
		this.spineRoleProfileId = spineRoleProfileId;
	}

	protected String messageId;

	protected String localAuditId;
	protected String spineUserId;
	protected String spineRoleId;
	protected String spineRoleProfileId;
	
	public ITKRequest(){
		this.messageId = UUID.randomUUID().toString().toUpperCase();
	}
	
}
