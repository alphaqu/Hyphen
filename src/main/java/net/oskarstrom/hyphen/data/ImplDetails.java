package net.oskarstrom.hyphen.data;

import net.oskarstrom.hyphen.ObjectSerializationDef;

public record ImplDetails(ObjectSerializationDef def, ClassInfo source, FieldInfo field) {
}
