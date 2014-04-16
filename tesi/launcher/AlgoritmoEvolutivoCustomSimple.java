/**
 * 
 */
package tesi.launcher;

import tesi.controllers.GAIT_simple;
import tesi.interfaces.CromosomaDecorator;
import tesi.models.Dataset;


public class AlgoritmoEvolutivoCustomSimple extends AlgoritmoEvolutivo implements Runnable {

	public AlgoritmoEvolutivoCustomSimple(Dataset d, int numerogenerazioni, int popolazione_iniziale, boolean mutante) {
		super(d, numerogenerazioni, popolazione_iniziale, mutante);
	}

	public AlgoritmoEvolutivoCustomSimple(Dataset d, int numerogenerazioni, int popolazione_iniziale, boolean mutante,
			int campioniperalbero) {
		super(d, numerogenerazioni, popolazione_iniziale, mutante, campioniperalbero);
	}

	/**
	 * Avvia la classificazione
	 * 
	 * @throws Exception
	 */
	public CromosomaDecorator begin() throws Exception {
		return startevolution(new GAIT_simple(scoringset, nclassi, this.popolazione_iniziale_size));
	}
}
