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
import uk.co.inhealthcare.open.smsc.messages.GetNHSNumberRequest;
import uk.co.inhealthcare.open.smsc.messages.GetNHSNumberResponse;
import uk.co.inhealthcare.open.smsc.messages.logging.LoggingException;
import uk.co.inhealthcare.open.smsc.messages.logging.SMSCLoggingService_Mock;

/*
 * Unit
 * - This operation
 * - Related Messages (GetNHSNumberRequest/Response)
 * - Superclass Hierarchy
 * 
 * Responsibilities
 * - Accept a GetNHSNumberRequest
 * - Check configuration [TEST: All mandatory configurations are verified]
 * - Check parameters [TEST: All mandatory parameters are verified]
 * - Log the request [TEST: Message is logged / Logging Exception]
 * - Validate the request [TEST: All validations]
 * - Build the ITK Request
 * - Send the ITK Request [TEST: Get the request from the Mock. Check properties. Check message using XPATHS]
 * - Build Appropriate Response: Happy Day  / Not Found / Busy / Error [TEST: Trigger all 4 types]
 * - Log response [TEST: Message is logged / Logging Exception]
 * - Return the GetNHSNumber Response
 * 
 * Configuration Required
 * - Service: ITKMessageSender [Mandatory]
 * - Service: SMSCLoggingService [Mandatory]
 * - Value: serviceProvider (e.g. "urn:nhs-uk:addressing:ods:TKW") [Mandatory]
 * - Value: serviceId (e.g. "urn:nhs-itk:services:201005:getNHSNumber-v1-0") [Mandatory]
 * - Value: profileId (e.g. "urn:nhs-en:profile:getNHSNumberRequest-v1-0") [Mandatory]
 * - Value: sender (e.g. "urn:nhs-uk:addressing:ods:XXXX:JSAT") [Mandatory]
 * - Value: toPayloadTransform (e.g. "xslt/smsc/ToGetNHSNumberRequest.xslt") [Mandatory]
 * 
 * Mocks Required
 * - ITKMessageSender - store parameters and return 3 return types depending on serviceId
 * - SMSCLoggingService - store the message
 *  
 */
public class GetNHSNumberOperationUnitTest extends TestCase {
	
	private static final String VALID_DOB       = "19700630";
	private static final String VALID_SURNAME   = "SMITH";
	private static final String VALID_GIVENNAME = "FRED";
	private static final String VALID_GENDER    = "1";
	private static final String VALID_POSTCODE  = "CH1 1AA";
	private static final String VALID_AUDITID  = "TESTER1";

	private static final String SERVICEPROVIDER = "urn:nhs-uk:addressing:ods:TKW";
	private static final String SERVICEID = "urn:nhs-itk:services:201005:getNHSNumber-v1-0";
	private static final String PROFILEID = "urn:nhs-en:profile:getNHSNumberRequest-v1-0";
	private static final String SENDER = "urn:nhs-uk:addressing:ods:XXXX:JSAT";
	private static final String TOPAYLOADTRANSFORM = "xslt/smsc/ToGetNHSNumberRequest.xslt";

	// TEST CONFIGURATION

