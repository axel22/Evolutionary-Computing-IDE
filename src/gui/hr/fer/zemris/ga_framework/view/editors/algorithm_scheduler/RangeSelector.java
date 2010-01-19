package hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler;

import hr.fer.zemris.ga_framework.model.ConstraintTypes;
import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.ParameterTypes;
import hr.fer.zemris.ga_framework.model.impl.criteriums.PositiveIntegerCriterium;
import hr.fer.zemris.ga_framework.model.impl.criteriums.PositiveRealCriterium;
import hr.fer.zemris.ga_framework.model.impl.criteriums.PositiveTimeCriterium;
import hr.fer.zemris.ga_framework.model.impl.parameters.PrimitiveParameter;
import hr.fer.zemris.ga_framework.model.misc.Time;
import hr.fer.zemris.ga_framework.model.misc.Time.Metric;
import hr.fer.zemris.ga_framework.view.ImageLoader;
import hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler.Model.IRange;
import hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler.Model.RangeData;
import hr.fer.zemris.ga_framework.view.gadgets.parameter_editor.Cell;
import hr.fer.zemris.ga_framework.view.gadgets.parameter_editor.CellFactory;
import hr.fer.zemris.ga_framework.view.gadgets.parameter_editor.ICellListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class RangeSelector extends Dialog {

	/* static fields */
	private static final ImageData ICON_RANGES = ImageLoader.loadImage("icons", "param_lengths.png");
	
	/* private fields */
	private Button addAllButton;
	private Button removeButton;
	private Button enumeratedButton;
	private Button singleValueButton;
	private Button intervalButton;
	private Composite stepCellSlot;
	private Composite toCellSlot;
	private Composite fromCellSlot;
	private Composite enumCellSlot;
	private Table table;
	private Composite intValComp;
	private Composite enumValComp;
	private Composite singleCellSlot;
	private Label descriptionSlotLabel;
	private Label paramTypeSlotLabel;
	private Label constraintSlotLabel;
	private Label paramNameSlotLabel;
	private IParameter parameter;
	private Object defaultvalue;
	private List<Object> enumerated;
	private IRange result;
	private IRange oldresult;
	protected Shell shell;
	
	/* ctors */

	/**
	 * Create the dialog
	 * @param parent
	 * @param style
	 */
	public RangeSelector(Shell parent, int style, IParameter param, Object defval, IRange oldrange) {
		super(parent, style);
		
		parameter = param;
		defaultvalue = defval;
		enumerated = new ArrayList<Object>();
		oldresult = oldrange;
	}

	/**
	 * Create the dialog
	 * @param parent
	 */
	public RangeSelector(Shell parent, IParameter param, Object defval, IRange oldrange) {
		this(parent, SWT.NONE, param, defval, oldrange);
	}

	/* methods */
	
	/**
	 * Open the dialog
	 * @return the result
	 */
	public IRange open() {
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
		shell.setImage(new Image(shell.getDisplay(), ICON_RANGES));
		
		// set description
		paramNameSlotLabel.setText(parameter.getName());
		paramTypeSlotLabel.setText(parameter.getParamType().niceName());
		constraintSlotLabel.setText(parameter.getConstraintDescription());
		descriptionSlotLabel.setText(parameter.getDescription());
		
		// set cells
		Cell c = CellFactory.createCell(singleCellSlot, parameter, 0, 0, SWT.BORDER);
		c.setValue(defaultvalue);
		c.addCellListener(new ICellListener() {
			public void onCellValueChange(int xpos, int ypos, Object nval) {
			}
			public void onCellFocus(int xpos, int ypos) {
				setRadioSelection(1);
			}
		});
		c = CellFactory.createCell(fromCellSlot, parameter, 0, 0, SWT.BORDER);
		c.setValue(defaultvalue);
		c.addCellListener(new ICellListener() {
			public void onCellValueChange(int xpos, int ypos, Object nval) {
			}
			public void onCellFocus(int xpos, int ypos) {
				setRadioSelection(2);
			}
		});
		c = CellFactory.createCell(toCellSlot, parameter, 0, 0, SWT.BORDER);
		c.setValue(defaultvalue);
		c.addCellListener(new ICellListener() {
			public void onCellValueChange(int xpos, int ypos, Object nval) {
			}
			public void onCellFocus(int xpos, int ypos) {
				setRadioSelection(2);
			}
		});
		c = CellFactory.createCell(stepCellSlot, getStepParameter(), 0, 0, SWT.BORDER);
		c.setValue(getDefaultStep(parameter.getParamType(), defaultvalue));
		c.addCellListener(new ICellListener() {
			public void onCellValueChange(int xpos, int ypos, Object nval) {
			}
			public void onCellFocus(int xpos, int ypos) {
				setRadioSelection(2);
			}
		});
		c = CellFactory.createCell(enumCellSlot, parameter, 0, 0, SWT.BORDER);
		c.setValue(defaultvalue);
		c.addCellListener(new ICellListener() {
			public void onCellValueChange(int xpos, int ypos, Object nval) {
			}
			public void onCellFocus(int xpos, int ypos) {
				setRadioSelection(3);
			}
		});
		c.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				setRadioSelection(3);
			}
		});
		
		// disable radio if needed
		ParameterTypes tp = parameter.getParamType();
		if (tp != ParameterTypes.INTEGER && tp != ParameterTypes.REAL && tp != ParameterTypes.TIME) {
			intervalButton.setEnabled(false);
			fromCellSlot.getChildren()[0].setEnabled(false);
			toCellSlot.getChildren()[0].setEnabled(false);
			stepCellSlot.getChildren()[0].setEnabled(false);
			intValComp.setVisible(false);
		}
		
		// enable "Add all" button if needed
		if (parameter.getConstraint() == ConstraintTypes.ENUMERATION) {
			addAllButton.setEnabled(true);
			addAllButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					List<Object> allowed = parameter.getAllowed();
					
					table.removeAll();
					enumerated.clear();
					
					for (Object o : allowed) {
						addToEnumerated(o);
					}
					
					setRadioSelection(3);
				}
			});
		}
		
		// rebuild from old result
		RangeData data = oldresult.getRangeData();
		if (data.singleVal != null) {
			setRadioSelection(1);
			((Cell)singleCellSlot.getChildren()[0]).setValue(data.singleVal);
		}
		if (data.from != null) {
			setRadioSelection(2);
			((Cell)fromCellSlot.getChildren()[0]).setValue(data.from);
			((Cell)toCellSlot.getChildren()[0]).setValue(data.to);
			((Cell)stepCellSlot.getChildren()[0]).setValue(data.step);
		}
		if (data.enumeration != null) {
			setRadioSelection(3);
			enumerated = data.enumeration;
			for (Object o : enumerated) {
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(o.toString());
			}
		}
	}
	
	private IParameter getStepParameter() {
		switch (parameter.getParamType()) {
		case INTEGER:
			return new PrimitiveParameter(parameter.getName(), parameter.getDescription(), parameter.getParamType(),
					new PositiveIntegerCriterium());
		case REAL:
			return new PrimitiveParameter(parameter.getName(), parameter.getDescription(), parameter.getParamType(),
					new PositiveRealCriterium());
		case TIME:
			return new PrimitiveParameter(parameter.getName(), parameter.getDescription(), parameter.getParamType(),
					new PositiveTimeCriterium());
		}
		
		return parameter;
	}

	protected void setRadioSelection(int i) {
		enumeratedButton.setSelection(false);
		intervalButton.setSelection(false);
		singleValueButton.setSelection(false);
		switch(i) {
		case 1:
			singleValueButton.setSelection(true);
			break;
		case 2:
			intervalButton.setSelection(true);
			break;
		case 3:
			enumeratedButton.setSelection(true);
			break;
		}
	}

	private Object getDefaultStep(ParameterTypes pt, Object defaultValue) {
		switch (pt) {
		case INTEGER:
			Integer i = new Integer((Integer)defaultValue / 10);
			if (i < 1) i = 1;
			return i;
		case REAL:
			Double d = new Double((Double)defaultValue / 10);
			return d;
		case TIME:
			Time t = (Time)defaultValue;
			Time ret = new Time(t.getInterval() / 10, t.getMetric());
			return ret;
		}
		return defaultValue;
	}

	/**
	 * Create contents of the dialog
	 */
	protected void initGUI() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 3;
		gridLayout.marginHeight = 3;
		gridLayout.horizontalSpacing = 3;
		shell.setLayout(gridLayout);
		shell.setSize(450, 385);
		shell.setText("Range editor");
		shell.setLocation(getParent().getLocation().x + getParent().getSize().x / 2 - shell.getSize().x / 2,
				getParent().getLocation().y + getParent().getSize().y / 2 - shell.getSize().y / 2);

		final Composite composite = new Composite(shell, SWT.NONE);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		gridLayout_1.verticalSpacing = 4;
		gridLayout_1.marginWidth = 4;
		gridLayout_1.marginHeight = 4;
		gridLayout_1.horizontalSpacing = 4;
		composite.setLayout(gridLayout_1);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Label parameterNameLabel = new Label(composite, SWT.NONE);
		parameterNameLabel.setText("Parameter name:");

		paramNameSlotLabel = new Label(composite, SWT.BORDER);
		final GridData gd_paramNameSlotLabel = new GridData(SWT.FILL, SWT.CENTER, true, false);
		paramNameSlotLabel.setLayoutData(gd_paramNameSlotLabel);

		final Label parameterTypeLabel = new Label(composite, SWT.NONE);
		parameterTypeLabel.setText("Parameter type:");

		paramTypeSlotLabel = new Label(composite, SWT.BORDER);
		paramTypeSlotLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Label constraintLabel = new Label(composite, SWT.NONE);
		constraintLabel.setText("Constraint:");

		constraintSlotLabel = new Label(composite, SWT.BORDER);
		constraintSlotLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Label descriptionLabel = new Label(composite, SWT.NONE);
		descriptionLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		descriptionLabel.setText("Description:");

		descriptionSlotLabel = new Label(composite, SWT.WRAP | SWT.BORDER);
		final GridData gd_descriptionSlotLabel = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_descriptionSlotLabel.heightHint = 42;
		descriptionSlotLabel.setLayoutData(gd_descriptionSlotLabel);

		final Label label = new Label(shell, SWT.HORIZONTAL | SWT.SEPARATOR);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		label.setText("Label");

		final Composite composite_2 = new Composite(shell, SWT.NONE);
		final GridData gd_composite_2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_composite_2.heightHint = 130;
		composite_2.setLayoutData(gd_composite_2);
		final GridLayout gridLayout_3 = new GridLayout();
		gridLayout_3.marginWidth = 0;
		gridLayout_3.marginHeight = 0;
		gridLayout_3.numColumns = 2;
		composite_2.setLayout(gridLayout_3);

		singleValueButton = new Button(composite_2, SWT.RADIO);
		singleValueButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		singleValueButton.setText("Single value");

		singleCellSlot = new Composite(composite_2, SWT.NONE);
		singleCellSlot.setLayout(new FillLayout());
		final GridData gd_singleCellSlot = new GridData(SWT.LEFT, SWT.FILL, true, false);
		gd_singleCellSlot.heightHint = 20;
		gd_singleCellSlot.widthHint = 106;
		singleCellSlot.setLayoutData(gd_singleCellSlot);

		intervalButton = new Button(composite_2, SWT.RADIO);
		final GridData gd_intervalButton = new GridData(SWT.LEFT, SWT.FILL, false, false);
		gd_intervalButton.heightHint = 18;
		intervalButton.setLayoutData(gd_intervalButton);
		intervalButton.setText("Interval");

		intValComp = new Composite(composite_2, SWT.NONE);
		final GridData gd_intValComp = new GridData(SWT.FILL, SWT.FILL, true, false);
		intValComp.setLayoutData(gd_intValComp);
		final GridLayout gridLayout_6 = new GridLayout();
		gridLayout_6.numColumns = 6;
		gridLayout_6.verticalSpacing = 3;
		gridLayout_6.marginWidth = 3;
		gridLayout_6.marginHeight = 0;
		gridLayout_6.horizontalSpacing = 3;
		intValComp.setLayout(gridLayout_6);

		final Label fromLabel = new Label(intValComp, SWT.NONE);
		fromLabel.setText("From ");

		fromCellSlot = new Composite(intValComp, SWT.NONE);
		fromCellSlot.setLayout(new FillLayout());
		final GridData gd_fromCellSlot = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_fromCellSlot.heightHint = 20;
		fromCellSlot.setLayoutData(gd_fromCellSlot);

		final Label toLabel = new Label(intValComp, SWT.NONE);
		toLabel.setText(" to ");

		toCellSlot = new Composite(intValComp, SWT.NONE);
		toCellSlot.setLayout(new FillLayout());
		final GridData gd_toCellSlot = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_toCellSlot.heightHint = 20;
		toCellSlot.setLayoutData(gd_toCellSlot);

		final Label withStepLabel = new Label(intValComp, SWT.NONE);
		withStepLabel.setText(" with step ");

		stepCellSlot = new Composite(intValComp, SWT.NONE);
		stepCellSlot.setLayout(new FillLayout());
		final GridData gd_stepCellSlot = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_stepCellSlot.heightHint = 20;
		stepCellSlot.setLayoutData(gd_stepCellSlot);

		enumeratedButton = new Button(composite_2, SWT.RADIO);
		enumeratedButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		enumeratedButton.setText("Enumerated");

		enumValComp = new Composite(composite_2, SWT.NONE);
		final GridData gd_enumValComp = new GridData(SWT.FILL, SWT.FILL, true, false);
		enumValComp.setLayoutData(gd_enumValComp);
		final GridLayout gridLayout_4 = new GridLayout();
		gridLayout_4.verticalSpacing = 3;
		gridLayout_4.marginWidth = 3;
		gridLayout_4.marginHeight = 3;
		gridLayout_4.horizontalSpacing = 3;
		gridLayout_4.numColumns = 2;
		enumValComp.setLayout(gridLayout_4);

		table = new Table(enumValComp, SWT.FULL_SELECTION | SWT.BORDER);
		table.setLinesVisible(true);
		final GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_table.heightHint = 123;
		table.setLayoutData(gd_table);
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (removeButton == null) return;
				
				setRemoveButtonEnabled();
				
				// select group
				setRadioSelection(3);
			}
		});

		final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
		newColumnTableColumn.setWidth(211);
		newColumnTableColumn.setText("Value");

		final Composite composite_3 = new Composite(enumValComp, SWT.NONE);
		composite_3.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		final GridLayout gridLayout_5 = new GridLayout();
		gridLayout_5.verticalSpacing = 2;
		gridLayout_5.marginWidth = 0;
		gridLayout_5.marginHeight = 0;
		gridLayout_5.horizontalSpacing = 2;
		composite_3.setLayout(gridLayout_5);

		enumCellSlot = new Composite(composite_3, SWT.NONE);
		enumCellSlot.setLayout(new FillLayout());
		final GridData gd_enumCellSlot = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_enumCellSlot.heightHint = 20;
		gd_enumCellSlot.widthHint = 101;
		enumCellSlot.setLayoutData(gd_enumCellSlot);

		final Button addButton = new Button(composite_3, SWT.NONE);
		addButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		addButton.setText("Add");
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Object obj = ((Cell)enumCellSlot.getChildren()[0]).getValue();
				
				addToEnumerated(obj);
				
				// select group
				setRadioSelection(3);
			}
		});

		addAllButton = new Button(composite_3, SWT.NONE);
		addAllButton.setEnabled(false);
		final GridData gd_addAllButton = new GridData(SWT.FILL, SWT.CENTER, true, false);
		addAllButton.setLayoutData(gd_addAllButton);
		addAllButton.setText("Add all");

		removeButton = new Button(composite_3, SWT.NONE);
		removeButton.setEnabled(false);
		removeButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		removeButton.setText("Remove");
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				int pos = table.getSelectionIndex();
				if (pos == -1) return;
				int sz = enumerated.size();
				
				enumerated.remove(pos);
				table.remove(pos);
				if (pos == sz - 1) table.select(pos - 1);
				else table.select(pos);
				setRemoveButtonEnabled();
				
				// select group
				setRadioSelection(3);
			}
		});

		final Button removeAllButton = new Button(composite_3, SWT.NONE);
		removeAllButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				enumerated.clear();
				table.removeAll();
				
				setRemoveButtonEnabled();
			}
		});
		final GridData gd_removeAllButton = new GridData(SWT.FILL, SWT.CENTER, true, false);
		removeAllButton.setLayoutData(gd_removeAllButton);
		removeAllButton.setText("Remove all");

		final Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.verticalSpacing = 2;
		gridLayout_2.marginWidth = 2;
		gridLayout_2.marginHeight = 2;
		gridLayout_2.horizontalSpacing = 2;
		gridLayout_2.numColumns = 2;
		composite_1.setLayout(gridLayout_2);

		final Button okButton = new Button(composite_1, SWT.NONE);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				// extract result
				result = extractResult();
				
				if (result != null) shell.dispose();
			}
		});
		okButton.setText("OK");

		final Button cancelButton = new Button(composite_1, SWT.NONE);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				result = null;
				shell.dispose();
			}
		});
		cancelButton.setText("Cancel");
	}
	
	private void addToEnumerated(Object obj) {
		enumerated.add(obj);
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(0, obj.toString());
	}

	private void setRemoveButtonEnabled() {
		int pos = table.getSelectionIndex();
		if (pos == -1) {
			removeButton.setEnabled(false);
		} else {
			removeButton.setEnabled(true);
		}
	}

	/**
	 * Try to extract result. If this is not possible - complain,
	 * and return null.
	 * 
	 * @return
	 */
	private IRange extractResult() {
		if (singleValueButton.getSelection()) {
			final Object val = ((Cell)singleCellSlot.getChildren()[0]).getValue();
			if (!parameter.isValueValid(val)) {
				MessageBox box = new MessageBox(this.getParent(), SWT.OK);
				box.setMessage("Value not allowed by constraint.");
				box.setText("Invalid range");
				box.open();
				return null;
			}
			return new IRange() {
				public String getDescription() {
					return "Single value: " + val.toString();
				}
				public String getParamName() {
					return parameter.getName();
				}
				public Iterator<Object> iterator() {
					return new AlgorithmScheduler.SingleIterator<Object>(val);
				}
				public RangeData getRangeData() {
					RangeData data = new RangeData(parameter);
					
					data.singleVal = val;
					
					return data;
				}
			};
		} else if (intervalButton.getSelection()) {
			ParameterTypes tp = parameter.getParamType();
			
			switch (tp) {
			case INTEGER:
				return getIntegerRange();
			case REAL:
				return getRealRange();
			case TIME:
				return getTimeRange();
			}
			
			return null;
		} else if (enumeratedButton.getSelection()) {
			final int size = enumerated.size();
			if (size == 0) {
				MessageBox box = new MessageBox(this.getParent(), SWT.OK);
				box.setMessage("Enumerated values list is empty.");
				box.setText("Invalid range");
				box.open();
				return null;
			}
			for (Object obj : enumerated) {
				if (!parameter.isValueValid(obj)) {
					MessageBox box = new MessageBox(this.getParent(), SWT.OK);
					box.setMessage("Value " + obj + " not allowed by constraint.");
					box.setText("Invalid range");
					box.open();
					return null;
				}
			}
			
			return new IRange() {
				public String getDescription() {
					StringBuilder sb = new StringBuilder("Enumerated set (");
					sb.append(size).append(" value");
					if (size != 1) sb.append("s");
					sb.append("): ");
					int count = 0;
					boolean start = true;
					for (Object val : enumerated) {
						if (!start) sb.append(", "); else start = !start;
						sb.append(val);
						count++;
						if (count == 4) {
							if (count < size) sb.append(", ...");
							break;
						}
					}
					return sb.toString();
				}
				public String getParamName() {
					return parameter.getName();
				}
				public Iterator<Object> iterator() {
					return enumerated.iterator();
				}
				public RangeData getRangeData() {
					RangeData data = new RangeData(parameter);
					
					data.enumeration = enumerated;
					
					return data;
				}
			};
		} else {
			throw new IllegalStateException("No radio selected.");
		}
	}

	private IRange getRealRange() {
		final Double from = (Double)((Cell)fromCellSlot.getChildren()[0]).getValue();
		final Double to = (Double)((Cell)toCellSlot.getChildren()[0]).getValue();
		final Double step = (Double)((Cell)stepCellSlot.getChildren()[0]).getValue();
		if (from > to) {
			MessageBox box = new MessageBox(this.getParent(), SWT.OK);
			box.setText("Invalid range");
			box.setMessage("Lower value greater than higher value.");
			box.open();
			return null;
		}
		
		try {
			return new RealStepRange(from, to, step, parameter);
		} catch (IllegalArgumentException e) {
			MessageBox box = new MessageBox(this.getParent(), SWT.OK);
			box.setText("Invalid range");
			box.setMessage(e.getMessage());
			box.open();
			return null;
		}
	}

	private IRange getIntegerRange() {
		final Integer from = (Integer)((Cell)fromCellSlot.getChildren()[0]).getValue();
		final Integer to = (Integer)((Cell)toCellSlot.getChildren()[0]).getValue();
		final Integer step = (Integer)((Cell)stepCellSlot.getChildren()[0]).getValue();
		if (from > to) {
			MessageBox box = new MessageBox(this.getParent(), SWT.OK);
			box.setText("Invalid range");
			box.setMessage("Lower value greater than higher value.");
			box.open();
			return null;
		}
		
		try {
			return new IntegerStepRange(from, step, to, parameter);
		} catch (IllegalArgumentException e) {
			MessageBox box = new MessageBox(this.getParent(), SWT.OK);
			box.setText("Invalid range");
			box.setMessage(e.getMessage());
			box.open();
			return null;
		}
	}

	private IRange getTimeRange() {
		final Time from = ((Time)((Cell)fromCellSlot.getChildren()[0]).getValue()).convertTo(Metric.us);
		final Time to = ((Time)((Cell)toCellSlot.getChildren()[0]).getValue()).convertTo(Metric.us);
		final Time step = ((Time)((Cell)stepCellSlot.getChildren()[0]).getValue()).convertTo(Metric.us);
		if (from.getInterval() > to.getInterval()) {
			MessageBox box = new MessageBox(this.getParent(), SWT.OK);
			box.setText("Invalid range");
			box.setMessage("Lower value greater than higher value.");
			box.open();
			return null;
		}
		
		try {
			return new TimeStepRange(from, step, to, parameter);
		} catch (IllegalArgumentException e) {
			MessageBox box = new MessageBox(this.getParent(), SWT.OK);
			box.setText("Invalid range");
			box.setMessage(e.getMessage());
			box.open();
			return null;
		}
	}

}














