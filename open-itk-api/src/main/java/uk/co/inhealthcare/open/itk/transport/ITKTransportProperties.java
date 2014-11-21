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
package uk.co.inhealthcare.open.itk.transport;

import uk.co.inhealthcare.open.itk.infrastructure.ITKMessageProperties;
import uk.co.inhealthcare.open.itk.payload.ITKMessage;

/**
 * Container for transport properties associated with the
 * {@link ITKMessage}.<br/><br/>
 * 
 * 
 * @see ITKMessageProperties
 * @see ITKMessage
 * 
 * @author Nick Jones
 *
 */
public interface ITKTransportProperties {

	/**
	 * Sets the unique transport level identifier for the
	 * associated message.
	 * 
	 * Typically this identifier is not
	 * know by, or preserved by application layer components
	 * and is often only pertinent to an individual transport
	 * hop (for example ITK WS-ADR-01 says every hop/WS invocation
	 * should use a new
	 * <code>&lt;wsa:MessageID/&gt;</code>).
	 * 
	 * @param transportMessageId
	 */
	public void setTransportMessageId(String transportMessageId);

	/**
	 * Gets the unique transport level identifier for the
	 * associated message.
	 * 
	 * Typically this identifier is not
	 * know by, or preserved by application layer components
	 * and is often only pertinent to an individual transport
	 * hop (for example ITK WS-ADR-01 says every hop/WS invocation
	 * should use a new
	 * <code>&lt;wsa:MessageID/&gt;</code>).
	 * 
	 * @return the <code>transportMessageId</code> for
	 * the transport hop
	 */
	public String getTransportMessageId();

	/**
	 * Sets the transportAction for the associated ITKMessage.<br/><br/>
	 * 
	 * For SOAP this is mapped to/from <code>&lt;wsa:Action/&gt;</code>
	 * 
	 * @param transportAction The transport action - e.g. 
	 * <code>urn:nhs-itk:services:201005:transferPatient-v1-0</code>
	 */
	public void setTransportAction(String transportAction);

	/**
	 * Gets the transportAction for the associated ITKMessage.<br/><br/>
	 * 
	 * For SOAP this is mapped to/from <code>&lt;wsa:Action/&gt;</code>
	 * 
	 * @return The transport action - e.g. 
	 * <code>urn:nhs-itk:services:201005:transferPatient-v1-0</code>
	 */
	public String getTransportAction();

	/**
	 * Sets the transport address property that allows asynchronous transport layer
	 * faults to be returned.<br/><br/>
	 * 
	 * For SOAP this is mapped to/from <code>&lt;wsa:FaultTo/&gt;</code>.<br/><br/>
	 * 
	 * Note if the transport does not support a distinct <code>faultTo</code> property
	 * this should be set to the same as {@link #setTransportReplyTo(String)} property
	 * or failing that the {@link #setTransportFrom(String)}
	 * 
	 * @param transportFaultTo The transport fault to address - e.g. 
	 * <code>http://myserver.com/faulthandler</code>
	 */
	public void setTransportFaultTo(String transportFaultTo);

	/**
	 * Gets the transport address property that allows asynchronous transport layer
	 * faults to be returned.<br/><br/>
	 * 
	 * For SOAP this is mapped to/from <code>&lt;wsa:FaultTo/&gt;</code><br/><br/>
	 * 
	 * Note if the transport does not support a distinct <code>faultTo</code> property
	 * this should return the same as {@link #getTransportReplyTo()} property
	 * or failing that the {@link #getTransportFrom()}
	 * 
	 * @return The transport fault to address - e.g. 
	 * <code>http://myserver.com/faulthandler</code>
	 */
	public String getTransportFaultTo();

	/**
	 * Sets the transport address property that allows asynchronous transport layer
	 * responses to be returned.<br/><br/>
	 * 
	 * For SOAP this is mapped to/from <code>&lt;wsa:ReplyTo/&gt;</code>.<br/><br/>
	 * 
	 * Note if the transport does not support a distinct <code>replyTo</code> property
	 * this should be set to the same as the {@link #setTransportFrom(String)}
	 * 
	 * @param transportReplyTo The transport reply to address - e.g. 
	 * <code>http://myserver.com/responsehandler</code>
	 */
	public void setTransportReplyTo(String transportReplyTo);

	/**
	 * Gets the transport address property that allows asynchronous transport layer
	 * responses to be returned.<br/><br/>
	 * 
	 * For SOAP this is mapped to/from <code>&lt;wsa:ReplyTo/&gt;</code>.<br/><br/>
	 * 
	 * Note if the transport does not support a distinct <code>replyTo</code> property
	 * this should return the same as the {@link #getTransportFrom()}
	 * 
	 * @return The transport reply to address - e.g. 
	 * <code>http://myserver.com/responsehandler</code>
	 */
	public String getTransportReplyTo();

	/**
	 * Sets the transport relates to property that allows asynchronous transport layer
	 * messages to be correlated with their associated requests.<br/><br/>
	 * 
	 * For SOAP this is mapped to/from <code>&lt;wsa:RelatesTo/&gt;</code>.<br/><br/>
	 * 
	 * @param transportRelatesTo The message id of the message to which this relates.
	 * For request messages this should be <code>null</code>
	 */
	public void setTransportRelatesTo(String transportRelatesTo);

	/**
	 * Gets the transport relates to property that allows asynchronous transport layer
	 * messages to be correlated with their associated requests.<br/><br/>
	 * 
	 * For SOAP this is mapped to/from <code>&lt;wsa:RelatesTo/&gt;</code>.<br/><br/>
	 * 
	 * @return The message id of the message to which this relates.
	 * For request messages this will return <code>null</code>
	 */
	public String getTransportRelatesTo();

	/**
	 * Sets the transport hop destination. Typically (at least for SOAP)
	 * this may be equivalent to {@link #setInvokedUrl(String)}
	 * 
	 * @param transportTo The destination system address
	 */
	public void setTransportTo(String transportTo);

	/**
	 * Gets the transport hop destination. Typically (at least for SOAP)
	 * this may be equivalent to {@link #getInvokedUrl()}
	 * 
	 * @return The destination system address
	 */
	public String getTransportTo();

	/**
	 * Sets the transport from address property - this may be used to return
	 * asynchronous responses to the originator.<br/><br/>
	 * 
	 * For SOAP this is mapped to/from <code>&lt;wsa:From/&gt;</code>.<br/><br/>
	 * 
	 * @param transportFrom The address of the message originator
	 */
	public void setTransportFrom(String transportFrom);

	/**
	 * Gets the transport from address property - this may be used to return
	 * asynchronous responses to the originator.<br/><br/>
	 * 
	 * For SOAP this is mapped to/from <code>&lt;wsa:From/&gt;</code>.<br/><br/>
	 * 
	 * @return The address of the message originator
	 */
	public String getTransportFrom();

	public String getTransportUsername();

	public void setTransportUsername(String transportUsername);
	
	public String getTransportCreatedTime();

	public void setTransportCreatedTime(String createdTime);
	
	public String getTransportExpiresTime();

	public void setTransportExpiresTime(String expiresTime);
	

}
