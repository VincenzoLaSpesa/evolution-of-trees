package tesi.controllers;

import tesi.models.popolazione.PopolazioneRAM;
import weka.core.Instances;

public class SFP_multiobiettivo extends GAIT_multiobiettivo {

	public SFP_multiobiettivo(Instances testset, int nclassi, int limit) {
		super(testset, nclassi, limit);
		popolazione= new PopolazioneRAM();
	}

	@Override
	/**
	 * definisce il crossover come crossover_spf();
	 * @see tesi.controllers.GAIT_noFC_abstract.crossover_spf();
	 */
	public void crossover() {
		crossover_spf(crossover_rate);
	}
	
	
}
