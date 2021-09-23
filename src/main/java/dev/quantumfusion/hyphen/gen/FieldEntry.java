package dev.quantumfusion.hyphen.gen;

import dev.quantumfusion.hyphen.info.TypeInfo;

public record FieldEntry(TypeInfo clazz, int modifier, String name) {
}
