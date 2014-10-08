
package at.gv.e_government.reference.namespace.moa._20020822;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AllSignatoriesType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AllSignatoriesType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="all"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AllSignatoriesType")
@XmlEnum
public enum AllSignatoriesType {

    @XmlEnumValue("all")
    ALL("all");
    private final String value;

    AllSignatoriesType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AllSignatoriesType fromValue(String v) {
        for (AllSignatoriesType c: AllSignatoriesType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
