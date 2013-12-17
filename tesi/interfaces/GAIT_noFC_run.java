package tesi.interfaces;

import java.util.LinkedList;

import tesi.controllers.GAIT_noFC_simple;
import tesi.controllers.TreeEvaluator;
import tesi.models.Cromosoma;
import weka.classifiers.trees.J48;
import weka.core.Instances;

/**
 * Permette di avviare la procedura descritta in GAIT ( variante 1),
 * <i> la classe è deprecata perchè può essere implementata attraverso AlgoritmoEvolutivoCustom </i>
 * @author darshan
 *
 */
@Deprecated
public class GAIT_noFC_run implements Runnable{
	int nclassi;
	static int maxelementi=50;
	Instances trainingset;
	Instances testset;
	Instances scoringset;
	LinkedList<Cromosoma> popolazione_iniziale;
	GAIT_noFC_simple gait;
	Cromosoma esemplare;
	CromosomaDecorator cd;
	TreeEvaluator te;
	
	
	
	/**
	 * Il trainingset viene diviso in 50 partizioni da 60 elementi <br>
	 * Le partizioni vengono utilizzate insieme allo scoringset per generare 50 alberi con J48 <br>
	 * I 50 alberi vengono evoluti per 10 generazioni calcolandone il fitness sullo scoringset<br>
	 * L'albero migliore dopo 10 generazioni viene valutato usando il testset.
	 * 
	 * <strong>Il costruttore inizializza solo i dati, la procedura viene avviata dal metodo run()</strong>
	 * @param trainingset
	 * @param testset
	 * @param scoringset
	 * @param nclassi
	 */
	public GAIT_noFC_run(Instances trainingset, Instances testset,Instances scoringset, int nclassi) {
		super();
		this.trainingset = trainingset;
		this.testset = testset;
		this.scoringset=scoringset;
		popolazione_iniziale= new LinkedList<>();
		this.nclassi=nclassi;
	}
	
	/**
	 * Il trainingset viene diviso in 50 partizioni da 60 elementi <br>
	 * Le partizioni vengono utilizzate insieme allo scoringset per generare 50 alberi con J48 <br>
	 * I 50 alberi vengono evoluti per 10 generazioni calcolandone il fitness sullo scoringset<br>
	 * L'albero migliore dopo 10 generazioni viene valutato usando il testset.
	 * <br><strong>
	 * La funzione si aspetta un dataset sufficentemente grande per poter creare gli alberi, 
	 * questo significa almeno 50*60=3000 record</strong>
	 * @throws Exception
	 */
	public void begin() throws Exception{
		System.out.printf("Il training set è composto da \t%d elementi\n",trainingset.numInstances());
		System.out.printf("Il test set è composto da \t%d elementi\n",testset.numInstances());
		System.out.printf("Lo scoring set è composto da \t%d elementi\n",scoringset.numInstances());
		System.out.printf("Evolvo per 10 generazioni con una popolazione fissa di %d elementi\n\n",maxelementi);
		trainingset.setClassIndex(trainingset.numAttributes() - 1);
		testset.setClassIndex(testset.numAttributes() - 1);
		for(int i=0; i<50 ; i++){
			//				new Instances(dataset,primoelemento,numeroelementi)
			Instances data= new Instances(trainingset, i*60, 60);
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
		gait= new GAIT_noFC_simple(scoringset, nclassi, maxelementi);
		esemplare=gait.GAIT(popolazione_iniziale);
		te= new TreeEvaluator(esemplare, testset, nclassi);
		te.evaluate();
		cd=new CromosomaDecorator(esemplare);
		cd.caricaColonne(trainingset);
		System.out.println("L'esemplare migliore dopo 10 generazioni è il seguente:");
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
