package dev.quantumfusion.hyphen;

public interface HyphenSerializer<IO, D> {
	void encode(IO io, D data);
	D decode(IO io);
	int measure(D data);
}
