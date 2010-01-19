package hr.fer.zemris.ga_framework.algorithms.tsp;

import java.util.Random;



public class Specie {

	/* static fields */

	/* private fields */
	private PartialPermutation[] population;
	private int actualtotalcities, citiesperspecie;
	private double avgcost, observedmin;
	private int timeSinceLastImprovement, totalAdapts;

	/* ctors */
	
	/**
	 * Creates a new random specie.
	 */
	public Specie(int citynumperspecie, int popsize, int actualTotalCitiesToChooseFrom) {
		population = new PartialPermutation[popsize];
		citiesperspecie = citynumperspecie;
		actualtotalcities = actualTotalCitiesToChooseFrom;
		timeSinceLastImprovement = 0;
		avgcost = 0.0;
		observedmin = Double.MAX_VALUE;
		totalAdapts = 0;
		
		randomizeSpecie();
	}

	/* methods */
	
	public int getTimeSinceImprovement() {
		return timeSinceLastImprovement;
	}
	
	public void incTimeSinceImprovement() {
		timeSinceLastImprovement++;
	}
	
	public void resetTimeSinceImprovement() {
		timeSinceLastImprovement = 0;
	}
	
	private void randomizeSpecie() {
		for (int i = 0; i < population.length; i++) {
			population[i] = new PartialPermutation(actualtotalcities, citiesperspecie, true);
		}
	}
	
	public PartialPermutation[] getPopulation() {
		return population;
	}
	
	public void selectRandomRepresentatives(PartialPermutation[] representativespool) {
		Random rand = new Random();
		for (int i = 0; i < representativespool.length; i++) {
			representativespool[i] = population[rand.nextInt(population.length)];
		}
	}

	public void setAverageCost(double c) {
		avgcost = c;
	}
	
	public double getAverageCost() {
		return avgcost;
	}

	public void setObservedMinimum(double observedmin) {
		this.observedmin = observedmin;
	}

	public double getObservedMinimum() {
		return observedmin;
	}

	public void setTotalAdapts(int totalAdapts) {
		this.totalAdapts = totalAdapts;
	}

	public int getTotalAdapts() {
		return totalAdapts;
	}
	
	public int incTotalAdapts() {
		return totalAdapts++;
	}

}














