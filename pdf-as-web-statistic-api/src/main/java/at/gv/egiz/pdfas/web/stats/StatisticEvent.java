package at.gv.egiz.pdfas.web.stats;

import java.util.Date;


/**
 * Timestamp; [Der Zeitpunkt des Signaturvorgangs]
Operation; [Die Operation des Signaturvorgangs (SIGN | VERIFY) ]
Signaturemode; [Der Siganturemode (BINARY | TEXTUAL) default BINARY]
Device; [Das Signaturgeraet (bku (lokale BKU) | moa (configured moa instance) | moc (online bku MOCCA) | mobile (Handy Signatur))]
ProfileId; [Das verwendete Signaturprofil ein Beispiel waere: SIGNATURBLOCK_DE]
Filesize; [Die Dateigroesse des PDF Dokuments]
User Agent; [Der User-Agent (wenn verfuegbar)]
Status; [Der Status der Operation: (OK | ERROR)]
Exception Class; [Exception Klasse falls ein Fehler vorliegt]
ErrorCode; [Exception Code falls ein Fehler vorliegt]
External Errorcode; [Exception Code von externer Componente falls vorhanden]
Duration [Verbrauchte Zeit fuer diese Operation in Millisekunden, wenn feststellbar] 
 * @author Andreas Fitzek
 *
 */
public class StatisticEvent {

	public enum Operation {
		SIGN("sign"),
		VERIFY("verify");
		
		private String name;
		
		private Operation(String name) {
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
	}
	
	public enum Source {
		WEB("web"),
		SOAP("soap"),
		JSON("json");
		
		private String name;
		
		private Source(String name) {
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
	}
	
	public enum Status {
		OK("ok"), 
		ERROR("error");
		
		private String name;
		
		private Status(String name) {
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
	}
	
	private long timestamp;
	private Operation operation;
	private String device;
	private String profileId;
	private long filesize;
	private String userAgent;
	private Status status;
	private Throwable exception;
	private long errorCode;
	private long start;
	private long end;
	private Source source;
	private boolean logged = false;
	
	public StatisticEvent() {
		
	}
	
	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}
	
	public void setStartNow() {
		this.start = (new Date()).getTime();
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}
	
	public void setEndNow() {
		this.end = (new Date()).getTime();
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public void setTimestampNow() {
		this.timestamp = (new Date()).getTime();
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public long getFilesize() {
		return filesize;
	}

	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}

	public long getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(long errorCode) {
		this.errorCode = errorCode;
	}

	public long getDuration() {
		return this.end - this.start;
	}

	public boolean isLogged() {
		return logged;
	}

	public void setLogged(boolean logged) {
		this.logged = logged;
	}
}
