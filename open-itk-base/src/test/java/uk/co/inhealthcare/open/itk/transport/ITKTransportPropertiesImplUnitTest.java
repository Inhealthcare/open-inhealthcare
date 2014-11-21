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
package uk.co.inhealthcare.open.itk.transport;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.transport.ITKTransportProperties;
import uk.co.inhealthcare.open.itk.transport.ITKTransportPropertiesImpl;
import uk.co.inhealthcare.open.itk.util.xml.DomUtils;

/**
 * @author Administrator
 *
 */
public class ITKTransportPropertiesImplUnitTest extends TestCase {

	@Test
	public void testConstructor() {
		ITKTransportProperties transportProps = new ITKTransportPropertiesImpl();
		assertTrue(transportProps!=null);
	}

	@Test
	public void testBuildNullDocument() {

		try {
			ITKTransportPropertiesImpl.buildFromSoap(null);
			fail("Failed to reject null document");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}

	}

	@Test
	public void testBuildFromSoap() throws IOException, SAXException, ParserConfigurationException {

		String testMessage = readTestMessage("HappyDaySoap.xml");
		String messageId = "1E21265D-10C8-11E4-8DA7-6B0452CE1A74";
		String toAddress = "https://localhost:4848/syncsoap";
		String action = "urn:nhs-itk:services:201005:testService-v1-0";
		String from = "urn:nhs-uk:addressing:ods:XXXX:testAddressFrom";
		String replyTo = "https://localhost:4848/ReplyTo";
		String faultTo = "https://localhost:4848/FaultTo";
		String relatesTo = "1E21265D-10C8-11E4-8DA7-222222222222";
		String username = "testUsername";
		String created = "2014-06-12T06:58:38Z";
		String expires = "2014-06-12T06:59:38Z";
		
		Document soapDocument = DomUtils.parse(testMessage);

		ITKTransportProperties props = null;
		try {
			props = ITKTransportPropertiesImpl.buildFromSoap(soapDocument);
		} catch (ITKMessagingException e1) {
			fail("Failed to build properties from soap document");
		}
		
		assertTrue(props!=null);
		assertTrue(props.getTransportMessageId().equals(messageId));
		assertTrue(props.getTransportAction().equals(action));
		assertTrue(props.getTransportTo().equals(toAddress));
		assertTrue(props.getTransportFrom().equals(from));
		assertTrue(props.getTransportReplyTo().equals(replyTo));
		assertTrue(props.getTransportFaultTo().equals(faultTo));
		assertTrue(props.getTransportRelatesTo().equals(relatesTo));
		assertTrue(props.getTransportUsername().equals(username));
		assertTrue(props.getTransportCreatedTime().equals(created));
		assertTrue(props.getTransportExpiresTime().equals(expires));

	}
		
	@Test
	public void testBuildFromSoapMinimal() throws IOException, SAXException, ParserConfigurationException {

		String testMessage = readTestMessage("HappyDaySoapMinimal.xml");
		String messageId = "1E21265D-10C8-11E4-8DA7-6B0452CE1A74";
		String action = "urn:nhs-itk:services:201005:testService-v1-0";
		
		Document soapDocument = DomUtils.parse(testMessage);

		ITKTransportProperties props = null;
		try {
			props = ITKTransportPropertiesImpl.buildFromSoap(soapDocument);
		} catch (ITKMessagingException e1) {
			fail("Failed to build properties from soap document");
		}
		
		assertTrue(props!=null);
		assertTrue(props.getTransportMessageId().equals(messageId));
		assertTrue(props.getTransportAction().equals(action));
	}

	private String readTestMessage(String testMessageFile) throws IOException {
		
	    InputStream tis = ITKTransportPropertiesImplUnitTest.class.getResourceAsStream(testMessageFile);

		BufferedInputStream bis = new BufferedInputStream(tis);
		byte[] contents = new byte[1024];
		int bytesRead = 0;
		String messageString = "";
		while ((bytesRead = bis.read(contents)) != -1) {
			messageString = messageString + new String(contents, 0, bytesRead);
		}
		bis.close();
		
		return messageString ;
	}

}
