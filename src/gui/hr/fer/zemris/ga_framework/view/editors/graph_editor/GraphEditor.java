package hr.fer.zemris.ga_framework.view.editors.graph_editor;

import hr.fer.zemris.ga_framework.controller.CommandResult;
import hr.fer.zemris.ga_framework.controller.Events;
import hr.fer.zemris.ga_framework.controller.IController;
import hr.fer.zemris.ga_framework.model.ConstraintTypes;
import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.model.IGraph;
import hr.fer.zemris.ga_framework.model.IInfoListener;
import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.IParameterInventory;
import hr.fer.zemris.ga_framework.model.IValue;
import hr.fer.zemris.ga_framework.model.RunningAlgorithmInfo;
import hr.fer.zemris.ga_framework.model.impl.Value;
import hr.fer.zemris.ga_framework.model.misc.Pair;
import hr.fer.zemris.ga_framework.model.misc.Time;
import hr.fer.zemris.ga_framework.model.misc.Time.Metric;
import hr.fer.zemris.ga_framework.view.Editor;
import hr.fer.zemris.ga_framework.view.ImageLoader;
import hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler.Model.AdditionalHandler;
import hr.fer.zemris.ga_framework.view.editors.graph_editor.GraphInfoSerializer.SWrapper;
import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.TracePoint2D;
import info.monitorenter.gui.chart.axis.AAxis;
import info.monitorenter.gui.chart.axis.AxisLinear;
import info.monitorenter.gui.chart.labelformatters.ALabelFormatter;
import info.monitorenter.gui.chart.labelformatters.LabelFormatterNumber;
import info.monitorenter.gui.chart.traces.Trace2DSorted;
import info.monitorenter.gui.chart.traces.painters.ATracePainter;
import info.monitorenter.gui.chart.traces.painters.TracePainterLine;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


public class GraphEditor extends Editor {
	
	/* static fields */
	private static final ImageData IMAGE_ICON = ImageLoader.loadImage("icons", "chart_curve.png");
	private static final Map<String, String> SAVE_EXTENSION_MAP = new LinkedHashMap<String, String>();
	private static final Events[] EVENTS = new Events[]{};
	private static final Color[] BASIC_COLORS = new Color[]{
		new Color(220, 50, 50), new Color(50, 50, 220), new Color(50, 220, 50),
		new Color(220, 220, 50), new Color(220, 50, 220), new Color(50, 220, 220),
	};
	public static final String DEFAULT_EXTENSION = "graph";
	
	static {
		SAVE_EXTENSION_MAP.put(DEFAULT_EXTENSION, "Save graph information");
		SAVE_EXTENSION_MAP.put("png", "Save graph as picture");
	}

	/* private fields */
	/* model */
	private IGraph graph;
	private IAlgorithm algorithm;
	private IParameterInventory inventory;
	
	/* gui*/
	private volatile boolean isAntiAliasing;
	private Table valsToShowTable;
	private Table otherParamValuesTable;
	private List<CCombo> otherParamValuesCombos;
	private CCombo inputValueCombo;
	private Table retValTable;
	private Chart2D chart;
	private Label backgroundColorLabel;
	private Label gridColorLabel;
	private Label axisColorLabel;
	private ScrolledComposite scrolledComposite;
	private Frame chFrame;
	private Table table;
	private Button antialiasingButton;
	private Button fitToAreaButton;
	private Button predefinedButton;
	private Spinner yspinner;
	private Spinner xspinner;
	private Composite chartComposite;
	private Map<String, ITrace2D> tracemap;
	
	/* model */
	

	/* ctors */

	public GraphEditor(Composite parent, IController ctrl, long id, CommandResult result) {
		super(parent, SWT.NONE, ctrl, id, EVENTS);
		
		isAntiAliasing = true;
		
		initGUI();
		advinitGUI(result);
	}

	/* methods */
	
	private String colorToString(Color c) {
		StringBuilder sb = new StringBuilder();
		if (c.getRed() < 16) sb.append("0");
		sb.append(Integer.toHexString(c.getRed()));
		if (c.getGreen() < 16) sb.append("0");
		sb.append(Integer.toHexString(c.getGreen()));
		if (c.getBlue() < 16) sb.append("0");
		sb.append(Integer.toHexString(c.getBlue()));
		return sb.toString();
	}

	private void advinitGUI(CommandResult result) {
		chFrame = SWT_AWT.new_Frame(chartComposite);
		chart = new Chart2D() {
			private static final long serialVersionUID = -2636034793603726157L;
			@Override
			public synchronized void paint(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				if (isAntiAliasing) {
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				}
				super.paint(g);
			}
		};
		chFrame.setLayout(new BorderLayout());
		chFrame.add(chart, BorderLayout.CENTER);
		
		// configure controls
		backgroundColorLabel.setText("Back: " + colorToString(chart.getBackground()));
		axisColorLabel.setText("Axis: " + colorToString(chart.getForeground()));
		gridColorLabel.setText("Grid: " + colorToString(chart.getGridColor()));
		
		// configure chart2d
		RunningAlgorithmInfo nfo = (RunningAlgorithmInfo) result.msg(Events.KEY.JOB_INFO);
		graph = (IGraph) result.msg(Events.KEY.GRAPH_OBJECT);
		algorithm = (IAlgorithm) result.msg(Events.KEY.ALGORITHM_OBJECT);
		if (nfo != null) {
			inventory = findInv(algorithm, nfo.getJobs());
			initChart2d(inventory);
		} else return;

		// rebuild table
		rebuildTraceTable();
		rebuildTableTab();
	}
	
