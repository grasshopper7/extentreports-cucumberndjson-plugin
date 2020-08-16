package tech.grasshopper.processor;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;

import io.cucumber.messages.Messages.Timestamp;
import io.cucumber.messages.Messages;
import io.cucumber.messages.TimeConversion;

public class DateConverter {

	public static Date parseToDate(String timeStamp) {
		return Date.from(ZonedDateTime.parse(timeStamp).toInstant());
	}
	
	public static Date parseToDate(Timestamp timeStamp) {
		return Date.from(TimeConversion.timestampToJavaInstant(timeStamp));
	}
	
	public static Date parseToDate(ZonedDateTime zonedDateTime) {
		return Date.from(zonedDateTime.toInstant());
	}
	
	public static ZonedDateTime parseToZonedDateTime(String timeStamp) {
		return ZonedDateTime.parse(timeStamp);
	}
	
	public static Duration parseToDuration(Messages.Duration duration) {
		return TimeConversion.durationToJavaDuration(duration);
	}
}
