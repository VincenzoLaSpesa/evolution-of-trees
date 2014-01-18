package tesi.main;

import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Map;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import tesi.controllers.GeneticOperators;
import tesi.controllers.TreeEvaluator;
import tesi.interfaces.CromosomaDecorator;
import tesi.interfaces.GAIT_noFC_run;
import tesi.interfaces.launchers.AlgoritmoEvolutivoCustom;
import tesi.interfaces.launchers.AlgoritmoEvolutivoCustomMultiobiettivo;
import tesi.interfaces.launchers.J48Wholetraining;
import tesi.models.Cromosoma;
import tesi.models.Dataset;
import tesi.models.Singletons;
import tesi.util.ArrayUtil;
import tesi.util.StringUtil;
import tesi.util.logging.FloatStream;
import tesi.util.logging.GlobalLogger;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.j48.ClassifierTree;
import weka.core.Instances;
import weka.core.Range;

import com.google.gson.Gson;

public class Main {
	public static final String defaultdataset = "/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/completedataset.arff";

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		GlobalLogger.init_quiet();
		OptionParser parser = new OptionParser();
		parser.accepts("gait").withOptionalArg();
		parser.accepts("gait-multi").withOptionalArg();
		parser.accepts("trainingset", "specifica la path del trainingset").withRequiredArg();
		parser.accepts("settings", "carica i settaggi da un file json").withRequiredArg();
		parser.accepts("testset", "specifica la path del testset").withRequiredArg();
		parser.accepts("scoringset", "specifica la path dello scoringset").withRequiredArg();
		parser.accepts("dataset", "specifica la path del dataset unico").withRequiredArg();
		parser.accepts("generazioni", "numero di generazioni").withRequiredArg();
		parser.accepts("popolazione", "popolazione iniziale").withRequiredArg();

		//
		parser.accepts("iris", "Avvia i test su Iris con le impostazioni di default");
		parser.accepts("gaitDefault", "Avvia i test su Gait con le impostazioni di default");
		// parser.accepts("gaitMultiDefault",
		// "Avvia i test su GaitMulti con le impostazioni di default");
		parser.accepts("testaalbero", "Avvia i test sulla creazione/serializzazione degli alberi");

		parser.accepts("gaitCompleteBenchmark",
				"Avvia il benchmark su gait con le impostazioni di default utilizzando il dataset per intero");
		parser.accepts("gaitMultiBenchmark", "Avvia il benchmark su GaitMulti con le impostazioni di default");
		parser.accepts("WholeTrainingBenchmark3000", "Calcola 100 alberi j48 su trainingset di 3000 elementi");

