package dev.quantumfusion.hyphen;

public interface HyphenSerializer<D, IO> {
	D get(IO io);

	void put(IO io, D data);

	long measure(D data);
}
