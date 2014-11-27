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
package uk.co.inhealthcare.open.itk.payload;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import uk.co.inhealthcare.open.itk.infrastructure.ITKAddress;
import uk.co.inhealthcare.open.itk.infrastructure.ITKAddressImpl;
import uk.co.inhealthcare.open.itk.infrastructure.ITKIdentity;
import uk.co.inhealthcare.open.itk.infrastructure.ITKIdentityImpl;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessageProperties;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagePropertiesImpl;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.util.xml.DomUtils;

/**
 * @author Administrator
 *
 */
public class ITKSimpleMessageImplUnitTest {

	@Test
	public void testConstructor() {
		ITKMessage msg = new ITKSimpleMessageImpl();
		assertTrue(msg.getConversationId().length()==36);
	}

	@Test
	public void testConstructorWithId() {
		
		String conversationId = UUID.randomUUID().toString().toUpperCase();

		ITKMessage msg = new ITKSimpleMessageImpl(conversationId);
		assertTrue(msg.getConversationId().equals(conversationId));
	}

	@Test
	public void testBuildNoProperties() {
		ITKMessage msg = new ITKSimpleMessageImpl();
		try {
			msg.buildFullMessage(null);
			fail("Failed to catch null properties name");
		} catch (ITKMessagingException e1) {
			assertTrue(e1.getMessage().contains("Message Properties not set"));
		}
	}

	@Test
	public void testBuildNoServiceId() {
		ITKMessage msg = new ITKSimpleMessageImpl();
		msg.setMessageProperties(new ITKMessagePropertiesImpl());
		try {
			msg.buildFullMessage(null);
			fail("Failed to catch missing service name");
		} catch (ITKMessagingException e1) {
			assertTrue(e1.getMessage().contains("Service Id not set"));
		}
	}
	
	@Test
	public void testBuildWithDefaultTemplate() {
		ITKMessage msg = new ITKSimpleMessageImpl();
		ITKMessageProperties msgPropsIn = new ITKMessagePropertiesImpl();
		msgPropsIn.setServiceId("testServiceId");
		msg.setMessageProperties(msgPropsIn);
		try {
			msg.buildFullMessage(null);
		} catch (ITKMessagingException e1) {
			fail("Failed to build Full ITK Message");
		}
	}

	@Test
	public void testBuildFullMessage() throws IOException, SAXException, ParserConfigurationException {
		ITKMessage msg = new ITKSimpleMessageImpl();
		ITKMessageProperties msgPropsIn = new ITKMessagePropertiesImpl();
		msgPropsIn.setServiceId("urn:nhs-itk:services:201005:testService-v1-0");
		msgPropsIn.setProfileId("urn:nhs-en:profile:testProfile-v1-0");
		ITKAddress toAddress = new ITKAddressImpl("urn:nhs-uk:addressing:ods:XXXX:toAddress");
		msgPropsIn.setToAddress(toAddress);
		ITKAddress fromAddress = new ITKAddressImpl("urn:nhs-uk:addressing:ods:XXXX:fromAddress");
		msgPropsIn.setFromAddress(fromAddress);
		ITKIdentity fromId = new ITKIdentityImpl("urn:nhs-uk:identity:ods:XXXX:Fred.Smith");
		msgPropsIn.addAuditIdentity(fromId);

		msg.setMessageProperties(msgPropsIn);
		try {
			msg.buildFullMessage(null);
		} catch (ITKMessagingException e1) {
			fail("Failed to build Full ITK Message");
		}
		Document deDoc = DomUtils.parse(msg.getFullMessage());
		try {
			
			// EFFECTIVELY TESTING ITKMessagePropertiesImpl as well but these are part of the same unit
			ITKMessageProperties msgProps = ITKMessagePropertiesImpl.build(deDoc);
			assertTrue(msgProps!=null);
			assertTrue(msgProps.getTrackingId().equals(msgPropsIn.getTrackingId()));
			assertTrue(msgProps.getServiceId().equals("urn:nhs-itk:services:201005:testService-v1-0"));
			assertTrue(msgProps.getProfileId().equals("urn:nhs-en:profile:testProfile-v1-0"));
			assertTrue(msgProps.getAuditIdentities().get(0).getURI().equals("urn:nhs-uk:identity:ods:XXXX:Fred.Smith"));
			assertTrue(msgProps.getAuditIdentities().get(0).getType().equals(ITKIdentity.DEFAULT_IDENTITY_TYPE));
			assertTrue(msgProps.getToAddress().getURI().equals("urn:nhs-uk:addressing:ods:XXXX:toAddress"));
			assertTrue(msgProps.getToAddress().getType().equals(ITKAddress.DEFAULT_ADDRESS_TYPE));
			assertTrue(msgProps.getFromAddress().getURI().equals("urn:nhs-uk:addressing:ods:XXXX:fromAddress"));
			assertTrue(msgProps.getFromAddress().getType().equals(ITKAddress.DEFAULT_ADDRESS_TYPE));
			/**
			assertTrue(msgProps.getHandlingSpecification(ITKMessageProperties.BUSINESS_ACK_HANDLING_SPECIFICATION_KEY).length()==0);
			**/

		} catch (ITKMessagingException e) {
			fail("ITKMessagingException thrown");
		}
		
	}

}
