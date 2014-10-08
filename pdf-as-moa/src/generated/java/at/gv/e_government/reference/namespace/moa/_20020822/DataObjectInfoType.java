
package at.gv.e_government.reference.namespace.moa._20020822;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for DataObjectInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataObjectInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DataObject">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://reference.e-government.gv.at/namespace/moa/20020822#}ContentOptionalRefType">
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;choice>
 *           &lt;element ref="{http://reference.e-government.gv.at/namespace/moa/20020822#}CreateTransformsInfoProfile"/>
 *           &lt;element name="CreateTransformsInfoProfileID" type="{http://reference.e-government.gv.at/namespace/moa/20020822#}ProfileIdentifierType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="Structure" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="detached"/>
 *             &lt;enumeration value="enveloping"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataObjectInfoType", propOrder = {
    "dataObject",
    "createTransformsInfoProfile",
    "createTransformsInfoProfileID"
})
@XmlSeeAlso({
    at.gv.e_government.reference.namespace.moa._20020822.CreateXMLSignatureRequestType.SingleSignatureInfo.DataObjectInfo.class
})
public class DataObjectInfoType {

    @XmlElement(name = "DataObject", required = true)
    protected DataObjectInfoType.DataObject dataObject;
    @XmlElement(name = "CreateTransformsInfoProfile")
    protected CreateTransformsInfoProfile createTransformsInfoProfile;
    @XmlElement(name = "CreateTransformsInfoProfileID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String createTransformsInfoProfileID;
    @XmlAttribute(name = "Structure", required = true)
    protected String structure;

    /**
     * Gets the value of the dataObject property.
     * 
     * @return
     *     possible object is
     *     {@link DataObjectInfoType.DataObject }
     *     
     */
    public DataObjectInfoType.DataObject getDataObject() {
        return dataObject;
    }

    /**
     * Sets the value of the dataObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataObjectInfoType.DataObject }
     *     
     */
    public void setDataObject(DataObjectInfoType.DataObject value) {
        this.dataObject = value;
    }

    /**
     * Gets the value of the createTransformsInfoProfile property.
     * 
     * @return
     *     possible object is
     *     {@link CreateTransformsInfoProfile }
     *     
     */
    public CreateTransformsInfoProfile getCreateTransformsInfoProfile() {
        return createTransformsInfoProfile;
    }

    /**
     * Sets the value of the createTransformsInfoProfile property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreateTransformsInfoProfile }
     *     
     */
    public void setCreateTransformsInfoProfile(CreateTransformsInfoProfile value) {
        this.createTransformsInfoProfile = value;
    }

    /**
     * Gets the value of the createTransformsInfoProfileID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreateTransformsInfoProfileID() {
        return createTransformsInfoProfileID;
    }

    /**
     * Sets the value of the createTransformsInfoProfileID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreateTransformsInfoProfileID(String value) {
        this.createTransformsInfoProfileID = value;
    }

    /**
     * Gets the value of the structure property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStructure() {
        return structure;
    }

    /**
     * Sets the value of the structure property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStructure(String value) {
        this.structure = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://reference.e-government.gv.at/namespace/moa/20020822#}ContentOptionalRefType">
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DataObject
        extends ContentOptionalRefType
    {


    }

}
