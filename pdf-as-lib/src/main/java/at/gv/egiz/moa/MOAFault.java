/*******************************************************************************
 * <copyright> Copyright 2014 by E-Government Innovation Center EGIZ, Graz, Austria </copyright>
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
 ******************************************************************************/
/**
 * MOAFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package at.gv.egiz.moa;

public class MOAFault extends java.lang.Exception{

    private static final long serialVersionUID = 1385038355027L;
    
    private at.gv.egiz.moa.SignatureCreationServiceStub.ErrorResponse faultMessage;

    
        public MOAFault() {
            super("MOAFault");
        }

        public MOAFault(java.lang.String s) {
           super(s);
        }

        public MOAFault(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public MOAFault(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(at.gv.egiz.moa.SignatureCreationServiceStub.ErrorResponse msg){
       faultMessage = msg;
    }
    
    public at.gv.egiz.moa.SignatureCreationServiceStub.ErrorResponse getFaultMessage(){
       return faultMessage;
    }
}
    
