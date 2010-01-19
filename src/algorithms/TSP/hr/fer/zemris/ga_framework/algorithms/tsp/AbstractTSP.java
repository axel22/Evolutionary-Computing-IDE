package hr.fer.zemris.ga_framework.algorithms.tsp;

import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.model.ICanvas;
import hr.fer.zemris.ga_framework.model.IInfoListener;
import hr.fer.zemris.ga_framework.model.IPainter;

public abstract class AbstractTSP implements IAlgorithm {
	
	protected CityTable temptable;
	private double zoomFactor = 1.0;
	
	protected double getZoomFactor() {
		return zoomFactor;
	}

	protected double fitness(Permutation p) {
		double sum = 0;
		for (int i = 1; i < p.field.length; i++) {
			sum += temptable.get(p.field[i - 1], p.field[i]);
		}
		sum += temptable.get(p.field[p.field.length - 1], p.field[0]);
		return sum;
	}

	protected void sendInfoAboutBest(IInfoListener listener, Permutation bestperm, double bestval) {
		listener.setProperty("Best solution", bestperm.toString());
		listener.setProperty("Minimum cost", String.valueOf(bestval));
	}
	
	protected void sendInfoPercentage(IInfoListener listener, double percent) {
		listener.setPercentage(percent);
	}
	
	protected void initCanvas(IInfoListener listener, final CityTable cities) {
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
				
				zoomFactor = xzoom < yzoom ? xzoom : yzoom;
				
				canvas.setCanvasSize(wdt, hgt);
			}
		});
	}
	
	protected void redrawImage(IInfoListener listener, final Permutation bestperm, final CityTable cities, final String text) {
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














