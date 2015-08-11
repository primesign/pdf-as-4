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
 * <p>Java class for BulkResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BulkResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="CreateSignatureResponse" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}CreateCMSSignatureResponse"/>
 *                   &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}CreateXMLSignatureResponse"/>
 *                   &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}ErrorResponse"/>
 *                 &lt;/choice>
 *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="VerifySignatureResponse" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}VerifyCMSSignatureResponse"/>
 *                   &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}VerifyXMLSignatureResponse"/>
 *                   &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}ErrorResponse"/>
 *                 &lt;/choice>
 *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="EncryptResponse" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}EncryptCMSResponse"/>
 *                   &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}EncryptXMLResponse"/>
 *                   &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}ErrorResponse"/>
 *                 &lt;/choice>
 *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DecryptResponse" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}DecryptCMSResponse"/>
 *                   &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}DecryptXMLResponse"/>
 *                   &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}ErrorResponse"/>
 *                 &lt;/choice>
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
@XmlType(name = "BulkResponseType", propOrder = {
    "createSignatureResponse",
    "verifySignatureResponse",
    "encryptResponse",
    "decryptResponse"
})
public class BulkResponseType {

    @XmlElement(name = "CreateSignatureResponse")
    protected List<BulkResponseType.CreateSignatureResponse> createSignatureResponse;
    @XmlElement(name = "VerifySignatureResponse")
    protected List<BulkResponseType.VerifySignatureResponse> verifySignatureResponse;
    @XmlElement(name = "EncryptResponse")
    protected List<BulkResponseType.EncryptResponse> encryptResponse;
    @XmlElement(name = "DecryptResponse")
    protected List<BulkResponseType.DecryptResponse> decryptResponse;

    /**
     * Gets the value of the createSignatureResponse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the createSignatureResponse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCreateSignatureResponse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BulkResponseType.CreateSignatureResponse }
     * 
     * 
     */
    public List<BulkResponseType.CreateSignatureResponse> getCreateSignatureResponse() {
        if (createSignatureResponse == null) {
            createSignatureResponse = new ArrayList<BulkResponseType.CreateSignatureResponse>();
        }
        return this.createSignatureResponse;
    }

    /**
     * Gets the value of the verifySignatureResponse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the verifySignatureResponse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVerifySignatureResponse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BulkResponseType.VerifySignatureResponse }
     * 
     * 
     */
    public List<BulkResponseType.VerifySignatureResponse> getVerifySignatureResponse() {
        if (verifySignatureResponse == null) {
            verifySignatureResponse = new ArrayList<BulkResponseType.VerifySignatureResponse>();
        }
        return this.verifySignatureResponse;
    }

    /**
     * Gets the value of the encryptResponse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the encryptResponse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEncryptResponse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BulkResponseType.EncryptResponse }
     * 
     * 
     */
    public List<BulkResponseType.EncryptResponse> getEncryptResponse() {
        if (encryptResponse == null) {
            encryptResponse = new ArrayList<BulkResponseType.EncryptResponse>();
        }
        return this.encryptResponse;
    }

