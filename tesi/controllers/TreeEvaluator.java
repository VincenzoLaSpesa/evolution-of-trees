package tesi.controllers;

import java.util.Enumeration;

import tesi.models.Cromosoma;
import tesi.models.Gene;
import tesi.models.Taglio;
import tesi.util.ArrayUtil;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Costruisce valutazioni degli alberi su interi testset e ne ritorna la matrice
 * di confusione e la prestazione, dispone anche di funzioni statiche per
 * valutare singoli cromosomi su singole entry del testset
 * 
 * @author darshan
 * 
 */
public class TreeEvaluator {

	Cromosoma cromosoma;
	Instances testset;
	int nclassi;

	int[][] confusion;
	double[][] confusion_f;
	int[] lut;//una lookuptable, nel caso gli id delle classi non comincino da 0

	double prestazioni = 0;

	
	/**
	 * Valuta il Cromosoma sull'istanza, supponendo che tutte le caratteristiche
	 * dell'istanza siano numeri reali e che l'ultima contenga la classe di
	 * appartenenza.
	 * 
	 * La valutazione di un albero serializzato può essere effettuata
	 * semplicemente in modo iterativo visto che nell'esplorazione di un albero
	 * di scelta non si torna mai indietro di livello
	 * 
	 * @param c
	 * @param istanza
	 * @return
	 */
	public static int evaluate_one(Cromosoma c, double[] istanza) {
		// System.out.println("    "+Gene.csvHead);
		int cursore = 0;
		boolean flag;
		Gene g = c.cromosoma.elementAt(cursore);
		// System.out.println(String.format("%d -> %s - (%f)", cursore ,
		// g.toCsv() , istanza[g.attributo]));
		while (g.fine > 0) {
			if (g.taglio == Taglio.Continuo) {
				flag = (istanza[g.attributo] <= g.punto);
			} else {
				flag = (istanza[g.attributo] == g.punto);
			}
			if (flag) {
				cursore++;
			} else {
				cursore = g.fine;
			}
			g = c.cromosoma.elementAt(cursore);
			// System.out.println(String.format("%d -> %s - (%f)", cursore ,
			// g.toCsv() , istanza[g.attributo]));

		}
		return g.attributo;
	}

	/**
	 * Valuta l'albero con
	 * tesi.controllers.TreeEvaluator.evaluate_one(Cromosoma, double[]),
	 * paragona la valutazione col valore effettivo, ritorna la correttezza
	 * della valutazione come valore booleano
	 * 
	 * @param c
	 * @param istanza
	 * @return
	 */
	public static boolean check_one(Cromosoma c, double[] istanza) {
		return evaluate_one(c, istanza) == istanza[istanza.length - 1];
	}

	public TreeEvaluator(Cromosoma cromosoma, Instances testset, int nclassi) {
		this.cromosoma = cromosoma;
		this.testset = testset;
		this.nclassi = nclassi;
		this.confusion = new int[nclassi][nclassi];
		this.confusion_f= new double[nclassi][nclassi];
		this.lut= new int[64];
		//
		int i=testset.classIndex();
		int j=0;
		int n;
		Enumeration<String> e=testset.attribute(i).enumerateValues();
		while(e.hasMoreElements()){
			n=Integer.parseInt(e.nextElement());
			lut[n]=j;
			j++;
		}
	}

	public double evaluate() {
		int nIstanze = testset.numInstances();
		int responso;
		int classe;
		double[] istanza;
		double[] sommarighe=new double[nclassi];
		Instance in;
		for (int i = 0; i < nIstanze; i++) {
			in=testset.instance(i);
			istanza = in.toDoubleArray();
			responso = TreeEvaluator.evaluate_one(cromosoma, istanza);			
			classe=(int)testset.instance(i).classValue();
			responso=lut[responso];
			confusion[classe][responso]++;
		}
		
		
		for (int i = 0; i < nclassi; i++) {
			prestazioni = prestazioni + confusion[i][i];
		}
		
		for (int r = 0; r < nclassi; r++) {
			for (int c = 0; c < nclassi; c++) {
				sommarighe[r]+=confusion[r][c];
			}
			for (int c = 0; c < nclassi; c++) {
				confusion_f[r][c]= confusion[r][c]/sommarighe[r];
			}
		}
		
		prestazioni = prestazioni  / nIstanze;
		return prestazioni;
	}

	public int[][] getConfusion() {
		return confusion;
	}

	public double[][] getConfusion_f() {
		return confusion_f;
	}

	public String getConfusionasString() {
		return ArrayUtil.dump(confusion);
	}
	
	public String getConfusionasFloatString() {
		return ArrayUtil.dump(confusion_f);
	}
	

	public double getPrestazioni() {
		return prestazioni;
	}

}
