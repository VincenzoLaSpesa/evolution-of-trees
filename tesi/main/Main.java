package tesi.main;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import tesi.controllers.GAIT_multiobiettivo;
import tesi.models.Dataset;
import tesi.util.SysUtil;
import tesi.util.logging.FloatStream;
import tesi.util.logging.GlobalLogger;
import tesi.util.logging.Singletons;
import weka.core.Instances;


/**
 * @author darshan
 *
 */
public class Main {
	
	static OptionParser parser;
	static OptionSet options;
	static boolean debugMode=true;
	
	private static OptionParser inizializzaOptionParser(){
		OptionParser parser = new OptionParser();
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
		return parser;
	}
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		boolean fatto=false;
		Singletons.cromosomastream.active=true;
		GlobalLogger.init_quiet();
		parser= inizializzaOptionParser();
		options = parser.parse(args);
		//inizializza le variabili globali
		parsaSettaggi();
		//esegue le operazioni
		fatto=eseguiComandi();
		
		if(fatto)return;

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

		System.err.println("Non è stato fornito nessun argomento dalla linea di comando o sono stati forniti argomenti non validi,\n");
		System.out.printf("il Jar è stato compilato il %s\n", SysUtil.jarBuildTime());

		if(debugMode){
			System.out.println("\tavvio con le impostazioni di default");
			String fakecmd="--stat --gaitMultiMutanteBenchmark --alpha 5 --beta 1 --gamma 15 --campioniperalbero 16 --istanze 10 --generazioni 100";
			String[] fakeargs=fakecmd.split(" ");
			options = parser.parse(fakeargs);
			//inizializza le variabili globali
			parsaSettaggi();
			//esegue le operazioni
			eseguiComandi();
		}
	}

	private static boolean eseguiComandi() throws Exception {
		if (options.has("ProduciAlberi")) {
			Test.producialberi();
			return true;
		}
		
		if (options.has("MisuraTempi")) {
			Test.misuratempi(Settings.albericolorati);
			return true;
		}
		
		if (options.has("iris")) {
			Singletons.cromosomastream.active=false;
			Test.iris();
			return true;
		}
				
		if (options.has("testaalbero")) {
			Singletons.cromosomastream.active=false;
			Test.testaalbero(Settings.albero);
			return true;
		}
		if (options.has("gaitMultiBenchmark")) {
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			final String info="Benchmark per Gait multi su "+generazioni+ " generazioni.";		
			startBenchmark(generazioni, new Funzione() {				
				@Override
				public String info() {
					return info;
				}
				@Override
				public void call(Dataset d, int generazioni) throws Exception {
					Algoritmi.gait_multi(d, generazioni,false);					
				}
			});
			
			return true;
		}
		if (options.has("gaitMultiMutanteBenchmark")) {
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			final String info="Benchmark per Gait multi-mutante su "+generazioni+ " generazioni.";		
			startBenchmark(generazioni, new Funzione() {				
				@Override
				public String info() {
					return info;
				}
				@Override
				public void call(Dataset d, int generazioni) throws Exception {
					//GaitMulti.gait_multi(Dataset d, int generazioni, boolean mutante)
					Algoritmi.gait_multi(d, generazioni,true);					
				}
			});
			return true;
		}

		
		
		
		if (options.has("gaitCompleteTarpeianBenchmark")) {
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			final String info="Benchmark per Gait Tarpeian su "+generazioni+ " generazioni.";		
			startBenchmark(generazioni, new Funzione() {				
				@Override
				public String info() {
					return info;
				}
				@Override
				public void call(Dataset d, int generazioni) throws Exception {
					Algoritmi.gait_tarpeian(d, generazioni);
				}
			});
			return true;
		}
		
		
		if (options.has("gaitCompleteBenchmark")) {
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");

			final String info="Benchmark per Gait classic su "+generazioni+ " generazioni.";		
			startBenchmark(generazioni, new Funzione() {				
				@Override
				public String info() {
					return info;
				}
				@Override
				public void call(Dataset d, int generazioni) throws Exception {
					Algoritmi.gait_classic(d, generazioni);
				}
			});			
			return true;
		}
		if (options.has("gaitCompleteMutanteBenchmark")) {
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			final String info="Benchmark per Gait mutante su "+generazioni+ " generazioni.";		
			startBenchmark(generazioni, new Funzione() {				
				@Override
				public String info() {
					return info;
				}
				@Override
				public void call(Dataset d, int generazioni) throws Exception {
					Algoritmi.gait_mutante_complete(d, generazioni);					
				}
			});
			return true;
		}		
		
		
		
		if (options.has("SfpMultiBenchmark")) {
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");

			final String info="Benchmark per Gait multi-spf su "+generazioni+ " generazioni.";		
			startBenchmark(generazioni, new Funzione() {				
				@Override
				public String info() {
					return info;
				}
				@Override
				public void call(Dataset d, int generazioni) throws Exception {
					Algoritmi.sfp_multi(d, generazioni, false);
				}
			});						
			return true;
		}		
		
		if (options.has("RankedMultiBenchmark")) {
			//Singletons.cromosomastream.active=true;
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			final String info="Benchmark per Gait Ranked Multi su "+generazioni+ " generazioni.";		
			startBenchmark(generazioni, new Funzione() {				
				@Override
				public String info() {
					return info;
				}
				@Override
				public void call(Dataset d, int generazioni) throws Exception {
					Algoritmi.ranked_multi(d, generazioni);
				}
			});
			
			return true;
		}		
		
		if (options.has("TorneoMultiMutanteBenchmark")) {
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");
			final String info="Benchmark per Gait torneo multi-mutante su "+generazioni+ " generazioni.";		
			startBenchmark(generazioni, new Funzione() {				
				@Override
				public String info() {
					return info;
				}
				@Override
				public void call(Dataset d, int generazioni) throws Exception {
					Algoritmi.torneo_multi_mutante(d, generazioni);
				}
			});
			return true;
		}		
		
		if (options.has("TorneoMultiBenchmark")) {
			//Singletons.cromosomastream.active=true;
			Integer generazioni = 25;
			if (options.hasArgument("generazioni")) {
				generazioni = Integer.parseInt((String)options.valueOf("generazioni"));
			}
			System.out.printf("§§\tPrestGAIT\tPrestWhole\tHGait\tHWhole\n");

			final String info="Benchmark per Gait multi-torneo su "+generazioni+ " generazioni.";		
			startBenchmark(generazioni, new Funzione() {				
				@Override
				public String info() {
					return info;
				}
				@Override
				public void call(Dataset d, int generazioni) throws Exception {
					Algoritmi.torneo_multi(d, generazioni);
				}
			});			
			return true;
		}		

		if (options.has("WholeTrainingBenchmark3000")) {
			Singletons.cromosomastream.active=false;
			Test.WholeTrainingBenchmark(100);
			return true;
		}
		return false;
	}

	private static void parsaSettaggi() {
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
			GAIT_multiobiettivo.alpha=alpha;
			System.out.printf("alpha -> %f \n",GAIT_multiobiettivo.alpha);
		}
		if (options.hasArgument("beta")) {
			double beta = Double.parseDouble((String) options.valueOf("beta"));
			GAIT_multiobiettivo.beta=beta;
			System.out.printf("beta -> %f \n",GAIT_multiobiettivo.beta);
		}
		if (options.hasArgument("gamma")) {
			double gamma = Double.parseDouble((String) options.valueOf("gamma"));
			GAIT_multiobiettivo.gamma=gamma;
			System.out.printf("gamma -> %f \n",GAIT_multiobiettivo.gamma);
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

		
	}

	/*
	 * Avvia un benchmark.
	 * (Devo ammettere che in Java8 sarebbe venuto tutto più pulito...)
	 */
	public static void startBenchmark(int generazioni, Funzione funzione) throws Exception{
		System.out.println("Benchmark di "+funzione.toString());
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
			try {
				funzione.call(d,generazioni);
			} catch (Exception e) {
				GlobalLogger.logger.severe("Errore durante l'esecuzione di "+funzione.toString()+ "\n Verrà generata un eccezione.");
				System.err.println("Errore durante l'esecuzione di "+funzione.toString());
				throw new Exception();
			}
			FloatStream ft = Singletons.cromosomastream.calcola(d.testset, d.nclassi);
			Singletons.floatstream.merge(ft);
			Singletons.cromosomastream.deleteColumn(nomecolonna);
		}
		System.out.println(Singletons.floatstream.ricomponi().toString());
		System.out.println(Singletons.pesistream.ricomponi().toString());		
	}
	
}
