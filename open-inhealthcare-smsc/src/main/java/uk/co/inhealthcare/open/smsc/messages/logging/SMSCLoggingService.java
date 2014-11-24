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
package uk.co.inhealthcare.open.smsc.messages.logging;

import uk.co.inhealthcare.open.smsc.messages.SMSPRequest;
import uk.co.inhealthcare.open.smsc.messages.SMSPResponse;


/**
 * An representation of an ITK compliant audit service that has the capability
 * to audit ITK messaging related events
 * 
 * In general the expected usage pattern is to audit on both the client and service
 * provider sides of any ITK operation. Additionally the intent to do something
 * should be audited - e.g. intention to send a message or call a service {@link #MESSAGE_SENT_EVENT}.
 * An additional audit call may then be required for the same logical action should
 * any processing errors occur - e.g. connection timeouts {@link #MESSAGE_PROCESSING_FAILURE}
 *  
 * @author Nick Jones
 * @since version 0.1
 */
public interface SMSCLoggingService {
	
	public void logSMSPRequest(SMSPRequest request) throws LoggingException;
	public void logSMSPResponse(SMSPResponse response) throws LoggingException;

}
