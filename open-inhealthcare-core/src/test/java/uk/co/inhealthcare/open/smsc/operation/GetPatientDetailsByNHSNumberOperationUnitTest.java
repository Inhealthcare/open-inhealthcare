package uk.co.inhealthcare.open.smsc.operation;

import java.io.IOException;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathException;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import uk.co.inhealthcare.open.itk.infrastructure.ITKAddress;
import uk.co.inhealthcare.open.itk.infrastructure.ITKIdentity;
import uk.co.inhealthcare.open.itk.payload.ITKMessage;
import uk.co.inhealthcare.open.itk.source.ITKMessageSenderImpl_Mock;
import uk.co.inhealthcare.open.itk.util.xml.DomUtils;
import uk.co.inhealthcare.open.itk.util.xml.XPaths;
import uk.co.inhealthcare.open.jsat.ConfigurationException;
import uk.co.inhealthcare.open.jsat.ValidationException;
import uk.co.inhealthcare.open.smsc.messages.GetPatientDetailsByNHSNumberRequest;
import uk.co.inhealthcare.open.smsc.messages.GetPatientDetailsResponse;
import uk.co.inhealthcare.open.smsc.messages.logging.LoggingException;
import uk.co.inhealthcare.open.smsc.messages.logging.SMSCLoggingService_Mock;
import uk.co.inhealthcare.open.smsc.operation.GetPatientDetailsByNHSNumberOperation;

/*
 * Unit
 * - This operation
 * - Related Messages (GetPatientDetailsByNHSNumberRequest/Response)
 * - Superclass Hierarchy
 * 
 * Responsibilities
 * - Accept a GetPatientDetailsByNHSNumberRequest
 * - Check configuration [TEST: All mandatory configurations are verified]
 * - Check parameters [TEST: All mandatory parameters are verified]
 * - Log the request [TEST: Message is logged / Logging Exception]
 * - Validate the request [TEST: All validations]
 * - Build the ITK Request
 * - Send the ITK Request [TEST: Get the request from the Mock. Check properties. Check message using XPATHS]
 * - Build Appropriate Response: Happy Day  / Not Found / Busy / Error [TEST: Trigger all 4 types]
 * - Log response [TEST: Message is logged / Logging Exception]
 * - Return the GetPatientDetails Response
 * 
 * Configuration Required
 * - Service: ITKMessageSender [Mandatory]
 * - Service: SMSCLoggingService [Mandatory]
 * - Value: serviceProvider (e.g. "urn:nhs-uk:addressing:ods:TKW") [Mandatory]
 * - Value: serviceId (e.g. "urn:nhs-itk:services:201005:getPatientDetailsByNHSNumber-v1-0") [Mandatory]
 * - Value: profileId (e.g. "urn:nhs-en:profile:getPatientDetailsByNHSNumberRequest-v1-0") [Mandatory]
 * - Value: sender (e.g. "urn:nhs-uk:addressing:ods:XXXX:JSAT") [Mandatory]
 * - Value: toPayloadTransform (e.g. "xslt/smsc/ToGetPatientDetailsByNHSNumberRequest.xslt") [Mandatory]
 * 
 * Mocks Required
 * - ITKMessageSender - store parameters and return 3 return types depending on serviceId
 * - SMSCLoggingService - store the message
 *  
 */
public class GetPatientDetailsByNHSNumberOperationUnitTest extends TestCase {
	
	private static final String VALID_DOB       = "19700630";
	private static final String VALID_SURNAME   = "SMITH";
	private static final String VALID_GIVENNAME = "FRED";
	private static final String VALID_NHSNUMBER = "9449310602";
	private static final String VALID_AUDITID  = "TESTER1";

	private static final String SERVICEPROVIDER = "urn:nhs-uk:addressing:ods:TKW";
	private static final String SERVICEID = "urn:nhs-itk:services:201005:getPatientDetailsByNHSNumber-v1-0";
	private static final String PROFILEID = "urn:nhs-en:profile:getPatientDetailsByNHSNumberRequest-v1-0";
	private static final String SENDER = "urn:nhs-uk:addressing:ods:XXXS:JSAT";
	private static final String TOPAYLOADTRANSFORM = "xslt/smsc/ToGetPatientDetailsByNHSNumberRequest.xslt";

	// TEST CONFIGURATION

