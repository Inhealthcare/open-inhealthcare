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
package uk.co.inhealthcare.open.itk.transport.WS;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.util.xml.DomUtils;
import uk.co.inhealthcare.open.itk.util.xml.XPaths;

/**
 * The Class ITKSOAPException.
 *
 * @author Nick Jones
 */
public class ITKSOAPException extends ITKMessagingException {

	private final static Logger logger = LoggerFactory.getLogger(ITKSOAPException.class);
	private static final long serialVersionUID = -4655001784827242123L;

	/**
	 * Instantiates a new iTKSOAP exception.
	 *
	 * @param arg0 the arg0
	 */
	public ITKSOAPException(String arg0) {
		super(arg0);
	}

	
	/**
	 * Parses the soap fault.
	 *
	 * @param soapFaultXML the soap fault xml
	 * @return the iTKSOAP exception
	 */
	public static final ITKSOAPException parseSOAPFault(String soapFaultXML) {
		if (soapFaultXML != null) {
			try {
				Document doc = DomUtils.parse(soapFaultXML);
				Document faultDetail = DomUtils.createDocumentFromNode((Node)XPaths.WSA_SOAP_ERROR_DETAIL_XPATH.evaluate(doc, XPathConstants.NODE));
				Integer errorCode = Integer.parseInt(XPaths.WSA_SOAP_ERROR_DETAIL_CODE_XPATH.evaluate(faultDetail));
				String errorText = XPaths.WSA_SOAP_ERROR_DETAIL_DIAGNOSTIC_XPATH.evaluate(faultDetail);
				String errorId = XPaths.WSA_SOAP_ERROR_DETAIL_ID_XPATH.evaluate(faultDetail);
				ITKSOAPException soapFault = new ITKSOAPException(errorText);
				soapFault.setErrorId(errorId);
				soapFault.setErrorCode(errorCode);
				return soapFault;
			} catch (SAXException e) {
				logger.error("Could not parse SOAP fault", e);
			} catch (IOException e) {
				logger.error("Could not parse SOAP fault", e);
			} catch (ParserConfigurationException e) {
				logger.error("Could not parse SOAP fault", e);
			} catch (XPathExpressionException e) {
				logger.error("Could not parse SOAP fault", e);
			}
		}
		return new ITKSOAPException("SOAP fault (could not parse soap fault");
	}
	
	
	

}
