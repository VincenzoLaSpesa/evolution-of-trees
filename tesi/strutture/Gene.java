package tesi.strutture;

import java.io.Serializable;

/**
 * Rappresenta un singolo nodo dell'albero di scelta, che viene rappresentato con un attributo e un punto di taglio. <br>
 * - Se il punto di taglio è NaN il nodo viene interpretato come foglia e l'attributo come la classe scelta.<br>
 * - Se il punto di taglio è un float allora il nodo è interpretato come un nodo non terminale e l'attributo 
 * rappresenta l'attributo su cui si sta effettuando la scelta, il tipo di condizione di taglio viene delezionata dall'enum taglio 
 * @author darshan
 *
 */
public class Gene implements Serializable{

	private static final long serialVersionUID = 8793885518667156524L;
	public int attributo;
	public double punto;
	public Taglio taglio;
	
	public String toString(){
		return String.format("[%d %f %s]", attributo,punto, taglio.toString());
	}

	public Gene() {
		super();
		this.attributo = 0;
		this.punto = Float.NaN;
		this.taglio = Taglio.Continuo;

	}
	
	

}
