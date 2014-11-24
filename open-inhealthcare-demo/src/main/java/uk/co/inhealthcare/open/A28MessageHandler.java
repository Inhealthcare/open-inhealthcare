package uk.co.inhealthcare.open;

import org.springframework.stereotype.Component;

import uk.co.inhealthcare.open.jsat.hl7.HL7MessageHandler;
import ca.uhn.hl7v2.model.v24.message.ADT_A05;

@HL7MessageHandler(triggerEvent = "A28")
@Component
public class A28MessageHandler {

	@HL7MessageHandler(triggerEvent = "A28")
	public void processA28(ADT_A05 hl7Message) {

		System.out.println("Processing 28: " + hl7Message);

	}

	@HL7MessageHandler(triggerEvent = "A31")
	public void processA31(ADT_A05 hl7Message) {

		System.out.println("Processing 31: " + hl7Message);

	}

	@HL7MessageHandler
	public void processAllAllowedByClass(ADT_A05 hl7Message) {

		System.out.println("Processing All: " + hl7Message);

	}

	@HL7MessageHandler
	public void processAll(ADT_A05 hl7Message) {

		System.out.println("Processing All: " + hl7Message);

	}

}
