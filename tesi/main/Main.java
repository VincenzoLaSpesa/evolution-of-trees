package tesi.main;

import java.io.FileReader;
import java.io.PrintWriter;

import tesi.controllers.GeneticOperator;
import tesi.controllers.TreeEvaluator;
import tesi.models.Cromosoma;
import tesi.util.ArrayUtil;
import tesi.util.StringUtil;
import tesi.views.CromosomaDecorator;
import tesi.views.GAIT_noFC_run;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.j48.ClassifierTree;
import weka.core.Instances;
import weka.core.Range;

public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//iris();
		gait();
		//testaalbero();
	}
	
	public static void gait() throws Exception{
		String testset_url="/home/darshan/Desktop/Università/Tesi/matlab_scripts/Datasets/testset_paper.arff";
		String dataset_url="/home/darshan/Desktop/Università/Tesi/matlab_scripts/Datasets/dataset_paper.arff";
		FileReader testset_stream= new FileReader(testset_url);
		FileReader dataset_stream= new FileReader(dataset_url);
		Instances dataset = new Instances(dataset_stream);
		Instances testset = new Instances(testset_stream);		
		dataset.setClassIndex(dataset.numAttributes() - 1);
		testset.setClassIndex(testset.numAttributes() - 1);
		int nclassi=dataset.numClasses();
		GAIT_noFC_run gaitrunner= new GAIT_noFC_run(dataset, testset, nclassi);
		gaitrunner.run();
		
	}
	
	public static void iris() throws Exception{
		String dataset_url="/home/darshan/Desktop/Università/Tesi/weka-3-6-10/data/iris_numeric.arff";
		FileReader dataset_stream= new FileReader(dataset_url);
		Instances dataset = new Instances(dataset_stream);
		dataset.setClassIndex(dataset.numAttributes() - 1);
		J48 j48 = new J48();
		j48.setBinarySplits(true);
		j48.buildClassifier(dataset);
		
		Evaluation evaluation = new Evaluation(dataset); //si inizializza col training set
		StringBuffer statistiche= new StringBuffer();
		Range r= new Range();
		evaluation.evaluateModel(j48, dataset, statistiche,r,false);
		ClassifierTree albero=  j48.getTree();
		Cromosoma c= Cromosoma.loadFromJ48(j48);
		System.out.println(albero.graph());		
		System.out.println(c.toString());
		System.out.println(c.toYaml());
		Cromosoma c2=Cromosoma.loadFromYaml(c.toYaml());
		System.out.println(c2.toYaml());
		System.out.println("done");		
	}

	/**
	 * Crea un albero, lo esporta, calcola le statistiche, produce due file di testo.
	 * @param dataset
	 * @param testset
	 * @param nomebase
	 * @throws Exception
	 */
	public static String esportaalbero(Instances dataset, Instances testset , String nomebase) throws Exception{
		J48 j48 = new J48();
		j48.setBinarySplits(true);
		j48.buildClassifier(dataset);	
		Evaluation evaluation = new Evaluation(dataset); //si inizializza col training set
		StringBuffer statistiche= new StringBuffer();
		Range r= new Range();
		evaluation.evaluateModel(j48, testset, statistiche,r,false);
		Cromosoma c= Cromosoma.loadFromJ48(j48);
		PrintWriter writer = new PrintWriter(nomebase+".yaml", "UTF-8");
		writer.println(c.toYaml());
		writer.close();
		writer = new PrintWriter(nomebase+".info", "UTF-8");
		//writer.println(statistiche.toString());
		writer.println(evaluation.toSummaryString());
		writer.println(evaluation.toClassDetailsString());
		writer.println(evaluation.toMatrixString());
		writer.println(evaluation.correct()/testset.numInstances());
		writer.close();

		
		writer = new PrintWriter(nomebase+".dot", "UTF-8");
		writer.println(j48.graph());
		writer.close();
		
		
		
		return String.format("%f;%d",evaluation.correct()/testset.numInstances(),c.cromosoma.size());
		
		
	}
	
	public static void producipopolazioneinizialegait() throws Exception{
		//creo l'albero completo del dataset
		String testset_url="/home/darshan/Desktop/Università/Tesi/matlab_scripts/Datasets/testset_paper.arff";
		String dataset_url="/home/darshan/Desktop/Università/Tesi/matlab_scripts/Datasets/dataset_paper.arff";
		FileReader testset_stream= new FileReader(testset_url);
		FileReader dataset_stream= new FileReader(dataset_url);
		Instances dataset = new Instances(dataset_stream);
		Instances testset = new Instances(testset_stream);		
		dataset.setClassIndex(dataset.numAttributes() - 1);
		testset.setClassIndex(testset.numAttributes() - 1);
		esportaalbero(dataset, testset, "/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dist/export/wholetraining");
		System.out.println("Wholetraining");
		//partiziono il dataset in 50 parti, per ogni parte creo un albero
		String nomefile;
		String caratteristiche;
		PrintWriter writer = new PrintWriter("/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dist/export/partizioni.csv", "UTF-8");
		for(int i=0; i<50 ; i++){
			Instances data= new Instances(dataset, i*60, 60);
			dataset.setClassIndex(dataset.numAttributes() - 1);
			nomefile=String.format("/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dist/export/Albero%d", i+1);
			caratteristiche=esportaalbero(data, testset, nomefile);
			writer.println(String.format("%d;%s", i+1,caratteristiche));
			System.out.println(i+1);
		}
		writer.close();
		System.out.println("Done");
	}
	
	public static void testaalbero() throws Exception{
		//creo l'albero completo del dataset
		String testset_url="/home/darshan/Desktop/Università/Tesi/matlab_scripts/Datasets/testset_paper.arff";
		String dataset_url="/home/darshan/Desktop/Università/Tesi/matlab_scripts/Datasets/dataset_paper.arff";
		FileReader testset_stream= new FileReader(testset_url);
		FileReader dataset_stream= new FileReader(dataset_url);
		Instances dataset = new Instances(dataset_stream);
		Instances testset = new Instances(testset_stream);		
		dataset.setClassIndex(dataset.numAttributes() - 1);
		testset.setClassIndex(testset.numAttributes() - 1);
		
		String alberourl="/home/darshan/Uni/Tesi/tesi/Tesi/dist/export/Albero18.yaml";
		String Yaml_str = StringUtil.readFileAsString(alberourl);
		
		Cromosoma c= Cromosoma.loadFromYaml(Yaml_str);		
		
		TreeEvaluator te= new TreeEvaluator(c, testset, dataset.numClasses());
		te.evaluate();
		System.out.println(String.format("%f;%d",te.getPrestazioni(),c.cromosoma.size()));
		System.out.println(ArrayUtil.dump(te.getConfusion()));
		System.out.println(c.toYaml());
		
		Cromosoma c2=GeneticOperator.crossover(c, c, false);
		System.out.println(c2.toYaml());
		
		CromosomaDecorator cd= new CromosomaDecorator(c);
		cd.caricaColonne(dataset);
		
		System.out.println(cd.getGraph().toString());

		
		System.out.println("Done");
		
		
	}
}
