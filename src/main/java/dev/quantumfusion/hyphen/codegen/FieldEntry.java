package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.info.TypeInfo;

public record FieldEntry(TypeInfo clazz, int modifier, String name) {
}
