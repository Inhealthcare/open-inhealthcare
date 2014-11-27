package uk.co.inhealthcare.open.itk.source;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import uk.co.inhealthcare.open.itk.capabilities.AuditService;
import uk.co.inhealthcare.open.itk.capabilities.ITKAuditDetails;
import uk.co.inhealthcare.open.itk.infrastructure.ITKAddress;
import uk.co.inhealthcare.open.itk.infrastructure.ITKAddressImpl;
import uk.co.inhealthcare.open.itk.infrastructure.ITKIdentity;
import uk.co.inhealthcare.open.itk.infrastructure.ITKIdentityImpl;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessageProperties;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagePropertiesImpl;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.payload.ITKMessage;
import uk.co.inhealthcare.open.itk.payload.ITKSimpleMessageImpl;
import uk.co.inhealthcare.open.itk.service.ITKSimpleAudit_Mock;
import uk.co.inhealthcare.open.itk.service.ITKSimpleDOSImpl;
import uk.co.inhealthcare.open.itk.test.TestUtils;
import uk.co.inhealthcare.open.itk.transport.WS.ITKSenderWSImpl_Mock;

public class ITKMessageSenderImplUnitTest {

	private final static String SERVICE_ID = "urn:nhs-itk:services:201005:testServiceA-v1-0";
	private final static String SERVICE_ID_NO_SYNC = "urn:nhs-itk:services:201005:testServiceD-v1-0";
	private final static String PROFILE_ID = "testProfile";
	private final static String SERVICE_PROVIDER = "urn:nhs-uk:addressing:ods:TKW";
	private final static String FROM_ADDRESS = "fromAddress";
	
	private final static String RESPONSE_SERVICE_ID = "urn:nhs-itk:services:201005:getNHSNumber-v1-0Response";
	private final static String RESPONSE_TRACKING_ID = "2D37D9CA-5223-41C7-A159-F33D5A914EB5";
	private final static String RESPONSE_LOCAL_AUDIT_ID = "urn:nhs-uk:addressing:ods:XXXX:team1:C";
	private final static String RESPONSE_ITK_PAYLOAD_ID = "0808A967-49B2-498B-AD75-1D7A0F1262D7";
	private final static String RESPONSE_PROFILE_ID = "urn:nhs-en:profile:getNHSNumberResponse-v1-0";
	private final static String RESPONSE_SENDER_ADDRESS = "urn:nhs-uk:addressing:ods:XXXX:team1:C";

	private final static String LOCAL_USER_ID = "LocalUserId.0001";
	private final static String SPINE_RP_ID = "SPINERPId.0001";
	private final static String SPINE_ROLE_ID = "SPINEROLEId.0001";
	private final static String SPINE_USER_ID = "SPINEUSERId.0001";

	private final static String LOCAL_PATIENT_ID = "LocalPatientId.0001";
	private final static String NHS_NUMBER = "1234512345";

	private ITKMessageSenderImpl sender;

	@Before
	public void setup() throws Exception {

		// Configure Sender
		ITKSimpleDOSImpl itkSimpleDOS = new ITKSimpleDOSImpl();
		TestUtils.loadTestProperties(itkSimpleDOS);
		sender = new ITKMessageSenderImpl();
		sender.setDirectoryOfServices(itkSimpleDOS);

	}

	@Test
	public void testAsyncNotImplemented() throws Exception {
		
		sender.setItkSenderWS(new ITKSenderWSImpl_Mock());

		try {
			sender.sendAsync(getGoodRequest());
			fail("Should throw an ITKMessagingException");
		} catch (ITKMessagingException e) {
			assertTrue(e.getMessage().contains("Async Not Yet Implemented"));
		}
	}

	@Test
	public void testSendNotImplemented() throws ITKMessagingException {
		
		//Configure Sender
		sender.setItkSenderWS(new ITKSenderWSImpl_Mock());

		try {
			sender.send(getGoodRequest());
			fail("Should throw an ITKMessagingException");
		} catch (ITKMessagingException e) {
			assertTrue(e.getMessage().contains("Send Not Yet Implemented"));
		}
	}

	@Test
	public void testNoRequest() {
		//Configure Sender
		sender.setItkSenderWS(new ITKSenderWSImpl_Mock());

		try {
			sender.sendSync(null);
			fail("Should throw an ITKMessagingException");
		} catch (ITKMessagingException e) {
			assertTrue(e.getMessage().contains("Request is null"));
		}
	}

