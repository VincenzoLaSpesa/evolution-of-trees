package tesi.controllers;

import java.util.logging.Logger;

import tesi.models.Cromosoma;
import tesi.util.SingletonGenerator;
import tesi.util.logging.GlobalLogger;

/**
 * Classe statica che contiene le implementazioni degli operatori genetici
 * @author darshan
 *
 */
public abstract class GeneticOperators {

	static final Logger logger;
	
	static{
		String path=GeneticOperators.class.getName();
		logger= Logger.getLogger(path);
		logger.setLevel(GlobalLogger.level);
		//logger.addHandler(GlobalLogger.console);
		logger.fine(String.format("Logger inizializzato per: %s", path));
		
	}
	
	/**
	 * Esegue il Crossover tra due segmenti specifici passati come parametro, 
	 * non viene fatto controllo sulla coerenza dei segmenti, 
	 * per generare sottoalberi coerenti è meglio utilizzare
	 * public static Cromosoma crossover(Cromosoma c1, Cromosoma c2, boolean scambiafoglie)
	 * <br> questa funzione è stata separata da essa ed è stata resa pubblica fondamentalmente per 
	 * implementare il controllo semantico degli alberi (Feasibility check in Fu Et Al. )
	 * @param c1
	 * @param c2
	 * @param sottoalbero
	 * @param scambiafoglie
	 * @return
	 */
	public static Cromosoma crossover(Cromosoma c1, Cromosoma c2, int[][] sottoalbero) {
		int len[] = new int[2];
		int cur1 = 0;
		int cur2 = 0;
		
		final int c1_s = c1.cromosoma.size(); // size è un metodo sincrono, i
		final int c2_s = c2.cromosoma.size(); // metodi sincroni sono lenti,
												// meglio non chiamarli dentro
												// un ciclo for
		len[0] = sottoalbero[0][1] - sottoalbero[0][0] + 1;// dimensione del
		// primo sottoalbero
		len[1] = sottoalbero[1][1] - sottoalbero[1][0];// dimensione del secondo
		// sottoalbero
		// System.out.println(ArrayUtil.dump(len));

		if (len[0] < 0)
			len[0] = 1;// era una foglia.
		if (len[1] < 0)
			len[1] = 0;// era una foglia.

		// System.out.println(ArrayUtil.dump(len));
		Cromosoma c3 = new Cromosoma();

		for (cur1 = 0; cur1 < sottoalbero[0][0]; cur1++) {
			c3.cromosoma.add(c1.cromosoma.elementAt(cur1).clone());
			// System.out.printf("[-  ] %d -> %d (%s)\n", c3.cromosoma.size() -
			// 1, cur1, c3.cromosoma.lastElement());
		}
		// System.out.println("-");
		cur2 = sottoalbero[1][0];
		while (cur2 <= sottoalbero[1][0] + len[1] && cur2 < c2_s) {
			c3.cromosoma.add(c2.cromosoma.elementAt(cur2).clone());
			// System.out.printf("[-- ] %d -> %d (%s)\n", c3.cromosoma.size() -
			// 1, cur2, c3.cromosoma.lastElement());
			cur2++;
		}
		// System.out.println("-");
		cur1 = sottoalbero[0][0] + len[0];
		while (cur1 < c1_s) {
			c3.cromosoma.add(c1.cromosoma.elementAt(cur1).clone());
			// System.out.printf("[---] %d -> %d (%s)\n", c3.cromosoma.size() -
			// 1, cur1, c3.cromosoma.lastElement());
			cur1++;

		}
		/*
		 * LastParsedObjects.push(ArrayUtil.dump(sottoalbero));
		 * LastParsedObjects.push(c2.toYaml());
		 * LastParsedObjects.push(c1.toYaml());
		 */
		c3.ristruttura();

		return c3;

	}

	/**
	 * Genera un nuovo Cromosoma incrociando i due cromosomi in input, la
	 * variabile scambiafoglie permette di evitare che avvengano crossover
	 * relativi soltanto alle foglie (gait per esempio proibisce questo tipo di
	 * crossover) <b>Il cromosoma figlio dovrà essere come il primo genitore un
	 * cui sottoalbero è stato scambiato con un sottoalbero del secondo
	 * genitore</b>
	 * 
	 * @param c1
	 * @param c2
	 * @param scambiafoglie
	 * @return
	 */
	public static Cromosoma crossover(Cromosoma c1, Cromosoma c2, boolean scambiafoglie) {
		int sottoalbero[][] = new int[2][2];
		final int c1_s = c1.cromosoma.size(); // size è un metodo sincrono, i
		final int c2_s = c2.cromosoma.size(); // metodi sincroni sono lenti,
												// meglio non chiamarli dentro
												// un ciclo for

		boolean flag;

		// Genera i due sottoalberi, se il flag scambiafoglia non è true fa in
		// modo che non siano entrambi foglie
		int k = 0;
		do {
			sottoalbero[0][0] = SingletonGenerator.r.nextInt(c1.cromosoma.size());
			sottoalbero[1][0] = SingletonGenerator.r.nextInt(c2.cromosoma.size());
			sottoalbero[0][1] = c1.trovaconfine(sottoalbero[0][0]);
			sottoalbero[1][1] = c2.trovaconfine(sottoalbero[1][0]);

			flag = scambiafoglie || sottoalbero[0][1] != 0 || sottoalbero[1][1] != 0;
			k++;

			/*
			 * Ci sono alcuni alberi che sono impossibili da accoppiare senza
			 * scambiare foglie o senza sovrascriture complete ( gli alberi da 3
			 * elementi per esempio)
			 */
			if (k > c1_s * c2_s) {
				logger.info("Questi due alberi non sono compatibili");				
				logger.info(c1.toYaml());
				logger.info(c2.toYaml());
				return c1.clone();
			}
		} while (!flag);
		// System.out.println(ArrayUtil.dump(sottoalbero));
		return crossover(c1, c2, sottoalbero);

	}

	/**
	 * Genera un nuovo cromosoma mutando il cromosoma in input ( secondo le
	 * specifiche di Gait una mutazione può essere pensata come un crossover
	 * dell'albero con se stesso) la variabile foglie permette di evitare che
	 * avvengano crossover relativi soltanto alle foglie (gait per esempio
	 * proibisce questo tipo di mutazione)
	 * 
	 * @param c1
	 * @param foglie
	 * @return
	 */
	public static Cromosoma mutate(Cromosoma c1, boolean foglie) {
		return crossover(c1, c1, foglie);
	}

	/**
	 * Una funzione di fitness multiobiettivo che tiene conto delle prestazioni
	 * del'albero e della sua altezza, <br>
	 * è definita come: <tt> prestazioni * alpha + beta*(Math.sqrt(1 / (gamma + c.getComplessita()))); </tt><br>
	 * <b> non <b> è limitata in [0 1]
	 * 
	 * @param prestazioni
	 * @param c
	 * @param alpha
	 * @param beta
	 * @return
	 */
	public static double calcola_fitness_multiobiettivo_nonlineare(double prestazioni, Cromosoma c, double alpha,
			double beta, double gamma) {
		double p = prestazioni * alpha + beta*(Math.sqrt(1 / (gamma + c.getComplessita())));
		//double p = Math.pow(prestazioni, epsilon) * alpha + (Math.sqrt(beta / (gamma + c.getComplessita())));
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

}
