package hr.fer.zemris.ga_framework;

import hr.fer.zemris.ga_framework.controller.IController;
import hr.fer.zemris.ga_framework.controller.impl.Controller;
import hr.fer.zemris.ga_framework.model.Model;
import hr.fer.zemris.ga_framework.view.Master;









public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// init operations
			initialization();
			
			// init mvc
			Model model = new Model();
			IController ctrl = new Controller();
			ctrl.setModel(model);
			
			// main part
			try {
				Master masta = new Master();
				masta.open(ctrl);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// exit operations
			exitOperations(model);
		} catch (Exception e) {
			Application.logexcept("Application unexpectedly aborted execution.", e);
		}
	}

	@SuppressWarnings("deprecation")
	private static void exitOperations(Model model) throws InterruptedException {
		// save user properties
		Application.saveUserProperties();
		
		// clear jobs
		model.getDispatcher().clearAllJobs();
		Thread.sleep(250);
		if (model.getDispatcher().getThreadMap().values().size() > 0) Thread.sleep(500);
		if (model.getDispatcher().getThreadMap().values().size() > 0) Thread.sleep(1500);
		// clear jobs - use force if necessary
		for (Thread thread : model.getDispatcher().getThreadMap().values()) {
			thread.stop();
		}
	}

	private static void initialization() {
	}

}














