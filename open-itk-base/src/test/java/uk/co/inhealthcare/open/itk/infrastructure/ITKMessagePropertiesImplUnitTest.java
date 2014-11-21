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
package uk.co.inhealthcare.open.itk.infrastructure;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import uk.co.inhealthcare.open.itk.infrastructure.ITKAddress;
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
public class ITKMessagePropertiesImplUnitTest extends TestCase {

	@Test
	public void testConstructor() {
		ITKMessageProperties msgProps = new ITKMessagePropertiesImpl();
		assertTrue(msgProps.getTrackingId().length()==36);
	}

	@Test
	public void testGetAuditIdentityByType_Default() {
		String ITK_IDENTITY = "urn:nhs-uk:identity:ods:XXXX:User:TestUser";
		ITKMessageProperties msgProps = new ITKMessagePropertiesImpl();
		msgProps.addAuditIdentity(new ITKIdentityImpl(ITK_IDENTITY));
		assertTrue(msgProps.getAuditIdentityByType(ITKIdentity.DEFAULT_IDENTITY_TYPE).equals(ITK_IDENTITY));
	}
	
	@Test
	public void testGetAuditIdentityByType_Keyed() {
		String ITK_IDENTITY = "urn:nhs-uk:identity:ods:XXXX:User:TestUser";
		String SPINE_UUID_IDENTITY = "111111111";
		String SPINE_ROLE_IDENTITY = "222222222";
		String SPINE_URP_IDENTITY  = "333333333";
		
		ITKMessageProperties msgProps = new ITKMessagePropertiesImpl();
		msgProps.addAuditIdentity(new ITKIdentityImpl(ITK_IDENTITY, ITKIdentity.DEFAULT_IDENTITY_TYPE));
		msgProps.addAuditIdentity(new ITKIdentityImpl(SPINE_UUID_IDENTITY, ITKIdentity.SPINE_UUID_TYPE));
		msgProps.addAuditIdentity(new ITKIdentityImpl(SPINE_ROLE_IDENTITY, ITKIdentity.SPINE_ROLE_TYPE));
		msgProps.addAuditIdentity(new ITKIdentityImpl(SPINE_URP_IDENTITY, ITKIdentity.SPINE_ROLE_PROFILE_TYPE));

		assertTrue(msgProps.getAuditIdentityByType(ITKIdentity.DEFAULT_IDENTITY_TYPE).equals(ITK_IDENTITY));
		assertTrue(msgProps.getAuditIdentityByType(ITKIdentity.SPINE_UUID_TYPE).equals(SPINE_UUID_IDENTITY));
		assertTrue(msgProps.getAuditIdentityByType(ITKIdentity.SPINE_ROLE_TYPE).equals(SPINE_ROLE_IDENTITY));
		assertTrue(msgProps.getAuditIdentityByType(ITKIdentity.SPINE_ROLE_PROFILE_TYPE).equals(SPINE_URP_IDENTITY));

		assertTrue(msgProps.getAuditIdentityByType(ITKIdentity.NHS_NUMBER_TYPE)==null);
		assertTrue(msgProps.getAuditIdentityByType(ITKIdentity.LOCAL_PATIENT_TYPE)==null);

	}

	@Test
	public void testGetAuditIdentityByType_Empty() {
		ITKMessageProperties msgProps = new ITKMessagePropertiesImpl();
		assertTrue(msgProps.getAuditIdentityByType(ITKIdentity.DEFAULT_IDENTITY_TYPE)==null);
	}
	
