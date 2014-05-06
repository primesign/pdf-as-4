//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.04.22 at 04:01:10 PM CEST 
//


package at.gv.egiz.sl.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ApplicationIdentifierType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ApplicationIdentifierType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SecureSignatureApplication"/>
 *     &lt;enumeration value="CertifiedApplication"/>
 *     &lt;enumeration value="InfoboxApplication"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ApplicationIdentifierType")
@XmlEnum
public enum ApplicationIdentifierType {

    @XmlEnumValue("SecureSignatureApplication")
    SECURE_SIGNATURE_APPLICATION("SecureSignatureApplication"),
    @XmlEnumValue("CertifiedApplication")
    CERTIFIED_APPLICATION("CertifiedApplication"),
    @XmlEnumValue("InfoboxApplication")
    INFOBOX_APPLICATION("InfoboxApplication");
    private final String value;

    ApplicationIdentifierType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ApplicationIdentifierType fromValue(String v) {
        for (ApplicationIdentifierType c: ApplicationIdentifierType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}