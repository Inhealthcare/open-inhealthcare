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
package uk.co.inhealthcare.open.itk.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import uk.co.inhealthcare.open.itk.infrastructure.ITKAddressImpl;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.test.TestUtils;
import uk.co.inhealthcare.open.itk.transport.ITKTransportRoute;

/**
 * @author Administrator
 *
 */
public class ITKSimpleDOSImplUnitTest {
	private final static String SERVICE_ID = "urn:nhs-itk:services:201005:testServiceA-v1-0";
	private final static String EXPLICIT_SERVICE_ID = "urn:nhs-itk:services:201005:testServiceB-v1-0";
	private final static String UNSUPPORTED_SERVICE_ID = "urn:nhs-itk:services:201005:testServiceC-v1-0";
	private final static String UNK_SERVICE_ID = "urn:nhs-itk:services:201005:unknownService-v1-0";

	private final static String ADDRESS_URI = "urn:nhs-uk:addressing:ods:TKW";
	private final static String EXPLICIT_ADDRESS_URI = "urn:nhs-uk:addressing:ods:EXPLICIT";
	private final static String UNK_ADDRESS_URI = "urn:nhs-uk:addressing:ods:unknown";
	private ITKSimpleDOSImpl itkSimpleDOS;
	
	@Before
	public void setup() throws Exception {
		itkSimpleDOS = new ITKSimpleDOSImpl();
		TestUtils.loadTestProperties(itkSimpleDOS);
	}

	@Test
	public void testResolveDestinationNullService() {
		
		try {
			itkSimpleDOS.resolveDestination(null, new ITKAddressImpl(
					UNK_ADDRESS_URI));
			fail("Failed to reject null service");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
	}

	@Test
	public void testResolveDestinationEmptyService() {
		
		try {
			itkSimpleDOS.resolveDestination("", new ITKAddressImpl(
					UNK_ADDRESS_URI));
			fail("Failed to reject null service");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
	}

	@Test
	public void testResolveDestinationNullAddress() {
		
		try {
			itkSimpleDOS.resolveDestination(UNK_SERVICE_ID, null);
			fail("Failed to reject null service");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
	}

	@Test
	public void testResolveDestinationNullAddressURI() {
		
		try {
			itkSimpleDOS.resolveDestination(UNK_SERVICE_ID, new ITKAddressImpl(
					null));
			fail("Failed to reject null service");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
	}

	@Test
	public void testResolveDestinationEmptyAddressURI() {
		
		try {
			itkSimpleDOS.resolveDestination(UNK_SERVICE_ID, new ITKAddressImpl(
					""));
			fail("Failed to reject null service");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
	}

	@Test
	public void testResolveUnknownDestination() {
		
		try {
			itkSimpleDOS.resolveDestination(UNK_SERVICE_ID, new ITKAddressImpl(
					UNK_ADDRESS_URI));
			fail("Failed to reject unknown destination");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
	}

	@Test
	public void testResolveKnownDestinationWithDefaults() {
		ITKTransportRoute route = null;
		
		try {
			route = itkSimpleDOS.resolveDestination(SERVICE_ID,
					new ITKAddressImpl(ADDRESS_URI));
		} catch (ITKMessagingException e1) {
			fail("Failed to find known destination");
		}
		assertTrue(route!=null);
		//EXPLICIT VALUES
		assertTrue(route.getPhysicalAddress().equals("https://localhost:4848/syncsoap"));
		//DEFAULTS
		assertTrue(route.getTransportType().equals("WS"));
		assertTrue(route.getTimeToLive()==3600);
		assertTrue(route.getTransportTimeout()==10000);
		assertTrue(route.getReplyToAddress().equals(""));
		assertTrue(route.getExceptionToAddress().equals(""));
	}
	
	@Test
	public void testResolveKnownDestinationExplicit() {
		ITKTransportRoute route = null;
		
		try {
			route = itkSimpleDOS.resolveDestination(SERVICE_ID,
					new ITKAddressImpl(EXPLICIT_ADDRESS_URI));
		} catch (ITKMessagingException e1) {
			fail("Failed to find known destination");
		}
		assertTrue(route!=null);
		//EXPLICIT VALUES
		assertTrue(route.getPhysicalAddress().equals("https://localhost:4848/Explicit"));
		assertTrue(route.getTransportType().equals("WSEX"));
		assertTrue(route.getTimeToLive()==360000);
		assertTrue(route.getTransportTimeout()==1000000);
		assertTrue(route.getReplyToAddress().equals("ExplicitReplyTo.com"));
		assertTrue(route.getExceptionToAddress().equals("ExplicitExceptionTo.com"));
	}
	
	@Test
	public void testGetNullService() {
		
		try {
			itkSimpleDOS.getService(null);
			fail("Failed to reject bad service name");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testGetBlankService() {
		
		try {
			itkSimpleDOS.getService("");
			fail("Failed to reject bad service name");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
	}

	@Test
	public void testGetUnknownService() {
		
		try {
			itkSimpleDOS.getService(UNK_SERVICE_ID);
			fail("Failed to reject bad service name");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testGetUnsupportedService() {
		
		try {
			itkSimpleDOS.getService(UNSUPPORTED_SERVICE_ID);
			fail("Failed to reject unsupported service name");
		} catch (ITKMessagingException e1) {
			assertTrue(e1.getMessage().contains("Service not supported"));
		}
	}
	
	@Test
	public void testGetValidServiceA() {
		ITKService service = null;
		
		try {
			service = itkSimpleDOS.getService(SERVICE_ID);
		} catch (ITKMessagingException e1) {
			fail("Failed to find service name");
		}
		assertTrue(service!=null);
		assertTrue(service.getServiceId().equals(SERVICE_ID));
		assertTrue(service.getMimeType().equals("text/xml"));
		assertTrue(service.supportsSync());
		assertFalse(service.supportsAsync());
		assertFalse(service.isBase64());
		
	}
	
	@Test
	public void testGetValidServiceB() {
		ITKService service = null;
		
		try {
			service = itkSimpleDOS.getService(EXPLICIT_SERVICE_ID);
		} catch (ITKMessagingException e1) {
			fail("Failed to find service name");
		}
		assertTrue(service!=null);
		assertTrue(service.getServiceId().equals(EXPLICIT_SERVICE_ID));
		assertTrue(service.getMimeType().equals("test/Mime"));
		assertFalse(service.supportsSync());
		assertTrue(service.supportsAsync());
		assertTrue(service.isBase64());
		
	}
	
}