	@Test
	public void testGetPatientIdentityByType() {
		
		String LOCAL_IDENTITY = "AH1234567";
		String NHS_IDENTITY = "12312341234";
		
		ITKMessageProperties msgProps = new ITKMessagePropertiesImpl();
		msgProps.addPatientIdentity(new ITKIdentityImpl(LOCAL_IDENTITY, ITKIdentity.LOCAL_PATIENT_TYPE));
		msgProps.addPatientIdentity(new ITKIdentityImpl(NHS_IDENTITY, ITKIdentity.NHS_NUMBER_TYPE));

		assertTrue(msgProps.getPatientIdentityByType(ITKIdentity.LOCAL_PATIENT_TYPE).equals(LOCAL_IDENTITY));
		assertTrue(msgProps.getPatientIdentityByType(ITKIdentity.NHS_NUMBER_TYPE).equals(NHS_IDENTITY));

		assertFalse(msgProps.getPatientIdentityByType(ITKIdentity.NHS_NUMBER_TYPE).equals(LOCAL_IDENTITY));
		assertFalse(msgProps.getPatientIdentityByType(ITKIdentity.LOCAL_PATIENT_TYPE).equals(NHS_IDENTITY));
		
		assertTrue(msgProps.getPatientIdentityByType(ITKIdentity.DEFAULT_IDENTITY_TYPE)==null);
		
	}
	
	@Test
	public void testGetPatientIdentityByType_Empty() {
		ITKMessageProperties msgProps = new ITKMessagePropertiesImpl();
		assertTrue(msgProps.getPatientIdentityByType(ITKIdentity.LOCAL_PATIENT_TYPE)==null);
	}
	
	@Test
	public void testGetHandlingSpecification() {
		
		String SPEC_KEY = "TestSpecKey";
		String SPEC_KEY2= "TestSpecKey2";
		String SPEC_VALUE = "TestSpecValue";
		
		ITKMessageProperties msgProps = new ITKMessagePropertiesImpl();
		msgProps.addHandlingSpecification(SPEC_KEY, SPEC_VALUE);

		assertTrue(msgProps.getHandlingSpecification(SPEC_KEY).equals(SPEC_VALUE));
		assertTrue(msgProps.getHandlingSpecification(SPEC_KEY2)==null);
		
	}

