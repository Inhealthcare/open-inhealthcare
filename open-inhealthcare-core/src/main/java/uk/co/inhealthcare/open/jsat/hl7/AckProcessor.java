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

package uk.co.inhealthcare.open.jsat.hl7;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.parser.PipeParser;

public class AckProcessor {

	private final static Logger logger = LoggerFactory.getLogger(AckProcessor.class);
	
	public AckProcessor(){
		logger.debug("Starting BuildAck Processor.");
	}
	
	public Message process(Message hl7Msg) {
		
    	Message response = null;
		try {
	        String encodedMessage = new PipeParser().encode(hl7Msg);
	        logger.debug("Received message:\n" + encodedMessage);
	
			// Get key details and log
			MSH msh = (MSH) hl7Msg.get("MSH");
			String messageId = msh.getMessageControlID().getValue(); 
			String sourceApp = msh.getMsh3_SendingApplication().getNamespaceID().getValue();
			String sourceFacility = msh.getMsh4_SendingFacility().getNamespaceID().getValue();
			String messageType = msh.getMsh9_MessageType().getMessageType().getValue();
			String triggerEvent = msh.getMsh9_MessageType().getTriggerEvent().getValue();
			String messageStructure = msh.getMsh9_MessageType().getMessageStructure().getValue();
			String createdTime = msh.getMsh7_DateTimeOfMessage().getTimeOfAnEvent().getValue();
			
			// Build log message
			String logMsg = "[MSG:ID]"+messageId+"[MSG:TIME]"+createdTime+"[SRC:APP]"+sourceApp+"[SRC:FAC]"+sourceFacility+
							"[TYPE]"+messageType+":"+triggerEvent+":"+messageStructure;
			logger.info("Received Message:"+logMsg);
			
			// Send back an ACK. 
			try {
				response = hl7Msg.generateACK();
			} catch (IOException e) {
				logger.error("Failed to generate ACK.",e);
			}
			logger.info("Sending ACK for:"+messageId);
			
		} catch (HL7Exception hl7Ex){
			
			logger.error("JSAT Error generating an ACK:"+hl7Ex.getMessage());
			
		}
		
        return response;

	}


}
