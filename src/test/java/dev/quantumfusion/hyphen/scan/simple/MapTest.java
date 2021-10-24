package dev.quantumfusion.hyphen.scan.simple;


import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Map;

@TestThis
@Data
public class MapTest {
	@Data // FIXME, why is this annotation needed?
	public final Map<String, Integer> data;

	public MapTest(Map<String, Integer> data) {
		this.data = data;
	}
}
