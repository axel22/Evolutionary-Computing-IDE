package hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler;

import hr.fer.zemris.ga_framework.Application;
import hr.fer.zemris.ga_framework.model.HandlerTypes;
import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.model.IGraph;
import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.ReturnHandler;
import hr.fer.zemris.ga_framework.model.misc.Time;
import hr.fer.zemris.ga_framework.view.gadgets.parameter_editor.dialogs.ParameterDialogFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import name.brijest.mvcapi.model.ListProp;
import name.brijest.mvcapi.model.Prop;

import org.apache.commons.digester.Digester;

public class Model {
	
	public static class GnuPlotData {
		private String filename;
		private List<String[]> cols = new ArrayList<String[]>();
		public void setFilename(String filename) {
			this.filename = filename;
		}
		public String getFilename() {
			return filename;
		}
		public void addInputValue(String iv) {
			cols.add(new String[]{iv, null});
		}
		public void addReturnValue(String rv) {
			cols.add(new String[]{rv, ""});
		}
		public List<String[]> getCols() {
			return cols;
		}
	}
	
	public static class AdditionalHandler {
		private String handlerName;
		private String actualValueName;
		private HandlerTypes handler;
		public void setHandlerName(String handlerName) {
			this.handlerName = handlerName;
		}
		public String getHandlerName() {
			return handlerName;
		}
		public void setActualValueName(String actualValueName) {
			this.actualValueName = actualValueName;
		}
		public String getActualValueName() {
			return actualValueName;
		}
		public void setHandler(String hs) {
			try {
				handler = HandlerTypes.valueOf(HandlerTypes.class, hs);
			} catch (IllegalArgumentException e) {
				Application.logexcept("Could not deserialize additional handler, name = " + handlerName, e);
				e.printStackTrace();
				handler = null;
			}
		}
		public String getHandler() {
			return handler.toString();
		}
		public HandlerTypes getHandlerType() {
			return handler;
		}
	}
	
	public static class RangeData {
		public Object singleVal;
		public List<Object> enumeration;
		public Object from, to, step;
		private IParameter p;
		public RangeData(IParameter parameter) {
			p = parameter;
		}
		public String serialize() {
			StringBuilder sb = new StringBuilder();
			
			if (singleVal != null) {
				sb.append("\t\t\t\t<singleVal>");
				sb.append("<![CDATA[").append(p.serialize(singleVal)).append("]]>");
				sb.append("</singleVal>\n");
			}
			if (from != null && to != null && step != null) {
				sb.append("\t\t\t\t<from>");
				sb.append("<![CDATA[").append(p.serialize(from)).append("]]>");
				sb.append("</from>\n");
				sb.append("\t\t\t\t<to>");
				sb.append("<![CDATA[").append(p.serialize(to)).append("]]>");
				sb.append("</to>\n");
				sb.append("\t\t\t\t<step>");
				sb.append("<![CDATA[").append(p.serialize(step)).append("]]>");
				sb.append("</step>\n");
			}
			if (enumeration != null) {
				sb.append("\t\t\t\t<enum>\n");
				for (Object o : enumeration) {
					sb.append("\t\t\t\t\t<val>");
					sb.append("<![CDATA[").append(p.serialize(o)).append("]]>");
					sb.append("</val>\n");
				}
				sb.append("\t\t\t\t</enum>\n");
			}
			sb.append("");
			
			return sb.toString();
		}
		public boolean isSingle() {
			return singleVal != null;
		}
		public boolean isEnumed() {
			return enumeration != null;
		}
	}
	
	public static interface IRange extends Iterable<Object> {
		public String getParamName();
		public String getDescription();
		public RangeData getRangeData();
	}
	
	public static class DefaultGraph implements IGraph {
		private String name, abscissa, ordinate, desc;
		private Map<String, Object> cfam;
		private IAlgorithm algorithm;
		public DefaultGraph() {
			cfam = new HashMap<String, Object>();
		}
		public String getAbscissaName() {
			return abscissa;
		}
		public Map<String, Object> getCurveFamily() {
			return cfam;
		}
		public String getDescription() {
			return desc;
		}
		public String getGraphName() {
			return name;
		}
		public String getOrdinateName() {
			return ordinate;
		}
		public void setName(String n) {
			name = n;
		}
		public void setAbscissa(String a) {
			abscissa = a;
		}
		public void setOrdinate(String o) {
			ordinate = o;
		}
		public void setDescription(String d) {
			desc = d;
		}
		public void algWithModel(Object m) {
			algorithm = ((Model)m).algorithm.get();
		}
		public void addCurveFamilyEntry(CFam cf) {
			Object val = algorithm.getParameter(cf.getName()).deserialize(cf.objectString);
			cfam.put(cf.getName(), val);
		}
	}
	
