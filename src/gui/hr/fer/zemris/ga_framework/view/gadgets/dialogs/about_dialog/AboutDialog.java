package hr.fer.zemris.ga_framework.view.gadgets.dialogs.about_dialog;

import hr.fer.zemris.ga_framework.Application;
import hr.fer.zemris.ga_framework.view.ImageLoader;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

public class AboutDialog extends Dialog {

	/* static fields */
	private Label architectureLabel;
	private static final ImageData IMAGE_ICON = ImageLoader.loadImage("icons", "information.png");
	private static final ImageData IMAGE_DNA = ImageLoader.loadImage("icons", "ga_ide_big.png");
	private static final FontData FONT_TITLE = new FontData("Verdana", 16, SWT.NORMAL);

	/* private fields */
	private Label versionLabel;
	protected Object result;
	protected Shell shell;
	private Label dnalabel;
	private Label evolutionaryComputingIdeLabel;
	private Label runningLabel;
	private Label operatingSystemLabel;

	/* ctors */
	
	/**
	 * Create the dialog
	 * @param parent
	 * @param style
	 */
	public AboutDialog(Shell parent, int style) {
		super(parent, style);
	}
	
	/**
	 * Create the dialog
	 * @param parent
	 */
	public AboutDialog(Shell parent) {
		this(parent, SWT.NONE);
	}

	/* methods */
	
