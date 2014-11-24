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

package uk.co.inhealthcare.open.jsat.routes;

import org.apache.camel.builder.RouteBuilder;

/**
 * Java DSL Route definition for the main SMSC input channel and router.
 */
public class JSATMainRouter extends RouteBuilder {
    public void configure() throws Exception {
		from("smscListener")
				.routeId("core-route")
				.unmarshal("hl7dataformat")
				.to("log:uk.co.inhealthcare.open.jsat.routes.JSATMainRouter?level=DEBUG&showAll=true&multiline=true")
				.to("seda:hl7msgin?waitForTaskToComplete=Never")
				.to("bean:ackProcess").marshal("hl7dataformat");
 
    }
}
