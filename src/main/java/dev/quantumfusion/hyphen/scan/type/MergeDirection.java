package dev.quantumfusion.hyphen.scan.type;

public enum MergeDirection {
	LEFT {
		@Override
		<T> T getBase(T left, T right) {
			return left;
		}

		@Override
		<T> T getSub(T left, T right) {
			return right;
		}
	},
	RIGHT {

		@Override
		<T> T getBase(T left, T right) {
			return right;
		}

		@Override
		<T> T getSub(T left, T right) {
			return left;
		}
	};

	boolean isAssignable(Class<?> left, Class<?> right){
		return this.getBase(left, right).isAssignableFrom(this.getSub(left, right));
	}
	abstract <T> T getBase(T left, T right);
	abstract <T> T getSub(T left, T right);

	MergeDirection swap(){
		return values()[1 - this.ordinal()];
	}
}
