package tech.grasshopper.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Step extends StepOrHook{

	private String keyword;
	private String text;
}
