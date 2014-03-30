package tesi.main;

import java.io.FileReader;
import java.util.Map;

import com.google.gson.Gson;

import tesi.interfaces.launchers.AlgoritmoEvolutivoCustom;
import tesi.interfaces.launchers.AlgoritmoEvolutivoCustomMultiobiettivo;
import tesi.interfaces.launchers.GAIT_noFC_run;
import tesi.models.Dataset;
import tesi.util.SysUtil;
import tesi.util.logging.FloatStream;
import tesi.util.logging.Singletons;
import weka.core.Instances;

@Deprecated
/**
 * Insieme di funzioni deprecate che erano presenti nella prima versione del codice, 
 * le ho lasciate perchè sono legate a parametri della riga di comando ma sinceraente 
 * non ho idea del loro attuale comportamento visto che intanto ho modificato radicalmente 
 * il funzionamento del progetto.
 * @author darshan
 *
 */
public abstract class Deprecati {

	/**
	 * Esegue gait_multi con i parametri in ingresso utilizzando
	 * AlgoritmoEvolutivoCustomMultiobiettivo(dataset, generazioni,
	 * popolazione_size, nclassi, percentualetrainingset, percentualescoringset,
	 * percentualetestset);
	 * 
	 * @param dataset_url
	 * @param generazioni
	 * @throws Exception
	 * @see AlgoritmoEvolutivoCustomMultiobiettivo
	 */
	public static void gait_multi(String dataset_url, int generazioni) throws Exception {
		FileReader dataset_stream = new FileReader(dataset_url);
		Instances dataset = new Instances(dataset_stream);
		dataset.setClassIndex(dataset.numAttributes() - 1);
		int nclassi = dataset.numClasses();
		// AlgoritmoEvolutivoCustomMultiobiettivo(Instances dataset, int
		// numerogenerazioni, int popolazione_iniziale,int nclassi, float
		// percentualetrainingset, float percentualetestset, float
		// percentualescoringset)
		AlgoritmoEvolutivoCustomMultiobiettivo gaitrunner = new AlgoritmoEvolutivoCustomMultiobiettivo(dataset,
				generazioni, Settings.popolazione_size, nclassi, Settings.percentualetrainingset,
				Settings.percentualescoringset, Settings.percentualetestset);
		gaitrunner.begin();
	}

	/**
	 * Avvia {@link #gait_complete(String,int) gait_complete(dataset_url,
	 * generazioni)} ottenendo i dati da una stringa JSON
	 * 
	 * @param settings_content
	 * @throws Exception
	 * @see gait_complete(String,int)
	 */
	@Deprecated
	public static void gait_complete(String settings_content) throws Exception {
		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		Map<String, String> map = gson.fromJson(settings_content, Map.class);
		String dataset_url = map.get("dataset");
		int generazioni = Integer.parseInt(map.get("generazioni"));
		gait_complete(dataset_url, generazioni);
	}

	/**
	 * Avvia Gait sul dataset, utilizza AlgoritmoEvolutivoCustom(dataset,
	 * generazioni, popolazione_size, nclassi,percentualetrainingset,
	 * percentualescoringset, percentualetestset); con popolazioni iniziali di
	 * popolazione_size elementi. <strong>è deprecato</strong>
	 * 
	 * 
	 * @deprecated è meglio passare un dataset già tagliato, per poter poi
	 *             effettuare misurazioni dall'esterno
	 * @param dataset_url
	 * @param generazioni
	 * @throws Exception
	 */
	@Deprecated
	public static void gait_complete(String dataset_url, int generazioni) throws Exception {
		FileReader dataset_stream = new FileReader(dataset_url);
		Instances dataset = new Instances(dataset_stream);
		dataset.setClassIndex(dataset.numAttributes() - 1);
		int nclassi = dataset.numClasses();
		AlgoritmoEvolutivoCustom gaitrunner = new AlgoritmoEvolutivoCustom(dataset, generazioni,
				Settings.popolazione_size, nclassi, Settings.percentualetrainingset, Settings.percentualescoringset,
				Settings.percentualetestset);
		// gaitrunner.begin_compact();
		gaitrunner.begin();
	}

