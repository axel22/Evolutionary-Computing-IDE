package hr.fer.zemris.ga_framework.view.gadgets.parameter_editor;

import hr.fer.zemris.ga_framework.model.ConstraintTypes;
import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.ISerializable;
import hr.fer.zemris.ga_framework.model.IValue;
import hr.fer.zemris.ga_framework.model.ParameterTypes;
import hr.fer.zemris.ga_framework.model.impl.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


/**
 * Lists the properties and allows listeners
 * to register.
 * Listeners are advised when a property change occurs,
 * and they should act once it occurs (trigger something
 * else, set previous value to parameter editor, etc.).
 * 
 * Parameters are shown according to the following rules:
 * - if the parameter is an enumeration, this parameter will
 * be shown with a combo box containing the result of the
 * <code>getValueString</code> method of <code>IValue</code>,
 * unless there is only one element of the enumeration
 * Other rules describe the parameters that are not enumerated:
 * - if the value parameter type is TIME, a control will be
 * shown allowing user to set both the interval and the metric
 * - if p.t. is INTEGER, a control will be a simple text box,
 * and non-integer values will be refused without sending
 * a command to change the value of the parameter
 * - if p.t. is REAL, display is similar to INTEGER
 * - if p.t. is STRING, a text box will be used
 * - if p.t. is BOOLEAN, a combo box will be used, containing
 * true and false values
 * - if p.t. is ISERIALIZABLE, an unmodifiable textbox is shown
 * showing the parameter's value string, and a small "..." button
 * next to it. The button opens a dialog specific to the class
 * of the value (which must implement ISerializable). Dialog will
 * be generated through a factory. Note that if the appropriate
 * dialog does not exist (no dialog is assigned to this class name),
 * the parameter value will no be able to be changed.
 * 
 * 
 * @author Axel
 *
 */
public class ParameterEditor extends Composite {

	/* static fields */	

	/* private fields */
	private Table table;
	private double first, second;
	private List<IParameter> parameters;
	private List<Object> currentvalues;
	private Map<String, Cell> cellmap;
	private List<IParameterListener> listeners;
	private ICellListener cellListener;

	/* ctors */
	
	/**
	 * Create the composite
	 * @param parent
	 * @param style
	 */
	public ParameterEditor(Composite parent, int style) {
		super(parent, style);
		
		first = 50;
		second = 50;
		parameters = null;
		currentvalues = null;
		listeners = new ArrayList<IParameterListener>();
		cellmap = new HashMap<String, Cell>();
		
		initGUI();
		advinitGUI();
	}

	/* methods */

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	private void advinitGUI() {
		resizeTableColumns();
	}
	
	private void initGUI() {
		addControlListener(new ControlAdapter() {
			public void controlResized(final ControlEvent arg0) {
				resizeTableColumns();
			}
		});
		setLayout(new FillLayout(SWT.VERTICAL));
		
		table = new Table(this, SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
				event.height = 18;
			}
		});

		final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
		newColumnTableColumn.setWidth(100);
		newColumnTableColumn.setText("Parameter");

		final TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_1.setWidth(100);
		newColumnTableColumn_1.setText("Value");
		