	public void testBuild01() throws IOException, SAXException, ParserConfigurationException {
		String testMessage = readTestMessage("HappyDayDistEnv.xml");
		Document deDoc = DomUtils.parse(testMessage);
		ITKMessageProperties msgProps = null;
		
		try {
			msgProps = ITKMessagePropertiesImpl.build(deDoc);
			assertTrue(msgProps!=null);
			assertTrue(msgProps.getItkPayloadId().equals("0808A967-49B2-498B-AD75-1D7A0F1262D7"));
			assertTrue(msgProps.getTrackingId().equals("2D37D9CA-5223-41C7-A159-F33D5A914EB5"));
			assertTrue(msgProps.getServiceId().equals("urn:nhs-itk:services:201005:getNHSNumber-v1-0Response"));
			assertTrue(msgProps.getProfileId().equals("urn:nhs-en:profile:getNHSNumberResponse-v1-0"));
			assertTrue(msgProps.getFromAddress().getURI().equals("urn:nhs-uk:addressing:ods:XXXX:team1:C"));
			assertTrue(msgProps.getFromAddress().getType().equals(ITKAddress.DEFAULT_ADDRESS_TYPE));
			assertTrue(msgProps.getToAddress().getURI().equals("testAddress"));
			assertTrue(msgProps.getToAddress().getType().equals(ITKAddress.DEFAULT_ADDRESS_TYPE));
			assertTrue(msgProps.getAuditIdentities().get(0).getURI().equals("urn:nhs-uk:identity:ods:XXXX:Fred.Smith"));
			assertTrue(msgProps.getAuditIdentities().get(0).getType().equals(ITKIdentity.DEFAULT_IDENTITY_TYPE));
			assertTrue(msgProps.getHandlingSpecification(ITKMessageProperties.BUSINESS_ACK_HANDLING_SPECIFICATION_KEY).length()==0);

		} catch (ITKMessagingException e) {
			fail("ITKMessagingException thrown");
		}

	}
	public void testBuild02() throws IOException, SAXException, ParserConfigurationException {
		String testMessage = readTestMessage("HappyDayDistEnv_WithTypes.xml");
		Document deDoc = DomUtils.parse(testMessage);
		ITKMessageProperties msgProps = null;
		
		try {
			msgProps = ITKMessagePropertiesImpl.build(deDoc);
			assertTrue(msgProps!=null);
			assertTrue(msgProps.getItkPayloadId().equals("0808A967-49B2-498B-AD75-1D7A0F1262D7"));
			assertTrue(msgProps.getTrackingId().equals("2D37D9CA-5223-41C7-A159-F33D5A914EB5"));
			assertTrue(msgProps.getServiceId().equals("urn:nhs-itk:services:201005:getNHSNumber-v1-0Response"));
			assertTrue(msgProps.getProfileId().equals("urn:nhs-en:profile:getNHSNumberResponse-v1-0"));
			assertTrue(msgProps.getFromAddress().getURI().equals("urn:nhs-uk:addressing:ods:XXXX:team1:C"));
			assertTrue(msgProps.getFromAddress().getType().equals("testType"));
			assertTrue(msgProps.getToAddress().getURI().equals("testAddress"));
			assertTrue(msgProps.getToAddress().getType().equals("testType"));
			assertTrue(msgProps.getAuditIdentities().get(0).getURI().equals("urn:nhs-uk:identity:ods:XXXX:Fred.Smith"));
			assertTrue(msgProps.getAuditIdentities().get(0).getType().equals(ITKIdentity.SPINE_ROLE_PROFILE_TYPE));
			assertTrue(msgProps.getHandlingSpecification(ITKMessageProperties.BUSINESS_ACK_HANDLING_SPECIFICATION_KEY).length()==0);

		} catch (ITKMessagingException e) {
			fail("ITKMessagingException thrown");
		}

	}
	public void testBuild03() throws IOException, SAXException, ParserConfigurationException {
		String testMessage = readTestMessage("HappyDayDistEnv_ExplicitDefaultTypes.xml");
		Document deDoc = DomUtils.parse(testMessage);
		ITKMessageProperties msgProps = null;
		
		try {
			msgProps = ITKMessagePropertiesImpl.build(deDoc);
			assertTrue(msgProps!=null);
			assertTrue(msgProps.getItkPayloadId().equals("0808A967-49B2-498B-AD75-1D7A0F1262D7"));
			assertTrue(msgProps.getTrackingId().equals("2D37D9CA-5223-41C7-A159-F33D5A914EB5"));
			assertTrue(msgProps.getServiceId().equals("urn:nhs-itk:services:201005:getNHSNumber-v1-0Response"));
			assertTrue(msgProps.getProfileId().equals("urn:nhs-en:profile:getNHSNumberResponse-v1-0"));
			assertTrue(msgProps.getFromAddress().getURI().equals("urn:nhs-uk:addressing:ods:XXXX:team1:C"));
			assertTrue(msgProps.getFromAddress().getType().equals(ITKAddress.DEFAULT_ADDRESS_TYPE));
			assertTrue(msgProps.getToAddress().getURI().equals("testAddress"));
			assertTrue(msgProps.getToAddress().getType().equals(ITKAddress.DEFAULT_ADDRESS_TYPE));
			assertTrue(msgProps.getAuditIdentities().get(0).getURI().equals("urn:nhs-uk:identity:ods:XXXX:Fred.Smith"));
			assertTrue(msgProps.getAuditIdentities().get(0).getType().equals(ITKIdentity.DEFAULT_IDENTITY_TYPE));
			assertTrue(msgProps.getHandlingSpecification(ITKMessageProperties.BUSINESS_ACK_HANDLING_SPECIFICATION_KEY).length()==0);

		} catch (ITKMessagingException e) {
			fail("ITKMessagingException thrown");
		}

	}
		
	private String readTestMessage(String testMessageFile) throws IOException {
		
	    InputStream tis = ITKMessagePropertiesImplUnitTest.class.getResourceAsStream(testMessageFile);

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
