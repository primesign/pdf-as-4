
package at.gv.e_government.reference.namespace.moa._20020822;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import org.w3._2000._09.xmldsig.KeyInfoType;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the at.gv.e_government.reference.namespace.moa._20020822 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _CreateXMLSignatureResponse_QNAME = new QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "CreateXMLSignatureResponse");
    private final static QName _IssuingCountry_QNAME = new QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "IssuingCountry");
    private final static QName _VerifyCMSSignatureResponse_QNAME = new QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "VerifyCMSSignatureResponse");
    private final static QName _CreateCMSSignatureResponse_QNAME = new QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "CreateCMSSignatureResponse");
    private final static QName _SupplementProfile_QNAME = new QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "SupplementProfile");
    private final static QName _VerifyXMLSignatureRequest_QNAME = new QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "VerifyXMLSignatureRequest");
    private final static QName _PublicAuthority_QNAME = new QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "PublicAuthority");
    private final static QName _ErrorResponse_QNAME = new QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "ErrorResponse");
    private final static QName _VerifyXMLSignatureResponse_QNAME = new QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "VerifyXMLSignatureResponse");
    private final static QName _Supplement_QNAME = new QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "Supplement");
    private final static QName _VerifyCMSSignatureResponseTypeCertificateCheck_QNAME = new QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "CertificateCheck");
    private final static QName _VerifyCMSSignatureResponseTypeSignerInfo_QNAME = new QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "SignerInfo");
    private final static QName _VerifyCMSSignatureResponseTypeSignatureCheck_QNAME = new QName("http://reference.e-government.gv.at/namespace/moa/20020822#", "SignatureCheck");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: at.gv.e_government.reference.namespace.moa._20020822
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CreateXMLSignatureRequestType }
     * 
     */
    public CreateXMLSignatureRequestType createCreateXMLSignatureRequestType() {
        return new CreateXMLSignatureRequestType();
    }

    /**
     * Create an instance of {@link CreateCMSSignatureRequestType }
     * 
     */
    public CreateCMSSignatureRequestType createCreateCMSSignatureRequestType() {
        return new CreateCMSSignatureRequestType();
    }

    /**
     * Create an instance of {@link CMSDataObjectInfoType }
     * 
     */
    public CMSDataObjectInfoType createCMSDataObjectInfoType() {
        return new CMSDataObjectInfoType();
    }

    /**
     * Create an instance of {@link DataObjectInfoType }
     * 
     */
    public DataObjectInfoType createDataObjectInfoType() {
        return new DataObjectInfoType();
    }

    /**
     * Create an instance of {@link VerifyXMLSignatureRequestType }
     * 
     */
    public VerifyXMLSignatureRequestType createVerifyXMLSignatureRequestType() {
        return new VerifyXMLSignatureRequestType();
    }

    /**
     * Create an instance of {@link CreateCMSSignatureRequestType.SingleSignatureInfo }
     * 
     */
    public CreateCMSSignatureRequestType.SingleSignatureInfo createCreateCMSSignatureRequestTypeSingleSignatureInfo() {
        return new CreateCMSSignatureRequestType.SingleSignatureInfo();
    }

    /**
     * Create an instance of {@link CreateXMLSignatureResponseType }
     * 
     */
    public CreateXMLSignatureResponseType createCreateXMLSignatureResponseType() {
        return new CreateXMLSignatureResponseType();
    }

    /**
     * Create an instance of {@link TransformParameterType }
     * 
     */
    public TransformParameterType createTransformParameterType() {
        return new TransformParameterType();
    }

    /**
     * Create an instance of {@link CreateXMLSignatureRequestType.SingleSignatureInfo }
     * 
     */
    public CreateXMLSignatureRequestType.SingleSignatureInfo createCreateXMLSignatureRequestTypeSingleSignatureInfo() {
        return new CreateXMLSignatureRequestType.SingleSignatureInfo();
    }

    /**
     * Create an instance of {@link VerifyXMLSignatureResponseType }
     * 
     */
    public VerifyXMLSignatureResponseType createVerifyXMLSignatureResponseType() {
        return new VerifyXMLSignatureResponseType();
    }

    /**
     * Create an instance of {@link PublicAuthorityType }
     * 
     */
    public PublicAuthorityType createPublicAuthorityType() {
        return new PublicAuthorityType();
    }

    /**
     * Create an instance of {@link VerifyCMSSignatureRequest }
     * 
     */
    public VerifyCMSSignatureRequest createVerifyCMSSignatureRequest() {
        return new VerifyCMSSignatureRequest();
    }

    /**
     * Create an instance of {@link VerifyCMSSignatureRequestType }
     * 
     */
    public VerifyCMSSignatureRequestType createVerifyCMSSignatureRequestType() {
        return new VerifyCMSSignatureRequestType();
    }

    /**
     * Create an instance of {@link CMSDataObjectOptionalMetaType }
     * 
     */
    public CMSDataObjectOptionalMetaType createCMSDataObjectOptionalMetaType() {
        return new CMSDataObjectOptionalMetaType();
    }

    /**
     * Create an instance of {@link XMLDataObjectAssociationType }
     * 
     */
    public XMLDataObjectAssociationType createXMLDataObjectAssociationType() {
        return new XMLDataObjectAssociationType();
    }

    /**
     * Create an instance of {@link QualifiedCertificate }
     * 
     */
    public QualifiedCertificate createQualifiedCertificate() {
        return new QualifiedCertificate();
    }

    /**
     * Create an instance of {@link CreateXMLSignatureRequest }
     * 
     */
    public CreateXMLSignatureRequest createCreateXMLSignatureRequest() {
        return new CreateXMLSignatureRequest();
    }

    /**
     * Create an instance of {@link VerifyTransformsInfoProfile }
     * 
     */
    public VerifyTransformsInfoProfile createVerifyTransformsInfoProfile() {
        return new VerifyTransformsInfoProfile();
    }

    /**
     * Create an instance of {@link CreateCMSSignatureRequest }
     * 
     */
    public CreateCMSSignatureRequest createCreateCMSSignatureRequest() {
        return new CreateCMSSignatureRequest();
    }

    /**
     * Create an instance of {@link ErrorResponseType }
     * 
     */
    public ErrorResponseType createErrorResponseType() {
        return new ErrorResponseType();
    }

    /**
     * Create an instance of {@link CreateSignatureEnvironmentProfile }
     * 
     */
    public CreateSignatureEnvironmentProfile createCreateSignatureEnvironmentProfile() {
        return new CreateSignatureEnvironmentProfile();
    }

    /**
     * Create an instance of {@link CreateSignatureLocationType }
     * 
     */
    public CreateSignatureLocationType createCreateSignatureLocationType() {
        return new CreateSignatureLocationType();
    }

    /**
     * Create an instance of {@link CreateCMSSignatureResponseType }
     * 
     */
    public CreateCMSSignatureResponseType createCreateCMSSignatureResponseType() {
        return new CreateCMSSignatureResponseType();
    }

    /**
     * Create an instance of {@link SecureSignatureCreationDevice }
     * 
     */
    public SecureSignatureCreationDevice createSecureSignatureCreationDevice() {
        return new SecureSignatureCreationDevice();
    }

    /**
     * Create an instance of {@link VerifyCMSSignatureResponseType }
     * 
     */
    public VerifyCMSSignatureResponseType createVerifyCMSSignatureResponseType() {
        return new VerifyCMSSignatureResponseType();
    }

    /**
     * Create an instance of {@link CreateTransformsInfoProfile }
     * 
     */
    public CreateTransformsInfoProfile createCreateTransformsInfoProfile() {
        return new CreateTransformsInfoProfile();
    }

    /**
     * Create an instance of {@link TransformsInfoType }
     * 
     */
    public TransformsInfoType createTransformsInfoType() {
        return new TransformsInfoType();
    }

    /**
     * Create an instance of {@link CMSContentBaseType }
     * 
     */
    public CMSContentBaseType createCMSContentBaseType() {
        return new CMSContentBaseType();
    }

    /**
     * Create an instance of {@link ManifestRefsCheckResultType }
     * 
     */
    public ManifestRefsCheckResultType createManifestRefsCheckResultType() {
        return new ManifestRefsCheckResultType();
    }

    /**
     * Create an instance of {@link InputDataType }
     * 
     */
    public InputDataType createInputDataType() {
        return new InputDataType();
    }

    /**
     * Create an instance of {@link CheckResultType }
     * 
     */
    public CheckResultType createCheckResultType() {
        return new CheckResultType();
    }

    /**
     * Create an instance of {@link ContentBaseType }
     * 
     */
    public ContentBaseType createContentBaseType() {
        return new ContentBaseType();
    }

    /**
     * Create an instance of {@link FinalDataMetaInfoType }
     * 
     */
    public FinalDataMetaInfoType createFinalDataMetaInfoType() {
        return new FinalDataMetaInfoType();
    }

    /**
     * Create an instance of {@link ReferencesCheckResultInfoType }
     * 
     */
    public ReferencesCheckResultInfoType createReferencesCheckResultInfoType() {
        return new ReferencesCheckResultInfoType();
    }

    /**
     * Create an instance of {@link CMSDataObjectRequiredMetaType }
     * 
     */
    public CMSDataObjectRequiredMetaType createCMSDataObjectRequiredMetaType() {
        return new CMSDataObjectRequiredMetaType();
    }

    /**
     * Create an instance of {@link XMLContentType }
     * 
     */
    public XMLContentType createXMLContentType() {
        return new XMLContentType();
    }

    /**
     * Create an instance of {@link MetaInfoType }
     * 
     */
    public MetaInfoType createMetaInfoType() {
        return new MetaInfoType();
    }

    /**
     * Create an instance of {@link ContentExLocRefBaseType }
     * 
     */
    public ContentExLocRefBaseType createContentExLocRefBaseType() {
        return new ContentExLocRefBaseType();
    }

    /**
     * Create an instance of {@link AnyChildrenType }
     * 
     */
    public AnyChildrenType createAnyChildrenType() {
        return new AnyChildrenType();
    }

    /**
     * Create an instance of {@link ReferencesCheckResultType }
     * 
     */
    public ReferencesCheckResultType createReferencesCheckResultType() {
        return new ReferencesCheckResultType();
    }

    /**
     * Create an instance of {@link ManifestRefsCheckResultInfoType }
     * 
     */
    public ManifestRefsCheckResultInfoType createManifestRefsCheckResultInfoType() {
        return new ManifestRefsCheckResultInfoType();
    }

    /**
     * Create an instance of {@link VerifyTransformsDataType }
     * 
     */
    public VerifyTransformsDataType createVerifyTransformsDataType() {
        return new VerifyTransformsDataType();
    }

    /**
     * Create an instance of {@link ContentRequiredRefType }
     * 
     */
    public ContentRequiredRefType createContentRequiredRefType() {
        return new ContentRequiredRefType();
    }

    /**
     * Create an instance of {@link ContentOptionalRefType }
     * 
     */
    public ContentOptionalRefType createContentOptionalRefType() {
        return new ContentOptionalRefType();
    }

    /**
     * Create an instance of {@link CMSDataObjectInfoType.DataObject }
     * 
     */
    public CMSDataObjectInfoType.DataObject createCMSDataObjectInfoTypeDataObject() {
        return new CMSDataObjectInfoType.DataObject();
    }

    /**
     * Create an instance of {@link DataObjectInfoType.DataObject }
     * 
     */
    public DataObjectInfoType.DataObject createDataObjectInfoTypeDataObject() {
        return new DataObjectInfoType.DataObject();
    }

    /**
     * Create an instance of {@link VerifyXMLSignatureRequestType.VerifySignatureInfo }
     * 
     */
    public VerifyXMLSignatureRequestType.VerifySignatureInfo createVerifyXMLSignatureRequestTypeVerifySignatureInfo() {
        return new VerifyXMLSignatureRequestType.VerifySignatureInfo();
    }

    /**
     * Create an instance of {@link VerifyXMLSignatureRequestType.SignatureManifestCheckParams }
     * 
     */
    public VerifyXMLSignatureRequestType.SignatureManifestCheckParams createVerifyXMLSignatureRequestTypeSignatureManifestCheckParams() {
        return new VerifyXMLSignatureRequestType.SignatureManifestCheckParams();
    }

    /**
     * Create an instance of {@link CreateCMSSignatureRequestType.SingleSignatureInfo.DataObjectInfo }
     * 
     */
    public CreateCMSSignatureRequestType.SingleSignatureInfo.DataObjectInfo createCreateCMSSignatureRequestTypeSingleSignatureInfoDataObjectInfo() {
        return new CreateCMSSignatureRequestType.SingleSignatureInfo.DataObjectInfo();
    }

    /**
     * Create an instance of {@link CreateXMLSignatureResponseType.SignatureEnvironment }
     * 
     */
    public CreateXMLSignatureResponseType.SignatureEnvironment createCreateXMLSignatureResponseTypeSignatureEnvironment() {
        return new CreateXMLSignatureResponseType.SignatureEnvironment();
    }

    /**
     * Create an instance of {@link TransformParameterType.Hash }
     * 
     */
    public TransformParameterType.Hash createTransformParameterTypeHash() {
        return new TransformParameterType.Hash();
    }

    /**
     * Create an instance of {@link CreateXMLSignatureRequestType.SingleSignatureInfo.DataObjectInfo }
     * 
     */
    public CreateXMLSignatureRequestType.SingleSignatureInfo.DataObjectInfo createCreateXMLSignatureRequestTypeSingleSignatureInfoDataObjectInfo() {
        return new CreateXMLSignatureRequestType.SingleSignatureInfo.DataObjectInfo();
    }

    /**
     * Create an instance of {@link CreateXMLSignatureRequestType.SingleSignatureInfo.CreateSignatureInfo }
     * 
     */
    public CreateXMLSignatureRequestType.SingleSignatureInfo.CreateSignatureInfo createCreateXMLSignatureRequestTypeSingleSignatureInfoCreateSignatureInfo() {
        return new CreateXMLSignatureRequestType.SingleSignatureInfo.CreateSignatureInfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateXMLSignatureResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://reference.e-government.gv.at/namespace/moa/20020822#", name = "CreateXMLSignatureResponse")
    public JAXBElement<CreateXMLSignatureResponseType> createCreateXMLSignatureResponse(CreateXMLSignatureResponseType value) {
        return new JAXBElement<CreateXMLSignatureResponseType>(_CreateXMLSignatureResponse_QNAME, CreateXMLSignatureResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://reference.e-government.gv.at/namespace/moa/20020822#", name = "IssuingCountry")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createIssuingCountry(String value) {
        return new JAXBElement<String>(_IssuingCountry_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerifyCMSSignatureResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://reference.e-government.gv.at/namespace/moa/20020822#", name = "VerifyCMSSignatureResponse")
    public JAXBElement<VerifyCMSSignatureResponseType> createVerifyCMSSignatureResponse(VerifyCMSSignatureResponseType value) {
        return new JAXBElement<VerifyCMSSignatureResponseType>(_VerifyCMSSignatureResponse_QNAME, VerifyCMSSignatureResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateCMSSignatureResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://reference.e-government.gv.at/namespace/moa/20020822#", name = "CreateCMSSignatureResponse")
    public JAXBElement<CreateCMSSignatureResponseType> createCreateCMSSignatureResponse(CreateCMSSignatureResponseType value) {
        return new JAXBElement<CreateCMSSignatureResponseType>(_CreateCMSSignatureResponse_QNAME, CreateCMSSignatureResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLDataObjectAssociationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://reference.e-government.gv.at/namespace/moa/20020822#", name = "SupplementProfile")
    public JAXBElement<XMLDataObjectAssociationType> createSupplementProfile(XMLDataObjectAssociationType value) {
        return new JAXBElement<XMLDataObjectAssociationType>(_SupplementProfile_QNAME, XMLDataObjectAssociationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerifyXMLSignatureRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://reference.e-government.gv.at/namespace/moa/20020822#", name = "VerifyXMLSignatureRequest")
    public JAXBElement<VerifyXMLSignatureRequestType> createVerifyXMLSignatureRequest(VerifyXMLSignatureRequestType value) {
        return new JAXBElement<VerifyXMLSignatureRequestType>(_VerifyXMLSignatureRequest_QNAME, VerifyXMLSignatureRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PublicAuthorityType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://reference.e-government.gv.at/namespace/moa/20020822#", name = "PublicAuthority")
    public JAXBElement<PublicAuthorityType> createPublicAuthority(PublicAuthorityType value) {
        return new JAXBElement<PublicAuthorityType>(_PublicAuthority_QNAME, PublicAuthorityType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://reference.e-government.gv.at/namespace/moa/20020822#", name = "ErrorResponse")
    public JAXBElement<ErrorResponseType> createErrorResponse(ErrorResponseType value) {
        return new JAXBElement<ErrorResponseType>(_ErrorResponse_QNAME, ErrorResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerifyXMLSignatureResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://reference.e-government.gv.at/namespace/moa/20020822#", name = "VerifyXMLSignatureResponse")
    public JAXBElement<VerifyXMLSignatureResponseType> createVerifyXMLSignatureResponse(VerifyXMLSignatureResponseType value) {
        return new JAXBElement<VerifyXMLSignatureResponseType>(_VerifyXMLSignatureResponse_QNAME, VerifyXMLSignatureResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLDataObjectAssociationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://reference.e-government.gv.at/namespace/moa/20020822#", name = "Supplement")
    public JAXBElement<XMLDataObjectAssociationType> createSupplement(XMLDataObjectAssociationType value) {
        return new JAXBElement<XMLDataObjectAssociationType>(_Supplement_QNAME, XMLDataObjectAssociationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CheckResultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://reference.e-government.gv.at/namespace/moa/20020822#", name = "CertificateCheck", scope = VerifyCMSSignatureResponseType.class)
    public JAXBElement<CheckResultType> createVerifyCMSSignatureResponseTypeCertificateCheck(CheckResultType value) {
        return new JAXBElement<CheckResultType>(_VerifyCMSSignatureResponseTypeCertificateCheck_QNAME, CheckResultType.class, VerifyCMSSignatureResponseType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KeyInfoType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://reference.e-government.gv.at/namespace/moa/20020822#", name = "SignerInfo", scope = VerifyCMSSignatureResponseType.class)
    public JAXBElement<KeyInfoType> createVerifyCMSSignatureResponseTypeSignerInfo(KeyInfoType value) {
        return new JAXBElement<KeyInfoType>(_VerifyCMSSignatureResponseTypeSignerInfo_QNAME, KeyInfoType.class, VerifyCMSSignatureResponseType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CheckResultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://reference.e-government.gv.at/namespace/moa/20020822#", name = "SignatureCheck", scope = VerifyCMSSignatureResponseType.class)
    public JAXBElement<CheckResultType> createVerifyCMSSignatureResponseTypeSignatureCheck(CheckResultType value) {
        return new JAXBElement<CheckResultType>(_VerifyCMSSignatureResponseTypeSignatureCheck_QNAME, CheckResultType.class, VerifyCMSSignatureResponseType.class, value);
    }

}
