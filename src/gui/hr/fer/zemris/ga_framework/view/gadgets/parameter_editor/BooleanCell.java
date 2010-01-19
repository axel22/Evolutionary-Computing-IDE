package hr.fer.zemris.ga_framework.view.gadgets.parameter_editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;




public class BooleanCell extends Cell {

	/* static fields */

	/* private fields */
	private CCombo combo;
	

	/* ctors */

	public BooleanCell(Composite c, int x, int y, int style) {
		super(c, style, x, y);
		initGUI();
		advinitGUI();
	}



	/* methods */

	@Override
	public Object getValue() {
		int sel = combo.getSelectionIndex();
		if (sel == 0) return true;
		else if (sel == 1) return false;
		else return null;
	}
	
	
	private void initGUI() {
		setLayout(new FillLayout());

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
			public void modifyText(final ModifyEvent arg0) {
				changeOccured();
			}
		});
		combo.setItems(new String[] {"true", "false"});
		
	}
	
	private void changeOccured() {
		Boolean b = (Boolean)getValue();
		
		if (b == null) {
			return;
		}
		
		if (b) combo.select(0);
		else combo.select(1);
	}

	private void advinitGUI() {
		combo.select(0);
	}

	@Override
	public void setValue(Object o) {
		if (!(o instanceof Boolean)) return;
		Boolean b = (Boolean)o;
		if (b) {
			combo.select(0);
		} else {
			combo.select(1);
		}
	}
	
	
}














