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

import junit.framework.TestCase;

import org.junit.Test;

import uk.co.inhealthcare.open.itk.infrastructure.ITKAddressImpl;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.service.ITKService;
import uk.co.inhealthcare.open.itk.service.ITKSimpleDOSImpl;
import uk.co.inhealthcare.open.itk.transport.ITKTransportRoute;

/**
 * @author Administrator
 *
 */
public class ITKSimpleDOSImplUnitTest extends TestCase {
	private final static String SERVICE_ID = "urn:nhs-itk:services:201005:testServiceA-v1-0";
	private final static String EXPLICIT_SERVICE_ID = "urn:nhs-itk:services:201005:testServiceB-v1-0";
	private final static String UNSUPPORTED_SERVICE_ID = "urn:nhs-itk:services:201005:testServiceC-v1-0";
	private final static String UNK_SERVICE_ID = "urn:nhs-itk:services:201005:unknownService-v1-0";

	private final static String ADDRESS_URI = "urn:nhs-uk:addressing:ods:TKW";
	private final static String EXPLICIT_ADDRESS_URI = "urn:nhs-uk:addressing:ods:EXPLICIT";
	private final static String UNK_ADDRESS_URI = "urn:nhs-uk:addressing:ods:unknown";

	@Test
	public void testResolveDestinationNullService() {
		ITKSimpleDOSImpl dos = new ITKSimpleDOSImpl();
		
		try {
			dos.resolveDestination(null, new ITKAddressImpl(UNK_ADDRESS_URI));
			fail("Failed to reject null service");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
	}

	@Test
	public void testResolveDestinationEmptyService() {
		ITKSimpleDOSImpl dos = new ITKSimpleDOSImpl();
		
		try {
			dos.resolveDestination("", new ITKAddressImpl(UNK_ADDRESS_URI));
			fail("Failed to reject null service");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
	}

	@Test
	public void testResolveDestinationNullAddress() {
		ITKSimpleDOSImpl dos = new ITKSimpleDOSImpl();
		
		try {
			dos.resolveDestination(UNK_SERVICE_ID, null);
			fail("Failed to reject null service");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
	}

	@Test
	public void testResolveDestinationNullAddressURI() {
		ITKSimpleDOSImpl dos = new ITKSimpleDOSImpl();
		
		try {
			dos.resolveDestination(UNK_SERVICE_ID, new ITKAddressImpl(null));
			fail("Failed to reject null service");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
	}

	@Test
	public void testResolveDestinationEmptyAddressURI() {
		ITKSimpleDOSImpl dos = new ITKSimpleDOSImpl();
		
		try {
			dos.resolveDestination(UNK_SERVICE_ID, new ITKAddressImpl(""));
			fail("Failed to reject null service");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
	}

	@Test
	public void testResolveUnknownDestination() {
		ITKSimpleDOSImpl dos = new ITKSimpleDOSImpl();
		
		try {
			dos.resolveDestination(UNK_SERVICE_ID, new ITKAddressImpl(UNK_ADDRESS_URI));
			fail("Failed to reject unknown destination");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
	}

	@Test
	public void testResolveKnownDestinationWithDefaults() {
		ITKSimpleDOSImpl dos = new ITKSimpleDOSImpl();
		ITKTransportRoute route = null;
		
		try {
			route = dos.resolveDestination(SERVICE_ID, new ITKAddressImpl(ADDRESS_URI));
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
		ITKSimpleDOSImpl dos = new ITKSimpleDOSImpl();
		ITKTransportRoute route = null;
		
		try {
			route = dos.resolveDestination(SERVICE_ID, new ITKAddressImpl(EXPLICIT_ADDRESS_URI));
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
		ITKSimpleDOSImpl dos = new ITKSimpleDOSImpl();
		
		try {
			dos.getService(null);
			fail("Failed to reject bad service name");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testGetBlankService() {
		ITKSimpleDOSImpl dos = new ITKSimpleDOSImpl();
		
		try {
			dos.getService("");
			fail("Failed to reject bad service name");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
	}

	@Test
	public void testGetUnknownService() {
		ITKSimpleDOSImpl dos = new ITKSimpleDOSImpl();
		
		try {
			dos.getService(UNK_SERVICE_ID);
			fail("Failed to reject bad service name");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testGetUnsupportedService() {
		ITKSimpleDOSImpl dos = new ITKSimpleDOSImpl();
		
		try {
			dos.getService(UNSUPPORTED_SERVICE_ID);
			fail("Failed to reject unsupported service name");
		} catch (ITKMessagingException e1) {
			assertTrue(e1.getMessage().contains("Service not supported"));
		}
	}
	
	@Test
	public void testGetValidServiceA() {
		ITKSimpleDOSImpl dos = new ITKSimpleDOSImpl();
		ITKService service = null;
		
		try {
			service = dos.getService(SERVICE_ID);
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
		ITKSimpleDOSImpl dos = new ITKSimpleDOSImpl();
		ITKService service = null;
		
		try {
			service = dos.getService(EXPLICIT_SERVICE_ID);
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
