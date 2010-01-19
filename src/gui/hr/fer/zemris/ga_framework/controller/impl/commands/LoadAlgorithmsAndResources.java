package hr.fer.zemris.ga_framework.controller.impl.commands;

import hr.fer.zemris.ga_framework.Application;
import hr.fer.zemris.ga_framework.controller.CommandResult;
import hr.fer.zemris.ga_framework.controller.Events;
import hr.fer.zemris.ga_framework.controller.ICommand;
import hr.fer.zemris.ga_framework.model.AlgoDir;
import hr.fer.zemris.ga_framework.model.Constants;
import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.model.ISerializable;
import hr.fer.zemris.ga_framework.model.Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;



/**
 * Command that loads the algorithms, and their
 * resources (e.g. their parameter setter dialogs).
 * 
 * @author Axel
 *
 */
public class LoadAlgorithmsAndResources implements ICommand {
	
	/* static fields */

	/* private fields */
	private String path;
	
	/* ctors */
	
	public LoadAlgorithmsAndResources(String relativePath) {
		path = System.getProperty("user.dir") + File.separator + relativePath + File.separator;
	}

	/* methods */
	
	public CommandResult doCommand(Model model) {
		AlgoDir dir = null;
		try {
			File f = new File(path);
			if (!f.isDirectory()) throw new IOException("No such dir: " + f.getAbsolutePath());
			model.clearClassMap();
			dir = extractAlgorithms(f, model);
		} catch (Exception e) {
			Application.logexcept("Could not load algorithms.", e);
			return new CommandResult("Could not extract algorithms.");
		}
		
		model.setAlgorithmTree(dir);
		
		return new CommandResult(new Events[] {Events.ALGORITHM_TREE_CHANGED}, null);
	}

	private AlgoDir extractAlgorithms(File currdir, Model model) throws FileNotFoundException, IOException {
		AlgoDir treenode = new AlgoDir(currdir.getName());
		
		// parse all jars and extract classes
		// add algorithms to current dir
		// and call yerself for all the child dirs
		File[] filesSub = currdir.listFiles();
		for (File sub : filesSub) {
			if (sub.isDirectory()) {
				// extract
				AlgoDir subalgodir = extractAlgorithms(sub, model);
				treenode.addSubdir(subalgodir);
			} else if (sub.isFile()) {
				// see if jar
				String name = sub.getName();
				if (!name.endsWith(".jar")) continue;
				putAlgosFromJar(sub, treenode, model);
			}
		}
		
		return treenode;
	}

	private void putAlgosFromJar(File jarfile, AlgoDir dir, Model model)
		throws FileNotFoundException, IOException
	{
		URLClassLoader urlLoader = new URLClassLoader(new URL[]{jarfile.toURI().toURL()});
		JarInputStream jis = new JarInputStream(new FileInputStream(jarfile));
		JarEntry entry = jis.getNextJarEntry();
		String name = null;
		String tmpdir = System.getProperty("user.dir") + File.separator + Application.getProperty("dir.tmp") + File.separator;
		byte[] buffer = new byte[1000];
		
		while (entry != null) {
			name = entry.getName();
			if (name.endsWith(".class")) {
				name = name.substring(0, name.length() - 6);
				name = name.replace('/', '.');
				try {
					Class<?> cls = urlLoader.loadClass(name);
					if (IAlgorithm.class.isAssignableFrom(cls) && !cls.isInterface() && ((cls.getModifiers() & Modifier.ABSTRACT) == 0)) {
						// this class is an algorithm - add it to tree, and class map
						dir.addAlgorithm(cls);
						model.putClass(cls.getName(), cls);
					} else if (ISerializable.class.isAssignableFrom(cls))
					{
						// this class inherits ISerializable - add it to class map
						model.putClass(cls.getName(), cls);
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} else if (Constants.isAllowedImageType(name)) {
				int lastSep = name.lastIndexOf("/");
				if (lastSep != -1) {
					String dirs = tmpdir + name.substring(0, lastSep);
					File d = new File(dirs);
					if (!d.exists()) d.mkdirs();
				}
				String filename = tmpdir + name;
				File f = new File(filename);
				if (!f.exists()) {
					f.createNewFile();
					FileOutputStream fos = new FileOutputStream(f);
					int read = -1;
					while ((read = jis.read(buffer)) != -1) {
						fos.write(buffer, 0, read);
					}
					fos.close();
				}
			}
			
			entry = jis.getNextJarEntry();
		}
	}
	
	public String getName() {
		return "Load Algorithms";
	}

	public boolean doesChangeModel() {
		return true;
	}
}














