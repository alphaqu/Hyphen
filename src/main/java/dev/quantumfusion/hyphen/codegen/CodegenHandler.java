package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.HyphenSerializer;
import dev.quantumfusion.hyphen.Options;
import dev.quantumfusion.hyphen.io.IOInterface;
import dev.quantumfusion.hyphen.util.GenUtil;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CodegenHandler<IO extends IOInterface, D> {
	// Settings
	public final Class<IO> ioClass;
	public final Class<D> dataClass;
	private final boolean debug;

	// Options
	private final EnumMap<Options, Boolean> options;

	// Method Dedup
	@Nullable
	private final Map<MethodInfo, AtomicInteger> methodDedup;

	public CodegenHandler(Class<IO> ioClass, Class<D> dataClass, boolean debug, EnumMap<Options, Boolean> options) {
		this.ioClass = ioClass;
		this.dataClass = dataClass;
		this.debug = debug;
		this.options = options;
		this.methodDedup = this.options.get(Options.SHORT_METHOD_NAMES) ? new HashMap<>() : null;
	}

	public MethodInfo apply(MethodInfo info) {
		if (methodDedup != null)
			info.setName(GenUtil.hyphenShortMethodName(methodDedup.computeIfAbsent(info, info1 -> new AtomicInteger(0)).getAndIncrement()), this);
		return info;
	}

	public HyphenSerializer<IO, D> build() {
		return null;
	}
}
