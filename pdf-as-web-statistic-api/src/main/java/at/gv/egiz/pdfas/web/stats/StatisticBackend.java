package at.gv.egiz.pdfas.web.stats;

public interface StatisticBackend {
	public String getName();
	public void storeEvent(StatisticEvent statisticEvent);
}
