package tech.grasshopper.processor.message;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;

import io.cucumber.messages.Messages.Attachment.ContentEncoding;
import net.iharder.Base64;
import tech.grasshopper.logging.ExtentReportsCucumberLogger;
import tech.grasshopper.pojo.Attachment;
import tech.grasshopper.pojo.Attachment.AttachmentType;
import tech.grasshopper.properties.ReportProperties;

@Singleton
public class AttachmentProcessor {

	private ReportProperties reportProperties;
	private ExtentReportsCucumberLogger logger;

	private static final AtomicInteger ATTACHMENT_INDEX = new AtomicInteger(0);

	@SuppressWarnings("serial")
	private static final Map<String, String> MEDIA_TYPES_EXTENSIONS = new HashMap<String, String>() {
		{
			put("image/bmp", "bmp");
			put("image/gif", "gif");
			put("image/jpeg", "jpg");
			put("image/png", "png");
			put("image/svg+xml", "svg");
			put("video/ogg", "ogg");
		}
	};

	@Inject
	public AttachmentProcessor(ReportProperties reportProperties, ExtentReportsCucumberLogger logger) {
		this.reportProperties = reportProperties;
		this.logger = logger;
	}

	public Attachment process(io.cucumber.messages.Messages.Attachment attachment) {
		Attachment attach = new Attachment();
		if (attachment.getContentEncoding() == ContentEncoding.IDENTITY) {
			attach.setBody(attachment.getBody());
			attach.setType(AttachmentType.TEXT);
		} 
		else if (attachment.getContentEncoding() == ContentEncoding.BASE64) {
			String mediaType = attachment.getMediaType();
			String extension = MEDIA_TYPES_EXTENSIONS.get(mediaType);

			if (extension != null) {
				attachment.getContentEncoding();
				Path path = createAttachmentFile(extension);
				try {
					Files.write(path, Base64.decode(attachment.getBody()));
					attach.setFilePath(path.toString());
					attach.setType(AttachmentType.MEDIA);
				} catch (IOException e) {
					logger.warn("Skipping attachment creation at location - " + path.toString()
							+ ", due to error in creating file.");
				}
			} else {
				logger.warn("Media type '" + mediaType + "' is currently not supported.");
			}
		} 
		else {
			logger.warn("Skipping attachment as encoding not recognized.");
		}
		return attach;
	}

	private Path createAttachmentFile(String extension) {
		StringBuilder fileName = new StringBuilder("attachment").append(ATTACHMENT_INDEX.incrementAndGet()).append(".")
				.append(extension);
		String attachDirPath = reportProperties.getReportScreenshotLocation();

		File dir = new File(attachDirPath);
		// Create directory if not existing
		if (!dir.exists())
			dir.mkdirs();

		File file = new File(attachDirPath + "/" + fileName);
		Path path = Paths.get(file.getAbsolutePath());
		// Delete existing embedded stuff
		if (file.exists())
			file.delete();
		return path;
	}

	public void updateExtentTestWithAttachment(ExtentTest test, List<Attachment> attachments) {
		for (Attachment attachment : attachments) {
			if (attachment.getType() == AttachmentType.TEXT) {
				test.info(attachment.getBody());
			} else if (attachment.getType() == AttachmentType.MEDIA) {
				// TODO:
				// String name = embed.getName() == null ? "" : embed.getName();
				String name = "";
				String filePath = attachment.getFilePath();

				if (filePath == null || filePath.isEmpty()) {
					logger.warn("Skipping adding embedded file as filepath is empty for step - '"
							+ test.getModel().getName() + "'.");
					return;
				}
				try {
					test.info(name, MediaEntityBuilder.createScreenCaptureFromPath(filePath).build());
					// Embedding workaround for html report.
					test.addScreenCaptureFromPath(filePath);
				} catch (Exception e) {
					logger.warn("Skipping adding embedded file for step - '" + test.getModel().getName()
							+ "' as error in processing.");
					return;
				}
			}
		}
	}
}