	/**
	 * Open the dialog
	 * @return the result
	 */
	public Object open() {
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
		// set images
		shell.setImage(new Image(shell.getDisplay(), IMAGE_ICON));
		dnalabel.setImage(new Image(shell.getDisplay(), IMAGE_DNA));
		
		// set fonts
		evolutionaryComputingIdeLabel.setFont(new Font(shell.getDisplay(), FONT_TITLE));
		
		// set location
		Point ploc = getParent().getLocation();
		Point psz = getParent().getSize();
		shell.setLocation(ploc.x + (psz.x - shell.getSize().x) / 2, ploc.y + (psz.y - shell.getSize().y) / 2);
		
		// fill missing information in dialog (version, names, etc.)
		versionLabel.setText("Version " + Application.getProperty("version.major") + "." +
				Application.getProperty("version.minor"));
		operatingSystemLabel.setText("Operating system: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
		architectureLabel.setText("Architecture: " + System.getProperty("os.arch"));
		runningLabel.setText("Running on: " + System.getProperty("java.specification.name") + " " +
				System.getProperty("java.specification.version") + " by " + System.getProperty("java.specification.vendor"));
	}

	/**
	 * Create contents of the dialog
	 */
	protected void initGUI() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.verticalSpacing = 0;
		gridLayout_2.marginWidth = 0;
		gridLayout_2.marginHeight = 0;
		gridLayout_2.horizontalSpacing = 0;
		shell.setLayout(gridLayout_2);
		shell.setSize(523, 502);
		shell.setText("About");

		final Composite composite_3 = new Composite(shell, SWT.NONE);
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		final GridLayout gridLayout_3 = new GridLayout();
		gridLayout_3.numColumns = 2;
		gridLayout_3.verticalSpacing = 0;
		gridLayout_3.marginWidth = 0;
		gridLayout_3.marginHeight = 0;
		gridLayout_3.horizontalSpacing = 0;
		composite_3.setLayout(gridLayout_3);

		final ViewForm viewForm = new ViewForm(composite_3, SWT.FLAT | SWT.BORDER);
		viewForm.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
		
		final BannerComposite bc = new BannerComposite(viewForm);

		viewForm.setContent(bc);

		final Composite composite = new Composite(composite_3, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout());

		final Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		composite_1.setLayout(gridLayout);

		dnalabel = new Label(composite_1, SWT.NONE);
		dnalabel.setText("");

		final Composite composite_2 = new Composite(composite_1, SWT.NONE);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.verticalSpacing = 1;
		gridLayout_1.marginWidth = 0;
		gridLayout_1.marginHeight = 0;
		composite_2.setLayout(gridLayout_1);

		evolutionaryComputingIdeLabel = new Label(composite_2, SWT.NONE);
		evolutionaryComputingIdeLabel.setText("Evolutionary Computing IDE");

		versionLabel = new Label(composite_2, SWT.NONE);
		final GridData gd_versionLabel = new GridData();
		gd_versionLabel.horizontalIndent = 4;
		versionLabel.setLayoutData(gd_versionLabel);
		versionLabel.setText("Version ");

		final Label programmingAndDesignLabel = new Label(composite, SWT.NONE);
		programmingAndDesignLabel.setText("Programming: Aleksandar Prokopec");

		final Label designAleksandarProkopecLabel = new Label(composite, SWT.NONE);
		designAleksandarProkopecLabel.setText("Design: Aleksandar Prokopec");

		final Label supervisedByMarinLabel = new Label(composite, SWT.NONE);
		supervisedByMarinLabel.setText("Supervised by: Marin Golub");

		final Label organizationZemrisFacultyLabel = new Label(composite, SWT.NONE);
		organizationZemrisFacultyLabel.setText("ZEMRIS, Faculty of Electrotechnics and Computing, Zagreb, Croatia");

		final Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Label thisProductIsLabel = new Label(composite, SWT.NONE);
		thisProductIsLabel.setText("This product uses the following software and technologies:");

		final ViewForm viewForm_1 = new ViewForm(composite, SWT.FLAT | SWT.BORDER);
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.heightHint = 75;
		viewForm_1.setLayoutData(gridData);

		final ScrolledComposite scrolledComposite = new ScrolledComposite(viewForm_1, SWT.V_SCROLL);
		scrolledComposite.setAlwaysShowScrollBars(true);
		scrolledComposite.setExpandHorizontal(true);
		viewForm_1.setContent(scrolledComposite);

		final Composite composite_4 = new Composite(scrolledComposite, SWT.NONE);
		composite_4.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		composite_4.setLocation(0, 0);
		composite_4.setLayout(new GridLayout());

		final Link javacTechnologyLabel = new Link(composite_4, SWT.NONE);
		javacTechnologyLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		javacTechnologyLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		javacTechnologyLabel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				Program.launch(e.text);
			}
		});
		javacTechnologyLabel.setText("<a href=\"www.java.com\">(c)Java</a> technology");

		final Link apacheLabel = new Link(composite_4, SWT.NONE);
		apacheLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		apacheLabel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				Program.launch(arg0.text);
			}
		});
		apacheLabel.setText("<a href=\"http://www.apache.org/\">Apache Software Foundation</a>");

		final Link eclipseSoftwareFoundationLabel = new Link(composite_4, SWT.NONE);
		eclipseSoftwareFoundationLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		eclipseSoftwareFoundationLabel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				Program.launch(arg0.text);
			}
		});
		eclipseSoftwareFoundationLabel.setText("<a href=\"http://www.eclipse.org/\">The Eclipse Foundation</a>");

		final Link jchart2dLabel = new Link(composite_4, SWT.NONE);
		jchart2dLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		jchart2dLabel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				Program.launch(arg0.text);
			}
		});
		jchart2dLabel.setText("<a href=\"http://jchart2d.sourceforge.net/\">JChart2D</a> by Achim Westermann");
		composite_4.setSize(361, 80);
		scrolledComposite.setContent(composite_4);

		final Label label_1 = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Label thisProductUsesLabel = new Label(composite, SWT.NONE);
		thisProductUsesLabel.setText("This product uses the following resources:");

		final ViewForm viewForm_2 = new ViewForm(composite, SWT.FLAT | SWT.BORDER);
		final GridData gridData_1 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData_1.heightHint = 75;
		viewForm_2.setLayoutData(gridData_1);

		final ScrolledComposite scrolledComposite_1 = new ScrolledComposite(viewForm_2, SWT.V_SCROLL);
		scrolledComposite_1.setAlwaysShowScrollBars(true);
		scrolledComposite_1.setExpandHorizontal(true);
		viewForm_2.setContent(scrolledComposite_1);

		final Composite composite_5 = new Composite(scrolledComposite_1, SWT.NONE);
		composite_5.setLocation(0, 0);
		composite_5.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		composite_5.setLayout(new GridLayout());

		final Link eclipseorgLink = new Link(composite_5, SWT.NONE);
		eclipseorgLink.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		eclipseorgLink.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				Program.launch(arg0.text);
			}
		});
		eclipseorgLink.setText("<a href=\"http://www.famfamfam.com/lab/icons/mini/\">famfamfam.com Mini Icons</a>");

		final Link eclipseorgLink_1_1 = new Link(composite_5, SWT.NONE);
		eclipseorgLink_1_1.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		eclipseorgLink_1_1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				Program.launch(arg0.text);
			}
		});
		eclipseorgLink_1_1.setText("<a href=\"http://www.famfamfam.com/lab/icons/silk/\">famfamfam.com Silky Icon Set</a>");

		final Link eclipseorgLink_1 = new Link(composite_5, SWT.NONE);
		eclipseorgLink_1.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		eclipseorgLink_1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				Program.launch(arg0.text);
			}
		});
		eclipseorgLink_1.setText("<a href=\"http://www.pinvoke.com/\">PI Diagona Icons Pack</a>");

		final Link eclipseorgLink_1_1_1 = new Link(composite_5, SWT.NONE);
		eclipseorgLink_1_1_1.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		eclipseorgLink_1_1_1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				Program.launch(arg0.text);
			}
		});
		eclipseorgLink_1_1_1.setText("<a href=\"http://www.tenbytwenty.com/products/icon-sets/vaga\">Vaga Icon Set</a>");
		composite_5.setSize(361, 79);
		scrolledComposite_1.setContent(composite_5);

		final Label label_2 = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		architectureLabel = new Label(composite, SWT.NONE);
		architectureLabel.setLayoutData(new GridData());
		architectureLabel.setText("Architecture:");

		operatingSystemLabel = new Label(composite, SWT.NONE);
		operatingSystemLabel.setText("Operating system: ");

		runningLabel = new Label(composite, SWT.NONE);
		runningLabel.setText("Running on: ");

		final Label thisSoftwareIsLabel = new Label(composite, SWT.WRAP);
		final GridData gd_thisSoftwareIsLabel = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_thisSoftwareIsLabel.verticalIndent = 6;
		thisSoftwareIsLabel.setLayoutData(gd_thisSoftwareIsLabel);
		thisSoftwareIsLabel.setText("This product is open source and is published under GNU LESSER GENERAL PUBLIC LICENSE. Anyone can use this product, but there are no warranties. The product is intended for research and educational purposes.");

		final Button okButton = new Button(shell, SWT.NONE);
		final GridData gd_okButton = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		gd_okButton.verticalIndent = 6;
		okButton.setLayoutData(gd_okButton);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				shell.dispose();
			}
		});
		okButton.setText("OK");
	}

}














