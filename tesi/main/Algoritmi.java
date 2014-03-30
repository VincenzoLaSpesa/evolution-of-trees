package tesi.main;

import tesi.interfaces.launchers.AlgoritmoEvolutivoCustom;
import tesi.interfaces.launchers.AlgoritmoEvolutivoCustomMultiobiettivo;
import tesi.interfaces.launchers.AlgoritmoEvolutivoCustomRanked;
import tesi.interfaces.launchers.AlgoritmoEvolutivoCustomSPF;
import tesi.interfaces.launchers.AlgoritmoEvolutivoCustomTarpeian;
import tesi.interfaces.launchers.AlgoritmoEvolutivoCustomTorneo;
import tesi.models.Dataset;

public abstract class Algoritmi {

	/**
	 * Avvia Gait sul dataset, utilizza
	 * AlgoritmoEvolutivoCustomMultiobiettivo(Dataset d,int numerogenerazioni,
	 * int popolazione_iniziale) con popolazioni iniziali di popolazione_size elementi
	 * 
	 * @param d
	 * @param generazioni
	 * @param mutante
	 * @throws Exception
	 * @see AlgoritmoEvolutivoCustomMultiobiettivo
	 */
	public static void gait_multi(Dataset d, int generazioni, boolean mutante) throws Exception {
		AlgoritmoEvolutivoCustomMultiobiettivo gaitrunner = new AlgoritmoEvolutivoCustomMultiobiettivo(d, generazioni,
				Settings.popolazione_size,mutante,Settings.albero_size);
		gaitrunner.begin();
	}

	/**
	 * Avvia Gait sul dataset, utilizza AlgoritmoEvolutivoCustom(Dataset d,int
	 * numerogenerazioni, int popolazione_iniziale) con popolazioni iniziali di
	 * 50 elementi
	 * 
	 * @param d
	 * @param generazioni
	 * @throws Exception
	 * @see AlgoritmoEvolutivoCustom
	 */
	public static void gait_classic(Dataset d, int generazioni) throws Exception {
		AlgoritmoEvolutivoCustom gaitrunner = new AlgoritmoEvolutivoCustom(d, generazioni, Settings.popolazione_size,false,Settings.albero_size);
		gaitrunner.begin();
	}

	public static void gait_mutante_complete(Dataset d, int generazioni) throws Exception {
		AlgoritmoEvolutivoCustom gaitrunner = new AlgoritmoEvolutivoCustom(d, generazioni, Settings.popolazione_size,true,Settings.albero_size);
		gaitrunner.begin();
	}

	/**
	 * Avvia Gait sul dataset, utilizza AlgoritmoEvolutivoCustomTarpeian(Dataset d,int
	 * numerogenerazioni, int popolazione_iniziale) con popolazioni iniziali di
	 * 50 elementi
	 * 
	 * @param d
	 * @param generazioni
	 * @throws Exception
	 * @see AlgoritmoEvolutivoCustomTarpeian
	 */
	public static void gait_tarpeian(Dataset d, Integer generazioni) throws Exception {
		AlgoritmoEvolutivoCustomTarpeian gaitrunner = new AlgoritmoEvolutivoCustomTarpeian(d, generazioni,
				Settings.popolazione_size, false, Settings.albero_size);
		gaitrunner.begin();
	}

	public static void ranked_multi(Dataset d, Integer generazioni) throws Exception {
		AlgoritmoEvolutivoCustomRanked gaitrunner = new AlgoritmoEvolutivoCustomRanked(d, generazioni, Settings.popolazione_size,false,Settings.albero_size);
		gaitrunner.begin();
	}

	/**
	 * Avvia AlgoritmoEvolutivoCustomSPF sul dataset, utilizza AlgoritmoEvolutivoCustomSPF(Dataset d,int
	 * numerogenerazioni, int popolazione_iniziale) con popolazioni iniziali di
	 * popolazione_size elementi
	 * 
	 * @param d
	 * @param generazioni
	 * @throws Exception
	 * @see AlgoritmoEvolutivoCustomSPF
	 */
	public static void sfp_multi(Dataset d, Integer generazioni, boolean mutante) throws Exception {
		AlgoritmoEvolutivoCustomSPF gaitrunner = new AlgoritmoEvolutivoCustomSPF(d, generazioni, Settings.popolazione_size,mutante,Settings.albero_size);
		gaitrunner.begin();
	}

	public static void torneo_multi_mutante(Dataset d, Integer generazioni) throws Exception {
		AlgoritmoEvolutivoCustomTorneo gaitrunner = new AlgoritmoEvolutivoCustomTorneo(d, generazioni, Settings.popolazione_size,true,Settings.albero_size);
		gaitrunner.begin();
	}

	public static void torneo_multi(Dataset d, Integer generazioni) throws Exception {
		AlgoritmoEvolutivoCustomTorneo gaitrunner = new AlgoritmoEvolutivoCustomTorneo(d, generazioni, Settings.popolazione_size,false,Settings.albero_size);
		gaitrunner.begin();
	}


}
