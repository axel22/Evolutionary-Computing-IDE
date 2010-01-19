package hr.fer.zemris.ga_framework.view.editors.graph_editor;

import hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler.Model.AdditionalHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class GraphInfoWrapper {
	
	public static class Pair {
		private String key;
		private String value;
		public void setValue(String value) {
			this.value = value;
		}
		public String getValue() {
			return value;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getKey() {
			return key;
		}
	}
	
	public static class GraphWrapper {
		private String name;
		private String abscissa;
		private String description;
		private String ordinate;
		private Map<String, String> cfamily = new LinkedHashMap<String, String>();
		public void setCfamily(Map<String, String> cfamily) {
			this.cfamily = cfamily;
		}
		public Map<String, String> getCfamily() {
			return cfamily;
		}
		public void addToCurveFamily(Pair p) {
			cfamily.put(p.getKey(), p.getValue());
		}
		public void setOrdinate(String ordinate) {
			this.ordinate = ordinate;
		}
		public String getOrdinate() {
			return ordinate;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getDescription() {
			return description;
		}
		public void setAbscissa(String abscissa) {
			this.abscissa = abscissa;
		}
		public String getAbscissa() {
			return abscissa;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}
	
	public static class MapWrapper {
		private Map<String, String> parameterSet = new LinkedHashMap<String, String>();

		public void setParameterSet(Map<String, String> parameterSet) {
			this.parameterSet = parameterSet;
		}

		public Map<String, String> getParameterSet() {
			return parameterSet;
		}
		
		public void addPair(Object p) {
			parameterSet.put(((Pair) p).getKey(), ((Pair) p).getValue());
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MapWrapper)) return false;
			MapWrapper other = (MapWrapper)obj;
			return this.parameterSet.equals(other.parameterSet);
		}

		@Override
		public int hashCode() {
			return parameterSet.hashCode();
		}
		
	}
	
	public static class MapWrapperPair {
		private MapWrapper first;
		private MapWrapper second;

		public void setSecond(Object second) {
			this.second = (MapWrapper) second;
		}

		public MapWrapper getSecond() {
			return second;
		}

		public void setFirst(Object first) {
			this.first = (MapWrapper) first;
		}

		public MapWrapper getFirst() {
			return first;
		}
	}
	
	public static class ParameterInventoryWrapper {
		private String runs;
		private List<String> changingParameterNames = new ArrayList<String>();
		private Map<MapWrapper, MapWrapper> inventory = new LinkedHashMap<MapWrapper, MapWrapper>();
		private List<AdditionalHandler> additionalHandlers = new ArrayList<AdditionalHandler>();
		
		public void setChangingParameterNames(List<String> changingParameterNames) {
			this.changingParameterNames = changingParameterNames;
		}

		public List<String> getChangingParameterNames() {
			return changingParameterNames;
		}
		
		public void addToChangingNames(String s) {
			changingParameterNames.add(s);
		}
		
		public void addAdditionalHandler(AdditionalHandler ah) {
			additionalHandlers.add(ah);
		}

		public void setRuns(String runs) {
			this.runs = runs;
		}

		public String getRuns() {
			return runs;
		}
		
		public void addMapWrapperPair(Object pair) {
			getInventory().put(((MapWrapperPair)pair).first, ((MapWrapperPair)pair).second);
		}

		public void setInventory(Map<MapWrapper, MapWrapper> inventory) {
			this.inventory = inventory;
		}

		public Map<MapWrapper, MapWrapper> getInventory() {
			return inventory;
		}

		public void setAdditionalHandlers(List<AdditionalHandler> additionalHandlers) {
			this.additionalHandlers = additionalHandlers;
		}

		public List<AdditionalHandler> getAdditionalHandlers() {
			return additionalHandlers;
		}
		
		public AdditionalHandler getAdditionalHandler(String handlername) {
			for (AdditionalHandler ah : additionalHandlers) {
				if (ah.getHandlerName().equals(handlername)) return ah;
			}
			return null;
		}
	}
	
	private String algorithmName;
	private GraphWrapper graph;
	private ParameterInventoryWrapper inventory;

	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public void setGraph(final Object w) {
		graph = (GraphWrapper) w;
	}

	public GraphWrapper getGraph() {
		return graph;
	}

	public void setInventory(Object inventory) {
		this.inventory = (ParameterInventoryWrapper) inventory;
	}

	public ParameterInventoryWrapper getInventory() {
		return inventory;
	}
	
}














