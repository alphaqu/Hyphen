package net.oskarstrom.hyphen;

import net.oskarstrom.hyphen.annotation.DebugOnly;
import net.oskarstrom.hyphen.data.*;
import net.oskarstrom.hyphen.util.Color;

import java.util.Iterator;
import java.util.Map;

@DebugOnly
public class DebugHandler {

	public void printMethods(Map<ClassInfo, SerializerMethod> impl) {
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<ClassInfo, SerializerMethod> classInfoStringEntry : impl.entrySet()) {
			final ClassInfo classInfo = classInfoStringEntry.getKey();
			final SerializerMethod method = classInfoStringEntry.getValue();
			printClass(builder, Color.YELLOW, classInfo);
			builder.append(Color.RED).append("\n | ");
			builder.append(Color.WHITE).append("method => ").append(method.name).append("()\n");
			for (ImplDetails implDetail : method.implDetails) {
				final FieldInfo field = implDetail.field();
				builder.append(Color.RED).append("    - ");
				builder.append(Color.RESET).append(field.getName());
				builder.append(Color.RED).append(" : ");
				String clazzName;
				if (implDetail.def() != null) {
					clazzName = implDetail.def().getString(field, true);
				} else {
					clazzName = field.parseMethodName();
				}
				builder.append(clazzName);
				builder.append('\n');
			}
			builder.append('\n');
		}
		System.out.println(builder);
	}


	private void printTypes(StringBuilder sb, TypeMap mappings) {
		if (mappings != null && mappings.entrySet().size() > 0) {
			sb.append(Color.PURPLE);
			sb.append('<');
			for (Iterator<ClassInfo> iterator = mappings.values().iterator(); iterator.hasNext(); ) {
				ClassInfo clazz = iterator.next();
				printClass(sb, Color.CYAN, clazz);
				if (iterator.hasNext()) {
					sb.append(Color.RESET);
					sb.append(", ");
				}
			}
			sb.append(Color.PURPLE);
			sb.append('>');
		}
	}

	public void printClass(StringBuilder sb, Color color, ClassInfo info) {
		if (info instanceof FieldInfo fieldInfo) {
			printField(sb, color, fieldInfo);
		} else {
			sb.append(color);
			sb.append(info.getClazz().getSimpleName());
			printTypes(sb, info.typeMap);
		}
	}

	private void printField(StringBuilder sb, Color color, FieldInfo info) {
		sb.append(color);
		sb.append(info.getClazz().getSimpleName());
		printTypes(sb, info.typeMap);
		if (info.subclasses != null) {
			for (ClassInfo subclass : info.subclasses) {
				sb.append("\n");
				sb.append(Color.RED);
				sb.append(" | ");
				printClass(sb, Color.BLUE, subclass);

			}
		}
	}

}
