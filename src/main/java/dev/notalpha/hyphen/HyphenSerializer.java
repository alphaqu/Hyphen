package dev.notalpha.hyphen;

import dev.notalpha.hyphen.io.IOInterface;

/**
 * A Serializer made by Hyphen
 *
 * @param <IO> IO Class
 * @param <D>  Data Class
 */
public interface HyphenSerializer<IO extends IOInterface, D> {
	D get(IO io);

	void put(IO io, D data);

	long measure(D data);
}
