package hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler;

import hr.fer.zemris.ga_framework.model.ConstraintTypes;
import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.model.IGraph;
import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.view.ImageLoader;
import hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler.Model.IRange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;


public class GraphSelector extends Dialog {

	/* static fields */
	private static final ImageData ICON_GRAPHS = ImageLoader.loadImage("icons", "chart_curve.png");

	/* private fields */
	private Text graphText;
	private CCombo retValCombo;
	private CCombo paramCombo;
	private Table table;
	private IGraph oldresult;
	private IAlgorithm algorithm;
	private List<IRange> ranges;
	protected IGraph result;
	protected Shell shell;
	private CCombo[] combos;

	/* ctors */
	
	/**
	 * Create the dialog
	 * @param parent
	 * @param style
	 */
	public GraphSelector(Shell parent, int style, IGraph old, IAlgorithm alg, List<IRange> rangelist) {
		super(parent, style);
		
		oldresult = old;
		algorithm = alg;
		ranges = rangelist;
	}
	
	/**
	 * Create the dialog
	 * @param parent
	 */
	public GraphSelector(Shell parent, IGraph old, IAlgorithm alg, List<IRange> rangelist) {
		this(parent, SWT.NONE, old, alg, rangelist);
	}

	/* methods */
	
	/**
	 * Open the dialog
	 * @return the result
	 */
	public IGraph open() {
		initGUI();
		advinitGUI();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return result;
	}
	
	private void advinitGUI() {
		// set icon
		shell.setImage(new Image(shell.getDisplay(), ICON_GRAPHS));
		
		// fill combos
		for (IParameter param : algorithm.getParameters()) {
			if (!param.getParamType().isComparable(param.getValueClass()) && 
					param.getConstraint() != ConstraintTypes.ENUMERATION) continue;
			paramCombo.add(param.getName());
			if (oldresult != null && oldresult.getAbscissaName().equals(param.getName())) {
				paramCombo.select(paramCombo.getItemCount() - 1);
			}
		}
		if (oldresult == null) paramCombo.select(paramCombo.getItemCount() - 1);
		for (IParameter param : algorithm.getReturnValues()) {
			if (!param.getParamType().isComparable(param.getValueClass()) && 
					param.getConstraint() != ConstraintTypes.ENUMERATION) continue;
			retValCombo.add(param.getName());
			if (oldresult != null && oldresult.getOrdinateName().equals(param.getName())) {
				retValCombo.select(retValCombo.getItemCount() - 1);
			}
		}
		if (oldresult == null) retValCombo.select(retValCombo.getItemCount() - 1);
		
		// rebuild table
		rebuildTable();
		
		// disable slot that's selected
		disableSelected();
	}
	
	private void disableSelected() {
		if (combos == null) return;
		
		int combopos = paramCombo.getSelectionIndex();
		int pos = findParamPos(algorithm.getParameters(), paramCombo.getItem(combopos));
		for (int i = 0; i < combos.length; i++) {
			combos[i].setEnabled(true);
		}
		combos[pos].setEnabled(false);
	}

	private int findParamPos(List<IParameter> params, String name) {
		int i = 0;
		for (IParameter p : params) {
			if (p.getName().equals(name)) return i;
			i++;
		}
		return -1;
	}
	
	private void rebuildTable() {
		table.removeAll();
		for (Control c : table.getChildren()) {
			if (c instanceof CCombo) c.dispose();
		}
		combos = new CCombo[algorithm.getParameters().size()];
		
		int i = 0;
		for (IParameter param : algorithm.getParameters()) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, param.getName());
			
			CCombo combo = new CCombo(table, SWT.NONE);
			combo.setEditable(false);
			combo.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			Object shouldSelect = null;
			if (oldresult != null) shouldSelect = oldresult.getCurveFamily().get(param.getName());
			for (Object val : findRange(param.getName())) {
				String s = val.toString();
				combo.add(s);
				combo.setData(s, val);
				if (shouldSelect != null && shouldSelect.equals(val)) combo.select(combo.getItemCount() - 1);
			}
			combo.add("Draw a curve for each value");
			if (shouldSelect == null) combo.select(combo.getItemCount() - 1);
			combos[i++] = combo;
			
