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

import uk.co.inhealthcare.open.itk.service.ITKService;

/**
 * The Class ITKServiceImpl.
 *
 * @author Nick Jones
 */
public class ITKServiceImpl implements ITKService {

	private String serviceId;
    private boolean supportsSync;
    private boolean supportsAsync;
    private boolean base64;
    private String mimeType;
    
    /* (non-Javadoc)
     * @see uk.nhs.interoperability.service.ITKService#getMimeType()
     */
    public String getMimeType() {
		return mimeType;
	}
	
	/**
	 * Sets the mime type.
	 *
	 * @param mimeType the new mime type
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.service.ITKService#isBase64()
	 */
	public boolean isBase64(){
    	return base64;
    }
    
    /**
     * Sets the base64.
     *
     * @param base64 the new base64
     */
    public void setBase64(boolean base64){
    	this.base64 = base64;
    }
    
    /* (non-Javadoc)
     * @see uk.nhs.interoperability.service.ITKService#supportsAsync()
     */
    public boolean supportsAsync(){
    	return supportsAsync;
    }
    
    /**
     * Sets the supports async.
     *
     * @param supportsAsync the new supports async
     */
    public void setSupportsAsync(boolean supportsAsync){
    	this.supportsAsync = supportsAsync;
    }
    
    /* (non-Javadoc)
     * @see uk.nhs.interoperability.service.ITKService#supportsSync()
     */
    public boolean supportsSync(){
    	return supportsSync;
    }
    
    /**
     * Sets the supports sync.
     *
     * @param supportsSync the new supports sync
     */
    public void setSupportsSync(boolean supportsSync){
    	this.supportsSync = supportsSync;
    }
    
	/* (non-Javadoc)
	 * @see uk.nhs.interoperability.service.ITKService#getServiceId()
	 */
	public String getServiceId() {
		return serviceId;
	}

	/**
	 * Sets the service id.
	 *
	 * @param serviceId the new service id
	 */
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
	/**
	 * Instantiates a new iTK service impl.
	 *
	 * @param serviceId the service id
	 */
	public ITKServiceImpl(String serviceId){
		this.serviceId = serviceId;
	}
	
}
