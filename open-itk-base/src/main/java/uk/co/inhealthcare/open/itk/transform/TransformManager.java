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

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;

/**
 * The Class TransformManager.
 *
 * @author Nick Jones
 */
public class TransformManager {
	
	private final static Logger logger = LoggerFactory.getLogger(TransformManager.class);

	// This class would benefit from optimisation, but is kept simple for now
	// It will need some rationalisation w.r.t. template locations at least for RI
	
	/**
	 * Do transform.
	 *
	 * @param tname the tname
	 * @param input the input
	 * @return the string
	 * @throws ITKMessagingException the iTK messaging exception
	 */
	public static String doTransform(String tname, String input) throws ITKMessagingException {
		return doTransform(tname, input, null);
	}
	
	/**
	 * Do transform.
	 *
	 * @param tname the tname
	 * @param input the input
	 * @param parameters the parameters
	 * @return the string
	 * @throws ITKMessagingException the iTK messaging exception
	 */
	public static String doTransform(String tname, String input, Map<String, String> parameters) throws ITKMessagingException {
		InputStream tis = TransformManager.class.getResourceAsStream("/"
				+ tname);
		return doTransform(tname, tis, input, parameters);
	}

	/**
	 * Do transform.
	 *
	 * @param tname
	 *            the tname
	 * @param input
	 *            the input
	 * @param parameters
	 *            the parameters
	 * @return the string
	 * @throws ITKMessagingException
	 *             the iTK messaging exception
	 */
	public static String doTransform(String tname, InputStream tis,
			String input, Map<String, String> parameters)
			throws ITKMessagingException {
		
		if ((tname == null)||(tname.isEmpty())) {
			String eMsg = "Transformer name not provided";
			logger.error(eMsg);
			throw new ITKMessagingException(null, ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, eMsg);
		}
		if ((tis == null)) {
			String eMsg = "Transformer input stream not provided";
			logger.error(eMsg);
			throw new ITKMessagingException(null,
					ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE,
					eMsg);
		}
		if ((input == null)||(input.isEmpty())) {
			String eMsg = "Transformation Input not provided";
			logger.error(eMsg);
			throw new ITKMessagingException(null, ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, eMsg);
		}

		logger.trace("Transformation requested:"+tname);
    	String output = "";
    	
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
        StreamSource s = new StreamSource(tis);

        Templates t = null;
        try {
			t = transformerFactory.newTemplates(s);
	        Transformer tf = t.newTransformer();
	        
	        //Set any stylesheet parameters as appropriate
	        if (parameters != null) {
				for (Map.Entry<String, String> entry: parameters.entrySet()) {;
					logger.trace("Found a stylesheet parameter " + entry);
					tf.setParameter(entry.getKey(), entry.getValue());
				}
			}
	        
	        StreamSource s2 = new StreamSource(new StringReader(input));
	        StringWriter w = new StringWriter();
	        StreamResult r = new StreamResult(w);

	        tf.transform(s2, r);
	        output = w.getBuffer().toString();
	        logger.trace("Transformation complete. Transformer:"+tname);
		} catch (TransformerConfigurationException tce) {
			logger.error("Error Building ITK Message",tce);
			throw new ITKMessagingException("Transformer Configuration Exception");
		} catch (TransformerException te) {
			logger.error("Error Building ITK Message",te);
			throw new ITKMessagingException("Transformer Exception");
		}

        return output;

     }

}