		OptionSet options = parser.parse(args);
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
				gait(trainingset, testset, scoringset);
				return;

			}
			if (options.hasArgument("settings")) {
				String settings_content = (String) options.valueOf("settings");
				System.out.printf("setting -> '%s'\n", settings_content);
				System.out.printf("settings info\n");
				settings_content = StringUtil.readFileAsString(settings_content);
				System.out.println(settings_content);
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
			if (options.hasArgument("dataset") && options.hasArgument("generazioni")) {
				String dataset = (String) options.valueOf("dataset");
				int generazioni = Integer.parseInt((String) options.valueOf("generazioni"));
				//
				System.out.printf("dataset -> '%s'\n", dataset);
				System.out.printf("generazioni -> %d\n", generazioni);
				//
				gait_multi(dataset, generazioni);
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
			iris();
			return;
		}
		if (options.has("gaitDefault")) {
			gait();
			return;
		}
		if (options.has("testaalbero")) {
			testaalbero();
			return;
		}
		if (options.has("gaitMultiBenchmark")) {
			gait_multi_benchmark();
			return;
		}
		if (options.has("gaitCompleteBenchmark")) {
			gait_complete_benchmark();
			return;
		}
		if (options.has("WholeTrainingBenchmark3000")) {
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

		// System.err.println("Non è stato fornito nessun argomento dalla linea di comando o sonos tati forniti argomenti non validi,\n\tavvio gait con le impostazioni di default");
		// gait_complete_benchmark();

		// System.err.println("Non è stato fornito nessun argomento dalla linea di comando o sonos tati forniti argomenti non validi,\n\tavvio gait-multi con le impostazioni di default");

	}

	/**
	 * Calcola 100 alberi j48 su trainingset di 3000 elementi, produce un csv
	 * con le prestazioni e le altezze
	 * 
	 * @param i
	 * @throws Exception
	 */
	public static void WholeTrainingBenchmark(int i) throws Exception {
		FileReader dataset_stream = new FileReader(defaultdataset);
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

	public static void gait_complete() throws Exception {
		// String
		// dataset_url="/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/smalldataset.arff";
		String dataset_url = "/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/completedataset.arff";
		// String
		// dataset_url="/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/wine/winequality-all.arff";

		int generazioni = 10;
		gait_complete(dataset_url, generazioni);

	}

	/**
	 * Esegue per 25 volte {@link #gait_complete(Dataset, int)
	 * gait_complete(Dataset d, int generazioni)} dal dataset completo, per ogni
	 * run evolve per 100 generazioni. produce in output due csv: uno con
	 * l'evoluzione delle prestazioni e uno con l'evoluzione dei pesi
	 * 
	 * @see gait_complete(Dataset, int)
	 * @throws Exception
	 */
	public static void gait_complete_benchmark() throws Exception {
		// String
		// dataset_url="/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/smalldataset.arff";
		String dataset_url = "/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/completedataset.arff";
		// String
		// dataset_url="/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/wine/winequality-all.arff";
		FileReader dataset_stream = new FileReader(dataset_url);
		Instances dataset = new Instances(dataset_stream);
		int generazioni = 100;
		Dataset d;
		for (int a = 0; a < 25; a++) {
			String nomecolonna = "istanza_" + a;
			d = new Dataset(dataset, 0.5454545454, 0.1818181818, 0.2727272727);
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

	/**
	 * Avvia Gait sul dataset, utilizza
	 * AlgoritmoEvolutivoCustomMultiobiettivo(Dataset d,int numerogenerazioni,
	 * int popolazione_iniziale) con popolazioni iniziali di 50 elementi
	 * 
	 * @param d
	 * @param generazioni
	 * @throws Exception
	 * @see AlgoritmoEvolutivoCustomMultiobiettivo
	 */
	public static void gait_multi(Dataset d, int generazioni) throws Exception {
		AlgoritmoEvolutivoCustomMultiobiettivo gaitrunner = new AlgoritmoEvolutivoCustomMultiobiettivo(d, generazioni,
				50);
		gaitrunner.begin();
	}

	/**
	 * Avvia Gait sul dataset, utilizza AlgoritmoEvolutivoCustom(dataset,
	 * generazioni, 50, nclassi,0.5454545454, 0.1818181818, 0.2727272727); con
	 * popolazioni iniziali di 50 elementi. <strong>è deprecato</strong>
	 * 
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
		AlgoritmoEvolutivoCustom gaitrunner = new AlgoritmoEvolutivoCustom(dataset, generazioni, 50, nclassi,
				0.5454545454, 0.1818181818, 0.2727272727);
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
		AlgoritmoEvolutivoCustom gaitrunner = new AlgoritmoEvolutivoCustom(d, generazioni, 50);
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
		String dataset_url = "/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/completedataset.arff";
		// String
		// dataset_url="/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/wine/winequality-all.arff"
		int generazioni = 25;
		gait_multi(dataset_url, generazioni);

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
	public static void gait_multi_benchmark() throws Exception {
		// String
		// dataset_url="/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/smalldataset.arff";
		String dataset_url = "/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/completedataset.arff";
		// String
		// dataset_url="/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/wine/winequality-all.arff";
		FileReader dataset_stream = new FileReader(dataset_url);
		Instances dataset = new Instances(dataset_stream);
		int generazioni = 25;
		Dataset d;
		for (int a = 0; a < 100; a++) {
			String nomecolonna = "istanza_" + a;
			d = new Dataset(dataset, 0.5454545454, 0.1818181818, 0.2727272727);
			Singletons.cromosomastream.createColumn(nomecolonna);
			Singletons.cromosomastream.setColonna_corrente(nomecolonna);
			Singletons.pesistream.createColumn(nomecolonna);
			Singletons.pesistream.setColonna_corrente(nomecolonna);
			gait_multi(d, generazioni);
			// FloatStream ft=Singletons.cromosomastream.calcola(d.testset,
			// d.nclassi);
			// Singletons.floatstream.merge(ft);
			Singletons.cromosomastream.deleteColumn(nomecolonna);
		}
		System.out.println(Singletons.floatstream.ricomponi().toString());
		System.out.println(Singletons.pesistream.ricomponi().toString());

	}

	/**
	 * Esegue gait_multi con i parametri in ingresso utilizzando
	 * AlgoritmoEvolutivoCustomMultiobiettivo(dataset, generazioni, 50, nclassi,
	 * 0.5454545454, 0.1818181818, 0.2727272727);
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
				generazioni, 50, nclassi, 0.5454545454, 0.1818181818, 0.2727272727);
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
}
