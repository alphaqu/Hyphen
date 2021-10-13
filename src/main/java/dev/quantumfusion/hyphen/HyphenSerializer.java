package dev.quantumfusion.hyphen;

/**
 * A Serializer made by Hyphen
 * @param <IO> IO Class
 * @param <D> Data Class
 */
public interface HyphenSerializer<IO, D> {
	D get(IO io);
	void put(IO io, D data);
	int measure(D data);
}
