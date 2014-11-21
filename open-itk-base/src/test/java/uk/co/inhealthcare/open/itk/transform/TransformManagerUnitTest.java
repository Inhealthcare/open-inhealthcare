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
package uk.co.inhealthcare.open.itk.transform;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import junit.framework.TestCase;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.transform.TransformManager;
import uk.co.inhealthcare.open.itk.util.xml.DomUtils;
import uk.co.inhealthcare.open.itk.util.xml.XPaths;

/**
 * @author Administrator
 *
 */
public class TransformManagerUnitTest extends TestCase {

	@Test
	public void testTransformNoParms() throws IOException, SAXException, ParserConfigurationException {
		String testInput = "<Input><A>aval</A><B>bval</B><C>cval</C></Input>";
		String testOutput = null; 
		try {
			testOutput = TransformManager.doTransform("TestTransform.xslt",testInput);
		} catch (ITKMessagingException e1) {
			fail("Failed to execute test transformation");
		}
		assert(testOutput != null);
		Document testDoc = DomUtils.parse(testOutput);

		try {
			
			assertTrue(testDoc!=null);
			// XPaths.getXPathExpression("").evaluate(testDoc)
			//String aValue = XPaths.getXPathExpression("//TEST/A").evaluate(testDoc);
			assertTrue(XPaths.compileXPath("//TEST/A").evaluate(testDoc).equals("aval"));
			assertTrue(XPaths.compileXPath("//TEST/B").evaluate(testDoc).equals("bval"));
			assertTrue(XPaths.compileXPath("//TEST/C").evaluate(testDoc).equals("cval"));

		} catch (Exception e) {
			fail("Exception thrown");
		}
		
	}

	@Test
	public void testTransformWithParms() throws IOException, SAXException, ParserConfigurationException {
		String testInput = "<Input><A>aval</A><B>bval</B><C>cval</C></Input>";
		String testOutput = null;
		Map<String, String> parms = new HashMap<String,String>();
		parms.put("P1", "P1val");
		parms.put("P2", "P2val");
		parms.put("P3", "P3val");
		try {
			testOutput = TransformManager.doTransform("TestTransform.xslt",testInput,parms);
		} catch (ITKMessagingException e1) {
			fail("Failed to execute test transformation");
		}
		assert(testOutput != null);
		Document testDoc = DomUtils.parse(testOutput);

		try {
			
			assertTrue(testDoc!=null);
			// XPaths.getXPathExpression("").evaluate(testDoc)
			//String aValue = XPaths.getXPathExpression("//TEST/A").evaluate(testDoc);
			assertTrue(XPaths.compileXPath("//TEST/A").evaluate(testDoc).equals("aval"));
			assertTrue(XPaths.compileXPath("//TEST/B").evaluate(testDoc).equals("bval"));
			assertTrue(XPaths.compileXPath("//TEST/C").evaluate(testDoc).equals("cval"));
			// and the parameters ....
			assertTrue(XPaths.compileXPath("//TEST/P1").evaluate(testDoc).equals("P1val"));
			assertTrue(XPaths.compileXPath("//TEST/P2").evaluate(testDoc).equals("P2val"));
			assertTrue(XPaths.compileXPath("//TEST/P3").evaluate(testDoc).equals("P3val"));

		} catch (XPathExpressionException e) {
			fail("XPathExpressionException thrown");
		}
		
	}
	@Test
	public void testTransformBadInput() throws IOException, SAXException, ParserConfigurationException {
		String testInput = "<Input><A>aval</A><B>bval</B><C>cval</C></InputZ>";
		try {
			TransformManager.doTransform("TestTransform.xslt",testInput);
			fail("Failed to reject bad input");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
		
	}

	@Test
	public void testTransformBadTemplate() throws IOException, SAXException, ParserConfigurationException {
		String testInput = "<Input><A>aval</A><B>bval</B><C>cval</C></Input>";
		try {
			TransformManager.doTransform("TestTransformBad.xslt",testInput);
			fail("Failed to reject bad template");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
		
	}
	@Test
	public void testTransformNonExistantName() {
		try {
			TransformManager.doTransform("ABC","");
			fail("Failed to reject non-existant transform name");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
		try {
			TransformManager.doTransform("ABC","",null);
			fail("Failed to reject non-existant transform name");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
	}

	@Test
	public void testTransformNullName() {
		try {
			TransformManager.doTransform(null,"");
			fail("Failed to reject null transform name");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
		try {
			TransformManager.doTransform(null,"",null);
			fail("Failed to reject null transform name");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
	}

	@Test
	public void testTransformEmptyName() {
		try {
			TransformManager.doTransform("","");
			fail("Failed to reject empty transform name");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
		try {
			TransformManager.doTransform("","",null);
			fail("Failed to reject empty transform name");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
	}

	@Test
	public void testTransformNullInput() {
		try {
			TransformManager.doTransform("tname",null);
			fail("Failed to reject null input");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
		try {
			TransformManager.doTransform("tname",null,null);
			fail("Failed to reject null input");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
	}

	@Test
	public void testTransformEmptyInput() {
		try {
			TransformManager.doTransform("tname","");
			fail("Failed to reject empty input");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
		try {
			TransformManager.doTransform("tname","",null);
			fail("Failed to reject empty input");
		} catch (ITKMessagingException e1) {
			assertTrue(true);
		}
	}

}
