
package at.gv.e_government.reference.namespace.moa._20020822;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://reference.e-government.gv.at/namespace/moa/20020822#}VerifyCMSSignatureRequestType">
 *       &lt;attribute name="Signatories" type="{http://reference.e-government.gv.at/namespace/moa/20020822#}SignatoriesType" default="1" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "VerifyCMSSignatureRequest")
public class VerifyCMSSignatureRequest
    extends VerifyCMSSignatureRequestType
{

    @XmlAttribute(name = "Signatories")
    protected List<String> signatories;

    /**
     * Gets the value of the signatories property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the signatories property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSignatories().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSignatories() {
        if (signatories == null) {
            signatories = new ArrayList<String>();
        }
        return this.signatories;
    }

}
