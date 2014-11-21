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

package uk.co.inhealthcare.open.jsat.services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.inhealthcare.open.itk.transform.TransformManager;
import uk.co.inhealthcare.open.jsat.AlertException;

public class EmailSenderImpl implements EmailSender {

	private final static Logger logger = LoggerFactory.getLogger(EmailSenderImpl.class);

	public final static String ERR_SENDING = "Error sending the alert by email";
	public final static String ERR_BUILDING_CONTEXT = "Error building the business context";

	/** The Constant DATE_FORMAT. */
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	/** The Constant CAL. */
	private static final Calendar CAL = Calendar.getInstance();
	
	private ProducerTemplate template ;
	private String subject;
	private String contentTransformer;
	private String to;
	private String from;

	public void setTemplate(ProducerTemplate template) {
		this.template = template;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setContentTransformer(String contentTransformer) {
		this.contentTransformer = contentTransformer;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public EmailSenderImpl(){
		logger.debug("Starting EmailSender Processor.");
	}
	
	@Override
	public void send(String conversationId, String headline, String message,
			String technicalContext, String businessProcess,
			String businessContext) throws AlertException {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("To", this.to);
		map.put("Subject", this.subject);
		map.put("ContentType", "text/html");
		map.put("From", this.from);
		
		String htmlMessage = getContent(contentTransformer, conversationId, headline, message, 
				technicalContext, businessProcess, businessContext);
		try{
			template.sendBodyAndHeaders("direct:EmailSender", htmlMessage, map);
		} catch (Exception e){
			logger.error("Error sending email .",e);
			throw new AlertException(ERR_SENDING);
		}
		
	}

	private String getContent(String contentTransformer, String conversationId, String headline, String message, 
			String technicalContext, String businessProcess,
			String businessContext) throws AlertException {

		// Protect against illegal characters in the technicalContext (likely for HL7 messages/fragments) 
		String techContextEnc = StringEscapeUtils.escapeXml(technicalContext);

		String dateTime = DATE_FORMAT.format(CAL.getTime());
		String XML = "<Email>"+
			  "<Headline>"+headline+"</Headline>"+
			  "<Message>"+message+"</Message>"+
			  "<DateTime>"+dateTime+"</DateTime>"+
			  "<ConversationId>"+conversationId+"</ConversationId>"+
			  "<BusinessProcess>"+businessProcess+"</BusinessProcess>"+
			  "<TechnicalContext>"+techContextEnc+"</TechnicalContext>"+
			  "<BusinessContext>"+businessContext+"</BusinessContext>"+
			  "</Email>";
		logger.debug(XML);

		String serialisedMessage ="";
		try {
			serialisedMessage = TransformManager.doTransform(contentTransformer, XML);
		} catch (Exception e) {
			logger.error("Error creating email body.",e);
			throw new AlertException(ERR_BUILDING_CONTEXT);
		}

		logger.debug(serialisedMessage);
		return serialisedMessage;

	}
	
}
