package tesi.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;

import tesi.models.Cromosoma;
import tesi.models.CromosomaMisurato;
import tesi.models.popolazione.Popolazione;
import tesi.models.popolazione.PopolazioneOrdinata;
import tesi.models.popolazione.PopolazioneRAM;
import tesi.util.SingletonGenerator;
import tesi.util.logging.GlobalLogger;
import weka.core.Instances;

/**
 * Classe astratta che viene estesa per implementare gli algoritmi evolutivi
 * 
 * @author darshan
 * 
 */
public abstract class Ecosistema {
	public double bestfitness = -1;
	public Cromosoma bestcromosoma;
	public TreeEvaluator te;
	protected Instances testset;
	protected int nclassi;
	protected final Logger logger;
	protected Popolazione popolazione;
	// protected TreeSet<CromosomaMisurato> padri_ordinati;
	// protected LinkedList<Cromosoma> figli;
	public static double mutation_rate = 0.01;
	public static double crossover_rate = 0.85;
	public static final double baserate = mutation_rate;


	public Ecosistema(Instances testset, int nclassi) {

		// figli = new LinkedList<>();
		// padri_ordinati = new TreeSet<>();
		this.testset = testset;
		this.nclassi = nclassi;
		String path = this.getClass().getName();
		logger = Logger.getLogger(path);
		logger.setLevel(GlobalLogger.level);
		logger.addHandler(GlobalLogger.console);
		logger.fine(String.format("Logger inizializzato per: %s", path));
	}

	public int add(Cromosoma c) {
		return popolazione.aggiungifiglio(c);
	}

	/**
	 * Calcola il fitness di tutta la popolazione_nonvalutata e la carica in
	 * popolazione_valutata
	 * 
	 * @return
	 */
	public abstract double valuta_figli();

	/**
	 * Esegue un crossover ( che produce nuovi elementi ) sulla
	 * popolazione_valutata, i figli vengono piazzati in popolazione_nonvalutata
	 */

	public abstract void crossover();

	/**
	 * Esegue un crossover ( che produce nuovi elementi ) sulla
	 * popolazione_valutata, i figli vengono piazzati in popolazione_nonvalutata
	 * 
	 * @param probabilita
	 */
	public abstract void crossover_etilist(double probabilita);

	/**
	 * Implementa una strategia di crossover a roulette che estrae dalla roulet
	 * popolazione*probabilità elementi Alla fine della procedura si otterranno
	 * quindi popolazione+popolazione*probabilita*0.5 elementi nella
	 * popolazione. gli elementi nuovi vengono caricati in
	 * popolazione_nonvalutata.
	 * 
	 * @param probabilita
	 */
	public void crossover_spf(double probabilita) {
		PopolazioneRAM P = (PopolazioneRAM) popolazione;
		Cromosoma c;
		Roulette roulette;
		LinkedList<Cromosoma> coppie = new LinkedList<>();
		// estraggo le coppie
		/*ArrayList<Double> frequenze = new ArrayList<Double>();
		Iterator<CromosomaMisurato> entries = P.iteratorepadri();
		while (entries.hasNext()) {
			CromosomaMisurato e = entries.next();
			frequenze.add(e.prestazioni);
		}*/
		double[] frequenze=P.estraiprestazioni();
		roulette = new Roulette(frequenze);
		int numerocoppie = (int) (roulette.vettore.length * probabilita);

		for (int n = 0; n < numerocoppie; n++) {
			c = P.get_element(roulette.estrai()).cromosoma;
			coppie.add(c);
		}
		// le faccio accoppiare
		Iterator<Cromosoma> i = coppie.iterator();
		int n = coppie.size();
		while (n > 2) {
			c = GeneticOperators.crossover(i.next(), i.next(), false);
			n = n - 2;
			P.aggiungifiglio(c);
		}
		logger.fine(".");
	}