	/**
	 * esegue gait sui dati in ingresso usando GAIT_noFC_run(trainingset,
	 * testset, scoringset, nclassi) (che è deprecata)
	 * 
	 * @param trainingset_url
	 * @param testset_url
	 * @param scoringset_url
	 * @throws Exception
	 * @see GAIT_noFC_run
	 * 
	 */
	@Deprecated
	public static void gait(String trainingset_url, String testset_url, String scoringset_url) throws Exception {
		FileReader testset_stream = new FileReader(testset_url);
		FileReader trainingset_stream = new FileReader(trainingset_url);
		FileReader scoringset_stream = new FileReader(scoringset_url);
		//
		Instances trainingset = new Instances(trainingset_stream);
		Instances testset = new Instances(testset_stream);
		Instances scoringset = new Instances(scoringset_stream);
		//
		trainingset.setClassIndex(trainingset.numAttributes() - 1);
		testset.setClassIndex(testset.numAttributes() - 1);
		scoringset.setClassIndex(scoringset.numAttributes() - 1);
		//
		int nclassi = trainingset.numClasses();
		GAIT_noFC_run gaitrunner = new GAIT_noFC_run(trainingset, testset, scoringset, nclassi);
		gaitrunner.begin();
	}

	/**
	 * Avvia {@link gait_complete gait_complete(dataset_url, generazioni)} con
	 * le impostazioni di default
	 * 
	 * @throws Exception
	 * @see gait_complete(String,int)
	 */

	@Deprecated
	public static void gait_complete() throws Exception {
		// String
		// dataset_url="/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/smalldataset.arff";
		// String dataset_url =
		// "/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/completedataset.arff";
		// String
		// dataset_url="/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/wine/winequality-all.arff";

		int generazioni = 25;
		gait_complete(Settings.dataset_url, generazioni);

	}

	/**
	 * Esegue {@link gait gait(String,String,String)} sui
	 * dataset standard
	 * 
	 * @throws Exception
	 * @see gait(String,String,String)
	 */
	public static void gait() throws Exception {
		String testset = "/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/testset.arff";
		String trainingset = "/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/trainingset.arff";
		String scoringset = "/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/scoringset.arff";
	
		gait(trainingset, testset, scoringset);
	}

	/**
	 * Esegue {@link gait gait(String,String,String)}
	 * ottenendo i dati da una stringa JSON
	 * 
	 * @throws Exception
	 * @see gait(String,String,String)
	 */
	public static void gait(String settings_content) throws Exception {
		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		Map<String, String> map = gson.fromJson(settings_content, Map.class);
		String training_url = map.get("trainingset");
		String testset_url = map.get("testset");
		String scoringset_url = map.get("scoringset");
		gait(training_url, testset_url, scoringset_url);
	}

	/**
	 * Avvia {@link Main#gait_multi(String,int) gait_multi(dataset_url,
	 * generazioni)} con le impostazioni di default
	 * 
	 * @throws Exception
	 * @see gait_multi(String,int)
	 */
	
	public static void gait_multi() throws Exception {
		// String
		// dataset_url="/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/smalldataset.arff";
		//String dataset_url = "/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/completedataset.arff";
		// String
		// dataset_url="/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/wine/winequality-all.arff"
		int generazioni = 25;
		gait_multi(Settings.dataset_url, generazioni);
	
	}

	/**
	 * Avvia {@link Main#gait_multi(String,int) gait_multi(dataset_url,
	 * generazioni)} ottenendo i dati da una stringa JSON
	 * 
	 * @param settings_content
	 * @throws Exception
	 * @see gait_multi(String,int)
	 */
	public static void gait_multi(String settings_content) throws Exception {
		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		Map<String, String> map = gson.fromJson(settings_content, Map.class);
		String dataset_url = map.get("dataset");
		int generazioni = Integer.parseInt(map.get("generazioni"));
		gait_multi(dataset_url, generazioni);
	}

