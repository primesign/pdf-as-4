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
package at.gv.egiz.pdfas.lib.impl.stamping;

import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;
import at.gv.egiz.pdfas.common.utils.DNUtils;
import at.gv.egiz.pdfas.common.utils.OgnlUtils;
import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;
import iaik.x509.X509Certificate;
import ognl.OgnlContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InvalidNameException;
import java.util.HashMap;
import java.util.Map;

public class CertificateResolver implements IResolver {

    private static final Logger logger = LoggerFactory.getLogger(CertificateResolver.class);

    private OgnlContext ctx;
    private X509Certificate certificate;

    public CertificateResolver(X509Certificate certificate, OperationStatus operationStatus) {
        this.certificate = certificate;
        this.ctx = new OgnlContext();

        this.ctx.put("sn", this.certificate.getSerialNumber().toString());
        
        try {
            Map<String, String> issuerDNMap = DNUtils.dnToMap(certificate.getIssuerDN().getName());
            this.ctx.put("issuer", issuerDNMap);
        } catch (InvalidNameException e) {
            logger.warn("Failed to build issuer Map", e);
        }

        try {
            Map<String, String> subjectDNMap = DNUtils.dnToMap(certificate.getSubjectDN().getName());
            this.ctx.put("subject", subjectDNMap);
        } catch (InvalidNameException e) {
            logger.warn("Failed to build subject Map", e);
        }

        Map<String, String> iuiMap = new HashMap<String, String>();
        try {
        	iuiMap.put("pdfVersion", operationStatus.getPdfObject().getPDFVersion());
        } catch(Throwable e) {
        	logger.warn("Cannot determine pdfVersion: " + e.getMessage());
        }
        this.ctx.put("iui", iuiMap);
    }

    public String resolve(String key, String value, SignatureProfileSettings settings) {
        return OgnlUtils.resolvsOgnlExpression(value, this.ctx);
    }

}
