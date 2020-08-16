package tech.grasshopper.pojo;

import lombok.Data;

@Data
public class Attachment {

	private String body;
	
	private String filePath;
	private AttachmentType type;
	
	public enum AttachmentType {
		MEDIA,
		TEXT;
	}
}
