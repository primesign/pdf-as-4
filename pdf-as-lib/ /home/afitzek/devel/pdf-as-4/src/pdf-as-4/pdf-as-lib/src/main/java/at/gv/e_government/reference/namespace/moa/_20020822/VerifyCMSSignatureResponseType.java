/**
 * VerifyCMSSignatureResponseType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package at.gv.e_government.reference.namespace.moa._20020822;

public class VerifyCMSSignatureResponseType  implements java.io.Serializable {
    /* only ds:X509Data and RetrievalMethod is supported; QualifiedCertificate
     * is included as X509Data/any;publicAuthority is included as X509Data/any */
    private org.w3.www._2000._09.xmldsig.KeyInfoType signerInfo;

    private at.gv.e_government.reference.namespace.moa._20020822.CheckResultType signatureCheck;

    private at.gv.e_government.reference.namespace.moa._20020822.CheckResultType certificateCheck;

    public VerifyCMSSignatureResponseType() {
    }

    public VerifyCMSSignatureResponseType(
           org.w3.www._2000._09.xmldsig.KeyInfoType signerInfo,
           at.gv.e_government.reference.namespace.moa._20020822.CheckResultType signatureCheck,
           at.gv.e_government.reference.namespace.moa._20020822.CheckResultType certificateCheck) {
           this.signerInfo = signerInfo;
           this.signatureCheck = signatureCheck;
           this.certificateCheck = certificateCheck;
    }


    /**
     * Gets the signerInfo value for this VerifyCMSSignatureResponseType.
     * 
     * @return signerInfo   * only ds:X509Data and RetrievalMethod is supported; QualifiedCertificate
     * is included as X509Data/any;publicAuthority is included as X509Data/any
     */
    public org.w3.www._2000._09.xmldsig.KeyInfoType getSignerInfo() {
        return signerInfo;
    }


    /**
     * Sets the signerInfo value for this VerifyCMSSignatureResponseType.
     * 
     * @param signerInfo   * only ds:X509Data and RetrievalMethod is supported; QualifiedCertificate
     * is included as X509Data/any;publicAuthority is included as X509Data/any
     */
    public void setSignerInfo(org.w3.www._2000._09.xmldsig.KeyInfoType signerInfo) {
        this.signerInfo = signerInfo;
    }


    /**
     * Gets the signatureCheck value for this VerifyCMSSignatureResponseType.
     * 
     * @return signatureCheck
     */
    public at.gv.e_government.reference.namespace.moa._20020822.CheckResultType getSignatureCheck() {
        return signatureCheck;
    }


    /**
     * Sets the signatureCheck value for this VerifyCMSSignatureResponseType.
     * 
     * @param signatureCheck
     */
    public void setSignatureCheck(at.gv.e_government.reference.namespace.moa._20020822.CheckResultType signatureCheck) {
        this.signatureCheck = signatureCheck;
    }


    /**
     * Gets the certificateCheck value for this VerifyCMSSignatureResponseType.
     * 
     * @return certificateCheck
     */
    public at.gv.e_government.reference.namespace.moa._20020822.CheckResultType getCertificateCheck() {
        return certificateCheck;
    }


    /**
     * Sets the certificateCheck value for this VerifyCMSSignatureResponseType.
     * 
     * @param certificateCheck
     */
    public void setCertificateCheck(at.gv.e_government.reference.namespace.moa._20020822.CheckResultType certificateCheck) {
        this.certificateCheck = certificateCheck;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VerifyCMSSignatureResponseType)) return false;
        VerifyCMSSignatureResponseType other = (VerifyCMSSignatureResponseType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.signerInfo==null && other.getSignerInfo()==null) || 
             (this.signerInfo!=null &&
              this.signerInfo.equals(other.getSignerInfo()))) &&
            ((this.signatureCheck==null && other.getSignatureCheck()==null) || 
             (this.signatureCheck!=null &&
              this.signatureCheck.equals(other.getSignatureCheck()))) &&
            ((this.certificateCheck==null && other.getCertificateCheck()==null) || 
             (this.certificateCheck!=null &&
              this.certificateCheck.equals(other.getCertificateCheck())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getSignerInfo() != null) {
            _hashCode += getSignerInfo().hashCode();
        }
        if (getSignatureCheck() != null) {
            _hashCode += getSignatureCheck().hashCode();
        }
        if (getCertificateCheck() != null) {
            _hashCode += getCertificateCheck().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VerifyCMSSignatureResponseType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "VerifyCMSSignatureResponseType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signerInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "SignerInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2000/09/xmldsig#", "KeyInfoType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signatureCheck");
        elemField.setXmlName(new javax.xml.namespace.QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "SignatureCheck"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "CheckResultType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("certificateCheck");
        elemField.setXmlName(new javax.xml.namespace.QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "CertificateCheck"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "CheckResultType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
