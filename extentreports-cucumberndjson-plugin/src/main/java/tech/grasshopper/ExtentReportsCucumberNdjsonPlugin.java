package tech.grasshopper;

import javax.inject.Inject;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import tech.grasshopper.json.NdjsonFileConverter;
import tech.grasshopper.json.NdjsonPathCollector;
import tech.grasshopper.logging.ExtentReportsCucumberLogger;
import tech.grasshopper.properties.ReportProperties;
import tech.grasshopper.reporters.ReporterInitializer;
import tech.grasshopper.reporters.ReporterRunData;
import tech.grasshopper.test.ExtentTestManager;

@Mojo(name = "extentreportndjson")
public class ExtentReportsCucumberNdjsonPlugin extends AbstractMojo {

	/*
	 * @Parameter(property = "extentreport.cucumberNdjsonReportDirectory", required
	 * = true) private String cucumberNdJsonReportDirectory;
	 */

	@Parameter(property = "extentreport.extentPropertiesDirectory", defaultValue = "")
	private String extentPropertiesDirectory;

	private NdjsonPathCollector ndjsonPathCollector;
	private NdjsonFileConverter ndjsonFileConverter;
	private ReportProperties reportProperties;
	private ReporterInitializer reportInitializer;
	private ExtentTestManager extentTestManager;
	private ExtentReportsCucumberLogger logger;

	@Inject
	public ExtentReportsCucumberNdjsonPlugin(NdjsonPathCollector ndjsonPathCollector,
			NdjsonFileConverter ndjsonFileConverter, ReportProperties reportProperties,
			ReporterInitializer reportInitializer, ExtentTestManager extentTestManager,
			ExtentReportsCucumberLogger logger) {
		this.ndjsonPathCollector = ndjsonPathCollector;
		this.ndjsonFileConverter = ndjsonFileConverter;
		this.reportProperties = reportProperties;
		this.reportInitializer = reportInitializer;
		this.extentTestManager = extentTestManager;
		this.logger = logger;
	}

	@Override
	public void execute() {
		try {
			logger.initializeLogger(getLog());
			logger.info("STARTED EXTENT REPORT GENERATION PLUGIN");

			// List<Path> jsonFilePaths =
			// jsonPathCollector.retrieveFilePaths(cucumberJsonReportDirectory);

			ndjsonFileConverter.retrieveFeaturesFromReport();

			reportProperties.loadPropertyFiles(extentPropertiesDirectory);

			reportInitializer.instantiate();

			extentTestManager.initialize();
			extentTestManager.flushToReporters();

			logger.info("FINISHED EXTENT REPORT GENERATION PLUGIN");
		} catch (Throwable t) {
			// Report will not result in build failure.
			t.printStackTrace();
			logger.error(String.format("STOPPING EXTENT REPORT GENERATION - %s", t.getMessage()));
		}
	}
}
