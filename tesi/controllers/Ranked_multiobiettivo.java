package tesi.controllers;

import weka.core.Instances;

public class Ranked_multiobiettivo extends GAIT_noFC_multiobiettivo {

	public Ranked_multiobiettivo(Instances testset, int nclassi, int limit) {
		super(testset, nclassi, limit);
	}


	@Override
	public void crossover() {
		crossover_rank(0.6);
	}
		
	
}
