package net.oskarstrom.hyphen;

import net.oskarstrom.hyphen.annotation.DebugOnly;
import net.oskarstrom.hyphen.data.ClassInfo;
import net.oskarstrom.hyphen.data.SerializerMethodMetadata;

import java.util.Map;

@DebugOnly
public class DebugHandler {


	public void printMethods(Map<ClassInfo, SerializerMethodMetadata> methods) {
		methods.forEach((classInfo, serializerMethodMetadata) -> {
			System.out.println(classInfo.clazz.getSimpleName());
			serializerMethodMetadata.fields.forEach((field, objectSerializationDef) -> {
				System.out.println("   " + field.getName() + " : " + objectSerializationDef);
			});
			System.out.println();
		});
	}


}