	public static class CFam {
		private String name;
		private String objectString;
		public void setName(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public void setObjectString(String objectString) {
			this.objectString = objectString;
		}
		public String getObjectString() {
			return objectString;
		}
	}
	
	public static class DefaultRange implements IRange {
		private RangeData data;
		private String paramname, desc;
		private IParameter p;
		private IAlgorithm algorithm;
		public DefaultRange() {
		}
		public void algWithModel(Object m) {
			algorithm = ((Model)m).algorithm.get();
		}
		public void setSingleVal(String s) {
			data.singleVal = p.deserialize(s);
		}
		public void setFrom(String s) {
			data.from = p.deserialize(s);
		}
		public void setTo(String s) {
			data.to = p.deserialize(s);
		}
		public void setStep(String s) {
			data.step = p.deserialize(s);
		}
		public void addToEnum(String s) {
			if (data.enumeration == null) data.enumeration = new ArrayList<Object>();
			data.enumeration.add(p.deserialize(s));
		}
		public void setDescription(String d) {
			desc = d;
		}
		public void setParamName(String name) {
			paramname = name;
			p = algorithm.getParameter(paramname);
			data = new RangeData(p);
		}
		public void setRangeData(RangeData rd) {
			data = rd;
		}
		public String getDescription() {
			return desc;
		}
		public String getParamName() {
			return paramname;
		}
		public RangeData getRangeData() {
			return data;
		}
		public Iterator<Object> iterator() {
			if (data.isSingle()) return new Iterator<Object>() {
				private Object o = data.singleVal;
				public boolean hasNext() {
					return o != null;
				}
				public Object next() {
					Object toret = o;
					o = null;
					return toret;
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			}; else if (data.isEnumed()) return data.enumeration.iterator();
			else {
				switch (p.getParamType()) {
				case INTEGER:
					return new IntegerStepRange((Integer)data.from, (Integer)data.step, (Integer)data.to, p).iterator();
				case REAL:
					return new RealStepRange((Double)data.from, (Double)data.to, (Double)data.step, p).iterator();
				case TIME:
					return new TimeStepRange((Time)data.from, (Time)data.step, (Time)data.to, p).iterator();
				}
				throw new IllegalStateException("Cannot create step range for: " + paramname);
			}
		}
	}
	
	/* private fields */
	public final Prop<IAlgorithm> algorithm = new Prop<IAlgorithm>();
	public final Prop<Integer> runsPerSet = new Prop<Integer>();
	public final Prop<String> name = new Prop<String>();
	public final ListProp<IParameter> parameters = new ListProp<IParameter>(null);
	public final ListProp<IRange> ranges = new ListProp<IRange>(new ArrayList<IRange>());
	public final ListProp<ReturnHandler> returnhandlers = new ListProp<ReturnHandler>(new ArrayList<ReturnHandler>());
	public final ListProp<AdditionalHandler> addithandlers = new ListProp<AdditionalHandler>(new ArrayList<AdditionalHandler>());
	public final ListProp<IGraph> graphs = new ListProp<IGraph>(new ArrayList<IGraph>());
	public final ListProp<Object[]> gnuplots = new ListProp<Object[]>(new ArrayList<Object[]>());

	/* ctors */

	/* methods */
	
	@SuppressWarnings("unchecked")
	public String serialize() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<AlgorithmSchedule>\n");
		
		sb.append("\t<algorithm>");
		sb.append(algorithm.get().getClass().getName());
		sb.append("</algorithm>\n");
		
		sb.append("\t<runsPerSet>");
		sb.append(runsPerSet.get());
		sb.append("</runsPerSet>\n");
		
		sb.append("\t<name>");
		sb.append(name.get());
		sb.append("</name>\n");
		
