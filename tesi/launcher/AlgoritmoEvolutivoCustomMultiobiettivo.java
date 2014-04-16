/**
 * 
 */
package tesi.launcher;

import tesi.controllers.GAIT_multiobiettivo;
import tesi.interfaces.CromosomaDecorator;
import tesi.models.Dataset;

/**
 * Permette di avviare una variante generalizzata della procedura descritta in
 * GAIT ( variante 1) adattata per usare una funzione di fitness multiobiettivo.
 * 
 * @author darshan
 */
public class AlgoritmoEvolutivoCustomMultiobiettivo extends AlgoritmoEvolutivo {

	public AlgoritmoEvolutivoCustomMultiobiettivo(Dataset d, int numerogenerazioni, int popolazione_iniziale,
			boolean mutante) {
		super(d, numerogenerazioni, popolazione_iniziale, mutante);
	}

	public AlgoritmoEvolutivoCustomMultiobiettivo(Dataset d, int numerogenerazioni, int popolazione_iniziale,
			boolean mutante, int campioniperalbero) {
		super(d, numerogenerazioni, popolazione_iniziale, mutante, campioniperalbero);
	}

	public CromosomaDecorator begin() throws Exception {
		return startevolution( new GAIT_multiobiettivo(scoringset, nclassi, this.popolazione_iniziale_size));
	}

}
