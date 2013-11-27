
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
    