package at.gv.e_government.reference.namespace.moa._20020822_;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 3.0.1
 * 2014-10-08T16:17:24.741+02:00
 * Generated source version: 3.0.1
 * 
 */
@WebService(targetNamespace = "http://reference.e-government.gv.at/namespace/moa/20020822#", name = "SignatureVerificationPortType")
@XmlSeeAlso({at.gv.e_government.reference.namespace.moa._20020822.ObjectFactory.class, org.w3._2000._09.xmldsig.ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface SignatureVerificationPortType {

    @WebResult(name = "VerifyXMLSignatureResponse", targetNamespace = "http://reference.e-government.gv.at/namespace/moa/20020822#", partName = "body")
    @WebMethod(action = "urn:VerifyXMLSignatureAction")
    public at.gv.e_government.reference.namespace.moa._20020822.VerifyXMLSignatureResponseType verifyXMLSignature(
        @WebParam(partName = "body", name = "VerifyXMLSignatureRequest", targetNamespace = "http://reference.e-government.gv.at/namespace/moa/20020822#")
        at.gv.e_government.reference.namespace.moa._20020822.VerifyXMLSignatureRequestType body
    ) throws MOAFault;

    @WebResult(name = "VerifyCMSSignatureResponse", targetNamespace = "http://reference.e-government.gv.at/namespace/moa/20020822#", partName = "body")
    @WebMethod(action = "urn:VerifyCMSSignatureAction")
    public at.gv.e_government.reference.namespace.moa._20020822.VerifyCMSSignatureResponseType verifyCMSSignature(
        @WebParam(partName = "body", name = "VerifyCMSSignatureRequest", targetNamespace = "http://reference.e-government.gv.at/namespace/moa/20020822#")
        at.gv.e_government.reference.namespace.moa._20020822.VerifyCMSSignatureRequest body
    ) throws MOAFault;
}