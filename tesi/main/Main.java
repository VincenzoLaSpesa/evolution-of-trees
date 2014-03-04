package tesi.main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import tesi.controllers.GAIT_noFC_multiobiettivo;
import tesi.controllers.GeneticOperators;
import tesi.controllers.TreeEvaluator;
import tesi.interfaces.CromosomaDecorator;
import tesi.interfaces.launchers.AlgoritmoEvolutivoCustom;
import tesi.interfaces.launchers.AlgoritmoEvolutivoCustomMultiobiettivo;
import tesi.interfaces.launchers.AlgoritmoEvolutivoCustomRanked;
import tesi.interfaces.launchers.AlgoritmoEvolutivoCustomSPF;
import tesi.interfaces.launchers.AlgoritmoEvolutivoCustomTarpeian;
import tesi.interfaces.launchers.AlgoritmoEvolutivoCustomTorneo;
import tesi.interfaces.launchers.GAIT_noFC_run;
import tesi.interfaces.launchers.J48Wholetraining;
import tesi.models.Cromosoma;
import tesi.models.Dataset;
import tesi.util.ArrayUtil;
import tesi.util.StringUtil;
import tesi.util.SysUtil;
import tesi.util.logging.FloatStream;
import tesi.util.logging.GlobalLogger;
import tesi.util.logging.Singletons;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.j48.ClassifierTree;
import weka.core.Instances;
import weka.core.Range;

import com.google.gson.Gson;

public class Main {
	//
	public static final String parkingson="/home/darshan/Uni/Tesi/tesi/Tesi/dataset/parkingsons/parkinsons.arff";
	public static final String adult = "/home/darshan/Uni/Tesi/tesi/Tesi/dataset/completedataset.arff";
	public static final String breast = "/home/darshan/Uni/Tesi/tesi/Tesi/dataset/breast-cancer/breast-cancer-wisconsin.arff";
	public static final String heart = "/home/darshan/Uni/Tesi/tesi/Tesi/dataset/Statlog (Heart) Data Set /heart.arff";	
	public static final String covtype = "/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/covtype/covtype.data.arff";
	//

	public static String dataset_url = "../dataset/completedataset.arff";
	public static String dataset_url_fallback = covtype;

	public static int popolazione_size=-1;//se negativo lo autodimensiona 
	public static int istanze=30;
	public static int albero_size=100; // se negativo lo autodimensiona
	public static String base;
	public static String pattern;
	public static String albero;
	public static double percentualetrainingset=0.5454545454;
	public static double percentualescoringset=0.1818181818;
	public static double percentualetestset= 0.2727272727;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Singletons.cromosomastream.active=true;
		GlobalLogger.init_quiet();
		OptionParser parser = new OptionParser();
		parser.accepts("gait").withOptionalArg();
		parser.accepts("gait-multi").withOptionalArg();
		parser.accepts("trainingset", "specifica la path del trainingset").withRequiredArg();
		parser.accepts("settings", "carica i settaggi da un file json").withRequiredArg();
		parser.accepts("testset", "specifica la path del testset").withRequiredArg();
		parser.accepts("scoringset", "specifica la path dello scoringset").withRequiredArg();
		parser.accepts("dataset", "specifica la path del dataset unico, se non specificato lo cerca in "+dataset_url).withRequiredArg();
		parser.accepts("generazioni", "numero di generazioni").withRequiredArg();
		parser.accepts("popolazione", "numerosità della popolazione durante l'algoritmo evolutivo").withRequiredArg();
		parser.accepts("istanze", "Numero di ecosistemi avviati durante un benchmark").withRequiredArg();
		parser.accepts("campioniperalbero", "numero di campioni utilizzati per costruire un singolo albero").withRequiredArg();
		//
		parser.accepts("percentualetrainingset", "percentuale di dataset che diventerà trainingset").withRequiredArg();
		parser.accepts("percentualescoringset", "percentuale di dataset che diventerà scoringset").withRequiredArg();
		parser.accepts("percentualetestset", "percentuale di dataset che diventerà testset").withRequiredArg();
		//
		parser.accepts("alpha", "specifica un parametro per la formula del fitness multiobiettivo").withRequiredArg();
		parser.accepts("beta", "specifica un parametro per la formula del fitness multiobiettivo").withRequiredArg();
		parser.accepts("gamma", "specifica un parametro per la formula del fitness multiobiettivo").withRequiredArg();
		parser.accepts("quiet", "Disattiva il calcolo delel prestazioni sulle generazioni intermedie");
		parser.accepts("verbose", "Attiva tutti i livelli del logger");
		parser.accepts("stat", "Attiva le statistiche");
		parser.accepts("base", "specifica il prefisso che dovranno avere eventuali files generati").withRequiredArg();
		parser.accepts("pattern", "permette di caricare dei files attraverso dei pattern espressi nel formato glob di java").withRequiredArg();
		parser.accepts("albero", "specifica un albero da caricare").withRequiredArg();

		//
		parser.accepts("iris", "Avvia i test su Iris con le impostazioni di default");
		parser.accepts("gaitDefault", "Avvia i test su Gait con le impostazioni di default");
		// parser.accepts("gaitMultiDefault",
		// "Avvia i test su GaitMulti con le impostazioni di default");
		parser.accepts("testaalbero", "Avvia i test sulla creazione/serializzazione degli alberi");

		parser.accepts(
				"gaitCompleteBenchmark",
				"Avvia il benchmark su gait con le impostazioni di default utilizzando il dataset per intero, è possibile specificare il numero di generazioni con l'apposito flag --generazioni");

		parser.accepts(
				"gaitCompleteTarpeianBenchmark",
				"Avvia il benchmark su gait con le impostazioni di default utilizzando il dataset per intero e applicando un controllo tarpeian sul bloat, è possibile specificare il numero di generazioni con l'apposito flag --generazioni");

