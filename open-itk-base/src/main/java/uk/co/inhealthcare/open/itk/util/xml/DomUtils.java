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
package uk.co.inhealthcare.open.itk.util.xml;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The Class DomUtils.
 *
 * @author Nick Jones
 */
public class DomUtils {

	/**
	 * Instantiates a new dom utils. Private to
	 * ensure static methods are used
	 */
	private DomUtils() {
	}
	
	/** The Constant PRETTY_PRINT. */
	public static final boolean PRETTY_PRINT = true;
	
	/**
	 * Serialise to xml.
	 *
	 * @param document the document
	 * @return the string
	 */
	public static final String serialiseToXML(Document document) {
		return serialiseToXML(document, false);
	}
	
	/**
	 * Serialise to xml.
	 *
	 * @param document the document
	 * @param prettyPrint the pretty print
	 * @return the string
	 */
	public static final String serialiseToXML(Document document, boolean prettyPrint) {
		if (document != null) {
			DOMImplementation domImplementation = document.getImplementation();
			if (domImplementation.hasFeature("LS", "3.0") && domImplementation.hasFeature("Core", "2.0")) {
				DOMImplementationLS domImplementationLS = (DOMImplementationLS) domImplementation.getFeature("LS", "3.0");
				LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
				DOMConfiguration domConfiguration = lsSerializer.getDomConfig();
				if (prettyPrint && domConfiguration.canSetParameter("format-pretty-print", Boolean.TRUE)) {
					lsSerializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
				}
				LSOutput lsOutput = domImplementationLS.createLSOutput();
				lsOutput.setEncoding("UTF-8");
				StringWriter stringWriter = new StringWriter();
				lsOutput.setCharacterStream(stringWriter);
				lsSerializer.write(document, lsOutput);
				return stringWriter.toString();
			} else {
				throw new RuntimeException("DOM 3.0 LS and/or DOM 2.0 Core not supported.");
			}
		}
		return null;
	}
	
	/**
	 * Creates the document from node.
	 *
	 * @param node the node
	 * @return the document
	 * @throws ParserConfigurationException the parser configuration exception
	 */
	public static final Document createDocumentFromNode(Node node) throws ParserConfigurationException {
		if (node != null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc =  builder.newDocument();
			Node clone = node.cloneNode(true);
			doc.adoptNode(clone);
			doc.appendChild(clone);
			return doc;
		} 
		return null;
	}
	
	/**
	 * Parses the xmlString and returns a Document
	 *
	 * @param xmlString the xml string
	 * @return the document
	 * @throws SAXException the sAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 */
	public static final Document parse(String xmlString) throws SAXException, IOException, ParserConfigurationException {
		if (xmlString != null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();			
			InputSource is = new InputSource(new StringReader(xmlString));
			return builder.parse(is);
		}
		return null;
	}

}
