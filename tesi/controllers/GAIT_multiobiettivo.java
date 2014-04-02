package tesi.controllers;

import tesi.models.Cromosoma;
import tesi.models.popolazione.PopolazioneOrdinata;
import weka.core.Instances;

/**
 * Implementa Gait senza controllo semantico con una funzione di fitness multiobiettivo 
 * ( ci possono essere nell'albero due vincoli che non possono essere 
 * soddisfatti contemporaneamente o vincoli che non aggiungono informazione 
 * perchè soddisfatti sicuramente)
 * @author darshan
 * 
 */
public class GAIT_multiobiettivo extends GAIT_abstract {

	/**
	 * 
	 * @param testset
	 * 		Le istanze del testset
	 * @param nclassi
	 * 		Il numero delle classi da cercare
	 * @param limit
	 * 		Il limite massimo della popolazione
	 */
	
	public static double alpha=5;
	public static double beta=1;
	public static double gamma=15;
	

	public GAIT_multiobiettivo(Instances testset, int nclassi, int limit) {
		super(testset, nclassi, limit);
		popolazione= new PopolazioneOrdinata();
	}
	
	

	public GAIT_multiobiettivo(Instances testset, int nclassi, int limit, double alpha, double beta, double gamma) {
		super(testset, nclassi, limit);
		GAIT_multiobiettivo.alpha = alpha;
		GAIT_multiobiettivo.beta = beta;
		GAIT_multiobiettivo.gamma = gamma;
	}

	public void reimpostaparametri(double alpha, double beta, double gamma) {
		GAIT_multiobiettivo.alpha = alpha;
		GAIT_multiobiettivo.beta = beta;
		GAIT_multiobiettivo.gamma = gamma;
	}


	@Override
	public double valuta_figli() {
		return multiobjective_fitness();
	}
	
	/**
	 * Definisce la funzione di fitness, in questo caso utilizza quella definita in calcola_fitness_multiobiettivo_additiva(double prestazioni, Cromosoma c, double alpha, double beta){

	 */
	@Override
	public double calcola_fitness_multiobiettivo(double prestazioni, Cromosoma c) {
		//return GeneticOperators.calcola_fitness_multiobiettivo_nonlineare_semplice(prestazioni, c, alpha, beta , gamma);
		return GeneticOperators.calcola_fitness_multiobiettivo_nonlineare(prestazioni, c, alpha, beta , gamma);
		//return calcola_fitness_multiobiettivo_additiva(prestazioni, c,5,2,5);
		//return calcola_fitness_multiobiettivo_additiva(prestazioni, c,5,1,15,0.5);
		//return calcola_fitness_multiobiettivo_additiva(prestazioni, c,5,2,5,0.5);
		//return calcola_fitness_multiobiettivo_nonlineare(prestazioni, c,5,1,15,1);// <-- migliore
		//return calcola_fitness_multiobiettivo_lineare(prestazioni, c,50,2);
	}

	@Override
	/**
	 * definisce il crossover come crossover_etilist();
	 * @see tesi.controllers.GAIT_noFC_abstract.crossover_etilist();
	 */
	public void crossover() {
		crossover_etilist();
	}
}
