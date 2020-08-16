package tech.grasshopper.test;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ConfigurableReporter;

import tech.grasshopper.reporters.ReporterInitializer;
import tech.grasshopper.reporters.ReporterRunData;
import tech.grasshopper.test.heirarchy.DefaultExtentTestHeirarchy;

@Singleton
public class ExtentTestManager {

	private ExtentReports extent;
	private ReporterInitializer reportInitializer;
	private DefaultExtentTestHeirarchy testHeirarchy;
	private ReporterRunData runData;

	@Inject
	public ExtentTestManager(ReporterInitializer reportInitializer, DefaultExtentTestHeirarchy testHeirarchy, ReporterRunData runData) {
		this.extent = new ExtentReports();
		this.reportInitializer = reportInitializer;
		this.testHeirarchy = testHeirarchy;
		this.runData = runData;
	}

	public void initialize() {
		Map<String, ConfigurableReporter> reporters = reportInitializer.getReportKeyToInstance();
		extent.setReportUsesManualConfiguration(true);
		testHeirarchy.createTestHeirarchy(runData.getFeatures(), extent);

		extent.attachReporter(reportInitializer.instantiatReportAggregateUpdater());
		
		for (String key : reporters.keySet()) {
			ConfigurableReporter reporter = reporters.get(key);
			extent.attachReporter(reporter);
		}	
	}

	public void flushToReporters() {
		extent.flush();
	}
}
