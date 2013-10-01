package at.gv.egiz.pdfas.lib.impl.stamping;

import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;
import at.gv.egiz.pdfas.common.utils.DNUtils;
import at.gv.egiz.pdfas.common.utils.OgnlUtils;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import iaik.x509.X509Certificate;
import ognl.OgnlContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

import java.util.Map;

public class CertificateResolver implements IResolver {

    private static final Logger logger = LoggerFactory.getLogger(CertificateResolver.class);

    private OgnlContext ctx;
    private X509Certificate certificate;

    public CertificateResolver(X509Certificate certificate) {
        this.certificate = certificate;
        this.ctx = new OgnlContext();

        try {
            Map<String, String> issuerDNMap = DNUtils.dnToMap(certificate.getIssuerDN().getName());
            this.ctx.put("issuer", issuerDNMap);
        } catch (InvalidNameException e) {
            logger.error("Failed to build issuer Map", e);
        }

        try {
            Map<String, String> subjectDNMap = DNUtils.dnToMap(certificate.getSubjectDN().getName());
            this.ctx.put("subject", subjectDNMap);
        } catch (InvalidNameException e) {
            logger.error("Failed to build subject Map", e);
        }

    }

    public String resolve(String key, String value, SignatureProfileSettings settings,
    		RequestedSignature signature) {
        return OgnlUtils.resolvsOgnlExpression(value, this.ctx);
    }

}
