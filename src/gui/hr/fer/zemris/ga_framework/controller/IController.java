package hr.fer.zemris.ga_framework.controller;

import hr.fer.zemris.ga_framework.model.IAlgorithmObserver;
import hr.fer.zemris.ga_framework.model.Model;

import java.util.List;



/**
 * Controller interface.
 * 
 * Used for issuing user commands, monitoring
 * tasks, managing undo, etc.
 * 
 * Controller shouldn't be implemented as a
 * bootstrap listener while issuing commands.
 * A newly created editor will be given an
 * instance of a controller, and will register
 * itself to it.
 * 
 * The controller also implements algorithm observer
 * interface, however, with one additional contract.
 * Methods of algorithm observer interface must be
 * synchronized with the controller's methods for
 * issuing commands and registering listeners. The
 * rationale behind this is simple - the algorithm's
 * thread shall invoke the algorithm observer, thus
 * raising the possibility of the algorithm event being
 * sent while another event is in progress (another algorithm
 * event or event triggered by some command).
 * A suggestion - make all methods of these interfaces synchronized.
 * 
 * @author Axel
 *
 */
public interface IController extends IAlgorithmObserver {

	
	/**
	 * @return
	 * Returns an unmodifiable list of editor
	 * info objects. Editors themselves should
	 * not be changed.
	 */
	public List<IEditor> getEditors();
	
	/**
	 * Registers an editor with the controller.
	 * An editor should call this when it's
	 * created.
	 * This method shall be synchronized.
	 * 
	 * @param editor
	 */
	public void registerEditor(Events[] evs, IEditor editor);
	
	/**
	 * Unregisters an editor.
	 * 
	 * @param editor
	 */
	public void unregisterEditor(IEditor editor);
	
	/**
	 * @return
	 * Returns an unmodifiable list of views.
	 */
	public List<IView> getViews();
	
	/**
	 * Registers a view with the controller.
	 * Each view should call this method when
	 * it's being created.
	 * This method shall be synchronized.
	 * 
	 * @param view
	 */
	public void registerView(Events[] evs, IView view);
	
	/**
	 * Unregisters a view.
	 * 
	 * @param view
	 */
	public void unregisterView(IView view);
	
	/**
	 * For elements of the gui other than editors
	 * and views, an element may be registered as
	 * listener.
	 * It will be informed about the type of change
	 * it has been registered for once it occurs.
	 * 
	 * @param evtypes
	 * Event types this listener is triggered for.
	 * @param listener
	 * @throws IllegalArgumentException
	 * If the event type list is empty or null.
	 */
	public void registerListener(Events[] evtypes, IListener listener);
	
	/**
	 * Unregister listener.
	 * @param listener
	 */
	public void unregisterListener(IListener listener);
	
	/**
	 * Registers listener of unsuccessful commands.
	 * @param listener
	 */
	public void registerUnsuccessfulCommandListener(IUnsuccessfulListener listener);
	
	/**
	 * Unregisters listener of unsuccessful commands.
	 * @param listener
	 */
	public void unregisterUnsuccessfulCommandListener(IUnsuccessfulListener listener);
	
	/**
	 * Sets the model for the controller.
	 * Should be invoked on initialization, as it hooks
	 * certain listeners to model.
	 * Specifically, it sets the IAlgorithmObserver and
	 * IInfoListenerProvider fields to the model, thus allowing
	 * the threads (who's owner is the model) to invoke
	 * various methods of the controller, which then passes
	 * them forward to the user interface.
	 * 
	 * @param model
	 */
	public void setModel(Model model);
	
	/**
	 * Returns the model object.
	 * For convenience, no const interfaces have been made.
	 * Curse Java once more for no const mechanism.
	 * 
	 * @return
	 * Returns the model object. Do not invoke methods
	 * that change the model.
	 */
	public Model getModel();
	
	/**
	 * Issues a command to the controller.
	 * 
	 * @param command
	 * @return
	 * An object with the result of the command's
	 * operations.
	 */
	public CommandResult issueCommand(ICommand command);
	
	
	
}














