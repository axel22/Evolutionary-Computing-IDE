package hr.fer.zemris.ga_framework.view.gadgets.parameter_editor;

import hr.fer.zemris.ga_framework.model.misc.Time;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;



public class TimeCell extends Cell {

	
	/* static fields */
	

	/* private fields */
	private CCombo combo;
	private Text text;
	private String oldtext;
	

	/* ctors */
	
	public TimeCell(Composite c, int xc, int yc, int style) {
		super(c, style, xc, yc);
		
		initGUI();
		advinitGUI();
	}

	
	
	/* methods */

	private void initGUI() {
		final GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.numColumns = 2;
		setLayout(gridLayout);
		
		text = new Text(this, SWT.NONE);
		text.addFocusListener(new FocusAdapter() {
			public void focusLost(final FocusEvent arg0) {
				changeOccured();
			}
			@Override
			public void focusGained(FocusEvent arg0) {
				informListenersOfFocus();
			}
		});
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.keyCode == SWT.CR) changeOccured();
			}
		});
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		text.setLayoutData(data);

		combo = new CCombo(this, SWT.NONE);
		combo.addFocusListener(new FocusAdapter() {
			public void focusLost(final FocusEvent arg0) {
				changeOccured();
			}
			@Override
			public void focusGained(FocusEvent arg0) {
				informListenersOfFocus();
			}
		});
		combo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				changeOccured();
			}
		});
		data = new GridData(SWT.FILL, SWT.FILL, false, true);
		data.widthHint = 40;
		combo.setLayoutData(data);
	}
	
	private void advinitGUI() {
		oldtext = "0";
		text.setText("0");
		
		for (Time.Metric m : Time.Metric.values()) {
			combo.add(m.toString());
		}
		combo.select(0);
	}
	
	private Time.Metric getMetricFromCombo() {
		int sel = combo.getSelectionIndex();
		if (sel == -1) return null;
		return Time.Metric.values()[sel];
	}

	private void changeOccured() {
		Time t = (Time)getValue();
		if (t == null) return;
		
		oldtext = text.getText();
		informListeners(t);
	}

	@Override
	public Object getValue() {
		Time t = null;
		try {
			t = new Time(Double.parseDouble(text.getText()), getMetricFromCombo());
		} catch (NumberFormatException e) {
			text.setText(oldtext);
		} catch (IllegalArgumentException e) {
		}
		
		return t;
	}

	@Override
	public void setValue(Object o) {
		if (!(o instanceof Time)) return;
		Time t = (Time)o;
		text.setText(String.valueOf(t.getInterval()));
		combo.select(Time.Metric.ordinalPosition(t.getMetric()));
	}
	
	
	
}














