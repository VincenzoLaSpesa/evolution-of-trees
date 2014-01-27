package tesi.controllers;

import weka.core.Instances;

public class Torneo_multiobietivo extends GAIT_noFC_multiobiettivo {


	public Torneo_multiobietivo(Instances testset, int nclassi, int limit) {
		super(testset, nclassi, limit);
	}

	@Override
	public void crossover() {
		crossover_torneo(crossover_rate, (int)Math.log(limit));
	}				
}
