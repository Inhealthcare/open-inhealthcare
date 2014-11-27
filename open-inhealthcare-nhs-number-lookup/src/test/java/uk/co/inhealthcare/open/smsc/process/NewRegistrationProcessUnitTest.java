package uk.co.inhealthcare.open.smsc.process;

import java.io.IOException;

import junit.framework.TestCase;
import uk.co.inhealthcare.open.jsat.ConfigurationException;
import uk.co.inhealthcare.open.jsat.TransformationException;
import uk.co.inhealthcare.open.jsat.ValidationException;
import uk.co.inhealthcare.open.jsat.services.EmailSenderImpl_Mock;
import uk.co.inhealthcare.open.jsat.services.HL7SenderImpl_Mock;
import uk.co.inhealthcare.open.smsc.messages.logging.LoggingException;
import uk.co.inhealthcare.open.smsc.transformations.A05ToDemographicUpdate_ITK;
import uk.co.inhealthcare.open.smsc.transformations.ToPASUpdate_ITK;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v24.datatype.CX;
import ca.uhn.hl7v2.model.v24.message.ADT_A05;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.model.v24.segment.PID;

/*
 * Unit
 * - This operation
 * - Related Messages (DemographicUpdate)
 * - Superclass Hierarchy (SMSCProcess)
 * - Transformers
 * 
 * Responsibilities
 * - Accept a New Registration Message 
 * - Check configuration [TEST: All mandatory configurations are verified]
 * - Check parameters [TEST: All mandatory parameters are verified]
 * - Log the request [TEST: Message is logged / Logging Exception]
 * - Check if any SMSP action required [TEST: With/Without NHS Number and all NSTS codes]
 * - Build the SMSP Request and call the SMSP Operation [TEST: Use Mock Services to check requests]
 * - Manage validation errors [TEST: Simulate error and check process outcome [Mock:logger] and alert [Mock:Alerter]]
 * - Check Business Alerting [TEST: By response code/trigger type | just response code]
 * - Check Technical Alerting [TEST: By response code [Mock:logger]]
 * - Check PAS Update [TEST: SMSP response codes configuration / Message build [Mock:Hl7Sender]]
 * - Log outcome [TEST: Message is logged / Logging Exception]
 *  
 * Configuration Required
 * - Service: SMSCLoggingService [Mandatory]
 * - Service: transformIn [Mandatory]
 * - Service: transformOut [Mandatory]
 * - Service: technicalAlerter [Mandatory]
 * - Service: businessAlerter [Mandatory]
 * - Service: getNhsNumberOperation [Mandatory]
 * - Service: verifyNhsNumberOperation [Mandatory]
 * - Service: hl7Sender [Mandatory]
 * - Value: localAuditId (e.g. "urn:nhs-uk:identity:ods:XXXX:System:JSAT") [Mandatory]
 *
 * Mocks Required
 * - GetNHSNumberOperation - store parameters and return Validation error or response code as dictated by priming
 * - VerifyNHSNumberOperation - store parameters and return validation error or response code as dictated by priming
 * - SMSCLoggingService - store the message (throw exception if primed)
 * - TechnicalAlerter - store the message (throw exception if primed)
 * - BusinessAlerterTechnicalAlerter - store the message (throw exception if primed)
 * - HL7Sender - store the message (throw exception if primed)
 *  
 */
public class NewRegistrationProcessUnitTest extends TestCase {
	
	private static final String VALID_LOCALAUDITID = "urn:nhs-uk:identity:ods:XXXX:System:JSAT";
	
	private static final String VALID_DOB = "19430828";
	private static final String VALID_GENDER = "1";
	private static final String VALID_SURNAME = "DOE";
	private static final String VALID_GIVEN = "JOHN";
	private static final String VALID_POSTCODE = "CH1 1AA";
	private static final String VALID_LOCAL_PATIENT_ID = "ZZZ:12345";
	private static final String VALID_NHSNUMBER = "9999345201";
	private static final String VALID_NHSNUMBER2 = "9449310416"; 

