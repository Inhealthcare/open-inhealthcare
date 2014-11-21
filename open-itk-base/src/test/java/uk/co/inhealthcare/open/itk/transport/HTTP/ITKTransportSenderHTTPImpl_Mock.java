package uk.co.inhealthcare.open.itk.transport.HTTP;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
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

public class ITKTransportSenderHTTPImpl_Mock implements ITKTransportSender {
	private final static Logger logger = LoggerFactory.getLogger(ITKTransportSenderHTTPImpl_Mock.class);
	public String serviceEndpoint = "";
	public String soapAction = "";
	public int timeout = -1;
	
	public String mockResponseCode = "503";
	public String mockResponseFile = "HappyDay.xml";

	/**
	 * Transport send.
	 *
	 * @param message The ITKMessage object
	 * @param destination the destination
	 * @return the document
	 * @throws ITKMessagingException the iTK messaging exception
	 */
	public Document transportSend(ITKMessage message, ITKTransportRoute destination, Map <String,String> transportProperties)
			throws ITKMessagingException {
		
		Document responseDoc = null;
		// Post the msg
		serviceEndpoint = destination.getPhysicalAddress();
		soapAction = transportProperties.get("SOAPAction");
		timeout = destination.getTransportTimeout(); 

		try {
			int responseCode = 200;
			if (mockResponseCode.equalsIgnoreCase("503")){
				responseCode = 503;
			}
			logger.trace("HTTP Response Code:"+responseCode);
			if (responseCode == HttpURLConnection.HTTP_ACCEPTED){
				
				logger.trace("SIMPLE HTTP ACCEPT (202)");
				
			} else if (responseCode == HttpURLConnection.HTTP_OK) {
				logger.trace("HTTP 200");
				String responseString = readInput(mockResponseFile);
				responseDoc = DomUtils.parse(responseString);
				
			} else if (responseCode == HttpURLConnection.HTTP_UNAVAILABLE) {
				logger.error("HTTP 503");
				throw new ITKUnavailableException("HTTP Unavailable Response / ITK Busy Tone");
				
			} else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
				logger.error("HTTP 500");
				String responseString = readInput(mockResponseFile);
				responseDoc = DomUtils.parse(responseString);

				// Understand why an error has occurred - do we have a SOAP fault?				
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
	private String readInput(String testFileName) throws IOException {
		
	    InputStream tis = ITKTransportSenderHTTPImpl_Mock.class.getResourceAsStream(testFileName);

		BufferedInputStream bis = new BufferedInputStream(tis);
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
