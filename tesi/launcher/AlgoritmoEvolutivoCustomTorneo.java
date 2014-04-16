package tesi.launcher;

import tesi.controllers.Torneo_multiobietivo;
import tesi.interfaces.CromosomaDecorator;
import tesi.models.Dataset;

public class AlgoritmoEvolutivoCustomTorneo extends AlgoritmoEvolutivo {

	public AlgoritmoEvolutivoCustomTorneo(Dataset d, int numerogenerazioni, int popolazione_iniziale, boolean mutante) {
		super(d, numerogenerazioni, popolazione_iniziale,mutante);
	}
	public AlgoritmoEvolutivoCustomTorneo(Dataset d, int numerogenerazioni, int popolazione_iniziale,boolean mutante, int campioniperalbero){
		super(d, numerogenerazioni, popolazione_iniziale, mutante, campioniperalbero);
	}
	public CromosomaDecorator begin() throws Exception{
		return startevolution(new Torneo_multiobietivo(scoringset, nclassi, this.popolazione_iniziale_size));
	}	
}
