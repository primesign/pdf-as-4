/*******************************************************************************
 * <copyright> Copyright 2014 by E-Government Innovation Center EGIZ, Graz, Austria </copyright>
 * PDF-AS has been contracted by the E-Government Innovation Center EGIZ, a
 * joint initiative of the Federal Chancellery Austria and Graz University of
 * Technology.
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * http://www.osor.eu/eupl/
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * 
 * This product combines work with different licenses. See the "NOTICE" text
 * file for details on the various modules and licenses.
 * The "NOTICE" text file is part of the distribution. Any derivative works
 * that you distribute must include a readable copy of the "NOTICE" text file.
 ******************************************************************************/
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.04.22 at 04:01:10 PM CEST 
//


package at.gv.egiz.sl.schema;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for SignatureInfoCreationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SignatureInfoCreationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SignatureEnvironment" type="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}Base64XMLOptRefContentType"/>
 *         &lt;element name="SignatureLocation">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>token">
 *                 &lt;attribute name="Index" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Supplement" type="{http://www.buergerkarte.at/namespaces/securitylayer/1.2#}DataObjectAssociationType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SignatureInfoCreationType", propOrder = {
    "signatureEnvironment",
    "signatureLocation",
    "supplement"
})
public class SignatureInfoCreationType {

    @XmlElement(name = "SignatureEnvironment", required = true)
    protected Base64XMLOptRefContentType signatureEnvironment;
    @XmlElement(name = "SignatureLocation", required = true)
    protected SignatureInfoCreationType.SignatureLocation signatureLocation;
    @XmlElement(name = "Supplement")
    protected List<DataObjectAssociationType> supplement;

    /**
     * Gets the value of the signatureEnvironment property.
     * 
     * @return
     *     possible object is
     *     {@link Base64XMLOptRefContentType }
     *     
     */
    public Base64XMLOptRefContentType getSignatureEnvironment() {
        return signatureEnvironment;
    }

    /**
     * Sets the value of the signatureEnvironment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Base64XMLOptRefContentType }
     *     
     */
    public void setSignatureEnvironment(Base64XMLOptRefContentType value) {
        this.signatureEnvironment = value;
    }

    /**
     * Gets the value of the signatureLocation property.
     * 
     * @return
     *     possible object is
     *     {@link SignatureInfoCreationType.SignatureLocation }
     *     
     */
    public SignatureInfoCreationType.SignatureLocation getSignatureLocation() {
        return signatureLocation;
    }

    /**
     * Sets the value of the signatureLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureInfoCreationType.SignatureLocation }
     *     
     */
    public void setSignatureLocation(SignatureInfoCreationType.SignatureLocation value) {
        this.signatureLocation = value;
    }

    /**
     * Gets the value of the supplement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supplement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupplement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataObjectAssociationType }
     * 
     * 
     */
    public List<DataObjectAssociationType> getSupplement() {
        if (supplement == null) {
            supplement = new ArrayList<DataObjectAssociationType>();
        }
        return this.supplement;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>token">
     *       &lt;attribute name="Index" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class SignatureLocation {

        @XmlValue
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "token")
        protected String value;
        @XmlAttribute(name = "Index", required = true)
        @XmlSchemaType(name = "nonNegativeInteger")
        protected BigInteger index;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Gets the value of the index property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getIndex() {
            return index;
        }

        /**
         * Sets the value of the index property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setIndex(BigInteger value) {
            this.index = value;
        }

    }

}
