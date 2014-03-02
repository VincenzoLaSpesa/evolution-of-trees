package tesi.models.popolazione;

import java.util.ArrayList;
import java.util.Collections;

import tesi.models.CromosomaMisurato;
import tesi.util.SingletonGenerator;
import tesi.util.logging.GlobalLogger;

public class PopolazioneRAM extends Popolazione {
	private ArrayList<CromosomaMisurato> padri_array;
	protected CromosomaMisurato cromosomamigliore;
	protected int actualsize;

	public PopolazioneRAM() {
		super();
		padri = new ArrayList<CromosomaMisurato>();
		padri_array = (ArrayList<CromosomaMisurato>) padri;
		cromosomamigliore = null;
		actualsize = padri.size();
	}

	public CromosomaMisurato get_element(int n) {
		return padri_array.get(n);
	}

	@Override
	/**
	 * Riduce di dimensione attraverso scontri binari, non necessita di ordinamento e non assicura di eliminare i peggiori.
	 * In realtà i cromosomi non vengono deallocati, per migliorare le prestazioni.
	 * Considerando che in ogni caso la dimenzione della popolazione oscillerebbe entro gli stessi limiti.
	 * Il resize avviene solo attraverso i padri ( perchè si suppone che i figli vengano fusi nei padri alla fine di ogni generazione)
	 */
	public void trimtosize(int size) {
		// System.out.println("un allegro scontro binario");
		// int psize = padri.size();
		int psize = actualsize;
		int n = psize - size;
		while (n > 0) {
			CromosomaMisurato cm1, cm2;
			int id1 = SingletonGenerator.r.nextInt(psize);
			int id2 = SingletonGenerator.r.nextInt(psize);
			cm1 = padri_array.get(id1);
			cm2 = padri_array.get(id2);
			if (cm1.compareTo(cm2) > 0) {
				swap(id2, --psize);

			} else {
				swap(id1, --psize);
			}
			// padri_array.remove(--psize);
			n--;
		}
		actualsize = psize;
		// System.out.println(padrisize());

	}

	// dealloca i cromosomi non utilizzati
	public int flush() {
		// si, lo so che è orrendo ma è il modo standard di java per tagliare
		// una collection
		padri_array.subList(actualsize, padri_array.size()).clear();
		return padri_array.size();

	}

	/**
	 * E' una struttura pensata per non essere ordinata, va usato solo se
	 * veramente necessario.
	 */
	@Override
	public boolean sort() {
		//System.err.println("Yikes!");
		GlobalLogger.logger
				.warning("Stai ordinando una popolazione appartenente a una classe pensata per non essere ordinata, è una cosa lenta.");
		flush();
		Collections.sort(padri_array);
		
		return true;
	}

	@Override
	public CromosomaMisurato estraimigliore() {
		return cromosomamigliore;
	}

	@Override
	protected void aggiornamassimo(CromosomaMisurato c) {
		if (cromosomamigliore == null || cromosomamigliore.compareTo(c) < 0)
			cromosomamigliore = c;

	}

	private void swap(int a, int b) {
		CromosomaMisurato t = padri_array.get(a);
		padri_array.set(a, padri_array.get(b));
		padri_array.set(b, t);
	}

	@Override
	public int aggiungipadre(CromosomaMisurato c) {
		if (actualsize == padri_array.size()) {
			padri_array.add(c);
		} else {
			padri_array.set(actualsize, c);
		}
		aggiornamassimo(c);
		return ++actualsize;
	}

	@Override
	public int padrisize() {
		return actualsize;
	}

	public double somma(){
		double acc=0;
		for(int n=0;n<actualsize;n++){acc+=padri_array.get(n).prestazioni;}
		return acc;
	}
	
	public double[] estraiprestazioni(){
		double[] p= new double[actualsize];
		for(int n=0;n<actualsize;n++){p[n]=padri_array.get(n).prestazioni;}
		return p;
	}
	
}