//		TableItem it = new TableItem(table, SWT.NONE);
//		it.setText(0, "Ok.");
//		Button b = new Button(table, SWT.CHECK);
//		b.setText("Ok.");
//		b.pack();
//		TableEditor ed = new TableEditor(table);
//		ed.minimumWidth = b.getSize().x;
//		ed.setEditor(b, it, 1);
	}
	

	private void resizeTableColumns() {
		int wdt = this.getSize().x;
		
		table.getColumn(0).setWidth((int) ((wdt - this.getBorderWidth() * 2) * (first / (first + second))));
		table.getColumn(1).setWidth((int) ((wdt - this.getBorderWidth() * 2) * (second / (first + second))));
	}
	
	public void setParameters(List<IParameter> params, Map<String, IValue> values) {
		parameters = params;
		currentvalues = new ArrayList<Object>();
		
		rebuildFromParameters(params, values);
	}
	
	public Map<String, IValue> getValues() {
		Map<String, IValue> values = new HashMap<String, IValue>();
		
		for (Entry<String, Cell> ntr : cellmap.entrySet()) {
			Cell cell = ntr.getValue();
			IValue val = new Value(cell.getValue(), parameters.get(cell.getYPos()));
			values.put(ntr.getKey(), val);
		}
		
		return values;
	}
	
	/**
	 * Changes the parameter value.
	 * If such parameter does not exist, nothing
	 * is done.
	 * 
	 * @param name
	 * Name of the parameter
	 * @param value
	 */
	public void setParameterValue(String name, Object value) {
		Cell cell = cellmap.get(name);
		
		if (cell != null) {
			cell.setValue(value);
		}
	}
	
	public void refreshValues(Map<String, IValue> values) {
		for (Entry<String, Cell> ntr : cellmap.entrySet()) {
			ntr.getValue().setValue(values.get(ntr.getKey()).value());
		}
	}
	
	private void rebuildFromParameters(List<IParameter> parameters, Map<String, IValue> values) {
		// dispose cells
		for (Entry<String, Cell> ntr : cellmap.entrySet()) {
			ntr.getValue().dispose();
		}
		table.removeAll();
		cellmap.clear();
		
		// rebuild from parameter list
		if (parameters == null) return;
		
		cellListener = new ICellListener() {
			public void onCellValueChange(int xpos, int ypos, Object nval) {
				informListenersAndRefresh(ypos, nval);
			}
			public void onCellFocus(int xpos, int ypos) {
				informListenersOfFocus(ypos);
			}
		};
		
		int y = -1;
		for (IParameter p : parameters) {
			y++;
			
			// add a new item
			TableItem item = new TableItem(table, SWT.NONE);
			
			// set label as first field
			TableEditor tabed = new TableEditor(table);
			tabed.grabHorizontal = true;
			CLabel paramname = new CLabel(table, SWT.NONE);
			paramname.setText(p.getName());
			paramname.setToolTipText(getTooltipForParameter(p));
			paramname.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			tabed.setEditor(paramname, item, 0);
			
			addCellEditor(p, y, item);
			
			// set parameter value in cell editor
			cellmap.get(p.getName()).setValue(values.get(p.getName()).value());
			currentvalues.add(values.get(p.getName()).value());
		}
		
		pack();
	}
	
	private String getTooltipForParameter(IParameter p) {
		StringBuilder sb = new StringBuilder(p.getDescription());
		sb.append('\n').append(p.getConstraintDescription()).append('\n');
		sb.append("Type: ");
		sb.append(p.getParamType().niceName());
		return sb.toString();
	}

	private void addCellEditor(IParameter p, int paramIndex, TableItem item) {
		TableEditor tableEditor = new TableEditor(table);
		tableEditor.grabHorizontal = true;
		if (p.getConstraint() == ConstraintTypes.ENUMERATION) {
			// create combo
			ComboCell combocell = new ComboCell(table, 1, paramIndex, p.getAllowed(), SWT.NONE);

			// set combo to table cell
			tableEditor.setEditor(combocell, item, 1);
			
			// set change listener to combo
			combocell.addCellListener(cellListener);
			
			// pack cell
			combocell.pack();
			
			// add cell to cellmap
			cellmap.put(p.getName(), combocell);
		} else {
			// create a specific control in cell
			ParameterTypes ptype = p.getParamType();
			Cell cell = null;
			switch (ptype) {
			case TIME:
				TimeCell tc = new TimeCell(table, 1, paramIndex, SWT.NONE);
				tableEditor.setEditor(tc, item, 1);
				tc.addCellListener(cellListener);
				cell = tc;
				break;
			case INTEGER:
				IntegerCell ic = new IntegerCell(table, 1, paramIndex, SWT.NONE);
				tableEditor.setEditor(ic, item, 1);
				ic.addCellListener(cellListener);
				cell = ic;
				break;
			case REAL:
				RealCell rc = new RealCell(table, 1, paramIndex, SWT.NONE);
				tableEditor.setEditor(rc, item, 1);
				rc.addCellListener(cellListener);
				cell = rc;
				break;
			case STRING:
				StringCell sc = new StringCell(table, 1, paramIndex, SWT.NONE);
				tableEditor.setEditor(sc, item, 1);
				sc.addCellListener(cellListener);
				cell = sc;
				break;
			case BOOLEAN:
				BooleanCell bc = new BooleanCell(table, 1, paramIndex, SWT.NONE);
				tableEditor.setEditor(bc, item, 1);
				bc.addCellListener(cellListener);
				cell = bc;
				break;
			case ISERIALIZABLE:
				Class<ISerializable> cls = p.getValueClass();
				SerializableCell sercell = new SerializableCell(table, 1, paramIndex, cls, SWT.NONE);
				tableEditor.setEditor(sercell, item, 1);
				sercell.addCellListener(cellListener);
				cell = sercell;
				break;
			}
			cell.pack();
			cell.setToolTipText(p.getDescription());
			
			// add parameter to position map
			cellmap.put(p.getName(), cell);
		}
	}
	
	public void addParameterListener(IParameterListener l) {
		listeners.add(l);
	}
	
	public boolean removeParameterListener(IParameterListener l) {
		return listeners.remove(l);
	}
	
	private void informListenersOfFocus(int ypos) {
		// nothin' yet
	}
	
	private void informListenersAndRefresh(int paramIndex, Object nval) {
		IParameter param = parameters.get(paramIndex);
		if (param.getConstraint() != null) {
			if (!param.isValueValid(nval)) {
				Cell c = cellmap.get(param.getName());
				c.setValue(currentvalues.get(paramIndex));
				return;
			}
		}
		
		currentvalues.set(paramIndex, nval);
		
		for (IParameterListener l : listeners) {
			l.onParameterValueChange(paramIndex, param, nval);
		}
		
		// refresh value in question
		table.getItem(paramIndex).setText(1, nval.toString());
	}

	public void rebuild(List<IParameter> paramlist, Map<String, IValue> paramvalmap) {
		parameters = paramlist;
		currentvalues.clear();
		for (IParameter p : parameters) {
			currentvalues.add(paramvalmap.get(p.getName()).value());
		}
		
		int wdt = getSize().x;
		int hgt = getSize().y;
		rebuildFromParameters(paramlist, paramvalmap);
		pack();
		setSize(wdt, hgt);
	}
	
	
	
}














