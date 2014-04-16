package tesi.launcher;

import java.util.LinkedList;
import java.util.logging.Logger;

import tesi.controllers.GAIT_abstract;
import tesi.controllers.GeneticOperators;
import tesi.controllers.TreeEvaluator;
import tesi.interfaces.CromosomaDecorator;
import tesi.models.Cromosoma;
import tesi.models.Dataset;
import tesi.util.SingletonGenerator;
import tesi.util.logging.GlobalLogger;
import weka.classifiers.trees.J48;
import weka.core.Instances;

/**
 * Permette di avviare una versione generalizzata della procedura descritta in
 * GAIT ( variante 1).
 * 
 * @author darshan
 */
public abstract class AlgoritmoEvolutivo {
	public boolean mutante = false;
	public static boolean eseguiwhole = false;
	int numerogenerazioni;
	int popolazione_iniziale_size;
	int datasetsize;
	//
	double percentualetrainingset;
	double percentualetestset;
	double percentualescoringset;
	double media=0;
	int campioni_per_albero;
	//
	Instances trainingset;
	Instances testset;
	Instances scoringset;
	//
	int nclassi;
	LinkedList<Cromosoma> popolazione_iniziale;
	protected GAIT_abstract ecosistema;
	Cromosoma esemplare;
	CromosomaDecorator cd;
	TreeEvaluator te;
	static final Logger logger;

	static {
		String path = GeneticOperators.class.getName();
		logger = Logger.getLogger(path);
		logger.setLevel(GlobalLogger.level);
		logger.fine(String.format("Logger inizializzato per: %s", path));

	}

	public abstract CromosomaDecorator begin() throws Exception;	
	
	public AlgoritmoEvolutivo(Dataset d, int numerogenerazioni, int popolazione_iniziale, boolean mutante) {
		init(d, numerogenerazioni, popolazione_iniziale, mutante, 0);
	}

	public AlgoritmoEvolutivo(Dataset d, int numerogenerazioni, int popolazione_iniziale, boolean mutante,
			int campioniperalbero) {
		init(d, numerogenerazioni, popolazione_iniziale, mutante, campioniperalbero);
	}

	
	protected void init(Dataset d, int numerogenerazioni, int popolazione_iniziale, boolean mutante,
			int campioniperalbero) {
		this.popolazione_iniziale = new LinkedList<Cromosoma>();
		this.numerogenerazioni = numerogenerazioni;
		this.popolazione_iniziale_size = popolazione_iniziale;
		this.percentualetrainingset = d.percentualetrainingset;
		this.percentualetestset = d.percentualetestset;
		this.percentualescoringset = d.percentualescoringset;
		this.datasetsize = d.datasetsize;
		this.nclassi = d.nclassi;
		this.trainingset = d.trainingset;
		this.scoringset = d.scoringset;
		this.testset = d.testset;
		this.mutante = mutante;

		campioni_per_albero = campioniperalbero;
		if (popolazione_iniziale_size < 0) {
			logger.info("Non è stata fornita la dimenzione della popolazione nell'ecosistema\n provo a dedurla");
			popolazione_iniziale_size = (int) (datasetsize * percentualetrainingset / campioni_per_albero);
			if (campioniperalbero < 1) {
				logger.info("Non sono stati forniti ne la dimenzione della popolazione ne quella degli alberi!");
			}
		}
		if (campioniperalbero < 1)
			campioni_per_albero = (int) (datasetsize * percentualetrainingset / popolazione_iniziale_size);
		if (campioni_per_albero > (datasetsize * percentualetrainingset / popolazione_iniziale_size))
			logger.warning("Gli alberi sono troppo grandi, preparati a un 'out of bound error'");
		if (campioni_per_albero < 5)
			logger.warning("Gli alberi sono troppo piccoli, le cose andranno male...");
	}

	@Deprecated
	protected void init(Instances dataset, int numerogenerazioni, int popolazione_iniziale, int nclassi,
			double percentualetrainingset, double percentualetestset, double percentualescoringset) {
		this.popolazione_iniziale = new LinkedList<Cromosoma>();
		this.numerogenerazioni = numerogenerazioni;
		this.popolazione_iniziale_size = popolazione_iniziale;
		this.percentualetrainingset = percentualetrainingset;
		this.percentualetestset = percentualetestset;
		this.percentualescoringset = percentualescoringset;
		this.datasetsize = dataset.numInstances();
		this.nclassi = nclassi;
		if (percentualetrainingset + percentualescoringset + percentualetestset < 0.9) {
			System.err.println("Parte del dataset non verrà utilizzato con le percentuali correnti.");
		}

		dataset.setClassIndex(dataset.numAttributes() - 1);

		dataset.randomize(SingletonGenerator.r);

		trainingset = new Instances(dataset, 0, (int) Math.round(datasetsize * this.percentualetrainingset));
		scoringset = new Instances(dataset, 1 + (int) Math.round(datasetsize * this.percentualetrainingset),
				(int) Math.round(datasetsize * this.percentualescoringset));
		testset = new Instances(dataset, 1 + (int) Math.round(datasetsize * this.percentualetrainingset
				+ percentualescoringset), (int) Math.round(datasetsize * this.percentualetestset));
		trainingset.setClassIndex(dataset.numAttributes() - 1);
		scoringset.setClassIndex(dataset.numAttributes() - 1);
		testset.setClassIndex(dataset.numAttributes() - 1);
		campioni_per_albero = (int) (datasetsize * percentualetrainingset / popolazione_iniziale);

	}

	/**
	 * Wrapper senza eccezioni a begin(),mi serve per implementare l'interfaccia
	 * runnable.
	 */
	public void run() {
		try {
			begin();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

	}
	
	public CromosomaDecorator startevolution(GAIT_abstract algoritmo) throws Exception {
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
			media = media + c.altezza;
			popolazione_iniziale.add(c);
		}
		media = 2 * media / popolazione_iniziale_size;

		
		J48 j48 = new J48();
		sb = new StringBuilder();
		sb.append(String.format("La popolazione iniziale è generata con J48, un porting in Java di C4.5"));
		sb.append(String.format(j48.getTechnicalInformation().toBibTex() + "\n"));
		logger.fine(sb.toString());
		ecosistema = algoritmo;
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
