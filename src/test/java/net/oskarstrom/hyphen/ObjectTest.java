package net.oskarstrom.hyphen;

import net.oskarstrom.hyphen.annotation.*;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class ObjectTest {

	@Test
	public void mainTest() {
		SerializerFactory debug = SerializerFactory.createDebug();
		debug.build(BasicScanTest.class);
	}

	@Test
	public void simpleTest() {
		SerializerFactory debug = SerializerFactory.createDebug();
		debug.build(BasicSimpleScanTest.class);
	}

	@TestFactory
	public Stream<DynamicNode> polyTest() {
		return Arrays.stream(PolymorphicGenericTypeTests.class.getDeclaredClasses()).map(clz ->
				DynamicTest.dynamicTest(clz.getSimpleName(), () -> {
					SerializerFactory debug = SerializerFactory.createDebug();
					debug.build(clz);
				})
		);
	}

	public static class BasicSimpleScanTest {

		@Serialize
		public Foo2<Integer> foo2integer;

		public BasicSimpleScanTest(Foo2<Integer> foo2integer) {
			this.foo2integer = foo2integer;
		}
	}

	public static class BasicScanTest {
		@Serialize
		@SerNull
		public int integer;

		@Serialize
		@SerNull
		public TestingObjectScan object;

		@Serialize
		public TestingObjectScan testingDeduplication;

		@Serialize
		public TestingInhiritedField inhiritedField;

		public BasicScanTest(int integer, TestingObjectScan object, TestingObjectScan testingDeduplication, TestingInhiritedField inhiritedField) {
			this.integer = integer;
			this.object = object;
			this.testingDeduplication = testingDeduplication;
			this.inhiritedField = inhiritedField;
		}
	}


	public static class TestingObjectScan {
		@Serialize
		public int something;

		public TestingObjectScan(int something) {
			this.something = something;
		}
	}

	public static class TestingInhiritedField extends ImYoSuper {
		@Serialize
		public int something;

		public TestingInhiritedField(int something, int SUPERFIELD, List<@SerNull Integer> list,TestingInhiritedField field) {
			super(SUPERFIELD, list);
			this.something = something;
		}
	}

	public static class ImYoSuper {
		@Serialize
		public int SUPERFIELD;

		@Serialize
		@SerNull
		@SerSubclasses({LinkedList.class, ArrayList.class})
		public List<@SerNull Integer> list;

		@Serialize
		public TestingInhiritedField field;

		public ImYoSuper(int SUPERFIELD, List<@SerNull Integer> list) {
			this.SUPERFIELD = SUPERFIELD;
			this.list = list;
		}
	}

	public static class Foo<T> {
		@Serialize
		public T t;

		public Foo(T t) {
			this.t = t;
		}
	}

	public static class Foo2<E> extends Foo<E>{
		@Serialize
		public E e;

		public Foo2(E e, E t) {
			super(t);
			this.e = e;
		}
	}

	public static class Pair<A, B>{
		@Serialize
		public A first;

		@Serialize
		public B second;

		public Pair(A a, B b) {
			this.first = a;
			this.second = b;
		}
	}

	public static class SelfPair<T> extends Pair<T,T>{
		public SelfPair(T left, T right) {
			super(left, right);
		}
	}

	public static class Bar<K, T> extends Foo<T> {
		@Serialize
		public K k;

		public Bar(K k, T t) {
			super(t);
			this.k = k;
		}
	}

	public static class Baz<F> extends Bar<String, F> {
		public Baz(String s, F t) {
			super(s, t);
		}
	}

	public static class Obe<T> extends Foo<List<T>> {
		public Obe(List<T> ts) {
			super(ts);
		}
	}

	public static class Caz<K, T extends List<K>> extends Foo<T> {
		@Serialize
		public K kkk;

		public Caz(K kkk, T ks) {
			super(ks);
			this.kkk = kkk;
		}
	}

	public static class PolymorphicGenericTypeTests {
		public static class Simpler {
			@Serialize
			@SerSubclasses({Integer.class, Float.class})
			public Number simpler;

			public Simpler(Number simpler) {
				this.simpler = simpler;
			}
		}

		public static class Simple {
			@Serialize
			@SerSubclasses({Foo.class, Foo2.class})
			public Foo<Integer> simple;

			public Simple(Foo<Integer> simple) {
				this.simple = simple;
			}
		}

		public static class Simplish {
			@Serialize
			@SerSubclasses({Foo.class, Baz.class})
			public Foo<Integer> simplish;

			public Simplish(Foo<Integer> simplish) {
				this.simplish = simplish;
			}
		}

		public static class Complex {
			@Serialize
			@SerSubclasses({Foo.class, Baz.class})
			@SerComplexSubClass(
					value = Bar.class,
					types = {@SerDefined(name = "K", values = {
							String.class, Boolean.class
					})}
			)
			public Foo<Integer> complex;

			public Complex(@SerComplexSubClass(
					value = Bar.class,
					types = {@SerDefined(name = "K", values = {
							String.class, Boolean.class
					})}
			) Foo<Integer> complex) {
				this.complex = complex;
			}
		}

		public static class Errors {
			@Serialize
			@SerSubclasses({Foo.class, Baz.class, Obe.class, Caz.class})
			@SerComplexSubClass(
					value = Bar.class,
					types = {@SerDefined(name = "K", values = {
							String.class, Boolean.class
					})}
			)
			public Foo<Integer> errors;

			public Errors(@SerComplexSubClass(
					value = Bar.class,
					types = {@SerDefined(name = "K", values = {
							String.class, Boolean.class
					})}
			) Foo<Integer> errors) {
				this.errors = errors;
			}
		}

		public static class Errors2 {
			@Serialize
			@SerComplexSubClass(
					value = ArrayList.class,
					types = {@SerDefined(name = "E", values = {
							Integer.class
					})}
			)
			@SerComplexSubClass(
					value = ArrayList.class,
					types = {@SerDefined(name = "E", values = {
							Float.class
					})}
			)
			public List<? extends Number> errors;

			public Errors2(List<? extends Number> errors) {
				this.errors = errors;
			}
		}

		public static class Errors3{
			@Serialize
			@SerSubclasses({Pair.class, SelfPair.class})
			public Pair<Integer, Float> errors3;

			public Errors3(Pair<Integer, Float> errors3) {
				this.errors3 = errors3;
			}
		}

		public static class FooArrayList {
			@Serialize
			@SerSubclasses({Foo.class, Baz.class, Obe.class})
			@SerComplexSubClass(
					value = Bar.class,
					types = {@SerDefined(name = "K", values = {
							String.class, Boolean.class
					})}
			)
			public Foo<ArrayList<Integer>> FooArrayList;

			public FooArrayList(@SerComplexSubClass(
					value = Bar.class,
					types = {@SerDefined(name = "K", values = {
							String.class, Boolean.class
					})}
			) Foo<ArrayList<Integer>> fooArrayList) {
				this.FooArrayList = fooArrayList;
			}
		}

		public static class FooList {
			@Serialize
			@SerSubclasses({Foo.class, Baz.class, Obe.class})
			@SerComplexSubClass(
					value = Bar.class,
					types = {@SerDefined(name = "K", values = {
							String.class, Boolean.class
					})}
			)
			public Foo<@SerSubclasses({ArrayList.class, LinkedList.class}) List<Integer>> FooList;

			public FooList(@SerComplexSubClass(
					value = Bar.class,
					types = {@SerDefined(name = "K", values = {
							String.class, Boolean.class
					})}
			) Foo<@SerSubclasses({ArrayList.class, LinkedList.class}) List<Integer>> fooList) {
				this.FooList = fooList;
			}
		}

		public static class SimpleNumber {
			@Serialize
			@SerSubclasses({Foo.class, Baz.class})
			public Foo<@SerSubclasses({Integer.class, Float.class}) Number> simple_number;

			public SimpleNumber(Foo<@SerSubclasses({Integer.class, Float.class}) Number> simple_number) {
				this.simple_number = simple_number;
			}
		}

		public static class ComplexNumber {
			@Serialize
			@SerSubclasses({Foo.class, Baz.class})
			@SerComplexSubClass(
					value = Bar.class,
					types = {@SerDefined(name = "K", values = {
							String.class, Boolean.class
					})}
			)
			public Foo<@SerSubclasses({Integer.class, Float.class}) Number> complex_number;

			public ComplexNumber(@SerComplexSubClass(
					value = Bar.class,
					types = {@SerDefined(name = "K", values = {
							String.class, Boolean.class
					})}
			) Foo<@SerSubclasses({Integer.class, Float.class}) Number> complex_number) {
				this.complex_number = complex_number;
			}
		}

		public static class ErrorsNumber {

			@Serialize
			@SerSubclasses({Foo.class, Baz.class, Obe.class, Caz.class})
			@SerComplexSubClass(
					value = Bar.class,
					types = {@SerDefined(name = "K", values = {
							String.class, Boolean.class
					})}
			)
			public Foo<@SerSubclasses({Integer.class, Float.class}) Number> errors_number;

			public ErrorsNumber(@SerComplexSubClass(
					value = Bar.class,
					types = {@SerDefined(name = "K", values = {
							String.class, Boolean.class
					})}
			) Foo<@SerSubclasses({Integer.class, Float.class}) Number> errors_number) {
				this.errors_number = errors_number;
			}
		}

		public static class FooArrayListNumber {
			@Serialize
			@SerSubclasses({Foo.class, Baz.class, Obe.class})
			@SerComplexSubClass(
					value = Bar.class,
					types = {@SerDefined(name = "K", values = {
							String.class, Boolean.class
					})}
			)
			public Foo<ArrayList<@SerSubclasses({Integer.class, Float.class}) Number>> FooArrayList_number;

			public FooArrayListNumber(@SerComplexSubClass(
					value = Bar.class,
					types = {@SerDefined(name = "K", values = {
							String.class, Boolean.class
					})}
			) Foo<ArrayList<@SerSubclasses({Integer.class, Float.class}) Number>> fooArrayList_number) {
				this.FooArrayList_number = fooArrayList_number;
			}
		}

		public static class FooListNumber {
			@Serialize
			@SerSubclasses({Foo.class, Baz.class, Obe.class})
			@SerComplexSubClass(
					value = Bar.class,
					types = {@SerDefined(name = "K", values = {
							String.class, Boolean.class
					})}
			)
			public Foo<@SerSubclasses({ArrayList.class, LinkedList.class}) List<
					@SerSubclasses({Integer.class, Float.class}) Number>> FooList_number;

			public FooListNumber(@SerComplexSubClass(
					value = Bar.class,
					types = {@SerDefined(name = "K", values = {
							String.class, Boolean.class
					})}
			) Foo<@SerSubclasses({ArrayList.class, LinkedList.class}) List<@SerSubclasses({Integer.class, Float.class}) Number>> fooList_number) {
				this.FooList_number = fooList_number;
			}
		}

		public static class PartiallyErrorsNumber {
			@Serialize
			@SerSubclasses({Foo.class, Baz.class, Obe.class})
			@SerComplexSubClass(
					value = Bar.class,
					types = {@SerDefined(name = "K", values = {
							String.class, Boolean.class
					})}
			)
			public Foo<
					@SerSubclasses({Integer.class, Float.class})
					@SerComplexSubClass(value = ArrayList.class, types =
					@SerDefined(name = "E", values = {Integer.class, Float.class}))
							Object> partiallyErrors_number;

			public PartiallyErrorsNumber(@SerComplexSubClass(
					value = Bar.class,
					types = {@SerDefined(name = "K", values = {
							String.class, Boolean.class
					})}
			) Foo<@SerSubclasses({Integer.class, Float.class}) @SerComplexSubClass(value = ArrayList.class, types =
			@SerDefined(name = "E", values = {Integer.class, Float.class})) Object> partiallyErrors_number) {
				this.partiallyErrors_number = partiallyErrors_number;
			}
		}

		public static class Extract {


			@Serialize
			@SerSubclasses({Foo.class, Baz.class, Obe.class, Caz.class})
			public Foo<@SerSubclasses({ArrayList.class, LinkedList.class}) List<Integer>> extract;

			public Extract(Foo<@SerSubclasses({ArrayList.class, LinkedList.class}) List<Integer>> extract) {
				this.extract = extract;
			}
		}

		public static class PartiallyErrorsExtractNumber {
			@Serialize
			@SerSubclasses({Foo.class, Baz.class, Obe.class, Caz.class})
			@SerComplexSubClass(
					value = Bar.class,
					types = {@SerDefined(name = "K", values = {
							String.class, Boolean.class
					})}
			)
			public Foo<
					@SerSubclasses({Integer.class, Float.class})
					@SerComplexSubClass(value = ArrayList.class, types =
					@SerDefined(name = "E", values = {Integer.class, Float.class}))
							Object> partiallyErrorsExtract_number;

			public PartiallyErrorsExtractNumber(@SerComplexSubClass(
					value = Bar.class,
					types = {@SerDefined(name = "K", values = {
							String.class, Boolean.class
					})}
			) Foo<@SerSubclasses({Integer.class, Float.class}) @SerComplexSubClass(value = ArrayList.class, types =
			@SerDefined(name = "E", values = {Integer.class, Float.class})) Object> partiallyErrorsExtract_number) {
				this.partiallyErrorsExtract_number = partiallyErrorsExtract_number;
			}
		}

		public static class Arrays{
			@Serialize
			@SerSubclasses({Integer[].class, Float[].class})
			public Number[] numbers;

			public Arrays(@SerSubclasses({Integer[].class, Float[].class}) Number[] numbers) {
				this.numbers = numbers;
			}
		}
	}
}
