/**
 * 
 */
package tesi.interfaces.launchers;

import tesi.controllers.GAIT_noFC_multiobiettivo;
import tesi.controllers.TreeEvaluator;
import tesi.interfaces.CromosomaDecorator;
import tesi.models.Cromosoma;
import weka.classifiers.trees.J48;
import weka.core.Instances;

/**
 * Permette di avviare una variante generalizzata della procedura descritta in GAIT ( variante 1) adattata per usare una funzione di fitness multiobiettivo.
 * @author darshan
 * TODO: Testare questa classe!
 */
public class AlgoritmoEvolutivoCustomMultiobiettivo extends AlgoritmoEvolutivoCustom {

	public AlgoritmoEvolutivoCustomMultiobiettivo(Instances dataset, int numerogenerazioni, int popolazione_iniziale,
			int nclassi) {
		super(dataset, numerogenerazioni, popolazione_iniziale, nclassi);
	}

	public AlgoritmoEvolutivoCustomMultiobiettivo(Instances dataset, int numerogenerazioni, int popolazione_iniziale,
			int nclassi, double percentualetrainingset, double percentualetestset, double percentualescoringset) {
		super(dataset, numerogenerazioni, popolazione_iniziale, nclassi, percentualetrainingset, percentualetestset,
				percentualescoringset);
	}

	public void begin() throws Exception{
		System.out.printf("Il training set è composto da \t%d elementi\n",trainingset.numInstances());
		System.out.printf("Il test set è composto da \t%d elementi\n",testset.numInstances());
		System.out.printf("Lo scoring set è composto da \t%d elementi\n",scoringset.numInstances());
		System.out.printf("Evolvo per %d generazioni con una popolazione fissa di %d elementi\n\n",this.numerogenerazioni, this.popolazione_iniziale_size);
		trainingset.setClassIndex(trainingset.numAttributes() - 1);
		testset.setClassIndex(testset.numAttributes() - 1);
		for(int i=0; i<popolazione_iniziale_size ; i++){
			Instances data= new Instances(trainingset, i*campioni_per_albero, campioni_per_albero);
			data.setClassIndex(trainingset.numAttributes() - 1);
			J48 j48 = new J48();
			j48.setBinarySplits(true);
			j48.buildClassifier(data);	
			Cromosoma c= Cromosoma.loadFromJ48(j48);
			popolazione_iniziale.add(c);
		}
		J48 j48 = new J48();
		System.out.println("La popolazione iniziale è generata con J48, un porting in Java di C4.5");
		System.out.println(j48.getTechnicalInformation().toBibTex()+"\n");
		gait= new GAIT_noFC_multiobiettivo(scoringset, nclassi, this.popolazione_iniziale_size);
		esemplare=gait.GAIT(popolazione_iniziale, numerogenerazioni);
		te= new TreeEvaluator(esemplare, testset, nclassi);
		te.evaluate();
		cd=new CromosomaDecorator(esemplare);
		cd.caricaColonne(trainingset);
		System.out.println("L'esemplare migliore dopo n generazioni è il seguente:");
		System.out.println(cd.getGraph());
		System.out.println(esemplare.toYaml());
		System.out.printf("e ha peso: %.1f \n", esemplare.getComplessita());
		
		//System.out.println(cd.getGraph_numerico());
		System.out.println("le prestazioni dell'esemplare migliore calcolate sul testset sono:");
		System.out.printf("p=\t%f\n",te.getPrestazioni());
		System.out.println(te.getConfusionasFloatString());
		//
		j48.setBinarySplits(true);
		j48.buildClassifier(trainingset);	
		Cromosoma whole= Cromosoma.loadFromJ48(j48);
		te= new TreeEvaluator(whole, testset, nclassi);
		te.evaluate();
		System.out.println("Le prestazioni dell'albero generato sull'intero trainingset e calcolate sul testset (wholetraining) sono:");
		System.out.printf("%f\n",te.getPrestazioni());
		System.out.println(te.getConfusionasFloatString());
		System.out.println("(Le prestazioni sono prestazioni, non coincidono con i valori di fitness)");	
	}
	
	
}