	private static final String SMSP_OK = "SMSP-0000";
	private static final String SMSP_NOMATCH = "DEMOG-0001";
	private static final String SMSP_MULTIPLES = "DEMOG-0007";
	private static final String SMSP_SUPERCEDING = "DEMOG-0017";
	private static final String SMSP_INVALID = "DEMOG-0022";
	private static final String SMSP_NOTVERIFIED = "DEMOG-0040";
	private static final String SMSP_NOTNEWSTYLE = "DEMOG-0042";
	private static final String SMSP_SPINEGENERIC_ERR = "DEMOG-9999";
	private static final String SMSP_SMSPVALIDATION_IN = "SMSP-0001";
	private static final String SMSP_SMSPVALIDATION_OUT = "SMSP-0002";
	private static final String SMSP_SMSPGENERIC_ERR = "SMSP-9999";

	public void testConfigMissingSMSCLogger()  {
		
		NewRegistrationProcess process = getConfiguredProcess(SMSP_OK);
		process.setSmscLogger(null);

		process.process(null);
		EmailSenderImpl_Mock techSender = (EmailSenderImpl_Mock) process.technicalAlerter;
		assertTrue(techSender.message!=null);
		assertTrue(techSender.message.equals(SMSCProcess.SMSCLOGGER_NOT_CONFIGURED));
			
	}
	public void testConfigMissingTechnicalAlerter()  {
		
		NewRegistrationProcess process = getConfiguredProcess(SMSP_OK);
		process.setTechnicalAlerter(null);

		process.process(null);
		SMSCLoggingService_Mock logger = (SMSCLoggingService_Mock) process.smscLogger;
		assertTrue(logger.parm_outcomeConversationId.get(0)!=null);
		assertTrue(logger.parm_Outcome.get(0).contains(SMSCProcess.TECHALERT_NOT_CONFIGURED));
			
	}

	public void testConfigMissingBusinessAlerter()  {
		
		NewRegistrationProcess process = getConfiguredProcess(SMSP_OK);
		process.setBusinessAlerter(null);

		process.process(null);
		SMSCLoggingService_Mock logger = (SMSCLoggingService_Mock) process.smscLogger;
		assertTrue(logger.parm_outcomeConversationId.get(0)!=null);
		assertTrue(logger.parm_Outcome.get(0).contains(SMSCProcess.BUSALERT_NOT_CONFIGURED));
			
	}

	public void testConfigMissingLocalAuditId()  {
		
		NewRegistrationProcess process = getConfiguredProcess(SMSP_OK);
		process.setLocalAuditId(null);

		process.process(null);
		EmailSenderImpl_Mock techSender = (EmailSenderImpl_Mock) process.technicalAlerter;
		assertTrue(techSender.message!=null);
		assertTrue(techSender.message.equals(SMSCProcess.LOCALAUDITID_NOT_CONFIGURED));
		SMSCLoggingService_Mock logger = (SMSCLoggingService_Mock) process.smscLogger;
		assertTrue(logger.parm_outcomeConversationId.get(0)!=null);
		assertTrue(logger.parm_Outcome.get(0).contains(SMSCProcess.LOCALAUDITID_NOT_CONFIGURED));
			
	}

	public void testConfigMissingTransformIn()  {
		
		NewRegistrationProcess process = getConfiguredProcess(SMSP_OK);
		process.setTransformIn(null);

		process.process(null);
		EmailSenderImpl_Mock techSender = (EmailSenderImpl_Mock) process.technicalAlerter;
		assertTrue(techSender.message!=null);
		assertTrue(techSender.message.equals(SMSCProcess.TRANSFORMERIN_NOT_CONFIGURED));
		SMSCLoggingService_Mock logger = (SMSCLoggingService_Mock) process.smscLogger;
		assertTrue(logger.parm_outcomeConversationId.get(0)!=null);
		assertTrue(logger.parm_Outcome.get(0).contains(SMSCProcess.TRANSFORMERIN_NOT_CONFIGURED));
			
	}

