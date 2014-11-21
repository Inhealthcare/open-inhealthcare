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

package uk.co.inhealthcare.open.jsat;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This abstract class provides support for all JSAT Components.
 * The primary function is to manage the JSAT configuration and provide convenience lookup methods.
 * 
 * @author Nick Jones
 *
 */
public abstract class JSATComponent {
	
	protected static final String SMSP_REQUIRED_FOR_NHS_STATUS = "SMSP.RequiredForNHSStatus";
	protected static final String SMSP_RESPONSE_CODE_TECH_ALERT = "SMSP.ResponseCodeTechnicalAlert";
	protected static final String SMSP_RESPONSE_CODE_BUS_ALERT = "SMSP.ResponseCodeBusinessAlert";
	protected static final String SMSP_RESPONSE_CODE_DESC = "SMSP.ResponseCodeDesc";
	protected static final String SMSP_RESPONSE_TO_NSTS = "SMSP.ResponseToNSTS";
	protected static final String SMSP_RETAIN_NHS_NUMBER = "SMSP.NSTSCodeRetainNHSNumber";
	protected static final String SMSP_GENDER = "SMSP.Gender";
	protected static final String SMSP_ALERT_ON_VALIDATION_ERROR = "SMSP.AlertOnValidationError";
	
	private final static Logger logger = LoggerFactory.getLogger(JSATComponent.class);

	protected static final String propertiesLocation = "/JSAT.properties";
		
	protected static final Properties jsatProperties = loadProperties();
	
	private static Properties loadProperties() {
		Properties props = new Properties();
		try {
			props.load(JSATComponent.class.getResourceAsStream(propertiesLocation));
		} catch (IOException e) {
			logger.error("Failed to set System Properties",e);
			throw new RuntimeException(e);
		}
		logger.debug("JSAT: Properties Initialised.");
		return props;
	}
	
	public String getLookup(String tableName, String key, String defaultValue){
		String propertyName = tableName+"."+key;
		return jsatProperties.getProperty(propertyName,defaultValue);
	}
	
	public boolean getLookupBoolean(String tableName, String key, String defaultValue){
		String propertyName = tableName+"."+key;
		String val = jsatProperties.getProperty(propertyName,defaultValue);
		boolean boolVal = false;
		if (val.equalsIgnoreCase("Y")) boolVal = true;
		return boolVal;
	}

	public String getLookup(String tableName, String key){
		String propertyName = tableName+"."+key;
		return jsatProperties.getProperty(propertyName);
	}
	
}
