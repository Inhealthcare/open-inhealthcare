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
 * An exception to report any errors writing audit entries
 * for instance insufficient information to create the audit
 * entry, DB connectivity or file I/O issues.
 * 
 * @author Nick Jones
 * 
 */
public class AuditException extends Exception {

	private static final long serialVersionUID = 8891576465479815175L;

	/**
	 * Creates an AuditException with any relevant diagnostic
	 * information about the error - such as the likely cause - 
	 * for instance insufficient information to write an audit
	 * record
	 * 
	 * @param message The diagnostic message about the error condition
	 */
	public AuditException(String message) {
		super(message);
	}

	/**
	 * Creates an AuditException that wraps an underlying
	 * <code>Throwable</code> which has been encountered
	 * when try to write the audit record
	 * 
	 * @param cause The <code>Throwable</code> containing
	 * the root cause for the AuditException
	 */
	public AuditException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates an AuditException that wraps an underlying
	 * <code>Throwable</code> which has been encountered
	 * when try to write the audit record as well as any
	 * additional diagnostic information about the error
	 * 
	 * @param message The diagnostic message about the
	 * the error condition
	 * @param cause The <code>Throwable</code> containing
	 * the root cause for the AuditException
	 */
	public AuditException(String message, Throwable cause) {
		super(message, cause);
	}

}
