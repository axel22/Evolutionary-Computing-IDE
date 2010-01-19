package hr.fer.zemris.ga_framework.view.editors.algorithm_editor;

public class Const {

	/* static fields */
	public static class ERROR {
		public static final int VALUE_NOT_ALLOWED = 1;
		public static final int ALGORITHM_DOES_NOT_EXIST = 2;
		public static final int UNKNOWN_INSTANTIATION_ERROR = 3;
		public static final int INVALID_SERIALIZATION = 4;
	}
	
	public static class EVENT {
		public static final int PARAMETER_CHANGED = 1;
		public static final int ALGORITHM_CHANGED = 2;
	}
	
	public static final int[] ALL_EVENTS = new int[] {
		EVENT.PARAMETER_CHANGED, EVENT.ALGORITHM_CHANGED
	};

}














