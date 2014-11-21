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

package uk.co.inhealthcare.open.itk.capabilities;


/**
 * Interface representing the pertinent information for auditing a SOAP part of an ITK Message.
 * 
 * @author Nick Jones
 *
 */
public interface SOAPAuditDetails extends AuditDetails {
	
	// Transport Details
	public String getMessageId();
	public String getCreationTime();
	public String getTo();
	public String getAction();
	public String getUserId();
	
	// SETTERS
	// Transport Details
	public void setMessageId(String value);
	public void setCreationTime(String value);
	public void setTo(String value);
	public void setAction(String value);
	public void setUserId(String value);
	
}
