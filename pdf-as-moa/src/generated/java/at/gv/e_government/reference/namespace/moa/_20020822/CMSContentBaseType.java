
package at.gv.e_government.reference.namespace.moa._20020822;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CMSContentBaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CMSContentBaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://reference.e-government.gv.at/namespace/moa/20020822#}ContentOptionalRefType">
 *       &lt;choice minOccurs="0">
 *         &lt;element name="Base64Content" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CMSContentBaseType")
public class CMSContentBaseType
    extends ContentOptionalRefType
{


}
