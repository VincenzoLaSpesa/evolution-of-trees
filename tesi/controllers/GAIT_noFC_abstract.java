package tesi.controllers;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import tesi.models.Cromosoma;
import tesi.models.CromosomaMisurato;
import tesi.models.Singletons;
import tesi.util.SingletonGenerator;
import weka.core.Instances;

/**
 * Implementa Gait senza controllo semantico ( ci possono essere nell'albero due
 * vincoli che non possono essere soddisfatti contemporaneamente o vincoli che
 * non aggiungono informazione perchè soddisfatti sicuramente) <b> la funzione
 * di fitness non è definita<b>
 * 
 * @author darshan
 * 
 */
public abstract class GAIT_noFC_abstract extends Ecosistema {

	/**
	 * Il Numero massimo di elementi nella popolazione
	 */
	public int limit;

	/**
	 * Per come è definito gait scelgo i cromosomi da crossare linearmente
	 * rispetto al fitness, questo va bene de il fitness è in [0 1], in caso
	 * contrario possono verificarsi cose strane, con il fattore riscalo il
	 * fitness in modo da rientrare nel range corretto
	 */
	public double fattorediscalatura = 1;

	public double calcola_fitness_multiobiettivo(double prestazioni, Cromosoma c) {
		logger.warning("Questa funzione andrebbe ridefinita, altrimenti si comporta come una funzione di fitness semplice");
		return prestazioni;
	}

	/**
	 * Una funzione di fitness multiobiettivo che tiene conto delle prestazioni
	 * del'albero e della sua lunghezza, <br>
	 * è definita come: <tt> p * 1/(alpha+beta*len) </tt><br>
	 * Essendo p definita in [0 1] e len definita in [1 n] la funzione stessa è
	 * definita in [0 1].<br>
	 * I parametri alpha e beta permettono di modificare il peso della lunghezza
	 * dell'albero
	 * 
	 * @param prestazioni
	 * @param c
	 * @param alpha
	 * @param beta
	 * @return
	 */
	public static double calcola_fitness_multiobiettivo_semplice(double prestazioni, Cromosoma c, double alpha,
			double beta) {

		return prestazioni * 1 / (alpha + beta * c.getComplessita());
	}

	/**
	 * Una funzione di fitness multiobiettivo che tiene conto delle prestazioni
	 * del'albero e della sua altezza, <br>
	 * è definita come: <tt> alpha * p + beta / len </tt><br>
	 * <b> non <b> è limitata in [0 1]
	 * 
	 * @param prestazioni
	 * @param c
	 * @param alpha
	 * @param beta
	 * @return
	 */
	public static double calcola_fitness_multiobiettivo_additiva(double prestazioni, Cromosoma c, double alpha,
			double beta, double gamma, double epsilon) {
		double p = Math.pow(prestazioni, epsilon) * alpha + (Math.sqrt(beta / (gamma + c.getComplessita())));
		// System.out.printf("%f + %f\n",prestazioni *
		// alpha,(Math.sqrt(beta/(gamma+c.getComplessita()))));
		return p;
	}

	/**
	 * Una funzione di fitness multiobiettivo che tiene conto delle prestazioni
	 * del'albero e della sua altezza, <br>
	 * è definita come: <tt> p * 1/(alpha+beta*len) </tt><br>
	 * Essendo p definita in [0 1] e len definita in [1 n] la funzione stessa è
	 * definita in [0 1].<br>
	 * I parametri alpha e beta permettono di modificare il peso della lunghezza
	 * dell'albero
	 * 
	 * @param prestazioni
	 * @param c
	 * @param alpha
	 * @param beta
	 * @return
	 */
	public static double calcola_fitness_multiobiettivo_lineare(double prestazioni, Cromosoma c, double alpha,
			double beta) {

		return prestazioni * alpha - beta * c.getComplessita();
	}

	public GAIT_noFC_abstract(Instances testset, int nclassi, int limit) {
		super(testset, nclassi);
		this.limit = limit;
	}

	/**
	 * Implementa un fitness semplice, va utilizzata in get_fitness() che qui è
	 * astratta
	 * 
	 * @return
	 */
	protected double simple_fitness() {

		TreeEvaluator te;
		Iterator<Cromosoma> i = popolazione_nonvalutata.iterator();
		double media = 0;
		double a = 1;
		boolean b;
		while (i.hasNext()) {

			Cromosoma c = i.next();
			te = new TreeEvaluator(c, testset, nclassi);
			te.evaluate();
			CromosomaMisurato cm = new CromosomaMisurato(te.prestazioni, c);
			b = popolazione_valutata.add(cm);
			i.remove();
			media += te.prestazioni;
			if (Double.isNaN(media)) {
				logger.warning("no no no, questo non dovrebbe succedere!");
			}
			if (!b) {
				logger.warning("Un cromosoma duplicato è stato eliminato");
			}
			a++;
			// System.out.printf("\t%d:\t%f\n",a,te.prestazioni);
		}
		media = media / a;
		return media;
	}

	/**
	 * Implementa un fitness multiobiettivo, va utilizzata in get_fitness() che
	 * qui è astratta, si basa su calcola_fitness_multiobiettivo(double
	 * prestazioni, Cromosoma c) che va ridefinita. ( è una funzione
	 * simil-astratta che viene definita solo per poter istanziare semplicemente
	 * una classe che lavora di fitness semplice e non la usa )
	 * 
	 * @return
	 */
	protected double multiobjective_fitness() {

		TreeEvaluator te;
		Iterator<Cromosoma> i = popolazione_nonvalutata.iterator();
		double media = 0;
		double a = 1;
		double fitness;
		while (i.hasNext()) {

			Cromosoma c = i.next();
			te = new TreeEvaluator(c, testset, nclassi);
			te.evaluate();
			fitness = calcola_fitness_multiobiettivo(te.prestazioni, c);
			CromosomaMisurato cm = new CromosomaMisurato(fitness, c);
			popolazione_valutata.add(cm);
			i.remove();
			media += te.prestazioni;
			if (Double.isNaN(media)) {
				logger.warning("no no no, questo non dovrebbe succedere!");
			}
			a++;
			// System.out.printf("\t%d:\t%f\n",a,te.prestazioni);
		}
		media = media / a;
		return media;
	}

