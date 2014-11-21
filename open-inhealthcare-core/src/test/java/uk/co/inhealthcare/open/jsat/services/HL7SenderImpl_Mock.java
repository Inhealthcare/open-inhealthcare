package uk.co.inhealthcare.open.jsat.services;

import uk.co.inhealthcare.open.jsat.services.HL7Sender;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;

public class HL7SenderImpl_Mock implements HL7Sender {

	public final static String HL7_EXCEPTION = "HL7EX";
	private String exceptionType = "";
	public void primeResponse(String exceptionType){
		this.exceptionType = exceptionType;
	}
	
	public Message parm_hl7Msg = null;
	
	public HL7SenderImpl_Mock(){
	}
	
	public void process(Message hl7Msg) throws HL7Exception {
		if (exceptionType.equals(HL7_EXCEPTION)) throw new HL7Exception("Primed");
		
		this.parm_hl7Msg = hl7Msg;
	}

}
