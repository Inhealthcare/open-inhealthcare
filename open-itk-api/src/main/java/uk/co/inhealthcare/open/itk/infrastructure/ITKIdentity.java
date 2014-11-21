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
package uk.co.inhealthcare.open.itk.infrastructure;

/**
 * Interface representing a logical identity of a person or system typically used for audit purposes.
 * The identity is made up of two parts: the type which defaults to an ITK Identity, and the uri 
 * which contains the actual value.
 * 
 * @author Nick Jones
 *
 */
public interface ITKIdentity {
	
	/**
	 * The OID representing the default (ITK) identity type. 
	 */
	public static final String DEFAULT_IDENTITY_TYPE = "2.16.840.1.113883.2.1.3.2.4.18.27";

	/**
	 * The OID representing the SPINE User Id. 
	 */
	public static final String SPINE_UUID_TYPE = "1.2.826.0.1285.0.2.0.65";

	/**
	 * The OID representing the SPINE User Role Profile Id. 
	 */
	public static final String SPINE_ROLE_PROFILE_TYPE = "1.2.826.0.1285.0.2.0.67";

	/**
	 * The OID representing the SPINE ROLE Id. 
	 */
	public static final String SPINE_ROLE_TYPE = "1.2.826.0.1285.0.2.0.108";

	/**
	 * The OID representing the NHS Number. 
	 */
	public static final String NHS_NUMBER_TYPE = "2.16.840.1.113883.2.1.4.1";

	/**
	 * The OID representing the Local Patient Id 
	 */
	public static final String LOCAL_PATIENT_TYPE = "2.16.840.1.113883.2.1.3.2.4.18.24 ";

	/**
	 * Allows the identity type to be specified if it is not the {@link #DEFAULT_IDENTITY_TYPE}
	 * @param type The OID representing the address type
	 */
	public void setType(String type);

	/**
	 * Obtains the identity type
	 * @return the OID representing the type of identity
	 */
	public String getType();

	/**
	 * Set the URI for this identity
	 * @param uRI the URI to set
	 */
	public void setURI(String uRI);

	/**
	 * Obtain the URI associated with this identity
	 */
	public String getURI();

}
