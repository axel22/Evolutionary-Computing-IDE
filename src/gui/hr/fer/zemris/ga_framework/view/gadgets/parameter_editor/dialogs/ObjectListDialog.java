package hr.fer.zemris.ga_framework.view.gadgets.parameter_editor.dialogs;

import hr.fer.zemris.ga_framework.model.ISerializable;
import hr.fer.zemris.ga_framework.model.ParameterTypes;
import hr.fer.zemris.ga_framework.model.impl.ObjectList;
import hr.fer.zemris.ga_framework.model.misc.Pair;
import hr.fer.zemris.ga_framework.view.gadgets.parameter_editor.Cell;
import hr.fer.zemris.ga_framework.view.gadgets.parameter_editor.CellFactory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class ObjectListDialog extends ParameterDialog {

	/* static fields */
	
	/* private fields */
	private Button clearAllButton;
	private Button removeButton;
	private Button addButton;
	private Composite cellComposite;
	private Table table;
	private ObjectList lst;
	private Cell inputCell;
	
	/* ctors */
	
	public ObjectListDialog(Composite c, int style) {
		super(c, style);
		initGUI();
	}
	
	/* methods */

	public Pair<Integer, Integer> getDimensions() {
		return new Pair<Integer, Integer>(500, 400);
	}

	public ISerializable getValue() {
		return new ObjectList(lst);
	}

	public Class<? extends ISerializable> isUsedFor() {
		return ObjectList.class;
	}

	public void setValue(ISerializable value) {
		if (!(value instanceof ObjectList)) throw new IllegalArgumentException("This dialog is only intended for ObjectList objects.");
		
		// clear
		if (inputCell != null) inputCell.dispose();
		table.removeAll();
		
		// rebuild
		ObjectList olst = new ObjectList((ObjectList) value);
		ParameterTypes pt = olst.getParameterType();
		List<Object> allowed = olst.getAllowedObjects();
		inputCell = CellFactory.createCell(cellComposite, pt, allowed, null, 0, 0, SWT.BORDER);
		inputCell.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		lst = olst;
		
		for (Object o : lst) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setData(o);
			item.setText(o.toString());
		}
	}
	
	private void initGUI() {
		final GridLayout gridLayout = new GridLayout();
		setLayout(gridLayout);

		final Composite composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.verticalSpacing = 0;
		gridLayout_1.marginWidth = 0;
		gridLayout_1.marginHeight = 0;
		gridLayout_1.horizontalSpacing = 2;
		gridLayout_1.numColumns = 4;
		composite_1.setLayout(gridLayout_1);

		cellComposite = new Composite(composite_1, SWT.NONE);
		final GridData gridData = new GridData(SWT.LEFT, SWT.FILL, false, false);
		gridData.widthHint = 126;
		cellComposite.setLayoutData(gridData);
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.verticalSpacing = 1;
		gridLayout_2.marginWidth = 0;
		gridLayout_2.marginHeight = 0;
		gridLayout_2.horizontalSpacing = 0;
		cellComposite.setLayout(gridLayout_2);

		addButton = new Button(composite_1, SWT.NONE);
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				TableItem item = new TableItem(table, SWT.NONE);
				Object o = inputCell.getValue();
				item.setData(o);
				item.setText(o.toString());
			}
		});
		addButton.setText("Add");

		removeButton = new Button(composite_1, SWT.NONE);
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				int selind = table.getSelectionIndex();
				if (selind == -1) return;
				else table.remove(selind);
			}
		});
		removeButton.setEnabled(false);
		removeButton.setText("Remove");

		clearAllButton = new Button(composite_1, SWT.NONE);
		clearAllButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				table.removeAll();
			}
		});
		clearAllButton.setText("Clear all");

		table = new Table(this, SWT.HIDE_SELECTION | SWT.FULL_SELECTION | SWT.BORDER);
		table.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				ArrayList<Object> arrlst = new ArrayList<Object>();
				
				for (TableItem item : table.getItems()) {
					arrlst.add(item.getData());
				}
				
				lst = new ObjectList(arrlst, lst.getParameterType(), lst.getAllowedObjects());
			}
		});
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				int selind = table.getSelectionIndex();
				if (selind == -1) {
					removeButton.setEnabled(false);
				} else {
					removeButton.setEnabled(true);
				}
			}
		});
		table.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent arg0) {
				table.getColumn(0).setWidth(table.getSize().x - table.getBorderWidth() * 2);
			}
		});
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
		newColumnTableColumn.setWidth(430);
		newColumnTableColumn.setText("Value");
	}

}