	private void rebuildTableTab() {
		// fill input parameter select combo
		inputValueCombo.removeAll();
		inputValueCombo.setEditable(false);
		for (IParameter p : algorithm.getParameters()) {
			inputValueCombo.add(p.getName());
		}
		
		// fill parameter fixed values table
		otherParamValuesTable.removeAll();
		if (otherParamValuesCombos == null) otherParamValuesCombos = new ArrayList<CCombo>();
		else {
			// dispose old combos
			for (CCombo c : otherParamValuesCombos) {
				c.dispose();
			}
			otherParamValuesCombos.clear();
		}
		for (IParameter p : algorithm.getParameters()) {
			TableItem item = new TableItem(otherParamValuesTable, SWT.NONE);
			item.setText(0, p.getName());
			
			TableEditor ted = new TableEditor(otherParamValuesTable);
			
			CCombo combo = new CCombo(otherParamValuesTable, SWT.NONE);
			combo.setEditable(false);
			combo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			fillComboWithPossibleValues(combo, p);
			combo.select(0);
			combo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					rebuildRetValsTable();
				}
			});
			otherParamValuesCombos.add(combo);
			
			ted.grabHorizontal = true;
			ted.setEditor(combo, item, 1);
		}
		
		// fill the values to show table with data
		valsToShowTable.removeAll();
		for (IParameter rv : algorithm.getReturnValues()) {
			TableItem item = new TableItem(valsToShowTable, SWT.NONE);
			item.setText(rv.getName());
			item.setChecked(false);
		}
		for (AdditionalHandler ah : inventory.getAdditionalHandlers()) {
			TableItem item = new TableItem(valsToShowTable, SWT.NONE);
			item.setText(ah.getHandlerName());
			item.setChecked(false);
		}
	}

	private void fillComboWithPossibleValues(CCombo combo, IParameter p) {
		List<Object> possibleValues = extractPossibleValues(p.getName());
		combo.setData(possibleValues);
		for (Object o : possibleValues) {
			combo.add(o.toString());
		}
	}

	private List<Object> extractPossibleValues(String paramname) {
		Set<Object> pvset = new HashSet<Object>();
		List<Object> pvlist = new ArrayList<Object>();
		
		for (Map<String, IValue> map : inventory) {
			Object o = map.get(paramname).value();
			if (pvset.contains(o)) continue;
			pvset.add(o);
			pvlist.add(o);
		}
		
		return pvlist;
	}

	private void initChart2d(IParameterInventory inventory) {
		fillChart(inventory);
		RGB rgb = this.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND).getRGB();
		chart.setBackground(new Color(rgb.red, rgb.green, rgb.blue));
		scrolledComposite.setBackground(new org.eclipse.swt.graphics.Color(this.getDisplay(), rgb));
	}

	private void rebuildTraceTable() {
		for (final Entry<String, ITrace2D> ntr : tracemap.entrySet()) {
			final TableItem item = new TableItem(table, SWT.NONE);
			
			item.setText(ntr.getKey());
			item.setChecked(true);
			
			// create combos for changing color and stroke
			TableEditor coledit = new TableEditor(table);
			coledit.grabHorizontal = true;
			Composite comp = new Composite(table, SWT.NONE);
			GridLayout gl = new GridLayout();
			gl.horizontalSpacing = 0;
			gl.verticalSpacing = 0;
			gl.marginHeight = 0;
			gl.marginWidth = 0;
			gl.numColumns = 2;
			comp.setLayout(gl);
			Label collab = new Label(comp, SWT.NONE);
			collab.setText(colorToString(ntr.getValue().getColor()));
			collab.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			collab.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			Button chcol = new Button(comp, SWT.ARROW | SWT.DOWN);
			chcol.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseUp(MouseEvent e) {
					Color old = ntr.getValue().getColor();
					RGB rgb = new RGB(old.getRed(), old.getGreen(), old.getBlue());
					ColorDialog cdial = new ColorDialog(GraphEditor.this.getShell());
					cdial.setText("Choose trace color");
					cdial.setRGB(rgb);
					
					rgb = cdial.open();
					if (rgb != null) {
						ntr.getValue().setColor(new Color(rgb.red, rgb.green, rgb.blue));
						item.setText(1, colorToString(ntr.getValue().getColor()));
					}
				}
			});
			coledit.setEditor(comp, item, 1);
			
			TableEditor stredit = new TableEditor(table);
			stredit.grabHorizontal = true;
			final CCombo scombo = new CCombo(table, SWT.NONE);
			scombo.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			scombo.setEditable(false);
			scombo.add("Solid");
			scombo.add("Dashed");
			BasicStroke stroke = (BasicStroke)ntr.getValue().getStroke();
			if (stroke != null && stroke.getDashArray() != null && stroke.getDashArray().length > 1) 
				scombo.select(1);
			else scombo.select(0);
			scombo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					int sel = scombo.getSelectionIndex();
					if (sel == -1) return;
					
					switch (sel) {
					case 0:
						ntr.getValue().setStroke(new BasicStroke(1.f));
						break;
					case 1:
						ntr.getValue().setStroke(new BasicStroke(1.f, BasicStroke.CAP_ROUND,
								BasicStroke.JOIN_ROUND, 10.f, new float[]{2.f, 2.f}, 1.f));
						break;
					}
				}
			});
			stredit.setEditor(scombo, item, 2);
			
			TableEditor pedit = new TableEditor(table);
			pedit.grabHorizontal = true;
			final CCombo pcombo = new CCombo(table, SWT.NONE);
			pcombo.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			pcombo.setEditable(false);
			pcombo.add("None");
			pcombo.add("Triangles");
			pcombo.add("Circles");
			pcombo.add("Squares");
			pcombo.add("Crosses");
			pcombo.select(0);
			pcombo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					int sel = pcombo.getSelectionIndex();
					if (sel == -1) return;
					
					switch (sel) {
					case 0:
						ntr.getValue().setTracePainter(new TracePainterLine());
						break;
					case 1:
						ntr.getValue().setTracePainter(new ATracePainter() {
							private static final long serialVersionUID = 1L;
							@Override
							public void paintPoint(int absoluteX,
									int absoluteY, int nextX, int nextY,
									Graphics2D g, TracePoint2D original)
							{
								g.drawLine(absoluteX, absoluteY, nextX, nextY);
								g.fillPolygon(new Polygon(
										new int[]{absoluteX - 4, absoluteX, absoluteX + 4},
										new int[]{absoluteY + 4, absoluteY - 4, absoluteY + 4},
										3));
								g.fillPolygon(new Polygon(
										new int[]{nextX - 4, nextX, nextX + 4},
										new int[]{nextY + 4, nextY - 4, nextY + 4},
										3));
//								g.drawLine(absoluteX - 3, absoluteY + 3, absoluteX + 3, absoluteY + 3);
//								g.drawLine(absoluteX - 3, absoluteY + 3, absoluteX, absoluteY - 3);
//								g.drawLine(absoluteX + 3, absoluteY + 3, absoluteX, absoluteY - 3);
							}
						});
						break;
					case 2:
						ntr.getValue().setTracePainter(new ATracePainter() {
							private static final long serialVersionUID = 1L;
							@Override
							public void paintPoint(int absoluteX,
									int absoluteY, int nextX, int nextY,
									Graphics2D g, TracePoint2D original)
							{
								g.drawLine(absoluteX, absoluteY, nextX, nextY);
								g.fillOval(absoluteX - 4, absoluteY - 4, 8, 8);
								g.fillOval(nextX - 4, nextY - 4, 8, 8);
							}
						});
						break;
					case 3:
						ntr.getValue().setTracePainter(new ATracePainter() {
							private static final long serialVersionUID = 1L;
							@Override
							public void paintPoint(int absoluteX,
									int absoluteY, int nextX, int nextY,
									Graphics2D g, TracePoint2D original)
							{
								g.drawLine(absoluteX, absoluteY, nextX, nextY);
								g.fillRect(absoluteX - 4, absoluteY - 4, 8, 8);
								g.fillRect(nextX - 4, nextY - 4, 8, 8);
							}
						});
						break;
					case 4:
						ntr.getValue().setTracePainter(new ATracePainter() {
							private static final long serialVersionUID = 1L;
							@Override
							public void paintPoint(int absoluteX,
									int absoluteY, int nextX, int nextY,
									Graphics2D g, TracePoint2D original)
							{
								g.drawLine(absoluteX, absoluteY, nextX, nextY);
								g.drawLine(absoluteX - 4, absoluteY - 4, absoluteX + 4, absoluteY + 4);
								g.drawLine(absoluteX - 4, absoluteY + 4, absoluteX + 4, absoluteY - 4);
								g.drawLine(nextX - 4, nextY - 4, nextX + 4, nextY + 4);
								g.drawLine(nextX - 4, nextY + 4, nextX + 4, nextY - 4);
							}
						});
						break;
					}
				}
			});
			pedit.setEditor(pcombo, item, 3);
		}
	}

	private IParameterInventory findInv(IAlgorithm alg, List<Pair<IAlgorithm, IParameterInventory>> lst) {
		for (Pair<IAlgorithm, IParameterInventory> pair : lst) {
			if (pair.first.equals(alg)) return pair.second;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private void fillChart(IParameterInventory inventory) {
		// enumerate non-fixed parameters
		int index = 0;
		Map<String, Object> family = graph.getCurveFamily();
		List<String> nonfixed = new ArrayList<String>();
		for (IParameter p : algorithm.getParameters()) {
			String name = p.getName();
			if (name.equals(graph.getAbscissaName())) continue;
			if (!family.containsKey(name)) nonfixed.add(name);
		}
		
		// create axis according to parameter type
		chart.setAxisXBottom(createAxis(algorithm.getParameter(graph.getAbscissaName()), false));
		chart.setAxisYLeft(createAxis(algorithm.getReturnValue(graph.getOrdinateName()), true));
		
		// iterate through all the values in the inventory
		// and create traces for the chart
		int r = 240, g = 120, b = 0;
		Map<Map<String, Object>, ITrace2D> traces = new LinkedHashMap<Map<String,Object>, ITrace2D>();
		tracemap = new LinkedHashMap<String, ITrace2D>();
		Map<String, List<IValue[]>> points = new HashMap<String, List<IValue[]>>();
		ArrayList<Comparable<Object>> xlist = new ArrayList<Comparable<Object>>(), ylist = new ArrayList<Comparable<Object>>();
		for (Map<String, IValue> paramset : inventory)  {
			// check if this parameter set should be analysed
			// that is - if the parameter set contains a value
			// which isn't within the family, it should be skipped
			boolean shouldskip = false;
			for (Entry<String, Object> pntr : family.entrySet()) {
				IValue val = paramset.get(pntr.getKey());
				if (!val.value().equals(pntr.getValue())) {
					shouldskip = true;
					break;
				}
			}
			if (shouldskip) continue;
			
			// extract non-fixed parameter values
			Map<String, Object> nfmap = new LinkedHashMap<String, Object>();
			for (String s : nonfixed) {
				nfmap.put(s, paramset.get(s).value());
			}
			ITrace2D currtrace;
			if ((currtrace = traces.get(nfmap)) == null) {
				index++;
				currtrace = new Trace2DSorted();
				currtrace.setName(createTraceName(nfmap, index));
				List<IValue[]> pointlist = new ArrayList<IValue[]>();
				points.put(currtrace.getName(), pointlist);
				
				// set color
				r = (r + 60) % 240;
				g = (g + 100) % 240;
				b = (b + 90) % 240;
				if (traces.size() < BASIC_COLORS.length) {
					currtrace.setColor(BASIC_COLORS[traces.size()]);
				} else {
					currtrace.setColor(new Color(r, g, b));
					currtrace.setStroke(new BasicStroke(1.f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
							1.f, new float[]{2.f, 2.f}, 0.f));
				}
				
				// add trace to map
				traces.put(nfmap, currtrace);
				tracemap.put(currtrace.getName(), currtrace);
			}
			
			// get return value set
			Map<String, IValue> retset = inventory.getReturnValues(paramset);
			IValue xval = paramset.get(graph.getAbscissaName());
			IValue yval = retset.get(graph.getOrdinateName());
			points.get(currtrace.getName()).add(new IValue[]{xval, yval});
			xlist.add((Comparable<Object>) xval.value());
			ylist.add((Comparable<Object>) yval.value());
		}
		Collections.sort(xlist);
		Collections.sort(ylist);
		Map<Object, Double> xmap = new HashMap<Object, Double>();
		Map<Object, Double> ymap = new HashMap<Object, Double>();
		Double counter = 0.0;
		for (Object o : xlist) {
			xmap.put(o, counter);
			counter += 1;
		}
		counter = 0.0;
		for (Object o : ylist) {
			ymap.put(o, counter);
			counter += 1;
		}
		for (ITrace2D trace : traces.values()) {
			List<IValue[]> lst = points.get(trace.getName());
			for (IValue[] xy : lst) {
				double x = calculateVal(xy[0], xmap);
				double y = calculateVal(xy[1], ymap);
				trace.addPoint(x, y);
			}
			
			chart.addTrace(trace);
		}
	}
	
	private double calculateVal(IValue val, Map<Object, Double> sortedmap) {
		if (val.parameter().getConstraint() == ConstraintTypes.ENUMERATION) {
			return val.parameter().getAllowed().indexOf(val.value());
		} else {
			switch (val.parameter().getParamType()) {
			case BOOLEAN:
				if ((Boolean)val.value()) return 1;
				else return 0;
			case INTEGER:
				return (Integer)val.value();
			case REAL:
				return (Double)val.value();
			case TIME:
				Time t = (Time)val.value();
				return t.convertTo(Metric.s).getInterval();
			case STRING:
			case ISERIALIZABLE:
				return sortedmap.get(val.value());
			}
		}
		
		throw new IllegalStateException("Shouldn't be here.");
	}

	private AAxis createAxis(IParameter param, boolean isReturnValue) {
		AAxis axis = new AxisLinear();
		
		axis.setAxisTitle(new IAxis.AxisTitle(param.getName()));
		axis.setPaintGrid(true);
		axis.setFormatter(new LabelFormatterNumber());
		
		// if this value is:
		// - input value, it's enumerated, and does not support arithmetic
		// - return value, is enumerated, and does not support arithmetic
		// than handle this value differently - as an enumeration
		if (param.getConstraint() == ConstraintTypes.ENUMERATION &&
				(!param.getParamType().isSimpleArithmeticType())) {
			final List<Object> allowed = param.getAllowed();
			axis.setFormatter(new ALabelFormatter() {
				private static final long serialVersionUID = -7348464540103886927L;
				public String format(double d) {
					if (d < 0 || d >= allowed.size()) return "";
					return allowed.get((int)d).toString();
				}
				public double getMinimumValueShiftForChange() {
					return 1;
				}

				public double getNextEvenValue(double d, boolean ceiling) {
					if (ceiling) {
						return (int)d + 1;
					} else {
						return (int)d;
					}
				}

				public Number parse(String s) throws NumberFormatException {
					int i = -1;
					for (Object o : allowed) {
						i++;
						if (s.equals(o.toString())) return i;
					}
//					System.out.println(s);
					throw new NumberFormatException();
				}
			});
		}
		
		return axis;
	}
	
	private String createTraceName(Map<String, Object> nfmap, int index) {
		StringBuilder sb = new StringBuilder(index + ") ");
		boolean first = true;
		for (Entry<String, Object> ntr : nfmap.entrySet()) {
			if (first) first = false; else sb.append(", ");
			sb.append(ntr.getKey()).append(" = ").append(ntr.getValue());
		}
		return sb.toString();
	}

	private void initGUI() {
		final FillLayout fillLayout = new FillLayout();
		fillLayout.marginWidth = 2;
		fillLayout.marginHeight = 2;
		setLayout(fillLayout);

		final CTabFolder tabFolder = new CTabFolder(this, SWT.BORDER);

		final CTabItem graphTabItem = new CTabItem(tabFolder, SWT.NONE);
		graphTabItem.setText("Graph");

		final CTabItem tableTabItem = new CTabItem(tabFolder, SWT.NONE);
		tableTabItem.setText("Table");

		final SashForm sashForm_1 = new SashForm(tabFolder, SWT.NONE);
		sashForm_1.setSashWidth(2);

		final ViewForm viewForm_4 = new ViewForm(sashForm_1, SWT.FLAT | SWT.BORDER);

		final Composite composite_1 = new Composite(viewForm_4, SWT.NONE);
		final GridLayout gridLayout_3 = new GridLayout();
		gridLayout_3.verticalSpacing = 4;
		gridLayout_3.marginWidth = 4;
		gridLayout_3.marginHeight = 4;
		gridLayout_3.horizontalSpacing = 4;
		composite_1.setLayout(gridLayout_3);
		viewForm_4.setContent(composite_1);

		final Label chooseAnInputLabel = new Label(composite_1, SWT.WRAP);
		final GridData gd_chooseAnInputLabel = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		chooseAnInputLabel.setLayoutData(gd_chooseAnInputLabel);
		chooseAnInputLabel.setText("1) Choose an input parameter for which return values will be shown:");

		final ViewForm viewForm_5 = new ViewForm(composite_1, SWT.FLAT | SWT.BORDER);
		viewForm_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		inputValueCombo = new CCombo(viewForm_5, SWT.NONE);
		inputValueCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				int selind = inputValueCombo.getSelectionIndex();
				if (selind != -1) {
					setEnabledStateOfOtherValuesTable();
					rebuildRetValsTable();
				}
			}
		});
		inputValueCombo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		viewForm_5.setContent(inputValueCombo);

		final Label label = new Label(composite_1, SWT.WRAP);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		label.setText("2) Choose a fixed value for every other input parameter:");

		final ViewForm viewForm_6 = new ViewForm(composite_1, SWT.FLAT | SWT.BORDER);
		final GridData gd_viewForm_6 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_viewForm_6.heightHint = 280;
		viewForm_6.setLayoutData(gd_viewForm_6);

		otherParamValuesTable = new Table(viewForm_6, SWT.FULL_SELECTION);
		otherParamValuesTable.addControlListener(new ControlAdapter() {
			public void controlResized(final ControlEvent arg0) {
				int wdt1 = otherParamValuesTable.getColumn(0).getWidth();
				int wdtTotal = otherParamValuesTable.getSize().x - otherParamValuesTable.getBorderWidth() * 2;
				otherParamValuesTable.getColumn(1).setWidth(wdtTotal - wdt1);
			}
		});
		viewForm_6.setContent(otherParamValuesTable);
		otherParamValuesTable.setLinesVisible(true);
		otherParamValuesTable.setHeaderVisible(true);

		final TableColumn newColumnTableColumn_3 = new TableColumn(otherParamValuesTable, SWT.NONE);
		newColumnTableColumn_3.setWidth(122);
		newColumnTableColumn_3.setText("Name");

		final TableColumn newColumnTableColumn_4 = new TableColumn(otherParamValuesTable, SWT.NONE);
		newColumnTableColumn_4.setWidth(47);
		newColumnTableColumn_4.setText("Value");

		final Label label_1 = new Label(composite_1, SWT.WRAP);
		label_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		label_1.setText("3) Choose return values that will be shown in the table:");

		final ViewForm viewForm_7 = new ViewForm(composite_1, SWT.FLAT | SWT.BORDER);
		viewForm_7.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		valsToShowTable = new Table(viewForm_7, SWT.FULL_SELECTION | SWT.CHECK);
		valsToShowTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				rebuildRetValsTable();
			}
		});
		viewForm_7.setContent(valsToShowTable);
		valsToShowTable.setLinesVisible(true);
		valsToShowTable.setHeaderVisible(true);

		final TableColumn newColumnTableColumn_5 = new TableColumn(valsToShowTable, SWT.NONE);
		newColumnTableColumn_5.setWidth(187);
		newColumnTableColumn_5.setText("Return value name");
		tableTabItem.setControl(sashForm_1);

		final ViewForm viewForm_3 = new ViewForm(sashForm_1, SWT.FLAT | SWT.BORDER);

		retValTable = new Table(viewForm_3, SWT.FULL_SELECTION);
		viewForm_3.setContent(retValTable);
		retValTable.setLinesVisible(true);
		retValTable.setHeaderVisible(true);
		sashForm_1.setWeights(new int[] {184, 449 });

		final SashForm sashForm = new SashForm(tabFolder, SWT.VERTICAL);
		graphTabItem.setControl(sashForm);

		final ViewForm viewForm = new ViewForm(sashForm, SWT.FLAT | SWT.BORDER);
		scrolledComposite = new ScrolledComposite(viewForm, SWT.V_SCROLL | SWT.H_SCROLL);
		scrolledComposite.addControlListener(new ControlAdapter() {
			public void controlResized(final ControlEvent e) {
				if (fitToAreaButton.getSelection()) {
					resizeChartComposite();
				}
			}
		});
		scrolledComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		scrolledComposite.setAlwaysShowScrollBars(true);
		viewForm.setContent(scrolledComposite);

		chartComposite = new Composite(scrolledComposite, SWT.EMBEDDED);
		chartComposite.setLocation(0, 0);
		chartComposite.setSize(765, 410);
		scrolledComposite.setContent(chartComposite);

		final Composite graphOptionsComp = new Composite(sashForm, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.verticalSpacing = 2;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 2;
		graphOptionsComp.setLayout(gridLayout);

		final ViewForm viewForm_2 = new ViewForm(graphOptionsComp, SWT.FLAT | SWT.BORDER);
		final GridData gd_viewForm_2 = new GridData(SWT.FILL, SWT.FILL, false, true);
		gd_viewForm_2.widthHint = 251;
		viewForm_2.setLayoutData(gd_viewForm_2);

		final Composite composite = new Composite(viewForm_2, SWT.NONE);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.horizontalSpacing = 10;
		gridLayout_1.numColumns = 2;
		composite.setLayout(gridLayout_1);
		viewForm_2.setContent(composite);

		final Label drawTypeLabel = new Label(composite, SWT.NONE);
		drawTypeLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		drawTypeLabel.setText("Drawing:");

		final Composite composite_3 = new Composite(composite, SWT.NONE);
		composite_3.setLayoutData(new GridData());
		final GridLayout gridLayout_4 = new GridLayout();
		gridLayout_4.verticalSpacing = 1;
		gridLayout_4.marginWidth = 0;
		gridLayout_4.marginHeight = 0;
		gridLayout_4.horizontalSpacing = 1;
		composite_3.setLayout(gridLayout_4);

		antialiasingButton = new Button(composite_3, SWT.CHECK);
		antialiasingButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				if (antialiasingButton.getSelection()) isAntiAliasing = true;
				else isAntiAliasing = false;
				chart.paintImmediately(0, 0, chart.getWidth(), chart.getHeight());
			}
		});
		antialiasingButton.setSelection(true);
		antialiasingButton.setText("Antialiasing");

		final Button horizontalGridButton = new Button(composite_3, SWT.CHECK);
		horizontalGridButton.setSelection(true);
		horizontalGridButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				if (horizontalGridButton.getSelection()) {
					chart.getAxisY().setPaintGrid(true);
				} else {
					chart.getAxisY().setPaintGrid(false);
				}
			}
		});
		horizontalGridButton.setText("Horizontal grid");

		final Button verticalGridButton = new Button(composite_3, SWT.CHECK);
		verticalGridButton.setSelection(true);
		verticalGridButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				if (verticalGridButton.getSelection())  {
					chart.getAxisX().setPaintGrid(true);
				} else {
					chart.getAxisX().setPaintGrid(false);
				}
			}
		});
		verticalGridButton.setText("Vertical grid");

		final Label sizeLabel = new Label(composite, SWT.NONE);
		sizeLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		sizeLabel.setText("Size:");

		final Composite composite_2 = new Composite(composite, SWT.NONE);
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 3;
		gridLayout_2.verticalSpacing = 1;
		gridLayout_2.marginWidth = 0;
		gridLayout_2.marginHeight = 0;
		gridLayout_2.horizontalSpacing = 1;
		composite_2.setLayout(gridLayout_2);

		fitToAreaButton = new Button(composite_2, SWT.RADIO);
		fitToAreaButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				xspinner.setEnabled(false);
				yspinner.setEnabled(false);
				resizeChartComposite();
			}
		});
		fitToAreaButton.setSelection(true);
		fitToAreaButton.setText("Fit to area");
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);

		predefinedButton = new Button(composite_2, SWT.RADIO);
		predefinedButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				xspinner.setEnabled(true);
				yspinner.setEnabled(true);
			}
		});
		predefinedButton.setText("Predefined");

		xspinner = new Spinner(composite_2, SWT.BORDER);
		xspinner.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent arg0) {
				resizeChartCompositeWithSpinners();
			}
		});
		xspinner.setEnabled(false);
		xspinner.setSelection(760);
		xspinner.setMaximum(4000);
		xspinner.setMinimum(100);

		yspinner = new Spinner(composite_2, SWT.BORDER);
		yspinner.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent arg0) {
				resizeChartCompositeWithSpinners();
			}
		});
		yspinner.setEnabled(false);
		yspinner.setSelection(450);
		yspinner.setMinimum(100);
		yspinner.setMaximum(4000);

		final Label colorsLabel = new Label(composite, SWT.NONE);
		final GridData gd_colorsLabel = new GridData(SWT.LEFT, SWT.TOP, false, false);
		colorsLabel.setLayoutData(gd_colorsLabel);
		colorsLabel.setText("Colors:");

		final Composite composite_4 = new Composite(composite, SWT.NONE);
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final GridLayout gridLayout_5 = new GridLayout();
		gridLayout_5.numColumns = 2;
		gridLayout_5.verticalSpacing = 2;
		gridLayout_5.marginWidth = 0;
		gridLayout_5.marginHeight = 0;
		gridLayout_5.horizontalSpacing = 1;
		composite_4.setLayout(gridLayout_5);

		axisColorLabel = new Label(composite_4, SWT.BORDER);
		axisColorLabel.addMouseTrackListener(new MouseTrackAdapter() {
			public void mouseEnter(final MouseEvent arg0) {
				axisColorLabel.setBackground(axisColorLabel.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			}
			public void mouseExit(final MouseEvent arg0) {
				axisColorLabel.setBackground(axisColorLabel.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			}
		});
		axisColorLabel.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent e) {
				Color col = chart.getForeground();
				
				ColorDialog cdial = new ColorDialog(GraphEditor.this.getShell());
				cdial.setText("Choose axis color");
				cdial.setRGB(new RGB(col.getRed(), col.getGreen(), col.getBlue()));
				RGB nrgb = cdial.open();
				
				if (nrgb != null) {
					chart.setForeground(new Color(nrgb.red, nrgb.green, nrgb.blue));
					axisColorLabel.setText("Axis: " + colorToString(chart.getForeground()));
				}
			}
		});
		final GridData gd_oooolabel = new GridData(SWT.FILL, SWT.CENTER, true, false);
		axisColorLabel.setLayoutData(gd_oooolabel);
		axisColorLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		axisColorLabel.setText("#000000");

		gridColorLabel = new Label(composite_4, SWT.BORDER);
		gridColorLabel.addMouseTrackListener(new MouseTrackAdapter() {
			public void mouseEnter(final MouseEvent arg0) {
				gridColorLabel.setBackground(gridColorLabel.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			}
			public void mouseExit(final MouseEvent arg0) {
				gridColorLabel.setBackground(gridColorLabel.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			}
		});
		gridColorLabel.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent e) {
				Color col = chart.getGridColor();
				
				ColorDialog cdial = new ColorDialog(GraphEditor.this.getShell());
				cdial.setText("Choose grid color");
				cdial.setRGB(new RGB(col.getRed(), col.getGreen(), col.getBlue()));
				RGB nrgb = cdial.open();
				
				if (nrgb != null) {
					chart.setGridColor(new Color(nrgb.red, nrgb.green, nrgb.blue));
					gridColorLabel.setText("Grid: " + colorToString(chart.getGridColor()));
				}
			}
		});
		final GridData gd_bbbbbbLabel = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridColorLabel.setLayoutData(gd_bbbbbbLabel);
		gridColorLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		gridColorLabel.setText("#aaaaaa");

		backgroundColorLabel = new Label(composite_4, SWT.BORDER);
		backgroundColorLabel.addMouseTrackListener(new MouseTrackAdapter() {
			public void mouseEnter(final MouseEvent arg0) {
				backgroundColorLabel.setBackground(backgroundColorLabel.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			}
			public void mouseExit(final MouseEvent arg0) {
				backgroundColorLabel.setBackground(backgroundColorLabel.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			}
		});
		backgroundColorLabel.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent e) {
				Color col = chart.getBackground();
				
				ColorDialog cdial = new ColorDialog(GraphEditor.this.getShell());
				cdial.setText("Choose background color");
				cdial.setRGB(new RGB(col.getRed(), col.getGreen(), col.getBlue()));
				RGB nrgb = cdial.open();
				
				if (nrgb != null) {
					setChartBackground(nrgb);
				}
			}
		});
		final GridData gd_ffffffLabel = new GridData(SWT.FILL, SWT.CENTER, true, false);
		backgroundColorLabel.setLayoutData(gd_ffffffLabel);
		backgroundColorLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		backgroundColorLabel.setText("#ffffff");
		new Label(composite_4, SWT.NONE);

		final ViewForm viewForm_1 = new ViewForm(graphOptionsComp, SWT.FLAT | SWT.BORDER);
		viewForm_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		table = new Table(viewForm_1, SWT.HIDE_SELECTION | SWT.CHECK);
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (e.detail == SWT.CHECK) {
					int i = -1;
					for (Entry<String, ITrace2D> ntr : tracemap.entrySet()) {
						i++;
						if (table.getItem(i).getChecked()) {
							ntr.getValue().setVisible(true);
						} else {
							ntr.getValue().setVisible(false);
						}
					}
				}
			}
		});
		table.addControlListener(new ControlAdapter() {
			public void controlResized(final ControlEvent e) {
				int wdt = table.getSize().x - table.getBorderWidth() * 2;
				table.getColumn(0).setWidth(wdt - 240);
				table.getColumn(1).setWidth(80);
				table.getColumn(2).setWidth(80);
				table.getColumn(3).setWidth(80);
			}
		});
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		viewForm_1.setContent(table);

		final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
		newColumnTableColumn.setWidth(191);
		newColumnTableColumn.setText("Trace");

		final TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_1.setWidth(115);
		newColumnTableColumn_1.setText("Color");

		final TableColumn newColumnTableColumn_2 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_2.setWidth(76);
		newColumnTableColumn_2.setText("Stroke");

		final TableColumn pointColumn = new TableColumn(table, SWT.NONE);
		pointColumn.setWidth(100);
		pointColumn.setText("Points");
		sashForm.setWeights(new int[] {384, 170 });
		
		this.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				chart.destroy();
			}
		});
	}

	private void setEnabledStateOfOtherValuesTable() {
		int i = -1;
		for (CCombo c : otherParamValuesCombos) {
			i++;
			IParameter p = algorithm.getParameters().get(i);
			if (inputValueCombo.getItem(inputValueCombo.getSelectionIndex()).equals(p.getName())) {
				c.setEnabled(false);
			} else {
				c.setEnabled(true);
			}
		}
	}

	private void rebuildRetValsTable() {
		// clear everything in the table
		retValTable.removeAll();
		for (TableColumn col : retValTable.getColumns()) {
			col.dispose();
		}
		
		// get selected parameter
		int selind = inputValueCombo.getSelectionIndex();
		if (selind == -1) return;
		String selectedParamName = inputValueCombo.getItem(selind);
		
		// add the first row - the parameter that is changing
		TableColumn firstcol = new TableColumn(retValTable, SWT.NONE);
		firstcol.setText(selectedParamName);
		firstcol.setWidth(75);
		
		// add the rest of the rows - the return values tracked
		List<String> trackedValues = new ArrayList<String>();
		for (TableItem item : valsToShowTable.getItems()) {
			if (item.getChecked()) {
				trackedValues.add(item.getText());
				TableColumn col = new TableColumn(retValTable, SWT.NONE);
				col.setText(item.getText());
				col.setWidth(75);
			}
		}
		
		// fill the table
		// first create the input map, retrieve possible values for the input parameter
		Map<String, IValue> inputmap = createInputMapForParameters();
		IParameter selectedparam = algorithm.getParameter(selectedParamName);
		List<Object> selectedParamValues = getSelectedParamObjects(selectedParamName);
		
		// add table items
		for (Object o : selectedParamValues) {
			// retrieve the respective return value map
			Value v = new Value(o, selectedparam);
			inputmap.put(selectedParamName, v);
			Map<String, IValue> retvalmap = inventory.getReturnValues(inputmap);
			
			// now create table item
			TableItem item = new TableItem(retValTable, SWT.NONE);
			item.setText(0, o.toString());
			int i = 0;
			for (String trv : trackedValues) {
				i++;
				item.setText(i, retvalmap.get(trv).value().toString());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<Object> getSelectedParamObjects(String selectedParamName) {
		int i = -1;
		for (IParameter p : algorithm.getParameters()) {
			i++;
			if (p.getName().equals(selectedParamName)) {
				return (List<Object>) otherParamValuesCombos.get(i).getData();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Map<String, IValue> createInputMapForParameters() {
		Map<String, IValue> map = new HashMap<String, IValue>();
		
		int i = -1;
		for (IParameter p : algorithm.getParameters()) {
			i++;
			int selind = otherParamValuesCombos.get(i).getSelectionIndex();
			List<Object> vals = (List<Object>) otherParamValuesCombos.get(i).getData();
			Object o = vals.get(selind);
			map.put(p.getName(), new Value(o, p));
		}
		
		return map;
	}

	private void setChartBackground(RGB nrgb) {
		chart.setBackground(new Color(nrgb.red, nrgb.green, nrgb.blue));
		backgroundColorLabel.setText("Back: " + colorToString(chart.getBackground()));
		
		// change chart wrapper background
		org.eclipse.swt.graphics.Color chwoldcol = scrolledComposite.getBackground();
		scrolledComposite.setBackground(new org.eclipse.swt.graphics.Color(this.getDisplay(), nrgb));
		chwoldcol.dispose();
	}

	private void resizeChartCompositeWithSpinners() {
		if (xspinner != null && yspinner != null) {
			int wdt = xspinner.getSelection();
			int hgt = yspinner.getSelection();
			chartComposite.setSize(wdt, hgt);
		}
	}
	
	private void resizeChartComposite() {
		int wdt = scrolledComposite.getSize().x - scrolledComposite.getBorderWidth() * 2 - 30;
		int hgt = scrolledComposite.getSize().y - scrolledComposite.getBorderWidth() * 2 - 30;
		chartComposite.setSize(wdt, hgt);
		xspinner.setSelection(wdt);
		yspinner.setSelection(hgt);
	}

	@Override
	public Image getImage(Display d) {
		return new Image(d, IMAGE_ICON);
	}

	public void onEvent(Events evtype, CommandResult messages) {
	}

	public boolean canRedo() {
		return false;
	}

	public boolean canUndo() {
		return false;
	}

	public String getEditorName() {
		return "Graph Editor: " + graph.getGraphName();
	}

	public IInfoListener getInfoListener() {
		return null;
	}

	public void redo() {
		throw new IllegalStateException("Editor not undoable.");
	}

	public void undo() {
		throw new IllegalStateException("Editor not undoable.");
	}

	@Override
	public Map<String, String> getSaveTypes() {
		return SAVE_EXTENSION_MAP;
	}

	@Override
	public void save(String extension, OutputStream os) {
		if (extension.equals(DEFAULT_EXTENSION)) {
			try {
				saveGraph(os);
			} catch (IOException e) {
				throw new RuntimeException("Could not save graph.", e);
			}
		} else if (extension.equals("png")) {
			savePicture(os);
		} else throw new IllegalArgumentException("Unknown extension: " + extension);
	}

	private void savePicture(OutputStream os) {
		try {
			ImageIO.write(chart.snapShot(), "png", os);
		} catch (IOException e) {
			throw new RuntimeException("Could not save picture.", e);
		}
	}

	private void saveGraph(OutputStream os) throws IOException {
		GraphInfoSerializer.serialize(algorithm, graph, inventory, os);
	}

	@Override
	public String getLoadExtension() {
		return DEFAULT_EXTENSION;
	}

	@Override
	public boolean isLoadable() {
		return true;
	}

	@Override
	public void load(InputStream is) {
		try {
			SWrapper sw = GraphInfoSerializer.deserialize(is, ctrl.getModel());
			
			algorithm = sw.algorithm;
			graph = sw.graph;
			inventory = sw.inventory;
			
			// refresh GUI
			initChart2d(sw.inventory);
			rebuildTraceTable();
			rebuildTableTab();
		} catch (Exception e) {
			throw new IllegalArgumentException("Could not load.", e);
		}
	}
}














