package tesi.launcher;

import tesi.controllers.Ranked_multiobiettivo;
import tesi.interfaces.CromosomaDecorator;
import tesi.models.Dataset;

public class AlgoritmoEvolutivoCustomRanked extends AlgoritmoEvolutivo {

	public AlgoritmoEvolutivoCustomRanked(Dataset d, int numerogenerazioni, int popolazione_iniziale, boolean mutante) {
		super(d, numerogenerazioni, popolazione_iniziale,mutante);
	}
	public AlgoritmoEvolutivoCustomRanked(Dataset d, int numerogenerazioni, int popolazione_iniziale, boolean mutante, int campioniperalbero){
		super(d, numerogenerazioni, popolazione_iniziale, mutante, campioniperalbero);
	}
	public CromosomaDecorator begin() throws Exception{
		return startevolution(new Ranked_multiobiettivo(scoringset, nclassi, this.popolazione_iniziale_size));
	}	

}
