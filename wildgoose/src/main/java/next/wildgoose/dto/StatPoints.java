package next.wildgoose.dto;

import java.util.HashMap;
import java.util.Map;

public class StatPoints {
	Map<String, Integer> stat = new HashMap<String, Integer>();

	public void setStat(String statName, int stat) {
		this.stat.put(statName, stat);
	}
	public Map getStat() {
		return stat;
	}
}
