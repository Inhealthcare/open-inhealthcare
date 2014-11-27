package uk.co.inhealthcare.open.itk.transport.WS;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import uk.co.inhealthcare.open.itk.capabilities.AuditService;
import uk.co.inhealthcare.open.itk.capabilities.SOAPAuditDetails;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessageProperties;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagePropertiesImpl;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.infrastructure.ITKUnavailableException;
import uk.co.inhealthcare.open.itk.payload.ITKMessage;
import uk.co.inhealthcare.open.itk.payload.ITKSimpleMessageImpl;
import uk.co.inhealthcare.open.itk.service.ITKSimpleAudit_Mock;
import uk.co.inhealthcare.open.itk.transport.ITKTransportRouteImpl;
import uk.co.inhealthcare.open.itk.transport.HTTP.ITKTransportSenderHTTPImpl_Mock;

public class ITKsenderWSImplUnitTest {

	private ITKSenderWSImpl wsSender;

	private ITKTransportSenderHTTPImpl_Mock itkHttpSender;
	private ITKSimpleAudit_Mock auditService;

	private final static String SERVICE_ID = "urn:nhs-itk:services:201005:getNHSNumber-v1-0";
	private final static String SERVICE_ENDPOINT = "http://localhost:8080/itk";

	private final static String SOAP_USERNAME = "JSAT";
	private final static String SOAP_FROM_ADDRESS = "http://127.0.0.1:4000/syncsoap";

	@Before
	public void setUp() throws Exception {
		// Configure the service
		itkHttpSender = new ITKTransportSenderHTTPImpl_Mock();
		auditService = new ITKSimpleAudit_Mock();

		wsSender = new ITKSenderWSImpl();
		wsSender.setSoapUsername(SOAP_USERNAME);
		wsSender.setSoapFromAddress(SOAP_FROM_ADDRESS);
		wsSender.setToSoapTransform("ToSOAP.xslt");
		wsSender.setTransportService(itkHttpSender);
		wsSender.setAuditService(auditService);
	}

	@SuppressWarnings("static-access")
	@Test
	public void testAuditFailure() {

		itkHttpSender.mockResponseCode = "200";

		ITKMessage request = new ITKSimpleMessageImpl();
		ITKMessageProperties props = new ITKMessagePropertiesImpl();
		props.setServiceId(SERVICE_ID);
		request.setMessageProperties(props);
		ITKTransportRouteImpl dest = new ITKTransportRouteImpl("WS",
				SERVICE_ENDPOINT);

		auditService.primeResponse(auditService.FAIL_SOAPREQUEST);
		try {
			wsSender.sendSync(dest, request);
			fail("AuditException should have been thrown");
		} catch (ITKMessagingException auditEx) {
			assertTrue(auditEx.getMessage().contains(
					"Error Auditing SOAP Request"));
		}

		auditService.primeResponse(auditService.FAIL_SOAPRESPONSE);
		try {
			wsSender.sendSync(dest, request);
			fail("AuditException should have been thrown");
		} catch (ITKMessagingException auditEx) {
			assertTrue(auditEx.getMessage().contains(
					"Error Auditing SOAP Response"));
		}

	}

	@Test
	public void testAudit() throws ITKMessagingException {

		itkHttpSender.mockResponseCode = "200";

		ITKMessage request = new ITKSimpleMessageImpl();
		ITKMessageProperties props = new ITKMessagePropertiesImpl();
		props.setServiceId(SERVICE_ID);
		request.setMessageProperties(props);
		ITKTransportRouteImpl dest = new ITKTransportRouteImpl("WS",
				SERVICE_ENDPOINT);

		wsSender.sendSync(dest, request);

		assertTrue(auditService.parm_requestAuditType.get(0).equals(
				AuditService.SOAPREQUEST));
		assertTrue(auditService.parm_requestAuditType.get(1).equals(
				AuditService.SOAPRESPONSE));
		SOAPAuditDetails soapRequestAudit = (SOAPAuditDetails) auditService.parm_requestAuditDetails
				.get(0);
		assertTrue(soapRequestAudit != null);
		// Only conversation Id and Transport details are audited for the SOAP
		// Request
		assertTrue(soapRequestAudit.getConversationId().equals(
				request.getConversationId()));
		assertTrue(soapRequestAudit.getCreationTime() != null);
		assertTrue(soapRequestAudit.getMessageId().length() == 36);
		assertFalse(soapRequestAudit.getMessageId().equals(
				request.getConversationId()));
		assertTrue(soapRequestAudit.getTo().equals(SERVICE_ENDPOINT));
		assertTrue(soapRequestAudit.getAction().equals(SERVICE_ID));
		assertTrue(soapRequestAudit.getUserId().equals(SOAP_USERNAME));

		SOAPAuditDetails soapResponseAudit = (SOAPAuditDetails) auditService.parm_requestAuditDetails
				.get(1);
		assertTrue(soapResponseAudit != null);
		assertTrue(soapResponseAudit.getConversationId().equals(
				request.getConversationId()));
		assertTrue(soapResponseAudit.getAction()
				.equals(SERVICE_ID + "Response"));
		assertTrue(soapResponseAudit.getStatus().equals("OK"));
		assertTrue(soapResponseAudit.getMessageId().length() == 36);
		// Transport id on the response should not be the same as the
		// conversation id
		assertFalse(soapResponseAudit.getMessageId().equals(
				request.getConversationId()));
		// Transport id on the response should not be the same as the outbound
		// transport id
		assertFalse(soapResponseAudit.getMessageId().equals(
				soapRequestAudit.getMessageId()));

	}