	/**
	 * TODO: Ottimizzare sta roba.
	 * 
	 * @param r
	 */
	public void crossover_rank(double r) {
		int n;
		//ArrayList<Integer> candidati = new ArrayList<>();
		PopolazioneOrdinata P = (PopolazioneOrdinata) popolazione;
		Cromosoma c;
		LinkedList<Cromosoma> coppie = new LinkedList<>();
		// estraggo le coppie
		Iterator<CromosomaMisurato> entries = P.iteratorepadri();
		ArrayList<Double> frequenze = new ArrayList<Double>();
		while (entries.hasNext()) {
			CromosomaMisurato e = entries.next();
			frequenze.add(e.prestazioni);
		}

		// seleziono i candidati
		int N = P.padrisize();
		Object[] cromosomi=P.padri_toArray();
		
		double k = crossover_rate / GeneticOperators.probabilita_rank_lineare(1, N, r);
		for (n = 0; n < N; n++) {
			double f = SingletonGenerator.r.nextDouble();
			double p = k * GeneticOperators.probabilita_rank_lineare(n + 1, N, r);
			// System.out.printf("%f < %f ?? ",f,p);
			if (f < p) {
				// System.out.print("Yep!");
				// il mio array è in ordine crescente, una classifica di rank in
				// ordine decrescente.
				//candidati.add(N - n - 1);
				coppie.add(((CromosomaMisurato)cromosomi[N - n - 1]).cromosoma);
				
			}
			// System.out.println(".");
		}
		/*
		 *Ordino i candidati e scorro il set dei padri estraendo quelli contenuti 
		 *nella lista dei candidati 
		 
		
		Collections.sort(candidati);
		Iterator<CromosomaMisurato> iterator = P.iteratorepadri();
		int n = 0;
		N = candidati.size();
		int j = 0;
		while (n < N) {
			k = candidati.get(n);
			while (j < k && iterator.hasNext()) {
				iterator.next();
				j++;
			}
			CromosomaMisurato cm = iterator.next();
			j++;
			while (n < N && candidati.get(n) == k) {
				coppie.add(cm.cromosoma);
				n++;
			}
		}*/
		// le mischio
		Collections.shuffle(coppie, SingletonGenerator.r);
		// le faccio accoppiare
		Iterator<Cromosoma> i = coppie.iterator();
		n = coppie.size();
		while (n > 2) {
			c = GeneticOperators.crossover(i.next(), i.next(), false);
			n = n - 2;
			P.aggiungifiglio(c);
		}
		logger.fine(".");
		// System.out.println("Crossed");

	}

	public void crossover_torneo(double probabilita, int apertura) {
		PopolazioneRAM P = (PopolazioneRAM) popolazione;

		Cromosoma c;

		LinkedList<Cromosoma> coppie = new LinkedList<>();
		// estraggo le coppie
		Iterator<CromosomaMisurato> entries = P.iteratorepadri();
		ArrayList<Double> frequenze = new ArrayList<Double>();
		while (entries.hasNext()) {
			CromosomaMisurato e = entries.next();
			frequenze.add(e.prestazioni);
		}
		// spero vivamente che usi le reference e non copi i cromosomi
		// Object cromosomi[]=padri_ordinati.toArray();
		int len = P.padrisize();
		int numerocoppie = (int) Math.round(len * probabilita);

		for (int n = 0; n < numerocoppie; n++) {
			Cromosoma candidato = null;
			int dado;
			double f_estratto = -1;
			// un bel torneo
			for (int k = 0; k < apertura; k++) {
				dado = SingletonGenerator.r.nextInt(len);
				CromosomaMisurato cm = P.get_element(dado);
				if (cm.prestazioni > f_estratto) {
					f_estratto = cm.prestazioni;
					candidato = cm.cromosoma;
				}
			}
			coppie.add(candidato);
		}
		// le mischio
		Collections.shuffle(coppie, SingletonGenerator.r);
		// le faccio accoppiare
		Iterator<Cromosoma> i = coppie.iterator();
		int n = coppie.size();
		while (n > 2) {
			c = GeneticOperators.crossover(i.next(), i.next(), false);
			n = n - 2;
			P.aggiungifiglio(c);
		}
		logger.fine(".");
	}

	// public abstract double probabilita_rank(int i, int size);

	/**
	 * Seleziona dei figli dall'ultima generazione ( stanno in
	 * popolazione_nonvalutata) e ne esegue la mutazione, SOSTITUENDOLI
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
		popolazione.trimtosize(size);
	}

	/**
	 * Avanza di una generazione
	 */
	public abstract double evolvi();

	protected double estrai_migliore() {
		CromosomaMisurato M = popolazione.estraimigliore();
		double f = M.prestazioni;
		if (f > this.bestfitness) {
			this.bestfitness = f;
			this.bestcromosoma = M.cromosoma;
		}
		return f;
	}

}
