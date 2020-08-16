package tech.grasshopper.reporters.aggregates;

import com.aventstack.extentreports.ReportAggregates;
import com.aventstack.extentreports.reporter.AbstractReporter;

import tech.grasshopper.reporters.ReporterRunData;

public class ReportAggregateUpdater extends AbstractReporter {

	public static final String REPORTER_NAME = "dummy";
	private ReporterRunData runData;

	public ReportAggregateUpdater(ReporterRunData runData) {
		this.runData = runData;
	}

	@Override
	public synchronized void flush(ReportAggregates reportAggregates) {	
		reportAggregates.setStartTime(runData.getDuration().getStartTime());
		reportAggregates.setEndTime(runData.getDuration().getEndTime());
		
		(new DurationCalculator(runData.getFeatures(), reportAggregates.getTestList())).updateExtentTestTimeData();;
	}

	@Override
	public String getReporterName() {
		return REPORTER_NAME;
	}

	@Override
	public void start() {
		
	}
}
