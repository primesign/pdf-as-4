package at.gv.egiz.sl.schema;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for BulkRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BulkRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="CreateSignatureRequest" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}CreateCMSSignatureRequest"/>
 *                   &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}CreateXMLSignatureRequest"/>
 *                 &lt;/choice>
 *                 &lt;attribute name="displayName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="VerifySignatureRequest" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}VerifyCMSSignatureRequest"/>
 *                   &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}VerifyXMLSignatureRequest"/>
 *                 &lt;/choice>
 *                 &lt;attribute name="displayName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="EncryptRequest" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}EncryptCMSRequest"/>
 *                   &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}EncryptXMLRequest"/>
 *                 &lt;/choice>
 *                 &lt;attribute name="displayName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DecryptRequest" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}DecryptCMSRequest"/>
 *                   &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}DecryptXMLRequest"/>
 *                 &lt;/choice>
 *                 &lt;attribute name="displayName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BulkRequestType", propOrder = {
    "createSignatureRequest",
    "verifySignatureRequest",
    "encryptRequest",
    "decryptRequest"
})
public class BulkRequestType {

    @XmlElement(name = "CreateSignatureRequest")
    protected List<BulkRequestType.CreateSignatureRequest> createSignatureRequest;
    @XmlElement(name = "VerifySignatureRequest")
    protected List<BulkRequestType.VerifySignatureRequest> verifySignatureRequest;
    @XmlElement(name = "EncryptRequest")
    protected List<BulkRequestType.EncryptRequest> encryptRequest;
    @XmlElement(name = "DecryptRequest")
    protected List<BulkRequestType.DecryptRequest> decryptRequest;

    /**
     * Gets the value of the createSignatureRequest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the createSignatureRequest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCreateSignatureRequest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BulkRequestType.CreateSignatureRequest }
     * 
     * 
     */
    public List<BulkRequestType.CreateSignatureRequest> getCreateSignatureRequest() {
        if (createSignatureRequest == null) {
            createSignatureRequest = new ArrayList<BulkRequestType.CreateSignatureRequest>();
        }
        return this.createSignatureRequest;
    }

    /**
     * Gets the value of the verifySignatureRequest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the verifySignatureRequest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVerifySignatureRequest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BulkRequestType.VerifySignatureRequest }
     * 
     * 
     */
    public List<BulkRequestType.VerifySignatureRequest> getVerifySignatureRequest() {
        if (verifySignatureRequest == null) {
            verifySignatureRequest = new ArrayList<BulkRequestType.VerifySignatureRequest>();
        }
        return this.verifySignatureRequest;
    }

    /**
     * Gets the value of the encryptRequest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the encryptRequest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEncryptRequest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BulkRequestType.EncryptRequest }
     * 
     * 
     */
    public List<BulkRequestType.EncryptRequest> getEncryptRequest() {
        if (encryptRequest == null) {
            encryptRequest = new ArrayList<BulkRequestType.EncryptRequest>();
        }
        return this.encryptRequest;
    }

