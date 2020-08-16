package tech.grasshopper.pojo;

import io.cucumber.messages.Messages.Pickle.PickleTag;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Tag {

	private String name;
	
	public static Tag createTag(PickleTag pickleTag) {
		Tag tag = new Tag();
		tag.setName(pickleTag.getName());
		return tag;
	}
	
	public static Tag createTag(io.cucumber.messages.Messages.GherkinDocument.Feature.Tag envelopTag) {
		Tag tag = new Tag();
		tag.setName(envelopTag.getName());
		return tag;
	}
}
