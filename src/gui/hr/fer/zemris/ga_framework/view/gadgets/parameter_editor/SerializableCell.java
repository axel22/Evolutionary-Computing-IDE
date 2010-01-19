package hr.fer.zemris.ga_framework.view.gadgets.parameter_editor;

import hr.fer.zemris.ga_framework.model.IParameterDialog;
import hr.fer.zemris.ga_framework.model.ISerializable;
import hr.fer.zemris.ga_framework.model.misc.Pair;
import hr.fer.zemris.ga_framework.view.ImageLoader;
import hr.fer.zemris.ga_framework.view.gadgets.parameter_editor.dialogs.ParameterDialogFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;



/**
 * Cell for setting ISerializable objects.
 * 
 * @author Axel
 *
 */
public class SerializableCell extends Cell {
	
	private class OkCancelComposite extends Composite {
		private Shell parentshell;
		private IParameterDialog dialog;
		private boolean okflag;
		public OkCancelComposite(Shell sh, int style) {
			super(sh, style);
			parentshell = sh;
			okflag = false;
			advinitGUI();
		}
		@Override
		protected void checkSubclass() {
		}
		public IParameterDialog getParameterDialog() {
			return dialog;
		}
		private void advinitGUI() {
			this.setLayout(new GridLayout(1, true));
			
			dialog = ParameterDialogFactory.createDialog(valclass, this);
			Composite cdialog = (Composite)dialog;
			if (dialog == null) {
				throw new IllegalArgumentException("Dialog was null - factory returned nothing.");
			}
			cdialog.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			dialog.setValue(val);
			
			Composite yesNoComp = new Composite(this, SWT.NONE);
			yesNoComp.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
			yesNoComp.setLayout(new GridLayout(2, true));
			
			Button ok = new Button(yesNoComp, SWT.PUSH);
			ok.setText("OK");
			ok.setLayoutData(new GridData(GridData.CENTER));
			ok.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event arg0) {
					okflag = true;
					parentshell.close();
				}
			});
			
			Button cancel = new Button(yesNoComp, SWT.PUSH);
			cancel.setText("Cancel");
			cancel.setLayoutData(new GridData(GridData.CENTER));
			cancel.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event arg0) {
					okflag = false;
					parentshell.close();
				}
			});
		}
		public boolean wasOkPressed() {
			return okflag;
		}
	}
	
	

	/* static fields */
	private static final ImageData DOTS = ImageLoader.loadImage("icons", "dots.png");
	private static final ImageData PARAM_DIALOG = ImageLoader.loadImage("icons", "paramdialog.png");
	

	/* private fields */
	private Label label;
	private Class<ISerializable> valclass;
	private ISerializable val;
	

	/* ctors */

	public SerializableCell(Composite c, int x, int y, Class<ISerializable> valueClass, int style) {
		super(c, style, x, y);
		
		valclass = valueClass;
		
		initGUI();
		advinitGUI();
	}



	/* methods */

	@Override
	public Object getValue() {
		return val.deepCopy();
	}
	
	@Override
	public void setValue(Object o) {
		if (!(o instanceof ISerializable)) return;
		val = (ISerializable)o;
		label.setText(val.toString());
	}

	private void initGUI() {
		GridData data;
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);
		
		label = new Label(this, SWT.NONE);
		label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		final GridData gd_label = new GridData(GridData.FILL_BOTH);
		label.setLayoutData(gd_label);
		label.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				informListenersOfFocus();
			}
		});

		final Button button = new Button(this, SWT.NONE);
		data = new GridData(GridData.FILL_VERTICAL);
		data.widthHint = 24;
		button.setLayoutData(data);
		button.setImage(new Image(this.getDisplay(), DOTS));
		button.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				informListenersOfFocus();
				
				Shell dialog = new Shell(SerializableCell.this.getShell(), SWT.APPLICATION_MODAL | SWT.TITLE | SWT.CLOSE);
				dialog.setLayout(new FillLayout());
				dialog.setImage(new Image(SerializableCell.this.getDisplay(), PARAM_DIALOG));
				IParameterDialog dgcomp = null;
				OkCancelComposite okc = null;
				try {
					okc = new OkCancelComposite(dialog, SWT.NONE);
					dgcomp = okc.getParameterDialog();
					dgcomp.setValue(val);
					dialog.setText("Parameter dialog");
					Pair<Integer, Integer> sz = dgcomp.getDimensions();
					dialog.setSize(sz.getFirst(), sz.getSecond());
					center(dialog, SerializableCell.this.getShell());
				} catch (IllegalArgumentException ex) {
					MessageBox box = new MessageBox(SerializableCell.this.getShell(), SWT.OK);
					box.setText("Missing dialog");
					box.setMessage("No dialog is available for this parameter type.");
					box.open();
					return;
				}
				dialog.open();
				while (!dialog.isDisposed()) {
					if (!dialog.getDisplay().readAndDispatch()) dialog.getDisplay().sleep();
				}
				if (okc.wasOkPressed()) {
					val = dgcomp.getValue();
					label.setText(val.toString());
					informListeners(val);
				}
			}

			private void center(Shell dialog, Shell shell) {
				Point loc = shell.getLocation();
				Point sz = shell.getSize();
				Point dsz = dialog.getSize();
				dialog.setLocation(loc.x + sz.x / 2 - dsz.x / 2, loc.y + sz.y / 2 - dsz.y / 2);
			}
		});
	}

	private void advinitGUI() {
		val = null;
	}
	
}














