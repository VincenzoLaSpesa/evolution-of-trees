package tesi.controllers;

import java.util.logging.Logger;

import tesi.models.Cromosoma;
import tesi.util.GlobalLogger;
import tesi.util.SingletonGenerator;

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

}
