package dev.quantumfusion.hyphen.scan;

import dev.quantumfusion.hyphen.scan.annotations.DataGlobalAnnotation;
import dev.quantumfusion.hyphen.scan.struct.*;
import dev.quantumfusion.hyphen.thr.HyphenException;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class StructScanner {
	private final Map<Object, List<Annotation>> annotationProviders;

	public StructScanner() {
		this(Map.of());
	}

	public StructScanner(Map<Object, List<Annotation>> annotationProviders) {
		this.annotationProviders = annotationProviders;
	}

	public Struct scan(AnnotatedType annotatedType, @Nullable StructParameters parent) {
		return this.scan(annotatedType.getType(), annotatedType, parent);
	}

	public Struct scan(Class<?> annotatedType, @Nullable StructParameters parent) {
		return this.scan(annotatedType, annotatedType, parent);
	}

	public Struct getSubtype(Class<?> subType, Struct superType) {
		if (subType.isArray()) {
			if (superType instanceof ArrayStruct superStruct) {
				return new ArrayStruct(this.getSubtype(subType.componentType(), superStruct.component));
			}
		} else {
			if (superType instanceof ClassStruct superStruct) {
				ClassStruct subStruct = (ClassStruct) this.scan(subType, null);
				try {
					if (!resolveSubtype(subStruct, superStruct)) {
						throw new HyphenException(
								"Subclass " + subStruct + " does not inherit " + superStruct,
								"Inherit " + superStruct.aClass.getSimpleName() + " or don't use " + subStruct.aClass.getSimpleName()
						);
					}
				} catch (Throwable throwable) {
					throw new HyphenException(subStruct.simpleString() + " is incompatible with " + superStruct.simpleString(), throwable, "Find out why the subtype is incompatible with the target by looking at the other exceptions.");
				}
				return subStruct;
			}
		}

		throw new HyphenException("Subtype " + subType + " is incompatible with supertype " + superType, null);
	}

	private boolean resolveSubtype(ClassStruct subStruct, ClassStruct targetStruct) {
		if (subStruct.aClass == targetStruct.aClass) {
			subStruct.resolve(targetStruct);
			return true;
		}

		if (subStruct == ClassStruct.OBJECT) {
			return false;
		}

		ClassStruct aSuper = subStruct.getSuper(this);
		try {
			if (aSuper != null && this.resolveSubtype(aSuper, targetStruct)) {
				return true;
			}
		} catch (Throwable throwable) {
			throw HyphenException.rethrow(subStruct, "super " + aSuper.simpleString(), throwable);
		}


		for (ClassStruct anInterface : subStruct.getInterfaces(this)) {
			try {
				if (this.resolveSubtype(anInterface, targetStruct)) {
					return true;
				}
			} catch (Throwable throwable) {
				throw HyphenException.rethrow(subStruct, "interface " + anInterface.simpleString(), throwable);
			}
		}

		return false;
	}

	/**
	 * Scans the type to a Struct implementation.
	 *
	 * @param type             The type to be scanned and turned into a struct.
	 * @param annotatedElement The types annotation element which contains annotation information for the type.
	 * @param parent           parent to be used for linking {@link TypeStruct}s to the parents {@link ParameterStruct}s
	 * @return A struct representing the type
	 */
	public Struct scan(Type type, AnnotatedElement annotatedElement, @Nullable StructParameters parent) {
		if (type instanceof WildcardType) {
			return scanWildcard((AnnotatedWildcardType) annotatedElement, parent);
		}

		if (type instanceof GenericArrayType) {
			return scanArray((AnnotatedArrayType) annotatedElement, parent);
		}

		if (type instanceof TypeVariable<?> ty) {
			return scanType(ty, (AnnotatedTypeVariable) annotatedElement, parent);
		}

		if (type instanceof ParameterizedType) {
			return scanParameterized((AnnotatedParameterizedType) annotatedElement, parent);
		}

		if (type instanceof Class<?> aClass) {
			return scanClass(aClass, annotatedElement, parent);
		}

		throw new IllegalStateException(annotatedElement.toString());
	}

	private ArrayStruct scanArray(AnnotatedArrayType arrayType, @Nullable StructParameters parent) {
		return new ArrayStruct(
				getAnnotations(arrayType),
				this.scan(arrayType.getAnnotatedGenericComponentType(), parent)
		);
	}

	private Struct scanClass(Class<?> aClass, AnnotatedElement annotatedElement, @Nullable StructParameters parent) {
		if (aClass.isArray()) {
			if (annotatedElement instanceof AnnotatedArrayType type) {
				return new ArrayStruct(
						getAnnotations(annotatedElement),
						this.scan(type.getAnnotatedGenericComponentType(), parent)
				);
			}

			throw new IllegalStateException("my ass");
		}

		TypeVariable<? extends Class<?>>[] typeParameters = aClass.getTypeParameters();

		var parameters = new ArrayList<ParameterStruct>();
		for (TypeVariable<? extends Class<?>> typeParameter : typeParameters) {
			parameters.add(new ParameterStruct(
					getAnnotations(typeParameter), this.getBound(typeParameter.getAnnotatedBounds(), parent),
					ClassStruct.OBJECT, typeParameter.getName()));
		}
		return new ClassStruct(getAnnotations(aClass, annotatedElement), aClass, parameters);
	}

	private ClassStruct scanParameterized(AnnotatedParameterizedType annotatedType, @Nullable StructParameters parent) {
		ParameterizedType type = (ParameterizedType) annotatedType.getType();
		Class<?> aClass = (Class<?>) type.getRawType();

		// We scan both the actual parameters and the raw ones. This is because java does not have an AnnotatedTypeVariable
		// but instead merges both the Annotation information and the Type information into TypeVariable.
		// Why they do this, I have no idea but here we are.
		var typeParameters = aClass.getTypeParameters();
		var annotatedParameters = annotatedType.getAnnotatedActualTypeArguments();

		// Preload all of our parameters
		var parameters = new ArrayList<ParameterStruct>();
		for (var typeParameter : typeParameters) {
			parameters.add(new ParameterStruct(
					getAnnotations(typeParameter),
					ClassStruct.OBJECT,
					ClassStruct.OBJECT,
					typeParameter.getName())
			);
		}

		// If the parent is null, that means we are resolving ourselves and to have some epic resolution
		// we need to link parameters bound to other parameters because that is possible under self scanning
		StructParameters structParameters = parent;
		if (structParameters == null) {
			structParameters = new StructParameters(parameters);
		}


		for (int i = 0; i < typeParameters.length; i++) {
			var annotated = annotatedParameters[i];
			var typeParameter = typeParameters[i];
			ParameterStruct parameterStruct = parameters.get(i);
			parameterStruct.bound = this.getBound(typeParameter.getAnnotatedBounds(), parent);
			parameterStruct.resolved = this.scan(annotated.getType(), annotated, structParameters);
		}

		return new ClassStruct(
				getAnnotations(aClass, annotatedType), aClass,
				parameters);
	}

	private WildcardStruct scanWildcard(AnnotatedWildcardType annotatedType, @Nullable StructParameters parent) {
		// Add annotations applied on the wildcard
		var annotations = getAnnotations(annotatedType);

		// Check the lower bound and unsure it only has 1 bound.
		// Lower bounds return an empty array if it does not contain a lower bound.
		var annotatedLowerBound = annotatedType.getAnnotatedLowerBounds();
		if (annotatedLowerBound.length != 0) {
			// This is not something the JVM at the moment allows
			//if (annotatedLowerBound.length != 1) {
			//	throw new IllegalArgumentException("Cannot have more than 1 bound");
			//}
			return new WildcardStruct(
					annotations,
					this.scan(annotatedLowerBound[0].getType(), annotatedLowerBound[0], parent),
					true
			);
		}

		// Check the upper type and unsure it only has 1 bound.
		// Upper bounds return an array of size 1 containing Object if it's an unbound wildcard.
		var annotatedUpperBounds = annotatedType.getAnnotatedUpperBounds();
		var annotatedUpperBound = annotatedUpperBounds[0];

		// This is not something the JVM at the moment allows
		//if (annotatedUpperBounds.length != 1) {
		//	throw new IllegalArgumentException("Cannot have more than 1 bound");
		//}

		return new WildcardStruct(
				annotations,
				this.scan(annotatedUpperBound.getType(), annotatedUpperBound, parent),
				false
		);
	}

	private Struct scanType(TypeVariable<?> type, AnnotatedTypeVariable annotatedElement, @Nullable StructParameters
			parent) {
		if (parent == null) {
			return ClassStruct.OBJECT;
		}
		Integer integer = parent.lookup.get(type.getName());
		if (integer == null) {
			return ClassStruct.OBJECT;
		}

		ParameterStruct parameterStruct = parent.getParameter(integer);
		return new TypeStruct(getAnnotations(annotatedElement), parameterStruct);
	}


	private Struct getBound(AnnotatedType[] bounds, @Nullable StructParameters parent) {
		if (bounds.length > 1) {
			throw new IllegalArgumentException("Cannot have more than 1 bound");
		}
		return this.scan(bounds[0].getType(), bounds[0], parent);
	}

	private List<Annotation> getAnnotations(AnnotatedElement... AnnotatedElement) {
		List<Annotation> annotationMap = new ArrayList<>();

		for (AnnotatedElement annotatedElement : AnnotatedElement) {
			List<Annotation> annotations = annotationProviders.get(annotatedElement);
			if (annotations != null) {
				annotationMap.addAll(annotations);
			}

			Annotation[] declaredAnnotations = annotatedElement.getDeclaredAnnotations();
			for (Annotation declaredAnnotation : declaredAnnotations) {
				if (declaredAnnotation instanceof DataGlobalAnnotation globalAnnotation) {
					List<Annotation> annotations2 = annotationProviders.get(globalAnnotation.value());
					if (annotations2 != null) {
						annotationMap.addAll(annotations2);
					}
				}
			}
			annotationMap.addAll(List.of(declaredAnnotations));
		}
		return annotationMap;
	}

	public static <T extends Annotation> T[] mergeArrays(T[]... arrays) {
		int length = 0;

		T[] oneArray = null;
		for (T[] array : arrays) {
			length += array.length;
			if (array.length == 1) {
				oneArray = array;
			}
		}

		if (length == 0) {
			return (T[]) new Annotation[0];
		} else if (length == 1) {
			return oneArray;
		} else {
			T[] annotations = (T[]) new Annotation[length];
			int i = 0;
			for (T[] array : arrays) {
				System.arraycopy(array, 0, annotations, i, array.length);
				i += array.length;
			}
			return annotations;
		}

	}

	public static String writeAnnotations(Map<Class<? extends Annotation>, Annotation> annotations) {
		if (annotations.isEmpty()) {
			return "";
		}
		StringJoiner stringJoiner = new StringJoiner(" ", "(", ")");
		for (Annotation annotation : annotations.values()) {
			stringJoiner.add(annotation.toString());
		}
		return stringJoiner.toString();
	}
}