    /**
     * Gets the value of the decryptResponse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the decryptResponse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDecryptResponse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BulkResponseType.DecryptResponse }
     * 
     * 
     */
    public List<BulkResponseType.DecryptResponse> getDecryptResponse() {
        if (decryptResponse == null) {
            decryptResponse = new ArrayList<BulkResponseType.DecryptResponse>();
        }
        return this.decryptResponse;
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
     *         &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}CreateCMSSignatureResponse"/>
     *         &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}CreateXMLSignatureResponse"/>
     *         &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}ErrorResponse"/>
     *       &lt;/choice>
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
        "createCMSSignatureResponse",
        "createXMLSignatureResponse",
        "errorResponse"
    })
    public static class CreateSignatureResponse {

        @XmlElement(name = "CreateCMSSignatureResponse")
        protected CreateCMSSignatureResponseType createCMSSignatureResponse;
        @XmlElement(name = "CreateXMLSignatureResponse")
        protected CreateXMLSignatureResponseType createXMLSignatureResponse;
        @XmlElement(name = "ErrorResponse")
        protected ErrorResponseType errorResponse;
        @XmlAttribute
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;

        /**
         * Gets the value of the createCMSSignatureResponse property.
         * 
         * @return
         *     possible object is
         *     {@link CreateCMSSignatureResponseType }
         *     
         */
        public CreateCMSSignatureResponseType getCreateCMSSignatureResponse() {
            return createCMSSignatureResponse;
        }

        /**
         * Sets the value of the createCMSSignatureResponse property.
         * 
         * @param value
         *     allowed object is
         *     {@link CreateCMSSignatureResponseType }
         *     
         */
        public void setCreateCMSSignatureResponse(CreateCMSSignatureResponseType value) {
            this.createCMSSignatureResponse = value;
        }

        /**
         * Gets the value of the createXMLSignatureResponse property.
         * 
         * @return
         *     possible object is
         *     {@link CreateXMLSignatureResponseType }
         *     
         */
        public CreateXMLSignatureResponseType getCreateXMLSignatureResponse() {
            return createXMLSignatureResponse;
        }

        /**
         * Sets the value of the createXMLSignatureResponse property.
         * 
         * @param value
         *     allowed object is
         *     {@link CreateXMLSignatureResponseType }
         *     
         */
        public void setCreateXMLSignatureResponse(CreateXMLSignatureResponseType value) {
            this.createXMLSignatureResponse = value;
        }

        /**
         * Gets the value of the errorResponse property.
         * 
         * @return
         *     possible object is
         *     {@link ErrorResponseType }
         *     
         */
        public ErrorResponseType getErrorResponse() {
            return errorResponse;
        }

        /**
         * Sets the value of the errorResponse property.
         * 
         * @param value
         *     allowed object is
         *     {@link ErrorResponseType }
         *     
         */
        public void setErrorResponse(ErrorResponseType value) {
            this.errorResponse = value;
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
     *         &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}DecryptCMSResponse"/>
     *         &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}DecryptXMLResponse"/>
     *         &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}ErrorResponse"/>
     *       &lt;/choice>
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
        "decryptCMSResponse",
        "decryptXMLResponse",
        "errorResponse"
    })
    public static class DecryptResponse {

        @XmlElement(name = "DecryptCMSResponse")
        protected DecryptCMSResponseType decryptCMSResponse;
        @XmlElement(name = "DecryptXMLResponse")
        protected DecryptXMLResponseType decryptXMLResponse;
        @XmlElement(name = "ErrorResponse")
        protected ErrorResponseType errorResponse;
        @XmlAttribute
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;

        /**
         * Gets the value of the decryptCMSResponse property.
         * 
         * @return
         *     possible object is
         *     {@link DecryptCMSResponseType }
         *     
         */
        public DecryptCMSResponseType getDecryptCMSResponse() {
            return decryptCMSResponse;
        }

        /**
         * Sets the value of the decryptCMSResponse property.
         * 
         * @param value
         *     allowed object is
         *     {@link DecryptCMSResponseType }
         *     
         */
        public void setDecryptCMSResponse(DecryptCMSResponseType value) {
            this.decryptCMSResponse = value;
        }

        /**
         * Gets the value of the decryptXMLResponse property.
         * 
         * @return
         *     possible object is
         *     {@link DecryptXMLResponseType }
         *     
         */
        public DecryptXMLResponseType getDecryptXMLResponse() {
            return decryptXMLResponse;
        }

        /**
         * Sets the value of the decryptXMLResponse property.
         * 
         * @param value
         *     allowed object is
         *     {@link DecryptXMLResponseType }
         *     
         */
        public void setDecryptXMLResponse(DecryptXMLResponseType value) {
            this.decryptXMLResponse = value;
        }

        /**
         * Gets the value of the errorResponse property.
         * 
         * @return
         *     possible object is
         *     {@link ErrorResponseType }
         *     
         */
        public ErrorResponseType getErrorResponse() {
            return errorResponse;
        }

        /**
         * Sets the value of the errorResponse property.
         * 
         * @param value
         *     allowed object is
         *     {@link ErrorResponseType }
         *     
         */
        public void setErrorResponse(ErrorResponseType value) {
            this.errorResponse = value;
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
     *         &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}EncryptCMSResponse"/>
     *         &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}EncryptXMLResponse"/>
     *         &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}ErrorResponse"/>
     *       &lt;/choice>
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
        "encryptCMSResponse",
        "encryptXMLResponse",
        "errorResponse"
    })
    public static class EncryptResponse {

        @XmlElement(name = "EncryptCMSResponse")
        protected EncryptCMSResponseType encryptCMSResponse;
        @XmlElement(name = "EncryptXMLResponse")
        protected EncryptXMLResponseType encryptXMLResponse;
        @XmlElement(name = "ErrorResponse")
        protected ErrorResponseType errorResponse;
        @XmlAttribute
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;

        /**
         * Gets the value of the encryptCMSResponse property.
         * 
         * @return
         *     possible object is
         *     {@link EncryptCMSResponseType }
         *     
         */
        public EncryptCMSResponseType getEncryptCMSResponse() {
            return encryptCMSResponse;
        }

        /**
         * Sets the value of the encryptCMSResponse property.
         * 
         * @param value
         *     allowed object is
         *     {@link EncryptCMSResponseType }
         *     
         */
        public void setEncryptCMSResponse(EncryptCMSResponseType value) {
            this.encryptCMSResponse = value;
        }

        /**
         * Gets the value of the encryptXMLResponse property.
         * 
         * @return
         *     possible object is
         *     {@link EncryptXMLResponseType }
         *     
         */
        public EncryptXMLResponseType getEncryptXMLResponse() {
            return encryptXMLResponse;
        }

        /**
         * Sets the value of the encryptXMLResponse property.
         * 
         * @param value
         *     allowed object is
         *     {@link EncryptXMLResponseType }
         *     
         */
        public void setEncryptXMLResponse(EncryptXMLResponseType value) {
            this.encryptXMLResponse = value;
        }

        /**
         * Gets the value of the errorResponse property.
         * 
         * @return
         *     possible object is
         *     {@link ErrorResponseType }
         *     
         */
        public ErrorResponseType getErrorResponse() {
            return errorResponse;
        }

        /**
         * Sets the value of the errorResponse property.
         * 
         * @param value
         *     allowed object is
         *     {@link ErrorResponseType }
         *     
         */
        public void setErrorResponse(ErrorResponseType value) {
            this.errorResponse = value;
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
     *         &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}VerifyCMSSignatureResponse"/>
     *         &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}VerifyXMLSignatureResponse"/>
     *         &lt;element ref="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}ErrorResponse"/>
     *       &lt;/choice>
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
        "verifyCMSSignatureResponse",
        "verifyXMLSignatureResponse",
        "errorResponse"
    })
    public static class VerifySignatureResponse {

        @XmlElement(name = "VerifyCMSSignatureResponse")
        protected VerifyCMSSignatureResponseType verifyCMSSignatureResponse;
        @XmlElement(name = "VerifyXMLSignatureResponse")
        protected VerifyXMLSignatureResponseType verifyXMLSignatureResponse;
        @XmlElement(name = "ErrorResponse")
        protected ErrorResponseType errorResponse;
        @XmlAttribute
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;

        /**
         * Gets the value of the verifyCMSSignatureResponse property.
         * 
         * @return
         *     possible object is
         *     {@link VerifyCMSSignatureResponseType }
         *     
         */
        public VerifyCMSSignatureResponseType getVerifyCMSSignatureResponse() {
            return verifyCMSSignatureResponse;
        }

        /**
         * Sets the value of the verifyCMSSignatureResponse property.
         * 
         * @param value
         *     allowed object is
         *     {@link VerifyCMSSignatureResponseType }
         *     
         */
        public void setVerifyCMSSignatureResponse(VerifyCMSSignatureResponseType value) {
            this.verifyCMSSignatureResponse = value;
        }

        /**
         * Gets the value of the verifyXMLSignatureResponse property.
         * 
         * @return
         *     possible object is
         *     {@link VerifyXMLSignatureResponseType }
         *     
         */
        public VerifyXMLSignatureResponseType getVerifyXMLSignatureResponse() {
            return verifyXMLSignatureResponse;
        }

        /**
         * Sets the value of the verifyXMLSignatureResponse property.
         * 
         * @param value
         *     allowed object is
         *     {@link VerifyXMLSignatureResponseType }
         *     
         */
        public void setVerifyXMLSignatureResponse(VerifyXMLSignatureResponseType value) {
            this.verifyXMLSignatureResponse = value;
        }

        /**
         * Gets the value of the errorResponse property.
         * 
         * @return
         *     possible object is
         *     {@link ErrorResponseType }
         *     
         */
        public ErrorResponseType getErrorResponse() {
            return errorResponse;
        }

        /**
         * Sets the value of the errorResponse property.
         * 
         * @param value
         *     allowed object is
         *     {@link ErrorResponseType }
         *     
         */
        public void setErrorResponse(ErrorResponseType value) {
            this.errorResponse = value;
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
