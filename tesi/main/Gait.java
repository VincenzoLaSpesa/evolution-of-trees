package tesi.main;

import java.io.FileReader;

import tesi.interfaces.launchers.AlgoritmoEvolutivoCustom;
import tesi.interfaces.launchers.AlgoritmoEvolutivoCustomTarpeian;
import tesi.models.Dataset;
import tesi.util.SysUtil;
import tesi.util.logging.FloatStream;
import tesi.util.logging.Singletons;
import weka.core.Instances;

public class Gait {

	/**
	 * Esegue per 25 volte {@link #gait_classic(Dataset, int)
	 * gait_complete(Dataset d, int generazioni)} dal dataset completo. produce
	 * in output due csv: uno con l'evoluzione delle prestazioni e uno con
	 * l'evoluzione dei pesi
	 * 
	 * @see gait_complete(Dataset, int)
	 * @throws Exception
	 */
	public static void gait_classic_benchmark(int generazioni) throws Exception {
		System.out.println(SysUtil.getMethodName(1));
		FileReader dataset_stream = new FileReader(Settings.dataset_url);
		Instances dataset = new Instances(dataset_stream);
		System.out.println(generazioni);
		// int generazioni = 100;
		Dataset d;
		for (int a = 0; a < Settings.istanze; a++) {
			String nomecolonna = "istanza_" + a;
			d = new Dataset(dataset, Settings.percentualetrainingset, Settings.percentualescoringset, Settings.percentualetestset);
			Singletons.cromosomastream.createColumn(nomecolonna);
			Singletons.cromosomastream.setColonna_corrente(nomecolonna);
			Singletons.pesistream.createColumn(nomecolonna);
			Singletons.pesistream.setColonna_corrente(nomecolonna);
			gait_classic(d, generazioni);
			FloatStream ft = Singletons.cromosomastream.calcola(d.testset, d.nclassi);
			Singletons.floatstream.merge(ft);
			Singletons.cromosomastream.deleteColumn(nomecolonna);
		}
		System.out.println(Singletons.floatstream.ricomponi().toString());
		System.out.println(Singletons.pesistream.ricomponi().toString());
	
	}

	public static void gait_mutante_benchmark(int generazioni) throws Exception {
		// String
		// dataset_url="/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/smalldataset.arff";
		//String dataset_url = "/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/completedataset.arff";
		// String
		// dataset_url="/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/wine/winequality-all.arff";
		System.out.println(SysUtil.getMethodName(1));
		FileReader dataset_stream = new FileReader(Settings.dataset_url);
		Instances dataset = new Instances(dataset_stream);
		System.out.println(generazioni);
		// int generazioni = 100;
		Dataset d;
		for (int a = 0; a < Settings.istanze; a++) {
			String nomecolonna = "istanza_" + a;
			d = new Dataset(dataset, Settings.percentualetrainingset, Settings.percentualescoringset, Settings.percentualetestset);
			Singletons.cromosomastream.createColumn(nomecolonna);
			Singletons.cromosomastream.setColonna_corrente(nomecolonna);
			Singletons.pesistream.createColumn(nomecolonna);
			Singletons.pesistream.setColonna_corrente(nomecolonna);
			gait_mutante_complete(d, generazioni);
			FloatStream ft = Singletons.cromosomastream.calcola(d.testset, d.nclassi);
			Singletons.floatstream.merge(ft);
			Singletons.cromosomastream.deleteColumn(nomecolonna);
		}
		System.out.println(Singletons.floatstream.ricomponi().toString());
		System.out.println(Singletons.pesistream.ricomponi().toString());
	
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
	 * Esegue un benchmark su Gait-tarpeian (obiettivo singolo)
	 * @param generazioni
	 * @throws Exception
	 */
	public static void gait_tarpeian_benchmark(Integer generazioni) throws Exception {
			System.out.println(SysUtil.getMethodName(1));
			FileReader dataset_stream = new FileReader(Settings.dataset_url);
			Instances dataset = new Instances(dataset_stream);
			System.out.println(generazioni);
			// int generazioni = 100;
			Dataset d;
			for (int a = 0; a < Settings.istanze; a++) {
				String nomecolonna = "istanza_" + a;
				d = new Dataset(dataset, Settings.percentualetrainingset, Settings.percentualescoringset, Settings.percentualetestset);
				Singletons.cromosomastream.createColumn(nomecolonna);
				Singletons.cromosomastream.setColonna_corrente(nomecolonna);
				Singletons.pesistream.createColumn(nomecolonna);
				Singletons.pesistream.setColonna_corrente(nomecolonna);
				gait_tarpeian(d, generazioni);
				FloatStream ft = Singletons.cromosomastream.calcola(d.testset, d.nclassi);
				Singletons.floatstream.merge(ft);
				Singletons.cromosomastream.deleteColumn(nomecolonna);
			}
			System.out.println(Singletons.floatstream.ricomponi().toString());
			System.out.println(Singletons.pesistream.ricomponi().toString());
	
		
		
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

}
