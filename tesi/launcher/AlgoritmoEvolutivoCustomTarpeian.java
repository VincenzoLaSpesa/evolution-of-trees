/**
 * 
 */
package tesi.launcher;

import tesi.controllers.GAIT_simple;
import tesi.interfaces.CromosomaDecorator;
import tesi.models.Dataset;

/**
 * Permette di avviare una versione generalizzata della procedura descritta in
 * GAIT ( variante 1) in cui viene applicato un controllo antibloat Tarpeian.
 * 
 * @author darshan
 */
public class AlgoritmoEvolutivoCustomTarpeian extends AlgoritmoEvolutivo implements Runnable {

	public AlgoritmoEvolutivoCustomTarpeian(Dataset d, int numerogenerazioni, int popolazione_iniziale,
			boolean mutante, int campioniperalbero) {
		super(d, numerogenerazioni, popolazione_iniziale, mutante, campioniperalbero);
	}


	public AlgoritmoEvolutivoCustomTarpeian(Dataset d, int numerogenerazioni, int popolazione_iniziale, boolean mutante) {
		super(d, numerogenerazioni, popolazione_iniziale, mutante);
	}

	/**
	 * Avvia la classificazione
	 * 
	 * @throws Exception
	 */
	public CromosomaDecorator begin() throws Exception {
		ecosistema = new GAIT_simple(scoringset, nclassi, this.popolazione_iniziale_size);
		ecosistema.tarpeian = true;
		ecosistema.tarpean_soglia = media;
		return startevolution(ecosistema);
	}
}
