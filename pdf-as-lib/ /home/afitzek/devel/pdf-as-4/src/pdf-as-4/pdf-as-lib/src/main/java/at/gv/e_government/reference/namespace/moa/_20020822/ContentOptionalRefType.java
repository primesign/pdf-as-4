/**
 * ContentOptionalRefType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package at.gv.e_government.reference.namespace.moa._20020822;

public class ContentOptionalRefType  extends at.gv.e_government.reference.namespace.moa._20020822.ContentBaseType  implements java.io.Serializable {
    private org.apache.axis.types.URI reference;  // attribute

    public ContentOptionalRefType() {
    }

    public ContentOptionalRefType(
           byte[] base64Content,
           at.gv.e_government.reference.namespace.moa._20020822.XMLContentType XMLContent,
           org.apache.axis.types.URI locRefContent,
           org.apache.axis.types.URI reference) {
        super(
            base64Content,
            XMLContent,
            locRefContent);
        this.reference = reference;
    }


    /**
     * Gets the reference value for this ContentOptionalRefType.
     * 
     * @return reference
     */
    public org.apache.axis.types.URI getReference() {
        return reference;
    }


    /**
     * Sets the reference value for this ContentOptionalRefType.
     * 
     * @param reference
     */
    public void setReference(org.apache.axis.types.URI reference) {
        this.reference = reference;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ContentOptionalRefType)) return false;
        ContentOptionalRefType other = (ContentOptionalRefType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.reference==null && other.getReference()==null) || 
             (this.reference!=null &&
              this.reference.equals(other.getReference())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getReference() != null) {
            _hashCode += getReference().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ContentOptionalRefType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "ContentOptionalRefType"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("reference");
        attrField.setXmlName(new javax.xml.namespace.QName("", "Reference"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        typeDesc.addFieldDesc(attrField);
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
