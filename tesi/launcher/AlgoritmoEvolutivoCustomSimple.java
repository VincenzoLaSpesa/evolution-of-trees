/**
 * 
 */
package tesi.launcher;

import tesi.controllers.GAIT_simple;
import tesi.controllers.TreeEvaluator;
import tesi.interfaces.CromosomaDecorator;
import tesi.models.Cromosoma;
import tesi.models.Dataset;
import weka.classifiers.trees.J48;
import weka.core.Instances;


public class AlgoritmoEvolutivoCustomSimple extends AlgoritmoEvolutivo implements Runnable {

	public AlgoritmoEvolutivoCustomSimple(Dataset d, int numerogenerazioni, int popolazione_iniziale, boolean mutante) {
		super(d, numerogenerazioni, popolazione_iniziale, mutante);
	}

	public AlgoritmoEvolutivoCustomSimple(Dataset d, int numerogenerazioni, int popolazione_iniziale, boolean mutante,
			int campioniperalbero) {
		super(d, numerogenerazioni, popolazione_iniziale, mutante, campioniperalbero);
	}

	/**
	 * Avvia la classificazione
	 * 
	 * @throws Exception
	 */
	public CromosomaDecorator begin() throws Exception {
		double prestazioni_gait;
		double prestazioni_j48 = -1;
		double peso_gait;
		double peso_J48w = -1;

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Il training set è composto da \t%d elementi\n", trainingset.numInstances()));
		sb.append(String.format("Il test set è composto da \t%d elementi\n", testset.numInstances()));
		sb.append(String.format("Lo scoring set è composto da \t%d elementi\n", scoringset.numInstances()));
		sb.append(String.format("Evolvo per %d generazioni con una popolazione fissa di %d elementi\n\n",
				this.numerogenerazioni, this.popolazione_iniziale_size));
		logger.info(sb.toString());
		trainingset.setClassIndex(trainingset.numAttributes() - 1);
		testset.setClassIndex(testset.numAttributes() - 1);
		for (int i = 0; i < popolazione_iniziale_size; i++) {
			Instances data = new Instances(trainingset, i * campioni_per_albero, campioni_per_albero);
			data.setClassIndex(trainingset.numAttributes() - 1);
			J48 j48 = new J48();
			j48.setBinarySplits(true);
			j48.buildClassifier(data);
			Cromosoma c = Cromosoma.loadFromJ48(j48);
			popolazione_iniziale.add(c);
		}
		J48 j48 = new J48();
		sb = new StringBuilder();
		sb.append(String.format("La popolazione iniziale è generata con J48, un porting in Java di C4.5"));
		sb.append(String.format(j48.getTechnicalInformation().toBibTex() + "\n"));
		logger.fine(sb.toString());
		ecosistema = new GAIT_simple(scoringset, nclassi, this.popolazione_iniziale_size);
		esemplare = ecosistema.GAIT(popolazione_iniziale, numerogenerazioni, mutante);
		te = new TreeEvaluator(esemplare, testset, nclassi);
		te.evaluate();
		prestazioni_gait = te.getPrestazioni();
		peso_gait = esemplare.getComplessita();
		cd = new CromosomaDecorator(esemplare);
		cd.caricaColonne(trainingset);
		sb = new StringBuilder();
		sb.append("L'esemplare migliore dopo n generazioni è il seguente:\n");
		sb.append(cd.getGraph());
		sb.append("\n");
		sb.append(esemplare.toYaml());
		sb.append(String.format("\ne ha peso: %.1f \n", peso_gait));
		sb.append("le prestazioni dell'esemplare migliore calcolate sul testset sono:");
		sb.append(String.format("p=\t%f\n", prestazioni_gait));
		sb.append(te.getConfusionasFloatString());
		sb.append("\n");
		logger.info(sb.toString());
		//
		if (eseguiwhole) {
			j48.setBinarySplits(true);
			j48.buildClassifier(trainingset);
			Cromosoma whole = Cromosoma.loadFromJ48(j48);
			te = new TreeEvaluator(whole, testset, nclassi);
			te.evaluate();
			sb = new StringBuilder();
			sb.append("Le prestazioni dell'albero generato sull'intero trainingset e calcolate sul testset (wholetraining) sono:");
			prestazioni_j48 = te.getPrestazioni();
			peso_J48w = whole.getComplessita();
			sb.append(String.format("%f\n", prestazioni_j48));
			sb.append("\n");
			sb.append(te.getConfusionasFloatString());
			logger.info(sb.toString());
		}
		System.out.printf("§§\t%f\t%f\t%.1f\t%.1f\n", prestazioni_gait, prestazioni_j48, peso_gait, peso_J48w);
		return cd;
	}
}
