package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.gen.SerializerDef;
import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.io.IOInterface;
import dev.quantumfusion.hyphen.scan.type.Clazz;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * The SerializerHandlers end-user side. Everything here is just for users to easly find methods.
 *
 * @param <IO> The IO class
 * @param <D>  The Data class
 */
public class SerializerFactory<IO extends IOInterface, D> {
	/**
	 * The internal creator.
	 */
	private final SerializerHandler sh;
	private static final AtomicInteger serializerNumberCount = new AtomicInteger(0);

	private SerializerFactory(SerializerHandler serializerHandler) {
		this.sh = serializerHandler;
	}

	// ======================================== CREATE ======================================== //
	/**
	 * The create method. This is where you create the factory that will make the Serializer
	 *
	 * @param dataClass The Data Class you want to serialize.
	 * @param <IO>      IO Class Type
	 * @param <D>       Data Class Type
	 * @return Factory.
	 */
	public static <IO extends IOInterface, D> SerializerFactory<IO, D> create(Class<D> dataClass) {
		return create(ByteBufferIO.class, dataClass);
	}

	/**
	 * The create method. This is where you create the factory that will make the Serializer
	 *
	 * @param ioClass   The IO Class you want to use.
	 * @param dataClass The Data Class you want to serialize
	 * @param <IO>      IO Class Type
	 * @param <D>       Data Class Type
	 * @return Factory.
	 */
	public static <IO extends IOInterface, D> SerializerFactory<IO, D> create(Class<? extends IOInterface> ioClass, Class<D> dataClass) {
		return create(ioClass, dataClass, "Hyphen" + serializerNumberCount.getAndIncrement());
	}

	/**
	 * The create method. This is where you create the factory that will make the Serializer
	 *
	 * @param ioClass        The IO Class you want to use.
	 * @param dataClass      The Data Class you want to serialize
	 * @param serializerName The Class Name of the output serializer.
	 * @param <IO>           IO Class Type
	 * @param <D>            Data Class Type
	 * @return Factory.
	 */
	public static <IO extends IOInterface, D> SerializerFactory<IO, D> create(Class<? extends IOInterface> ioClass, Class<D> dataClass, String serializerName) {
		return new SerializerFactory<IO, D>(new SerializerHandler(ioClass, dataClass, serializerName));
	}

	// ========================================== DEF ========================================== //
	/**
	 * Add a static serializer definitions.
	 *
	 * @param def A serializer definition.
	 */
	public void addSerializerDef(SerializerDef def) {
		this.addDynamicSerializerDef(def.getType(), clazz -> def);
	}

	/**
	 * Adds multiple static serializer definitions.
	 *
	 * @param defs serializer definitions.
	 */
	public void addSerializerDefs(SerializerDef... defs) {
		for (SerializerDef def : defs) this.addSerializerDef(def);
	}

	/**
	 * Adds multiple static serializer definitions through a creator.
	 *
	 * @param defCreator The creator that will make the serializers
	 * @param inputs     The Class Inputs to the creator.
	 */
	public void addSerializerDefs(Function<Class<?>, SerializerDef> defCreator, Class<?>... inputs) {
		for (Class<?> input : inputs) this.addSerializerDef(defCreator.apply(input));
	}

	/**
	 * Adds a dynamic serializer definition.
	 * @param targetClass The Class That you are adding a definition to.
	 * @param dynamicDef The Creator that will make the serializer def at runtume.
	 */
	public void addDynamicSerializerDef(Class<?> targetClass, Function<Clazz, SerializerDef> dynamicDef) {
		this.sh.definitions.put(targetClass, dynamicDef);
	}

	// ========================================= BUILD ========================================= //

	/**
	 * Builds the serializer.
	 * @return A Hyphen Powered Serializer.
	 */
	public HyphenSerializer<IO, D> build() {
		return null;
	}
}
