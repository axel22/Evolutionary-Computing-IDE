package hr.fer.zemris.ga_framework.view.gadgets.parameter_editor;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;



public class ComboCell extends Cell {
	
	/* static fields */

	/* private fields */
	private CCombo combo;
	private List<Object> allowed;
	

	/* ctors */

	public ComboCell(Composite c, int x, int y, List<Object> allowedVals, int style) {
		super(c, style, x, y);
		
		allowed = allowedVals;
		
		initGUI();
		advinitGUI();
	}



	/* methods */

	@Override
	public Object getValue() {
		int sel = combo.getSelectionIndex();
		
		if (sel == -1) return null;
		
		return allowed.get(sel);
	}
	
	private void initGUI() {
		setLayout(new FillLayout());
		combo = new CCombo(this, SWT.NONE);
		combo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		combo.setEditable(false);
		combo.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent arg0) {
				Object val = getValue();
				if (val != null) {
					combo.setText(val.toString());
					informListeners(val);
				}
			}
		});
		combo.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				Object val = getValue();
				if (val != null) {
					combo.setText(val.toString());
					informListeners(val);
				}
			}
			@Override
			public void focusGained(FocusEvent arg0) {
				informListenersOfFocus();
			}
		});
	}
	
	private void advinitGUI() {
		// fill combo
		for (Object o : allowed) {
			combo.add(o.toString());
		}
		combo.select(0);
	}

	@Override
	public void setValue(Object nval) {
		if (nval == null) return;
		int pos = -1;
		for (Object obj : allowed) {
			++pos;
			if (nval.equals(obj)) {
				combo.select(pos);
				return;
			}
		}
	}
	
}














