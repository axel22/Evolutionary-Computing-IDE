package hr.fer.zemris.ga_framework.algorithms.tsp;

import hr.fer.zemris.ga_framework.model.IParameterDialog;
import hr.fer.zemris.ga_framework.model.ISerializable;
import hr.fer.zemris.ga_framework.model.misc.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Map.Entry;

import name.brijest.extrawidgets.DoublePoint;
import name.brijest.extrawidgets.dragpane.DragPane;
import name.brijest.extrawidgets.dragpane.DragPane.IDrawable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;




public class CitySelectPane extends Composite implements IParameterDialog {
	
	private static class CityToken implements IDrawable {
		private static final DoublePoint TOKEN_SIZE = new DoublePoint(32, 32);
		private int index;
		public CityToken(int num) {
			index = num;
		}
		public int getIndex() {
			return index;
		}
		public void drawHover(GC gc) {
			drawSelf(gc);
		}
		public void drawSelf(GC gc) {
			gc.setAntialias(SWT.ON);
			Color c = gc.getBackground();
			gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_DARK_GRAY));
			gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_GRAY));
			gc.fillOval(-11, -11, 22, 22);
			gc.setLineWidth(3);
			gc.drawOval(-11, -11, 22, 22);
			gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
			String sval = String.valueOf(index);
			gc.drawText(sval, -gc.getFontMetrics().getAverageCharWidth() / 2 * sval.length(),
					-gc.getFontMetrics().getHeight() / 2, true);
			gc.setBackground(c);
		}
		public DoublePoint getSize() {
			return TOKEN_SIZE;
		}
	}

	/* static fields */
	private static final int MANY_CITIES = 40;

	/* private fields */
	private Spinner mapHeightSpinner;
	private Spinner mapWidthSpinner;
	private Table table;
	private Spinner cityNumSpinner;
	private CityTable citytab;
	private DragPane map;
	private Text[] ttxts;
	private Label[] tlabs;
	private DragPane.IDrawable[] cityTokens;
	private Button triangularButton;
	private Button multicircularButton;
	private Button spiralButton;
	
	/* ctors */
	
	public CitySelectPane(Composite c, int style) {
		super(c, style);
		
		citytab = new CityTable(1, 500, 400);
		
		initGUI();
		advinitGUI();
	}
	
	/* methods */
	
	private void advinitGUI() {
		buildMapAndTable();
	}

	@Override
	protected void checkSubclass() {
	}
	
	private void initGUI() {
		final GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);

		final Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 6;
		composite.setLayout(gridLayout_1);

		final Label numberOfCitiesLabel = new Label(composite, SWT.NONE);
		numberOfCitiesLabel.setText("Number of cities");

		cityNumSpinner = new Spinner(composite, SWT.BORDER);
		cityNumSpinner.setLayoutData(new GridData());
		cityNumSpinner.setMaximum(45000);
		cityNumSpinner.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent ev) {
				if (table == null) return;
				
				Object data = ev.widget.getData();
				if (data != null && (Boolean)data == true) {
					int ctnum = cityNumSpinner.getSelection();
					citytab = new CityTable(ctnum, mapWidthSpinner.getSelection(), mapHeightSpinner.getSelection());
					
					circulateCities();
					buildMapAndTable();
				}
			}
		});
		cityNumSpinner.setMinimum(1);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);

		final Label mapWidthLabel = new Label(composite, SWT.NONE);
		mapWidthLabel.setText("Map width and height");

		mapWidthSpinner = new Spinner(composite, SWT.BORDER);
		mapWidthSpinner.setLayoutData(new GridData());
		mapWidthSpinner.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				if (table == null) return;
				int wdt = mapWidthSpinner.getSelection();
				if (wdt < 100) mapWidthSpinner.setSelection(citytab.getWidth());
				
				Object data = e.widget.getData();
				if (data != null && (Boolean)data == true) {
					int ctnum = cityNumSpinner.getSelection();
					citytab = new CityTable(ctnum, mapWidthSpinner.getSelection(), mapHeightSpinner.getSelection());
					
					circulateCities();
					buildMapAndTable();
				}
			}
		});
		mapWidthSpinner.setMinimum(100);
		mapWidthSpinner.setMaximum(100000);
		mapWidthSpinner.setSelection(500);

		mapHeightSpinner = new Spinner(composite, SWT.BORDER);
		mapHeightSpinner.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				if (table == null) return;
				int hgt = mapHeightSpinner.getSelection();
				if (hgt < 100) mapHeightSpinner.setSelection(citytab.getHeight());
				
				Object data = e.widget.getData();
				if (data != null && (Boolean)data == true) {
					int ctnum = cityNumSpinner.getSelection();
					citytab = new CityTable(ctnum, mapWidthSpinner.getSelection(), mapHeightSpinner.getSelection());
					
					circulateCities();
					buildMapAndTable();
				}
			}
		});
		mapHeightSpinner.setMinimum(100);
		
		final GridData gd_mapHeightSpinner = new GridData();
		mapHeightSpinner.setLayoutData(gd_mapHeightSpinner);
		mapHeightSpinner.setMaximum(100000);
		mapHeightSpinner.setSelection(400);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);

		final Label distancesLabel = new Label(composite, SWT.NONE);
		distancesLabel.setLayoutData(new GridData());
		distancesLabel.setText("City positions:");

		final Button circularButton = new Button(composite, SWT.NONE);
		circularButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (table == null) return;
				
				circulateCities();
				refillMapAndTable();
			}
		});
		circularButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		circularButton.setToolTipText("Cities shall be set in circle.");
		circularButton.setText("Circular");

		multicircularButton = new Button(composite, SWT.NONE);
		multicircularButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		multicircularButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				if (table == null) return;
				
				multicirculateCities();
				refillMapAndTable();
			}
		});
		multicircularButton.setText("Multicircular");

		spiralButton = new Button(composite, SWT.NONE);
		spiralButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		spiralButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				if (table == null) return;
				
				spiralateCities();
				refillMapAndTable();
			}
		});
		spiralButton.setText("Spiral");
		
		triangularButton = new Button(composite, SWT.NONE);
		triangularButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				if (table == null) return;
				
				triangulateCities();
				refillMapAndTable();
			}
		});
		triangularButton.setText("Triangular");

		final Composite composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayout(new FillLayout());
		composite_1.setLayoutData(new GridData(GridData.FILL_BOTH));

		final TabFolder tabFolder = new TabFolder(composite_1, SWT.NONE);

		final TabItem cityMapTabItem = new TabItem(tabFolder, SWT.NONE);
		cityMapTabItem.setText("Map");

		final ScrolledComposite scrolledComposite = new ScrolledComposite(tabFolder, SWT.V_SCROLL | SWT.H_SCROLL);
		scrolledComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		cityMapTabItem.setControl(scrolledComposite);

		map = new DragPane(scrolledComposite, SWT.BORDER);
		map.setSize(mapWidthSpinner.getSelection(), mapHeightSpinner.getSelection());

		final Button button = new Button(composite, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				if (table == null) return;
				
				threeLineateCities();
				refillMapAndTable();
			}
		});
		button.setText("3-linear");
		new Label(composite, SWT.NONE);

		final Button perturbateButton = new Button(composite, SWT.NONE);
		perturbateButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				if (table == null) return;
				
				perturbatePositions();
				refillMapAndTable();
			}
		});
		perturbateButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		perturbateButton.setText("Perturbate");

		final Button randomizePositionsButton = new Button(composite, SWT.NONE);
		randomizePositionsButton.setToolTipText("City positions shall be randomized.");
		randomizePositionsButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		randomizePositionsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (table == null) return;
				
				randomizePositions();
				refillMapAndTable();
			}
		});
		randomizePositionsButton.setText("Randomize");

		final Button randomPathButton = new Button(composite, SWT.NONE);
		randomPathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				if (table == null) return;
				
				randomPathPositions();
				refillMapAndTable();
			}
		});
		randomPathButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		randomPathButton.setText("Random path");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);

		final Button loadButton = new Button(composite, SWT.NONE);
		loadButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				FileDialog fd = new FileDialog(CitySelectPane.this.getShell(), SWT.OPEN);
				fd.setText("Load problem instance");
				fd.setFilterNames(new String[]{"TSP problem instance"});
				fd.setFilterExtensions(new String[]{"*.tsp"});
				String filename = fd.open();
				if (filename != null) try {
					loadProblemInstanceFromFile(filename);
					cityNumSpinner.setData(false);
					cityNumSpinner.setSelection(citytab.getCityNum());
					cityNumSpinner.setData(true);
					mapWidthSpinner.setData(false);
					mapWidthSpinner.setSelection(citytab.getWidth());
					mapWidthSpinner.setData(true);
					mapHeightSpinner.setData(false);
					mapHeightSpinner.setSelection(citytab.getHeight());
					mapHeightSpinner.setData(true);
					buildMapAndTable();
				} catch (Exception e) {
					e.printStackTrace();
					MessageBox box = new MessageBox(CitySelectPane.this.getShell(), SWT.OK | SWT.ICON_WARNING);
					box.setText("Could not load file");
					box.setMessage("Cannot load problem instance file - it doesn't exist or is of invalid format.\n" + e);
					box.open();
				}
			}
		});
		loadButton.setToolTipText("Loads city locations from a file. Only euclidian TSP problem instances are supported.");
		final GridData gd_loadButton = new GridData(SWT.FILL, SWT.CENTER, false, false);
		loadButton.setLayoutData(gd_loadButton);
		loadButton.setText("Load...");

		final Button saveButton = new Button(composite, SWT.NONE);
		saveButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				FileDialog fd = new FileDialog(CitySelectPane.this.getShell(), SWT.SAVE);
				fd.setOverwrite(true);
				fd.setText("Save problem instance");
				fd.setFilterNames(new String[]{"TSP problem instance"});
				fd.setFilterExtensions(new String[]{"*.tsp"});
				
				String s = fd.open();
				if (s != null) {
					if (!s.endsWith(".tsp")) s = s + ".tsp";
					saveCitiesToFile(s);
				}
			}
		});
		saveButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		saveButton.setText("Save...");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);

		scrolledComposite.setContent(map);

		final TabItem cityTableTabItem = new TabItem(tabFolder, SWT.NONE);
		cityTableTabItem.setText("Table");

		table = new Table(tabFolder, SWT.NONE);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		cityTableTabItem.setControl(table);

		final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
		newColumnTableColumn.setWidth(61);
		newColumnTableColumn.setText("City number");

		final TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_1.setWidth(100);
		newColumnTableColumn_1.setText("X position");

		final TableColumn newColumnTableColumn_2 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_2.setWidth(100);
		newColumnTableColumn_2.setText("Y position");
	}
	
	private void saveCitiesToFile(String filename) {
		try {
			FileWriter fw = new FileWriter(filename, false);
			
			try {
				fw.write("TYPE: TSP\n");
				fw.write("DIMENSION: " + citytab.getCityNum() + "\n");
				fw.write("EDGE_WEIGHT_TYPE: EUC_2D\n");
				fw.write("NODE_COORD_SECTION\n");
				for (int i = 0, sz = citytab.getCityNum(); i < sz; i++) {
					fw.write((i + 1) + " " + citytab.getCityX(i) + " " + citytab.getCityY(i) + "\n");
				}
				fw.write("EOF\n");
				fw.flush();
			} finally {
				fw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadProblemInstanceFromFile(String filename) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		Integer dimension = null;
		
		try {
			// read line by line - load DIMENSION, check EDGE_WEIGHT_TYPE, load NODE_COORD_SECTION
			String line = null;
			while ((line = br.readLine()) != null) {
				Scanner s = new Scanner(line);
				s.useDelimiter("\\s*:\\s*");
				String key = s.next();
				if (key.equals("DIMENSION")) {
					dimension = s.nextInt();
				} else if (key.equals("EDGE_WEIGHT_TYPE")) {
					String value = s.next();
					if (!value.equals("EUC_2D")) throw new Exception("Only euclidian 2d problems allowed.");
				} else if (key.equals("NODE_COORD_SECTION")) {
					break;
				}
			}
			
			if (dimension == null) throw new Exception("Must specify dimension.");
			
			Map<Integer, Double[]> coords = new HashMap<Integer, Double[]>();
			double maxx = 200, maxy = 200;
			while ((line = br.readLine()) != null) {
				if (line.charAt(0) == 'E') break;
				Scanner s = new Scanner(line);
				int city = s.nextInt();
				double x = s.nextDouble();
				double y = s.nextDouble();
				if (x > maxx) maxx = x;
				if (y > maxy) maxy = y;
				coords.put(city - 1, new Double[]{x, y});
			}
			
			citytab = new CityTable(dimension, (int)(maxx * 1.05), (int)(maxy * 1.05));
			for (Entry<Integer, Double[]> ntr : coords.entrySet()) {
				Double[] xy = ntr.getValue();
				citytab.set(ntr.getKey(), xy[0], xy[1]);
			}
		} finally {
			br.close();
		}
	}

	private void circulateCities() {
		int numcities = cityNumSpinner.getSelection();
		int width = citytab.getWidth();
		int height = citytab.getHeight();
		double lesser = ((width < height) ? width : height) * 0.9;
		for (int i = 0; i < numcities; i++) {
			double x = width / 2 + lesser / 2 * Math.sin(2 * Math.PI * i / numcities);
			double y = height / 2 + lesser / 2 * Math.cos(2 * Math.PI * i / numcities);
			x = (int)x;
			y = (int)y;
			citytab.set(i, x, y);
		}
	}
	
	private void multicirculateCities() {
		int numcities = cityNumSpinner.getSelection();
		int width = citytab.getWidth();
		int height = citytab.getHeight();
		double lesser = ((width < height) ? width : height) * 0.9;
		int div = 2, nummod = 4 / 3;
		for (int i = 0; i < numcities; i++) {
			if (i > (14 * numcities / 16)) { div = 6; nummod = numcities * 2 / 16; }
			else if (i > (5 * numcities / 8)) { div = 3; nummod = numcities * 4 / 16; }
			else { div = 2; nummod = numcities * 5 / 8; }
			double alfa = 2 * Math.PI * i / nummod;
			double x = width / 2 + lesser / div * Math.cos(alfa);
			double y = height / 2 + lesser / div * Math.sin(alfa);
			x = (int)x;
			y = (int)y;
			citytab.set(i, x, y);
		}
	}
	
	private void spiralateCities() {
		int numcities = cityNumSpinner.getSelection();
		int width = citytab.getWidth();
		int height = citytab.getHeight();
		double lesser = ((width < height) ? width : height) * 0.9;
		for (int i = 0; i < numcities; i++) {
			double rad = 1.0 * (i + numcities / 10) / numcities;
			double x = width / 2 + rad * lesser / 2 * Math.cos(6 * Math.PI * i / numcities);
			double y = height / 2 + rad * lesser / 2 * Math.sin(6 * Math.PI * i / numcities);
			x = (int)x;
			y = (int)y;
			citytab.set(i, x, y);
		}
	}
	
	private void triangulateCities() {
		int numcities = cityNumSpinner.getSelection();
		int width = citytab.getWidth();
		int height = citytab.getHeight();
		double lesser = ((width < height) ? width : height) * 0.9;
		// numc=S(2i+1)=n(n+1)+n=n^2+2*n => n^2 + 2*n - numc = 0 
		// => (n+1)^2 = numc + 1 => n = sqrt(numc + 1) - 1
		int totalrows = (int)(Math.sqrt(numcities + 1));
		int inlastrow = 2 * totalrows + 1;
		int incurrent = 1;
		int current = 1;
		int counter = 0;
		for (int i = 0; i < numcities; i++) {
			if (counter == incurrent) {
				incurrent += 2;
				current++;
				counter = 1;
			} else {
				counter++;
			}
			double x = width / 2 - lesser / 2 + lesser * (inlastrow - incurrent) / inlastrow / 2 + counter * lesser / inlastrow;
			double y = height / 2 - lesser / 2 + lesser * (current - 1) / totalrows;
			x = (int)x;
			y = (int)y;
			citytab.set(i, x, y);
		}
	}
	
	private void threeLineateCities() {
		int numcities = cityNumSpinner.getSelection();
		int width = citytab.getWidth();
		int height = citytab.getHeight();
		double lesser = ((width < height) ? width : height) * 0.9;
		
		int numcdiv3 = numcities / 3;
		for (int i = 0; i < numcities; i++) {
			double x, y;
			
			if (i < numcdiv3) {
				x = (width - lesser) / 2 + lesser / 3 - lesser / 6;
				y = (height - lesser) / 2 + lesser * i / (numcdiv3 + 2);
			} else if (i < 2 * numcdiv3) {
				x = (width - lesser) / 2 + 2 * lesser / 3 - lesser / 6;
				y = (height - lesser) / 2 + lesser * (i - numcdiv3) / (numcdiv3 + 2);
			} else {
				x = (width - lesser) / 2 + lesser - lesser / 6;
				y = (height - lesser) / 2 + lesser * (i - 2 * numcdiv3) / (numcdiv3 + 2);
			}
			
			citytab.set(i, x, y);
		}
	}

	private void randomizePositions() {
		Random rand = new Random();
		int citynum = cityNumSpinner.getSelection();
		int xlimit = mapWidthSpinner.getSelection();
		int ylimit = mapHeightSpinner.getSelection();
		
		citytab = new CityTable(citynum, xlimit, ylimit);
		for (int i = 0; i < citynum; i++) {
			double x = (int)(rand.nextDouble() * xlimit * 0.9 + xlimit * 0.05);
			double y = (int)(rand.nextDouble() * ylimit * 0.9 + ylimit * 0.05);
			citytab.set(i, x, y);
		}
	}
	
	private void randomPathPositions() {
		Random rand = new Random();
		int citynum = cityNumSpinner.getSelection();
		int xlimit = mapWidthSpinner.getSelection();
		int ylimit = mapHeightSpinner.getSelection();
		double smaller = (xlimit < ylimit) ? xlimit : ylimit;
		double xdir = 1.0, ydir = 1.0;
		double xlast = xlimit * 0.05, ylast = ylimit * 0.05;
		double step = smaller / citynum * 4;
		double ylow = 0.05 * ylimit, yhigh = 0.95 * ylimit, xlow = 0.05 * xlimit, xhigh = 0.95 * xlimit;
		
		citytab = new CityTable(citynum, xlimit, ylimit);
		for (int i = 0; i < citynum; i++) {
			xdir = xdir + (rand.nextDouble() - 0.5) * 0.5;
			ydir = ydir + (rand.nextDouble() - 0.5) * 0.5;
			if (xdir < -1.0) xdir = -1.0;
			if (xdir > 1.0) xdir = 1.0;
			if (ydir < -1.0) ydir = -1.0;
			if (ydir > 1.0) ydir = 1.0;
			
			xlast += (xdir < -0.25) ? -step : ((xdir < 0.25) ? 0 : step);
			ylast += (ydir < -0.25) ? -step : ((ydir < 0.25) ? 0 : step);
			
			if (xlast < xlow) {
				xlast = 0.05 * xlimit;
				xdir = -xdir;
			}
			if (ylast < ylow) {
				ylast = 0.05 * ylimit;
				ydir = -ydir;
			}
			if (xlast > xhigh) {
				xlast = 0.95 * xlimit;
				xdir = -xdir;
			}
			if (ylast > yhigh) {
				ylast = 0.95 * ylimit;
				ydir = -ydir;
			}
			
			citytab.set(i, xlast, ylast);
		}
	}
	
	private void perturbatePositions() {
		Random rand = new Random();
		int citynum = cityNumSpinner.getSelection();
		int xlimit = mapWidthSpinner.getSelection();
		int ylimit = mapHeightSpinner.getSelection();
		
		CityTable oldctab = citytab;
		citytab = new CityTable(citynum, xlimit, ylimit);
		for (int i = 0; i < citynum; i++) {
			double x = (int)(oldctab.getCityX(i) + (rand.nextDouble() - 0.5) * xlimit / 40);
			if (x < 0.05 * xlimit) x = -x;
			if (x > 0.95 * xlimit) x -= 2 * (x - xlimit);
			if (x < 0.05 * xlimit) x = 0.05 * xlimit;
			if (x > 0.95 * xlimit) x = 0.95 * xlimit;
			double y = (int)(oldctab.getCityY(i) + (rand.nextDouble() - 0.5) * ylimit / 40);
			if (y < 0.05 * ylimit) y = -y;
			if (y > 0.95 * ylimit) y -= 2 * (y - ylimit);
			if (y < 0.05 * ylimit) y = 0.05 * ylimit;
			if (y > 0.95 * ylimit) y = 0.95 * ylimit;
			citytab.set(i, x, y);
		}
	}

	private void buildMapAndTable() {
		int citynum = citytab.getCityNum();
		cityTokens = new DragPane.IDrawable[citynum];
		
		// build table
		table.removeAll();
		if (ttxts != null) {
			for (int i = 0; i < ttxts.length; i++) ttxts[i].dispose();
			for (int i = 0; i < tlabs.length; i++) tlabs[i].dispose();
		}
		if (citynum < MANY_CITIES) {
			ttxts = new Text[citynum * 2];
			tlabs = new Label[citynum];
			for (int i = 0; i < citynum; i++) {
				final int index = i;
				TableItem item = new TableItem(table, SWT.NONE);
				final TableEditor labed = new TableEditor(table);
				TableEditor xeditor = new TableEditor(table);
				TableEditor yeditor = new TableEditor(table);
				labed.grabHorizontal = true;
				xeditor.grabHorizontal = true;
				yeditor.grabHorizontal = true;
				Label lab = new Label(table, SWT.None);
				tlabs[i] = lab;
				ttxts[i * 2] = new Text(table, SWT.NONE);
				ttxts[i * 2 + 1] = new Text(table, SWT.NONE);
				lab.setText(String.valueOf(i));
				ttxts[i * 2].setText(String.valueOf(citytab.getCityX(i)));
				ttxts[i * 2 + 1].setText(String.valueOf(citytab.getCityY(i)));
				labed.setEditor(lab, item, 0);
				xeditor.setEditor(ttxts[i * 2], item, 1);
				yeditor.setEditor(ttxts[i * 2 + 1], item, 2);
				ttxts[i * 2].addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent e) {
						double oldval = citytab.getCityX(index);
						double newval = 0;
						try {
							// parse double
							newval = Double.parseDouble(ttxts[index * 2].getText());
							
							// check if value too big or too small
							if (newval < 0.0 || newval > citytab.getWidth()) throw new NumberFormatException();
							
							// set new value
							oldval = newval;
							citytab.set(index, newval, citytab.getCityY(index));
							
							// inform map that change has occured
							informMap(index);
						} catch (NumberFormatException ex) {
							ttxts[index * 2].setText(String.valueOf(oldval));
						}
					}
				});
				ttxts[i * 2 + 1].addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent e) {
						double oldval = citytab.getCityY(index);
						double newval = 0;
						try {
							// parse double
							newval = Double.parseDouble(ttxts[index * 2 + 1].getText());
							
							// check if value too big or too small
							if (newval < 0.0 || newval > citytab.getHeight()) throw new NumberFormatException();
							
							// set new value
							oldval = newval;
							citytab.set(index, citytab.getCityX(index), newval);
							
							// inform map that change has occured
							informMap(index);
						} catch (NumberFormatException ex) {
							ttxts[index * 2 + 1].setText(String.valueOf(oldval));
						}
					}
				});
			}
		} else {
			TableItem item = new TableItem(table, SWT.NONE);
			TableEditor editor = new TableEditor(table);
			tlabs = new Label[1];
			Label lab = new Label(table, SWT.NONE);
			tlabs[0] = lab;
			lab.setText("Too many cities - use the map to place them around.");
			editor.grabHorizontal = true;
			editor.setEditor(lab, item, 0);
		}
		
		// build map
		map.clearClickListeners();
		map.clearDragListeners();
		map.setSize(mapWidthSpinner.getSelection() + 1, mapHeightSpinner.getSelection() + 1);
		fillMap();
		map.addDragListener(new DragPane.IDragListener() {
			public void onDrag(IDrawable d, DoublePoint to, DoublePoint from) {
				CityToken token = (CityToken)d;
				citytab.set(token.getIndex(), to.x, to.y);
				informTable(token.getIndex());
			}
		});
	}
	
	private void fillMap() {
		int citynum = cityNumSpinner.getSelection();
		
		map.removeAll();
		for (int i = 0; i < citynum; i++) {
			IDrawable d = new CityToken(i);
			cityTokens[i] = d;
			map.addObject(d, new DoublePoint(citytab.getCityX(i), citytab.getCityY(i)));
		}
	}

	private void informMap(int index) {
		if (cityTokens != null && cityTokens[index] != null) {
			map.reinsertObject(cityTokens[index], 
					new DoublePoint(citytab.getCityX(index), citytab.getCityY(index)));
		}
	}
	
	private void informTable(int index) {
		int numc = citytab.getCityNum();
		if (table != null && ttxts != null && numc < MANY_CITIES) {
			ttxts[2 * index].setText(String.valueOf(citytab.getCityX(index)));
			ttxts[2 * index + 1].setText(String.valueOf(citytab.getCityY(index)));
		}
	}

	private void refillMapAndTable() {
		int citynum = cityNumSpinner.getSelection();
		
		if (citynum < MANY_CITIES) {
			// set table values
			for (int i = 0; i < citynum; i++) {
				ttxts[2 * i].setText(String.valueOf(citytab.getCityX(i)));
				ttxts[2 * i + 1].setText(String.valueOf(citytab.getCityY(i)));
			}
		}
		
		// set map values
		fillMap();
	}


	
	// IParameterDialog methods
	
	public Pair<Integer, Integer> getDimensions() {
		return new Pair<Integer, Integer>(650, 600);
	}

	public ISerializable getValue() {
		return new CityTable(citytab);
	}

	public void setValue(ISerializable value) {
		if (value == null) {
			cityNumSpinner.setSelection(1);
			buildMapAndTable();
			return;
		}
		
		if (!(value instanceof CityTable)) throw new IllegalArgumentException("Only sets CityTables.");
		citytab = new CityTable((CityTable)value);
		value = citytab;
		int numcities = citytab.getCityNum();
		
		// init table and spinners
		cityNumSpinner.setData(false);
		cityNumSpinner.setSelection(numcities);
		cityNumSpinner.setData(true);
		mapWidthSpinner.setData(false);
		mapWidthSpinner.setSelection(citytab.getWidth());
		mapWidthSpinner.setData(true);
		mapHeightSpinner.setData(false);
		mapHeightSpinner.setSelection(citytab.getHeight());
		mapHeightSpinner.setData(true);
		buildMapAndTable();
	}

	public Class<? extends ISerializable> isUsedFor() {
		return CityTable.class;
	}
	

}














