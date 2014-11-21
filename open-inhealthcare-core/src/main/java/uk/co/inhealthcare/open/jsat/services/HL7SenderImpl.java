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

import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;

public class HL7SenderImpl implements HL7Sender {

	private final static Logger logger = LoggerFactory.getLogger(HL7SenderImpl.class);
	
	private ProducerTemplate template ;

	public void setTemplate(ProducerTemplate template) {
		this.template = template;
	}


	public HL7SenderImpl(){
		logger.debug("Starting HL7Sender Processor.");
	}
	
	public void process(Message hl7Msg) throws HL7Exception {
		
		template.sendBody("direct:HL7Sender", hl7Msg);
		
		
	}


}
