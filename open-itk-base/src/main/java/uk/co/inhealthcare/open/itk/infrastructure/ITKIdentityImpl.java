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

import uk.co.inhealthcare.open.itk.infrastructure.ITKIdentity;

/**
 * The Class ITKIdentityImpl.
 *
 * @author Nick Jones
 * 
 */
public class ITKIdentityImpl implements ITKIdentity {
	
	private String URI;
	private String type;
	
	/**
	 * Instantiates a new iTK identity impl.
	 *
	 * @param URI the uri
	 * @param type the type
	 */
	public ITKIdentityImpl(String URI, String type){
		this.URI = URI;
		this.type = type;
	}
	
	/**
	 * Instantiates a new iTK identity impl.
	 *
	 * @param URI the uri
	 */
	public ITKIdentityImpl(String URI){
		this.URI = URI;
		this.type = ITKIdentity.DEFAULT_IDENTITY_TYPE;
	}

	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.infrastructure.ITKIdentity#getURI()
	 */
	@Override
	public String getURI() {
		return URI;
	}
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.infrastructure.ITKIdentity#setURI(java.lang.String)
	 */
	@Override
	public void setURI(String uRI) {
		URI = uRI;
	}
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.infrastructure.ITKIdentity#getType()
	 */
	@Override
	public String getType() {
		return type;
	}
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.infrastructure.ITKIdentity#setType(java.lang.String)
	 */
	@Override
	public void setType(String type) {
		this.type = type;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ITKIdentity [" + this.type + "] " + this.URI;
	}
}
