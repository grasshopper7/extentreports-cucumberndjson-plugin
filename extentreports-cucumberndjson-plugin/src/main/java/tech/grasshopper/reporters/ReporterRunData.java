package tech.grasshopper.reporters;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;

import lombok.Data;
import tech.grasshopper.pojo.Feature;

@Data
@Singleton
public class ReporterRunData {

	private ReporterDuration duration;
	private Set<Feature> features;
	
	public ReporterRunData() {
		duration = new ReporterDuration();
		features = new HashSet<>();
	}
	
	@Data
	public static class ReporterDuration {
		private Date startTime;
		private Date endTime;
	}
}
