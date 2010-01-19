package hr.fer.zemris.ga_framework.algorithms.tsp;

import hr.fer.zemris.ga_framework.algorithms.tsp.crossovers.CycleCrossover;
import hr.fer.zemris.ga_framework.algorithms.tsp.crossovers.EdgeCrossover2;
import hr.fer.zemris.ga_framework.algorithms.tsp.crossovers.GreedySubtourCrossover;
import hr.fer.zemris.ga_framework.algorithms.tsp.crossovers.OnePointPartialPreservationCrossover;
import hr.fer.zemris.ga_framework.algorithms.tsp.crossovers.OrderCrossover;
import hr.fer.zemris.ga_framework.algorithms.tsp.crossovers.PartiallyMappedCrossover;
import hr.fer.zemris.ga_framework.algorithms.tsp.mutations.GShiftMutation;
import hr.fer.zemris.ga_framework.algorithms.tsp.mutations.InsertMutation;
import hr.fer.zemris.ga_framework.algorithms.tsp.mutations.InversionMutation;
import hr.fer.zemris.ga_framework.algorithms.tsp.mutations.ScrambleMutation;
import hr.fer.zemris.ga_framework.algorithms.tsp.mutations.ShiftMutation;
import hr.fer.zemris.ga_framework.algorithms.tsp.mutations.SwapMutation;
import hr.fer.zemris.ga_framework.algorithms.tsp.mutations.SwapSegmentMutation;
import hr.fer.zemris.ga_framework.algorithms.tsp.mutations.TwoInversionMutation;
import hr.fer.zemris.ga_framework.algorithms.tsp.mutations.TwoOptMutation;
import hr.fer.zemris.ga_framework.model.ICanvas;
import hr.fer.zemris.ga_framework.model.IInfoListener;
import hr.fer.zemris.ga_framework.model.IPainter;

import java.util.Arrays;
import java.util.Random;

public class TSPUtility {

	public static final IMutation createMutation(int permlength, String mutationop, CityTable cities) {
		if (permlength == 1) return new IMutation() {
			public void mutate(Permutation tomutate, int sl) {}
		};
		
		if (mutationop.equals("Swap")) return new SwapMutation();
		if (mutationop.equals("Scramble")) return new ScrambleMutation(permlength);
		if (mutationop.equals("Inversion")) return new InversionMutation(permlength);
		if (mutationop.equals("Insert")) return new InsertMutation(permlength);
		if (mutationop.equals("Shift")) return new ShiftMutation(permlength);
		if (mutationop.equals("MultiShift")) return new ShiftMutation(permlength);
		if (mutationop.equals("2-inversion")) return new TwoInversionMutation(permlength);
		if (mutationop.equals("SwapSeg")) return new SwapSegmentMutation(permlength);
		if (mutationop.equals("GShift")) return new GShiftMutation(permlength, cities);
		if (mutationop.equals("2 opt")) return new TwoOptMutation(permlength, cities);
		throw new IllegalArgumentException("Cannot create mutation." + mutationop);
	}
	
	public static final ICrossover createCrossover(int permlength, String crossovername) {
		if (permlength == 1) return new ICrossover() {
			private Random rand = new Random();
			public void crossover(Permutation firstpar, Permutation secondpar, Permutation child) {
				if (rand.nextBoolean()) {
					child.field = Arrays.copyOf(firstpar.field, firstpar.field.length);
				} else {
					child.field = Arrays.copyOf(secondpar.field, secondpar.field.length);
				}
			}
		};
		
		if (crossovername.equals("Edge")) return new EdgeCrossover2(permlength);
		if (crossovername.equals("Order")) return new OrderCrossover(permlength);
		if (crossovername.equals("GSX")) return new GreedySubtourCrossover(permlength);
		if (crossovername.equals("PMX")) return new PartiallyMappedCrossover(permlength);
		if (crossovername.equals("Cycle")) return new CycleCrossover(permlength);
		if (crossovername.equals("1-PPP")) return new OnePointPartialPreservationCrossover(permlength);
		throw new IllegalArgumentException("Cannot create crossover: " + crossovername);
	}
	
	public static void initCanvas(IInfoListener listener, final CityTable cities, final IZoomSettable zs) {
		listener.paint(new IPainter() {
			public void paint(ICanvas canvas) {
				canvas.showNotAvailable(false);
				
				int wdt = cities.getWidth();
				int hgt = cities.getHeight();
				double xzoom = 1.0;
				double yzoom = 1.0;
				
				if (wdt > 800) {
					xzoom = 800.0 / wdt;
					wdt = 800;
				}
				if (hgt > 600) {
					yzoom = 600.0 / hgt;
					hgt = 600;
				}
				
				double zoomFactor = xzoom < yzoom ? xzoom : yzoom;
				zs.setZoomFactor(zoomFactor);
				
				canvas.setCanvasSize(wdt, hgt);
			}
		});
	}
	
	public static void redrawImage(IInfoListener listener, final Permutation bestperm, final CityTable cities, final String text, final double zoomFactor) {
		listener.paint(new IPainter() {
			public void paint(ICanvas canvas) {
				// clear canvas
				canvas.clearCanvas();
				
				// connect cities
				canvas.setDrawColor(140, 140, 140);
				canvas.drawLine((int)(cities.getCityX(bestperm.field[0]) * zoomFactor),
						(int)(cities.getCityY(bestperm.field[0]) * zoomFactor),
						(int)(cities.getCityX(bestperm.field[bestperm.field.length - 1]) * zoomFactor),
						(int)(cities.getCityY(bestperm.field[bestperm.field.length - 1]) * zoomFactor));
				for (int i = 1; i < bestperm.field.length; i++) {
					canvas.drawLine((int)(cities.getCityX(bestperm.field[i - 1]) * zoomFactor),
							(int)(cities.getCityY(bestperm.field[i - 1]) * zoomFactor),
							(int)(cities.getCityX(bestperm.field[i]) * zoomFactor),
							(int)(cities.getCityY(bestperm.field[i]) * zoomFactor));
				}
				
				// draw cities
				if (bestperm.field.length <= 100) {
					// detailed draw
					canvas.setFillColor(160, 160, 160);
					canvas.setDrawColor(255, 255, 255);
					for (int i = 0, numc = cities.getCityNum(); i < numc; i++) {
						int x = (int)(cities.getCityX(i) * zoomFactor);
						int y = (int)(cities.getCityY(i) * zoomFactor);
						canvas.fillOval(x - 10, y - 10, 20, 20);
						canvas.drawText(String.valueOf(i), x, y, true);
					}
				} else if (bestperm.field.length <= 200) {
					// simple draw, because there are many cities
					canvas.setFillColor(100, 100, 100);
					for (int i = 0, numc = cities.getCityNum(); i < numc; i++) {
						int x = (int)(cities.getCityX(i) * zoomFactor);
						int y = (int)(cities.getCityY(i) * zoomFactor);
						canvas.fillOval(x - 5, y - 5, 10, 10);
					}
				} else if (bestperm.field.length <= 1000) {
					// very simple draw, small dots for cities
					canvas.setFillColor(100, 100, 100);
					for (int i = 0, numc = cities.getCityNum(); i < numc; i++) {
						int x = (int)(cities.getCityX(i) * zoomFactor);
						int y = (int)(cities.getCityY(i) * zoomFactor);
						canvas.fillOval(x - 3, y - 3, 6, 6);
					}
				} else {
					// simplest drawing - no cities should be drawn :)
				}
				
				// draw text
				canvas.setDrawColor(150, 150, 150);
				canvas.drawText(text, 0, 0, false);
				
				canvas.flip();
			}
		});
	}
	
}














