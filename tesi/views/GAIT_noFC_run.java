package tesi.views;

import java.util.LinkedList;

import tesi.controllers.GAIT_noFC;
import tesi.controllers.TreeEvaluator;
import tesi.models.Cromosoma;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class GAIT_noFC_run {
	int nclassi;
	Instances dataset;
	Instances testset;
	LinkedList<Cromosoma> popolazione_iniziale;
	GAIT_noFC gait;
	Cromosoma esemplare;
	CromosomaDecorator cd;
	TreeEvaluator te;
	
	
	
	public GAIT_noFC_run(Instances dataset, Instances testset, int nclassi) {
		super();
		this.dataset = dataset;
		this.testset = testset;
		popolazione_iniziale= new LinkedList<>();
		this.nclassi=nclassi;
	}
	
	public void run() throws Exception{
		dataset.setClassIndex(dataset.numAttributes() - 1);
		testset.setClassIndex(testset.numAttributes() - 1);
		for(int i=0; i<50 ; i++){
			Instances data= new Instances(dataset, i*60, 60);
			data.setClassIndex(dataset.numAttributes() - 1);
			J48 j48 = new J48();
			j48.setBinarySplits(true);
			j48.buildClassifier(data);	
			Cromosoma c= Cromosoma.loadFromJ48(j48);
			popolazione_iniziale.add(c);
		}		
		gait= new GAIT_noFC(testset, nclassi);
		esemplare=gait.GAIT(popolazione_iniziale);
		te= new TreeEvaluator(esemplare, testset, nclassi);
		te.evaluate();
		cd=new CromosomaDecorator(esemplare);
		cd.caricaColonne(dataset);
		System.out.println("L'esemplare migliore dopo 10 generazioni è il seguente:");
		System.out.println(esemplare.toYaml());
		System.out.println(cd.getGraph());
		System.out.println(te.getPrestazioni());
		System.out.println(te.getConfusionasString());
		
		
	}

}
