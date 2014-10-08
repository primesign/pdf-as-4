
package at.gv.e_government.reference.namespace.moa._20020822;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for KeyStorageType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="KeyStorageType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Software"/>
 *     &lt;enumeration value="Hardware"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "KeyStorageType")
@XmlEnum
public enum KeyStorageType {

    @XmlEnumValue("Software")
    SOFTWARE("Software"),
    @XmlEnumValue("Hardware")
    HARDWARE("Hardware");
    private final String value;

    KeyStorageType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static KeyStorageType fromValue(String v) {
        for (KeyStorageType c: KeyStorageType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
