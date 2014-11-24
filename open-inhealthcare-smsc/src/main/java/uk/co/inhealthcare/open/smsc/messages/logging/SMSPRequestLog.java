package uk.co.inhealthcare.open.smsc.messages.logging;

public class SMSPRequestLog {
	
 private String timestamp;
 private String conversationId;
 private String request;
 
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
public String getRequest() {
	return request;
}
public void setRequest(String request) {
	this.request = request;
}
 
}
