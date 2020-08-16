package tech.grasshopper.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.cucumber.messages.Messages.GherkinDocument;

import lombok.Data;

@Data
public class Feature {

	private String uri;
	private Location location;
	private String keyword;
	private String name;
	private String description;
	private List<Tag> tags = new ArrayList<>();
	private List<Scenario> scenarios = new ArrayList<>();

	private int testId;
	private Date featureStartTime;
	private Date featureEndTime;

	public static Feature createFeature(GherkinDocument document) {
		Feature feature = new Feature();
		io.cucumber.messages.Messages.GherkinDocument.Feature messageFeature = document.getFeature(); 
		
		feature.setUri(document.getUri());
		feature.setKeyword(messageFeature.getKeyword());
		feature.setName(messageFeature.getName());
		feature.setLocation(Location.createLocation(messageFeature.getLocation()));
		feature.setDescription(messageFeature.getDescription().trim());
		messageFeature.getTagsList().forEach(t -> feature.tags.add(Tag.createTag(t)));
		return feature;
	}

	public void addScenario(Scenario scenario) {
		scenarios.add(scenario);
	}

	public void updateStartEndTimes(Scenario scenario) {
		if(featureStartTime == null || scenario.getScenarioStartTime().compareTo(featureStartTime) < 0)
			featureStartTime = scenario.getScenarioStartTime();
		
		if(featureEndTime == null || scenario.getScenarioEndTime().compareTo(featureEndTime) > 0)
			featureEndTime = scenario.getScenarioEndTime();
	}

	@Override
	public String toString() {
		return "Feature [uri=" + uri + ", location=" + location + ", keyword=" + keyword + ", name=" + name
				+ ", description=" + description + ", tags=" + tags + ", featureStartTime=" + featureStartTime
				+ ", featureEndTime=" + featureEndTime + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Feature other = (Feature) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}
}
