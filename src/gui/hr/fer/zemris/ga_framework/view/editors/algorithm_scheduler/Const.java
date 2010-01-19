package hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler;

public class Const {
	
	public static class EVENT {
		public static final int RANGE_TABLE_CHANGED = 1;
		public static final int RUN_PER_SET_CHANGED = 2;
		public static final int RETURN_TABLE_CHANGED = 3;
		public static final int GRAPH_TABLE_CHANGED = 4;
		public static final int GNUPLOT_TABLE_CHANGED = 5;
		public static final int ADD_HANDLERS_CHANGED = 6;
	}
	
	public static final int[] ALL_EVENTS = new int[] {
		EVENT.GRAPH_TABLE_CHANGED, EVENT.RANGE_TABLE_CHANGED,
		EVENT.RETURN_TABLE_CHANGED, EVENT.RUN_PER_SET_CHANGED,
		EVENT.GNUPLOT_TABLE_CHANGED, EVENT.ADD_HANDLERS_CHANGED
	};
	
	public static class KEY {
		public static final String MUST_SELECT = "last_sel";
		public static final String GRAPH_MUST_SELECT = "graph_sel";
	}
	
}














