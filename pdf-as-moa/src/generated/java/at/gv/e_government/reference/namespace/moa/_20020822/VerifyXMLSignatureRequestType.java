
package at.gv.e_government.reference.namespace.moa._20020822;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for VerifyXMLSignatureRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VerifyXMLSignatureRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="VerifySignatureInfo">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="VerifySignatureEnvironment" type="{http://reference.e-government.gv.at/namespace/moa/20020822#}ContentOptionalRefType"/>
 *                   &lt;element name="VerifySignatureLocation" type="{http://www.w3.org/2001/XMLSchema}token"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://reference.e-government.gv.at/namespace/moa/20020822#}SupplementProfile"/>
 *           &lt;element name="SupplementProfileID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;/choice>
 *         &lt;element name="SignatureManifestCheckParams" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ReferenceInfo" type="{http://reference.e-government.gv.at/namespace/moa/20020822#}VerifyTransformsDataType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="ReturnReferenceInputData" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ReturnHashInputData" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="TrustProfileID" type="{http://www.w3.org/2001/XMLSchema}token"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VerifyXMLSignatureRequestType", propOrder = {
    "dateTime",
    "verifySignatureInfo",
    "supplementProfileOrSupplementProfileID",
    "signatureManifestCheckParams",
    "returnHashInputData",
    "trustProfileID"
})
public class VerifyXMLSignatureRequestType {

    @XmlElement(name = "DateTime")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateTime;
    @XmlElement(name = "VerifySignatureInfo", required = true)
    protected VerifyXMLSignatureRequestType.VerifySignatureInfo verifySignatureInfo;
    @XmlElements({
        @XmlElement(name = "SupplementProfile", type = XMLDataObjectAssociationType.class),
        @XmlElement(name = "SupplementProfileID", type = String.class)
    })
    protected List<Object> supplementProfileOrSupplementProfileID;
    @XmlElement(name = "SignatureManifestCheckParams")
    protected VerifyXMLSignatureRequestType.SignatureManifestCheckParams signatureManifestCheckParams;
    @XmlElement(name = "ReturnHashInputData")
    protected Object returnHashInputData;
    @XmlElement(name = "TrustProfileID", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String trustProfileID;

    /**
     * Gets the value of the dateTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateTime() {
        return dateTime;
    }

    /**
     * Sets the value of the dateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateTime(XMLGregorianCalendar value) {
        this.dateTime = value;
    }

    /**
     * Gets the value of the verifySignatureInfo property.
     * 
     * @return
     *     possible object is
     *     {@link VerifyXMLSignatureRequestType.VerifySignatureInfo }
     *     
     */
    public VerifyXMLSignatureRequestType.VerifySignatureInfo getVerifySignatureInfo() {
        return verifySignatureInfo;
    }

    /**
     * Sets the value of the verifySignatureInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link VerifyXMLSignatureRequestType.VerifySignatureInfo }
     *     
     */
    public void setVerifySignatureInfo(VerifyXMLSignatureRequestType.VerifySignatureInfo value) {
        this.verifySignatureInfo = value;
    }

    /**
     * Gets the value of the supplementProfileOrSupplementProfileID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supplementProfileOrSupplementProfileID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupplementProfileOrSupplementProfileID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XMLDataObjectAssociationType }
     * {@link String }
     * 
     * 
     */
    public List<Object> getSupplementProfileOrSupplementProfileID() {
        if (supplementProfileOrSupplementProfileID == null) {
            supplementProfileOrSupplementProfileID = new ArrayList<Object>();
        }
        return this.supplementProfileOrSupplementProfileID;
    }

    /**
     * Gets the value of the signatureManifestCheckParams property.
     * 
     * @return
     *     possible object is
     *     {@link VerifyXMLSignatureRequestType.SignatureManifestCheckParams }
     *     
     */
    public VerifyXMLSignatureRequestType.SignatureManifestCheckParams getSignatureManifestCheckParams() {
        return signatureManifestCheckParams;
    }

    /**
     * Sets the value of the signatureManifestCheckParams property.
     * 
     * @param value
     *     allowed object is
     *     {@link VerifyXMLSignatureRequestType.SignatureManifestCheckParams }
     *     
     */
    public void setSignatureManifestCheckParams(VerifyXMLSignatureRequestType.SignatureManifestCheckParams value) {
        this.signatureManifestCheckParams = value;
    }

    /**
     * Gets the value of the returnHashInputData property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getReturnHashInputData() {
        return returnHashInputData;
    }

    /**
     * Sets the value of the returnHashInputData property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setReturnHashInputData(Object value) {
        this.returnHashInputData = value;
    }

    /**
     * Gets the value of the trustProfileID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrustProfileID() {
        return trustProfileID;
    }

    /**
     * Sets the value of the trustProfileID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrustProfileID(String value) {
        this.trustProfileID = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="ReferenceInfo" type="{http://reference.e-government.gv.at/namespace/moa/20020822#}VerifyTransformsDataType" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *       &lt;attribute name="ReturnReferenceInputData" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "referenceInfo"
    })
    public static class SignatureManifestCheckParams {

        @XmlElement(name = "ReferenceInfo", required = true)
        protected List<VerifyTransformsDataType> referenceInfo;
        @XmlAttribute(name = "ReturnReferenceInputData")
        protected Boolean returnReferenceInputData;

        /**
         * Gets the value of the referenceInfo property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the referenceInfo property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getReferenceInfo().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link VerifyTransformsDataType }
         * 
         * 
         */
        public List<VerifyTransformsDataType> getReferenceInfo() {
            if (referenceInfo == null) {
                referenceInfo = new ArrayList<VerifyTransformsDataType>();
            }
            return this.referenceInfo;
        }

        /**
         * Gets the value of the returnReferenceInputData property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public boolean isReturnReferenceInputData() {
            if (returnReferenceInputData == null) {
                return true;
            } else {
                return returnReferenceInputData;
            }
        }

        /**
         * Sets the value of the returnReferenceInputData property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setReturnReferenceInputData(Boolean value) {
            this.returnReferenceInputData = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="VerifySignatureEnvironment" type="{http://reference.e-government.gv.at/namespace/moa/20020822#}ContentOptionalRefType"/>
     *         &lt;element name="VerifySignatureLocation" type="{http://www.w3.org/2001/XMLSchema}token"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "verifySignatureEnvironment",
        "verifySignatureLocation"
    })
    public static class VerifySignatureInfo {

        @XmlElement(name = "VerifySignatureEnvironment", required = true)
        protected ContentOptionalRefType verifySignatureEnvironment;
        @XmlElement(name = "VerifySignatureLocation", required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "token")
        protected String verifySignatureLocation;

        /**
         * Gets the value of the verifySignatureEnvironment property.
         * 
         * @return
         *     possible object is
         *     {@link ContentOptionalRefType }
         *     
         */
        public ContentOptionalRefType getVerifySignatureEnvironment() {
            return verifySignatureEnvironment;
        }

        /**
         * Sets the value of the verifySignatureEnvironment property.
         * 
         * @param value
         *     allowed object is
         *     {@link ContentOptionalRefType }
         *     
         */
        public void setVerifySignatureEnvironment(ContentOptionalRefType value) {
            this.verifySignatureEnvironment = value;
        }

        /**
         * Gets the value of the verifySignatureLocation property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getVerifySignatureLocation() {
            return verifySignatureLocation;
        }

        /**
         * Sets the value of the verifySignatureLocation property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setVerifySignatureLocation(String value) {
            this.verifySignatureLocation = value;
        }

    }

}
