package tesi.main;

import java.io.FileReader;

import tesi.interfaces.launchers.AlgoritmoEvolutivoCustomSPF;
import tesi.models.Dataset;
import tesi.util.logging.FloatStream;
import tesi.util.logging.Singletons;
import weka.core.Instances;

public class SelezioneFitnessProporzionale {

	/**
	 * Esegue per 100 volte {@link #sfp_multi(Dataset, int,boolean) sfp_multi(Dataset
	 * d, int generazioni, boolean mutante)} dal dataset completo, per ogni run evolve per 25
	 * generazioni. produce in output due csv: uno con l'evoluzione delle
	 * prestazioni e uno con l'evoluzione dei pesi
	 * @see sfp_multi(Dataset, int,boolean)
	 * @throws Exception
	 */
	public static void sfp_Multi_Benchmark(Integer generazioni) throws Exception {
			
			FileReader dataset_stream = new FileReader(Settings.dataset_url);
			Instances dataset = new Instances(dataset_stream);
			//int generazioni = 25;
			Dataset d;
			for (int a = 0; a < Settings.istanze; a++) {
				String nomecolonna = "istanza_" + a;
				d = new Dataset(dataset, Settings.percentualetrainingset, Settings.percentualescoringset, Settings.percentualetestset);
				Singletons.cromosomastream.createColumn(nomecolonna);
				Singletons.cromosomastream.setColonna_corrente(nomecolonna);
				Singletons.pesistream.createColumn(nomecolonna);
				Singletons.pesistream.setColonna_corrente(nomecolonna);
				sfp_multi(d, generazioni,false);
				FloatStream ft=Singletons.cromosomastream.calcola(d.testset, d.nclassi);
				Singletons.floatstream.merge(ft);
				Singletons.cromosomastream.deleteColumn(nomecolonna);
			}
			System.out.println(Singletons.floatstream.ricomponi().toString());
			System.out.println(Singletons.pesistream.ricomponi().toString());				
	}

	/**
	 * Esegue per 100 volte {@link #sfp_multi(Dataset, int,boolean) sfp_multi(Dataset
	 * d, int generazioni, boolean mutante)} dal dataset completo, per ogni run evolve per 25
	 * generazioni. produce in output due csv: uno con l'evoluzione delle
	 * prestazioni e uno con l'evoluzione dei pesi
	 * @see sfp_multi(Dataset, int,boolean)
	 * @throws Exception
	 */
	public static void sfp_Multi_mutante_Benchmark(Integer generazioni) throws Exception {
			
			FileReader dataset_stream = new FileReader(Settings.dataset_url);
			Instances dataset = new Instances(dataset_stream);
			//int generazioni = 25;
			Dataset d;
			for (int a = 0; a < Settings.istanze; a++) {
				String nomecolonna = "istanza_" + a;
				d = new Dataset(dataset, Settings.percentualetrainingset, Settings.percentualescoringset, Settings.percentualetestset);
				Singletons.cromosomastream.createColumn(nomecolonna);
				Singletons.cromosomastream.setColonna_corrente(nomecolonna);
				Singletons.pesistream.createColumn(nomecolonna);
				Singletons.pesistream.setColonna_corrente(nomecolonna);
				sfp_multi(d, generazioni,true);
				FloatStream ft=Singletons.cromosomastream.calcola(d.testset, d.nclassi);
				Singletons.floatstream.merge(ft);
				Singletons.cromosomastream.deleteColumn(nomecolonna);
			}
			System.out.println(Singletons.floatstream.ricomponi().toString());
			System.out.println(Singletons.pesistream.ricomponi().toString());				
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

}