	/**
	 * Alias per {@link gait gait()}
	 * 
	 * @see gait()
	 * @throws Exception
	 */
	public static void gaitDefault() throws Exception {
		gait();
	}

	/**
	 * Esegue per 100 volte {@link Algoritmi#gait_multi(Dataset, int,boolean) gait_multi(Dataset
	 * d, int generazioni, boolean mutante)} dal dataset completo, per ogni run evolve per 25
	 * generazioni. produce in output due csv: uno con l'evoluzione delle
	 * prestazioni e uno con l'evoluzione dei pesi
	 * 
	 * @see gait_multi(Dataset, int)
	 * @throws Exception
	 */
	private static void gait_multi_mutante_benchmark(int generazioni) throws Exception {
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
			Algoritmi.gait_multi(d, generazioni,true);
			FloatStream ft=Singletons.cromosomastream.calcola(d.testset, d.nclassi);
			Singletons.floatstream.merge(ft);
			Singletons.cromosomastream.deleteColumn(nomecolonna);
		}
		System.out.println(Singletons.floatstream.ricomponi().toString());
		System.out.println(Singletons.pesistream.ricomponi().toString());
	
	}

	/**
	 * Esegue per 100 volte {@link Algoritmi#gait_multi(Dataset, int) gait_multi(Dataset
	 * d, int generazioni)} dal dataset completo, per ogni run evolve per 25
	 * generazioni. produce in output due csv: uno con l'evoluzione delle
	 * prestazioni e uno con l'evoluzione dei pesi
	 * 
	 * @see gait_multi(Dataset, int)
	 * @throws Exception
	 */
	private static void gait_multi_benchmark(int generazioni) throws Exception {
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
			Algoritmi.gait_multi(d, generazioni,false);
			FloatStream ft=Singletons.cromosomastream.calcola(d.testset, d.nclassi);
			Singletons.floatstream.merge(ft);
			Singletons.cromosomastream.deleteColumn(nomecolonna);
		}
		System.out.println(Singletons.floatstream.ricomponi().toString());
		System.out.println(Singletons.pesistream.ricomponi().toString());
	
	}

	/**
	 * Esegue per 25 volte {@link Algoritmi#gait_classic(Dataset, int)
	 * gait_complete(Dataset d, int generazioni)} dal dataset completo. produce
	 * in output due csv: uno con l'evoluzione delle prestazioni e uno con
	 * l'evoluzione dei pesi
	 * 
	 * @see gait_complete(Dataset, int)
	 * @throws Exception
	 */
	private static void gait_classic_benchmark(int generazioni) throws Exception {
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
			Algoritmi.gait_classic(d, generazioni);
			FloatStream ft = Singletons.cromosomastream.calcola(d.testset, d.nclassi);
			Singletons.floatstream.merge(ft);
			Singletons.cromosomastream.deleteColumn(nomecolonna);
		}
		System.out.println(Singletons.floatstream.ricomponi().toString());
		System.out.println(Singletons.pesistream.ricomponi().toString());
	
	}

	private static void gait_mutante_benchmark(int generazioni) throws Exception {
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
			Algoritmi.gait_mutante_complete(d, generazioni);
			FloatStream ft = Singletons.cromosomastream.calcola(d.testset, d.nclassi);
			Singletons.floatstream.merge(ft);
			Singletons.cromosomastream.deleteColumn(nomecolonna);
		}
		System.out.println(Singletons.floatstream.ricomponi().toString());
		System.out.println(Singletons.pesistream.ricomponi().toString());
	
	}

	/**
	 * Esegue un benchmark su Gait-tarpeian (obiettivo singolo)
	 * @param generazioni
	 * @throws Exception
	 */
	private static void gait_tarpeian_benchmark(Integer generazioni) throws Exception {
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
				Algoritmi.gait_tarpeian(d, generazioni);
				FloatStream ft = Singletons.cromosomastream.calcola(d.testset, d.nclassi);
				Singletons.floatstream.merge(ft);
				Singletons.cromosomastream.deleteColumn(nomecolonna);
			}
			System.out.println(Singletons.floatstream.ricomponi().toString());
			System.out.println(Singletons.pesistream.ricomponi().toString());
	
		
		
	}

	private static void ranked_Multi_Benchmark(Integer generazioni) throws Exception {
		
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
			Algoritmi.ranked_multi(d, generazioni);
			FloatStream ft=Singletons.cromosomastream.calcola(d.testset, d.nclassi);
			Singletons.floatstream.merge(ft);
			Singletons.cromosomastream.deleteColumn(nomecolonna);
		}
		System.out.println(Singletons.floatstream.ricomponi().toString());
		System.out.println(Singletons.pesistream.ricomponi().toString());				
	}

	/**
	 * Esegue per 100 volte {@link Algoritmi#sfp_multi(Dataset, int,boolean) sfp_multi(Dataset
	 * d, int generazioni, boolean mutante)} dal dataset completo, per ogni run evolve per 25
	 * generazioni. produce in output due csv: uno con l'evoluzione delle
	 * prestazioni e uno con l'evoluzione dei pesi
	 * @see sfp_multi(Dataset, int,boolean)
	 * @throws Exception
	 */
	private static void sfp_Multi_Benchmark(Integer generazioni) throws Exception {
			
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
				Algoritmi.sfp_multi(d, generazioni,false);
				FloatStream ft=Singletons.cromosomastream.calcola(d.testset, d.nclassi);
				Singletons.floatstream.merge(ft);
				Singletons.cromosomastream.deleteColumn(nomecolonna);
			}
			System.out.println(Singletons.floatstream.ricomponi().toString());
			System.out.println(Singletons.pesistream.ricomponi().toString());				
	}

	/**
	 * Esegue per 100 volte {@link Algoritmi#sfp_multi(Dataset, int,boolean) sfp_multi(Dataset
	 * d, int generazioni, boolean mutante)} dal dataset completo, per ogni run evolve per 25
	 * generazioni. produce in output due csv: uno con l'evoluzione delle
	 * prestazioni e uno con l'evoluzione dei pesi
	 * @see sfp_multi(Dataset, int,boolean)
	 * @throws Exception
	 */
	private static void sfp_Multi_mutante_Benchmark(Integer generazioni) throws Exception {
			
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
				Algoritmi.sfp_multi(d, generazioni,true);
				FloatStream ft=Singletons.cromosomastream.calcola(d.testset, d.nclassi);
				Singletons.floatstream.merge(ft);
				Singletons.cromosomastream.deleteColumn(nomecolonna);
			}
			System.out.println(Singletons.floatstream.ricomponi().toString());
			System.out.println(Singletons.pesistream.ricomponi().toString());				
	}

	private static void torneo_Multi_Benchmark(Integer generazioni) throws Exception {
		
		System.out.flush();
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
			Algoritmi.torneo_multi(d, generazioni);
			FloatStream ft=Singletons.cromosomastream.calcola(d.testset, d.nclassi);
			Singletons.floatstream.merge(ft);
			Singletons.cromosomastream.deleteColumn(nomecolonna);
		}
		System.out.println(Singletons.floatstream.ricomponi().toString());
		System.out.println(Singletons.pesistream.ricomponi().toString());				
	}

	private static void torneo_MultiMutante_Benchmark(Integer generazioni) throws Exception {
		
		System.out.flush();
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
			Algoritmi.torneo_multi_mutante(d, generazioni);
			FloatStream ft=Singletons.cromosomastream.calcola(d.testset, d.nclassi);
			Singletons.floatstream.merge(ft);
			Singletons.cromosomastream.deleteColumn(nomecolonna);
		}
		System.out.println(Singletons.floatstream.ricomponi().toString());
		System.out.println(Singletons.pesistream.ricomponi().toString());				
	}

}
