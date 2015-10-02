package at.gv.egiz.pdfas.web.store.db;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import at.gv.egiz.pdfas.web.stats.StatisticEvent;

@Entity
@Table(name = "statisticRequest")
public class StatisticRequest {
	private String uuid;
	private Date created;
	private StatisticEvent statisticEvent;

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

	@Column(name = "statisticEvent", nullable = false, length = 52428800)
	public StatisticEvent getStatisticEvent() {
		return this.statisticEvent;
	}

	public void setStatisticEvent(StatisticEvent statisticEvent) {
		this.statisticEvent = statisticEvent;
	}
}
