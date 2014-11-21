package uk.co.inhealthcare.open.smsc.messages.logging;

public class SMSCOutcome {
	
 private String timestamp;
 private String conversationId;
 private String outcome;
 
public String getTimestamp() {
	return timestamp;
}
public void setTimestamp(String timestamp) {
	this.timestamp = timestamp;
}
public String getConversationId() {
	return conversationId;
}
public void setConversationId(String conversationId) {
	this.conversationId = conversationId;
}
public String getOutcome() {
	return outcome;
}
public void setOutcome(String outcome) {
	this.outcome = outcome;
}
 
}
