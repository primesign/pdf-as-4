/**
 * VerifyXMLSignatureResponseType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package at.gv.e_government.reference.namespace.moa._20020822;

public class VerifyXMLSignatureResponseType  implements java.io.Serializable {
    /* only ds:X509Data and ds:RetrievalMethod is supported; QualifiedCertificate
     * is included as X509Data/any; PublicAuthority is included as X509Data/any */
    private org.w3.www._2000._09.xmldsig.KeyInfoType signerInfo;

    private at.gv.e_government.reference.namespace.moa._20020822.InputDataType[] hashInputData;

    private at.gv.e_government.reference.namespace.moa._20020822.InputDataType[] referenceInputData;

    private at.gv.e_government.reference.namespace.moa._20020822.ReferencesCheckResultType signatureCheck;

    private at.gv.e_government.reference.namespace.moa._20020822.ReferencesCheckResultType signatureManifestCheck;

    private at.gv.e_government.reference.namespace.moa._20020822.ManifestRefsCheckResultType[] XMLDSIGManifestCheck;

    private at.gv.e_government.reference.namespace.moa._20020822.CheckResultType certificateCheck;

    public VerifyXMLSignatureResponseType() {
    }

    public VerifyXMLSignatureResponseType(
           org.w3.www._2000._09.xmldsig.KeyInfoType signerInfo,
           at.gv.e_government.reference.namespace.moa._20020822.InputDataType[] hashInputData,
           at.gv.e_government.reference.namespace.moa._20020822.InputDataType[] referenceInputData,
           at.gv.e_government.reference.namespace.moa._20020822.ReferencesCheckResultType signatureCheck,
           at.gv.e_government.reference.namespace.moa._20020822.ReferencesCheckResultType signatureManifestCheck,
           at.gv.e_government.reference.namespace.moa._20020822.ManifestRefsCheckResultType[] XMLDSIGManifestCheck,
           at.gv.e_government.reference.namespace.moa._20020822.CheckResultType certificateCheck) {
           this.signerInfo = signerInfo;
           this.hashInputData = hashInputData;
           this.referenceInputData = referenceInputData;
           this.signatureCheck = signatureCheck;
           this.signatureManifestCheck = signatureManifestCheck;
           this.XMLDSIGManifestCheck = XMLDSIGManifestCheck;
           this.certificateCheck = certificateCheck;
    }


    /**
     * Gets the signerInfo value for this VerifyXMLSignatureResponseType.
     * 
     * @return signerInfo   * only ds:X509Data and ds:RetrievalMethod is supported; QualifiedCertificate
     * is included as X509Data/any; PublicAuthority is included as X509Data/any
     */
    public org.w3.www._2000._09.xmldsig.KeyInfoType getSignerInfo() {
        return signerInfo;
    }


    /**
     * Sets the signerInfo value for this VerifyXMLSignatureResponseType.
     * 
     * @param signerInfo   * only ds:X509Data and ds:RetrievalMethod is supported; QualifiedCertificate
     * is included as X509Data/any; PublicAuthority is included as X509Data/any
     */
    public void setSignerInfo(org.w3.www._2000._09.xmldsig.KeyInfoType signerInfo) {
        this.signerInfo = signerInfo;
    }


    /**
     * Gets the hashInputData value for this VerifyXMLSignatureResponseType.
     * 
     * @return hashInputData
     */
    public at.gv.e_government.reference.namespace.moa._20020822.InputDataType[] getHashInputData() {
        return hashInputData;
    }


    /**
     * Sets the hashInputData value for this VerifyXMLSignatureResponseType.
     * 
     * @param hashInputData
     */
    public void setHashInputData(at.gv.e_government.reference.namespace.moa._20020822.InputDataType[] hashInputData) {
        this.hashInputData = hashInputData;
    }

    public at.gv.e_government.reference.namespace.moa._20020822.InputDataType getHashInputData(int i) {
        return this.hashInputData[i];
    }

    public void setHashInputData(int i, at.gv.e_government.reference.namespace.moa._20020822.InputDataType _value) {
        this.hashInputData[i] = _value;
    }


    /**
     * Gets the referenceInputData value for this VerifyXMLSignatureResponseType.
     * 
     * @return referenceInputData
     */
    public at.gv.e_government.reference.namespace.moa._20020822.InputDataType[] getReferenceInputData() {
        return referenceInputData;
    }


    /**
     * Sets the referenceInputData value for this VerifyXMLSignatureResponseType.
     * 
     * @param referenceInputData
     */
    public void setReferenceInputData(at.gv.e_government.reference.namespace.moa._20020822.InputDataType[] referenceInputData) {
        this.referenceInputData = referenceInputData;
    }

    public at.gv.e_government.reference.namespace.moa._20020822.InputDataType getReferenceInputData(int i) {
        return this.referenceInputData[i];
    }

    public void setReferenceInputData(int i, at.gv.e_government.reference.namespace.moa._20020822.InputDataType _value) {
        this.referenceInputData[i] = _value;
    }


    /**
     * Gets the signatureCheck value for this VerifyXMLSignatureResponseType.
     * 
     * @return signatureCheck
     */
    public at.gv.e_government.reference.namespace.moa._20020822.ReferencesCheckResultType getSignatureCheck() {
        return signatureCheck;
    }


    /**
     * Sets the signatureCheck value for this VerifyXMLSignatureResponseType.
     * 
     * @param signatureCheck
     */
    public void setSignatureCheck(at.gv.e_government.reference.namespace.moa._20020822.ReferencesCheckResultType signatureCheck) {
        this.signatureCheck = signatureCheck;
    }


    /**
     * Gets the signatureManifestCheck value for this VerifyXMLSignatureResponseType.
     * 
     * @return signatureManifestCheck
     */
    public at.gv.e_government.reference.namespace.moa._20020822.ReferencesCheckResultType getSignatureManifestCheck() {
        return signatureManifestCheck;
    }


    /**
     * Sets the signatureManifestCheck value for this VerifyXMLSignatureResponseType.
     * 
     * @param signatureManifestCheck
     */
    public void setSignatureManifestCheck(at.gv.e_government.reference.namespace.moa._20020822.ReferencesCheckResultType signatureManifestCheck) {
        this.signatureManifestCheck = signatureManifestCheck;
    }


    /**
     * Gets the XMLDSIGManifestCheck value for this VerifyXMLSignatureResponseType.
     * 
     * @return XMLDSIGManifestCheck
     */
    public at.gv.e_government.reference.namespace.moa._20020822.ManifestRefsCheckResultType[] getXMLDSIGManifestCheck() {
        return XMLDSIGManifestCheck;
    }


    /**
     * Sets the XMLDSIGManifestCheck value for this VerifyXMLSignatureResponseType.
     * 
     * @param XMLDSIGManifestCheck
     */
    public void setXMLDSIGManifestCheck(at.gv.e_government.reference.namespace.moa._20020822.ManifestRefsCheckResultType[] XMLDSIGManifestCheck) {
        this.XMLDSIGManifestCheck = XMLDSIGManifestCheck;
    }

    public at.gv.e_government.reference.namespace.moa._20020822.ManifestRefsCheckResultType getXMLDSIGManifestCheck(int i) {
        return this.XMLDSIGManifestCheck[i];
    }

    public void setXMLDSIGManifestCheck(int i, at.gv.e_government.reference.namespace.moa._20020822.ManifestRefsCheckResultType _value) {
        this.XMLDSIGManifestCheck[i] = _value;
    }


    /**
     * Gets the certificateCheck value for this VerifyXMLSignatureResponseType.
     * 
     * @return certificateCheck
     */
    public at.gv.e_government.reference.namespace.moa._20020822.CheckResultType getCertificateCheck() {
        return certificateCheck;
    }


    /**
     * Sets the certificateCheck value for this VerifyXMLSignatureResponseType.
     * 
     * @param certificateCheck
     */
    public void setCertificateCheck(at.gv.e_government.reference.namespace.moa._20020822.CheckResultType certificateCheck) {
        this.certificateCheck = certificateCheck;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VerifyXMLSignatureResponseType)) return false;
        VerifyXMLSignatureResponseType other = (VerifyXMLSignatureResponseType) obj;
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
            ((this.hashInputData==null && other.getHashInputData()==null) || 
             (this.hashInputData!=null &&
              java.util.Arrays.equals(this.hashInputData, other.getHashInputData()))) &&
            ((this.referenceInputData==null && other.getReferenceInputData()==null) || 
             (this.referenceInputData!=null &&
              java.util.Arrays.equals(this.referenceInputData, other.getReferenceInputData()))) &&
            ((this.signatureCheck==null && other.getSignatureCheck()==null) || 
             (this.signatureCheck!=null &&
              this.signatureCheck.equals(other.getSignatureCheck()))) &&
            ((this.signatureManifestCheck==null && other.getSignatureManifestCheck()==null) || 
             (this.signatureManifestCheck!=null &&
              this.signatureManifestCheck.equals(other.getSignatureManifestCheck()))) &&
            ((this.XMLDSIGManifestCheck==null && other.getXMLDSIGManifestCheck()==null) || 
             (this.XMLDSIGManifestCheck!=null &&
              java.util.Arrays.equals(this.XMLDSIGManifestCheck, other.getXMLDSIGManifestCheck()))) &&
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
        if (getHashInputData() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getHashInputData());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getHashInputData(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getReferenceInputData() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getReferenceInputData());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getReferenceInputData(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSignatureCheck() != null) {
            _hashCode += getSignatureCheck().hashCode();
        }
        if (getSignatureManifestCheck() != null) {
            _hashCode += getSignatureManifestCheck().hashCode();
        }
        if (getXMLDSIGManifestCheck() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getXMLDSIGManifestCheck());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getXMLDSIGManifestCheck(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getCertificateCheck() != null) {
            _hashCode += getCertificateCheck().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VerifyXMLSignatureResponseType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "VerifyXMLSignatureResponseType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signerInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "SignerInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2000/09/xmldsig#", "KeyInfoType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hashInputData");
        elemField.setXmlName(new javax.xml.namespace.QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "HashInputData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "InputDataType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("referenceInputData");
        elemField.setXmlName(new javax.xml.namespace.QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "ReferenceInputData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "InputDataType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signatureCheck");
        elemField.setXmlName(new javax.xml.namespace.QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "SignatureCheck"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "ReferencesCheckResultType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signatureManifestCheck");
        elemField.setXmlName(new javax.xml.namespace.QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "SignatureManifestCheck"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "ReferencesCheckResultType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XMLDSIGManifestCheck");
        elemField.setXmlName(new javax.xml.namespace.QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "XMLDSIGManifestCheck"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "ManifestRefsCheckResultType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