	public void testConfigMissingITKMessageSender() throws ValidationException, LoggingException {
		
		GetPatientDetailsByNHSNumberOperation operation = getConfiguredOperation();
		operation.setItkMessageSender(null);

		try {
			operation.process(null);
			fail("Should throw a ConfigurationException");
		} catch (ConfigurationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.ITKMESSAGESENDER_NOT_CONFIGURED));
		}
	}

	public void testConfigMissingServiceProvider() throws ValidationException, LoggingException {
		
		GetPatientDetailsByNHSNumberOperation operation = getConfiguredOperation();
		operation.setServiceProvider(null);

		try {
			operation.process(null);
			fail("Should throw a ConfigurationException");
		} catch (ConfigurationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.SERVICEPROVIDER_NOT_CONFIGURED));
		}
	}

	public void testConfigMissingServiceId() throws ValidationException, LoggingException {
		
		GetPatientDetailsByNHSNumberOperation operation = getConfiguredOperation();
		operation.setServiceId(null);

		try {
			operation.process(null);
			fail("Should throw a ConfigurationException");
		} catch (ConfigurationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.SERVICEID_NOT_CONFIGURED));
		}
	}

	public void testConfigMissingProfileId() throws ValidationException, LoggingException {
		
		GetPatientDetailsByNHSNumberOperation operation = getConfiguredOperation();
		operation.setProfileId(null);

		try {
			operation.process(null);
			fail("Should throw a ConfigurationException");
		} catch (ConfigurationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.PROFILEID_NOT_CONFIGURED));
		}
	}

	public void testConfigMissingSender() throws ValidationException, LoggingException {
		
		GetPatientDetailsByNHSNumberOperation operation = getConfiguredOperation();
		operation.setSender(null);

		try {
			operation.process(null);
			fail("Should throw a ConfigurationException");
		} catch (ConfigurationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.SENDER_NOT_CONFIGURED));
		}
	}

	public void testConfigMissingToPayloadTransform() throws ValidationException, LoggingException {
		
		GetPatientDetailsByNHSNumberOperation operation = getConfiguredOperation();
		operation.setToPayloadTransform(null);

		try {
			operation.process(null);
			fail("Should throw a ConfigurationException");
		} catch (ConfigurationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.TOPAYLOADTRANSFORM_NOT_CONFIGURED));
		}
	}

	public void testConfigMissingSMSCLogger() throws ValidationException, LoggingException {
		
		GetPatientDetailsByNHSNumberOperation operation = getConfiguredOperation();
		operation.setSmscLogger(null);

		try {
			operation.process(null);
			fail("Should throw a ConfigurationException");
		} catch (ConfigurationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.SMSCLOGGER_NOT_CONFIGURED));
		}
	}

	// TEST PARAMETERS

	public void testParmsMissingRequest() throws ValidationException, LoggingException {
		
		GetPatientDetailsByNHSNumberOperation operation = getConfiguredOperation();

		try {
			operation.process(null);
			fail("Should throw a ConfigurationException");
		} catch (ConfigurationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.ERR_REQUEST_NOT_PROVIDED));
		}
	}

	// TEST REQUEST LOGGING

	public void testRequestLoggingException() throws ConfigurationException, ValidationException {
		
		GetPatientDetailsByNHSNumberOperation operation = getConfiguredOperation();
		GetPatientDetailsByNHSNumberRequest request = new GetPatientDetailsByNHSNumberRequest();

		// Prime the logger to FAIL
		SMSCLoggingService_Mock logger = new SMSCLoggingService_Mock();
		logger.primeSmspRequestOutcome(SMSCLoggingService_Mock.FAIL);
		operation.setSmscLogger(logger);

		try {
			operation.process(request);
			fail("Should throw a LoggingException");
		} catch (LoggingException e) {
			assertTrue(e.getMessage().contains(SMSCLoggingService_Mock.SMSP_REQUEST_ERROR));
		}
	}

	public void testRequestLogging() throws ConfigurationException, LoggingException, ValidationException {
		
		GetPatientDetailsByNHSNumberOperation operation = getConfiguredOperation();
		GetPatientDetailsByNHSNumberRequest request = getValidRequest();

		operation.process(request);

		// Check stored request from Mock logging service
		SMSCLoggingService_Mock logger = (SMSCLoggingService_Mock) operation.smscLogger;
		assertTrue(logger.parm_SMSPRequest.size()==1);
		assertTrue(logger.parm_SMSPRequest.get(0)==request);
		
	}

	// TEST REQUEST VALIDATION
	public void testValidationNhsNumber() throws ConfigurationException, LoggingException {

		// Mandatory, 10 digits and Mod11. 
		
		GetPatientDetailsByNHSNumberOperation operation = getConfiguredOperation();
		GetPatientDetailsByNHSNumberRequest request = getValidRequest();
		
		// Valid - as default message
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}
		
		// Invalid - Missing - Blank
		request.setNHSNumber("");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.NHSERROR_PREFIX));
		}

		// Invalid - Missing - Null
		request.setNHSNumber(null);
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.NHSERROR_PREFIX));
		}

		// Invalid - Mod11
		request.setNHSNumber("9449310603");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.NHSERROR_PREFIX));
		}

		// Invalid - Short
		request.setNHSNumber("944931060");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.NHSERROR_PREFIX));
		}

		// Invalid - Long
		request.setNHSNumber("94493106022");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.NHSERROR_PREFIX));
		}

	}

	public void testValidationDOB() throws ConfigurationException, LoggingException {

		// Mandatory. For a trace this can be - YYYYMMDD / YYYYMM / YYYY 
		
		GetPatientDetailsByNHSNumberOperation operation = getConfiguredOperation();
		GetPatientDetailsByNHSNumberRequest request = getValidRequest();
		
		// Valid - as default message
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}

		// Invalid - Null
		request.setDateOfBirth(null);
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.DOBERROR_PREFIX));
		}
		// Invalid - Blank
		request.setDateOfBirth("");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.DOBERROR_PREFIX));
		}
		// Invalid - YY
		request.setDateOfBirth("70");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.DOBERROR_PREFIX));
		}
		// Invalid - YYYY
		request.setDateOfBirth("1970");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.DOBERROR_PREFIX));
		}
		// Invalid - YYYYM
		request.setDateOfBirth("19701");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.DOBERROR_PREFIX));
		}
		// Invalid - YYYYMM
		request.setDateOfBirth("197001");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.DOBERROR_PREFIX));
		}
		// Invalid - YYYYMMD
		request.setDateOfBirth("1970011");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.DOBERROR_PREFIX));
		}
		// Invalid - YYYYMM - Bad month
		request.setDateOfBirth("197013");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.DOBERROR_PREFIX));
		}
		// Invalid - YYYYMMDD - Bad day
		request.setDateOfBirth("19701232");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.DOBERROR_PREFIX));
		}
	}

	public void testValidationSurname() throws ConfigurationException, LoggingException {

		// Optional. No Wildcards. If present must be at least 3 characters
		
		GetPatientDetailsByNHSNumberOperation operation = getConfiguredOperation();
		GetPatientDetailsByNHSNumberRequest request = getValidRequest();
		
		// Valid - as default message
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}
		
		// Valid - Just 3 character
		request.setSurname("SMI");
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}

		// Valid - Missing - Blank
		request.setSurname("");
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}

		// Valid - Missing - Null
		request.setSurname(null);
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}

		// Invalid - 2 chars
		request.setSurname("SM");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.SURNAMEERROR_PREFIX));
		}

		// Invalid - Wildcard before character 3
		request.setSurname("S*");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.SURNAMEERROR_PREFIX));
		}
		// Invalid - Wildcard after character 3
		request.setSurname("SM*");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.SURNAMEERROR_PREFIX));
		}
	}

	public void testValidationGivenName() throws ConfigurationException, LoggingException {

		// Given Name is optional for a TRACE
		// If present givenName can contain a wildcard ("*") - but only after two valid characters
		
		GetPatientDetailsByNHSNumberOperation operation = getConfiguredOperation();
		GetPatientDetailsByNHSNumberRequest request = getValidRequest();
		
		// Valid - as default message
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}
		
		// Valid - Missing - Null
		request.setGivenName(null);
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}

		// Valid - Missing - Blank
		request.setGivenName("");
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}

		// Valid - Just 1 character
		request.setGivenName("F");
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}

		// Invalid - Contains a wildcard
		request.setGivenName("F*");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.GIVENNAMEERROR_PREFIX));
		}
	
		// Invalid - Contains a wildcard (after character 2)
		request.setGivenName("FR*");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetPatientDetailsByNHSNumberOperation.GIVENNAMEERROR_PREFIX));
		}

	}
	
	// TEST - SENDING THE ITK REQUEST
	public void testITKSend() throws ConfigurationException, LoggingException, ValidationException{
		String conversationId = UUID.randomUUID().toString().toUpperCase();
		GetPatientDetailsByNHSNumberOperation operation = getConfiguredOperation();
		GetPatientDetailsByNHSNumberRequest request = getValidRequest();
		request.setConversationId(conversationId);
		ITKMessageSenderImpl_Mock sender = new ITKMessageSenderImpl_Mock();
		sender.primeResponse("HappyDayVNNResponse.xml");
		operation.setItkMessageSender(sender);

		operation.process(request);

		ITKMessage itkRequest = sender.parm_Request;
		
		// Check the message parameters
		assertTrue(itkRequest.getConversationId().equals(conversationId));
		assertTrue(itkRequest.getMessageProperties().getProfileId().equals(PROFILEID));
		assertTrue(itkRequest.getMessageProperties().getServiceId().equals(SERVICEID));
		assertTrue(itkRequest.getMessageProperties().getToAddress().getURI().equals(SERVICEPROVIDER));
		assertTrue(itkRequest.getMessageProperties().getToAddress().getType().equals(ITKAddress.DEFAULT_ADDRESS_TYPE));
		assertTrue(itkRequest.getMessageProperties().getFromAddress().getURI().equals(SENDER));
		assertTrue(itkRequest.getMessageProperties().getToAddress().getType().equals(ITKAddress.DEFAULT_ADDRESS_TYPE));
		
		// Check the Audit ids
		assertTrue(itkRequest.getMessageProperties().getAuditIdentityByType(ITKIdentity.DEFAULT_IDENTITY_TYPE).equals(VALID_AUDITID));
		
		// Check the payload by XPATH
		try {
			Document vnnRequestDoc = DomUtils.parse(itkRequest.getBusinessPayload());
			
			// Check the payload
			String dob = XPaths.compileXPath("/hl7:getPatientDetailsByNHSNumberRequest-v1-0/hl7:queryEvent/hl7:Person.DateOfBirth/hl7:value/@value").evaluate(vnnRequestDoc);
			assertTrue(dob.equals(VALID_DOB));
			String given = XPaths.compileXPath("/hl7:getPatientDetailsByNHSNumberRequest-v1-0/hl7:queryEvent/hl7:Person.Name/hl7:value/hl7:given").evaluate(vnnRequestDoc);
			assertTrue(given.equals(VALID_GIVENNAME));
			String surname = XPaths.compileXPath("/hl7:getPatientDetailsByNHSNumberRequest-v1-0/hl7:queryEvent/hl7:Person.Name/hl7:value/hl7:family").evaluate(vnnRequestDoc);
			assertTrue(surname.equals(VALID_SURNAME));
			String nhsNumber = XPaths.compileXPath("/hl7:getPatientDetailsByNHSNumberRequest-v1-0/hl7:queryEvent/hl7:Person.NHSNumber/hl7:value/@extension").evaluate(vnnRequestDoc);
			assertTrue(nhsNumber.equals(VALID_NHSNUMBER));
			
		} catch (XPathException | SAXException | IOException | ParserConfigurationException e) {
			fail(e.getMessage());
		}
	}
	
	// TEST - CHECKING THE ITK RESPONSE
	public void testCheckITKResponse() throws ConfigurationException, LoggingException, ValidationException {
		
		GetPatientDetailsByNHSNumberOperation operation = getConfiguredOperation();
		GetPatientDetailsByNHSNumberRequest request = getValidRequest();
		ITKMessageSenderImpl_Mock sender = new ITKMessageSenderImpl_Mock();
		sender.primeResponse("HappyDayGPDResponse.xml");
		operation.setItkMessageSender(sender);

		GetPatientDetailsResponse response = operation.process(request);

		assertTrue(response.getConversationId().equals(request.getConversationId()));
		assertTrue(response.getResponseCode().equals("SMSP-0000"));
		assertTrue(response.getPerson().getNhsNumber().equals(VALID_NHSNUMBER));
		assertTrue(response.getPerson().getDateOfBirth().equals("19890101"));
		assertTrue(response.getPerson().getDateOfDeath().equals(""));
		assertTrue(response.getPerson().getGender().equals("1"));
		assertTrue(response.getPerson().getGivenName().equals("John"));
		assertTrue(response.getPerson().getSurname().equals("Smith"));
		assertTrue(response.getPerson().getOtherGivenName().equals(""));
		assertTrue(response.getPerson().getTitle().equals(""));
		assertTrue(response.getPerson().getAddress().getPostcode().equals("LS17 7TR"));
		assertTrue(response.getPerson().getAddress().getLine1().equals("41 Manor Grove"));
		assertTrue(response.getPerson().getAddress().getLine2().equals("Little Manorton"));
		assertTrue(response.getPerson().getAddress().getLine3().equals("Leeds"));
		assertTrue(response.getPerson().getAddress().getLine4().equals(""));
		assertTrue(response.getPerson().getAddress().getLine5().equals(""));
		assertTrue(response.getPerson().getPracticeCode().equals("YT4"));
		assertTrue(response.getPerson().getPracticeName().equals("Grove GP Surgery"));
		assertTrue(response.getPerson().getPracticeContactTelephoneNumber().equals(""));
		assertTrue(response.getPerson().getPracticeAddress().getPostcode().equals("LS17 8TY"));
		assertTrue(response.getPerson().getPracticeAddress().getLine1().equals("2 High Street"));
		assertTrue(response.getPerson().getPracticeAddress().getLine2().equals("Little Manorton"));
		assertTrue(response.getPerson().getPracticeAddress().getLine3().equals("Leeds"));
		assertTrue(response.getPerson().getPracticeAddress().getLine4().equals(""));
		assertTrue(response.getPerson().getPracticeAddress().getLine5().equals(""));
	}
	
	public void testCheckITKResponseNotFound() throws ConfigurationException, LoggingException, ValidationException {
		
		GetPatientDetailsByNHSNumberOperation operation = getConfiguredOperation();
		GetPatientDetailsByNHSNumberRequest request = getValidRequest();
		ITKMessageSenderImpl_Mock sender = new ITKMessageSenderImpl_Mock();
		sender.primeResponse("NotFoundGPDResponse.xml");
		operation.setItkMessageSender(sender);

		GetPatientDetailsResponse response = operation.process(request);
		assertTrue(response.getResponseCode().equals("DEMOG-0001"));
		assertFalse(response.getPerson().getNhsNumber().equals(VALID_NHSNUMBER));
	}

	public void testCheckITKResponseBusy() throws ConfigurationException, LoggingException, ValidationException {
		
		GetPatientDetailsByNHSNumberOperation operation = getConfiguredOperation();
		GetPatientDetailsByNHSNumberRequest request = getValidRequest();
		ITKMessageSenderImpl_Mock sender = new ITKMessageSenderImpl_Mock();
		sender.primeResponse(ITKMessageSenderImpl_Mock.BUSY);
		operation.setItkMessageSender(sender);

		GetPatientDetailsResponse response = operation.process(request);
		assertTrue(response.getResponseCode().equals("BUSY"));
	}
	
	public void testCheckITKResponseFail() throws ConfigurationException, LoggingException, ValidationException {
		
		GetPatientDetailsByNHSNumberOperation operation = getConfiguredOperation();
		GetPatientDetailsByNHSNumberRequest request = getValidRequest();
		ITKMessageSenderImpl_Mock sender = new ITKMessageSenderImpl_Mock();
		sender.primeResponse(ITKMessageSenderImpl_Mock.FAIL);
		operation.setItkMessageSender(sender);

		GetPatientDetailsResponse response = operation.process(request);
		assertTrue(response.getResponseCode().equals("FAILED"));
		
	}

	// TEST - Logging the SMSP Response
	public void testLogResponse() throws ConfigurationException, LoggingException, ValidationException {
		
		GetPatientDetailsByNHSNumberOperation operation = getConfiguredOperation();
		GetPatientDetailsByNHSNumberRequest request = getValidRequest();
		ITKMessageSenderImpl_Mock sender = new ITKMessageSenderImpl_Mock();
		sender.primeResponse("HappyDayGPDResponse.xml");
		operation.setItkMessageSender(sender);

		GetPatientDetailsResponse response = operation.process(request);
		// Check stored messages from Mock logging service
		SMSCLoggingService_Mock logger = (SMSCLoggingService_Mock) operation.smscLogger;
		assertTrue(logger.parm_SMSPRequest.size()==1);
		assertTrue(logger.parm_SMSPRequest.get(0)==request);
		assertTrue(logger.parm_SMSPResponse.size()==1);
		assertTrue(logger.parm_SMSPResponse.get(0).equals(response));
		assertTrue(logger.parm_SMSPResponse.get(0).getResponseCode().equals("SMSP-0000"));

	}
	
	public void testLogResponseBusy() throws ConfigurationException, LoggingException, ValidationException {
		
		GetPatientDetailsByNHSNumberOperation operation = getConfiguredOperation();
		GetPatientDetailsByNHSNumberRequest request = getValidRequest();
		
		// Prime the sender to be busy
		ITKMessageSenderImpl_Mock sender = new ITKMessageSenderImpl_Mock();
		sender.primeResponse(ITKMessageSenderImpl_Mock.BUSY);
		operation.setItkMessageSender(sender);

		GetPatientDetailsResponse response = operation.process(request);
		assertTrue(response.getResponseCode().equals("BUSY"));
		
		// Check stored messages from Mock logging service
		SMSCLoggingService_Mock logger = (SMSCLoggingService_Mock) operation.smscLogger;
		assertTrue(logger.parm_SMSPRequest.size()==1);
		assertTrue(logger.parm_SMSPRequest.get(0)==request);
		assertTrue(logger.parm_SMSPResponse.size()==1);
		assertTrue(logger.parm_SMSPResponse.get(0).equals(response));
		assertTrue(logger.parm_SMSPResponse.get(0).getResponseCode().equals("BUSY"));
		
	}
	
	public void testLogResponseFail() throws ConfigurationException, LoggingException, ValidationException {
		
		GetPatientDetailsByNHSNumberOperation operation = getConfiguredOperation();
		GetPatientDetailsByNHSNumberRequest request = getValidRequest();
		
		// Prime the sender to be busy
		ITKMessageSenderImpl_Mock sender = new ITKMessageSenderImpl_Mock();
		sender.primeResponse(ITKMessageSenderImpl_Mock.FAIL);
		operation.setItkMessageSender(sender);

		GetPatientDetailsResponse response = operation.process(request);
		assertTrue(response.getResponseCode().equals("FAILED"));
		
		// Check stored messages from Mock logging service
		SMSCLoggingService_Mock logger = (SMSCLoggingService_Mock) operation.smscLogger;
		assertTrue(logger.parm_SMSPRequest.size()==1);
		assertTrue(logger.parm_SMSPRequest.get(0)==request);
		assertTrue(logger.parm_SMSPResponse.size()==1);
		assertTrue(logger.parm_SMSPResponse.get(0).equals(response));
		assertTrue(logger.parm_SMSPResponse.get(0).getResponseCode().equals("FAILED"));
		
	}

	public void testLogResponseException() throws ConfigurationException, LoggingException, ValidationException {
		GetPatientDetailsByNHSNumberOperation operation = getConfiguredOperation();
		GetPatientDetailsByNHSNumberRequest request = getValidRequest();
		
		// Prime the sender to FAIL
		ITKMessageSenderImpl_Mock sender = new ITKMessageSenderImpl_Mock();
		sender.primeResponse(ITKMessageSenderImpl_Mock.FAIL);
		operation.setItkMessageSender(sender);
		
		// Prime the logger to FAIL
		SMSCLoggingService_Mock logger = new SMSCLoggingService_Mock();
		logger.primeSmspResponseOutcome(SMSCLoggingService_Mock.FAIL);
		operation.setSmscLogger(logger);

		try {
			operation.process(request);
			fail("No logging exception produced");
		} catch (LoggingException e){
			assertTrue(e.getMessage().contains(SMSCLoggingService_Mock.SMSP_RESPONSE_ERROR));
		}
		
	}

	// UTILS
	private GetPatientDetailsByNHSNumberOperation getConfiguredOperation(){
		GetPatientDetailsByNHSNumberOperation operation = new GetPatientDetailsByNHSNumberOperation();
		operation.setServiceProvider(SERVICEPROVIDER);
		operation.setServiceId(SERVICEID);
		operation.setProfileId(PROFILEID);
		operation.setSender(SENDER);
		operation.setToPayloadTransform(TOPAYLOADTRANSFORM);

		// Set up Mock services
		operation.setItkMessageSender(new ITKMessageSenderImpl_Mock());
		operation.setSmscLogger(new SMSCLoggingService_Mock());

		return operation;
	}
	private GetPatientDetailsByNHSNumberRequest getValidRequest(){
		GetPatientDetailsByNHSNumberRequest request = new GetPatientDetailsByNHSNumberRequest();
		request.setDateOfBirth(VALID_DOB);
		request.setSurname(VALID_SURNAME);
		request.setGivenName(VALID_GIVENNAME);
		request.setNHSNumber(VALID_NHSNUMBER);

		request.setLocalAuditId(VALID_AUDITID);

		return request;
	}
}
