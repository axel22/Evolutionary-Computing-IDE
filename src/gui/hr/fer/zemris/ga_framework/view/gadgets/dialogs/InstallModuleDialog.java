package hr.fer.zemris.ga_framework.view.gadgets.dialogs;

import hr.fer.zemris.ga_framework.Application;
import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.IValue;
import hr.fer.zemris.ga_framework.model.Model;
import hr.fer.zemris.ga_framework.view.ImageLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import name.brijest.extrawidgets.directoryhandler.DirectoryHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class InstallModuleDialog extends Dialog {

	/* static fields */
	private static final ImageData IMAGE_ICON = ImageLoader.loadImage("icons", "brick_add.png");
	private static final ImageData IMAGE_FOLDER = ImageLoader.loadImage("icons", "folder.png");
	private static final ImageData IMAGE_RESTRICT = ImageLoader.loadImage("icons", "restrict.png");

	/* private fields */
	private Text pathText;
	protected Object result;
	protected Shell shell;
	private DirectoryHandler directoryHandler;
	
	/* ctors */
	
	/**
	 * Create the dialog
	 * @param parent
	 * @param style
	 */
	public InstallModuleDialog(Shell parent, int style) {
		super(parent, style);
	}
	
	/* methods */

	/**
	 * Create the dialog
	 * @param parent
	 */
	public InstallModuleDialog(Shell parent) {
		this(parent, SWT.NONE);
	}

	/**
	 * Open the dialog
	 * @return the result
	 */
	public Object open(Model m) {
		initGUI(m);
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
		
		// set location
		Point ploc = getParent().getLocation();
		Point psz = getParent().getSize();
		shell.setLocation(ploc.x + (psz.x - shell.getSize().x) / 2, ploc.y + (psz.y - shell.getSize().y) / 2);
	}

	/**
	 * Create contents of the dialog
	 * @param m 
	 */
	protected void initGUI(final Model m) {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		final GridLayout gridLayout = new GridLayout();
		shell.setLayout(gridLayout);
		shell.setSize(500, 353);
		shell.setText("Install module");

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.marginWidth = 0;
		gridLayout_1.marginHeight = 2;
		gridLayout_1.numColumns = 3;
		composite.setLayout(gridLayout_1);

		final Label pathLabel = new Label(composite, SWT.NONE);
		pathLabel.setText("Path:");

		pathText = new Text(composite, SWT.BORDER);
		pathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Button button = new Button(composite, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				FileDialog fd = new FileDialog(shell, SWT.NONE);
				fd.setText("Select module");
				fd.setFilterExtensions(new String[] {"*.jar"});
				String selected = fd.open();
				if (selected != null) {
					pathText.setText(selected);
				}
			}
		});
		button.setLayoutData(new GridData());
		button.setText("...");

		directoryHandler = new DirectoryHandler(shell, SWT.NONE, System.getProperty("user.dir") + File.separator +
				Application.getProperty("dir.algos"), IMAGE_FOLDER);
		directoryHandler.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final Composite composite_2 = new Composite(shell, SWT.NONE);
		final GridLayout gridLayout_3 = new GridLayout();
		gridLayout_3.verticalSpacing = 0;
		gridLayout_3.marginWidth = 0;
		gridLayout_3.marginHeight = 0;
		gridLayout_3.horizontalSpacing = 0;
		composite_2.setLayout(gridLayout_3);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Label theLabel = new Label(composite_2, SWT.WRAP);
		theLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		theLabel.setText("The specified module will be scanned for IAlgorithm implementations and some of the contracts will be checked. Note that not all contracts of the IAlgorithm interface can be checked, so the implementer is still responsible for testing his algorithm. This procedure is added as a convenience, and will only detect the most basic contract violations.");

		final Label applicationMustBeLabel = new Label(composite_2, SWT.NONE);
		final GridData gd_applicationMustBeLabel = new GridData(SWT.FILL, SWT.CENTER, true, false);
		applicationMustBeLabel.setLayoutData(gd_applicationMustBeLabel);
		applicationMustBeLabel.setText("Application must be restarted before any changes can take effect.");

		final Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 2;
		gridLayout_2.marginWidth = 0;
		gridLayout_2.marginHeight = 0;
		composite_1.setLayout(gridLayout_2);

		final Button okButton = new Button(composite_1, SWT.NONE);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				if (performModuleInstallation(m)) shell.dispose();
			}
		});
		okButton.setLayoutData(new GridData());
		okButton.setText("OK");

		final Button cancelButton = new Button(composite_1, SWT.NONE);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				shell.dispose();
			}
		});
		cancelButton.setText("Cancel");
	}

	private boolean performModuleInstallation(Model m) {
		String seldir = directoryHandler.getSelectedDirectory();
		if (seldir == null) {
			MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
			box.setText("Cannot install");
			box.setMessage("A target directory must be selected.");
			box.open();
			
			return false;
		}
		String sjar = pathText.getText();
		File fjar = new File(sjar);
		if (!fjar.exists()) {
			MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
			box.setText("Cannot install");
			box.setMessage("A non-existing jar file has been selected.");
			box.open();
			
			return false;
		}
		
		// check IAlgorithm implementations for errors
		int count = 0;
		try {
			URLClassLoader loader = new URLClassLoader(new URL[]{fjar.toURI().toURL()});
			JarInputStream jis = new JarInputStream(new FileInputStream(fjar));
			JarEntry entry = jis.getNextJarEntry();
			while (entry != null) {
				String name = entry.getName();
				if (name.endsWith(".class")) {
					name = name.substring(0, name.length() - 6);
					name = name.replace('/', '.');
					Class<?> cls = loader.loadClass(name);
					if (IAlgorithm.class.isAssignableFrom(cls) && !cls.isInterface() && (cls.getModifiers() & Modifier.ABSTRACT) == 0) {
						if (!testAlgorithm(cls, m)) return false;
						count++;
					}
				}
				
				entry = jis.getNextJarEntry();
			}
		} catch (Exception e1) {
			Application.logexcept("Could not load classes from jar file.", e1);
			return false;
		}
		if (count == 0) {
			MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
			box.setText("Cannot install");
			box.setMessage("There don't seem to be any algorithms in the specified module.");
			box.open();
			
			return false;
		}
		
		// perform installation - copy jar
		try {
			FileChannel ic = new FileInputStream(sjar).getChannel();
			FileChannel oc = new FileOutputStream(seldir + File.separator + fjar.getName()).getChannel();
			ic.transferTo(0, ic.size(), oc);
			ic.close();
			oc.close(); 
		} catch (Exception e) {
			Application.logexcept("Could not install module", e);
			return false;
		}
		
		// set result object and return true
		result = new Object();
		return true;
	}

	private boolean testAlgorithm(Class<?> cls, Model m) {
		// this class is an algorithm - test it
		IAlgorithm alg = null;
		try {
			alg = (IAlgorithm) cls.newInstance();
		} catch (Exception e) {
			WarningListDialog wdial = new WarningListDialog(shell);
			wdial.open("Algorithm implementation invalid", "It seems that the algorithm "
					+ cls.getSimpleName() + " violates some of the contracts of the IAlgorithm " +
					"interface. The module cannot be loaded until these violations are eliminated. " +
					"It is heartfully recommended that you read the documentation carefully, because there " +
					"may be other (more subtle) violations that are undetectable here.",
					new String[]{"The algorithm does not seem to have a public default ctor."}, IMAGE_RESTRICT);
			
			return false;
		}
		
		List<String> violations = new ArrayList<String>();
		
		List<IParameter> params = alg.getParameters();
		if (params == null) violations.add("List of parameters is null.");
		else {
			Set<String> names = new HashSet<String>();
			Map<String, IValue> defvals = alg.getDefaultValues();
			for (IParameter p : params) {
				if (p.getName() == null) {
					violations.add("A parameter name is null.");
					continue;
				}
				if (names.contains(p.getName())) violations.add("The parameter name '" + p.getName() + "' occurs more than once.");
				if (p.getName().indexOf("<") != -1) violations.add("There is a '<' character in a parameter name.");
				if (p.getName().indexOf(">") != -1) violations.add("There is a '>' character in a parameter name.");
				if (!p.equals(alg.getParameter(p.getName()))) violations.add("The parameter '" + p.getName()
						+ "' cannot be looked up with the 'getParameter' method.");
				if (defvals.get(p.getName()) == null) violations.add("Default values map does not contain the parameter '" + p.getName() + "'.");
				else {
					if (!p.equals(defvals.get(p.getName()).parameter())) {
						violations.add("Parameter '" + p.getName() + "' is not equal to the one assigned to it's value in the default values map.");
					}
				}
				names.add(p.getName());
			}
		}
		List<IParameter> retvals = alg.getReturnValues();
		if (retvals == null) violations.add("List of return values is null.");
		else {
			Set<String> names = new HashSet<String>();
			for (IParameter p : retvals) {
				if (p == null) {
					violations.add("A parameter in parameter list is null.");
					continue;
				}
				if (p.getName() == null) {
					violations.add("A return value name is null.");
					continue;
				}
				if (names.contains(p.getName())) violations.add("The return value name '" + p.getName() + "' occurs more than once.");
				if (p.getName().indexOf("<") != -1) violations.add("There is a '<' character in a return value name.");
				if (p.getName().indexOf(">") != -1) violations.add("There is a '>' character in a return value name.");
				if (!p.equals(alg.getReturnValue(p.getName()))) violations.add("The return value '" + p.getName()
						+ "' cannot be looked up with the 'getReturnValue' method.");
				names.add(p.getName());
			}
		}
		if (alg.getAuthors() == null) violations.add("Author list is null.");
		if (alg.getLiterature() == null) violations.add("Literature list is null.");
		try {
			if (alg.newInstance() == null) violations.add("The 'newInstance' method returns null.");
		} catch (Exception e) {
			violations.add("The 'newInstance' method throws exceptions.");
		}
		if (m.getClass(alg.getClass().getName()) != null) {
			violations.add("It would seem that the algorithm with class name '" + alg.getClass().getName() + "' already exists. Learn package name conventions.");
		}
		
		if (violations.size() > 0) {
			WarningListDialog wdial = new WarningListDialog(shell);
			wdial.open("Algorithm implementation invalid", "It seems that the algorithm "
					+ cls.getSimpleName() + " violates some of the contracts of the IAlgorithm " +
					"interface. The module cannot be loaded until these violations are eliminated. " +
					"It is heartfully recommended that you read the documentation carefully, because there " +
					"may be other (more subtle) violations that are undetectable here.",
					violations.toArray(new String[]{}), IMAGE_RESTRICT);
			
			return false;
		}
		
		return true;
	}

}














