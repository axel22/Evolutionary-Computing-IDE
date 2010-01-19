package hr.fer.zemris.ga_framework.controller;

import hr.fer.zemris.ga_framework.Application;

import java.util.LinkedList;

public class ActionDispatcher {
	
	private static class Action {
		public Runnable work;
		public Object lock;
		public Action(Runnable w, Object l) {
			work = w;
			lock = l;
		}
	}
	
	private class Dispatcher implements Runnable {
		public void run() {
			// empty runnable list, run all runnables
			while (dispon) {
				Action a = null;
				synchronized (reactions) {
					while (reactions.isEmpty()) {
						try {
//							System.out.println("Waitin'.");
							reactions.wait();
						} catch (InterruptedException e) {
							Application.logexcept("Interrupted, but shouldn't have been.", e);
						}
//						System.out.println("Woken.");
					}
					a = reactions.pollFirst();
				}
//				System.out.println("Workin'.");
				a.work.run();
				synchronized (a.lock) {
//					System.out.println("Releasing lock.");
					a.lock.notify();
//					System.out.println("Done releasing lock.");
				}
//				System.out.println("Done.");
			}
		}
	}
	
	/* static fields */

	/* private fields */
	private LinkedList<Action> reactions;
	private Thread dispthread;
	private volatile boolean dispon;

	/* ctors */
	
	public ActionDispatcher() {
		reactions = new LinkedList<Action>();
		runDispatcher();
	}

	/* methods */
	
	private void runDispatcher() {
		dispon = true;
		dispthread = new Thread(new Dispatcher());
		dispthread.setDaemon(true);
		dispthread.setName("Model action dispatcher thread");
		dispthread.start();
	}
	
	public void dispatch(Runnable r) {
		Object lock = new Object();
		synchronized (lock) {
			synchronized (reactions) {
//				System.out.println(reactions.size());
				reactions.addLast(new Action(r, lock));
				reactions.notify();
			}
			try {
//				System.out.println("Waiting for lock.");
				lock.wait();
//				System.out.println("Done waiting for lock.");
			} catch (InterruptedException e) {
				Application.logexcept("Interrupted, but shouldn't have been.", e);
			}
		}
	}
	
	public void setRunning(boolean shouldrun) {
		if (dispon && !shouldrun) {
			dispon = false;
			dispthread = null;
		} else if (!dispon && shouldrun) {
			runDispatcher();
		}
	}

}














