package tesi.controllers;

import tesi.models.Cromosoma;
import weka.core.Instances;

/**
 * Implementa Gait senza controllo semantico con una funzione di fitness multiobiettivo 
 * ( ci possono essere nell'albero due vincoli che non possono essere 
 * soddisfatti contemporaneamente o vincoli che non aggiungono informazione 
 * perchè soddisfatti sicuramente)
 * @author darshan
 * 
 */
public class GAIT_noFC_multiobiettivo extends GAIT_noFC_abstract {

	/**
	 * 
	 * @param testset
	 * 		Le istanze del testset
	 * @param nclassi
	 * 		Il numero delle classi da cercare
	 * @param limit
	 * 		Il limite massimo della popolazione
	 */
	public GAIT_noFC_multiobiettivo(Instances testset, int nclassi, int limit) {
		super(testset, nclassi, limit);
	}

	@Override
	public double get_fitness() {
		return multiobjective_fitness();
	}
	
	/**
	 * Definisce la funzione di fitness, in questo caso utilizza quella definita in calcola_fitness_multiobiettivo_additiva(double prestazioni, Cromosoma c, double alpha, double beta){
	 */
	@Override
	public double calcola_fitness_multiobiettivo(double prestazioni, Cromosoma c) {
		return calcola_fitness_multiobiettivo_additiva(prestazioni, c,5,2,5);
	}
}
