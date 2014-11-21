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

package uk.co.inhealthcare.open.smsc.process;

import ca.uhn.hl7v2.model.v24.message.ADT_A05;

public class MessageProperties {

	public String getConversationId() {
		return conversationId;
	}
	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getEncodedMSH() {
		return encodedMSH;
	}
	public void setEncodedMSH(String encodedMSH) {
		this.encodedMSH = encodedMSH;
	}
	public String getTriggerEvent() {
		return triggerEvent;
	}
	public void setTriggerEvent(String triggerEvent) {
		this.triggerEvent = triggerEvent;
	}
	public ADT_A05 getInboundMessage() {
		return inboundMessage;
	}
	public void setInboundMessage(ADT_A05 inboundMessage) {
		this.inboundMessage = inboundMessage;
	}

	private String conversationId;
	private String messageId;
	private String triggerEvent;
	private String encodedMSH;
	private ADT_A05 inboundMessage;
	
}