		parser.accepts(
				"gaitCompleteMutanteBenchmark",
				"Avvia il benchmark su gait con le impostazioni di default utilizzando il dataset per intero, è possibile specificare il numero di generazioni con l'apposito flag --generazioni, il mutation rate viene incrementato linearmente durante le generazioni di stallo");
		
		parser.accepts(
				"gaitMultiBenchmark", 
				"Avvia il benchmark su GaitMulti con le impostazioni di default utilizzando il dataset per intero, è possibile specificare il numero di generazioni con l'apposito flag --generazioni");

		parser.accepts(
				"gaitMultiMutanteBenchmark", 
				"Avvia il benchmark su GaitMulti con le impostazioni di default utilizzando il dataset per intero, è possibile specificare il numero di generazioni con l'apposito flag --generazioni, il mutation rate viene incrementato linearmente durante le generazioni di stallo");
				
		parser.accepts(
				"SfpMultiBenchmark", 
				"Avvia il benchmark su SfpMulti con le impostazioni di default utilizzando il dataset per intero, è possibile specificare il numero di generazioni con l'apposito flag --generazioni");

		parser.accepts(
				"RankedMultiBenchmark", 
				"Avvia il benchmark su RankedMulti con le impostazioni di default utilizzando il dataset per intero, è possibile specificare il numero di generazioni con l'apposito flag --generazioni");		
		
		/*parser.accepts(
				"TorneoMultiBenchmark", 
				"Avvia il benchmark su RankedMulti con le impostazioni di default utilizzando il dataset per intero, è possibile specificare il numero di generazioni con l'apposito flag --generazioni");		
		*/
		parser.accepts(
				"TorneoMultiMutanteBenchmark", 
				"Avvia il benchmark su RankedMulti con le impostazioni di default utilizzando il dataset per intero, è possibile specificare il numero di generazioni con l'apposito flag --generazioni, il mutation rate viene incrementato linearmente durante le generazioni di stallo");		
		parser.accepts(
				"TorneoMultiBenchmark", 
				"Avvia il benchmark su RankedMulti con le impostazioni di default utilizzando il dataset per intero, è possibile specificare il numero di generazioni con l'apposito flag --generazioni, il mutation rate viene mantenuto costante");		
		
		parser.accepts(
				"ProduciAlberi", 
				"Crea tanti file yaml con tutti gli algoritmi principali");		
		
		parser.accepts(
				"MisuraTempi", 
				"Produce files di log con le misurazioni dei tempi");		
		
		
		parser.accepts("WholeTrainingBenchmark3000", "Calcola 100 alberi j48 su trainingset di 3000 elementi");
		parser.accepts("gaitComplete", "Una esecuzione da 25 generazioni");
		OptionSet options = parser.parse(args);
		
		if (options.hasArgument("percentualetrainingset")) {
			double d = Double.parseDouble((String) options.valueOf("percentualetrainingset"));
			percentualetrainingset=d;
			System.out.printf("percentualetrainingset -> %f \n",percentualetrainingset);
		}

		if (options.hasArgument("percentualescoringset")) {
			double d = Double.parseDouble((String) options.valueOf("percentualescoringset"));
			percentualescoringset=d;
			System.out.printf("percentualescoringset -> %f \n",percentualescoringset);
		}
		if (options.hasArgument("percentualetestset")) {
			double d = Double.parseDouble((String) options.valueOf("percentualetestset"));
			percentualetestset=d;
			System.out.printf("percentualetestset -> %f \n",percentualetestset);
		}
		if (options.hasArgument("albero")) {
			String s = (String) options.valueOf("albero");
			albero=s;
		}else{
			albero="/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dist/piccolomostro_breast.yaml";
		}

		
		if (options.hasArgument("pattern")) {
			String s = (String) options.valueOf("pattern");
			pattern=s;
		}
		
		if (options.hasArgument("base")) {
			String s = (String) options.valueOf("base");
			base=s;
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("ddhhmmss_");
			base=sdf.format(new Date());
		}
		
		if (options.hasArgument("dataset")) {
			String s = (String) options.valueOf("dataset");
			dataset_url=s;
		}else{
			try{
				dataset_url= SysUtil.getAbsolutePath(dataset_url);
			}catch(Exception e){
				System.out.println("Non riesco a risolvere la path del dataset di default.");
				dataset_url=dataset_url_fallback;
			}
		}
		System.out.printf("dataset -> %s \n",dataset_url);			

		if (options.hasArgument("campioniperalbero")) {
			int i = Integer.parseInt((String) options.valueOf("campioniperalbero"));
			albero_size=i;
			System.out.printf("campioniperalbero -> %d \n",i);
		}		
		
		if (options.hasArgument("istanze")) {
			int i = Integer.parseInt((String) options.valueOf("istanze"));
			istanze=i;
			System.out.printf("istanze -> %d \n",i);
		}		
		if (options.hasArgument("popolazione")) {
			int p = Integer.parseInt((String) options.valueOf("popolazione"));
			popolazione_size=p;
			System.out.printf("popolazione -> %d \n",p);
		}		
		if (options.hasArgument("alpha")) {
			double alpha = Double.parseDouble((String) options.valueOf("alpha"));
			GAIT_noFC_multiobiettivo.alpha=alpha;
			System.out.printf("alpha -> %f \n",GAIT_noFC_multiobiettivo.alpha);
		}
		if (options.hasArgument("beta")) {
			double beta = Double.parseDouble((String) options.valueOf("beta"));
			GAIT_noFC_multiobiettivo.beta=beta;
			System.out.printf("beta -> %f \n",GAIT_noFC_multiobiettivo.beta);
		}
		if (options.hasArgument("gamma")) {
			double gamma = Double.parseDouble((String) options.valueOf("gamma"));
			GAIT_noFC_multiobiettivo.gamma=gamma;
			System.out.printf("gamma -> %f \n",GAIT_noFC_multiobiettivo.gamma);
		}
		if (options.has("quiet")) {
			Singletons.cromosomastream.active=false;
		}
		if (options.has("verbose")) {
			Singletons.cromosomastream.active=true;
			GlobalLogger.init_verbose();
		}
		if (options.has("stat")) {
			Singletons.cromosomastream.active=true;
		}
		if (options.has("buildTime")) {
			System.out.printf("il Jar è stato compilato il %s\n", SysUtil.jarBuildTime());
		}
		
		
		
