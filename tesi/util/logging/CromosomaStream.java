package tesi.util.logging;

import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;

import tesi.controllers.TreeEvaluator;
import tesi.models.Cromosoma;
import weka.core.Instances;

/**
 * Permette di accumulare dei cromosomi per valutarli in seguito
 * 
 * @author darshan
 */
public class CromosomaStream {
	public static int limit = 1024;
	protected TreeMap<String, LinkedList<Cromosoma>> Colonne;
	protected String colonna_corrente;

	public CromosomaStream() {
		colonna_corrente = "default";
		Colonne = new TreeMap<String, LinkedList<Cromosoma>>();
		createColumn("default");
	}

	public FloatStream calcola(Instances testset, int nclassi) {
		FloatStream ft = new FloatStream();
		Set<String> kset = Colonne.keySet();
		// i dati
		for (String k : kset) {
			ft.createColumn(k);
			ft.setColonna_corrente(k);
			LinkedList<Cromosoma> l = Colonne.get(k);
			for (Cromosoma c : l) {
				TreeEvaluator te = new TreeEvaluator(c, testset, nclassi);
				ft.append(te.evaluate());
			}
		}
		return ft;
	}

	public int append(Cromosoma e) {
		Colonne.get(colonna_corrente).add(e.clone());
		if (Colonne.get(colonna_corrente).size() > limit)
			Colonne.get(colonna_corrente).removeFirst();
		return Colonne.get(colonna_corrente).size();
	}

	public int appendDefault(Cromosoma e) {
		Colonne.get("default").add(e.clone());
		if (Colonne.get("default").size() > limit)
			Colonne.get("default").removeFirst();
		return Colonne.get("default").size();
	}

	public int append(String colonna, Cromosoma e) {
		Colonne.get(colonna).add(e.clone());
		if (Colonne.get(colonna).size() > limit)
			Colonne.get(colonna).removeFirst();
		return Colonne.get(colonna).size();
	}

	/**
	 * Crea una nuova colonna, <b>se la colonna esiste la sovrascrive</b>
	 * 
	 * @param k
	 * @return
	 */
	public int createColumn(String k) {
		Colonne.put(k, new LinkedList<Cromosoma>());
		return Colonne.size();
	}

	public String getColonna_corrente() {
		return colonna_corrente;
	}

	public void setColonna_corrente(String colonna_corrente) {
		this.colonna_corrente = colonna_corrente;
	}
	
	public void deleteColumn(String k){
		this.Colonne.remove(k);
	}

}