	public void testConfigMissingTransformOut()  {
		
		NewRegistrationProcess process = getConfiguredProcess(SMSP_OK);
		process.setTransformOut(null);

		process.process(null);
		EmailSenderImpl_Mock techSender = (EmailSenderImpl_Mock) process.technicalAlerter;
		assertTrue(techSender.message!=null);
		assertTrue(techSender.message.equals(SMSCProcess.TRANSFORMEROUT_NOT_CONFIGURED));
		SMSCLoggingService_Mock logger = (SMSCLoggingService_Mock) process.smscLogger;
		assertTrue(logger.parm_outcomeConversationId.get(0)!=null);
		assertTrue(logger.parm_Outcome.get(0).contains(SMSCProcess.TRANSFORMEROUT_NOT_CONFIGURED));
			
	}
	
	public void testConfigMissingHL7Sender()  {
		
		NewRegistrationProcess process = getConfiguredProcess(SMSP_OK);
		process.setHl7Sender(null);

		process.process(null);
		EmailSenderImpl_Mock techSender = (EmailSenderImpl_Mock) process.technicalAlerter;
		assertTrue(techSender.message!=null);
		assertTrue(techSender.message.equals(SMSCProcess.HL7SENDER_NOT_CONFIGURED));
		SMSCLoggingService_Mock logger = (SMSCLoggingService_Mock) process.smscLogger;
		assertTrue(logger.parm_outcomeConversationId.get(0)!=null);
		assertTrue(logger.parm_Outcome.get(0).contains(SMSCProcess.HL7SENDER_NOT_CONFIGURED));
			
	}

	// TEST PARAMETERS

	public void testParmsMissingRequest() throws ValidationException, LoggingException {
		
		NewRegistrationProcess process = getConfiguredProcess(SMSP_OK);

		process.process(null);
		EmailSenderImpl_Mock techSender = (EmailSenderImpl_Mock) process.technicalAlerter;
		assertTrue(techSender.message!=null);
		assertTrue(techSender.message.equals(SMSCProcess.ERR_REQUEST_NOT_PROVIDED));
		assertTrue(techSender.conversationId!=null);
		SMSCLoggingService_Mock logger = (SMSCLoggingService_Mock) process.smscLogger;
		assertTrue(logger.parm_outcomeConversationId.get(0)!=null);
		assertTrue(logger.parm_Outcome.get(0).contains(SMSCProcess.ERR_REQUEST_NOT_PROVIDED));

	}

	// TEST REQUEST LOGGING
	public void testRequestLoggingInputException() throws ConfigurationException, ValidationException {
		
		NewRegistrationProcess process = getConfiguredProcess(SMSP_OK);
		ADT_A05 request = getValidRequest("",null);

		// Prime the logger to FAIL
		SMSCLoggingService_Mock logger = new SMSCLoggingService_Mock();
		logger.primeSmscInputOutcome(SMSCLoggingService_Mock.FAIL);
		process.setSmscLogger(logger);

		process.process(request);

		// Confirm the input message is not logged
		assertTrue(logger.parm_inputConversationId.size()==0); 
		// Confirm the log failure is in the SMSC Output Log (unlikely in reality, but interesting test)
		assertTrue(logger.parm_Outcome.get(0).equals(SMSCLoggingService_Mock.SMSC_INPUT_ERROR));

		// Confirm the failure was alerted
		EmailSenderImpl_Mock techAlerter = (EmailSenderImpl_Mock) process.technicalAlerter;
		assertTrue(techAlerter.message.equals((SMSCLoggingService_Mock.SMSC_INPUT_ERROR)));
		assertTrue(techAlerter.businessProcess.equals("NewRegistrationProcess"));
		assertTrue(techAlerter.conversationId.equals(logger.parm_outcomeConversationId.get(0)));
	}
	public void testRequestLoggingOutputException() throws ConfigurationException, ValidationException {
		
		NewRegistrationProcess process = getConfiguredProcess(SMSP_OK);
		ADT_A05 request = getValidRequest("",null);

		// Prime the logger to FAIL
		SMSCLoggingService_Mock logger = new SMSCLoggingService_Mock();
		logger.primeSmscOutcomeOutcome(SMSCLoggingService_Mock.FAIL);
		process.setSmscLogger(logger);

		process.process(request);

		// Confirm the input message is logged
		assertTrue(logger.parm_inputConversationId.get(0)!=null); 
		// Confirm the output log is empty
		assertTrue(logger.parm_outcomeConversationId.size()==0); 

		// Confirm the failure was alerted
		EmailSenderImpl_Mock techAlerter = (EmailSenderImpl_Mock) process.technicalAlerter;
		assertTrue(techAlerter.message.equals((SMSCLoggingService_Mock.SMSC_OUTCOME_ERROR)));
		assertTrue(techAlerter.businessProcess.equals("NewRegistrationProcess"));
		assertTrue(techAlerter.conversationId.equals(logger.parm_inputConversationId.get(0)));
	}