		if (options.has("ProduciAlberi")) {
			producialberi();
			return;
		}
		
		if (options.has("MisuraTempi")) {
			misuratempi();
			return;
		}
		
		if (options.has("gait")) {
			// --gait --trainingset=<trainingsetpath> --testset=<testsetpath>
			// --scoringset=<scoringsetpath> --nclassi=<nclassi>
			// --gait --settings=<JsonSettingsPath>
			if (options.hasArgument("trainingset") && options.hasArgument("testset")
					&& options.hasArgument("scoringset") && options.hasArgument("nclassi")) {
				String trainingset = (String) options.valueOf("trainingset");
				String testset = (String) options.valueOf("testset");
				String scoringset = (String) options.valueOf("scoringset");
				//
				System.out.printf("trainingset -> '%s'\n", trainingset);
				System.out.printf("testset -> '%s'\n", testset);
				System.out.printf("scoringset -> '%s'\n", scoringset);
				//
				Singletons.cromosomastream.active=false;
				gait(trainingset, testset, scoringset);
				return;

			}
			if (options.hasArgument("settings")) {
				String settings_content = (String) options.valueOf("settings");
				System.out.printf("setting -> '%s'\n", settings_content);
				System.out.printf("settings info\n");
				settings_content = StringUtil.readFileAsString(settings_content);
				System.out.println(settings_content);
				Singletons.cromosomastream.active=false;
				gait(settings_content);
				return;

			}
			System.err.println("Le sintassi possibili di gait sono:");
			System.err
					.println(" --gait --trainingset=<trainingsetpath> --testset=<testsetpath>  --scoringset=<scoringsetpath> --nclassi=<nclassi>");
			System.err.println("\t oppure");
			System.err.println(" --gait --settings=<JsonSettingsPath>");
			return;
		}

		if (options.has("gait-multi")) {
			// --gait --trainingset=<trainingsetpath> --testset=<testsetpath>
			// --scoringset=<scoringsetpath> --nclassi=<nclassi>
			// --gait --settings=<JsonSettingsPath>
			Singletons.cromosomastream.active=false;
			if (options.hasArgument("dataset") && options.hasArgument("generazioni")) {
				dataset_url = (String) options.valueOf("dataset");
				int generazioni = Integer.parseInt((String) options.valueOf("generazioni"));
				//
				System.out.printf("dataset -> '%s'\n", dataset_url);
				System.out.printf("generazioni -> %d\n", generazioni);
				//
				gait_multi(dataset_url, generazioni);
				return;

			}
			if (options.hasArgument("settings")) {
				String settings_content = (String) options.valueOf("settings");
				System.out.printf("setting -> '%s'\n", settings_content);
				System.out.printf("settings info\n");
				settings_content = StringUtil.readFileAsString(settings_content);
				System.out.println(settings_content);
				gait_multi(settings_content);
				return;

			}

			return;
		}

		if (options.has("iris")) {
			Singletons.cromosomastream.active=false;
			iris();
			return;
		}
		if (options.has("gaitDefault")) {
			Singletons.cromosomastream.active=false;
			gait();
			return;
		}
		if (options.has("testaalbero")) {
			Singletons.cromosomastream.active=false;
			testaalbero(albero);
			return;
		}
		if (options.has("gaitMultiBenchmark")) {
			//Singletons.cromosomastream.active=true;
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			gait_multi_benchmark(generazioni);
			return;
		}
		if (options.has("gaitMultiMutanteBenchmark")) {
			//Singletons.cromosomastream.active=true;
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			gait_multi_mutante_benchmark(generazioni);
			return;
		}

		
		
		
		if (options.has("gaitCompleteTarpeianBenchmark")) {
			//Singletons.cromosomastream.active=true;
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			gait_complete_benchmark_tarpeian(generazioni);
			return;
		}
		
		
		if (options.has("gaitCompleteBenchmark")) {
			//Singletons.cromosomastream.active=true;
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			gait_complete_benchmark(generazioni);
			return;
		}
		if (options.has("gaitCompleteMutanteBenchmark")) {
			//Singletons.cromosomastream.active=true;
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			gait_complete_mutante_benchmark(generazioni);
			return;
		}		
		
		
		
		if (options.has("SfpMultiBenchmark")) {
			//Singletons.cromosomastream.active=true;
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			sfp_Multi_Benchmark(generazioni);
			return;
		}		
		
		if (options.has("RankedMultiBenchmark")) {
			//Singletons.cromosomastream.active=true;
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			ranked_Multi_Benchmark(generazioni);
			return;
		}		
		
		if (options.has("TorneoMultiMutanteBenchmark")) {
			//Singletons.cromosomastream.active=true;
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			torneo_MultiMutante_Benchmark(generazioni);
			return;
		}		
		