	@Test
	public void testMissingProperties() {
		//Configure Sender
		sender.setItkSenderWS(new ITKSenderWSImpl_Mock());

		ITKMessage request = new ITKSimpleMessageImpl();
		try {
			sender.sendSync(request);
			fail("Should throw an ITKMessagingException");
		} catch (ITKMessagingException e) {
			assertTrue(e.getMessage().contains("Message Properties are null"));
		}
	}

	@Test
	public void testNoServiceId() {
		//Configure Sender
		sender.setItkSenderWS(new ITKSenderWSImpl_Mock());
		
		ITKMessage request = new ITKSimpleMessageImpl();
		ITKMessageProperties props = new ITKMessagePropertiesImpl();
		request.setMessageProperties(props);

		try {
			sender.sendSync(request);
			fail("Should throw an ITKMessagingException");
		} catch (ITKMessagingException e) {
			assertTrue(e.getMessage().contains("Service Id is null"));
		}
	}

	@Test
	public void testNoSender() throws ITKMessagingException {
		try {
			sender.sendSync(getGoodRequest());
			fail("Should throw an ITKMessagingException");
		} catch (ITKMessagingException e) {
			assertTrue(e.getMessage().contains("ITK Sender has not been configured"));
		}
	}

	@Test
	public void testSendNoServiceId() throws ITKMessagingException {

		ITKAddress provider = new ITKAddressImpl(SERVICE_PROVIDER);
		
		ITKMessage request = new ITKSimpleMessageImpl();
		ITKMessageProperties props = new ITKMessagePropertiesImpl();
		request.setMessageProperties(props);
		props.setToAddress(provider);
		
		//Configure Sender
		sender.setItkSenderWS(new ITKSenderWSImpl_Mock());
		try {
			sender.sendSync(request);
			fail("Should throw an ITKMessagingException");
		} catch (ITKMessagingException e) {
			assertTrue(e.getMessage().contains("Service Id is null"));
		}
	}

	@Test
	public void testSendNoProfileId() throws ITKMessagingException {

		ITKAddress provider = new ITKAddressImpl(SERVICE_PROVIDER);
		
		ITKMessage request = new ITKSimpleMessageImpl();
		ITKMessageProperties props = new ITKMessagePropertiesImpl();
		request.setMessageProperties(props);
		props.setToAddress(provider);
		props.setServiceId(SERVICE_ID);
		
		//Configure Sender
		sender.setItkSenderWS(new ITKSenderWSImpl_Mock());
		try {
			sender.sendSync(request);
			fail("Should throw an ITKMessagingException");
		} catch (ITKMessagingException e) {
			assertTrue(e.getMessage().contains("Profile Id is null"));
		}
	}

	@Test
	public void testSendNoFromAddress1() throws ITKMessagingException {

		// From address not set
		ITKAddress provider = new ITKAddressImpl(SERVICE_PROVIDER);
		
		ITKMessage request = new ITKSimpleMessageImpl();
		ITKMessageProperties props = new ITKMessagePropertiesImpl();
		request.setMessageProperties(props);
		props.setToAddress(provider);
		props.setServiceId(SERVICE_ID);
		props.setProfileId(PROFILE_ID);
		
		//Configure Sender
		sender.setItkSenderWS(new ITKSenderWSImpl_Mock());
		try {
			sender.sendSync(request);
			fail("Should throw an ITKMessagingException");
		} catch (ITKMessagingException e) {
			assertTrue(e.getMessage().contains("From Address is null"));
		}
	}

	@Test
	public void testSendNoFromAddress2() throws ITKMessagingException {

		// From address set but not properly
		ITKAddress provider = new ITKAddressImpl(SERVICE_PROVIDER);
		
		ITKMessage request = new ITKSimpleMessageImpl();
		ITKMessageProperties props = new ITKMessagePropertiesImpl();
		request.setMessageProperties(props);
		props.setToAddress(provider);
		props.setServiceId(SERVICE_ID);
		props.setProfileId(PROFILE_ID);
		props.setFromAddress(new ITKAddressImpl(null));
		
		//Configure Sender
		sender.setItkSenderWS(new ITKSenderWSImpl_Mock());
		try {
			sender.sendSync(request);
			fail("Should throw an ITKMessagingException");
		} catch (ITKMessagingException e) {
			assertTrue(e.getMessage().contains("From Address is null"));
		}
	}
	
