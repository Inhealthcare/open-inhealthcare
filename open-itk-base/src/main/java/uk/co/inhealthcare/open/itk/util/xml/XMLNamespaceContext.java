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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class XMLNamespaceContext.
 *
 * @author Nick Jones
 */
public class XMLNamespaceContext implements NamespaceContext {

	private final static Logger logger = LoggerFactory.getLogger(XMLNamespaceContext.class);

	/** The Constant HL7NAMESPACE. */
	public static final String HL7NAMESPACE = "urn:hl7-org:v3";
	
	/** The Constant HL7NAMESPACE_DEFAULY_PREFIX. */
	public static final String HL7NAMESPACE_DEFAULY_PREFIX = "hl7";
    
    /** The Constant SOAPENVNAMESPACE. */
    public static final String SOAPENVNAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";
    
    /** The Constant SOAPENVNAMESPACE_DEFAULT_PREFIX. */
    public static final String SOAPENVNAMESPACE_DEFAULT_PREFIX = "SOAP";
    
    /** The Constant HL7V2NAMESPACE. */
    public static final String HL7V2NAMESPACE = "urn:hl7-org:v2xml";
    
    /** The Constant HL7V2NAMESPACE_DEFAULT_PREFIX. */
    public static final String HL7V2NAMESPACE_DEFAULT_PREFIX = "hl7v2";
    
    /** The Constant ITKNAMESPACE. */
    public static final String ITKNAMESPACE = "urn:nhs-itk:ns:201005";
    
    /** The Constant ITKNAMESPACE_DEFAULT_PREFIX. */
    public static final String ITKNAMESPACE_DEFAULT_PREFIX = "itk";
    
    /** The Constant WSANAMESPACE. */
    public static final String WSANAMESPACE = "http://www.w3.org/2005/08/addressing";
    
    /** The Constant WSANAMESPACE_DEFAULT_PREFIX. */
    public static final String WSANAMESPACE_DEFAULT_PREFIX = "wsa";
    
    /** The Constant WSSENAMESPACE. */
    public static final String WSSENAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    
    /** The Constant WSSENAMESPACE_DEFAULT_PREFIX. */
    public static final String WSSENAMESPACE_DEFAULT_PREFIX = "wsse";
    
    /** The Constant WSUNAMESPACE. */
    public static final String WSUNAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
    
    /** The Constant WSUNAMESPACE_DEFAULT_PREFIX. */
    public static final String WSUNAMESPACE_DEFAULT_PREFIX = "wsu";
    
    /** The namespaces. */
    public static Map<String, String> namespaces = new HashMap<String, String>();
    
    static {
    	namespaces.put(HL7NAMESPACE_DEFAULY_PREFIX, HL7NAMESPACE);
    	namespaces.put(SOAPENVNAMESPACE_DEFAULT_PREFIX, SOAPENVNAMESPACE);
    	namespaces.put(HL7V2NAMESPACE_DEFAULT_PREFIX, HL7V2NAMESPACE);
    	namespaces.put(ITKNAMESPACE_DEFAULT_PREFIX, ITKNAMESPACE);
    	namespaces.put(WSANAMESPACE_DEFAULT_PREFIX, WSANAMESPACE);
    	namespaces.put(WSSENAMESPACE_DEFAULT_PREFIX, WSSENAMESPACE);
    	namespaces.put(WSUNAMESPACE_DEFAULT_PREFIX, WSUNAMESPACE);
    	
    }

	/* (non-Javadoc)
	 * @see javax.xml.namespace.NamespaceContext#getNamespaceURI(java.lang.String)
	 */
	@Override
	public String getNamespaceURI(String prefix) {
		return namespaces.get(prefix);
	}

	/* (non-Javadoc)
	 * @see javax.xml.namespace.NamespaceContext#getPrefix(java.lang.String)
	 */
	@Override
	public String getPrefix(String namespaceURI) {
		for (Map.Entry<String, String> entry : namespaces.entrySet()) {
			if (entry.getValue().equals(namespaceURI)) {
				return entry.getKey();
			}
		}
		logger.warn("No prefix found for namespace: " + namespaceURI);
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.xml.namespace.NamespaceContext#getPrefixes(java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Iterator getPrefixes(String namespaceURI) {
		List<String> prefixes = new ArrayList<String>();
		for (Map.Entry<String, String> entry : namespaces.entrySet()) {
			if (entry.getValue().equals(namespaceURI)) {
				prefixes.add(entry.getKey());
			}
		}
		return prefixes.iterator();
	}
	
	

}
