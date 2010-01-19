package hr.fer.zemris.ga_framework.model;

import java.util.List;
import java.util.Map;



/**
 * An interface describing an algorithm.<br/>
 * <br/>
 * Those who implement an algorithm must abide
 * all rules as listed here.<br/>
 * <br/>
 * Instances of the implementation of this interface
 * should not allocate any heavyweight resources on
 * creation (that is, in the ctor). Furthermore, they
 * should not allocate singleton resources, nor use
 * modifiable static fields - one instance must be
 * entirely independent of other instances.<br/>
 * For aquiring any resources (large memory amount, etc.),
 * use runAlgorithm method - this is certainly the first
 * occasion when you will need them. You do not have to
 * dispose these resources once the run method finishes,
 * they may and probably should be kept for subsequent
 * calls.<br/>
 * <br/>
 * It is necessary that each algorithm object
 * has internal flags - running flag, pause
 * flag (if it's pausable), and terminate flag.
 * These flags are described along with some methods.
 * Any access to these flags should be synchronized!<br/>
 * <br/>
 * Note that all parameters of the IAlgorithm
 * object should have unique names within an
 * algorithm for this implementation to be valid.
 * This also applies to return values.<br/>
 * <br/>
 * Also, no algorithm parameter or return value shall
 * have "<" or ">", """ or "'" characters in it's name. Same goes
 * for return values.
 * <br/>
 * Each algorithm must have a public default ctor
 * (a ctor with no arguments).<br/>
 * 
 * @author Axel
 *
 */
public interface IAlgorithm {

	/**
	 * Name of the genetic algorithm.
	 * It may not be unique - the implementer cannot
	 * know the names of all the algorithms. This serves
	 * only as a mean to distinguish between algorithms
	 * when choosing them in a GUI.
	 * 
	 * @return
	 * Returns the name of the genetic algorithm.
	 */
	public String getName();
	
	/**
	 * A short (30 to 120 words) long description of
	 * the algorithm.
	 * 
	 * @return
	 * Description of the parameter, it's parameters,
	 * their meaning.
	 * Useful for a user working with the algorithm
	 * in GUI, as this will be shown there.
	 */
	public String getDescription();
	
	/**
	 * Longer description of the algorithm.<br/>
	 * This is shown when the user requests an extensive
	 * information about the algorithm, it's parameters,
	 * how the algorithm should be used, theoretical
	 * background, etc.<br/>
	 * This may be shown in some special window that
	 * provides extensive information about the algorithm.
	 * String itself may contain HTML tags, but not "html",
	 * "head" or "body" tags - these will be added automatically.
	 * Embedding images (through html tags) is only supported 
	 * if they are placed inside the "pics/custom" directory in which
	 * case their path is "<package_name>/<image_name>",
	 * where package_name is the name of the picture's package, and
	 * dots are replaced with '/'.<br/>
	 * Pictures must be placed inside the same JAR file like the algorithm,
	 * and they are copied to this directory when the algorithm is installed
	 * (in the GUI).<br/>
	 * Font for the text shall be set automatically by the GUI,
	 * and should not be set through HTML tags.<br/>
	 * NOTE: Title name of the algorithm, authors, parameters,
	 * return values, etc. shall be displayed by the GUI separately,
	 * and there is no need to list these here.<br/>
	 * It is recommended that h4 and h5 tags are used for subsections,
	 * otherwise description will look ugly.<br/>
	 * Only info not included in other methods should be included here.
	 * This includes discussion about the algorithm, it's operators,
	 * for instance, or tribute to original authors, etc.<br/>
	 * Literature is not included here, though it can be cited with
	 * a number in brackets (for instance - [12]). This number indicates
	 * the item with the same position in the list returned by
	 * <code>getLiterature()</code> method.
	 * 
	 * @return
	 * Extensive HTML info about the algorithm.<br/>
	 * Must never return null. It is recommended that you return a
	 * text longer than the one returned by the <code>getDescription</code>
	 * method.
	 * 
	 * @see Constants.ALLOWED_IMAGE_TYPES
	 * For a list of allowed image types.
	 */
	public String getExtensiveInfo();
	
	/**
	 * Used to retrieve author's name.
	 * 
	 * @return
	 * String containing author's name, or coma (plus space)
	 * separated list of author names if there are
	 * more authors. Never null.
	 */
	public List<String> getAuthors();
	
	/**
	 * Returns a list of recommended literature for this algorithm.
	 * The list is NOT intended to contain ANY html format, except
	 * the "i" tags for italics. It is recommended to make the author
	 * names normal, and the work itself italic.<br/>
	 * This list is displayed in GUI when a user requests info about
	 * the algorithm. If method <code>getExtensiveInfo()</code> cites
	 * any literature, it should pay attention to the order of items
	 * in this list (when citing, start counting from 1, not 0!).<br/>
	 * Strings should not contain indices, they will be added in GUI
	 * automatically.<br/>
	 * When citing web pages, one must be aware that these might not
	 * be available some time in the future.
	 * 
	 * @return
	 * Unmodifiable or copied list of literature. Never returns null.
	 */
	public List<String> getLiterature();
	