	/**
	 * In GAIT la probabilità di crossover dipende unicamente dal fitness, si
	 * genera un numero [0 1] e se è minore del valore di fitness ( che è sempre
	 * [0 1]) l'elemento viene scelto, questo significa che vengono estratti n
	 * elementi che verranno poi ordinati a caso formando n/2 coppie di elementi
	 * che creeranno n/2 figli.
	 * 
	 * In realtà uso un <b>fattorediscalatura</b> per riscalare i valori della
	 * fitness, se il fattore è 1 è identico al GAIT originale
	 */
	public void crossover_etilist() {
		float f;
		LinkedList<Cromosoma> coppie = new LinkedList<>();
		// estraggo le coppie
		Iterator<CromosomaMisurato> entries = popolazione_valutata.iterator();
		while (entries.hasNext()) {
			f = SingletonGenerator.r.nextFloat();
			CromosomaMisurato e = entries.next();
			if (e.prestazioni >= f * fattorediscalatura) {
				coppie.add(e.cromosoma);
			}
		}
		// le mischio
		Collections.shuffle(coppie, SingletonGenerator.r);
		// le faccio accoppiare
		Iterator<Cromosoma> i = coppie.iterator();
		int n = coppie.size();
		while (n > 2) {
			Cromosoma c = GeneticOperators.crossover(i.next(), i.next(), false);
			n = n - 2;
			popolazione_nonvalutata.add(c);
		}
		logger.fine(".");

	}

	/**
	 * Estrae elementi dalla popolazione_valutata, li muta e li sposta in
	 * popolazione_nonvalutata, non è il modo corretto di effettuare una
	 * mutazione.
	 * 
	 * 
	 * @param probabilita
	 */
	@Deprecated
	public void mutate_parents(double probabilita) {
		float f;
		Cromosoma c;
		//
		Iterator<CromosomaMisurato> entries = popolazione_valutata.iterator();
		while (entries.hasNext()) {
			f = SingletonGenerator.r.nextFloat();
			CromosomaMisurato e = entries.next();
			if (f <= probabilita) {
				c = GeneticOperators.mutate(e.cromosoma, false);
				popolazione_nonvalutata.add(c);
			}
		}

	}

	/**
	 * Estrae elementi dall'ultima generazione ( in popolazione_nonvalutata) ,
	 * li muta, E LI SOSTITUISCE
	 * 
	 * @param probabilita
	 */
	@Override
	public void mutate(double probabilita) {
		float f;
		Cromosoma c;
		//
		Iterator<Cromosoma> entries = popolazione_nonvalutata.iterator();
		LinkedList<Cromosoma> buffer = new LinkedList<>();
		while (entries.hasNext()) {
			f = SingletonGenerator.r.nextFloat();
			Cromosoma e = entries.next();
			if (f <= probabilita) {
				c = GeneticOperators.mutate(e, false);
				buffer.add(c);
				// the accepted safe way to modify a collection during iteration
				entries.remove();
			}
			popolazione_nonvalutata.addAll(buffer);
		}

	}

	@Override
	public double evolvi() {
		double f, m;
		crossover();
		f = get_fitness();
		mutate(mutation_rate);
		get_fitness();
		m = estrai_migliore();
		Singletons.cromosomastream.append(this.bestcromosoma);
		Singletons.pesistream.append(this.bestcromosoma.getComplessita());
		logger.fine(String.format("\t Il Fitness dei nuovi individui è %f\n\t il massimo %f\n", f, m));
		logger.fine(String.format("\t Ci sono %d = %d + %d elementi attivi\n", popolazione_nonvalutata.size()
				+ popolazione_valutata.size(), popolazione_valutata.size(), popolazione_nonvalutata.size()));
		trimtosize(limit);

		return m;

	}

	/**
	 * Evolve popolazione_iniziale per popolazione_iniziale generazioni e
	 * ritorna il cromosoma migliore
	 * 
	 * @param popolazione_iniziale
	 * @param numerogenerazioni
	 * @param mutante
	 *            se true fa in modo che la probabilità di mutazione venga
	 *            incrementata linearmente durante le fasi di stagnazione
	 * @return
	 */
	public Cromosoma GAIT(LinkedList<Cromosoma> popolazione_iniziale, int numerogenerazioni, boolean mutante) {
		this.popolazione_nonvalutata = popolazione_iniziale;
		get_fitness();
		double lastfitness = bestfitness;
		for (int i = 0; i < numerogenerazioni; i++) {
			logger.fine(String.format("Genero la generazione n %d, ci sono %d elementi\n", i,
					popolazione_valutata.size()));
			evolvi();
			if (mutante) {
				if (bestfitness == lastfitness) {
					mutation_rate += baserate;
				} else {
					mutation_rate = baserate;
				}
				lastfitness = bestfitness;
			}

			this.fattorediscalatura = bestfitness;
			logger.fine(String.format("OK"));
		}
		return bestcromosoma;
	}

	@Override
	@Deprecated
	/**
	 * In GAIT la probabilità di crossover dipende unicamente dal fitness, si genera un numero [0 1] e se è minore del valore di fitness ( che è sempre [0 1]) l'elemento viene scelto
	 */
	public void crossover_etilist(double probabilita) {
		crossover_etilist();

	}

}
