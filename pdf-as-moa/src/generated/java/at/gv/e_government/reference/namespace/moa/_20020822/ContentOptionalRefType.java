
package at.gv.e_government.reference.namespace.moa._20020822;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContentOptionalRefType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ContentOptionalRefType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://reference.e-government.gv.at/namespace/moa/20020822#}ContentBaseType">
 *       &lt;attribute name="Reference" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContentOptionalRefType")
@XmlSeeAlso({
    at.gv.e_government.reference.namespace.moa._20020822.DataObjectInfoType.DataObject.class,
    CMSContentBaseType.class,
    ContentRequiredRefType.class
})
public class ContentOptionalRefType
    extends ContentBaseType
{

    @XmlAttribute(name = "Reference")
    @XmlSchemaType(name = "anyURI")
    protected String reference;

    /**
     * Gets the value of the reference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReference() {
        return reference;
    }

    /**
     * Sets the value of the reference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReference(String value) {
        this.reference = value;
    }

}
