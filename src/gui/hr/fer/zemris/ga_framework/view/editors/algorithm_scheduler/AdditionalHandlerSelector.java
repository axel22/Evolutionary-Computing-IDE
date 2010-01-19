package hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler;

import hr.fer.zemris.ga_framework.model.HandlerTypes;
import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.ParameterTypes;
import hr.fer.zemris.ga_framework.view.ImageLoader;
import hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler.Model.AdditionalHandler;

import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
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
import org.eclipse.swt.widgets.Text;

public class AdditionalHandlerSelector extends Dialog {

	/* static fields */
	private static final ImageData ICON_HANDLER = ImageLoader.loadImage("icons", "arrow_right.png");
	private CCombo policyCombo;
	private CCombo actualValueCombo;
	private Text handlerName;
	private List<IParameter> retvals;
	
	protected AdditionalHandler result;
	protected Shell shell;
	private Set<String> illegalnames;

	/**
	 * Create the dialog
	 * @param parent
	 * @param style
	 */
	public AdditionalHandlerSelector(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Create the dialog
	 * @param parent
	 */
	public AdditionalHandlerSelector(Shell parent) {
		this(parent, SWT.NONE);
	}

	/**
	 * Open the dialog
	 * @return the result
	 */
	public AdditionalHandler open(List<IParameter> availableReturnTypes, Set<String> forbiddenNames) {
		initGUI();
		advinitGUI(availableReturnTypes, forbiddenNames);
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return result;
	}

	private void advinitGUI(List<IParameter> availableReturnTypes, Set<String> forbiddenNames) {
		// set image
		shell.setImage(new Image(shell.getDisplay(), ICON_HANDLER));
		
		// set as field
		retvals = availableReturnTypes;
		illegalnames = forbiddenNames;
		
		// fill combos
		for (IParameter rv : retvals) {
			actualValueCombo.add(rv.getName());
		}
		
		// center dialog
		Point pos = shell.getParent().getLocation(), sz = shell.getParent().getSize();
		shell.setLocation(pos.x + (sz.x - shell.getSize().x) / 2, pos.y + (sz.y - shell.getSize().y) / 2);
	}

	/**
	 * Create contents of the dialog
	 */
	protected void initGUI() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.verticalSpacing = 2;
		gridLayout_1.marginWidth = 2;
		gridLayout_1.marginHeight = 2;
		gridLayout_1.horizontalSpacing = 2;
		shell.setLayout(gridLayout_1);
		shell.setSize(465, 109);
		shell.setText("Additional return value");

		final Composite composite_2 = new Composite(shell, SWT.NONE);
		composite_2.setLayout(new FillLayout());
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final Label chooseAHandlerLabel = new Label(composite_2, SWT.NONE);
		chooseAHandlerLabel.setText("Choose a handler for the original value and a policy.");

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.verticalSpacing = 2;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 2;
		gridLayout.horizontalSpacing = 2;
		composite.setLayout(gridLayout);

		final ViewForm viewForm = new ViewForm(composite, SWT.FLAT | SWT.BORDER);
		viewForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		handlerName = new Text(viewForm, SWT.NONE);
		viewForm.setContent(handlerName);

		final ViewForm viewForm_1 = new ViewForm(composite, SWT.FLAT | SWT.BORDER);

		actualValueCombo = new CCombo(viewForm_1, SWT.NONE);
		actualValueCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				policyCombo.removeAll();
				int selind = actualValueCombo.getSelectionIndex();
				if (selind != -1) {
					IParameter rv = retvals.get(selind);
					ParameterTypes pt = rv.getParamType();
					for (HandlerTypes ht : HandlerTypes.values()) {
						if (pt.doesAllowHandler(rv.getValueClass(), ht)) policyCombo.add(ht.toString());
					}
				}
			}
		});
		viewForm_1.setContent(actualValueCombo);

		final ViewForm viewForm_2 = new ViewForm(composite, SWT.FLAT | SWT.BORDER);

		policyCombo = new CCombo(viewForm_2, SWT.NONE);
		viewForm_2.setContent(policyCombo);

		final Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 2;
		composite_1.setLayout(gridLayout_2);

		final Button okButton = new Button(composite_1, SWT.NONE);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				String errortext = null;
				
				// check policy
				int selind = policyCombo.getSelectionIndex();
				String policy = null;
				if (selind != -1) policy = policyCombo.getItem(selind);
				else errortext = "Must choose a policy for the handler.";
				
				// check actual value
				selind = actualValueCombo.getSelectionIndex();
				String actnm = null;
				if (selind != -1) actnm = actualValueCombo.getItem(selind);
				else errortext = "Must choose an actual return value for this handler.";
				
				// check name
				String nm = handlerName.getText();
				if (nm.trim().equals("")) errortext = "Name cannot be empty.";
				if (illegalnames.contains(nm)) errortext = "Name '" + nm + "' is already in use. Choose a different name.";

				if (errortext != null) {
					MessageBox mb = new MessageBox(shell, SWT.OK);
					mb.setText("Warning.");
					mb.setMessage(errortext);
					mb.open();
				} else {
					result = new AdditionalHandler();
					result.setHandlerName(nm);
					result.setActualValueName(actnm);
					result.setHandler(policy);
					shell.close();
				}
			}
		});
		okButton.setText("OK");

		final Button cancelButton = new Button(composite_1, SWT.NONE);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				shell.close();
			}
		});
		final GridData gd_cancelButton = new GridData();
		cancelButton.setLayoutData(gd_cancelButton);
		cancelButton.setText("Cancel");
	}

}














