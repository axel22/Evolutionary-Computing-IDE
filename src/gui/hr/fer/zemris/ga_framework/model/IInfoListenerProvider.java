package hr.fer.zemris.ga_framework.model;



/**
 * Returns an info listener.
 * 
 * @author Axel
 *
 */
public interface IInfoListenerProvider {

	/**
	 * Controller should provide IInfoListener objects
	 * through this interface. Controller should multiplex
	 * invocations it receives through this interface to
	 * actual IInfoListeners that are a part of the user
	 * interface.
	 * 
	 * @param jobid
	 * @return
	 */
	public IInfoListener getInfoListener(Long jobid);
	
	
}














