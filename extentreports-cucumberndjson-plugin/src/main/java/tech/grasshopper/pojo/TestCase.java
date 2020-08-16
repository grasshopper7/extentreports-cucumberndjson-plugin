package tech.grasshopper.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class TestCase {

	private io.cucumber.messages.Messages.TestCase testCase;
	private Date testCaseBeginTime;
	private Date testCaseEndTime;
	private List<TestStep> steps = new ArrayList<>();

	public void addTestStep(TestStep testStep) {
		steps.add(testStep);
	}

	public void updateTestStepData() {
		Map<String, List<io.cucumber.messages.Messages.TestCase.TestStep>> idToTestStepMap = testCase.getTestStepsList()
				.stream().collect(Collectors.groupingBy(s -> s.getId()));
		
		steps.forEach( s -> s.setTestStep(idToTestStepMap.get(s.getTestStepId()).get(0)) );
	}	
}
