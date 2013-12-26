package tesi.models;

import java.io.FileReader;
import java.io.IOException;

import tesi.util.SingletonGenerator;
import weka.core.Instances;

public class Dataset {
	public final Instances trainingset;
	public final Instances testset;
	public final Instances scoringset;
	public final int datasetsize;
	public final double percentualetrainingset;
	public final double percentualetestset;
	public final double percentualescoringset;
	public final int nclassi;
	
	public Dataset(Instances dataset,double percentualetrainingset, double percentualetestset, double percentualescoringset){

		if (percentualetrainingset + percentualescoringset + percentualetestset < 0.9) {
			System.err.println("Parte del dataset non verrà utilizzato con le percentuali correnti.");
			System.err.println(Thread.currentThread().getStackTrace()[0].toString());
		}
		
		this.percentualetrainingset = percentualetrainingset;
		this.percentualetestset = percentualetestset;
		this.percentualescoringset = percentualescoringset;
		
		dataset.setClassIndex(dataset.numAttributes() - 1);
		dataset.randomize(SingletonGenerator.r);
		datasetsize=dataset.numInstances();
		nclassi=dataset.numClasses();
		
		trainingset=new Instances(dataset, 0, (int)Math.round(datasetsize*this.percentualetrainingset));
		scoringset=new Instances(dataset, 1+(int)Math.round(datasetsize*this.percentualetrainingset), (int)Math.round(datasetsize*this.percentualescoringset));
		testset=new Instances(dataset, 1+(int)Math.round(datasetsize*this.percentualetrainingset+percentualescoringset), (int)Math.round(datasetsize*this.percentualetestset));
		trainingset.setClassIndex(dataset.numAttributes() - 1);
		scoringset.setClassIndex(dataset.numAttributes() - 1);
		testset.setClassIndex(dataset.numAttributes() - 1);
		
	}

	public static Dataset createFromFile(String dataset_url,double percentualetrainingset, double percentualetestset, double percentualescoringset) throws IOException{
		FileReader dataset_stream= new FileReader(dataset_url);
		Instances dataset = new Instances(dataset_stream);
		return new Dataset(dataset, percentualetrainingset, percentualetestset, percentualescoringset); 		
	}
	
}
