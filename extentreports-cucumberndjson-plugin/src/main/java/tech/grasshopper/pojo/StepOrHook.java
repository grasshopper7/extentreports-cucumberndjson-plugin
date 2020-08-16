package tech.grasshopper.pojo;

import java.time.Duration;
import java.util.Date;
import java.util.List;

import io.cucumber.messages.Messages.Pickle.PickleStep;
import lombok.Data;
import tech.grasshopper.pojo.TestStep.ExecutionType;
import tech.grasshopper.processor.DateConverter;

@Data
public abstract class StepOrHook {

	private String result;
	private String message;
	private Duration duration;
	private List<Attachment> attachments;
	
	private int testId;
	private Date executionStartTime;
	private Date executionEndTime;
	
	public static StepOrHook createStepOrHook(PickleStep pickleStep, io.cucumber.messages.Messages.GherkinDocument.Feature.Step messageStep, TestStep testStep) {
		StepOrHook stepOrHook = null;
		if(testStep.getType() == ExecutionType.STEP) {
			stepOrHook = new Step();
			((Step)stepOrHook).setKeyword(messageStep.getKeyword());
			((Step)stepOrHook).setText(pickleStep.getText());
		} else if (testStep.getType() == ExecutionType.HOOK)
			stepOrHook = new Hook();
			
		stepOrHook.setResult(testStep.getTestStepResult().getStatus().toString());
		stepOrHook.setMessage(testStep.getTestStepResult().getMessage());
		stepOrHook.setDuration(DateConverter.parseToDuration(testStep.getTestStepResult().getDuration()));
		
		stepOrHook.setExecutionStartTime(testStep.getTestStepBeginTime());
		stepOrHook.setExecutionEndTime(testStep.getTestStepEndTime());
		stepOrHook.setAttachments(testStep.getAttachments());
		return stepOrHook;
	}
}