		sb.append("\t<ranges><cl></cl>\n");
		for (IRange range : ranges.get()) {
			sb.append("\t\t<r>\n");
			sb.append("\t\t\t<alg></alg>\n");
			sb.append("\t\t\t<name>").append(range.getParamName()).append("</name>\n");
			sb.append("\t\t\t<desc>").append(range.getDescription()).append("</desc>\n");
			sb.append("\t\t\t<data>\n").append(range.getRangeData().serialize()).append("\t\t\t</data>\n");
			sb.append("\t\t</r>\n");
		}
		sb.append("\t</ranges>\n");
		
		sb.append("\t<handlers><cl></cl>\n");
		for (ReturnHandler h : returnhandlers.get()) {
			sb.append("\t\t<h>\n");
			sb.append("\t\t\t<name>").append(h.getParamname()).append("</name>\n");
			if (h.getBoundTo() != null) sb.append("\t\t\t<boundto>").append(h.getBoundTo()).append("</boundto>\n");
			sb.append("\t\t\t<handler>").append(h.getHandler()).append("</handler>\n");
			sb.append("\t\t</h>\n");
		}
		sb.append("\t</handlers>\n");
		
		sb.append("\t<addit_handlers><cl></cl>\n");
		for (AdditionalHandler h : addithandlers.get()) {
			sb.append("\t\t<h>\n");
			sb.append("\t\t\t<name>").append(h.getHandlerName()).append("</name>\n");
			sb.append("\t\t\t<valuename>").append(h.getActualValueName()).append("</valuename>\n");
			sb.append("\t\t\t<handler>").append(h.getHandler()).append("</handler>\n");
			sb.append("\t\t</h>\n");
		}
		sb.append("\t</addit_handlers>\n");
		
		sb.append("\t<graphs><cl></cl>\n");
		for (IGraph g : graphs.get()) {
			sb.append("\t\t<g>\n");
			sb.append("\t\t\t<alg></alg>\n");
			sb.append("\t\t\t<name>").append(g.getGraphName()).append("</name>\n");
			sb.append("\t\t\t<abscissa>").append(g.getAbscissaName()).append("</abscissa>\n");
			sb.append("\t\t\t<ordinate>").append(g.getOrdinateName()).append("</ordinate>\n");
			sb.append("\t\t\t<desc>").append(g.getDescription()).append("</desc>\n");
			sb.append("\t\t\t<cfamily>\n").append(serializeCurveFamily(g.getCurveFamily())).append("\t\t\t</cfamily>\n");
			sb.append("\t\t</g>\n");
		}
		sb.append("\t</graphs>\n");
		
		sb.append("\t<gnuplots><cl></cl>\n");
		for (Object[] of : gnuplots.get()) {
			sb.append("\t\t<gp>\n");
			sb.append("\t\t\t<filename>").append(of[0]).append("</filename>\n");
			sb.append("\t\t\t<columns>");
			for (String[] sf : (List<String[]>)of[1]) {
				if (sf[1] != null) sb.append("<rv>").append(sf[0]).append("</rv>");
				else sb.append("<iv>").append(sf[0]).append("</iv>");
			}
			sb.append("</columns>\n");
			sb.append("\t\t</gp>\n");
		}
		sb.append("\t</gnuplots>\n");
		
		sb.append("</AlgorithmSchedule>");
		
		return sb.toString();
	}
	
	private String serializeCurveFamily(Map<String, Object> curvefamily) {
		StringBuilder sb = new StringBuilder();
		
		for (Entry<String, Object> ntr : curvefamily.entrySet()) {
			sb.append("\t\t\t\t<ntr><k>").append(ntr.getKey()).append("</k>");
			sb.append("<v><![CDATA[");
			sb.append(algorithm.get().getParameter(ntr.getKey()).serialize(ntr.getValue()));
			sb.append("]]></v></ntr>\n");
		}
		
		return sb.toString();
	}
	
