package tesi.models.popolazione;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import tesi.models.Cromosoma;
import tesi.models.CromosomaMisurato;
import tesi.util.SingletonGenerator;

public abstract class Popolazione {
	protected Collection<CromosomaMisurato> padri;
	public LinkedList<Cromosoma> figli;
	public long idgenerazione = 0;

	public Popolazione() {
		figli = new LinkedList<Cromosoma>();
	}

	abstract protected void aggiornamassimo(CromosomaMisurato c);

	public int aggiungifiglio(Cromosoma c) {
		figli.add(c);
		return figli.size();
	}

	/**
	 * Applica il metodo tarpeian per ridurre il bloat basandosi sull'altezza
	 * della popolazione, cito dalla mia tesi: <quote> Si selezionano alcuni
	 * alberi più lunghi della media e li si esclude temporaneamente dalle
	 * selezioni per la riproduzione. Aumentando il numero di alberi e la
	 * frequenza di applicazione del metodo si può modularne l'intensità.
	 * </quote> Il tarpeian agisce sia sui padri che sui figli, per prima cosa
	 * agisce sui figli marcando i selezionati, poi sui padri marcandoli e
	 * SPOSTANDOLI tra i figli per evitare che successivi ridimensionamenti
	 * della popolazione possano uccidere cromosomi tarped. QUESTO VIOLA
	 * PARZIALMENTE IL VINCOLO DELLA POPOLAZIONE FISSA.
	 * 
	 * @param soglia
	 * @param probabilita
	 * @return
	 */
	public int tarpeian(double soglia, double probabilita) {
		double f;
		Cromosoma c;
		// i figli
		Iterator<Cromosoma> i = figli.iterator();
		int n = 0;
		while (i.hasNext()) {
			f = SingletonGenerator.r.nextFloat();
			c = i.next();
			if (c.altezza > soglia && f <= probabilita) {
				n++;
				c.pinned = idgenerazione + 2;
			}
		}

		double fitnessmigliore = estraimigliore().prestazioni;

		// i padri
		Iterator<CromosomaMisurato> i2 = padri.iterator();
		while (i2.hasNext()) {
			f = SingletonGenerator.r.nextFloat();
			CromosomaMisurato cm = i2.next();
			c = cm.cromosoma;
			if (c.altezza > soglia && f <= probabilita) {
				n++;
				c.pinned = idgenerazione + 2;
				if (cm.prestazioni < fitnessmigliore) {
					figli.add(c);
					i2.remove();
				}
			}
		}

		// System.out.println(n);
		return n;
	}

	abstract public int aggiungipadre(CromosomaMisurato c);

	abstract public CromosomaMisurato estraimigliore();

	abstract public int flush();

	public int init_figli(LinkedList<Cromosoma> pop) {
		figli = new LinkedList<>();
		figli.addAll(pop);
		return figli.size();
	}

	public Iterator<Cromosoma> iteratorefigli() {
		return figli.iterator();
	}

	public Iterator<CromosomaMisurato> iteratorepadri() {
		return padri.iterator();
	}

	public boolean mergefigli(LinkedList<Cromosoma> offspring) {
		return figli.addAll(offspring);
	}

	abstract public int padrisize();

	abstract public boolean sort();

	/**
	 * Elimina gli esemplari peggiori della popolazione riducendola a size
	 * elementi, il resize avviene solo attraverso i padri ( perchè si suppone
	 * che i figli vengano fusi nei padri alla fine di ogni generazione)
	 * 
	 * @param size
	 * @return
	 */
	abstract public void trimtosize(int size);
}
