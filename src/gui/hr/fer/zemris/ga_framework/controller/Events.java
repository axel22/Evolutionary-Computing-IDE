package hr.fer.zemris.ga_framework.controller;



public enum Events {
	
	EDITOR_CREATED,
	EDITOR_LOADED,
	VIEW_CREATED,
	ALGORITHM_TREE_CHANGED,
	ALGORITHM_STARTED,
	ALGORITHM_PAUSED,
	ALGORITHM_RESUMED,
	ALGORITHM_FINISHED,
	ALGORITHM_ERROR,
	JOB_STARTED,
	JOB_FINISHED,
	JOB_HALTED,
	JOB_ENQUEUED,
	JOB_REMOVED_FROM_QUEUE;
	
	
	public static class KEY {
		public static final String ALGORITHM_OBJECT = "AlgorithmObj";
		public static final String GRAPH_OBJECT = "GraphObj";
		public static final String EXCEPTION_OBJECT = "ExceptionObj";
		public static final String VIEW_CLSNAME = "ViewClassName";
		public static final String EDITOR_CLSNAME = "EditorClassName";
		public static final String EDITOR_ID = "EditorId";
		public static final String JOB_ID = "JobId";
		public static final String RETURN_VALS = "ReturnVals";
		public static final String INPUT_VALS = "InputVals";
		public static final String JOB_INFO = "JobInfo";
		public static final String ITERATION_INDEX = "ParamSetIndex";
		public static final String TOTAL_ITERATIONS = "TotalParamSets";
		public static final String ALGORITHM_INDEX = "AlgIndex";
		public static final String ENQUEUED_INDEX = "EnqueuedJobIndex";
		public static final String FILE_FOR_LOAD = "FileForLoad";
	}
	
}














