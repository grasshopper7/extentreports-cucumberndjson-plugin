package tech.grasshopper.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Location {

	private int line;
	private int column;
	
	public static Location createLocation(io.cucumber.messages.Messages.Location envelopLocation) {
		return new Location(envelopLocation.getLine(), envelopLocation.getColumn());
	}
}
