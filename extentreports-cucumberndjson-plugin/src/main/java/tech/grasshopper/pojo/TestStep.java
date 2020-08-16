package tech.grasshopper.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.cucumber.messages.Messages.TestStepFinished.TestStepResult;
import lombok.Data;

@Data
public class TestStep {
	
	private io.cucumber.messages.Messages.TestCase.TestStep testStep;
	private String testStepId;
	private Date testStepBeginTime;
	private Date testStepEndTime;
	private TestStepResult testStepResult;
	private ExecutionType type;
	private List<Attachment> attachments = new ArrayList<>();
	
	public void addAttachment(Attachment attachment) {
		attachments.add(attachment);
	}
	
	public enum ExecutionType {
		STEP,
		HOOK;
	}
}
