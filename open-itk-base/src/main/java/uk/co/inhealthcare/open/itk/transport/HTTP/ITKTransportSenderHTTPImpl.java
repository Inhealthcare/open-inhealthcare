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
package uk.co.inhealthcare.open.itk.transport.HTTP;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import uk.co.inhealthcare.open.itk.infrastructure.ITKCommsException;
import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.infrastructure.ITKTransportTimeoutException;
import uk.co.inhealthcare.open.itk.infrastructure.ITKUnavailableException;
import uk.co.inhealthcare.open.itk.payload.ITKMessage;
import uk.co.inhealthcare.open.itk.transport.ITKTransportRoute;
import uk.co.inhealthcare.open.itk.transport.ITKTransportSender;
import uk.co.inhealthcare.open.itk.transport.WS.ITKSOAPException;
import uk.co.inhealthcare.open.itk.util.xml.DomUtils;

/**
 * The Class HTTPSender.
 *
 * @author Nick Jones
 */
public class ITKTransportSenderHTTPImpl implements ITKTransportSender {

	private final static Logger logger = LoggerFactory.getLogger(ITKTransportSenderHTTPImpl.class);

	/**
	 * Transport send.
	 *
	 * @param message The ITKMessage object
	 * @param destination the destination
	 * @return the document
	 * @throws ITKMessagingException the iTK messaging exception
	 */
	public Document transportSend(ITKMessage message, ITKTransportRoute destination, Map<String, String> httpProperties)
			throws ITKMessagingException {
		
		Document responseDoc = null;
		
		// The following code allows test certificates where the CN name does not match the hostname
		// Consider if there is a more elegant way of achieving this.
		String allowLocalHost = System.getProperty(
				"JSAT.OverrideHostnameVerification", "N");
		if (allowLocalHost.equalsIgnoreCase("Y")) {
			logger.warn("JSAT configured to override hostname verification. Do not use this setting in Live");
			javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
				    new javax.net.ssl.HostnameVerifier(){
				 
				        public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
				        	logger.warn("Hostname is:"+hostname);
			                return true;
				        }
				    });
		}
			
		// Post the msg
		String serviceEndpoint = destination.getPhysicalAddress();
		try {
			URLConnection urlConnection = new URL(serviceEndpoint).openConnection();
            
			HttpURLConnection conn = (HttpURLConnection) urlConnection;
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-type","text/xml");
			conn.setRequestProperty("accept-charset","UTF-8");
			
			for (Map.Entry<String, String> entry : httpProperties.entrySet()) {
			    conn.setRequestProperty(entry.getKey(), entry.getValue());
			}

			conn.setConnectTimeout(destination.getTransportTimeout()); 
			PrintWriter pw = new PrintWriter(conn.getOutputStream());
			pw.write(message.getFullMessage());
			pw.close();
			int responseCode = conn.getResponseCode();
			logger.trace("HTTP Response Code:"+responseCode);
			if (responseCode == HttpURLConnection.HTTP_ACCEPTED){
				
				logger.trace("SIMPLE HTTP ACCEPT (202)");
				
			} else if (responseCode == HttpURLConnection.HTTP_OK) {
				logger.trace("HTTP 200");
				String responseString = readInput(conn.getInputStream());
				responseDoc = DomUtils.parse(responseString);
				
			} else if (responseCode == HttpURLConnection.HTTP_UNAVAILABLE) {
				logger.error("HTTP 503");
				throw new ITKUnavailableException("HTTP Unavailable Response / ITK Busy Tone");
				
			} else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
				logger.error("HTTP 500");
				String responseString = readInput(conn.getErrorStream());
				//Understand why an error has occurred - do we have a SOAP fault?				
				if (responseString != null && responseString.contains("http://www.w3.org/2005/08/addressing/fault")) {
					//SOAP fault
					throw ITKSOAPException.parseSOAPFault(responseString);
				} else {
					throw new ITKCommsException("HTTP Internal server error");
				}
			} else {
				
				logger.error("Unrecognized HTTP response code:"+responseCode);
				throw new ITKCommsException("Unrecognized HTTP response code:"+responseCode);
			}
			
		} catch (MalformedURLException mue) {
			logger.error("MalformedURLException on WS-CALL", mue);
			throw new ITKCommsException("Configuration error sending ITK Message");
		} catch (SocketTimeoutException ste) {
			logger.error("Timeout on WS-CALL", ste);
			throw new ITKTransportTimeoutException("Transport timeout sending ITK Message");
		} catch (IOException ioe) {
			logger.error("IOException on WS-CALL", ioe);
			throw new ITKCommsException("Transport error sending ITK Message");
		} catch (SAXException se) {
			logger.error("SAXException processing response from WS-CALL", se);
			throw new ITKCommsException("XML Error Processing ITK Response");
		} catch (ParserConfigurationException pce) {
			logger.error("ParseConfigurationException on WS-CALL", pce);
			throw new ITKCommsException("XML Configuration Error Processing ITK Response");
		}
		
		return responseDoc;
		
	}
	
	/**
	 * Read input from a stream
	 *
	 * @param is the is
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String readInput(InputStream is) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(is);
		byte[] contents = new byte[1024];
		int bytesRead = 0;
		String responseString = "";
		while ((bytesRead = bis.read(contents)) != -1) {
			responseString = responseString + new String(contents, 0, bytesRead);
		}
		logger.trace("Response was:"+responseString);
		bis.close();
		
		return responseString ;
	}
	
}
