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

}
