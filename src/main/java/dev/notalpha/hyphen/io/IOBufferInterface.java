package dev.notalpha.hyphen.io;

import java.nio.*;

public interface IOBufferInterface extends IOInterface {
	void getByteBuffer(ByteBuffer buffer, int length);
	void getCharBuffer(CharBuffer buffer, int length);
	void getShortBuffer(ShortBuffer buffer, int length);
	void getIntBuffer(IntBuffer buffer, int length);
	void getLongBuffer(LongBuffer buffer, int length);
	void getFloatBuffer(FloatBuffer buffer, int length);
	void getDoubleBuffer(DoubleBuffer buffer, int length);

	void putByteBuffer(ByteBuffer buffer, int length);
	void putCharBuffer(CharBuffer buffer, int length);
	void putShortBuffer(ShortBuffer buffer, int length);
	void putIntBuffer(IntBuffer buffer, int length);
	void putLongBuffer(LongBuffer buffer, int length);
	void putFloatBuffer(FloatBuffer buffer, int length);
	void putDoubleBuffer(DoubleBuffer buffer, int length);
}
