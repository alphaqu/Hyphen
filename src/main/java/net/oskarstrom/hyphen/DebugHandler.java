package net.oskarstrom.hyphen;

import net.oskarstrom.hyphen.annotation.DebugOnly;
import net.oskarstrom.hyphen.data.ClassInfo;
import net.oskarstrom.hyphen.data.SerializerMethodMetadata;
import net.oskarstrom.hyphen.options.AnnotationParser;
import net.oskarstrom.hyphen.util.Color;

import java.util.Map;
import java.util.StringJoiner;

@DebugOnly
public class DebugHandler {


	public void printMethods(Map<ClassInfo, SerializerMethodMetadata> methods) {
		StringBuilder sb = new StringBuilder();
		methods.forEach((classInfo, serializerMethodMetadata) -> {
			sb.append(Color.YELLOW).append(classInfo.toFancyString()).append(" ").append(AnnotationParser.toFancyString(classInfo.annotations)).append('\n');
			serializerMethodMetadata.fields.forEach((field, objectSerializationDef) -> {
				sb.append('\t').append(Color.RESET).append(field.name).append(Color.RED).append(" : ").append(Color.CYAN).append(objectSerializationDef.toString()).append('\n');
			});
			sb.append('\n');
		});
		System.out.println(sb);
	}


}
