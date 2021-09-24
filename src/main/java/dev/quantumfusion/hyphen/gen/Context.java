package dev.quantumfusion.hyphen.gen;

import org.objectweb.asm.Type;

public record Context(IOMode mode, VarHandler var, Type serializer, Runnable data, Runnable io) {
}
