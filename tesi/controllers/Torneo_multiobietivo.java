package tesi.controllers;

import tesi.models.popolazione.PopolazioneRAM;
import weka.core.Instances;

public class Torneo_multiobietivo extends GAIT_noFC_multiobiettivo {


	public Torneo_multiobietivo(Instances testset, int nclassi, int limit) {
		super(testset, nclassi, limit);
		popolazione= new PopolazioneRAM();
	}

	@Override
	public void crossover() {
		/*TODO ma sta cosa del logaritmo è sensata? */
		crossover_torneo(crossover_rate, (int)Math.log(limit));
	}				
}
