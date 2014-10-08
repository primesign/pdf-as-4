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
package at.gv.egiz.pdfas.wrapper;

import java.io.OutputStream;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.api.exceptions.ErrorCode;
import at.gv.egiz.pdfas.api.exceptions.PdfAsException;
import at.gv.egiz.pdfas.api.sign.SignParameters;
import at.gv.egiz.pdfas.lib.api.ByteArrayDataSink;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.moa.MOAConnector;
import at.gv.egiz.pdfas.sigs.pades.PAdESSigner;
import at.gv.egiz.sl.util.BKUSLConnector;

public class SignParameterWrapper {

	private static final Logger logger = LoggerFactory
			.getLogger(SignParameterWrapper.class);

	private SignParameter signParameter4;
	private SignParameters signParameters;
	private ByteArrayDataSink output;

	public SignParameterWrapper(SignParameters signParameters,
			SignParameter signParameter4) {
		this.signParameter4 = signParameter4;
		this.signParameters = signParameters;
	}

	public void syncOldToNew() throws PdfAsException {
		output = new ByteArrayDataSink();
		this.signParameter4.setOutput(output);

		if (this.signParameters.getSignaturePositioning() != null) {
			// Create positioning string
			String posString = this.signParameters.getSignaturePositioning()
					.getPositionString();
			logger.info("Pos String: " + posString);
			if (posString.equals("x:auto;y:auto;w:auto;p:auto;f:0.0")) {
				this.signParameter4.setSignaturePosition(null);
			} else {
				this.signParameter4.setSignaturePosition(posString);
			}
		} else {
			this.signParameter4.setSignaturePosition(null);
		}

		// Select signing device
		if (this.signParameters.getSignatureDevice().equals("moa")) {
			try {
				this.signParameter4
						.setPlainSigner(new PAdESSigner(new MOAConnector(
								this.signParameter4.getConfiguration())));
			} catch (Exception e) {
				throw new PdfAsException(ErrorCode.CERTIFICATE_NOT_FOUND,
						"You need to specify MOA certificate file to use moa (moa.sign.Certificate)");
			}
		} else if (this.signParameters.getSignatureDevice().equals("bku")) {
			this.signParameter4
					.setPlainSigner(new PAdESSigner(new BKUSLConnector(
							this.signParameter4.getConfiguration())));
		} else {
			throw new PdfAsException(ErrorCode.UNSUPPORTED_SIGNATURE,
					"Unsupported device! Use bku or moa!");
		}

		// Overwrite Configurations
		Enumeration<Object> keys = this.signParameters
				.getProfileOverrideProperties().keys();

		while (keys.hasMoreElements()) {
			Object obj = keys.nextElement();
			if (obj != null) {
				String key = obj.toString();
				this.signParameter4.getConfiguration().setValue(
						key,
						this.signParameters.getProfileOverrideProperties()
								.getProperty(key));
			}
		}
	}

	public void syncNewToOld() throws PdfAsException {
		try {
			OutputStream os = this.signParameters.getOutput()
					.createOutputStream("application/pdf");
			os.write(output.getData());
			os.close();
		} catch (Exception e) {
			throw new PdfAsException(ErrorCode.SIGNATURE_COULDNT_BE_CREATED,
					e.getMessage());
		}
	}

	public SignParameter getSignParameter4() {
		return this.signParameter4;
	}

	public SignParameters getSignParameters() {
		return this.signParameters;
	}
}