	public void testLoggingOk() throws ConfigurationException, ValidationException {
		
		NewRegistrationProcess process = getConfiguredProcess(SMSP_OK);
		ADT_A05 request = getValidRequest("NSTS01",VALID_NHSNUMBER);

		// Default request has NSTS01 Status code and therefore has no SMSP activity requirement
		process.process(request);

		// Confirm the input message is logged
		SMSCLoggingService_Mock logger = (SMSCLoggingService_Mock) process.smscLogger;
		assertTrue(logger.parm_inputConversationId.get(0)!=null); 
		assertTrue(logger.parm_DemographicUpdate.get(0)!=null);
		// assertTrue(logger.parm_DemographicUpdate.get(0).getFamilyName().equals(VALID_SURNAME));
		// assertTrue(logger.parm_DemographicUpdate.get(0).getGivenName().equals(VALID_GIVEN));
		// assertTrue(logger.parm_DemographicUpdate.get(0).getDateOfBirth().equals(VALID_DOB));
		// assertTrue(logger.parm_DemographicUpdate.get(0).getGender().equals(VALID_GENDER));
		// assertTrue(logger.parm_DemographicUpdate.get(0).getPostcode().equals(VALID_POSTCODE));
		// assertTrue(logger.parm_DemographicUpdate.get(0).getLocalPatientIdentifier().equals(VALID_LOCAL_PATIENT_ID));
		// assertTrue(logger.parm_DemographicUpdate.get(0).getNHSNumberStatus().equals("NSTS01"));
		// assertTrue(logger.parm_DemographicUpdate.get(0).getNHSNumber().equals(VALID_NHSNUMBER));
		
		// Confirm the output is logged
		assertTrue(logger.parm_outcomeConversationId.get(0)!=null); 
		assertTrue(logger.parm_outcomeConversationId.get(0).equals(logger.parm_inputConversationId.get(0)));
		assertTrue(logger.parm_Outcome.get(0).equals(NewRegistrationProcess.NO_SMSP_LOOKUP_REQUIRED));

		// Confirm nothing was alerted
		EmailSenderImpl_Mock techAlerter = (EmailSenderImpl_Mock) process.technicalAlerter;
		assertTrue(techAlerter.message==null);
		assertTrue(techAlerter.conversationId==null);
	}


	public void testNSTS01() throws ConfigurationException, ValidationException, HL7Exception, TransformationException {
		
		testNoLookupRequest("NSTS01",null);
		testNoLookupRequest("NSTS01",VALID_NHSNUMBER);
		
	}