	@Test
	public void testSendBadResponseProfile() throws ITKMessagingException {

		// From address set but not properly
		ITKAddress provider = new ITKAddressImpl(SERVICE_PROVIDER);
		
		ITKMessage request = new ITKSimpleMessageImpl();
		ITKMessageProperties props = new ITKMessagePropertiesImpl();
		request.setMessageProperties(props);
		props.setToAddress(provider);
		props.setServiceId(SERVICE_ID);
		props.setProfileId(PROFILE_ID);
		props.setFromAddress(new ITKAddressImpl(FROM_ADDRESS));
		
		//Configure Sender
		ITKSenderWSImpl_Mock itkSender = new ITKSenderWSImpl_Mock();
		itkSender.primeResponse("BadProfileDE.xml");
		sender.setItkSenderWS(itkSender);
		try {
			sender.sendSync(request);
			fail("Should throw an ITKMessagingException");
		} catch (ITKMessagingException e) {
			assertTrue(e.getMessage().contains("ProfileId not supported"));
		}
	}

	@SuppressWarnings("static-access")
	@Test
	public void testAuditFailure()  {
		
		ITKAddress provider = new ITKAddressImpl(SERVICE_PROVIDER);
		
		ITKMessage request = new ITKSimpleMessageImpl();
		ITKMessageProperties props = new ITKMessagePropertiesImpl();
		request.setMessageProperties(props);
		props.setToAddress(provider);
		props.setServiceId(SERVICE_ID);
		props.setProfileId(PROFILE_ID);
		props.setFromAddress(new ITKAddressImpl(FROM_ADDRESS));
		
		//Configure Sender
		sender.setItkSenderWS(new ITKSenderWSImpl_Mock());
		
		ITKSimpleAudit_Mock auditService = new ITKSimpleAudit_Mock();
		auditService.primeResponse(auditService.FAIL_ITKREQUEST);
		sender.setAuditService(auditService);

		try {
			sender.sendSync(request);
			fail("Should throw an ITKMessagingException");
		} catch (ITKMessagingException auditEx) {
			assertTrue(auditEx.getMessage().contains("Error Auditing ITK Request"));
		}
		
		auditService.primeResponse(auditService.FAIL_ITKRESPONSE);
		sender.setAuditService(auditService);

		try {
			sender.sendSync(request);
			fail("Should throw an ITKMessagingException");
		} catch (ITKMessagingException auditEx) {
			assertTrue(auditEx.getMessage().contains("Error Auditing ITK Response"));
		}

	}

