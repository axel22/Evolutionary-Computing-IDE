package hr.fer.zemris.ga_framework.view.editors.graph_editor;

import hr.fer.zemris.ga_framework.Application;
import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.model.IGraph;
import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.IParameterInventory;
import hr.fer.zemris.ga_framework.model.IValue;
import hr.fer.zemris.ga_framework.model.ReturnHandler;
import hr.fer.zemris.ga_framework.model.impl.Value;
import hr.fer.zemris.ga_framework.model.impl.parameters.DifferentNameParameter;
import hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler.Model.AdditionalHandler;
import hr.fer.zemris.ga_framework.view.editors.graph_editor.GraphInfoWrapper.MapWrapper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.digester.Digester;






public class GraphInfoSerializer {
	
	public static class SWrapper {
		public IAlgorithm algorithm;
		public IGraph graph;
		public IParameterInventory inventory;
	}
	
	private static class DefaultGraph implements IGraph {
		private String abs, desc, name, ord;
		private Map<String, Object> cfamily;
		public DefaultGraph(String n, String a, String o, String d, Map<String, Object> cf) {
			name = n;
			abs = a;
			ord = o;
			desc = d;
			cfamily = cf;
		}
		public String getAbscissaName() {
			return abs;
		}
		public Map<String, Object> getCurveFamily() {
			return cfamily;
		}
		public String getDescription() {
			return desc;
		}
		public String getGraphName() {
			return name;
		}
		public String getOrdinateName() {
			return ord;
		}
	}

	public static void serialize(IAlgorithm alg, IGraph graph, IParameterInventory inventory, OutputStream os) throws IOException {
		ZipOutputStream zos = new ZipOutputStream(os);
		zos.putNextEntry(new ZipEntry("Graph"));
		OutputStreamWriter w = new OutputStreamWriter(zos);
		
		w.write("<GraphInfo>\n");
		
		/* algorithm */
		w.write("\t<algorithm>");
		w.write(alg.getClass().getName());
		w.write("</algorithm>\n");
		
		/* graph */
		w.write("\t<graph>\n");
		w.write("\t\t<name>");
		w.write(graph.getGraphName());
		w.write("</name>\n");
		w.write("\t\t<abscissa>");
		w.write(graph.getAbscissaName());
		w.write("</abscissa>\n");
		w.write("\t\t<ordinate>");
		w.write(graph.getOrdinateName());
		w.write("</ordinate>\n");
		w.write("\t\t<description>");
		w.write(graph.getDescription());
		w.write("</description>\n");
		w.write("\t\t<cfamily>\n");
		for (Entry<String, Object> ntr : graph.getCurveFamily().entrySet()) {
			w.write("\t\t\t<ntr><k>");
			w.write(ntr.getKey());
			w.write("</k><v><![CDATA[");
			w.write(alg.getParameter(ntr.getKey()).serialize(ntr.getValue()));
			w.write("]]></v></ntr>\n");
		}
		w.write("\t\t</cfamily>\n");
		w.write("\t</graph>\n");
		
		/* inventory */
		w.write("\t<inventory>\n");
		w.write("\t\t<runs>");
		w.write(String.valueOf(inventory.getRunsPerSet()));
		w.write("</runs>\n");
		w.write("\t\t<changing>");
		for (String s : inventory.getChangingParamNames()) {
			w.write("<n>");
			w.write(s);
			w.write("</n>");
		}
		w.write("</changing>\n");
		w.write("\t\t<addit_handlers>\n");
		for (AdditionalHandler ah : inventory.getAdditionalHandlers()) {
			w.write("\t\t\t<ah>");
			w.write("<handlername>");
			w.write(ah.getHandlerName());
			w.write("</handlername>");
			w.write("<actualvaluename>");
			w.write(ah.getActualValueName());
			w.write("</actualvaluename>");
			w.write("<handlertype>");
			w.write(ah.getHandler());
			w.write("</handlertype>");
			w.write("</ah>\n");
		}
		w.write("\t\t</addit_handlers>\n");
		w.write("\t\t<paramMaps>\n");
		for (Map<String, IValue> inputMap : inventory) {
			Map<String, IValue> retMap = inventory.getReturnValues(inputMap);
			w.write("\t\t\t<mapEntry>\n");
			w.write("\t\t\t\t<input>\n");
			for (Entry<String, IValue> ntr : inputMap.entrySet()) {
				w.write("\t\t\t\t\t<ntr><k>");
				w.write(ntr.getKey());
				w.write("</k><v><![CDATA[");
				w.write(ntr.getValue().parameter().serialize(ntr.getValue().value()));
				w.write("]]></v></ntr>\n");
			}
			w.write("\t\t\t\t</input>\n");
			w.write("\t\t\t\t<return>\n");
			for (Entry<String, IValue> ntr : retMap.entrySet()) {
				w.write("\t\t\t\t\t<ntr><k>");
				w.write(ntr.getKey());
				w.write("</k><v><![CDATA[");
				w.write(ntr.getValue().parameter().serialize(ntr.getValue().value()));
				w.write("]]></v></ntr>\n");
			}
			w.write("\t\t\t\t</return>\n");
			w.write("\t\t\t</mapEntry>\n");
		}
		w.write("\t\t</paramMaps>\n");
		w.write("\t</inventory>\n");
		
		w.write("</GraphInfo>");
		
		w.flush();
		zos.closeEntry();
		zos.close();
	}
	
