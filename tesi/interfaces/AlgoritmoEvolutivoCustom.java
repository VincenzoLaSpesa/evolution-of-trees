/**
 * 
 */
package tesi.interfaces;

import java.util.LinkedList;
import java.util.Random;

import tesi.controllers.GAIT_noFC_simple;
import tesi.controllers.TreeEvaluator;
import tesi.models.Cromosoma;
import weka.classifiers.trees.J48;
import weka.core.Instances;

/**
 * Permette di avviare una versione generalizzata della procedura descritta in GAIT ( variante 1).
 * @author darshan
 * TODO: Testare questa classe!
 */
public class AlgoritmoEvolutivoCustom implements Runnable {
	int numerogenerazioni;
	int popolazione_iniziale_size;
	int datasetsize;
	//HashMap<String, String> risultati_classificazione;
	double percentualetrainingset;
	double percentualetestset;
	double percentualescoringset;
	int campioni_per_albero;
	//
	Instances trainingset;
	Instances testset;
	Instances scoringset;

	int nclassi;
	LinkedList<Cromosoma> popolazione_iniziale;
	GAIT_noFC_simple gait;
	Cromosoma esemplare;
	CromosomaDecorator cd;
	TreeEvaluator te;
	
	

	public AlgoritmoEvolutivoCustom(Instances dataset, int numerogenerazioni, int popolazione_iniziale, int nclassi,
			float percentualetrainingset, float percentualetestset, float percentualescoringset) {
		super();
		init(dataset, numerogenerazioni,nclassi, popolazione_iniziale, percentualetrainingset, percentualetestset,
				percentualescoringset);
	}

	public AlgoritmoEvolutivoCustom(Instances dataset, int numerogenerazioni, int popolazione_iniziale, int nclassi) {
		super();
		init(dataset, numerogenerazioni, popolazione_iniziale,nclassi, 0.42, 0.30, 0.28);
	}

	protected void init(Instances dataset, int numerogenerazioni, int popolazione_iniziale, int nclassi,
			double percentualetrainingset, double percentualetestset, double percentualescoringset) {
		this.numerogenerazioni = numerogenerazioni;
		this.popolazione_iniziale_size = popolazione_iniziale;
		this.percentualetrainingset = percentualetrainingset;
		this.percentualetestset = percentualetestset;
		this.percentualescoringset = percentualescoringset;
		this.datasetsize=dataset.numInstances();
		this.nclassi=nclassi;
		if (percentualetrainingset + percentualescoringset + percentualetestset < 0.9) {
			System.err.println("Parte del dataset non verrà utilizzato con le percentuali correnti.");
			System.err.println(Thread.currentThread().getStackTrace()[0].toString());
		}
		campioni_per_albero=(int) (datasetsize*percentualetrainingset/popolazione_iniziale);
		
		dataset.setClassIndex(dataset.numAttributes() - 1);

		dataset.randomize(new Random());
		
		trainingset=new Instances(dataset, 0, (int) (datasetsize*this.percentualetrainingset));
		scoringset=new Instances(dataset, 1+(int) (datasetsize*this.percentualetrainingset), (int) (datasetsize*this.percentualescoringset));
		testset=new Instances(dataset, 1+(int) (datasetsize*this.percentualetrainingset+percentualescoringset), (int) (datasetsize*this.percentualetestset));
		trainingset.setClassIndex(dataset.numAttributes() - 1);
		scoringset.setClassIndex(dataset.numAttributes() - 1);
		testset.setClassIndex(dataset.numAttributes() - 1);

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
		gait= new GAIT_noFC_simple(scoringset, nclassi, this.popolazione_iniziale_size);
		esemplare=gait.GAIT(popolazione_iniziale);
		te= new TreeEvaluator(esemplare, testset, nclassi);
		te.evaluate();
		cd=new CromosomaDecorator(esemplare);
		cd.caricaColonne(trainingset);
		System.out.println("L'esemplare migliore dopo n generazioni è il seguente:");
		System.out.println(esemplare.toYaml());
		System.out.println(cd.getGraph());
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
}
