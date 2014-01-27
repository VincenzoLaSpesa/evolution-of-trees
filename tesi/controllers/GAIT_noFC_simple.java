/**
 * 
 */
package tesi.controllers;

import weka.core.Instances;

/**
 * Implementa Gait senza controllo semantico <b> con funzione di fitness semplice <b> 
 * ( ci possono essere nell'albero due vincoli che non possono essere 
 * soddisfatti contemporaneamente o vincoli che non aggiungono informazione 
 * perchè soddisfatti sicuramente)
 * @author darshan
 * 
 */
public class GAIT_noFC_simple extends GAIT_noFC_abstract {

	public GAIT_noFC_simple(Instances testset, int nclassi, int limit) {
		super(testset, nclassi,limit);
	}

	/**
	 * Definisce get_fitness() come simple_fitness();
	 * @see tesi.controllers.GAIT_noFC_abstract.simple_fitness()
	 */
	@Override
	public double get_fitness() {
		return simple_fitness();
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