	/**
	 * Returns a new instance of this algorithm.
	 * 
	 * @return
	 * A new instance of the algorithm, created by
	 * invoking the default ctor.
	 */
	public IAlgorithm newInstance();
	
	/**
	 * Returns a parameter set.
	 * 
	 * @return
	 * Returns a list of parameters associated with this
	 * algorithm.
	 * Parameter's names must be unique for each parameter.
	 */
	public List<IParameter> getParameters();
	
	/**
	 * Returns the parameter with the given name.
	 * 
	 * @param name
	 * @return
	 * Returns the parameter with the given name, or 
	 * null if there is no such parameter.
	 */
	public IParameter getParameter(String name);
	
	/**
	 * Returns a map of default values for this algorithm.
	 * For an implementation to be valid, values for each
	 * of the parameters must be included here with it's
	 * respective default value. Each value must conform
	 * the parameter, i.e. abide it's constraint, if it
	 * exists.
	 * Map consists of keys that are parameter names, and
	 * values - represented by IValue.
	 * No value shall ever be null.
	 * 
	 * @return
	 * A map of default values, named by their parameter names.
	 */
	public Map<String, IValue> getDefaultValues();
	
	/**
	 * Returns a set of value types, described
	 * by the IParameter class, that the algorithm
	 * returns after the run.
	 * 
	 * @return
	 * A set of return value types for the algorithm.
	 */
	public List<IParameter> getReturnValues();
	
	/**
	 * Returns a return value by name.
	 * 
	 * @return
	 * A parameter with the given name, or null if such
	 * a parameter does not exist.
	 */
	public IParameter getReturnValue(String name);
	
	/**
	 * Specifies whether the algorithm may return information
	 * during the run. If it can, it will pass information
	 * about it's state during the run of the algorithm if
	 * appropriate callback interface is specified in the
	 * <code>runAlgorithm</code> method.
	 * 
	 * @return
	 * True if it does return information about the algorithm
	 * state during the run. False otherwise.
	 */
	public boolean doesReturnInfoDuringRun();
	
	/**
	 * Returns a map of classes of ISerializable types, and their
	 * editors. If an algorithm uses user-defined types as parameters
	 * or return values, than this method should return a map with
	 * appropriate values.
	 * If all values are standard, that is, the algorithm uses no
	 * user-defined types - null or empty map should be returned.
	 * NOTE:
	 * The classes of the dialogs, as well as ISerializable types
	 * should be included in the same JAR as this implementation
	 * is - only the algorithms within that JAR should use these
	 * classes, and not algorithms in other JARs.
	 * Users of this feature should only implement dialogs for their
	 * own object types, and no other object types (for instance,
	 * ObjectList is the object type provided by this API - implementer
	 * should neither implement it's editor, nor mention that type
	 * in this list if the algorithm uses it).
	 * 
	 * @return
	 * Null if there are no user-defined types to edit. A map otherwise.
	 */
	public Map<Class<? extends ISerializable>, Class<? extends IParameterDialog>> getEditors();
	
	/**
	 * Returns a map of classes of ISerializable types and their renderers.
	 * Renderers are used to display the ISerializable type, unlike the editors
	 * to edit it.
	 * Return values are typically rendered, and if an algorithm uses return values
	 * that should somehow be displayed, then this method should return such a map.
	 * If all values are standard, that is, the algorithm uses no
	 * user-defined types that should be rendered - null or empty map should be returned.
	 * NOTE:
	 * The classes of the dialogs, as well as ISerializable types
	 * should be included in the same JAR as this implementation
	 * is - only the algorithms within that JAR should use these
	 * classes, and not algorithms in other JARs.
	 * Users of this feature should only implement renderers for their
	 * own object types, and no other object types.
	 * 
	 * @return
	 * Null if there are no user-defined types to render. A map otherwise.
	 */
	public Map<Class<? extends ISerializable>, Class<? extends IValueRenderer>> getRenderers();
	
