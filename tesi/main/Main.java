package tesi.main;

import java.text.SimpleDateFormat;
import java.util.Date;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import tesi.controllers.GAIT_noFC_multiobiettivo;
import tesi.util.StringUtil;
import tesi.util.SysUtil;
import tesi.util.logging.GlobalLogger;
import tesi.util.logging.Singletons;


/**
 * @author darshan
 *
 */
public class Main {
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
		parser.accepts("dataset", "specifica la path del dataset unico, se non specificato lo cerca in "+Settings.dataset_url).withRequiredArg();
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
		parser.accepts("albericolorati", "gestisce l'output degli alberi di bloat");
		
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
		
		
		
		
		if (options.has("albericolorati")) {
			Settings.albericolorati=true;
			System.out.printf("albericolorati -> %b \n",Settings.albericolorati);
		}
		
		if (options.hasArgument("percentualetrainingset")) {
			double d = Double.parseDouble((String) options.valueOf("percentualetrainingset"));
			Settings.percentualetrainingset=d;
			System.out.printf("percentualetrainingset -> %f \n",Settings.percentualetrainingset);
		}

		if (options.hasArgument("percentualescoringset")) {
			double d = Double.parseDouble((String) options.valueOf("percentualescoringset"));
			Settings.percentualescoringset=d;
			System.out.printf("percentualescoringset -> %f \n",Settings.percentualescoringset);
		}
		if (options.hasArgument("percentualetestset")) {
			double d = Double.parseDouble((String) options.valueOf("percentualetestset"));
			Settings.percentualetestset=d;
			System.out.printf("percentualetestset -> %f \n",Settings.percentualetestset);
		}
		if (options.hasArgument("albero")) {
			String s = (String) options.valueOf("albero");
			Settings.albero=s;
		}else{
			Settings.albero="/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dist/piccolomostro_breast.yaml";
		}

		
		if (options.hasArgument("pattern")) {
			String s = (String) options.valueOf("pattern");
			Settings.pattern=s;
		}
		
		if (options.hasArgument("base")) {
			String s = (String) options.valueOf("base");
			Settings.base=s;
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("ddhhmmss_");
			Settings.base=sdf.format(new Date());
		}
		
		if (options.hasArgument("dataset")) {
			String s = (String) options.valueOf("dataset");
			Settings.dataset_url=s;
		}else{
			try{
				Settings.dataset_url= SysUtil.getAbsolutePath(Settings.dataset_url);
			}catch(Exception e){
				System.out.println("Non riesco a risolvere la path del dataset di default.");
				Settings.dataset_url=Settings.dataset_url_fallback;
			}
		}
		System.out.printf("dataset -> %s \n",Settings.dataset_url);			

		if (options.hasArgument("campioniperalbero")) {
			int i = Integer.parseInt((String) options.valueOf("campioniperalbero"));
			Settings.albero_size=i;
			System.out.printf("campioniperalbero -> %d \n",i);
		}		
		
		if (options.hasArgument("istanze")) {
			int i = Integer.parseInt((String) options.valueOf("istanze"));
			Settings.istanze=i;
			System.out.printf("istanze -> %d \n",i);
		}		
		if (options.hasArgument("popolazione")) {
			int p = Integer.parseInt((String) options.valueOf("popolazione"));
			Settings.popolazione_size=p;
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
			Test.producialberi();
			return;
		}
		
		if (options.has("MisuraTempi")) {
			Test.misuratempi(Settings.albericolorati);
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
				Deprecate.gait(trainingset, testset, scoringset);
				return;

			}
			if (options.hasArgument("settings")) {
				String settings_content = (String) options.valueOf("settings");
				System.out.printf("setting -> '%s'\n", settings_content);
				System.out.printf("settings info\n");
				settings_content = StringUtil.readFileAsString(settings_content);
				System.out.println(settings_content);
				Singletons.cromosomastream.active=false;
				Deprecate.gait(settings_content);
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
				Settings.dataset_url = (String) options.valueOf("dataset");
				int generazioni = Integer.parseInt((String) options.valueOf("generazioni"));
				//
				System.out.printf("dataset -> '%s'\n", Settings.dataset_url);
				System.out.printf("generazioni -> %d\n", generazioni);
				//
				Deprecate.gait_multi(Settings.dataset_url, generazioni);
				return;

			}
			if (options.hasArgument("settings")) {
				String settings_content = (String) options.valueOf("settings");
				System.out.printf("setting -> '%s'\n", settings_content);
				System.out.printf("settings info\n");
				settings_content = StringUtil.readFileAsString(settings_content);
				System.out.println(settings_content);
				Deprecate.gait_multi(settings_content);
				return;

			}

			return;
		}

		if (options.has("iris")) {
			Singletons.cromosomastream.active=false;
			Test.iris();
			return;
		}
		if (options.has("gaitDefault")) {
			Singletons.cromosomastream.active=false;
			Deprecate.gait();
			return;
		}
		if (options.has("testaalbero")) {
			Singletons.cromosomastream.active=false;
			Test.testaalbero(Settings.albero);
			return;
		}
		if (options.has("gaitMultiBenchmark")) {
			//Singletons.cromosomastream.active=true;
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			GaitMulti.gait_multi_benchmark(generazioni);
			return;
		}
		if (options.has("gaitMultiMutanteBenchmark")) {
			//Singletons.cromosomastream.active=true;
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			GaitMulti.gait_multi_mutante_benchmark(generazioni);
			return;
		}

		
		
		
		if (options.has("gaitCompleteTarpeianBenchmark")) {
			//Singletons.cromosomastream.active=true;
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			Gait.gait_tarpeian_benchmark(generazioni);
			return;
		}
		
		
		if (options.has("gaitCompleteBenchmark")) {
			//Singletons.cromosomastream.active=true;
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			Gait.gait_classic_benchmark(generazioni);
			return;
		}
		if (options.has("gaitCompleteMutanteBenchmark")) {
			//Singletons.cromosomastream.active=true;
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			Gait.gait_mutante_benchmark(generazioni);
			return;
		}		
		
		
		
		if (options.has("SfpMultiBenchmark")) {
			//Singletons.cromosomastream.active=true;
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			SelezioneFitnessProporzionale.sfp_Multi_Benchmark(generazioni);
			return;
		}		
		
		if (options.has("RankedMultiBenchmark")) {
			//Singletons.cromosomastream.active=true;
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			Ranked.ranked_Multi_Benchmark(generazioni);
			return;
		}		
		
		if (options.has("TorneoMultiMutanteBenchmark")) {
			//Singletons.cromosomastream.active=true;
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			Torneo.torneo_MultiMutante_Benchmark(generazioni);
			return;
		}		
		
		if (options.has("TorneoMultiBenchmark")) {
			//Singletons.cromosomastream.active=true;
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			Torneo.torneo_Multi_Benchmark(generazioni);
			return;
		}		

		
		if (options.has("gaitComplete")) {
			Singletons.cromosomastream.active=false;
			GlobalLogger.init_verbose();
			Deprecate.gait_complete();
			return;
		}

		if (options.has("WholeTrainingBenchmark3000")) {
			Singletons.cromosomastream.active=false;
			Test.WholeTrainingBenchmark(100);
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
	
}