	public void testNSTS02() throws ConfigurationException, ValidationException, HL7Exception, TransformationException {
	
		String inboundStatus = "NSTS02";
		// Get Path
		testValidRequest(inboundStatus, null, "NSTS01", VALID_NHSNUMBER, SMSP_OK);
		testValidRequest(inboundStatus, null, "NSTS04", "", SMSP_NOMATCH);
		testValidRequest(inboundStatus, null, "NSTS04", "", SMSP_MULTIPLES);
		testSMSPError(inboundStatus, null, SMSP_SMSPGENERIC_ERR);
		testSMSPError(inboundStatus, null, SMSP_SMSPVALIDATION_IN);
		testSMSPError(inboundStatus, null, SMSP_SMSPVALIDATION_OUT);
		testSMSPError(inboundStatus, null, SMSP_SPINEGENERIC_ERR);
		
		// Verify Path
		testValidRequest(inboundStatus,VALID_NHSNUMBER, "NSTS01", VALID_NHSNUMBER, SMSP_OK);
		testValidRequest(inboundStatus,VALID_NHSNUMBER, "NSTS05", VALID_NHSNUMBER, SMSP_NOTVERIFIED);
		testValidRequest(inboundStatus,VALID_NHSNUMBER, "NSTS04", VALID_NHSNUMBER, SMSP_NOMATCH);
		testValidRequest(inboundStatus,VALID_NHSNUMBER, "NSTS05", VALID_NHSNUMBER, SMSP_INVALID);
		testValidRequest(inboundStatus,VALID_NHSNUMBER, "NSTS05", VALID_NHSNUMBER, SMSP_NOTNEWSTYLE);
		testValidRequest(inboundStatus,VALID_NHSNUMBER, "NSTS01", VALID_NHSNUMBER2, SMSP_SUPERCEDING);
		testSMSPError(inboundStatus, VALID_NHSNUMBER, SMSP_SMSPGENERIC_ERR);
		testSMSPError(inboundStatus, VALID_NHSNUMBER, SMSP_SMSPVALIDATION_IN);
		testSMSPError(inboundStatus, VALID_NHSNUMBER, SMSP_SMSPVALIDATION_OUT);
		testSMSPError(inboundStatus, VALID_NHSNUMBER, SMSP_SPINEGENERIC_ERR);
		
	}

	public void testNSTS03() throws ConfigurationException, ValidationException, HL7Exception, TransformationException {
		
		String inboundStatus = "NSTS03";
		// Get Path
		testValidRequest(inboundStatus, null, "NSTS01", VALID_NHSNUMBER, SMSP_OK);
		testValidRequest(inboundStatus, null, "NSTS04", "", SMSP_NOMATCH);
		testValidRequest(inboundStatus, null, "NSTS04", "", SMSP_MULTIPLES);
		testSMSPError(inboundStatus, null, SMSP_SMSPGENERIC_ERR);
		testSMSPError(inboundStatus, null, SMSP_SMSPVALIDATION_IN);
		testSMSPError(inboundStatus, null, SMSP_SMSPVALIDATION_OUT);
		testSMSPError(inboundStatus, null, SMSP_SPINEGENERIC_ERR);
		
		// Verify Path
		testValidRequest(inboundStatus,VALID_NHSNUMBER, "NSTS01", VALID_NHSNUMBER, SMSP_OK);
		testValidRequest(inboundStatus,VALID_NHSNUMBER, "NSTS05", VALID_NHSNUMBER, SMSP_NOTVERIFIED);
		testValidRequest(inboundStatus,VALID_NHSNUMBER, "NSTS04", VALID_NHSNUMBER, SMSP_NOMATCH);
		testValidRequest(inboundStatus,VALID_NHSNUMBER, "NSTS05", VALID_NHSNUMBER, SMSP_INVALID);
		testValidRequest(inboundStatus,VALID_NHSNUMBER, "NSTS05", VALID_NHSNUMBER, SMSP_NOTNEWSTYLE);
		testValidRequest(inboundStatus,VALID_NHSNUMBER, "NSTS01", VALID_NHSNUMBER2, SMSP_SUPERCEDING);
		testSMSPError(inboundStatus, VALID_NHSNUMBER, SMSP_SMSPGENERIC_ERR);
		testSMSPError(inboundStatus, VALID_NHSNUMBER, SMSP_SMSPVALIDATION_IN);
		testSMSPError(inboundStatus, VALID_NHSNUMBER, SMSP_SMSPVALIDATION_OUT);
		testSMSPError(inboundStatus, VALID_NHSNUMBER, SMSP_SPINEGENERIC_ERR);
		
	}

