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
 * Interface representing a logical address of a message originator or destination. The underlying
 * ITK specification provides a structured address format of the form
 * <code><span style="color: red;">urn:nhs-uk:addressing:ods</span>:<span style="color: darkorange;">RHM</span>:<span style="color: green;">department:workgroup</span></code>
 * where the first part (<code><span style="color: red;">urn:nhs-uk:addressing:ods</span></code>) is
 * fixed the next part (<code><span style="color: darkorange;">RHM</span></code> in this example) is the ODS
 * code for the NHS organisation and the latter structures of the address (<code><span style="color: green;">department:workgroup</span></code>) can
 * be locally defined.</br></br>
 * 
 * @author Nick Jones
 *
 */
public interface ITKAddress {
	
	/**
	 * The OID representing the default (ITK) address type. 
	 */
	public static final String DEFAULT_ADDRESS_TYPE = "2.16.840.1.113883.2.1.3.2.4.18.22";

	/**
	 * Allows the address type to be specified if it is not the {@link #DEFAULT_ADDRESS_TYPE}
	 * @param type The OID representing the address type
	 */
	public void setType(String type);

	/**
	 * Obtains the address type
	 * @return the OID representing the type of address
	 */
	public String getType();

	/**
	 * Set the URI for this address
	 * @param uRI the URI to set
	 */
	public void setURI(String uRI);

	/**
	 * Obtain the URI associated with this address - 
	 * e.g. <code>urn:nhs-uk:addressing:ods:XXXX:department:workgroup</code>
	 */
	public String getURI();

}
