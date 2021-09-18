package net.oskarstrom.hyphen;

import net.oskarstrom.hyphen.annotation.*;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.net.URI;
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
				DynamicTest.dynamicTest(clz.getSimpleName(), URI.create("class:" + clz.getName()), () -> {
					System.out.println(clz.getName());
					SerializerFactory debug = SerializerFactory.createDebug();
					debug.build(clz);
				})
		);
	}

	public static class BasicSimpleScanTest {

		@Serialize
		public Foo<Foo<Integer>> foo2integer;

		public BasicSimpleScanTest(Foo<Foo<Integer>> foo2integer) {
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
		public TestingInhiritedField<Integer> inhiritedField;

		public BasicScanTest(int integer, TestingObjectScan object, TestingObjectScan testingDeduplication, TestingInhiritedField<Integer> inhiritedField) {
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

	public static class TestingInhiritedField<T> extends ImYoSuper<T> {
		@Serialize
		public int something;

		public TestingInhiritedField(int SUPERFIELD, T parameters, List<@SerNull Integer> list, TestingInhiritedField<T> field, int something) {
			super(SUPERFIELD, parameters, list, field);
			this.something = something;
		}
	}

	public static class ImYoSuper<K> {
		@Serialize
		public int SUPERFIELD;

		@Serialize
		public K parameters;

		@Serialize
		@SerNull
		@SerSubclasses({LinkedList.class, ArrayList.class})
		public List<@SerNull Integer> list;

		@Serialize
		public TestingInhiritedField<K> field;

		public ImYoSuper(int SUPERFIELD, K parameters, List<@SerNull Integer> list, TestingInhiritedField<K> field) {
			this.SUPERFIELD = SUPERFIELD;
			this.parameters = parameters;
			this.list = list;
			this.field = field;
		}
	}

	public static class Foo<T> {
		@Serialize
		public T t;

		public Foo(T t) {
			this.t = t;
		}
	}

	public static class IntFoo extends Foo<Integer> {
		public IntFoo(Integer t) {
			super(t);
		}

		// FIXME Tracking issue #10
		public IntFoo(Object t) {
			this((Integer) t);
		}
	}

	public static class Foo2<E> extends Foo<E> {
		@Serialize
		public E e;

		public Foo2(E e, E t) {
			super(t);
			this.e = e;
		}
	}

	public static class Pair<A, B> {
		@Serialize
		public A first;

		@Serialize
		public B second;

		public Pair(A a, B b) {
			this.first = a;
			this.second = b;
		}
	}

	public static class SelfPair<T> extends Pair<T, T> {
		public SelfPair(T left, T right) {
			super(left, right);
		}
	}

	public static class IntPair<T> extends Pair<Integer, T> {
		public IntPair(Integer i, T right) {
			super(i, right);
		}

		// FIXME Tracking issue #10
		public IntPair(Object i, T right) {
			this((Integer) i, right);
		}
	}

	public static class Bar<K, T> extends Foo<T> {
		@Serialize
		public K k;

		public Bar(T t, K k) {
			super(t);
			this.k = k;
		}
	}

	public static class Baz<F> extends Bar<String, F> {
		public Baz(F t, String s) {
			super(t, s);
		}

		// FIXME Tracking issue #10
		public Baz(F t, Object s) {
			this(t, (String) s);
		}
	}

	public static class FooObe<FooT> extends Foo<Foo<FooT>> {
		public FooObe(Foo<FooT> ts) {
			super(ts);
		}

		// FIXME Tracking issue #10
		public FooObe(Object ts) {
			//noinspection unchecked
			super((Foo<FooT>) ts);
		}
	}

	public static class Obe<T> extends Foo<List<T>> {
		public Obe(List<T> ts) {
			super(ts);
		}

		// FIXME Tracking issue #10
		public Obe(Object ts) {
			//noinspection unchecked
			super((List<T>) ts);
		}
	}

	public static class Caz<K, T extends List<K>> extends Foo<T> {
		@Serialize
		public K kkk;

		public Caz(K kkk, T ks) {
			super(ks);
			this.kkk = kkk;
		}

		// FIXME Tracking issue #10
		public Caz(K kkk, Object ks) {
			//noinspection unchecked
			this(kkk, (T) ks);
		}
	}

	public static class FooCaz<K, FooK extends Foo<K>> extends Foo<FooK> {
		@Serialize
		public K kkk;

		public FooCaz(K kkk, FooK ks) {
			super(ks);
			this.kkk = kkk;
		}

		// FIXME Tracking issue #10
		public FooCaz(K kkk, Object ks) {
			//noinspection unchecked
			this(kkk, (FooK) ks);
		}
	}

	public static class RecursiveFoo<T> extends Foo<T> {
		@Serialize
		@SerSubclasses({Foo.class, RecursiveFoo.class})
		public Foo<T> foo;

		public RecursiveFoo(T t, Foo<T> foo) {
			super(t);
			this.foo = foo;
		}
	}


	public static class PolymorphicGenericTypeTests {


		public static class Recursive {
			@Serialize
			@SerSubclasses({Foo.class, RecursiveFoo.class})
			public Foo<String> simpler;

			public Recursive(Foo<String> simpler) {
				this.simpler = simpler;
			}
		}

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

		public static class FooDeFoo {
			@Serialize
			@SerSubclasses({Foo.class, FooObe.class})
			public Foo<Foo<Integer>> simplish;

			public FooDeFoo(Foo<Foo<Integer>> simplish) {
				this.simplish = simplish;
			}
		}

		// Tracking issue #2
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

			public Complex(Foo<Integer> complex) {
				this.complex = complex;
			}
		}

		// Tracking issue #3
		public static class ErrorsMissingType {
			@Serialize
			@SerSubclasses({Foo.class, Bar.class})
			public Foo<Integer> errors;

			public ErrorsMissingType(Foo<Integer> errors) {
				this.errors = errors;
			}
		}

		// Tracking issues #3, #4
		public static class Errors {
			@Serialize
			@SerSubclasses({Foo.class, Obe.class})
			public Foo<Integer> errors;

			public Errors(Foo<Integer> errors) {
				this.errors = errors;
			}
		}


		// Tracking issue #3
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

		// Tracking issue #3
		public static class Errors3 {
			@Serialize
			@SerSubclasses({Pair.class, SelfPair.class})
			public Pair<Integer, Float> errors3;

			public Errors3(Pair<Integer, Float> errors3) {
				this.errors3 = errors3;
			}
		}

		public static class SimplerFooFoo {
			@Serialize
			@SerSubclasses({Foo.class, Foo2.class})
			public Foo<Foo<Integer>> FooArrayList;

			public SimplerFooFoo(Foo<Foo<Integer>> fooArrayList) {
				this.FooArrayList = fooArrayList;
			}
		}

		public static class SimpleFooFoo {
			@Serialize
			@SerSubclasses({Foo.class, Baz.class})
			public Foo<Foo<Integer>> FooArrayList;

			public SimpleFooFoo(Foo<Foo<Integer>> fooArrayList) {
				this.FooArrayList = fooArrayList;
			}
		}

		public static class SimplerStackedFooFoo {
			@Serialize
			@SerSubclasses({Foo.class, Foo2.class})
			public Foo<@SerSubclasses({Foo.class, Foo2.class}) Foo<Integer>> FooArrayList;

			public SimplerStackedFooFoo(Foo<Foo<Integer>> fooArrayList) {
				this.FooArrayList = fooArrayList;
			}
		}

		public static class SimpleStackedFooFoo {
			@Serialize
			@SerSubclasses({Foo.class, Baz.class})
			public Foo<@SerSubclasses({Foo.class, Foo2.class}) Foo<Integer>> FooArrayList;

			public SimpleStackedFooFoo(Foo<Foo<Integer>> fooArrayList) {
				this.FooArrayList = fooArrayList;
			}
		}
/*
		// Tracking issue #
		public static class SimpleSuperStackedFooFoo {
			@Serialize
			public
			@SerSubclasses({Foo.class, Baz.class}) Foo<
					@SerSubclasses({Foo.class, Foo2.class}) Foo<
							@SerSubclasses({Foo.class, Baz.class}) Foo<
									@SerSubclasses({Foo.class, Foo2.class}) Foo<
											@SerSubclasses({Foo.class, Baz.class}) Foo<
													@SerSubclasses({Foo.class, Foo2.class}) Foo<
															@SerSubclasses({Foo.class, Baz.class}) Foo<
																	@SerSubclasses({Foo.class, Foo2.class}) Foo<
																			@SerSubclasses({Foo.class, Baz.class}) Foo<
																					@SerSubclasses({Foo.class, Foo2.class}) Foo<
																							@SerSubclasses({Foo.class, Baz.class}) Foo<
																									@SerSubclasses({Foo.class, Foo2.class}) Foo<
																											@SerSubclasses({Foo.class, Baz.class}) Foo<
																													@SerSubclasses({Foo.class, Foo2.class}) Foo<
																															@SerSubclasses({Foo.class, Baz.class}) Foo<
																																	@SerSubclasses({Foo.class, Foo2.class}) Foo<
							Integer>>>>>>>>>>>>>>>> FooArrayList;

			public SimpleSuperStackedFooFoo(Foo<Foo<Foo<Foo<Foo<Foo<Foo<Foo<Foo<Foo<Foo<Foo<Foo<Foo<Foo<Foo<Integer>>>>>>>>>>>>>>>> fooArrayList) {
				this.FooArrayList = fooArrayList;
			}
		}*/

		// Tracking issue #2
		public static class FooFoo {
			@Serialize
			@SerSubclasses({Foo.class, Baz.class})
			@SerComplexSubClass(
					value = Bar.class,
					types = {@SerDefined(name = "K", values = {
							String.class, Boolean.class
					})}
			)
			public Foo<Foo<Integer>> FooArrayList;

			public FooFoo(Foo<Foo<Integer>> fooArrayList) {
				this.FooArrayList = fooArrayList;
			}
		}

		public static class SimplerFooArrayList {
			@Serialize
			@SerSubclasses({Foo.class, Foo2.class})
			public Foo<ArrayList<Integer>> FooArrayList;

			public SimplerFooArrayList(Foo<ArrayList<Integer>> fooArrayList) {
				this.FooArrayList = fooArrayList;
			}
		}

		public static class SimpleFooArrayList {
			@Serialize
			@SerSubclasses({Foo.class, Baz.class})
			public Foo<ArrayList<Integer>> FooArrayList;

			public SimpleFooArrayList(Foo<ArrayList<Integer>> fooArrayList) {
				this.FooArrayList = fooArrayList;
			}
		}

		// Tracking issue: #6
		public static class SimplishFooArrayList {
			@Serialize
			@SerSubclasses({Foo.class, Obe.class})
			public Foo<ArrayList<Integer>> FooArrayList;

			public SimplishFooArrayList(Foo<ArrayList<Integer>> fooArrayList) {
				this.FooArrayList = fooArrayList;
			}
		}

		// Tracking issue #2
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

			public FooArrayList(Foo<ArrayList<Integer>> fooArrayList) {
				this.FooArrayList = fooArrayList;
			}
		}

		// Tracking issue #2
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

			public FooList(Foo<List<Integer>> fooList) {
				this.FooList = fooList;
			}
		}

		public static class SimplerNumber {
			@Serialize
			@SerSubclasses({Foo.class, Foo2.class})
			public Foo<@SerSubclasses({Integer.class, Float.class}) Number> simple_number;

			public SimplerNumber(Foo<Number> simple_number) {
				this.simple_number = simple_number;
			}
		}

		public static class SimpleNumber {
			@Serialize
			@SerSubclasses({Foo.class, Baz.class})
			public Foo<@SerSubclasses({Integer.class, Float.class}) Number> simple_number;

			public SimpleNumber(Foo<Number> simple_number) {
				this.simple_number = simple_number;
			}
		}

		// Tracking issue #2
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

			public ComplexNumber(Foo<Number> complex_number) {
				this.complex_number = complex_number;
			}
		}

		// Tracking issue #3
		public static class ErrorsNumber {

			@Serialize
			@SerSubclasses({Foo.class, Baz.class, Obe.class})
			public Foo<@SerSubclasses({Integer.class, Float.class}) Number> errors_number;

			public ErrorsNumber(Foo<Number> errors_number) {
				this.errors_number = errors_number;
			}
		}

		// Tracking issue #6
		public static class FooArrayListNumber {
			@Serialize
			@SerSubclasses({Foo.class, Baz.class, Obe.class})
			public Foo<ArrayList<@SerSubclasses({Integer.class, Float.class}) Number>> FooArrayList_number;

			public FooArrayListNumber(Foo<ArrayList<Number>> fooArrayList_number) {
				this.FooArrayList_number = fooArrayList_number;
			}
		}

		// FIXME: I think this test is invalid
		public static class FooListNumber {
			@Serialize
			@SerSubclasses({Foo.class, Baz.class, Obe.class})
			public Foo<@SerSubclasses({ArrayList.class, LinkedList.class}) List<
					@SerSubclasses({Integer.class, Float.class}) Number>> FooList_number;

			public FooListNumber(Foo<List<Number>> fooList_number) {
				this.FooList_number = fooList_number;
			}
		}

		// FIXME: possibly invalid as Obe extends Foo<List<T>> and not Foo<ArrayList<T>>
		public static class PartiallyErrorsNumber {
			@Serialize
			@SerSubclasses({Foo.class, Baz.class, Obe.class})
			public Foo<
					@SerSubclasses({Integer.class, Float.class})
					@SerComplexSubClass(value = ArrayList.class, types =
					@SerDefined(name = "E", values = {Integer.class, Float.class}))
							Object> partiallyErrors_number;

			public PartiallyErrorsNumber(Foo<Object> partiallyErrors_number) {
				this.partiallyErrors_number = partiallyErrors_number;
			}
		}

		// FIXME: I think this test is invalid, IntFoo : Foo<Integer> is not assignable to Foo<Number>
		public static class PartiallyErrorsNumber2 {
			@Serialize
			@SerSubclasses({Foo.class, IntFoo.class})
			public Foo<@SerSubclasses({Integer.class, Float.class})
					Number> partiallyErrors_number;

			public PartiallyErrorsNumber2(Foo<Number> partiallyErrors_number) {
				this.partiallyErrors_number = partiallyErrors_number;
			}
		}

		// FIXME: I think this test is invalid, Pair<Integer, ?> is not assignable to Pair<Number, ?>
		public static class PartiallyErrorsNumber3 {
			@Serialize
			@SerSubclasses({Pair.class, IntPair.class})
			public Pair<
					@SerSubclasses({Integer.class, Float.class}) Number,
					@SerSubclasses({Integer.class, Float.class}) Number
					> partiallyErrors_number;

			public PartiallyErrorsNumber3(Pair<Number, Number> partiallyErrors_number) {
				this.partiallyErrors_number = partiallyErrors_number;
			}
		}

		public static class PartiallyErrorsNumber2Extends {
			@Serialize
			@SerSubclasses({Foo.class, IntFoo.class})
			public Foo<@SerSubclasses({Integer.class, Float.class})
					? extends Number> partiallyErrors_number;

			public PartiallyErrorsNumber2Extends(Foo<? extends Number> partiallyErrors_number) {
				this.partiallyErrors_number = partiallyErrors_number;
			}
		}

		// FIXME: I think this test is invalid, Pair<Integer, ?> is not assignable to Pair<Number, ?>
		public static class PartiallyErrorsNumber3Extends {
			@Serialize
			@SerSubclasses({Pair.class, IntPair.class})
			public Pair<
					@SerSubclasses({Integer.class, Float.class}) ? extends Number,
					@SerSubclasses({Integer.class, Float.class}) ? extends Number
					> partiallyErrors_number;

			// TODO: is there a difference between
			//  @SerSubclasses({Integer.class, Float.class}) ? extends Number
			//  ? extends @SerSubclasses({Integer.class, Float.class}) Number

			public PartiallyErrorsNumber3Extends(Pair<? extends Number, ? extends Number> partiallyErrors_number) {
				this.partiallyErrors_number = partiallyErrors_number;
			}
		}

		public static class CleanFooExtract {
			@Serialize
			@SerSubclasses({Foo.class, FooCaz.class})
			public Foo<Foo<Integer>> extract;

			public CleanFooExtract(Foo<Foo<Integer>> extract) {
				this.extract = extract;
			}
		}

		// Tracking issue #6
		public static class CleanFooExtendsExtract {
			@Serialize
			@SerSubclasses({Foo.class, FooCaz.class})
			public Foo<Foo2<Integer>> extract;

			public CleanFooExtendsExtract(Foo<Foo2<Integer>> extract) {
				this.extract = extract;
			}
		}

		public static class Extract {
			@Serialize
			@SerSubclasses({Foo.class, Baz.class, Caz.class})
			public Foo<@SerSubclasses({ArrayList.class, LinkedList.class}) List<Integer>> extract;

			public Extract(Foo<@SerSubclasses({ArrayList.class, LinkedList.class}) List<Integer>> extract) {
				this.extract = extract;
			}
		}

		// Tracking issue #2
		public static class PartiallyErrorsExtractNumber {
			@Serialize
			@SerSubclasses({Foo.class, Baz.class, Caz.class})
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

			public PartiallyErrorsExtractNumber(Foo<Object> partiallyErrorsExtract_number) {
				this.partiallyErrorsExtract_number = partiallyErrorsExtract_number;
			}
		}

		public static class Arrays {
			@Serialize
			@SerSubclasses({Integer[].class, Float[].class})
			public Number[] numbers;

			public Arrays(Number[] numbers) {
				this.numbers = numbers;
			}
		}

		// Tracking issue #8
		public static class FooExtendsNumber {
			@Serialize
			@SerSubclasses({Foo.class, Bar.class, Foo2.class})
			public Foo<@SerSubclasses({Integer.class, Float.class}) ? extends Number> numbers;

			public FooExtendsNumber(Foo<? extends Number> numbers) {
				this.numbers = numbers;
			}
		}

		// Tracking issue #9
		public static class FooNumberArray {
			@Serialize
			public Foo<@SerSubclasses({Integer.class, Float.class}) Number> @SerSubclasses({Foo.class, Bar.class, Foo2.class})[] numbers;

			public FooNumberArray(Foo<Number>[] numbers) {
				this.numbers = numbers;
			}
		}
	}
}
