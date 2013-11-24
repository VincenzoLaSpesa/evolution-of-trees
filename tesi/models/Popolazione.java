package tesi.models;

import java.util.LinkedList;
import java.util.TreeMap;

public abstract class Popolazione {

	protected TreeMap<Float, Cromosoma> popolazione_valutata;
	protected LinkedList<Cromosoma> popolazione_nonvalutata;
	
	
	public Popolazione() {
		super();
		popolazione_nonvalutata= new LinkedList<>();
		popolazione_valutata= new TreeMap<>();
	}

	public int add(Cromosoma c){
		popolazione_nonvalutata.add(c);
		return popolazione_nonvalutata.size();
	}
	
	/**
	 * Calcola il fitness di tutta la popolazione_nonvalutata e la carica in popolazione_valutata
	 * @return
	 */
	public abstract float get_fitness();
	
	/**
	 * Esegue un crossover ( che produce nuovi elementi ) sulla popolazione_valutata, i figli vengono piazzati in popolazione_nonvalutata
	 * @param probabilita
	 */
	public abstract void crossover(float probabilita);
	
	/**
	 * Estrae elementi dalla popolazione_valutata, li muta e li sposta in popolazione_nonvalutata
	 * @param probabilita
	 */
	public abstract void mutate(float probabilita);
	
	/**
	 * Elimina gli esemplari peggiori della popolazione riducendola a size elementi
	 * @param size
	 * @return
	 */
	public void trimtosize(int size){				
		int n=popolazione_valutata.size()-size;
		while(n>0){
			popolazione_valutata.pollFirstEntry();
			n--;
		}
	}

	/**
	 * Avanza di una generazione
	 */
	public abstract void evolvi();
}