    /**
     * Gets the value of the decryptRequest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the decryptRequest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDecryptRequest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BulkRequestType.DecryptRequest }
     * 
     * 
     */
    public List<BulkRequestType.DecryptRequest> getDecryptRequest() {
        if (decryptRequest == null) {
            decryptRequest = new ArrayList<BulkRequestType.DecryptRequest>();
        }
        return this.decryptRequest;
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
     *       &lt;choice>
     *         &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}CreateCMSSignatureRequest"/>
     *         &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}CreateXMLSignatureRequest"/>
     *       &lt;/choice>
     *       &lt;attribute name="displayName" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "createCMSSignatureRequest",
        "createXMLSignatureRequest"
    })
    public static class CreateSignatureRequest {

        @XmlElement(name = "CreateCMSSignatureRequest")
        protected CreateCMSSignatureRequestType createCMSSignatureRequest;
        @XmlElement(name = "CreateXMLSignatureRequest")
        protected CreateXMLSignatureRequestType createXMLSignatureRequest;
        @XmlAttribute
        protected String displayName;
        @XmlAttribute
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;

        /**
         * Gets the value of the createCMSSignatureRequest property.
         * 
         * @return
         *     possible object is
         *     {@link CreateCMSSignatureRequestType }
         *     
         */
        public CreateCMSSignatureRequestType getCreateCMSSignatureRequest() {
            return createCMSSignatureRequest;
        }

        /**
         * Sets the value of the createCMSSignatureRequest property.
         * 
         * @param value
         *     allowed object is
         *     {@link CreateCMSSignatureRequestType }
         *     
         */
        public void setCreateCMSSignatureRequest(CreateCMSSignatureRequestType value) {
            this.createCMSSignatureRequest = value;
        }

        /**
         * Gets the value of the createXMLSignatureRequest property.
         * 
         * @return
         *     possible object is
         *     {@link CreateXMLSignatureRequestType }
         *     
         */
        public CreateXMLSignatureRequestType getCreateXMLSignatureRequest() {
            return createXMLSignatureRequest;
        }

        /**
         * Sets the value of the createXMLSignatureRequest property.
         * 
         * @param value
         *     allowed object is
         *     {@link CreateXMLSignatureRequestType }
         *     
         */
        public void setCreateXMLSignatureRequest(CreateXMLSignatureRequestType value) {
            this.createXMLSignatureRequest = value;
        }

        /**
         * Gets the value of the displayName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * Sets the value of the displayName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDisplayName(String value) {
            this.displayName = value;
        }

        /**
         * Gets the value of the id property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setId(String value) {
            this.id = value;
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
     *       &lt;choice>
     *         &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}DecryptCMSRequest"/>
     *         &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}DecryptXMLRequest"/>
     *       &lt;/choice>
     *       &lt;attribute name="displayName" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "decryptCMSRequest",
        "decryptXMLRequest"
    })
    public static class DecryptRequest {

        @XmlElement(name = "DecryptCMSRequest")
        protected DecryptCMSRequestType decryptCMSRequest;
        @XmlElement(name = "DecryptXMLRequest")
        protected DecryptXMLRequestType decryptXMLRequest;
        @XmlAttribute
        protected String displayName;
        @XmlAttribute
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;

        /**
         * Gets the value of the decryptCMSRequest property.
         * 
         * @return
         *     possible object is
         *     {@link DecryptCMSRequestType }
         *     
         */
        public DecryptCMSRequestType getDecryptCMSRequest() {
            return decryptCMSRequest;
        }

        /**
         * Sets the value of the decryptCMSRequest property.
         * 
         * @param value
         *     allowed object is
         *     {@link DecryptCMSRequestType }
         *     
         */
        public void setDecryptCMSRequest(DecryptCMSRequestType value) {
            this.decryptCMSRequest = value;
        }

        /**
         * Gets the value of the decryptXMLRequest property.
         * 
         * @return
         *     possible object is
         *     {@link DecryptXMLRequestType }
         *     
         */
        public DecryptXMLRequestType getDecryptXMLRequest() {
            return decryptXMLRequest;
        }

        /**
         * Sets the value of the decryptXMLRequest property.
         * 
         * @param value
         *     allowed object is
         *     {@link DecryptXMLRequestType }
         *     
         */
        public void setDecryptXMLRequest(DecryptXMLRequestType value) {
            this.decryptXMLRequest = value;
        }

        /**
         * Gets the value of the displayName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * Sets the value of the displayName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDisplayName(String value) {
            this.displayName = value;
        }

        /**
         * Gets the value of the id property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setId(String value) {
            this.id = value;
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
     *       &lt;choice>
     *         &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}EncryptCMSRequest"/>
     *         &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}EncryptXMLRequest"/>
     *       &lt;/choice>
     *       &lt;attribute name="displayName" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "encryptCMSRequest",
        "encryptXMLRequest"
    })
    public static class EncryptRequest {

        @XmlElement(name = "EncryptCMSRequest")
        protected EncryptCMSRequestType encryptCMSRequest;
        @XmlElement(name = "EncryptXMLRequest")
        protected EncryptXMLRequest encryptXMLRequest;
        @XmlAttribute
        protected String displayName;
        @XmlAttribute
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;

        /**
         * Gets the value of the encryptCMSRequest property.
         * 
         * @return
         *     possible object is
         *     {@link EncryptCMSRequestType }
         *     
         */
        public EncryptCMSRequestType getEncryptCMSRequest() {
            return encryptCMSRequest;
        }

        /**
         * Sets the value of the encryptCMSRequest property.
         * 
         * @param value
         *     allowed object is
         *     {@link EncryptCMSRequestType }
         *     
         */
        public void setEncryptCMSRequest(EncryptCMSRequestType value) {
            this.encryptCMSRequest = value;
        }

        /**
         * Gets the value of the encryptXMLRequest property.
         * 
         * @return
         *     possible object is
         *     {@link EncryptXMLRequest }
         *     
         */
        public EncryptXMLRequest getEncryptXMLRequest() {
            return encryptXMLRequest;
        }

        /**
         * Sets the value of the encryptXMLRequest property.
         * 
         * @param value
         *     allowed object is
         *     {@link EncryptXMLRequest }
         *     
         */
        public void setEncryptXMLRequest(EncryptXMLRequest value) {
            this.encryptXMLRequest = value;
        }

        /**
         * Gets the value of the displayName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * Sets the value of the displayName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDisplayName(String value) {
            this.displayName = value;
        }

        /**
         * Gets the value of the id property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setId(String value) {
            this.id = value;
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
     *       &lt;choice>
     *         &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}VerifyCMSSignatureRequest"/>
     *         &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}VerifyXMLSignatureRequest"/>
     *       &lt;/choice>
     *       &lt;attribute name="displayName" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "verifyCMSSignatureRequest",
        "verifyXMLSignatureRequest"
    })
    public static class VerifySignatureRequest {

        @XmlElement(name = "VerifyCMSSignatureRequest")
        protected VerifyCMSSignatureRequestType verifyCMSSignatureRequest;
        @XmlElement(name = "VerifyXMLSignatureRequest")
        protected VerifyXMLSignatureRequestType verifyXMLSignatureRequest;
        @XmlAttribute
        protected String displayName;
        @XmlAttribute
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;

        /**
         * Gets the value of the verifyCMSSignatureRequest property.
         * 
         * @return
         *     possible object is
         *     {@link VerifyCMSSignatureRequestType }
         *     
         */
        public VerifyCMSSignatureRequestType getVerifyCMSSignatureRequest() {
            return verifyCMSSignatureRequest;
        }

        /**
         * Sets the value of the verifyCMSSignatureRequest property.
         * 
         * @param value
         *     allowed object is
         *     {@link VerifyCMSSignatureRequestType }
         *     
         */
        public void setVerifyCMSSignatureRequest(VerifyCMSSignatureRequestType value) {
            this.verifyCMSSignatureRequest = value;
        }

        /**
         * Gets the value of the verifyXMLSignatureRequest property.
         * 
         * @return
         *     possible object is
         *     {@link VerifyXMLSignatureRequestType }
         *     
         */
        public VerifyXMLSignatureRequestType getVerifyXMLSignatureRequest() {
            return verifyXMLSignatureRequest;
        }

        /**
         * Sets the value of the verifyXMLSignatureRequest property.
         * 
         * @param value
         *     allowed object is
         *     {@link VerifyXMLSignatureRequestType }
         *     
         */
        public void setVerifyXMLSignatureRequest(VerifyXMLSignatureRequestType value) {
            this.verifyXMLSignatureRequest = value;
        }

        /**
         * Gets the value of the displayName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * Sets the value of the displayName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDisplayName(String value) {
            this.displayName = value;
        }

        /**
         * Gets the value of the id property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setId(String value) {
            this.id = value;
        }

    }

}
