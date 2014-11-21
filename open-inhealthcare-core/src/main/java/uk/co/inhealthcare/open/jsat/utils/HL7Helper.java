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

package uk.co.inhealthcare.open.jsat.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

public class HL7Helper {

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
	
	private static AtomicInteger messageSequence = new AtomicInteger();

	public static String getCreationDate() {
		return DATE_FORMAT.format(Calendar.getInstance().getTime());
	}
	
	public static String getMessageId() {
		return getCreationDate() + getSequenceNumber(); 
	}

	// LIMITATION: This implementation has limitations and some implementors may want to replace this
	private static String getSequenceNumber(){

		String seqString = String.format("%06d", messageSequence.incrementAndGet());
		
		// Recycle the counter so that it never exceeds 6 digits
		if (messageSequence.get() >= 999999){
			messageSequence.set(0);
		}
		return seqString;
		
	}

}