	public void testConfigMissingITKMessageSender() throws ValidationException, LoggingException {
		
		GetNHSNumberOperation operation = getConfiguredOperation();
		operation.setItkMessageSender(null);

		try {
			operation.process(null);
			fail("Should throw a ConfigurationException");
		} catch (ConfigurationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.ITKMESSAGESENDER_NOT_CONFIGURED));
		}
	}

	public void testConfigMissingServiceProvider() throws ValidationException, LoggingException {
		
		GetNHSNumberOperation operation = getConfiguredOperation();
		operation.setServiceProvider(null);

		try {
			operation.process(null);
			fail("Should throw a ConfigurationException");
		} catch (ConfigurationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.SERVICEPROVIDER_NOT_CONFIGURED));
		}
	}

	public void testConfigMissingServiceId() throws ValidationException, LoggingException {
		
		GetNHSNumberOperation operation = getConfiguredOperation();
		operation.setServiceId(null);

		try {
			operation.process(null);
			fail("Should throw a ConfigurationException");
		} catch (ConfigurationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.SERVICEID_NOT_CONFIGURED));
		}
	}

	public void testConfigMissingProfileId() throws ValidationException, LoggingException {
		
		GetNHSNumberOperation operation = getConfiguredOperation();
		operation.setProfileId(null);

		try {
			operation.process(null);
			fail("Should throw a ConfigurationException");
		} catch (ConfigurationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.PROFILEID_NOT_CONFIGURED));
		}
	}

	public void testConfigMissingSender() throws ValidationException, LoggingException {
		
		GetNHSNumberOperation operation = getConfiguredOperation();
		operation.setSender(null);

		try {
			operation.process(null);
			fail("Should throw a ConfigurationException");
		} catch (ConfigurationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.SENDER_NOT_CONFIGURED));
		}
	}

	public void testConfigMissingToPayloadTransform() throws ValidationException, LoggingException {
		
		GetNHSNumberOperation operation = getConfiguredOperation();
		operation.setToPayloadTransform(null);

		try {
			operation.process(null);
			fail("Should throw a ConfigurationException");
		} catch (ConfigurationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.TOPAYLOADTRANSFORM_NOT_CONFIGURED));
		}
	}

	public void testConfigMissingSMSCLogger() throws ValidationException, LoggingException {
		
		GetNHSNumberOperation operation = getConfiguredOperation();
		operation.setSmscLogger(null);

		try {
			operation.process(null);
			fail("Should throw a ConfigurationException");
		} catch (ConfigurationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.SMSCLOGGER_NOT_CONFIGURED));
		}
	}

	// TEST PARAMETERS

	public void testParmsMissingRequest() throws ValidationException, LoggingException {
		
		GetNHSNumberOperation operation = getConfiguredOperation();

		try {
			operation.process(null);
			fail("Should throw a ConfigurationException");
		} catch (ConfigurationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.ERR_REQUEST_NOT_PROVIDED));
		}
	}

	// TEST REQUEST LOGGING

	public void testRequestLoggingException() throws ConfigurationException, ValidationException {
		
		GetNHSNumberOperation operation = getConfiguredOperation();
		GetNHSNumberRequest request = new GetNHSNumberRequest();

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
		
		GetNHSNumberOperation operation = getConfiguredOperation();
		GetNHSNumberRequest request = getValidRequest();
		operation.process(request);

		// Check stored request from Mock logging service
		SMSCLoggingService_Mock logger = (SMSCLoggingService_Mock) operation.smscLogger;
		assertTrue(logger.parm_SMSPRequest.size()==1);
		assertTrue(logger.parm_SMSPRequest.get(0)==request);
		
	}

	// TEST REQUEST VALIDATION

	public void testValidationDOB() throws ConfigurationException, LoggingException {

		// Mandatory. For a trace this can be - YYYYMMDD / YYYYMM / YYYY 
		
		GetNHSNumberOperation operation = getConfiguredOperation();
		GetNHSNumberRequest request = getValidRequest();
		
		// Valid - as default message
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}
		// Valid - YYYYMM
		request.setDateOfBirth("197001");
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}
		// Valid - YYYY
		request.setDateOfBirth("1970");
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
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.DOBERROR_PREFIX));
		}
		// Invalid - Blank
		request.setDateOfBirth("");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.DOBERROR_PREFIX));
		}
		// Invalid - YY
		request.setDateOfBirth("70");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.DOBERROR_PREFIX));
		}
		// Invalid - YYYYM
		request.setDateOfBirth("19701");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.DOBERROR_PREFIX));
		}
		// Invalid - YYYYMMD
		request.setDateOfBirth("1970011");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.DOBERROR_PREFIX));
		}
		// Invalid - YYYYMM - Bad month
		request.setDateOfBirth("197013");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.DOBERROR_PREFIX));
		}
		// Invalid - YYYYMMDD - Bad day
		request.setDateOfBirth("19701232");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.DOBERROR_PREFIX));
		}
	}

	public void testValidationSurname() throws ConfigurationException, LoggingException {

		// Mandatory. Wildcards must be preceded by 2 characters
		
		GetNHSNumberOperation operation = getConfiguredOperation();
		GetNHSNumberRequest request = getValidRequest();
		
		// Valid - as default message
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}
		
		// Valid - Just 1 character
		request.setSurname("S");
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}

		// Valid - Just 2 characters + wildcard
		request.setSurname("SM*");
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}

		// Invalid - Null
		request.setSurname(null);
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.SURNAMEERROR_PREFIX));
		}
		// Invalid - Blank
		request.setSurname("");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.SURNAMEERROR_PREFIX));
		}
		// Invalid - Wildcard before character 3
		request.setSurname("S*");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.SURNAMEERROR_PREFIX));
		}
	}
	
	public void testValidationGivenName() throws ConfigurationException, LoggingException {

		// Given Name is optional for a TRACE
		// If present givenName can contain a wildcard ("*") - but only after two valid characters
		
		GetNHSNumberOperation operation = getConfiguredOperation();
		GetNHSNumberRequest request = getValidRequest();
		
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

		// Valid - Just 2 characters + wildcard
		request.setGivenName("FR*");
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}

		// Invalid - Wildcard before character 3
		request.setGivenName("F*");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.GIVENNAMEERROR_PREFIX));
		}
	}
	
	public void testValidationGender() throws ConfigurationException, LoggingException {

		// Gender is mandatory for a TRACE
		// Configuration of valid values is fixed at 0/1/2/9 and tested as such here. 
		
		GetNHSNumberOperation operation = getConfiguredOperation();
		GetNHSNumberRequest request = getValidRequest();
		
		// Valid - as default message ("1")
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}
		
		// Valid - "0"
		request.setGender("0");
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}
		// Valid - "2"
		request.setGender("2");
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}
		// Valid - "9"
		request.setGender("9");
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}

		// Invalid - Missing - Null
		request.setGender(null);
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.GENDERERROR_PREFIX));
		}
	
		// Invalid - Missing - Blank
		request.setGender("");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.GENDERERROR_PREFIX));
		}

		// Invalid - Bad Value
		request.setGender("3");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.GENDERERROR_PREFIX));
		}

	}

	public void testValidationPostcode() throws ConfigurationException, LoggingException {

		// Postcode is optional for a TRACE
		// If present it can contain a wildcard ("*") - but only after two valid characters
		// If present it can't be longer than 8 characters
		// Only alphanumeric characters
		// Only 1 space allowed
		
		GetNHSNumberOperation operation = getConfiguredOperation();
		GetNHSNumberRequest request = getValidRequest();
		
		// Valid - as default message
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}
		
		// Valid - With wildcard
		request.setPostcode("CH*");
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}

		// Valid - Missing - Null
		request.setPostcode(null);
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}

		// Valid - Missing - Blank
		request.setPostcode("");
		try {
			operation.process(request);
		} catch (ValidationException e) {
			fail("Unexpected ValidationException:"+e.getMessage());
		}

		// Invalid - Wildcard too early
		request.setPostcode("C*");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.POSTCODEERROR_PREFIX));
		}

		// Invalid - Special Characters
		request.setPostcode("CH1 1A£");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.POSTCODEERROR_PREFIX));
		}

		// Invalid - Longer than 8
		request.setPostcode("CH10 1AAA");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.POSTCODEERROR_PREFIX));
		}

		// Invalid - Two spaces
		request.setPostcode("CH1  1AA");
		try {
			operation.process(request);
			fail("Should throw a ValidationException");
		} catch (ValidationException e) {
			assertTrue(e.getMessage().contains(GetNHSNumberOperation.POSTCODEERROR_PREFIX));
		}
		
	}
	
	// TEST - SENDING THE ITK REQUEST
	public void testITKSend() throws ConfigurationException, LoggingException, ValidationException{
		String conversationId = UUID.randomUUID().toString().toUpperCase();
		GetNHSNumberOperation operation = getConfiguredOperation();
		GetNHSNumberRequest request = getValidRequest();
		request.setConversationId(conversationId);
		ITKMessageSenderImpl_Mock sender = new ITKMessageSenderImpl_Mock();
		sender.primeResponse("HappyDayGNNResponse.xml");
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
			Document gnnRequestDoc = DomUtils.parse(itkRequest.getBusinessPayload());
			
			// Check the payload
			String dob = XPaths.compileXPath("/hl7:getNHSNumberRequest-v1-0/hl7:queryEvent/hl7:Person.DateOfBirth/hl7:value/@value").evaluate(gnnRequestDoc);
			assertTrue(dob.equals(VALID_DOB));
			String gender = XPaths.compileXPath("/hl7:getNHSNumberRequest-v1-0/hl7:queryEvent/hl7:Person.Gender/hl7:value/@code").evaluate(gnnRequestDoc);
			assertTrue(gender.equals(VALID_GENDER));
			String given = XPaths.compileXPath("/hl7:getNHSNumberRequest-v1-0/hl7:queryEvent/hl7:Person.Name/hl7:value/hl7:given").evaluate(gnnRequestDoc);
			assertTrue(given.equals(VALID_GIVENNAME));
			String surname = XPaths.compileXPath("/hl7:getNHSNumberRequest-v1-0/hl7:queryEvent/hl7:Person.Name/hl7:value/hl7:family").evaluate(gnnRequestDoc);
			assertTrue(surname.equals(VALID_SURNAME));
			String postcode = XPaths.compileXPath("/hl7:getNHSNumberRequest-v1-0/hl7:queryEvent/hl7:Person.Postcode/hl7:value/hl7:postalCode").evaluate(gnnRequestDoc);
			assertTrue(postcode.equals(VALID_POSTCODE));
			
		} catch (XPathException | SAXException | IOException | ParserConfigurationException e) {
			fail(e.getMessage());
		}
	}
	
	// TEST - CHECKING THE ITK RESPONSE
	public void testCheckITKResponse() throws ConfigurationException, LoggingException, ValidationException {
		
		GetNHSNumberOperation operation = getConfiguredOperation();
		GetNHSNumberRequest request = getValidRequest();
		ITKMessageSenderImpl_Mock sender = new ITKMessageSenderImpl_Mock();
		sender.primeResponse("HappyDayGNNResponse.xml");
		operation.setItkMessageSender(sender);

		GetNHSNumberResponse response = operation.process(request);
		assertTrue(response.getResponseCode().equals("SMSP-0000"));
		assertTrue(response.getNhsNumber().equals("9999345201"));
		assertTrue(response.getLocalIdentifier().equals("RXX:00001"));
		assertTrue(response.getConversationId().equals(request.getConversationId()));
	}
	
	public void testCheckITKResponseNotFound() throws ConfigurationException, LoggingException, ValidationException {
		
		GetNHSNumberOperation operation = getConfiguredOperation();
		GetNHSNumberRequest request = getValidRequest();
		ITKMessageSenderImpl_Mock sender = new ITKMessageSenderImpl_Mock();
		sender.primeResponse("NotFoundGNNResponse.xml");
		operation.setItkMessageSender(sender);

		GetNHSNumberResponse response = operation.process(request);
		assertTrue(response.getResponseCode().equals("DEMOG-0001"));
		assertTrue(response.getNhsNumber().length()==0);
		assertTrue(response.getLocalIdentifier().length()==0);
	}

	public void testCheckITKResponseBusy() throws ConfigurationException, LoggingException, ValidationException {
		
		GetNHSNumberOperation operation = getConfiguredOperation();
		GetNHSNumberRequest request = getValidRequest();
		ITKMessageSenderImpl_Mock sender = new ITKMessageSenderImpl_Mock();
		sender.primeResponse(ITKMessageSenderImpl_Mock.BUSY);
		operation.setItkMessageSender(sender);

		GetNHSNumberResponse response = operation.process(request);
		assertTrue(response.getResponseCode().equals("BUSY"));
	}
	
	public void testCheckITKResponseFail() throws ConfigurationException, LoggingException, ValidationException {
		
		GetNHSNumberOperation operation = getConfiguredOperation();
		GetNHSNumberRequest request = getValidRequest();
		ITKMessageSenderImpl_Mock sender = new ITKMessageSenderImpl_Mock();
		sender.primeResponse(ITKMessageSenderImpl_Mock.FAIL);
		operation.setItkMessageSender(sender);

		GetNHSNumberResponse response = operation.process(request);
		assertTrue(response.getResponseCode().equals("FAILED"));
		
	}

	// TEST - Logging the SMSP Response
	public void testLogResponse() throws ConfigurationException, LoggingException, ValidationException {
		
		GetNHSNumberOperation operation = getConfiguredOperation();
		GetNHSNumberRequest request = getValidRequest();
		ITKMessageSenderImpl_Mock sender = new ITKMessageSenderImpl_Mock();
		sender.primeResponse("HappyDayGNNResponse.xml");
		operation.setItkMessageSender(sender);

		GetNHSNumberResponse response = operation.process(request);
		// Check stored messages from Mock logging service
		SMSCLoggingService_Mock logger = (SMSCLoggingService_Mock) operation.smscLogger;
		assertTrue(logger.parm_SMSPRequest.size()==1);
		assertTrue(logger.parm_SMSPRequest.get(0)==request);
		assertTrue(logger.parm_SMSPResponse.size()==1);
		assertTrue(logger.parm_SMSPResponse.get(0).equals(response));
		assertTrue(logger.parm_SMSPResponse.get(0).getResponseCode().equals("SMSP-0000"));

	}
	
	public void testLogResponseBusy() throws ConfigurationException, LoggingException, ValidationException {
		
		GetNHSNumberOperation operation = getConfiguredOperation();
		GetNHSNumberRequest request = getValidRequest();
		
		// Prime the sender to be busy
		ITKMessageSenderImpl_Mock sender = new ITKMessageSenderImpl_Mock();
		sender.primeResponse(ITKMessageSenderImpl_Mock.BUSY);
		operation.setItkMessageSender(sender);

		GetNHSNumberResponse response = operation.process(request);
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
		
		GetNHSNumberOperation operation = getConfiguredOperation();
		GetNHSNumberRequest request = getValidRequest();
		
		// Prime the sender to be busy
		ITKMessageSenderImpl_Mock sender = new ITKMessageSenderImpl_Mock();
		sender.primeResponse(ITKMessageSenderImpl_Mock.FAIL);
		operation.setItkMessageSender(sender);

		GetNHSNumberResponse response = operation.process(request);
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
		GetNHSNumberOperation operation = getConfiguredOperation();
		GetNHSNumberRequest request = getValidRequest();
		
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
	private GetNHSNumberOperation getConfiguredOperation(){
		GetNHSNumberOperation operation = new GetNHSNumberOperation();
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
	private GetNHSNumberRequest getValidRequest(){
		GetNHSNumberRequest request = new GetNHSNumberRequest();
		request.setDateOfBirth(VALID_DOB);
		request.setSurname(VALID_SURNAME);
		request.setGivenName(VALID_GIVENNAME);
		request.setGender(VALID_GENDER);
		request.setPostcode(VALID_POSTCODE);

		request.setLocalAuditId(VALID_AUDITID);

		return request;
	}
}