		if (options.has("TorneoMultiBenchmark")) {
			//Singletons.cromosomastream.active=true;
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			torneo_Multi_Benchmark(generazioni);
			return;
		}		

		
		if (options.has("gaitComplete")) {
			Singletons.cromosomastream.active=false;
			GlobalLogger.init_verbose();
			gait_complete();
			return;
		}

		if (options.has("WholeTrainingBenchmark3000")) {
			Singletons.cromosomastream.active=false;
			WholeTrainingBenchmark(100);
			return;
		}

		System.out.println("Le sintassi possibili di gait sono:");
		System.out
				.println(" --gait --trainingset=<trainingsetpath> --testset=<testsetpath>  --scoringset=<scoringsetpath> --nclassi=<nclassi>");
		System.out.println("\t oppure");
		System.out.println(" --gait --settings=<JsonSettingsPath>");

		System.out.println("\nLe sintassi possibili di gait_multi sono:");
		System.out.println(" --gait-multi --dataset=<datasetpath> --generazioni=<numerogenerazioni>");
		System.out.println("\t oppure");
		System.out.println(" --gait-multi --settings=<JsonSettingsPath>");
		System.out.println("\nAltri comandi possibili sono:");
		parser.printHelpOn(System.out);

		System.err.println("Non è stato fornito nessun argomento dalla linea di comando o sonos tati forniti argomenti non validi,\n");
		System.out.println("\tavvio con le impostazioni di default");
		System.out.printf("il Jar è stato compilato il %s\n", SysUtil.jarBuildTime());
		//gait_multi();
		//gait_multi_benchmark(100);
		//sfp_Multi_Benchmark(25);
		//gait_complete_benchmark(100);
		//ranked_Multi_Benchmark(100);
		//torneo_Multi_Benchmark(500);
		//System.err.println("Non è stato fornito nessun argomento dalla linea di comando o sonos tati forniti argomenti non validi,\n\tavvio gait-multi con le impostazioni di default");
		//gait_complete_benchmark_tarpeian(100);
		//testaalbero(albero);
	}

	/**
	 * Esegue un benchmark su Gait-tarpeian (obiettivo singolo)
	 * @param generazioni
	 * @throws Exception
	 */
	private static void gait_complete_benchmark_tarpeian(Integer generazioni) throws Exception {
			System.out.println(SysUtil.getMethodName(1));
			FileReader dataset_stream = new FileReader(dataset_url);
			Instances dataset = new Instances(dataset_stream);
			System.out.println(generazioni);
			// int generazioni = 100;
			Dataset d;
			for (int a = 0; a < istanze; a++) {
				String nomecolonna = "istanza_" + a;
				d = new Dataset(dataset, percentualetrainingset, percentualescoringset, percentualetestset);
				Singletons.cromosomastream.createColumn(nomecolonna);
				Singletons.cromosomastream.setColonna_corrente(nomecolonna);
				Singletons.pesistream.createColumn(nomecolonna);
				Singletons.pesistream.setColonna_corrente(nomecolonna);
				gait_complete_tarpeian(d, generazioni);
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
	private static void gait_complete_tarpeian(Dataset d, Integer generazioni) throws Exception {
		AlgoritmoEvolutivoCustomTarpeian gaitrunner = new AlgoritmoEvolutivoCustomTarpeian(d, generazioni,
				popolazione_size, false, albero_size);
		gaitrunner.begin();
	}

	private static void torneo_MultiMutante_Benchmark(Integer generazioni) throws Exception {
		
		System.out.flush();
		System.out.println(SysUtil.getMethodName(1));
		
		FileReader dataset_stream = new FileReader(Main.dataset_url);
		Instances dataset = new Instances(dataset_stream);
		//int generazioni = 25;
		Dataset d;
		for (int a = 0; a < istanze; a++) {
			String nomecolonna = "istanza_" + a;
			d = new Dataset(dataset, percentualetrainingset, percentualescoringset, percentualetestset);
			Singletons.cromosomastream.createColumn(nomecolonna);
			Singletons.cromosomastream.setColonna_corrente(nomecolonna);
			Singletons.pesistream.createColumn(nomecolonna);
			Singletons.pesistream.setColonna_corrente(nomecolonna);
			torneo_multi_mutante(d, generazioni);
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
		
		FileReader dataset_stream = new FileReader(Main.dataset_url);
		Instances dataset = new Instances(dataset_stream);
		//int generazioni = 25;
		Dataset d;
		for (int a = 0; a < istanze; a++) {
			String nomecolonna = "istanza_" + a;
			d = new Dataset(dataset, percentualetrainingset, percentualescoringset, percentualetestset);
			Singletons.cromosomastream.createColumn(nomecolonna);
			Singletons.cromosomastream.setColonna_corrente(nomecolonna);
			Singletons.pesistream.createColumn(nomecolonna);
			Singletons.pesistream.setColonna_corrente(nomecolonna);
			torneo_multi(d, generazioni);
			FloatStream ft=Singletons.cromosomastream.calcola(d.testset, d.nclassi);
			Singletons.floatstream.merge(ft);
			Singletons.cromosomastream.deleteColumn(nomecolonna);
		}
		System.out.println(Singletons.floatstream.ricomponi().toString());
		System.out.println(Singletons.pesistream.ricomponi().toString());				
	}
	
	
	private static void ranked_Multi_Benchmark(Integer generazioni) throws Exception {
		
		FileReader dataset_stream = new FileReader(Main.dataset_url);
		Instances dataset = new Instances(dataset_stream);
		//int generazioni = 25;
		Dataset d;
		for (int a = 0; a < istanze; a++) {
			String nomecolonna = "istanza_" + a;
			d = new Dataset(dataset, percentualetrainingset, percentualescoringset, percentualetestset);
			Singletons.cromosomastream.createColumn(nomecolonna);
			Singletons.cromosomastream.setColonna_corrente(nomecolonna);
			Singletons.pesistream.createColumn(nomecolonna);
			Singletons.pesistream.setColonna_corrente(nomecolonna);
			ranked_multi(d, generazioni);
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
	public static void sfp_Multi_Benchmark(Integer generazioni) throws Exception {
			
			FileReader dataset_stream = new FileReader(Main.dataset_url);
			Instances dataset = new Instances(dataset_stream);
			//int generazioni = 25;
			Dataset d;
			for (int a = 0; a < istanze; a++) {
				String nomecolonna = "istanza_" + a;
				d = new Dataset(dataset, percentualetrainingset, percentualescoringset, percentualetestset);
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
			
			FileReader dataset_stream = new FileReader(Main.dataset_url);
			Instances dataset = new Instances(dataset_stream);
			//int generazioni = 25;
			Dataset d;
			for (int a = 0; a < istanze; a++) {
				String nomecolonna = "istanza_" + a;
				d = new Dataset(dataset, percentualetrainingset, percentualescoringset, percentualetestset);
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
		AlgoritmoEvolutivoCustomSPF gaitrunner = new AlgoritmoEvolutivoCustomSPF(d, generazioni, popolazione_size,mutante,albero_size);
		gaitrunner.begin();
	}


	private static void ranked_multi(Dataset d, Integer generazioni) throws Exception {
		AlgoritmoEvolutivoCustomRanked gaitrunner = new AlgoritmoEvolutivoCustomRanked(d, generazioni, popolazione_size,false,albero_size);
		gaitrunner.begin();
	}

	private static void torneo_multi_mutante(Dataset d, Integer generazioni) throws Exception {
		AlgoritmoEvolutivoCustomTorneo gaitrunner = new AlgoritmoEvolutivoCustomTorneo(d, generazioni, popolazione_size,true,albero_size);
		gaitrunner.begin();
	}
	private static void torneo_multi(Dataset d, Integer generazioni) throws Exception {
		AlgoritmoEvolutivoCustomTorneo gaitrunner = new AlgoritmoEvolutivoCustomTorneo(d, generazioni, popolazione_size,false,albero_size);
		gaitrunner.begin();
	}
	
	
	/**
	 * Calcola 100 alberi j48 su trainingset di 3000 elementi, produce un csv
	 * con le prestazioni e le altezze
	 * 
	 * @param i
	 * @throws Exception
	 */
	public static void WholeTrainingBenchmark(int i) throws Exception {
		FileReader dataset_stream = new FileReader(dataset_url);
		//
		Instances dataset = new Instances(dataset_stream);
		J48Wholetraining trainer = new J48Wholetraining(dataset, 2, i);
		trainer.begin();
		System.out.println(trainer.dump());

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
	 * Avvia {@link #gait_complete(String,int) gait_complete(dataset_url,
	 * generazioni)} con le impostazioni di default
	 * 
	 * @throws Exception
	 * @see gait_complete(String,int)
	 */

	@Deprecated
	public static void gait_complete() throws Exception {
		// String
		// dataset_url="/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/smalldataset.arff";
		// String dataset_url = "/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/completedataset.arff";
		// String
		// dataset_url="/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/wine/winequality-all.arff";

		int generazioni = 25;
		gait_complete(dataset_url, generazioni);

	}

	/**
	 * Esegue per 25 volte {@link #gait_complete(Dataset, int)
	 * gait_complete(Dataset d, int generazioni)} dal dataset completo. produce
	 * in output due csv: uno con l'evoluzione delle prestazioni e uno con
	 * l'evoluzione dei pesi
	 * 
	 * @see gait_complete(Dataset, int)
	 * @throws Exception
	 */
	public static void gait_complete_benchmark(int generazioni) throws Exception {
		System.out.println(SysUtil.getMethodName(1));
		FileReader dataset_stream = new FileReader(dataset_url);
		Instances dataset = new Instances(dataset_stream);
		System.out.println(generazioni);
		// int generazioni = 100;
		Dataset d;
		for (int a = 0; a < istanze; a++) {
			String nomecolonna = "istanza_" + a;
			d = new Dataset(dataset, percentualetrainingset, percentualescoringset, percentualetestset);
			Singletons.cromosomastream.createColumn(nomecolonna);
			Singletons.cromosomastream.setColonna_corrente(nomecolonna);
			Singletons.pesistream.createColumn(nomecolonna);
			Singletons.pesistream.setColonna_corrente(nomecolonna);
			gait_complete(d, generazioni);
			FloatStream ft = Singletons.cromosomastream.calcola(d.testset, d.nclassi);
			Singletons.floatstream.merge(ft);
			Singletons.cromosomastream.deleteColumn(nomecolonna);
		}
		System.out.println(Singletons.floatstream.ricomponi().toString());
		System.out.println(Singletons.pesistream.ricomponi().toString());

	}

	public static void gait_complete_mutante_benchmark(int generazioni) throws Exception {
		// String
		// dataset_url="/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/smalldataset.arff";
		//String dataset_url = "/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/completedataset.arff";
		// String
		// dataset_url="/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/wine/winequality-all.arff";
		System.out.println(SysUtil.getMethodName(1));
		FileReader dataset_stream = new FileReader(dataset_url);
		Instances dataset = new Instances(dataset_stream);
		System.out.println(generazioni);
		// int generazioni = 100;
		Dataset d;
		for (int a = 0; a < istanze; a++) {
			String nomecolonna = "istanza_" + a;
			d = new Dataset(dataset, percentualetrainingset, percentualescoringset, percentualetestset);
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
				popolazione_size,mutante,albero_size);
		gaitrunner.begin();
	}

	/**
	 * Avvia Gait sul dataset, utilizza AlgoritmoEvolutivoCustom(dataset,
	 * generazioni, popolazione_size, nclassi,percentualetrainingset, percentualescoringset, percentualetestset); con
	 * popolazioni iniziali di popolazione_size elementi. <strong>è deprecato</strong>
	 * 
	 * 
	 * @deprecated è meglio passare un dataset già tagliato, per poter poi effettuare misurazioni dall'esterno
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
		AlgoritmoEvolutivoCustom gaitrunner = new AlgoritmoEvolutivoCustom(dataset, generazioni, popolazione_size, nclassi,
				percentualetrainingset, percentualescoringset, percentualetestset);
		// gaitrunner.begin_compact();
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
	public static void gait_complete(Dataset d, int generazioni) throws Exception {
		AlgoritmoEvolutivoCustom gaitrunner = new AlgoritmoEvolutivoCustom(d, generazioni, popolazione_size,false,albero_size);
		gaitrunner.begin();
	}

	public static void gait_mutante_complete(Dataset d, int generazioni) throws Exception {
		AlgoritmoEvolutivoCustom gaitrunner = new AlgoritmoEvolutivoCustom(d, generazioni, popolazione_size,true,albero_size);
		gaitrunner.begin();
	}

	
	/**
	 * Avvia {@link #gait_multi(String,int) gait_multi(dataset_url,
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
	 * Avvia {@link #gait_multi(String,int) gait_multi(dataset_url,
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
		gait_multi(dataset_url, generazioni);

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
		FileReader dataset_stream = new FileReader(Main.dataset_url);
		Instances dataset = new Instances(dataset_stream);
		//int generazioni = 25;
		Dataset d;
		for (int a = 0; a < istanze; a++) {
			String nomecolonna = "istanza_" + a;
			d = new Dataset(dataset, percentualetrainingset, percentualescoringset, percentualetestset);
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
		FileReader dataset_stream = new FileReader(Main.dataset_url);
		Instances dataset = new Instances(dataset_stream);
		//int generazioni = 25;
		Dataset d;
		for (int a = 0; a < istanze; a++) {
			String nomecolonna = "istanza_" + a;
			d = new Dataset(dataset, percentualetrainingset, percentualescoringset, percentualetestset);
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

	/**
	 * Esegue gait_multi con i parametri in ingresso utilizzando
	 * AlgoritmoEvolutivoCustomMultiobiettivo(dataset, generazioni, popolazione_size, nclassi,
	 * percentualetrainingset, percentualescoringset, percentualetestset);
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
				generazioni, popolazione_size, nclassi, percentualetrainingset, percentualescoringset, percentualetestset);
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
	 * Alias per {@link #gait() gait()}
	 * 
	 * @see gait()
	 * @throws Exception
	 */
	public static void gaitDefault() throws Exception {
		gait();
	}

	/**
	 * Esegue {@link #gait(String,String,String) gait(String,String,String)} sui
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
	 * Esegue {@link #gait(String,String,String) gait(String,String,String)}
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
	 * Avvia i test su Iris con le impostazioni di default
	 * 
	 * @throws Exception
	 */
	public static void iris() throws Exception {
		System.out.println("Genero un albero J48 sull'dataset iris.");
		String dataset_url = "/home/darshan/Desktop/Università/Tesi/weka-3-6-10/data/iris_numeric.arff";
		FileReader dataset_stream = new FileReader(dataset_url);
		Instances dataset = new Instances(dataset_stream);
		dataset.setClassIndex(dataset.numAttributes() - 1);
		J48 j48 = new J48();
		j48.setBinarySplits(true);
		j48.buildClassifier(dataset);

		Evaluation evaluation = new Evaluation(dataset); // si inizializza col
															// training set
		StringBuffer statistiche = new StringBuffer();
		Range r = new Range();
		evaluation.evaluateModel(j48, dataset, statistiche, r, false);
		ClassifierTree albero = j48.getTree();
		Cromosoma c = Cromosoma.loadFromJ48(j48);
		System.out.println(albero.graph());
		System.out.println(c.toString());
		System.out.println(c.toYaml());
		Cromosoma c2 = Cromosoma.loadFromYaml(c.toYaml());
		System.out.println(c2.toYaml());
		System.out.println("done");
	}

	/**
	 * Crea un albero, lo esporta, calcola le statistiche, produce due file di
	 * testo.
	 * 
	 * @param dataset
	 * @param testset
	 * @param nomebase
	 * @throws Exception
	 */
	public static String esportaalbero(Instances dataset, Instances testset, String nomebase) throws Exception {
		J48 j48 = new J48();
		j48.setBinarySplits(true);
		j48.buildClassifier(dataset);
		Evaluation evaluation = new Evaluation(dataset); // si inizializza col
															// training set
		StringBuffer statistiche = new StringBuffer();
		Range r = new Range();
		evaluation.evaluateModel(j48, testset, statistiche, r, false);
		Cromosoma c = Cromosoma.loadFromJ48(j48);
		PrintWriter writer = new PrintWriter(nomebase + ".yaml", "UTF-8");
		writer.println(c.toYaml());
		writer.close();
		writer = new PrintWriter(nomebase + ".info", "UTF-8");
		// writer.println(statistiche.toString());
		writer.println(evaluation.toSummaryString());
		writer.println(evaluation.toClassDetailsString());
		writer.println(evaluation.toMatrixString());
		writer.println(evaluation.correct() / testset.numInstances());
		writer.close();

		writer = new PrintWriter(nomebase + ".dot", "UTF-8");
		writer.println(j48.graph());
		writer.close();

		return String.format("%f;%d", evaluation.correct() / testset.numInstances(), c.cromosoma.size());

	}

	/**
	 * Partiziona il dataset in 50 parti e produce 50 alberi con
	 * {@link #esportaalbero(Instances,Instances,String)
	 * esportaalbero(Instances,Instances,String)}
	 * 
	 * @throws Exception
	 * @see esportaalbero(Instances,Instances,String)
	 */
	@Deprecated
	public static void producipopolazioneinizialegait() throws Exception {
		// creo l'albero completo del dataset
		String testset_url = "/home/darshan/Desktop/Università/Tesi/matlab_scripts/Datasets/testset_paper.arff";
		String dataset_url = "/home/darshan/Desktop/Università/Tesi/matlab_scripts/Datasets/dataset_paper.arff";
		FileReader testset_stream = new FileReader(testset_url);
		FileReader dataset_stream = new FileReader(dataset_url);
		Instances dataset = new Instances(dataset_stream);
		Instances testset = new Instances(testset_stream);
		dataset.setClassIndex(dataset.numAttributes() - 1);
		testset.setClassIndex(testset.numAttributes() - 1);
		esportaalbero(dataset, testset, "/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dist/export/wholetraining");
		System.out.println("Wholetraining");
		// partiziono il dataset in 50 parti, per ogni parte creo un albero
		String nomefile;
		String caratteristiche;
		PrintWriter writer = new PrintWriter(
				"/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dist/export/partizioni.csv", "UTF-8");
		for (int i = 0; i < 50; i++) {
			Instances data = new Instances(dataset, i * 60, 60);
			dataset.setClassIndex(dataset.numAttributes() - 1);
			nomefile = String.format("/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dist/export/Albero%d", i + 1);
			caratteristiche = esportaalbero(data, testset, nomefile);
			writer.println(String.format("%d;%s", i + 1, caratteristiche));
			System.out.println(i + 1);
		}
		writer.close();
		System.out.println("Done");
	}

	/**
	 * Crea un albero, lo serializza e poi lo deserializza.<br>
	 * serve per testare la serializzazione.
	 * 
	 * @throws Exception
	 */
	public static void testaalbero() throws Exception {
		System.out.println("Avvio le routine per testare la struttura dati del Cromosoma.");
		// creo l'albero completo del dataset
		String testset_url = "/home/darshan/Desktop/Università/Tesi/matlab_scripts/Datasets/testset_paper.arff";
		String dataset_url = "/home/darshan/Desktop/Università/Tesi/matlab_scripts/Datasets/dataset_paper.arff";
		FileReader testset_stream = new FileReader(testset_url);
		FileReader dataset_stream = new FileReader(dataset_url);
		Instances dataset = new Instances(dataset_stream);
		Instances testset = new Instances(testset_stream);
		dataset.setClassIndex(dataset.numAttributes() - 1);
		testset.setClassIndex(testset.numAttributes() - 1);

		String alberourl = "/home/darshan/Uni/Tesi/tesi/Tesi/dist/export/Albero18.yaml";
		String Yaml_str = StringUtil.readFileAsString(alberourl);

		Cromosoma c = Cromosoma.loadFromYaml(Yaml_str);

		TreeEvaluator te = new TreeEvaluator(c, testset, dataset.numClasses());
		te.evaluate();
		System.out.println(String.format("%f;%d", te.getPrestazioni(), c.cromosoma.size()));
		System.out.println(ArrayUtil.dump(te.getConfusion()));
		System.out.println(c.toYaml());

		Cromosoma c2 = GeneticOperators.crossover(c, c, false);
		System.out.println(c2.toYaml());

		CromosomaDecorator cd = new CromosomaDecorator(c);
		cd.caricaColonne(dataset);

		System.out.println(cd.getGraph().toString());

		System.out.println("Done");

	}
	
	/**
	 * Crea un albero, lo serializza e poi lo deserializza.<br>
	 * serve per testare la serializzazione.
	 * 
	 * @throws Exception
	 */
	public static void testaalbero(String alberourl) throws Exception {
		System.out.println("Avvio le routine per testare la struttura dati del Cromosoma.");
		// creo l'albero completo del dataset
		FileReader dataset_stream = new FileReader(dataset_url);
		Instances dataset = new Instances(dataset_stream);
		dataset.setClassIndex(dataset.numAttributes() - 1);

		String Yaml_str = StringUtil.readFileAsString(alberourl);

		Cromosoma c = Cromosoma.loadFromYaml(Yaml_str);

		TreeEvaluator te = new TreeEvaluator(c, dataset, dataset.numClasses());
		te.evaluate();
		System.out.println(String.format("%f;%d", te.getPrestazioni(), c.cromosoma.size()));
		System.out.println(ArrayUtil.dump(te.getConfusion()));
		System.out.println(c.toYaml());
		CromosomaDecorator cd = new CromosomaDecorator(c);
		cd.caricaColonne(dataset);
		System.out.println(cd.getGraph().toString());
		System.out.println("Done");

	}

	
	
	public static void misuratempi() throws IOException{
		//http://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
		
		//variabili 
		long tic, tac;
	    String filename=base+".log";
	    PrintWriter writer = new PrintWriter(filename, "UTF-8");

		//FloatStream fstream= new FloatStream();
		//configuro il dataset
	    
		FileReader dataset_stream = new FileReader(dataset_url);
		Instances dataset = new Instances(dataset_stream);
		dataset.setClassIndex(dataset.numAttributes() - 1);
		//ottengo i files
		ArrayList<File> codafiles= new ArrayList<>();
		ArrayList<Cromosoma> codacromosomi= new ArrayList<>();
		PathMatcher matcher =  FileSystems.getDefault().getPathMatcher(pattern);
		System.out.printf("Ottengo la lista dei files applicando il pattern '%s'\n",pattern);
		File f = new File("."); // current directory

	    File[] files = f.listFiles();
	    for (File file : files) {
	    	//System.out.println(file.toPath());
	        if (!file.isDirectory() && matcher.matches(file.toPath())) {
		        System.out.println(file.getCanonicalPath());
		        codafiles.add(file);
	        }
	    }
	    //ottengo i cromosomi
	    System.out.println("Carico i cromosomi");
	    for (File file : codafiles) {
			String Yaml_str = StringUtil.readFileAsString(file);
			Cromosoma c = Cromosoma.loadFromYaml(Yaml_str);	  
			codacromosomi.add(c);
	        //fstream.createColumn(file.getName());		        
	    }
	    //misuro
	    System.out.println("Calcolo i tempi di esecuzione SUL DATASET SPECIFICATO COME ARGOMENTO DELLA LINEA DI COMANDO");
	    System.out.printf("produco il files di log:		%s\n",filename);
	    int colonna=0;
	    //warm up
	    int M=codacromosomi.size()/3;
	    if(M>5)M=5;
	    for (int a=0;a<M;a++) {
	    	TreeEvaluator te = new TreeEvaluator(codacromosomi.get(a), dataset, dataset.numClasses());
	    	tic=System.nanoTime();
	    	te.evaluate();
			tac=System.nanoTime();
	    }
	    //misurazioni vere.	 
	    writer.append("0000Nomefile\tTempi\tPrestazioni\tAltezze\tLunghezze\n\n");
	    for (Cromosoma c : codacromosomi) {
	    	//fstream.setColonna_corrente(codafiles.get(colonna).getName());
	    	TreeEvaluator te = new TreeEvaluator(c, dataset, dataset.numClasses());
	    	tic=System.nanoTime();
	    	te.evaluate();
			tac=System.nanoTime();
			//converto in millisecondi.
			double tempo=(0.0f+ tac-tic)/1000000.0f;
			//fstream.append(tempo);
			
			String linea=String.format("%s\t%f\t%f\t%d\t%d\n", codafiles.get(colonna).getName(),tempo,te.getPrestazioni(),c.altezza,c.cromosoma.size());
			writer.append(linea);
			colonna++;
	    }
	    //produco il file di log
	    //writer.append(fstream.ricomponi());
	    writer.close();
}
	
	public static void producialberi() throws Exception{
		String formato="%s03%d%s_%s_.yaml";
		System.out.println(SysUtil.getMethodName(1));
		Singletons.cromosomastream.active=false;
		Singletons.floatstream.active=false;
		Singletons.pesistream.active=false;
		
		GlobalLogger.init_middle();
		FileReader dataset_stream = new FileReader(Main.dataset_url);
		Instances dataset = new Instances(dataset_stream);
		final int alberi = 30, generazioni=100;
		Dataset d;
		String filename,datasetstr;
		CromosomaDecorator cm;
		PrintWriter writer;
		for (int a = 0; a < alberi; a++) {			
			d = new Dataset(dataset, percentualetrainingset ,percentualescoringset, percentualetestset);
			//gaitMulti 15-1-5			
			GAIT_noFC_multiobiettivo.alpha=15;
			GAIT_noFC_multiobiettivo.beta=1;
			GAIT_noFC_multiobiettivo.gamma=15;
			datasetstr=d.testset.relationName();
			filename=String.format(formato, base,a,datasetstr,"gaitMulti_15_1_15antibloat");
			System.out.printf("Scrivo un dump in : %s\n",filename);

			AlgoritmoEvolutivoCustomMultiobiettivo gaitrunner = new AlgoritmoEvolutivoCustomMultiobiettivo(d, generazioni,popolazione_size,false,albero_size);
			cm=gaitrunner.begin();
			writer = new PrintWriter(filename, "UTF-8");
			writer.append(cm.getCromosoma().toYaml());
			writer.close();
			//gaitMulti 5-1-15
			GAIT_noFC_multiobiettivo.alpha=5;
			GAIT_noFC_multiobiettivo.beta=1;
			GAIT_noFC_multiobiettivo.gamma=15;
			datasetstr=d.testset.relationName();
			filename=String.format(formato, base,a,datasetstr,"gaitMulti_5_1_15");
			System.out.printf("\nScrivo un dump in : %s\n",filename);

			gaitrunner = new AlgoritmoEvolutivoCustomMultiobiettivo(d, 100,popolazione_size,false,albero_size);
			cm=gaitrunner.begin();
			writer = new PrintWriter(filename, "UTF-8");
			writer.append(cm.getCromosoma().toYaml());
			writer.close();
			//tarpeian
			datasetstr=d.testset.relationName();
			filename=String.format(formato, base,a,datasetstr,"gaitTarpeian");
			System.out.printf("\nScrivo un dump in : %s\n",filename);
			AlgoritmoEvolutivoCustomTarpeian gaitrunner2 = new AlgoritmoEvolutivoCustomTarpeian(d, generazioni,	popolazione_size, false, albero_size);
			cm=gaitrunner2.begin();
			writer = new PrintWriter(filename, "UTF-8");
			writer.append(cm.getCromosoma().toYaml());
			writer.close();
			gaitrunner2=null;
			//gaitComplete
			datasetstr=d.testset.relationName();
			filename=String.format("%s03%d%s_%s_.yaml", base,a,datasetstr,"gait");
			System.out.printf("\nScrivo un dump in : %s\n",filename);
			AlgoritmoEvolutivoCustom gaitrunner3 = new AlgoritmoEvolutivoCustom(d, generazioni, popolazione_size,false,albero_size);
			gaitrunner3.begin();
			writer = new PrintWriter(filename, "UTF-8");
			writer.append(cm.getCromosoma().toYaml());
			writer.close();
			gaitrunner3=null;

		}
		
	}
	
}
