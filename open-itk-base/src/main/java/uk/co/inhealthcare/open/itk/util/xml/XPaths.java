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

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class XPaths.
 *
 * @author Nick Jones
 */
public class XPaths {
	
	private final static Logger logger = LoggerFactory.getLogger(XPaths.class);
	
	private static XPathFactory xpathFactory;
	
	static {
		try {
			xpathFactory = XPathFactory.newInstance(XPathConstants.DOM_OBJECT_MODEL);
		} catch (XPathFactoryConfigurationException e) {
			logger.error("Could not create XPath factory", e);
		}
	}
	
	private static final String ROOT = "/";
	private static final String SOAP_BODY = ROOT + "SOAP:Envelope/SOAP:Body";
	private static final String SOAP_BODY_CONTENT = SOAP_BODY + "/*[1]";
    private static final String SOAP_HEADER = ROOT + "SOAP:Envelope/SOAP:Header";
    private static final String WSA_MSG_ID = SOAP_HEADER + "/wsa:MessageID";
    private static final String WSA_TO = SOAP_HEADER + "/wsa:To";
    private static final String WSA_RELATES_TO = SOAP_HEADER + "/wsa:RelatesTo";
    private static final String WSA_FROM = SOAP_HEADER + "/wsa:From/wsa:Address";
    private static final String WSA_REPLY_TO = SOAP_HEADER + "/wsa:ReplyTo/wsa:Address";
    private static final String WSA_FAULT_TO = SOAP_HEADER + "/wsa:FaultTo/wsa:Address";
    private static final String WSA_ACTION = SOAP_HEADER + "/wsa:Action";
    
    // Add in Xpaths for security header
    private static final String WSA_SECURITY_TIMESTAMP = SOAP_HEADER + "/wsse:Security/wsu:Timestamp";
    private static final String WSA_SECURITY_CREATED = SOAP_HEADER + "/wsse:Security/wsu:Timestamp/wsu:Created";
    private static final String WSA_SECURITY_EXPIRES = SOAP_HEADER + "/wsse:Security/wsu:Timestamp/wsu:Expires";
    private static final String WSA_SECURITY_USERNAME = SOAP_HEADER + "/wsse:Security/wsse:UsernameToken/wsse:Username";
    
    /** Counts the number of payloads present in the message (does not rely on the count provided in the message itself). */
    private static final String ITK_PAYLOAD_COUNT = "count(itk:DistributionEnvelope/itk:payloads/itk:payload)";
    
    private static final String ITK_AUDIT_IDENTITY_URI = "itk:DistributionEnvelope/itk:header/itk:auditIdentity/itk:id/@uri";
    private static final String ITK_AUDIT_IDENTITY_TYPE = "itk:DistributionEnvelope/itk:header/itk:auditIdentity/itk:id/@type";

    private static final String ITK_FROM_ADDRESS_URI = "itk:DistributionEnvelope/itk:header/itk:senderAddress/@uri";
    private static final String ITK_FROM_ADDRESS_TYPE = "itk:DistributionEnvelope/itk:header/itk:senderAddress/@type";
    
    private static final String ITK_FIRST_TO_ADDRESS_URI = "itk:DistributionEnvelope/itk:header/itk:addresslist/itk:address[1]/@uri";
    private static final String ITK_FIRST_TO_ADDRESS_TYPE = "itk:DistributionEnvelope/itk:header/itk:addresslist/itk:address[1]/@type";
    
    private static final String ITK_FIRST_MIMETYPE = "itk:DistributionEnvelope/itk:header/itk:manifest/itk:manifestitem[1]/@mimetype";
    private static final String ITK_FIRST_BASE64 = "itk:DistributionEnvelope/itk:header/itk:manifest/itk:manifestitem[1]/@base64";
    
    /**
     * Obtains the profile id. Note this will take the profile id from the manifest for the manifest item. It is not
     * appropriate for a message containing multiple payloads
     */
    private static final String ITK_PROFILE_ID = "itk:DistributionEnvelope/itk:header/itk:manifest/itk:manifestitem[1]/@profileid";
    
    /**
     * The tracking id associated with the message. Used for end-to-end tracing and correlation
     */
    private static final String ITK_TRACKING_ID = "itk:DistributionEnvelope/itk:header/@trackingid";
    
