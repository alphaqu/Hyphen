package dev.quantumfusion.hyphen.sped;

import dev.quantumfusion.hyphen.scan.Clazzifier;
import dev.quantumfusion.hyphen.scan.Direction;
import dev.quantumfusion.hyphen.scan.FieldEntry;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.poly.classes.*;
import dev.quantumfusion.hyphen.scan.type.Clazz;

import java.util.List;

public class TestThings {
	@Data
	public CoWrappedC1<String, C2<String>> things;
	@Data
	public C1<Integer> thigns2;
	@Data
	public C1<C2<Object>> thing3;


}
