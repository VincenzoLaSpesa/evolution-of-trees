package tesi.controllers;

import tesi.models.popolazione.PopolazioneOrdinata;
import weka.core.Instances;

public class Ranked_multiobiettivo extends GAIT_multiobiettivo {
	public static double r=0.6;
	public Ranked_multiobiettivo(Instances testset, int nclassi, int limit) {
		super(testset, nclassi, limit);
		popolazione= new PopolazioneOrdinata();
	}


	@Override
	public void crossover() {
		crossover_rank(r);
	}
		
	
}
