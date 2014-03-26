package tesi.main;

import java.io.FileReader;

import tesi.interfaces.launchers.AlgoritmoEvolutivoCustomMultiobiettivo;
import tesi.models.Dataset;
import tesi.util.SysUtil;
import tesi.util.logging.FloatStream;
import tesi.util.logging.Singletons;
import weka.core.Instances;

public abstract class GaitMulti {

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
	 * Esegue per 100 volte {@link #gait_multi(Dataset, int,boolean) gait_multi(Dataset
	 * d, int generazioni, boolean mutante)} dal dataset completo, per ogni run evolve per 25
	 * generazioni. produce in output due csv: uno con l'evoluzione delle
	 * prestazioni e uno con l'evoluzione dei pesi
	 * 
	 * @see gait_multi(Dataset, int)
	 * @throws Exception
	 */
	public static void gait_multi_mutante_benchmark(int generazioni) throws Exception {
		// String
		// dataset_url="/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/smalldataset.arff";
		//String dataset_url = "/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/completedataset.arff";
		// String
		// dataset_url="/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/wine/winequality-all.arff";
		System.out.println(SysUtil.getMethodName(1));
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
			gait_multi(d, generazioni,true);
			FloatStream ft=Singletons.cromosomastream.calcola(d.testset, d.nclassi);
			Singletons.floatstream.merge(ft);
			Singletons.cromosomastream.deleteColumn(nomecolonna);
		}
		System.out.println(Singletons.floatstream.ricomponi().toString());
		System.out.println(Singletons.pesistream.ricomponi().toString());
	
	}

	/**
	 * Esegue per 100 volte {@link #gait_multi(Dataset, int) gait_multi(Dataset
	 * d, int generazioni)} dal dataset completo, per ogni run evolve per 25
	 * generazioni. produce in output due csv: uno con l'evoluzione delle
	 * prestazioni e uno con l'evoluzione dei pesi
	 * 
	 * @see gait_multi(Dataset, int)
	 * @throws Exception
	 */
	public static void gait_multi_benchmark(int generazioni) throws Exception {
		// String
		// dataset_url="/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/smalldataset.arff";
		//String dataset_url = "/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/completedataset.arff";
		// String
		// dataset_url="/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/wine/winequality-all.arff";
		System.out.println(SysUtil.getMethodName(1));
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
			gait_multi(d, generazioni,false);
			FloatStream ft=Singletons.cromosomastream.calcola(d.testset, d.nclassi);
			Singletons.floatstream.merge(ft);
			Singletons.cromosomastream.deleteColumn(nomecolonna);
		}
		System.out.println(Singletons.floatstream.ricomponi().toString());
		System.out.println(Singletons.pesistream.ricomponi().toString());
	
	}

}
