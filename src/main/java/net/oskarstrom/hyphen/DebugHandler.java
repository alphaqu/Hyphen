package net.oskarstrom.hyphen;

import net.oskarstrom.hyphen.annotation.DebugOnly;
import net.oskarstrom.hyphen.data.ClassSerializerMetadata;
import net.oskarstrom.hyphen.data.JunctionSerializerMetadata;
import net.oskarstrom.hyphen.data.SerializerMetadata;
import net.oskarstrom.hyphen.data.TypeInfo;
import net.oskarstrom.hyphen.options.AnnotationParser;
import net.oskarstrom.hyphen.util.Color;

import java.util.Map;

@DebugOnly
public class DebugHandler {


	public void printMethods(Map<? extends TypeInfo, ? extends SerializerMetadata> methods) {
		StringBuilder sb = new StringBuilder();
		methods.forEach((typeInfo, serializerMethodMetadata) -> {
			sb.append(Color.YELLOW).append(typeInfo.toFancyString()).append(" ").append(AnnotationParser.toFancyString(typeInfo.annotations)).append('\n');
			if(serializerMethodMetadata instanceof ClassSerializerMetadata classSerializerMetadata){
				this.printClass(sb, classSerializerMetadata);
			} else if(serializerMethodMetadata instanceof JunctionSerializerMetadata junctionSerializerMetadata){
				// this.printJunction(sb, junctionSerializerMetadata);
			}
		});
		System.out.println(sb);
	}

	private void printJunction(StringBuilder sb, TypeInfo typeInfo, JunctionSerializerMetadata junctionSerializerMetadata) {

	}

	public void printClass(StringBuilder sb, ClassSerializerMetadata serializerMetadata){
		serializerMetadata.fields.forEach((field, objectSerializationDef) -> {
			if(field == null){
				sb.append('\t').append(Color.RESET).append("THIS").append(Color.RED).append(" : ").append(objectSerializationDef.toFancyString()).append('\n');
			} else {
				sb.append('\t').append(Color.RESET).append(field.getName()).append(Color.RED).append(" : ").append(objectSerializationDef.toFancyString()).append('\n');
			}
		});
		sb.append('\n');
	}


}
