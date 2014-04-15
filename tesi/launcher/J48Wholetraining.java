package tesi.launcher;

import java.util.LinkedList;

import tesi.controllers.TreeEvaluator;
import tesi.models.Cromosoma;
import tesi.util.SingletonGenerator;
import weka.classifiers.trees.J48;
import weka.core.Instances;

/**
 * Permette di calcolare i wholetraining su interi dataset
 * @author darshan
 *
 */
public class J48Wholetraining implements Runnable{
	public LinkedList<Double> prestazioni;
	public LinkedList<Double> altezze;
	public LinkedList<Double> sbilanciamento;
	Instances trainingset;
	Instances testset;
	Instances dataset;
	int istanze;
	int nclassi;
	int trainingsetsize;
	int testsetsize;
	
	public J48Wholetraining(Instances dataset, int nclassi,int istanze, int testsetsize, int datasetsize){
		init(dataset,nclassi,istanze,testsetsize,datasetsize);
	}
	
	/**
	 * A partire dal dataset si produce il trainingset estraendo a caso 3000 elementi e il testset altri 1000
	 * @param trainingset
	 * @param numero
	 */
	public J48Wholetraining(Instances dataset, int nclassi,int istanze){
		init(dataset,nclassi,istanze,3000,1000);
	}

	public void init(Instances dataset, int nclassi,int istanze, int trainingsetsize, int testsetsize){
		this.dataset=dataset;
		this.istanze=istanze;
		this.nclassi=nclassi;
		altezze=new LinkedList<Double>();
		prestazioni=new LinkedList<Double>();
		sbilanciamento=new LinkedList<Double>();
		this.trainingsetsize=trainingsetsize;
		this.testsetsize=testsetsize;
		
	}

	
	
	/**
	 * Avvia la classificazione
	 * @throws Exception
	 */
	public void begin() throws Exception{
		TreeEvaluator te;
		
		for(int n=0;n<istanze;n++){
			dataset.randomize(SingletonGenerator.r);
			trainingset=new Instances(dataset, 0, trainingsetsize);
			testset=new Instances(dataset, 1+trainingsetsize,1+trainingsetsize+testsetsize);
			trainingset.setClassIndex(dataset.numAttributes() - 1);
			testset.setClassIndex(dataset.numAttributes() - 1);
			J48 j48 = new J48();
			j48.setBinarySplits(true);
			j48.buildClassifier(trainingset);
			Cromosoma whole= Cromosoma.loadFromJ48(j48);
			te= new TreeEvaluator(whole, testset, nclassi);
			te.evaluate();
			prestazioni.push(te.getPrestazioni());
			altezze.push(whole.getComplessita());
			sbilanciamento.push(whole.fattore_di_sbilanciamento);
		}		
		
	}
	
	/**
	 * Wrapper senza eccezioni a begin(),mi serve per implementare l'interfaccia runnable.
	 */
	@Override
	public void run() {
		try {
			begin();
		} catch (Exception e) {			
			System.err.println(e.getMessage());
			e.printStackTrace();			
		}
		
	}
	
	public String dump(){
		StringBuilder sb= new StringBuilder(prestazioni.size()*10);
		sb.append("prestazioni\taltezze\tsbilanciamento\n");
		while(!prestazioni.isEmpty()){
			sb.append(prestazioni.pop()).append("\t").append(altezze.pop()).append("\t").append(sbilanciamento.pop()).append("\n");
		}
		return sb.toString();
	}

}
