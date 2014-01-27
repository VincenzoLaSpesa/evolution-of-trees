package tesi.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.logging.Logger;

import tesi.models.Cromosoma;
import tesi.models.CromosomaMisurato;
import tesi.util.SingletonGenerator;
import tesi.util.logging.GlobalLogger;
import weka.core.Instances;

/**
 * Classe astratta che viene estesa per implementare gli algoritmi evolutivi
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
	protected TreeSet<CromosomaMisurato> popolazione_valutata;
	protected LinkedList<Cromosoma> popolazione_nonvalutata;
	public static double mutation_rate=0.01;
	public static double crossover_rate=0.85;	
	public static final double baserate=mutation_rate;
	
	public Ecosistema(Instances testset, int nclassi) {
		super();
		popolazione_nonvalutata = new LinkedList<>();
		popolazione_valutata = new TreeSet<>();
		this.testset=testset;
		this.nclassi=nclassi;
		String path=this.getClass().getName();
		logger= Logger.getLogger(path);
		logger.setLevel(GlobalLogger.level);
		logger.addHandler(GlobalLogger.console);
		logger.fine(String.format("Logger inizializzato per: %s", path));
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
	 * Implementa una strategia di crossover a roulette che estrae dalla roulet popolazione*probabilità elementi
	 * Alla fine della procedura si otterranno quindi popolazione+popolazione*probabilita*0.5 elementi nella popolazione.
	 * gli elementi nuovi vengono caricati in popolazione_nonvalutata. 
	 * @param probabilita
	 */
	public void crossover_spf(double probabilita) {		
		Cromosoma c;
		Roulette roulette;
		LinkedList<Cromosoma> coppie = new LinkedList<>();
		// estraggo le coppie
		Iterator<CromosomaMisurato> entries = popolazione_valutata.iterator();
		ArrayList<Double> frequenze= new ArrayList<Double>();
		while (entries.hasNext()) {
			CromosomaMisurato e = entries.next();
			frequenze.add(e.prestazioni);
		}
		roulette= new Roulette(frequenze);
		//spero vivamente che usi le reference e non copi i cromosomi
		Object cromosomi[]=popolazione_valutata.toArray();		
		int numerocoppie=(int)(roulette.vettore.length*probabilita);
				
		for(int n=0; n<numerocoppie;n++){
			c=((CromosomaMisurato)cromosomi[roulette.estrai()]).cromosoma;
			coppie.add(c);
		}
		// le faccio accoppiare
		Iterator<Cromosoma> i = coppie.iterator();
		int n = coppie.size();
		while (n > 2) {
			c = GeneticOperators.crossover(i.next(), i.next(), false);
			n = n - 2;
			popolazione_nonvalutata.add(c);
		}
		logger.fine(".");
	}
	
	public void crossover_rank(double r) {		
		Cromosoma c;
		LinkedList<Cromosoma> coppie = new LinkedList<>();
		// estraggo le coppie
		Iterator<CromosomaMisurato> entries = popolazione_valutata.iterator();
		ArrayList<Double> frequenze= new ArrayList<Double>();
		while (entries.hasNext()) {
			CromosomaMisurato e = entries.next();
			frequenze.add(e.prestazioni);
		}

		//spero vivamente che usi le reference e non copi i cromosomi
		Object cromosomi[]=popolazione_valutata.toArray();		
		
		int N=cromosomi.length;
		double k=crossover_rate/probabilita_rank_lineare(1,N,r);
		for(int n=0; n<N;n++){
			double f=SingletonGenerator.r.nextDouble();
			double p=k*probabilita_rank_lineare(n+1,cromosomi.length,r);
			//System.out.printf("%f < %f ?? ",f,p);
			if (f < p){
				//System.out.print("Yep!");
				//il mio array è in ordine crescente, una classifica di rank in ordine decrescente.
				c=((CromosomaMisurato)cromosomi[N-n-1]).cromosoma;
				coppie.add(c);
			}
			//System.out.println(".");
		}
		// le mischio
		Collections.shuffle(coppie, SingletonGenerator.r);
		// le faccio accoppiare
		Iterator<Cromosoma> i = coppie.iterator();
		int n = coppie.size();
		while (n > 2) {
			c = GeneticOperators.crossover(i.next(), i.next(), false);
			n = n - 2;
			popolazione_nonvalutata.add(c);
		}
		logger.fine(".");
		//System.out.println("Crossed");

	}

	public void crossover_torneo(double probabilita, int apertura) {		
		Cromosoma c;
		
		LinkedList<Cromosoma> coppie = new LinkedList<>();
		// estraggo le coppie
		Iterator<CromosomaMisurato> entries = popolazione_valutata.iterator();
		ArrayList<Double> frequenze= new ArrayList<Double>();
		while (entries.hasNext()) {
			CromosomaMisurato e = entries.next();
			frequenze.add(e.prestazioni);
		}
		//spero vivamente che usi le reference e non copi i cromosomi
		Object cromosomi[]=popolazione_valutata.toArray();		
		
		int numerocoppie=(int)Math.round(cromosomi.length*probabilita);
		
		for(int n=0; n<numerocoppie;n++){
			Cromosoma candidato=null;
			int dado;
			double f_estratto=-1;
			//un bel torneo
			for(int k=0;k<apertura;k++){
				dado=SingletonGenerator.r.nextInt(cromosomi.length);
				CromosomaMisurato cm= (CromosomaMisurato)cromosomi[dado];
				if(cm.prestazioni>f_estratto){
					f_estratto=cm.prestazioni;
					candidato=cm.cromosoma;
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
			popolazione_nonvalutata.add(c);
		}
		logger.fine(".");
	}

	
	/**
	 * Funzione di probabilità lineare legata al rank regolata sul parametro r che
	 * influenza la pressione selettiva.
	 * r=0 --> nessuna pressione selettiva 
	 * r=1 --> massima pressione selettiva.
	 * i valori in input variano tra [0..1] e verranno riscalati in  [0..2/(size*(size-1))]
	 * per essere usati nella seguente formula:
	 * prob(rank)=q-(rank-1)*r <br>
	 * con q definito come <br>
	 * q=r(size-1)/2+1/size<br>
	 * La funzione è descritta in [Michalweicz] 4.1 (pagina 60)
	 * @param i
	 * @param size
	 * @param q
	 * @return
	 */
	public static double  probabilita_rank_lineare(int rank, double size, double r) {
		if(rank<1 || rank>size){
			System.err.println("Questo non dovrebbe succedere");
			return -1;
		}		
		r=r*(2/(size*(size-1)));
		double q=r*(size-1)/2+1/size;
		double f=q-(rank-1)*r;
		return f;
	}
	
	/**
	/**
	 * Funzione di probabilità lineare legata al rank regolata sul parametro q che
	 * influenza la pressione selettiva.
	 * q=0 --> nessuna pressione selettiva 
	 * q=1 --> massima pressione selettiva.
	 * prob(rank)=c*q(1-q)^(rank-1) <br>
	 * con c definito come <br>
	 * c=1/( 1-(1-q)^size ) in modo da far in modo che la somma di tutte le probabilità sia 1<br>
	 * 
	 * La funzione è descritta in [Michalweicz] 4.1 (pagina 60)
	 * @param i
	 * @param size
	 * @param q
	 * @return
	 */
	public static double  probabilita_rank_nonlineare(int i, int size, double q) {
		double c=1/(1-Math.pow(1-q, size));
		double f=c*q*Math.pow(1-q, i);
		return f;
	}	

	//public abstract double  probabilita_rank(int i, int size);

	/**
	 * Seleziona dei figli dall'ultima generazione ( stanno in popolazione_nonvalutata) e ne esegue la mutazione, SOSTITUENDOLI
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
