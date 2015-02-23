package at.gv.egiz.pdfas.web.stats.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.web.stats.StatisticBackend;
import at.gv.egiz.pdfas.web.stats.StatisticEvent;

public class StatisticFileBackend implements StatisticBackend {

	public static final String NAME = "StatisticFileBackend";

	public static final String STATISTIC_LOGGER = "at.gv.egiz.pdfas.web.statistics";

	private static final Logger technical_logger = LoggerFactory
			.getLogger(StatisticFileBackend.class);
	private static final Logger statistic_logger = LoggerFactory
			.getLogger(STATISTIC_LOGGER);

	private void addCSVValue(String value, StringBuilder sb) {
		if (value != null) {
			value = value.replace(';', ',');
			sb.append(value);
		}
		sb.append(";");
	}

	private String getLogEntry(StatisticEvent statisticEvent) {
		StringBuilder sb = new StringBuilder();
		addCSVValue(String.valueOf(statisticEvent.getTimestamp()), sb);
		addCSVValue(statisticEvent.getOperation().getName(), sb);
		addCSVValue(statisticEvent.getDevice(), sb);
		addCSVValue(statisticEvent.getProfileId(), sb);
		addCSVValue(String.valueOf(statisticEvent.getFilesize()), sb);
		addCSVValue(statisticEvent.getUserAgent(), sb);
		addCSVValue(statisticEvent.getStatus().getName(), sb);
		addCSVValue((statisticEvent.getException() != null) ? statisticEvent
				.getException().getMessage() : null, sb);
		addCSVValue(String.valueOf(statisticEvent.getErrorCode()), sb);
		addCSVValue(String.valueOf(statisticEvent.getDuration()), sb);
		return sb.toString();
	}

	@Override
	public void storeEvent(StatisticEvent statisticEvent) {
		String entry = getLogEntry(statisticEvent);
		technical_logger.trace("Stat log entry: {}", entry);
		statistic_logger.info("{}", entry);
	}

	@Override
	public String getName() {
		return NAME;
	}

}
