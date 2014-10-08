
package at.gv.e_government.reference.namespace.moa._20020822;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VerifyTransformsDataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VerifyTransformsDataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element ref="{http://reference.e-government.gv.at/namespace/moa/20020822#}VerifyTransformsInfoProfile"/>
 *         &lt;element name="VerifyTransformsInfoProfileID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VerifyTransformsDataType", propOrder = {
    "verifyTransformsInfoProfileOrVerifyTransformsInfoProfileID"
})
public class VerifyTransformsDataType {

    @XmlElements({
        @XmlElement(name = "VerifyTransformsInfoProfile", type = VerifyTransformsInfoProfile.class),
        @XmlElement(name = "VerifyTransformsInfoProfileID", type = String.class)
    })
    protected List<Object> verifyTransformsInfoProfileOrVerifyTransformsInfoProfileID;

    /**
     * Gets the value of the verifyTransformsInfoProfileOrVerifyTransformsInfoProfileID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the verifyTransformsInfoProfileOrVerifyTransformsInfoProfileID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVerifyTransformsInfoProfileOrVerifyTransformsInfoProfileID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VerifyTransformsInfoProfile }
     * {@link String }
     * 
     * 
     */
    public List<Object> getVerifyTransformsInfoProfileOrVerifyTransformsInfoProfileID() {
        if (verifyTransformsInfoProfileOrVerifyTransformsInfoProfileID == null) {
            verifyTransformsInfoProfileOrVerifyTransformsInfoProfileID = new ArrayList<Object>();
        }
        return this.verifyTransformsInfoProfileOrVerifyTransformsInfoProfileID;
    }

}