	public void testNSTSOther() throws ConfigurationException, ValidationException, HL7Exception, TransformationException {
		
		testNoLookupRequest("NSTS04",null);
		testNoLookupRequest("NSTS04",VALID_NHSNUMBER);
		testNoLookupRequest("NSTS05",null);
		testNoLookupRequest("NSTS05",VALID_NHSNUMBER);
		testNoLookupRequest("NSTS06",null);
		testNoLookupRequest("NSTS06",VALID_NHSNUMBER);
		testNoLookupRequest("NSTS07",null);
		testNoLookupRequest("NSTS07",VALID_NHSNUMBER);
		testNoLookupRequest("NSTS08",null);
		testNoLookupRequest("NSTS08",VALID_NHSNUMBER);
		
	}

	// COMMON TESTS
	private void testNoLookupRequest(String inStatus, String inNhsNumber) throws ConfigurationException, ValidationException, HL7Exception {
		
		NewRegistrationProcess process = getConfiguredProcess(SMSP_OK);
		ADT_A05 request = getValidRequest(inStatus,inNhsNumber);

		process.process(request);

		// Confirm the input message is logged
		SMSCLoggingService_Mock logger = (SMSCLoggingService_Mock) process.smscLogger;
		assertTrue(logger.parm_inputConversationId.get(0)!=null); 
		assertTrue(logger.parm_DemographicUpdate.get(0)!=null);
		// assertTrue(logger.parm_DemographicUpdate.get(0).getNHSNumberStatus().equals(inStatus));
		
		// Confirm the output is logged
		assertTrue(logger.parm_outcomeConversationId.get(0)!=null); 
		assertTrue(logger.parm_outcomeConversationId.get(0).equals(logger.parm_inputConversationId.get(0)));
		assertTrue(logger.parm_Outcome.get(0).equals(NewRegistrationProcess.NO_SMSP_LOOKUP_REQUIRED));

		// Confirm nothing was alerted
		EmailSenderImpl_Mock techAlerter = (EmailSenderImpl_Mock) process.technicalAlerter;
		assertTrue(techAlerter.message==null);
		assertTrue(techAlerter.conversationId==null);

		// Confirm PAS Update was not sent
		HL7SenderImpl_Mock hl7Sender = (HL7SenderImpl_Mock) process.hl7Sender;
		assertTrue(hl7Sender.parm_hl7Msg==null);

	}

	private void testValidRequest(String inStatus, String inNhsNumber, String outStatus, String outNhsNumber, String smspResponseRequired) throws ConfigurationException, ValidationException, HL7Exception, TransformationException {
		
		NewRegistrationProcess process = getConfiguredProcess(smspResponseRequired);
		ADT_A05 request = getValidRequest(inStatus,inNhsNumber);

		process.process(request);

		// Confirm the input message is logged
		SMSCLoggingService_Mock logger = (SMSCLoggingService_Mock) process.smscLogger;
		assertTrue(logger.parm_inputConversationId.get(0)!=null); 
		assertTrue(logger.parm_DemographicUpdate.get(0)!=null);
		// assertTrue(logger.parm_DemographicUpdate.get(0).getNHSNumberStatus().equals(inStatus));
		
		// Confirm the outcome is logged correctly
		assertTrue(logger.parm_outcomeConversationId.get(0)!=null); 
		assertTrue(logger.parm_outcomeConversationId.get(0).equals(logger.parm_inputConversationId.get(0)));
		String expectedOutcome = "[NewNHSStatusCode]"+outStatus+"[NHSNumber]"+outNhsNumber;
		assertTrue(logger.parm_Outcome.get(0).equals(expectedOutcome));

		// Confirm business alerts as expected - DEPENDANT ON CONFIG 
		EmailSenderImpl_Mock businessAlerter = (EmailSenderImpl_Mock) process.businessAlerter;
		boolean businessAlertRequired = process.getLookupBoolean("SMSP.ResponseCodeBusinessAlert.A28",smspResponseRequired,"N");
		if (!businessAlertRequired){
			businessAlertRequired = process.getLookupBoolean("SMSP.ResponseCodeBusinessAlert",smspResponseRequired,"N");
		}
		if(businessAlertRequired) {
			assertTrue(businessAlerter.message!=null);
			assertTrue(businessAlerter.conversationId!=null);
		} else {
			assertTrue(businessAlerter.message==null);
			assertTrue(businessAlerter.conversationId==null);
		}
		
		// Confirm no technical alerts
		EmailSenderImpl_Mock techAlerter = (EmailSenderImpl_Mock) process.technicalAlerter;
		assertTrue(techAlerter.message==null);
		assertTrue(techAlerter.conversationId==null);

		// Confirm PAS Update was correct
		HL7SenderImpl_Mock hl7Sender = (HL7SenderImpl_Mock) process.hl7Sender;
		assertTrue(hl7Sender.parm_hl7Msg!=null);
		//DEPENDENCY ON DEFAULT TRANSFORMER - Part of the unit under test
		A05ToDemographicUpdate_ITK transformer = new A05ToDemographicUpdate_ITK();
		DemographicUpdate outboundMessage = transformer.getDemographicUpdate((ADT_A05) hl7Sender.parm_hl7Msg);
		assertTrue(outboundMessage.getNHSNumberStatus().equals(outStatus));
		assertTrue(outboundMessage.getNHSNumber().equals(outNhsNumber));
		assertTrue(outboundMessage.getDateOfBirth().equals(VALID_DOB));
		assertTrue(outboundMessage.getFamilyName().equals(VALID_SURNAME));
		assertTrue(outboundMessage.getGivenName().equals(VALID_GIVEN));
		assertTrue(outboundMessage.getGender().equals(VALID_GENDER));

	}

