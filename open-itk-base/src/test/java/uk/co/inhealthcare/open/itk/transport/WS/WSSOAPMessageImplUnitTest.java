package uk.co.inhealthcare.open.itk.transport.WS;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.xml.xpath.XPathConstants;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import uk.co.inhealthcare.open.itk.infrastructure.ITKAddressImpl;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagePropertiesImpl;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.payload.ITKMessage;
import uk.co.inhealthcare.open.itk.payload.ITKSimpleMessageImpl;
import uk.co.inhealthcare.open.itk.service.ITKSimpleDOSImpl;
import uk.co.inhealthcare.open.itk.test.TestUtils;
import uk.co.inhealthcare.open.itk.transport.ITKTransportProperties;
import uk.co.inhealthcare.open.itk.transport.ITKTransportPropertiesImpl;
import uk.co.inhealthcare.open.itk.transport.ITKTransportRoute;
import uk.co.inhealthcare.open.itk.util.xml.DomUtils;
import uk.co.inhealthcare.open.itk.util.xml.XPaths;

public class WSSOAPMessageImplUnitTest {
	
	private final static String SERVICE_ID = "urn:nhs-itk:services:201005:testServiceA-v1-0";
	private final static String PROVIDER_EXPLICIT = "urn:nhs-uk:addressing:ods:EXPLICIT";
	private final static String PROVIDER_DEFAULT = "urn:nhs-uk:addressing:ods:TKW";

	
	@Test
	public void testConstructorNullDestination() {
		ITKMessage msg = new ITKSimpleMessageImpl();
		
		try {
			new WSSOAPMessageImpl(null,msg,WSSOAPMessageImpl.SYNCREQ);
			fail("Blank destination not detected.");
		} catch (ITKMessagingException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testConstructorNullMsgProperties() throws Exception {
		ITKMessage msg = new ITKSimpleMessageImpl();
		ITKSimpleDOSImpl dos = new ITKSimpleDOSImpl();
		TestUtils.loadTestProperties(dos);
		ITKTransportRoute destination = null;
		
		try {
			destination = dos.resolveDestination(SERVICE_ID, new ITKAddressImpl(PROVIDER_DEFAULT));
		} catch (ITKMessagingException e1) {
			fail("Failed to find known destination");
		}
		
		try {
			new WSSOAPMessageImpl(destination,msg,WSSOAPMessageImpl.SYNCREQ);
			fail("Failed to detect null message properties");
		} catch (ITKMessagingException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testConstructor() throws Exception {

		ITKMessage msg = new ITKSimpleMessageImpl();
		msg.setMessageProperties(new ITKMessagePropertiesImpl());
		msg.getMessageProperties().setServiceId(SERVICE_ID);
		
		ITKSimpleDOSImpl dos = new ITKSimpleDOSImpl();
		TestUtils.loadTestProperties(dos);
		ITKTransportRoute destination = null;
		
		try {
			destination = dos.resolveDestination(SERVICE_ID, new ITKAddressImpl(PROVIDER_EXPLICIT));
		} catch (ITKMessagingException e1) {
			fail("Failed to find known destination");
		}
		
		WSSOAPMessageImpl soapMsg = null;
		try {
			soapMsg = new WSSOAPMessageImpl(destination,msg,WSSOAPMessageImpl.SYNCREQ);
		} catch (ITKMessagingException e) {
			fail("Failed to build SOAP Message");
		}
		
		assertTrue(soapMsg.getTransportProperties().getTransportAction().equals(SERVICE_ID));
		assertTrue(soapMsg.getTransportProperties().getTransportTo().equals("https://localhost:4848/Explicit"));
		assertTrue(soapMsg.getTransportProperties().getTransportMessageId().length()==36);
		assertTrue(soapMsg.getTransportProperties().getTransportFaultTo().equals("ExplicitExceptionTo.com"));
		assertTrue(soapMsg.getTransportProperties().getTransportReplyTo().equals("ExplicitReplyTo.com"));
		assertTrue(soapMsg.getMsgType().equals(WSSOAPMessageImpl.SYNCREQ));
		assertTrue(soapMsg.getTimeToLive()==360000);
		
	}
	@Test
	public void testBuildFullMessageNoTemplate() throws Exception {

		ITKMessage msg = new ITKSimpleMessageImpl();
		msg.setMessageProperties(new ITKMessagePropertiesImpl());
		msg.getMessageProperties().setServiceId(SERVICE_ID);
		
		ITKSimpleDOSImpl dos = new ITKSimpleDOSImpl();
		TestUtils.loadTestProperties(dos);
		ITKTransportRoute destination = null;
		
		try {
			destination = dos.resolveDestination(SERVICE_ID, new ITKAddressImpl(PROVIDER_EXPLICIT));
		} catch (ITKMessagingException e1) {
			fail("Failed to find known destination");
		}
		
		WSSOAPMessageImpl soapMsg = null;
		try {
			soapMsg = new WSSOAPMessageImpl(destination,msg,WSSOAPMessageImpl.SYNCREQ);
		} catch (ITKMessagingException e) {
			fail("Failed to build SOAP Message");
		}
		
		try {
			soapMsg.buildFullMessage("");
			fail("Failed to detect empty template name");
		} catch (ITKMessagingException e) {
			assertTrue(true);
		}
		try {
			soapMsg.buildFullMessage(null);
			fail("Failed to detect null template name");
		} catch (ITKMessagingException e) {
			assertTrue(true);
		}
	}
	@Test
	public void testBuildFullMessage() throws Exception {

		// As set from a directory.properties lookup
		String soapToAddress = "https://localhost:4848/Explicit";
		
		// As configured in the context for the ITKSenderWSImpl
		String soapFromAddress = "http://127.0.0.1:4000/syncsoap";
		String soapUsername = "JSAT";
		String toSoapTransform = "ToSOAP.xslt";
		
		ITKMessage msg = new ITKSimpleMessageImpl();
		msg.setMessageProperties(new ITKMessagePropertiesImpl());
		msg.getMessageProperties().setServiceId(SERVICE_ID);
		msg.setBusinessPayload("<TESTPAYLOAD/>");
		try {
			msg.buildFullMessage(null);
		} catch (ITKMessagingException e2) {
			fail("Failed to build ITK Message");
		}
		
		ITKSimpleDOSImpl dos = new ITKSimpleDOSImpl();
		TestUtils.loadTestProperties(dos);
		ITKTransportRoute destination = null;
		
		try {
			destination = dos.resolveDestination(SERVICE_ID, new ITKAddressImpl(PROVIDER_EXPLICIT));
		} catch (ITKMessagingException e1) {
			fail("Failed to find known destination");
		}
		
		WSSOAPMessageImpl soapMsg = null;
		try {
			soapMsg = new WSSOAPMessageImpl(destination,msg,WSSOAPMessageImpl.SYNCREQ);
		} catch (ITKMessagingException e) {
			fail("Failed to build SOAP Message object");
		}
		assertTrue(soapMsg!=null);	
		
		soapMsg.getTransportProperties().setTransportFrom(soapFromAddress);
		soapMsg.getTransportProperties().setTransportUsername(soapUsername);

		try {
			soapMsg.buildFullMessage(toSoapTransform);
		} catch (ITKMessagingException e) {
			fail("Failed to build Full SOAP Message");
		}
		assertTrue(soapMsg.getFullMessage().length()>0);

		// Verify the returned document
		ITKTransportProperties propsFromDoc = null;
		Document soapDocument = DomUtils.parse(soapMsg.getFullMessage());
		try {
			
			// EFFECTIVELY TESTING ITKTransportPropertiesImpl as well but these are part of the same unit
			propsFromDoc = ITKTransportPropertiesImpl.buildFromSoap(soapDocument);

			assertTrue(propsFromDoc.getTransportAction().equals(SERVICE_ID));
			assertTrue(propsFromDoc.getTransportTo().equals(soapToAddress));
			assertTrue(propsFromDoc.getTransportFrom().equals(soapFromAddress));
			assertTrue(propsFromDoc.getTransportMessageId().length()==36);
			assertTrue(propsFromDoc.getTransportUsername().equals(soapUsername));
			assertTrue(propsFromDoc.getTransportCreatedTime().length()==20);
			assertTrue(propsFromDoc.getTransportExpiresTime().length()==20);
			
			// Check the payload
			String payloadCount = XPaths.compileXPath("SOAP:Envelope/SOAP:Body/itk:DistributionEnvelope/itk:payloads/@count").evaluate(soapDocument);
			assertTrue(payloadCount.equals("1"));
			
			String itkPayloadId = XPaths.compileXPath("SOAP:Envelope/SOAP:Body/itk:DistributionEnvelope/itk:payloads/itk:payload/@id").evaluate(soapDocument);
			assertTrue(itkPayloadId.length()==41);
			assertTrue(itkPayloadId.startsWith("uuid_"));
			
			Document businessPayloadDocument = DomUtils.createDocumentFromNode((Node)XPaths.SOAP_WRAPPED_ITK_FIRST_PAYLOAD_XPATH.evaluate(soapDocument, XPathConstants.NODE));
			String payload = DomUtils.serialiseToXML(businessPayloadDocument);
			assertTrue(payload.length()>0);

		} catch (ITKMessagingException e) {
			fail("Failed to build transport properties from soap document");
		}
		
	}
}