	/**
	 * Runs the algorithm. Clears pause flag if algorithm
	 * is pausable. Always sets running flag, and clears
	 * it before this method returns.<br/>
	 * Clears the terminate flag.<br/>
	 * During the run of the algorithm, the terminate flag should
	 * periodically be checked. If the terminate flag is set, an
	 * exception is thrown, causing the algorithm to terminate.<br/>
	 * <br/>
	 * This method shall not open new threads or acquire locks.
	 * It may, however, perform native calls, but these native
	 * calls will not acquire locks or spawn processes. It may
	 * create threads, but this method is responsible for releasing
	 * these threads at any time. In any case, creating threads is probably
	 * not necessary.<br/>
	 * 
	 * @param values
	 * A set of parameter values for the algorithm. All values
	 * who's parameters are specified in the IParameter interface
	 * must be listed here, an {@link IllegalArgumentException} is
	 * thrown otherwise.
	 * @param listener
	 * Listener object through which the algorithm may return data
	 * during it's run. Algorithm will not return data if null is
	 * passed here. It must ignore this value if it cannot return
	 * information - see <code>doesReturnInfoDuringRun</code>.<br/>
	 * Note for instance that if the algorithm is performed in iterations
	 * or generations, info should not be returned for each iteration,
	 * as returning info is costy! The implementer must return info only each,
	 * say, 20000 iterations. It's up to the implementer to decide how often,
	 * and this decision is based upon algorithm performance and implementation.
	 * @return
	 * Returns a map of values returned by the algorithm. This map
	 * must include all values who's parameters are specified in
	 * the value returned by the <code>getReturnValues</code> method.<br/>
	 * This map is allocated for each run - never shall the same
	 * map object be returned on successive run. No value in the
	 * map shall ever be null.
	 * @throws IllegalArgumentException
	 * If invalid algorithm parameter values have been specified.
	 * @throws AlgorithmTerminatedException
	 * If someone sets the terminate flag, algorithm must throw
	 * this exception after a periodical check of the flag.
	 */
	public Map<String, IValue> runAlgorithm(Map<String, IValue> values, IInfoListener listener);
	
	/**
	 * Forcefully halts the algorithm by setting it's terminate flag.
	 * 
	 * Once the algorithm checks the terminate flag, it will terminate
	 * it's thread by throwing <code>AlgorithmTerminatedException</code>.
	 * 
	 * The algorithm itself is thus responsible for terminating. Welcome
	 * to Java! :)
	 */
	public void haltAlgorithm();
	
	/**
	 * Returns whether or not algorithm is running, that is,
	 * the current state of the run flag.
	 * 
	 * @return
	 * True if running flag is on.
	 */
	public boolean isRunning();
	
	/**
	 * Returns whether this algorithm can be paused.
	 * 
	 * @return
	 * True if pausable.
	 */
	public boolean isPausable();
	
	/**
	 * If algorithm is pausable, passing true to this
	 * method will set the algorithm's pause flag.
	 * Algorithm will then halt the first time it checks
	 * the flag, and will do so by invoking Thread.sleep().
	 * Passing false will clear it's pause flag, thus allowing
	 * the algorithm to continue.
	 * If this method is called before calling <code>runAlgorithm</code>
	 * method, pause flag will still be affected, however, it will
	 * also be cleared once <code>runAlgorithm</code> is called.
	 * Note: this method shall be synchronized.
	 * 
	 * @param on
	 * If set to true, algorithm will pause next time it checks
	 * the pause flag.
	 * @throws UnsupportedOperationException
	 * If the algorithm is not pausable.
	 */
	public void setPaused(boolean on);
	
	/**
	 * Returns the state of the pause flag.
	 * 
	 * @return
	 */
	public boolean isPaused();
	
	/**
	 * Returns whether or not algorithm's state
	 * can be saved, and later on retrieved.
	 * A precondition for a saveable algorithm
	 * is that the algorithm is pausable.
	 * Only once the algorithm is paused, can it
	 * be saved.
	 * 
	 * @return
	 * Whether or not algorithm is saveable.
	 * If the algorithm is not pausable, this
	 * method must always return false.
	 */
	public boolean isSaveable();
	
	/**
	 * If algorithm is running, paused and saveable, this will
	 * return the current algorithm state.
	 * 
	 * @return
	 * Serialization of the current algorithm state. It is not
	 * necessary that the algorithm serializes the parameters
	 * as well, this is done automatically.
	 * @throws UnsupportedOperationException
	 * If the algorithm is not saveable.
	 * @throws IllegalStateException
	 * If the algorithm is not running, or is not paused.
	 */
	public String save();
	
	/**
	 * If the algorithm is not running, but is saveable,
	 * then this method should put algorithm in the state
	 * specified by the serialization string, and run it,
	 * setting the running flag.
	 * Once the algorithm ends, the running flag is reset,
	 * just as if the method <code>runAlgorithm</code> was
	 * called.
	 * 
	 * @param r
	 * @throws UnsupportedOperationException
	 * If the algorithm is not saveable.
	 * @throws IllegalArgumentException
	 * If the serialization string is invalid.
	 * @throws IllegalStateException
	 * If the algorithm is already running.
	 */
	public void load(String s);
	
	/**
	 * Returns whether or not the algorithm internally calls
	 * system specific functions or uses the native api.
	 * If the algorithm relies exclusively on Java code,
	 * and Java framework, false is returned.
	 * 
	 * @return
	 * True if the algorithm is native, false otherwise.
	 */
	public boolean isNative();
	
	/**
	 * This is intended for future development.
	 * 
	 * @return
	 * Currently, it should only return null.
	 */
	public List<IParameter> getRunProperties();
	
	/**
	 * This is intended for future development.
	 * 
	 * @return
	 * Currently, it should only return null.
	 */
	public Map<String, IValue> getDefaultRunProperties();
	
	/**
	 * This is intended for future development.
	 * 
	 * Currently, it does nothing.
	 * 
	 * @param key
	 * @param value
	 */
	public void setRunProperty(String key, Object value);
	
}














