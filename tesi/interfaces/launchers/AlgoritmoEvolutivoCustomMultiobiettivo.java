/**
 * 
 */
package tesi.interfaces.launchers;

import tesi.controllers.GAIT_noFC_multiobiettivo;
import tesi.controllers.TreeEvaluator;
import tesi.interfaces.CromosomaDecorator;
import tesi.models.Cromosoma;
import tesi.models.Dataset;
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

	public AlgoritmoEvolutivoCustomMultiobiettivo(Dataset d, int numerogenerazioni, int popolazione_iniziale){
		super(d, numerogenerazioni, popolazione_iniziale);
	}
	public AlgoritmoEvolutivoCustomMultiobiettivo(Instances dataset, int numerogenerazioni, int popolazione_iniziale,
			int nclassi, double percentualetrainingset, double percentualetestset, double percentualescoringset) {
		super(dataset, numerogenerazioni, popolazione_iniziale, nclassi, percentualetrainingset, percentualetestset,
				percentualescoringset);
	}

	public void begin() throws Exception{
		double prestazioni1;
		double prestazioni2;
		double peso;

		StringBuilder sb= new StringBuilder();
		sb.append(String.format("Il training set è composto da \t%d elementi\n",trainingset.numInstances()));
		sb.append(String.format("Il test set è composto da \t%d elementi\n",testset.numInstances()));
		sb.append(String.format("Lo scoring set è composto da \t%d elementi\n",scoringset.numInstances()));
		sb.append(String.format("Evolvo per %d generazioni con una popolazione fissa di %d elementi\n\n",this.numerogenerazioni, this.popolazione_iniziale_size));
		logger.info(sb.toString());
		
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
		sb= new StringBuilder();
		sb.append(String.format("La popolazione iniziale è generata con J48, un porting in Java di C4.5"));
		sb.append(String.format(j48.getTechnicalInformation().toBibTex()+"\n"));
		logger.info(sb.toString());
		gait= new GAIT_noFC_multiobiettivo(scoringset, nclassi, this.popolazione_iniziale_size);
		esemplare=gait.GAIT(popolazione_iniziale, numerogenerazioni);
		te= new TreeEvaluator(esemplare, testset, nclassi);
		te.evaluate();
		cd=new CromosomaDecorator(esemplare);
		cd.caricaColonne(trainingset);
		prestazioni1=te.getPrestazioni();
		peso=esemplare.getComplessita();
		
		sb= new StringBuilder();
		sb.append("L'esemplare migliore dopo n generazioni è il seguente:\n");		
		sb.append(cd.getGraph());
		sb.append("\n");
		sb.append(esemplare.toYaml());
		sb.append(String.format("\ne ha peso: %.1f \n", peso));
		sb.append("le prestazioni dell'esemplare migliore calcolate sul testset sono:");
		sb.append(String.format("p=\t%f\n",prestazioni1));
		sb.append(te.getConfusionasFloatString());
		sb.append("\n");
		logger.info(sb.toString());
		//
		j48.setBinarySplits(true);
		j48.buildClassifier(trainingset);	
		Cromosoma whole= Cromosoma.loadFromJ48(j48);
		te= new TreeEvaluator(whole, testset, nclassi);
		te.evaluate();
		sb= new StringBuilder();
		sb.append("Le prestazioni dell'albero generato sull'intero trainingset e calcolate sul testset (wholetraining) sono:");
		prestazioni2=te.getPrestazioni();
		sb.append(String.format("%f\n",prestazioni2));
		sb.append("\n");
		sb.append(te.getConfusionasFloatString());
		logger.info(sb.toString());		
		System.out.printf("§§\t%f\t%f\t%.1f\n",prestazioni1,prestazioni2, peso);
	}
	
	
}