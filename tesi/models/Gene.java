package tesi.models;

import java.io.Serializable;

/**
 * Rappresenta un singolo nodo dell'albero di scelta, che viene rappresentato
 * con un attributo e un punto di taglio. <br>
 * - Se il punto di taglio è NaN il nodo viene interpretato come foglia e
 * l'attributo come la classe scelta.<br>
 * - Se il punto di taglio è un float allora il nodo è interpretato come un nodo
 * non terminale e l'attributo rappresenta l'attributo su cui si sta effettuando
 * la scelta, il tipo di condizione di taglio viene delezionata dall'enum taglio
 * 
 * @author darshan
 * 
 */
public class Gene implements Serializable {

	private static final long serialVersionUID = 8793885518667156524L;
	public static String csvHead = "Att\tPunto\tTaglio\tFine\n";
	//
	public int attributo;
	public double punto;
	public Taglio taglio;
	/**
	 * posizione dell'ultimo nodo dell sottoalbero corrente, da un punto di
	 * vista strettamente OOP non dovrebbe stare qui visto che non si riferisce
	 * al singolo gene ma all'intera struttura, ma vista l'implementazione del
	 * Cromosoma come semplice vettore di geni la mantengo qui.
	 */
	public int fine;

	public String toString() {
		return String.format("[%d %f %s]", attributo, punto, taglio.toString());
	}

	public Gene() {
		super();
		this.attributo = 0;
		this.fine = 0;
		this.punto = Float.NaN;
		this.taglio = Taglio.Continuo;

	}

	public Gene clone() {
		Gene g = new Gene();
		g.attributo = this.attributo;
		g.fine = this.fine;
		g.punto = this.punto;
		g.taglio = this.taglio;
		return g;

	}

	/**
	 * Produce una singola riga per la costruzione di un CSV
	 * 
	 * @return
	 */
	public String toCsv() {
		return String.format("%d	%.2f	%d	%d", attributo, punto,
				taglio.ordinal(), fine);
	}

}
