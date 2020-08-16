package tech.grasshopper.processor.message;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.cucumber.messages.Messages.TestRunFinished;
import io.cucumber.messages.Messages.TestRunStarted;
import lombok.Data;
import tech.grasshopper.processor.DateConverter;
import tech.grasshopper.reporters.ReporterRunData;

@Singleton
@Data
public class TestRunMessageProcessor {

	private ReporterRunData runData;
	
	@Inject
	public TestRunMessageProcessor(ReporterRunData runData) {
		this.runData = runData;
	}

	public void processRunBeginTime(TestRunStarted testRunStarted) {
		runData.getDuration().setStartTime(DateConverter.parseToDate(testRunStarted.getTimestamp()));
	}

	public void processRunEndTime(TestRunFinished testRunFinished) {
		runData.getDuration().setEndTime(DateConverter.parseToDate(testRunFinished.getTimestamp()));
	}
}
