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

import uk.co.inhealthcare.open.jsat.AlertException;

public interface EmailSender {
	
	public void send(String conversationId, String headline, String message, String technicalContext, 
			         String businessProcess, String businessContext) throws AlertException ;
}
