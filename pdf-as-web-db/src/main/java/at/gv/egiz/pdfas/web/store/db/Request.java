package at.gv.egiz.pdfas.web.store.db;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import at.gv.egiz.pdfas.api.ws.PDFASSignRequest;

@Entity
@Table(name = "requests")
public class Request {

	private String uuid;	
	private Date created;
	private PDFASSignRequest signRequest;
	
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(name = "id", unique = true)
	public String getId() {
		return this.uuid;
	}

	public void setId(String uuid) {
		this.uuid = uuid;
	}
	
	@Column(name = "created", nullable = false)
	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
	
	@Column(name = "signRequest", nullable = false, length = 52428800)
	public PDFASSignRequest getSignRequest() {
		return this.signRequest;
	}

	public void setSignRequest(PDFASSignRequest signRequest) {
		this.signRequest = signRequest;
	}
}