	@Test
	public void testSoapActionAndEndpoint() throws ITKMessagingException {

		itkHttpSender.mockResponseCode = "200";

		String serviceId = "TestServiceId";
		String serviceEndpoint = "http://localhost:8080/itk";

		ITKMessage request = new ITKSimpleMessageImpl();
		ITKMessageProperties props = new ITKMessagePropertiesImpl();
		props.setServiceId(serviceId);
		request.setMessageProperties(props);
		ITKTransportRouteImpl dest = new ITKTransportRouteImpl("WS",
				serviceEndpoint);

		wsSender.sendSync(dest, request);

		assertTrue(itkHttpSender.soapAction.equals(serviceId));
		assertTrue(itkHttpSender.serviceEndpoint.equals(serviceEndpoint));
	}

	public void testResponseFormat() throws ITKMessagingException {

		itkHttpSender.mockResponseCode = "200";

		String serviceId = "TestServiceId";
		String serviceEndpoint = "http://localhost:8080/itk";

		ITKMessage request = new ITKSimpleMessageImpl();
		ITKMessageProperties props = new ITKMessagePropertiesImpl();
		props.setServiceId(serviceId);
		request.setMessageProperties(props);
		ITKTransportRouteImpl dest = new ITKTransportRouteImpl("WS",
				serviceEndpoint);

		ITKMessage response = wsSender.sendSync(dest, request);

		assertTrue(response.getConversationId() != null);
		assertTrue(response.getConversationId().equalsIgnoreCase(
				request.getConversationId()));

	}

	public void testBusy() throws ITKMessagingException {

		((ITKTransportSenderHTTPImpl_Mock) itkHttpSender).mockResponseCode = "503";

		ITKMessage request = new ITKSimpleMessageImpl();
		ITKMessageProperties props = new ITKMessagePropertiesImpl();
		// props.setServiceId("TestServiceId");
		request.setMessageProperties(props);
		ITKTransportRouteImpl dest = new ITKTransportRouteImpl("WS",
				"http://localhost:8080/itk");
		try {
			wsSender.sendSync(dest, request);
			fail("Should throw an ITKUnavailableException");
		} catch (ITKUnavailableException e) {
			// Expect this
		}
	}

	public void testMissingProperties() {

		((ITKTransportSenderHTTPImpl_Mock) itkHttpSender).mockResponseCode = "503";

		ITKMessage request = new ITKSimpleMessageImpl();
		ITKTransportRouteImpl dest = new ITKTransportRouteImpl("WS", "");
		try {
			wsSender.sendSync(dest, request);
			fail("Should throw an ITKMessagingException");
		} catch (ITKMessagingException e) {
			// Expect this
		}
	}

	public void testNoDestinationOrRequest() {
		try {
			wsSender.sendSync(null, null);
			fail("Should throw an ITKMessagingException");
		} catch (ITKMessagingException e) {
			// Expect this
		}
	}

	public void testNoDestination() {
		try {
			ITKMessage request = new ITKSimpleMessageImpl();
			wsSender.sendSync(null, request);
			fail("Should throw an ITKMessagingException");
		} catch (ITKMessagingException e) {
			// Expect this
		}
	}

	public void testNoRequest() {
		try {
			ITKTransportRouteImpl dest = new ITKTransportRouteImpl("WS", "");
			wsSender.sendSync(dest, null);
			fail("Should throw an ITKMessagingException");
		} catch (ITKMessagingException e) {
			// Expect this
		}
	}

	public void testAsyncNotImplemented() throws ITKMessagingException {

		try {
			ITKTransportRouteImpl dest = new ITKTransportRouteImpl("WS", "");
			wsSender.sendAsync(dest, null);
			fail("Should throw an ITKMessagingException");
		} catch (ITKMessagingException e) {
			// Expect this
		}
	}

	public void testSendNotImplemented() throws ITKMessagingException {

		try {
			ITKTransportRouteImpl dest = new ITKTransportRouteImpl("WS", "");
			wsSender.send(dest, null);
			fail("Should throw an ITKMessagingException");
		} catch (ITKMessagingException e) {
			// Expect this
		}
	}

}