	private void testSMSPError(String inStatus, String inNhsNumber, String smspResponseRequired) throws ConfigurationException, ValidationException, HL7Exception, TransformationException {
		
		NewRegistrationProcess process = getConfiguredProcess(smspResponseRequired);
		ADT_A05 request = getValidRequest(inStatus,inNhsNumber);

		process.process(request);

		// Confirm the input message is logged
		SMSCLoggingService_Mock logger = (SMSCLoggingService_Mock) process.smscLogger;
		assertTrue(logger.parm_inputConversationId.get(0)!=null); 
		assertTrue(logger.parm_DemographicUpdate.get(0)!=null);
		// assertTrue(logger.parm_DemographicUpdate.get(0).getNHSNumberStatus().equals(inStatus));
		
		// Confirm the outcome is logged correctly
		assertTrue(logger.parm_outcomeConversationId.get(0)!=null); 
		assertTrue(logger.parm_outcomeConversationId.get(0).equals(logger.parm_inputConversationId.get(0)));
		String expectedOutcome = "PAS Update not required for :"+smspResponseRequired;
		assertTrue(logger.parm_Outcome.get(0).equals(expectedOutcome));

		// Confirm an alert was raised
		EmailSenderImpl_Mock techAlerter = (EmailSenderImpl_Mock) process.technicalAlerter;
		assertTrue(techAlerter.conversationId.equals(logger.parm_inputConversationId.get(0)));
		assertTrue(techAlerter.businessProcess.equals("NewRegistrationProcess"));
		assertTrue(techAlerter.headline.equals("SMSP Response Code triggered an alert:"));
		assertTrue(techAlerter.message!=null);
		assertTrue(techAlerter.businessContext!=null);
		if (inNhsNumber==null){
			assertTrue(techAlerter.businessContext.contains("<NhsNumber></NhsNumber>"));
		} else {
			assertTrue(techAlerter.businessContext.contains("<NhsNumber>"+inNhsNumber+"</NhsNumber>"));
		}
		assertTrue(techAlerter.businessContext.contains("<GivenName>"+VALID_GIVEN+"</GivenName>"));
		assertTrue(techAlerter.businessContext.contains("<FamilyName>"+VALID_SURNAME+"</FamilyName>"));
		assertTrue(techAlerter.businessContext.contains("<DateOfBirth>"+VALID_DOB+"</DateOfBirth>"));
		assertTrue(techAlerter.businessContext.contains("<Gender>"+VALID_GENDER+"</Gender>"));
		assertTrue(techAlerter.technicalContext!=null);

		// Confirm PAS Update was not sent
		HL7SenderImpl_Mock hl7Sender = (HL7SenderImpl_Mock) process.hl7Sender;
		assertTrue(hl7Sender.parm_hl7Msg==null);

	}

