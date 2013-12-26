/**
 * 
 */
package tesi.interfaces.launchers;

import java.util.LinkedList;
import java.util.logging.Logger;

import tesi.controllers.GAIT_noFC_abstract;
import tesi.controllers.GAIT_noFC_simple;
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
	protected GAIT_noFC_abstract gait;
	Cromosoma esemplare;
	CromosomaDecorator cd;
	TreeEvaluator te;
	
	static final Logger logger;
	
	static{
		String path=GeneticOperators.class.getName();
		logger= Logger.getLogger(path);
		logger.setLevel(GlobalLogger.level);
		//logger.addHandler(GlobalLogger.console);
		logger.fine(String.format("Logger inizializzato per: %s", path));
		
	}
	

	public AlgoritmoEvolutivoCustom(Instances dataset, int numerogenerazioni, int popolazione_iniziale, int nclassi,
			double percentualetrainingset, double percentualetestset, double percentualescoringset) {
		init(dataset, numerogenerazioni, popolazione_iniziale,nclassi, percentualetrainingset, percentualetestset,
				percentualescoringset);
	}

	public AlgoritmoEvolutivoCustom(Instances dataset, int numerogenerazioni, int popolazione_iniziale, int nclassi) {
		init(dataset, numerogenerazioni, popolazione_iniziale,nclassi, 0.5454545454,0.1818181818,0.2727272727);
	}

	public AlgoritmoEvolutivoCustom(Dataset d,int numerogenerazioni, int popolazione_iniziale) {
		init(d, numerogenerazioni, popolazione_iniziale);
	}

	
	protected void init(Dataset d, int numerogenerazioni, int popolazione_iniziale) {
		this.popolazione_iniziale= new LinkedList<Cromosoma>();
		this.numerogenerazioni = numerogenerazioni;
		this.popolazione_iniziale_size = popolazione_iniziale;
		this.percentualetrainingset = d.percentualetrainingset;
		this.percentualetestset = d.percentualetestset;
		this.percentualescoringset = d.percentualescoringset;
		this.datasetsize=d.datasetsize;
		this.nclassi=d.nclassi;
		this.trainingset=d.trainingset;
		this.scoringset=d.scoringset;
		this.testset=d.testset;
		campioni_per_albero=(int) (datasetsize*percentualetrainingset/popolazione_iniziale);
	}
	
	@Deprecated
	protected void init(Instances dataset, int numerogenerazioni, int popolazione_iniziale, int nclassi,
			double percentualetrainingset, double percentualetestset, double percentualescoringset) {
		this.popolazione_iniziale= new LinkedList<Cromosoma>();
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
		
		dataset.setClassIndex(dataset.numAttributes() - 1);

		dataset.randomize(SingletonGenerator.r);		
		
		trainingset=new Instances(dataset, 0, (int)Math.round(datasetsize*this.percentualetrainingset));
		scoringset=new Instances(dataset, 1+(int)Math.round(datasetsize*this.percentualetrainingset), (int)Math.round(datasetsize*this.percentualescoringset));
		testset=new Instances(dataset, 1+(int)Math.round(datasetsize*this.percentualetrainingset+percentualescoringset), (int)Math.round(datasetsize*this.percentualetestset));
		trainingset.setClassIndex(dataset.numAttributes() - 1);
		scoringset.setClassIndex(dataset.numAttributes() - 1);
		testset.setClassIndex(dataset.numAttributes() - 1);
		campioni_per_albero=(int) (datasetsize*percentualetrainingset/popolazione_iniziale);

	}

	public void begin() throws Exception{
		double prestazioni1;
		double prestazioni2;
		double peso_gait;
		double peso_J48w;

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
		gait= new GAIT_noFC_simple(scoringset, nclassi, this.popolazione_iniziale_size);
		esemplare=gait.GAIT(popolazione_iniziale, numerogenerazioni);
		te= new TreeEvaluator(esemplare, testset, nclassi);
		te.evaluate();
		prestazioni1=te.getPrestazioni();
		peso_gait=esemplare.getComplessita();
		cd=new CromosomaDecorator(esemplare);
		cd.caricaColonne(trainingset);
		sb= new StringBuilder();
		sb.append("L'esemplare migliore dopo n generazioni è il seguente:\n");		
		sb.append(cd.getGraph());
		sb.append("\n");
		sb.append(esemplare.toYaml());
		sb.append(String.format("\ne ha peso: %.1f \n", peso_gait));
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
		peso_J48w=whole.getComplessita();
		sb.append(String.format("%f\n",prestazioni2));
		sb.append("\n");
		sb.append(te.getConfusionasFloatString());
		logger.info(sb.toString());		
		System.out.printf("§§\t%f\t%f\t%.1f\t%.1f\n",prestazioni1,prestazioni2, peso_gait,peso_J48w);
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

	@Deprecated
	public void begin_compact() throws Exception {
		double prestazioni1;
		double prestazioni2;
		double peso;
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
		gait= new GAIT_noFC_simple(scoringset, nclassi, this.popolazione_iniziale_size);
		esemplare=gait.GAIT(popolazione_iniziale, numerogenerazioni);
		te= new TreeEvaluator(esemplare, testset, nclassi);
		te.evaluate();
		prestazioni1=te.getPrestazioni();
		peso=esemplare.getComplessita();
		j48.setBinarySplits(true);
		j48.buildClassifier(trainingset);	
		Cromosoma whole= Cromosoma.loadFromJ48(j48);
		te= new TreeEvaluator(whole, testset, nclassi);
		te.evaluate();
		prestazioni2=te.getPrestazioni();
		
		System.out.printf("§§\t%f\t%f\t%.1f\n",prestazioni1,prestazioni2, peso);
		
		
	}
}