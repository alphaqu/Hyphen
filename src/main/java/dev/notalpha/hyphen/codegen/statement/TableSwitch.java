package dev.notalpha.hyphen.codegen.statement;

import dev.notalpha.hyphen.codegen.MethodWriter;
import org.objectweb.asm.Label;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public class TableSwitch implements AutoCloseable {
	protected final MethodWriter mh;
	protected final List<Label> labels = new ArrayList<>();
	protected final Label defaultLabel = new Label();


	public TableSwitch(MethodWriter mh, int min, int max) {
		this.mh = mh;
		for (int i = min; i < max; i++) {
			labels.add(new Label());
		}
		mh.visitTableSwitchInsn(min, max - 1, defaultLabel, labels.toArray(Label[]::new));
	}

	public void labels(IntConsumer action) {
		for (int i = 0; i < labels.size(); i++) {
			mh.visitLabel(labels.get(i));
			action.accept(i);
		}
	}

	public void defaultLabel() {
		mh.visitLabel(defaultLabel);
	}


	@Override
	public void close() {

	}
}