			TableEditor editor = new TableEditor(table);
			editor.grabHorizontal = true;
			editor.setEditor(combo, item, 1);
		}
	}
	
	private IRange findRange(String name) {
		for (IRange r : ranges) {
			if (r.getParamName().equals(name)) return r;
		}
		return null;
	}

	/**
	 * Create contents of the dialog
	 */
	protected void initGUI() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.verticalSpacing = 3;
		gridLayout_1.marginWidth = 3;
		gridLayout_1.marginHeight = 3;
		gridLayout_1.horizontalSpacing = 3;
		shell.setLayout(gridLayout_1);
		shell.setSize(437, 375);
		shell.setText("Graph selector");
		shell.setLocation(getParent().getLocation().x + getParent().getSize().x / 2 - shell.getSize().x / 2,
				getParent().getLocation().y + getParent().getSize().y / 2 - shell.getSize().y / 2);

		final Composite composite = new Composite(shell, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 2;
		gridLayout.horizontalSpacing = 2;
		composite.setLayout(gridLayout);

		final Label graphNameLabel = new Label(composite, SWT.NONE);
		graphNameLabel.setText("Graph name:");

		graphText = new Text(composite, SWT.BORDER);
		graphText.setText("Graph");
		final GridData gd_graphText = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_graphText.widthHint = 134;
		graphText.setLayoutData(gd_graphText);

		final Label abscissaLabel = new Label(composite, SWT.NONE);
		abscissaLabel.setText("Abscissa (X-axis):");

		paramCombo = new CCombo(composite, SWT.BORDER);
		paramCombo.setEditable(false);
		paramCombo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
//		paramCombo.addModifyListener(new ModifyListener() {
		paramCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				int pos = paramCombo.getSelection().x;
				if (pos != -1) disableSelected();
			}
		});
		paramCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		final Label ordinateLabel = new Label(composite, SWT.NONE);
		ordinateLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		ordinateLabel.setText("Ordinate (Y-axis):");

		retValCombo = new CCombo(composite, SWT.BORDER);
		retValCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		retValCombo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		retValCombo.setEditable(false);

		final Label familyOfCurvesLabel = new Label(composite, SWT.NONE);
		familyOfCurvesLabel.setText("Families of curves:");
		new Label(composite, SWT.NONE);

		final Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite_1.setLayout(new FillLayout());

		table = new Table(composite_1, SWT.FULL_SELECTION | SWT.BORDER);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
		newColumnTableColumn.setWidth(155);
		newColumnTableColumn.setText("Parameter");

		final TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_1.setWidth(190);
		newColumnTableColumn_1.setText("Value");

		final Composite composite_2 = new Composite(shell, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.verticalSpacing = 2;
		gridLayout_2.marginWidth = 2;
		gridLayout_2.marginHeight = 2;
		gridLayout_2.horizontalSpacing = 2;
		gridLayout_2.numColumns = 2;
		composite_2.setLayout(gridLayout_2);

		final Button okButton = new Button(composite_2, SWT.NONE);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				final Map<String, Object> curvefamilies = new HashMap<String, Object>();
				int i = -1;
				for (CCombo c : combos) {
					i++;
					if (c.getEnabled() == false) continue;
					if (c.getSelectionIndex() == -1) continue;
					Object val = c.getData(c.getItem(c.getSelectionIndex()));
					if (val != null) curvefamilies.put(algorithm.getParameters().get(i).getName(), val);
				}
				
				result = new IGraph() {
					private String abscissa, ordinate;
					private String description;
					private String graphname;
					{
						abscissa = paramCombo.getItem(paramCombo.getSelectionIndex());
						ordinate = retValCombo.getItem(retValCombo.getSelectionIndex());
						StringBuilder sb = new StringBuilder(getAbscissaName());
						sb.append(" - ").append(getOrdinateName()).append(", values: ");
						boolean first = true;
						for (Entry<String, Object> ntr : getCurveFamily().entrySet()) {
							if (first) first = false; else sb.append(", ");
							sb.append(ntr.getKey()).append(" = ").append(ntr.getValue());
						}
						description = sb.toString();
						graphname = graphText.getText();
					}
					public String getAbscissaName() {
						return abscissa;
					}
					public Map<String, Object> getCurveFamily() {
						return curvefamilies;
					}
					public String getDescription() {
						return description;
					}
					public String getGraphName() {
						return graphname;
					}
					public String getOrdinateName() {
						return ordinate;
					}
				};
				
				shell.dispose();
			}
		});
		okButton.setText("OK");

		final Button cancelButton = new Button(composite_2, SWT.NONE);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				result = null;
				shell.dispose();
			}
		});
		cancelButton.setLayoutData(new GridData());
		cancelButton.setText("Cancel");
	}
}














