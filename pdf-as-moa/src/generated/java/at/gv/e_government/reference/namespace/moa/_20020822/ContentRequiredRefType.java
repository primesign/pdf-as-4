
package at.gv.e_government.reference.namespace.moa._20020822;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContentRequiredRefType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ContentRequiredRefType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://reference.e-government.gv.at/namespace/moa/20020822#}ContentOptionalRefType">
 *       &lt;choice minOccurs="0">
 *         &lt;element name="Base64Content" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="XMLContent" type="{http://reference.e-government.gv.at/namespace/moa/20020822#}XMLContentType"/>
 *         &lt;element name="LocRefContent" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *       &lt;/choice>
 *       &lt;attribute name="Reference" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContentRequiredRefType")
public class ContentRequiredRefType
    extends ContentOptionalRefType
{


}
