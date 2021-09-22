package dev.quantumfusion.hyphen.info;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.gen.metadata.SerializerMetadata;
import dev.quantumfusion.hyphen.util.Color;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedWildcardType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class WildcardInfo extends TypeInfo {
	private final boolean upperBound;
	private final List<? extends TypeInfo> bounds;

	public WildcardInfo(Class<?> clazz, Type type, @Nullable AnnotatedType annotatedType, Map<Class<? extends Annotation>, Annotation> annotations, boolean upperBound, List<? extends TypeInfo> bounds) {
		super(clazz, type, annotatedType, annotations);
		this.upperBound = upperBound;
		this.bounds = bounds;
	}

	@Override
	public SerializerMetadata createMetadata(ScanHandler factory) {
		return null;
	}

	@Override
	public String toFancyString() {
		StringJoiner sj = new StringJoiner(" & ", this.upperBound ? "? extends " : "? super ", "");
		for (TypeInfo bound : this.bounds) {
			sj.add(bound.toFancyString());
		}
		return sj.toString();
	}

	@Override
	public String getMethodName(boolean absolute) {
		StringJoiner sj = new StringJoiner(" & ", this.upperBound ? "? extends " : "? super ", "");
		for (TypeInfo bound : this.bounds) {
			sj.add(bound.getMethodName(absolute));
		}
		return sj.toString();
	}

	public static TypeInfo createType(ScanHandler handler, TypeInfo source, Class<?> clazz, WildcardType wildcardType, @Nullable AnnotatedType annotatedType, Map<Class<? extends Annotation>, Annotation> annotations) {
		AnnotatedWildcardType annotatedWildcardType = (AnnotatedWildcardType) annotatedType;

		if (wildcardType.getLowerBounds().length != 0) {
			Type[] upperBounds = wildcardType.getLowerBounds();

			// Do we even support annotations on the bounds themselves?
			//   I think we shouldn't
			AnnotatedType[] annotatedUpperBounds = annotatedWildcardType == null
					? new AnnotatedType[upperBounds.length]
					: annotatedWildcardType.getAnnotatedLowerBounds();

			return createWildcardInfo(handler, source, clazz, wildcardType, annotatedType, annotations, upperBounds, annotatedUpperBounds, false);
		}

		if (wildcardType.getUpperBounds().length == 1 && wildcardType.getUpperBounds()[0] == Object.class)
			return ScanHandler.UNKNOWN_INFO;


		Type[] upperBounds = wildcardType.getUpperBounds();

		// Do we even support annotations on the bounds themselves?
		//   I think we shouldn't
		AnnotatedType[] annotatedUpperBounds = annotatedWildcardType == null
				? new AnnotatedType[upperBounds.length]
				: annotatedWildcardType.getAnnotatedUpperBounds();

		return createWildcardInfo(handler, source, clazz, wildcardType, annotatedType, annotations, upperBounds, annotatedUpperBounds, true);
	}


	private static WildcardInfo createWildcardInfo(ScanHandler handler, TypeInfo source, Class<?> clazz, WildcardType wildcardType, @Nullable AnnotatedType annotatedType, Map<Class<? extends Annotation>, Annotation> annotations, Type[] bounds, AnnotatedType[] annotatedBounds, boolean upper) {
		assert bounds.length == annotatedBounds.length;

		List<TypeInfo> boundInfos = new ArrayList<>(bounds.length);

		for (int i = 0; i < bounds.length; i++) {
			var upperBound = bounds[i];
			var annotatedUpperBound = annotatedBounds[i];

			TypeInfo boundInfo = handler.create(source, null, upperBound, annotatedUpperBound);
			boundInfos.add(boundInfo);
		}

		return new WildcardInfo(clazz, wildcardType, annotatedType, annotations, upper, boundInfos);
	}

	public static WildcardInfo UNKNOWN = new WildcardInfo(null, null, null, Map.of(), true, List.of(new ClassInfo(Object.class, Map.of()))) {
		@Override
		public String toString() {
			return "UNKNOWN";
		}

		@Override
		public String toFancyString() {
			return Color.BLUE + "?";
		}
	};
}