	@Test
	public void testAudit() throws ITKMessagingException {
		ITKAddress provider = new ITKAddressImpl(SERVICE_PROVIDER);
		
		ITKMessage request = new ITKSimpleMessageImpl();
		ITKMessageProperties props = new ITKMessagePropertiesImpl();
		request.setMessageProperties(props);
		props.setToAddress(provider);
		props.setServiceId(SERVICE_ID);
		props.setProfileId(PROFILE_ID);
		props.setFromAddress(new ITKAddressImpl(FROM_ADDRESS));
		
		props.addAuditIdentity(new ITKIdentityImpl(LOCAL_USER_ID));
		props.addAuditIdentity(new ITKIdentityImpl(SPINE_ROLE_ID,ITKIdentity.SPINE_ROLE_TYPE));
		props.addAuditIdentity(new ITKIdentityImpl(SPINE_RP_ID,ITKIdentity.SPINE_ROLE_PROFILE_TYPE));
		props.addAuditIdentity(new ITKIdentityImpl(SPINE_USER_ID,ITKIdentity.SPINE_UUID_TYPE));
		
		
		props.addPatientIdentity(new ITKIdentityImpl(LOCAL_PATIENT_ID, ITKIdentity.LOCAL_PATIENT_TYPE));
		props.addPatientIdentity(new ITKIdentityImpl(NHS_NUMBER, ITKIdentity.NHS_NUMBER_TYPE));
		
		//Configure Sender
		sender.setItkSenderWS(new ITKSenderWSImpl_Mock());

		ITKSimpleAudit_Mock auditService = new ITKSimpleAudit_Mock();
		sender.setAuditService(auditService);
		
		ITKMessage response = sender.sendSync(request);
		assertTrue(response!=null);
		
		// Check the audited request
		assertTrue(auditService.parm_requestAuditType.get(0).equals(AuditService.ITKREQUEST));
		ITKAuditDetails itkRequestAudit = (ITKAuditDetails) auditService.parm_requestAuditDetails.get(0);
		assertTrue(itkRequestAudit!=null);
		assertTrue(itkRequestAudit.getPayloadId().equals(request.getMessageProperties().getItkPayloadId()));
		assertTrue(itkRequestAudit.getConversationId().equals(request.getConversationId()));
		assertTrue(itkRequestAudit.getServiceId().equals(SERVICE_ID));
		assertTrue(itkRequestAudit.getProfileId().equals(PROFILE_ID));
		assertTrue(itkRequestAudit.getTrackingId().equals(request.getMessageProperties().getTrackingId()));
		assertTrue(itkRequestAudit.getSenderAddress().equals(request.getMessageProperties().getFromAddress().getURI()));
		assertTrue(itkRequestAudit.getLocalAuditId().equals(LOCAL_USER_ID));
		assertTrue(itkRequestAudit.getLocalPatientId().equals(LOCAL_PATIENT_ID));
		assertTrue(itkRequestAudit.getNhsNumber().equals(NHS_NUMBER));

		// Check the audit response
		assertTrue(auditService.parm_requestAuditType.get(1).equals(AuditService.ITKRESPONSE));
		ITKAuditDetails itkResponseAudit = (ITKAuditDetails) auditService.parm_requestAuditDetails.get(1);
		assertTrue(itkResponseAudit!=null);
		assertTrue(itkResponseAudit.getConversationId().equals(request.getConversationId()));

		assertTrue(itkResponseAudit.getServiceId().equals(RESPONSE_SERVICE_ID));
		assertTrue(itkResponseAudit.getLocalAuditId().equals(RESPONSE_LOCAL_AUDIT_ID));
		assertTrue(itkResponseAudit.getPayloadId().equals(RESPONSE_ITK_PAYLOAD_ID));
		assertTrue(itkResponseAudit.getProfileId().equals(RESPONSE_PROFILE_ID));
		assertTrue(itkResponseAudit.getTrackingId().equals(RESPONSE_TRACKING_ID));
		assertTrue(itkResponseAudit.getSenderAddress().equals(RESPONSE_SENDER_ADDRESS));
	}

	@Test
	public void testSyncNotSupported() throws ITKMessagingException {
		ITKAddress provider = new ITKAddressImpl(SERVICE_PROVIDER);
		
		ITKMessage request = new ITKSimpleMessageImpl();
		ITKMessageProperties props = new ITKMessagePropertiesImpl();
		request.setMessageProperties(props);
		props.setToAddress(provider);
		props.setServiceId(SERVICE_ID_NO_SYNC);
		props.setProfileId(PROFILE_ID);
		props.setFromAddress(new ITKAddressImpl(FROM_ADDRESS));
		
		//Configure Sender
		sender.setItkSenderWS(new ITKSenderWSImpl_Mock());
		sender.setAuditService(new ITKSimpleAudit_Mock());
		
		try {
			sender.sendSync(request);
			fail("Should throw an ITKMessagingException");
		} catch (ITKMessagingException auditEx) {
			assertTrue(auditEx.getMessage().contains("can not be called Synchronously"));
		}

	}

	@Test
	public void testSend() throws ITKMessagingException {
		
		//Configure Sender
		sender.setItkSenderWS(new ITKSenderWSImpl_Mock());
		sender.setAuditService(new ITKSimpleAudit_Mock());
		
		ITKMessage response = sender.sendSync(getGoodRequest());
		assertTrue(response!=null);
	}

	private ITKMessage getGoodRequest(){
		ITKAddress provider = new ITKAddressImpl(SERVICE_PROVIDER);
		
		ITKMessage request = new ITKSimpleMessageImpl();
		ITKMessageProperties props = new ITKMessagePropertiesImpl();
		request.setMessageProperties(props);
		props.setToAddress(provider);
		props.setServiceId(SERVICE_ID);
		props.setProfileId(PROFILE_ID);
		props.setFromAddress(new ITKAddressImpl(FROM_ADDRESS));
		
		return request;
	}
}