	// UTILS
	private NewRegistrationProcess getConfiguredProcess(String smspResponse){
		
		String primeNhsNumber = "";
		boolean primeVerified = false;
		if (smspResponse.equals(SMSP_OK)){
			primeNhsNumber = VALID_NHSNUMBER;
			primeVerified = true;
		}
		if (smspResponse.equals(SMSP_SUPERCEDING)){
			primeNhsNumber = VALID_NHSNUMBER2;
			primeVerified = true;
		}
		NewRegistrationProcess process =  new NewRegistrationProcess();
		
		// Set up real services
		process.setTransformIn(new A05ToDemographicUpdate_ITK());
		process.setTransformOut(new ToPASUpdate_ITK());

		// Set up Mock services
		process.setSmscLogger(new SMSCLoggingService_Mock());
		process.setTechnicalAlerter(new EmailSenderImpl_Mock());
		process.setBusinessAlerter(new EmailSenderImpl_Mock());

		GetPatientDetailsByNHSNumberOperation_Mock mockGetByNhsNumber = new GetPatientDetailsByNHSNumberOperation_Mock();
		mockGetByNhsNumber.primeResponse(smspResponse, primeNhsNumber, primeVerified, "");
		process.setGetPatientDetailsByNHSNumberOperation(mockGetByNhsNumber);
		
		GetPatientDetailsBySearchOperation_Mock mockGetBySearch = new GetPatientDetailsBySearchOperation_Mock();
		mockGetBySearch.primeResponse(smspResponse, primeNhsNumber, "");
		process.setGetPatientDetailsBySearchOperation(mockGetBySearch);

		process.setHl7Sender(new HL7SenderImpl_Mock());

		// Set up Strings
		process.setLocalAuditId(VALID_LOCALAUDITID);

		return process;
	}
	
	private ADT_A05 getValidRequest(String nhsNumberStatus, String nhsNumber) {
        ADT_A05 adt = new ADT_A05();
        
        try {
	        adt.initQuickstart("ADT", "A28", "P");
	          
	        // Populate the MSH Segment
	        MSH mshSegment = adt.getMSH();
	        mshSegment.getSendingApplication().getNamespaceID().setValue("TestSendingSystem");
	        mshSegment.getSequenceNumber().setValue("123");
	        mshSegment.getMsh9_MessageType().getMsg3_MessageStructure().setValue("ADT_A05");
	          
	        // Populate the PID Segment
	        PID pid = adt.getPID(); 
	        pid.getPatientName(0).getFamilyName().getSurname().setValue(VALID_SURNAME);
	        pid.getPatientName(0).getGivenName().setValue(VALID_GIVEN);
	        pid.getAdministrativeSex().setValue(VALID_GENDER);
	        pid.getDateTimeOfBirth().getTimeOfAnEvent().setValue(VALID_DOB);
			pid.getPatientAddress(0).getXad5_ZipOrPostalCode().setValue(VALID_POSTCODE);

	        pid.getPatientIdentifierList(0).getID().setValue(VALID_LOCAL_PATIENT_ID);
	        pid.getPatientIdentifierList(0).getCx4_AssigningAuthority().getHd1_NamespaceID().setValue("RXX");
	        
	        pid.getIdentityReliabilityCode(0).setValue(nhsNumberStatus);
	        if ((nhsNumber!=null) &&(!nhsNumber.equals(""))){
	        	CX nhsNoCX = adt.getPID().insertPatientIdentifierList(0);
	    		nhsNoCX.getCx1_ID().setValue(nhsNumber);
	    		nhsNoCX.getCx4_AssigningAuthority().getNamespaceID().setValue("NHS");
	        }
        
	        
        } catch (HL7Exception e){
			e.printStackTrace();
        } catch (IOException e) {
			e.printStackTrace();
		}
		
		return adt;
	}
}