	public static SWrapper deserialize(InputStream is, hr.fer.zemris.ga_framework.model.Model appModel) {
		BufferedInputStream bis = new BufferedInputStream(is);
		
		Digester d = new Digester();
		
		d.addObjectCreate("GraphInfo", GraphInfoWrapper.class);
		d.addBeanPropertySetter("GraphInfo/algorithm", "algorithmName");
		d.addCallMethod("GraphInfo/graph", "setGraph", 1);
		d.addCallMethod("GraphInfo/inventory", "setInventory", 1);
		
		d.addObjectCreate("GraphInfo/graph", GraphInfoWrapper.GraphWrapper.class);
		d.addBeanPropertySetter("GraphInfo/graph/name", "name");
		d.addBeanPropertySetter("GraphInfo/graph/abscissa", "abscissa");
		d.addBeanPropertySetter("GraphInfo/graph/ordinate", "ordinate");
		d.addBeanPropertySetter("GraphInfo/graph/description", "description");
		d.addObjectCreate("GraphInfo/graph/cfamily/ntr", GraphInfoWrapper.Pair.class);
		d.addBeanPropertySetter("GraphInfo/graph/cfamily/ntr/k", "key");
		d.addBeanPropertySetter("GraphInfo/graph/cfamily/ntr/v", "value");
		d.addSetNext("GraphInfo/graph/cfamily/ntr", "addToCurveFamily");
		d.addCallParam("GraphInfo/graph", 0, true);
		
		/* inventory */
		d.addObjectCreate("GraphInfo/inventory", GraphInfoWrapper.ParameterInventoryWrapper.class);
		d.addBeanPropertySetter("GraphInfo/inventory/runs", "runs");
		
		/* inventory - changing names */
		d.addCallMethod("GraphInfo/inventory/changing/n", "addToChangingNames", 1);
		d.addCallParam("GraphInfo/inventory/changing/n", 0);
		
		/* inventory - additional handlers */
		//d.addCallMethod("GraphInfo/addit_handlers/cl", "clearAdditionalHandlers");
		d.addObjectCreate("GraphInfo/inventory/addit_handlers/ah", AdditionalHandler.class);
		d.addBeanPropertySetter("GraphInfo/inventory/addit_handlers/ah/handlername", "handlerName");
		d.addBeanPropertySetter("GraphInfo/inventory/addit_handlers/ah/actualvaluename", "actualValueName");
		d.addBeanPropertySetter("GraphInfo/inventory/addit_handlers/ah/handlertype", "handler");
		d.addSetNext("GraphInfo/inventory/addit_handlers/ah", "addAdditionalHandler");
		
		/* inventory - parameter maps */
		d.addCallMethod("GraphInfo/inventory/paramMaps/mapEntry", "addMapWrapperPair", 1);
		d.addObjectCreate("GraphInfo/inventory/paramMaps/mapEntry", GraphInfoWrapper.MapWrapperPair.class);
		d.addCallMethod("GraphInfo/inventory/paramMaps/mapEntry/input", "setFirst", 1);
		d.addCallMethod("GraphInfo/inventory/paramMaps/mapEntry/return", "setSecond", 1);
		
		d.addObjectCreate("GraphInfo/inventory/paramMaps/mapEntry/input", GraphInfoWrapper.MapWrapper.class);
		d.addCallMethod("GraphInfo/inventory/paramMaps/mapEntry/input/ntr", "addPair", 1);
		d.addObjectCreate("GraphInfo/inventory/paramMaps/mapEntry/input/ntr", GraphInfoWrapper.Pair.class);
		d.addBeanPropertySetter("GraphInfo/inventory/paramMaps/mapEntry/input/ntr/k", "key");
		d.addBeanPropertySetter("GraphInfo/inventory/paramMaps/mapEntry/input/ntr/v", "value");
		d.addCallParam("GraphInfo/inventory/paramMaps/mapEntry/input/ntr", 0, true);
		d.addCallParam("GraphInfo/inventory/paramMaps/mapEntry/input", 0, true);
		
		d.addObjectCreate("GraphInfo/inventory/paramMaps/mapEntry/return", GraphInfoWrapper.MapWrapper.class);
		d.addCallMethod("GraphInfo/inventory/paramMaps/mapEntry/return/ntr", "addPair", 1);
		d.addObjectCreate("GraphInfo/inventory/paramMaps/mapEntry/return/ntr", GraphInfoWrapper.Pair.class);
		d.addBeanPropertySetter("GraphInfo/inventory/paramMaps/mapEntry/return/ntr/k", "key");
		d.addBeanPropertySetter("GraphInfo/inventory/paramMaps/mapEntry/return/ntr/v", "value");
		d.addCallParam("GraphInfo/inventory/paramMaps/mapEntry/return/ntr", 0, true);
		d.addCallParam("GraphInfo/inventory/paramMaps/mapEntry/return", 0, true);
		
		d.addCallParam("GraphInfo/inventory/paramMaps/mapEntry", 0, true);
		
		d.addCallParam("GraphInfo/inventory", 0, true);
		
		GraphInfoWrapper wrapper = null;
		try {
			bis.mark(5000);
			ZipInputStream zis = new ZipInputStream(bis);
			@SuppressWarnings("unused")
			ZipEntry zntr = zis.getNextEntry();
			wrapper = (GraphInfoWrapper) d.parse(zis);
		} catch (Exception e) {
			try {
				bis.reset();
				wrapper = (GraphInfoWrapper) d.parse(bis);
			} catch (Exception e2) {
				throw new RuntimeException("Could not parse file.", e2);
			}
			Application.logerror("Old graph format.", "It seems old graph format file was loaded.");
		}
		final GraphInfoWrapper finalwrapper = wrapper;
		
		// transform wrapper to SWrapper
		// set algorithm
		final SWrapper sw = new SWrapper();
		try {
			sw.algorithm = (IAlgorithm) appModel.getClass(wrapper.getAlgorithmName()).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Could not create algorithm instance.", e);
		}
		
		// set graph
		Map<String, Object> cfmap = new LinkedHashMap<String, Object>();
		for (Entry<String, String> ntr : wrapper.getGraph().getCfamily().entrySet()) {
			Object obj = sw.algorithm.getParameter(ntr.getKey()).deserialize(ntr.getValue());
			cfmap.put(ntr.getKey(), obj);
		}
		sw.graph = new DefaultGraph(wrapper.getGraph().getName(), wrapper.getGraph().getAbscissa(),
				wrapper.getGraph().getOrdinate(), wrapper.getGraph().getDescription(), cfmap);
		
		// set parameter maps
		final Map<Map<String, IValue>, Map<String, IValue>> pmaps = new LinkedHashMap<Map<String,IValue>, Map<String,IValue>>();
		for (Entry<MapWrapper, MapWrapper> runEntry : wrapper.getInventory().getInventory().entrySet()) {
			Map<String, IValue> input = new HashMap<String, IValue>();
			Map<String, IValue> ret = new HashMap<String, IValue>();
			
			for (Entry<String, String> ntr : runEntry.getKey().getParameterSet().entrySet()) {
				IParameter p = sw.algorithm.getParameter(ntr.getKey()); 
				input.put(ntr.getKey(), new Value(p.deserialize(ntr.getValue()), p));
			}
			
			for (Entry<String, String> ntr : runEntry.getValue().getParameterSet().entrySet()) {
				IParameter p = sw.algorithm.getReturnValue(ntr.getKey()); 
				if (p == null) {
					// search for it in the list of additional handlers
					AdditionalHandler ah = wrapper.getInventory().getAdditionalHandler(ntr.getKey());
					IParameter origrv = sw.algorithm.getReturnValue(ah.getActualValueName());
					DifferentNameParameter dnrv = new DifferentNameParameter(ntr.getKey(), origrv);
					p = dnrv;
				}
				ret.put(ntr.getKey(), new Value(p.deserialize(ntr.getValue()), p));
			}
			
			pmaps.put(input, ret);
		}
		final int runsPerSet = Integer.parseInt(wrapper.getInventory().getRuns());
		final List<String> changingones = wrapper.getInventory().getChangingParameterNames();
		sw.inventory = new IParameterInventory() {
			public void appendTo(Map<String, IValue> inputValues, Map<String, IValue> returnValues) {
				throw new UnsupportedOperationException("Shouldn't be called now, hm?");
				// or simply add to map
			}
			public List<String> getChangingParamNames() {
				return changingones;
			}
			public List<ReturnHandler> getReturnHandlers() {
				throw new UnsupportedOperationException("Shouldn't be called now, hm?");
				// this really shouldn't be called from an object created here, thus, this
				// can be implemented later if needed
			}
			public List<AdditionalHandler> getAdditionalHandlers() {
				return finalwrapper.getInventory().getAdditionalHandlers();
			}
			public Map<String, IValue> getReturnValues(Map<String, IValue> setOfInputParams) {
				return pmaps.get(setOfInputParams);
			}
			public int getRunsPerSet() {
				return runsPerSet;
			}
			public int size() {
				return pmaps.size();
			}
			public Iterator<Map<String, IValue>> iterator() {
				return pmaps.keySet().iterator();
			}
		};
		
		return sw;
	}
	
}














