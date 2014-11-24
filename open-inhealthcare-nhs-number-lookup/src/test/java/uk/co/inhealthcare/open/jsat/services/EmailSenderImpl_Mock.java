package uk.co.inhealthcare.open.jsat.services;

import uk.co.inhealthcare.open.jsat.AlertException;
import uk.co.inhealthcare.open.jsat.services.EmailSender;


public class EmailSenderImpl_Mock implements EmailSender {


	public final static String FAIL = "FAIL";
	
	public EmailSenderImpl_Mock(){
	}
	public String conversationId;
	public String headline;
	public String message;
	public String technicalContext;
	public String businessProcess;
	public String businessContext;
	
	private String outcome = "OK";
	
	public void primeOutcome(String outcome) {this.outcome = outcome;}
	
	@Override
	public void send(String conversationId, String headline, String message,
			String technicalContext, String businessProcess,
			String businessContext) throws AlertException {

		if (outcome.equals(FAIL)){
			throw new AlertException("PRIMED TO FAIL");
		}
				
		this.conversationId = conversationId;
		this.headline = headline;
		this.message = message;
		this.technicalContext = technicalContext;
		this.businessProcess = businessProcess;
		this.businessContext = businessContext;
		
	}
	
}
