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
 * Interface representing the pertinent information for auditing an ITK Message.
 * 
 * @author Nick Jones
 *
 */
public interface AuditDetails {
	
	// Message Ids
	public String getConversationId();
	public String getTimestamp();
	public String getType();
	public String getStatus();
	
	// SETTERS
	// Message Ids
	public void setConversationId(String value);
	public void setTimestamp(String value);
	public void setType(String value);
	public void setStatus(String value);

}
