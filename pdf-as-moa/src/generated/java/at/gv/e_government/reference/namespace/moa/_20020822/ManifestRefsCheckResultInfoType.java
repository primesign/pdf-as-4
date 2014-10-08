
package at.gv.e_government.reference.namespace.moa._20020822;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ManifestRefsCheckResultInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ManifestRefsCheckResultInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://reference.e-government.gv.at/namespace/moa/20020822#}AnyChildrenType">
 *       &lt;sequence>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="FailedReference" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ReferringSigReference" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ManifestRefsCheckResultInfoType")
public class ManifestRefsCheckResultInfoType
    extends AnyChildrenType
{


}