    /**
     * XPath to extract the technical service from the message e.g. 
     * <code>urn:nhs-itk:services:201005:getNHSNumber-v1-0</code>
     */
    private static final String ITK_SERVICE = "itk:DistributionEnvelope/itk:header/@service";
    private static final String ITK_BUSINESS_ACK_HANDLING_SPECIFICATIONS = "itk:DistributionEnvelope/itk:header/itk:handlingSpecification/itk:spec[@key='urn:nhs:itk:ns:201005:ackrequested']/@value";
    private static final String ITK_FIRST_PAYLOAD = "itk:DistributionEnvelope/itk:payloads/itk:payload[1]/*[1]";
    private static final String ITK_FIRST_PAYLOAD_TEXT = "itk:DistributionEnvelope/itk:payloads/itk:payload[1]/text()";
    private static final String ITK_FIRST_PAYLOAD_ID = "itk:DistributionEnvelope/itk:payloads/itk:payload[1]/@id";
    private static final String ITK_SIMPLE_MESSAGE_RESPONSE = "itk:SimpleMessageResponse";
    private static final String ITK_SIMPLE_MESSAGE_RESPONSE_CONTENT = "itk:SimpleMessageResponse/* | itk:SimpleMessageResponse/text()";
    private static final String SOAP_WRAPPED_ITK_FIRST_PAYLOAD = SOAP_BODY + "/" + ITK_FIRST_PAYLOAD;
    private static final String SOAP_WRAPPED_ITK_SIMPLE_MESSAGE_RESPONSE = SOAP_BODY + "/" + ITK_SIMPLE_MESSAGE_RESPONSE;
    
    //SOAP Fault XPaths
    private static final String WSA_SOAP_ERROR_DETAIL = SOAP_BODY + "/SOAP:Fault/SOAP:detail/*[1]";
    private static final String WSA_SOAP_ERROR_DETAIL_ID = "itk:itkErrorInfo/itk:ErrorID";
    private static final String WSA_SOAP_ERROR_DETAIL_CODE = "itk:itkErrorInfo/itk:ErrorCode";
    private static final String WSA_SOAP_ERROR_DETAIL_TEXT = "itk:itkErrorInfo/itk:ErrorText";
    private static final String WSA_SOAP_ERROR_DETAIL_DIAGNOSTIC = "itk:itkErrorInfo/itk:ErrorDiagnosticText";
    private static final NamespaceContext NS_CONTEXT = new XMLNamespaceContext();	
    
    public static final XPathExpression ROOT_XPATH = compileXPath(ROOT);
    public static final XPathExpression WSA_MSGID_XPATH = compileXPath(WSA_MSG_ID);
    public static final XPathExpression WSA_REPLY_TO_XPATH = compileXPath(WSA_REPLY_TO);
    public static final XPathExpression WSA_FAULT_TO_XPATH = compileXPath(WSA_FAULT_TO);
    public static final XPathExpression WSA_ACTION_XPATH = compileXPath(WSA_ACTION);
    public static final XPathExpression WSA_FROM_XPATH = compileXPath(WSA_FROM);
    public static final XPathExpression WSA_TO_XPATH = compileXPath(WSA_TO);
    public static final XPathExpression WSA_RELATES_TO_XPATH = compileXPath(WSA_RELATES_TO);
    public static final XPathExpression SOAP_HEADER_XPATH = compileXPath(SOAP_HEADER);
    public static final XPathExpression SOAP_BODY_XPATH = compileXPath(SOAP_BODY);
    public static final XPathExpression SOAP_BODY_CONTENT_XPATH = compileXPath(SOAP_BODY_CONTENT);
    public static final XPathExpression SOAP_WRAPPED_ITK_FIRST_PAYLOAD_XPATH = compileXPath(SOAP_WRAPPED_ITK_FIRST_PAYLOAD);
    public static final XPathExpression SOAP_WRAPPED_ITK_SIMPLE_MESSAGE_RESPONSE_XPATH = compileXPath(SOAP_WRAPPED_ITK_SIMPLE_MESSAGE_RESPONSE);
    public static final XPathExpression ITK_SIMPLE_MESSAGE_RESPONSE_CONTENT_XPATH = compileXPath(ITK_SIMPLE_MESSAGE_RESPONSE_CONTENT);

