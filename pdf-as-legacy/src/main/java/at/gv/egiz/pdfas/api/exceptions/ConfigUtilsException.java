/**
 * <copyright> Copyright 2006 by Know-Center, Graz, Austria </copyright>
 * PDF-AS has been contracted by the E-Government Innovation Center EGIZ, a
 * joint initiative of the Federal Chancellery Austria and Graz University of
 * Technology.
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * http://www.osor.eu/eupl/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * This product combines work with different licenses. See the "NOTICE" text
 * file for details on the various modules and licenses.
 * The "NOTICE" text file is part of the distribution. Any derivative works
 * that you distribute must include a readable copy of the "NOTICE" text file.
 */
package at.gv.egiz.pdfas.api.exceptions;

/**
 * @author <a href="mailto:thomas.knall@egiz.gv.at">Thomas Knall</a>
 */
public class ConfigUtilsException extends Exception {

   /**
    * Marker for serialization.
    */
   private static final long serialVersionUID = 1L;

   /**
    * The underlying exception.
    */
   private Exception wrappedException;

   /**
    * Returns the underlying exception.
    *
    * @return The underlying exception.
    */
   public Exception getException() {
      return this.wrappedException;
   }

   /**
    * Returns the message of the wrapped exception.
    *
    * @return The message of the wrapped exception.
    */
   public String getMessage() {
      String message = super.getMessage();
      if (message == null && this.wrappedException != null) {
         return this.wrappedException.getMessage();
      } else {
         return message;
      }
   }

   /**
    * Instantiation of a new exception based on a message and another (wrapped)
    * exception.
    *
    * @param message
    *           The exception message.
    * @param exception
    *           Another exception.
    */
   public ConfigUtilsException(final String message, final Exception exception) {
      super(message);
      this.wrappedException = exception;
   }

   /**
    * Instantiated a new exception based on a message.
    *
    * @param message
    *           The message of the new exception.
    */
   public ConfigUtilsException(final String message) {
      super(message);
      this.wrappedException = null;
   }

   /**
    * Instantiates a new exception based on another (wrapped) exception.
    *
    * @param exception
    *           The wrapped exception.
    */
   public ConfigUtilsException(final Exception exception) {
      super();
      this.wrappedException = exception;
   }

   /**
    * Instantiates a new (unspecified) exception.
    */
   public ConfigUtilsException() {
      super();
      this.wrappedException = null;

   }

   /**
    * Returns the text representation of this instance.
    *
    * @return The text representation of this instance.
    */
   public String toString() {
      if (this.wrappedException != null) {
         return this.wrappedException.toString();
      } else {
         return super.toString();
      }
   }

}