	public void setAlgorithm(String clsname, hr.fer.zemris.ga_framework.model.Model appModel) {
		try {
			IAlgorithm alg = (IAlgorithm) appModel.getClass(clsname).newInstance();
			algorithm.set(alg);
			parameters.set(alg.getParameters());
			ParameterDialogFactory.registerDialogs(alg.getEditors());
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public void setRunsPerSet(String runs) {
		try {
			runsPerSet.set(Integer.valueOf(runs));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public void setName(String n) {
		name.set(n);
	}
	
	public void clearRanges() {
		ranges.clear();
	}
	
	public void addRange(DefaultRange range) {
		ranges.add(range);
	}
	
	public void clearHandlers() {
		returnhandlers.clear();
	}
	
	public void clearAdditionalHandlers() {
		addithandlers.clear();
	}
	
	public void addHandler(ReturnHandler handler) {
		returnhandlers.add(handler);
	}
	
	public void addAdditionalHandler(AdditionalHandler handler) {
		addithandlers.add(handler);
	}
	
	public void clearGraphs() {
		graphs.clear();
	}
	
	public void clearGnuPlots() {
		gnuplots.clear();
	}
	
	public void addGraph(DefaultGraph graph) {
		graphs.add(graph);
	}
	
	public void addGnuPlot(GnuPlotData gnuplot) {
		gnuplots.add(new Object[]{gnuplot.getFilename(), gnuplot.getCols()});
	}
	
	public void algLikeThisCauseSetAlgorithmDoesNotWorkForSomeBloodyReason(String s, Object o) {
//		System.out.println(s + " - " + o);
		setAlgorithm(s, (hr.fer.zemris.ga_framework.model.Model) o);
	}
	
	public void deserialize(InputStream is, hr.fer.zemris.ga_framework.model.Model appModel) throws IllegalArgumentException {
		Digester d = new Digester();
		
		// basic
		d.push(this);
		d.addCallMethod("AlgorithmSchedule/algorithm", "algLikeThisCauseSetAlgorithmDoesNotWorkForSomeBloodyReason", 2);
		d.addCallParam("AlgorithmSchedule/algorithm", 0);
		d.addObjectParam("AlgorithmSchedule/algorithm", 1, appModel);
		d.addBeanPropertySetter("AlgorithmSchedule/runsPerSet", "runsPerSet");
		d.addBeanPropertySetter("AlgorithmSchedule/name", "name");
		
		// ranges
		d.addCallMethod("AlgorithmSchedule/ranges/cl", "clearRanges");
		d.addObjectCreate("AlgorithmSchedule/ranges/r", DefaultRange.class);
		d.addCallMethod("AlgorithmSchedule/ranges/r/alg", "algWithModel", 1);
		d.addObjectParam("AlgorithmSchedule/ranges/r/alg", 0, this);
		d.addBeanPropertySetter("AlgorithmSchedule/ranges/r/name", "paramName");
		d.addBeanPropertySetter("AlgorithmSchedule/ranges/r/desc", "description");
		d.addBeanPropertySetter("AlgorithmSchedule/ranges/r/data/singleVal", "singleVal");
		d.addBeanPropertySetter("AlgorithmSchedule/ranges/r/data/from", "from");
		d.addBeanPropertySetter("AlgorithmSchedule/ranges/r/data/to", "to");
		d.addBeanPropertySetter("AlgorithmSchedule/ranges/r/data/step", "step");
		d.addCallMethod("AlgorithmSchedule/ranges/r/data/enum/val", "addToEnum", 1);
		d.addCallParam("AlgorithmSchedule/ranges/r/data/enum/val", 0);
		d.addSetNext("AlgorithmSchedule/ranges/r", "addRange");
		
		// handlers
		d.addCallMethod("AlgorithmSchedule/handlers/cl", "clearHandlers");
		d.addObjectCreate("AlgorithmSchedule/handlers/h", ReturnHandler.class);
		d.addBeanPropertySetter("AlgorithmSchedule/handlers/h/name", "paramname");
		d.addBeanPropertySetter("AlgorithmSchedule/handlers/h/handler", "handler");
		d.addBeanPropertySetter("AlgorithmSchedule/handlers/h/boundto", "boundTo");
		d.addSetNext("AlgorithmSchedule/handlers/h", "addHandler");
		
		// additional handlers
		d.addCallMethod("AlgorithmSchedule/addit_handlers/cl", "clearAdditionalHandlers");
		d.addObjectCreate("AlgorithmSchedule/addit_handlers/h", AdditionalHandler.class);
		d.addBeanPropertySetter("AlgorithmSchedule/addit_handlers/h/name", "handlerName");
		d.addBeanPropertySetter("AlgorithmSchedule/addit_handlers/h/valuename", "actualValueName");
		d.addBeanPropertySetter("AlgorithmSchedule/addit_handlers/h/handler", "handler");
		d.addSetNext("AlgorithmSchedule/addit_handlers/h", "addAdditionalHandler");
		
		// graphs
		d.addCallMethod("AlgorithmSchedule/graphs/cl", "clearGraphs");
		d.addObjectCreate("AlgorithmSchedule/graphs/g", DefaultGraph.class);
		d.addSetNext("AlgorithmSchedule/graphs/g", "addGraph");
		d.addCallMethod("AlgorithmSchedule/graphs/g/alg", "algWithModel", 1);
		d.addObjectParam("AlgorithmSchedule/graphs/g/alg", 0, this);
		d.addBeanPropertySetter("AlgorithmSchedule/graphs/g/name", "name");
		d.addBeanPropertySetter("AlgorithmSchedule/graphs/g/abscissa", "abscissa");
		d.addBeanPropertySetter("AlgorithmSchedule/graphs/g/ordinate", "ordinate");
		d.addBeanPropertySetter("AlgorithmSchedule/graphs/g/desc", "description");
		d.addObjectCreate("AlgorithmSchedule/graphs/g/cfamily/ntr", CFam.class);
		d.addBeanPropertySetter("AlgorithmSchedule/graphs/g/cfamily/ntr/k", "name");
		d.addBeanPropertySetter("AlgorithmSchedule/graphs/g/cfamily/ntr/v", "objectString");
		d.addSetNext("AlgorithmSchedule/graphs/g/cfamily/ntr", "addCurveFamilyEntry");
		
		// gnuplots
		d.addCallMethod("AlgorithmSchedule/gnuplots/cl", "clearGnuPlots");
		d.addObjectCreate("AlgorithmSchedule/gnuplots/gp", GnuPlotData.class);
		d.addSetNext("AlgorithmSchedule/gnuplots/gp", "addGnuPlot");
		d.addBeanPropertySetter("AlgorithmSchedule/gnuplots/gp/filename", "filename");
		d.addCallMethod("AlgorithmSchedule/gnuplots/gp/columns/iv", "addInputValue", 1);
		d.addCallParam("AlgorithmSchedule/gnuplots/gp/columns/iv", 0);
		d.addCallMethod("AlgorithmSchedule/gnuplots/gp/columns/rv", "addReturnValue", 1);
		d.addCallParam("AlgorithmSchedule/gnuplots/gp/columns/rv", 0);

		// those are the rules to parse this, for instance:
		//		<AlgorithmSchedule>
		//			<algorithm>hr.fer.zemris.ga_framework.algorithms.tsp.TournamentTSP</algorithm>
		//			<runsPerSet>1</runsPerSet>
		//			<name>Algorithm Scheduler: Tournament TSP</name>
		//			<ranges>
		//				<r>
		//					<alg></alg>
		//					<name>Expected cost</name>
		//					<desc>Single value: 0.0</desc>
		//					<data>
		//						<singleVal><![CDATA[0.0]]></singleVal>
		//					</data>
		//				</r>
		//			</ranges>
		//			<handlers>
		//				<h>
		//					<name>Iterations made</name>
		//					<handler>Average</handler>
		//				</h>
		//			</handlers>
		//			<graphs>
		//				<g>
		//					<alg></alg>
		//					<name>Graph121</name>
		//					<abscissa>Mutation probability</abscissa>
		//					<ordinate>Iterations made</ordinate>
		//					<desc>Mutation probability - Iterations made, values: Crossover operator = Order</desc>
		//					<cfamily>
		//						<ntr><k>Crossover operator</k><v><![CDATA[Order]]></v></ntr>
		//					</cfamily>
		//				</g>
		//			</graphs>
		//			<gnuplots>
		//				<gp>
		//					<filename>abcde</filename>
		//					<columns><iv>Mutation probability</iv><rv>Iterations made</rv></columns>
		//				</gp>
		//			</gnuplots>
		//		</AlgorithmSchedule>
		
		try {
			d.parse(is);
		} catch (Exception e) {
			throw new IllegalArgumentException("Input stream could not be parsed", e);
		}
	}

}














