package tesi.main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.ArrayList;

import tesi.controllers.GAIT_multiobiettivo;
import tesi.controllers.GeneticOperators;
import tesi.controllers.TreeEvaluator;
import tesi.interfaces.CromosomaDecorator;
import tesi.launcher.AlgoritmoEvolutivoCustomSimple;
import tesi.launcher.AlgoritmoEvolutivoCustomMultiobiettivo;
import tesi.launcher.AlgoritmoEvolutivoCustomTarpeian;
import tesi.launcher.J48Wholetraining;
import tesi.models.Cromosoma;
import tesi.models.Dataset;
import tesi.util.ArrayUtil;
import tesi.util.StringUtil;
import tesi.util.SysUtil;
import tesi.util.logging.GlobalLogger;
import tesi.util.logging.Singletons;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.j48.ClassifierTree;
import weka.core.Instances;
import weka.core.Range;

public abstract class Test {

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
		FileReader dataset_stream = new FileReader(Settings.dataset_url);
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

	public static void producialberi() throws Exception{
		String formato="%s03%d%s_%s_.yaml";
		System.out.println(SysUtil.getMethodName(1));
		Singletons.cromosomastream.active=false;
		Singletons.floatstream.active=false;
		Singletons.pesistream.active=false;
		
		GlobalLogger.init_middle();
		FileReader dataset_stream = new FileReader(Settings.dataset_url);
		Instances dataset = new Instances(dataset_stream);
		final int alberi = 30, generazioni=100;
		Dataset d;
		String filename,datasetstr;
		CromosomaDecorator cm;
		PrintWriter writer;
		for (int a = 0; a < alberi; a++) {			
			d = new Dataset(dataset, Settings.percentualetrainingset ,Settings.percentualescoringset, Settings.percentualetestset);
			//gaitMulti 15-1-5			
			GAIT_multiobiettivo.alpha=15;
			GAIT_multiobiettivo.beta=1;
			GAIT_multiobiettivo.gamma=15;
			datasetstr=d.testset.relationName();
			filename=String.format(formato, Settings.base,a,datasetstr,"gaitMulti_15_1_15antibloat");
			System.out.printf("Scrivo un dump in : %s\n",filename);
	
			AlgoritmoEvolutivoCustomMultiobiettivo gaitrunner = new AlgoritmoEvolutivoCustomMultiobiettivo(d, generazioni,Settings.popolazione_size,false,Settings.albero_size);
			cm=gaitrunner.begin();
			writer = new PrintWriter(filename, "UTF-8");
			writer.append(cm.getCromosoma().toYaml());
			writer.close();
			//gaitMulti 5-1-15
			GAIT_multiobiettivo.alpha=5;
			GAIT_multiobiettivo.beta=1;
			GAIT_multiobiettivo.gamma=15;
			datasetstr=d.testset.relationName();
			filename=String.format(formato, Settings.base,a,datasetstr,"gaitMulti_5_1_15");
			System.out.printf("\nScrivo un dump in : %s\n",filename);
	
			gaitrunner = new AlgoritmoEvolutivoCustomMultiobiettivo(d, 100,Settings.popolazione_size,false,Settings.albero_size);
			cm=gaitrunner.begin();
			writer = new PrintWriter(filename, "UTF-8");
			writer.append(cm.getCromosoma().toYaml());
			writer.close();
			//tarpeian
			datasetstr=d.testset.relationName();
			filename=String.format(formato, Settings.base,a,datasetstr,"gaitTarpeian");
			System.out.printf("\nScrivo un dump in : %s\n",filename);
			AlgoritmoEvolutivoCustomTarpeian gaitrunner2 = new AlgoritmoEvolutivoCustomTarpeian(d, generazioni,	Settings.popolazione_size, false, Settings.albero_size);
			cm=gaitrunner2.begin();
			writer = new PrintWriter(filename, "UTF-8");
			writer.append(cm.getCromosoma().toYaml());
			writer.close();
			gaitrunner2=null;
			//gaitComplete
			datasetstr=d.testset.relationName();
			filename=String.format("%s03%d%s_%s_.yaml", Settings.base,a,datasetstr,"gait");
			System.out.printf("\nScrivo un dump in : %s\n",filename);
			AlgoritmoEvolutivoCustomSimple gaitrunner3 = new AlgoritmoEvolutivoCustomSimple(d, generazioni, Settings.popolazione_size,false,Settings.albero_size);
			gaitrunner3.begin();
			writer = new PrintWriter(filename, "UTF-8");
			writer.append(cm.getCromosoma().toYaml());
			writer.close();
			gaitrunner3=null;
	
		}
		
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
	 * Calcola 100 alberi j48 su trainingset di 3000 elementi, produce un csv
	 * con le prestazioni e le altezze
	 * 
	 * @param i
	 * @throws Exception
	 */
	public static void WholeTrainingBenchmark(int i) throws Exception {
		FileReader dataset_stream = new FileReader(Settings.dataset_url);
		//
		Instances dataset = new Instances(dataset_stream);
		J48Wholetraining trainer = new J48Wholetraining(dataset, 2, i);
		trainer.begin();
		System.out.println(trainer.dump());
	
	}

	public static void misuratempi(boolean albericolorati) throws IOException{
			//http://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
			
			//variabili 
			long tic, tac;
		    String filename=Settings.base+".log";
		    PrintWriter writer = new PrintWriter(filename, "UTF-8");
		    FileReader dataset_stream;
		    Instances dataset;
			//FloatStream fstream= new FloatStream();
			//ottengo i files
			ArrayList<File> codafiles= new ArrayList<>();
			ArrayList<Cromosoma> codacromosomi= new ArrayList<>();
			PathMatcher matcher =  FileSystems.getDefault().getPathMatcher(Settings.pattern);
			System.out.printf("Ottengo la lista dei files applicando il pattern '%s'\n",Settings.pattern);
			File f = new File("."); // current directory
	
			
			
		    File[] files = f.listFiles();
		    	    
		    
		    
		    for (File file : files) {
		    	//System.out.println(file.toPath());
		        if (!file.isDirectory() && matcher.matches(file.toPath())) {
			        System.out.println(file.getCanonicalPath());
			        codafiles.add(file);
		        }
		    }
		    
			//configuro il dataset
		    if(codafiles.size()>0){
				dataset_stream = new FileReader(Settings.dataset_url);
				dataset = new Instances(dataset_stream);
				dataset.setClassIndex(dataset.numAttributes() - 1);
		    }else{
		    	writer.close();
		    	return;
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
		    int k=0;
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
			    if(albericolorati){
			    	CromosomaDecorator cd = new CromosomaDecorator(c);
			    	cd.caricaColonne(dataset);		    	
			    	String filename2=c.altezza+"_"+k+"_bloatcolorato_"+codafiles.get(colonna-1).getName()+".dot";
			    	System.out.printf("Produco l'albero di bloat in :%s 	\n",filename2);
				    PrintWriter writer2 = new PrintWriter(filename2, "UTF-8");
				    writer2.append(cd.getGraph_bloat(te.utilizzo));
				    writer2.append("//");
				    writer2.append("//Lunghezza: "+c.cromosoma.size()+"\n");//magie del preprocessore
				    writer2.append("//Altezza: "+c.altezza+"\n");
				    writer2.append("//Prestazioni: "+te.getPrestazioni()+"\n");
				    writer2.close();
				    k++;
			    }
			    
		    }
		    //produco il file di log
		    //writer.append(fstream.ricomponi());
		    writer.close();
	}

}