    // Security headers
    public static final XPathExpression WSA_SECURITY_TIMESTAMP_XPATH = compileXPath(WSA_SECURITY_TIMESTAMP);
    public static final XPathExpression WSA_SECURITY_CREATED_XPATH = compileXPath(WSA_SECURITY_CREATED);
    public static final XPathExpression WSA_SECURITY_EXPIRES_XPATH = compileXPath(WSA_SECURITY_EXPIRES);
    public static final XPathExpression WSA_SECURITY_USERNAME_XPATH = compileXPath(WSA_SECURITY_USERNAME);
    
    //The following compiled XPaths need the distribution envelope as the root node / evaluation context    
    public static final XPathExpression ITK_PAYLOAD_COUNT_XPATH = compileXPath(ITK_PAYLOAD_COUNT);
    public static final XPathExpression ITK_AUDIT_IDENTITY_URI_XPATH = compileXPath(ITK_AUDIT_IDENTITY_URI);
    public static final XPathExpression ITK_AUDIT_IDENTITY_TYPE_XPATH = compileXPath(ITK_AUDIT_IDENTITY_TYPE);
    
    public static final XPathExpression ITK_FROM_ADDRESS_URI_XPATH = compileXPath(ITK_FROM_ADDRESS_URI);
    public static final XPathExpression ITK_FROM_ADDRESS_TYPE_XPATH = compileXPath(ITK_FROM_ADDRESS_TYPE);
    
    public static final XPathExpression ITK_FIRST_TO_ADDRESS_URI_XPATH = compileXPath(ITK_FIRST_TO_ADDRESS_URI);
    public static final XPathExpression ITK_FIRST_TO_ADDRESS_TYPE_XPATH = compileXPath(ITK_FIRST_TO_ADDRESS_TYPE);
    
    public static final XPathExpression ITK_PROFILE_ID_XPATH = compileXPath(ITK_PROFILE_ID);
    public static final XPathExpression ITK_SERVICE_XPATH = compileXPath(ITK_SERVICE);
    public static final XPathExpression ITK_BUSINESS_ACK_HANDLING_SPECIFICATIONS_XPATH = compileXPath(ITK_BUSINESS_ACK_HANDLING_SPECIFICATIONS);
    public static final XPathExpression ITK_TRACKING_ID_XPATH = compileXPath(ITK_TRACKING_ID);
    public static final XPathExpression ITK_FIRST_PAYLOAD_XPATH = compileXPath(ITK_FIRST_PAYLOAD);
    public static final XPathExpression ITK_FIRST_PAYLOAD_ID_XPATH = compileXPath(ITK_FIRST_PAYLOAD_ID);
    public static final XPathExpression ITK_FIRST_PAYLOAD_TEXT_XPATH = compileXPath(ITK_FIRST_PAYLOAD_TEXT);

    public static final XPathExpression ITK_FIRST_MIMETYPE_XPATH = compileXPath(ITK_FIRST_MIMETYPE);
    public static final XPathExpression ITK_FIRST_BASE64_XPATH = compileXPath(ITK_FIRST_BASE64);

    //The following compiled XPaths work for SOAP faults
    public static final XPathExpression WSA_SOAP_ERROR_DETAIL_XPATH = compileXPath(WSA_SOAP_ERROR_DETAIL);
    public static final XPathExpression WSA_SOAP_ERROR_DETAIL_ID_XPATH = compileXPath(WSA_SOAP_ERROR_DETAIL_ID);
    public static final XPathExpression WSA_SOAP_ERROR_DETAIL_CODE_XPATH = compileXPath(WSA_SOAP_ERROR_DETAIL_CODE);
    public static final XPathExpression WSA_SOAP_ERROR_DETAIL_TEXT_XPATH = compileXPath(WSA_SOAP_ERROR_DETAIL_TEXT);
    public static final XPathExpression WSA_SOAP_ERROR_DETAIL_DIAGNOSTIC_XPATH = compileXPath(WSA_SOAP_ERROR_DETAIL_DIAGNOSTIC);
    
	/**
	 * Compile x path.
	 *
	 * @param xPath the x path
	 * @return the x path expression
	 */
	public static XPathExpression compileXPath(String xPath) {
		XPath xp = xpathFactory.newXPath();
		xp.setNamespaceContext(NS_CONTEXT);
		try {
			return xp.compile(xPath);
		} catch (XPathExpressionException e) {
			logger.error("Error compiling XPath \"" + xPath + "\"", e);
		}
		return null;
	}
	
	

}
