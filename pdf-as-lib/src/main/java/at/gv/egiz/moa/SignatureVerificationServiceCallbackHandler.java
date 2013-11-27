
/**
 * SignatureVerificationServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

    package at.gv.egiz.moa;

    /**
     *  SignatureVerificationServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class SignatureVerificationServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public SignatureVerificationServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public SignatureVerificationServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for verifyXMLSignature method
            * override this method for handling normal response from verifyXMLSignature operation
            */
           public void receiveResultverifyXMLSignature(
                    at.gv.egiz.moa.SignatureVerificationServiceStub.VerifyXMLSignatureResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from verifyXMLSignature operation
           */
            public void receiveErrorverifyXMLSignature(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for verifyCMSSignature method
            * override this method for handling normal response from verifyCMSSignature operation
            */
           public void receiveResultverifyCMSSignature(
                    at.gv.egiz.moa.SignatureVerificationServiceStub.VerifyCMSSignatureResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from verifyCMSSignature operation
           */
            public void receiveErrorverifyCMSSignature(java.lang.Exception e) {
            }
                


    }
    