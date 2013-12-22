package tesi.controllers;

import java.util.LinkedList;
import java.util.TreeSet;

import tesi.models.Cromosoma;
import tesi.models.CromosomaMisurato;
import weka.core.Instances;

/**
 * Classe astratta che viene estesa per implementare gli algoritmi evolutivi
 * @author darshan
 *
 */
public abstract class Ecosistema {	
	protected TreeSet<CromosomaMisurato> popolazione_valutata;
	protected LinkedList<Cromosoma> popolazione_nonvalutata;
	public double bestfitness = -1;
	public Cromosoma bestcromosoma;
	public TreeEvaluator te;
	protected Instances testset;
	protected int nclassi;

	public Ecosistema(Instances testset, int nclassi) {
		super();
		popolazione_nonvalutata = new LinkedList<>();
		popolazione_valutata = new TreeSet<>();
		this.testset=testset;
		this.nclassi=nclassi;
	}

	public int add(Cromosoma c) {
		popolazione_nonvalutata.add(c);
		return popolazione_nonvalutata.size();
	}

	/**
	 * Calcola il fitness di tutta la popolazione_nonvalutata e la carica in
	 * popolazione_valutata
	 * 
	 * @return
	 */
	public abstract double get_fitness();

	/**
	 * Esegue un crossover ( che produce nuovi elementi ) sulla
	 * popolazione_valutata, i figli vengono piazzati in popolazione_nonvalutata
	 * 
	 * @param probabilita
	 */
	public abstract void crossover(double probabilita);

	/**
	 * Estrae elementi dalla popolazione_valutata, li muta e li sposta in
	 * popolazione_nonvalutata
	 * 
	 * @param probabilita
	 */
	public abstract void mutate(double probabilita);

	/**
	 * Elimina gli esemplari peggiori della popolazione riducendola a size
	 * elementi
	 * 
	 * @param size
	 * @return
	 */
	public void trimtosize(int size) {
		int n = popolazione_valutata.size() - size;
		while (n > 0) {
			popolazione_valutata.pollFirst();
			n--;
		}
	}

	/**
	 * Avanza di una generazione
	 */
	public abstract double evolvi();

	protected double estrai_migliore() {
		double f = popolazione_valutata.last().prestazioni;
		if (f > this.bestfitness) {
			this.bestfitness = f;
			this.bestcromosoma = popolazione_valutata.last().cromosoma;
		}
		return f;
	}
		

}
