package tesi.main;

import java.io.FileReader;

import tesi.strutture.Cromosoma;
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
		String testset_url="/home/darshan/Desktop/Università/Tesi/matlab_scripts/Datasets/testset_paper.arff";
		String dataset_url="/home/darshan/Desktop/Università/Tesi/matlab_scripts/Datasets/dataset_paper.arff";
		FileReader testset_stream= new FileReader(testset_url);
		FileReader dataset_stream= new FileReader(dataset_url);
		Instances dataset = new Instances(dataset_stream);
		Instances testset = new Instances(testset_stream);
		dataset.setClassIndex(dataset.numAttributes() - 1);
		testset.setClassIndex(testset.numAttributes() - 1);
		J48 j48 = new J48();
		j48.setBinarySplits(true);
		j48.buildClassifier(dataset);
		//String grossografo=j48.toString();
		//System.out.println(grossografo);
		
		Evaluation evaluation = new Evaluation(dataset); //si inizializza col training set
		StringBuffer statistiche= new StringBuffer();
		Range r= new Range();
		evaluation.evaluateModel(j48, testset, statistiche,r,false);
		//ClassifierTree albero=  j48.getTree();
		//System.out.println(albero.graph());
		Cromosoma c= Cromosoma.loadFromJ48(j48);
		System.out.println(c.toString());
		System.out.println("done");
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

}
