package tesi.models;

import java.io.Serializable;
import java.io.StringReader;
import java.util.Vector;

import org.yaml.snakeyaml.Yaml;

import weka.classifiers.trees.J48;
import weka.classifiers.trees.j48.BinC45Split;
import weka.classifiers.trees.j48.ClassifierTree;

/**
 * Rappresenta un singolo albero di decisione linearizzato in un cromosoma, la
 * linearizzazione avviene con una visita per profondità (DFS). Il vantaggio di
 * questa linearizzazione è che <i> ogni sottoalbero è rappresentato da una
 * stringa contigua</i>
 * Essendo un classe entity contiene solo i metodi strettamente necessari per 
 * la sua creazione è per assicurare la coerenza della struttura dati e la sua 
 * correttezza sintattica ( non semantica ).
 * inoltre <b>non contiene la funzione per calcolare la fitness<b> essendo essa 
 * dipendente anche da informazioni che non sono interne al cromosoma ( la popolazione).
 * 
 * @author darshan
 * 
 */
public class Cromosoma implements Serializable {

	private static final long serialVersionUID = 346117818222219309L;
	public Vector<Gene> cromosoma;
	public int altezza=-1;
	//protected double peso=-1;
	public double fattore_di_sbilanciamento;
	public long pinned=-1;
	
	/**
	 * Deserializza da un weka.classifiers.trees.J48 generato da Weka, l'albero
	 * deve essere binario.
	 * 
	 * @param j48
	 * @return
	 * @throws Exception
	 */
	public static Cromosoma loadFromJ48(J48 j48) throws Exception {
		if (!j48.getBinarySplits()) {
			Exception e = new Exception("L'albero non è binario!");
			throw e;
		}
		ClassifierTree tree = j48.getTree();
		Cromosoma c = new Cromosoma();
		c.parse(tree,0);
		return c;
	}

	/**
	 * Deserializza da un dump Yaml
	 * 
	 * @param Yaml_str
	 * @return
	 */
	public static Cromosoma loadFromYaml(String Yaml_str) {
		Yaml y = new Yaml();
		StringReader r = new StringReader(Yaml_str);
		Cromosoma c = (Cromosoma) y.load(r);
		return c;
	}


	public Cromosoma() {
		cromosoma = new Vector<>();
	};

	@SuppressWarnings("unchecked")
	public Cromosoma clone(){
		Cromosoma c= new Cromosoma();
		c.cromosoma=((Vector<Gene>)this.cromosoma.clone());
		return c;
		
	}

	/**
	 * Ritorna la complessità dell'albero, 
	 * la complessità è definita come l'altezza massima dell'albero
	 * @return
	 */
	public double getComplessita(){
		//System.out.println((cromosoma.size()-1)/2);
		if(altezza<0){
			if(cromosoma.size()<4)altezza=1; else {
				String trace=Thread.currentThread().getStackTrace()[1].toString();
				System.err.printf("Questo non dovrebbe succedere %s \n",trace);
				System.err.println(this.toYaml());
			};
		}
		return this.altezza;
		//return (cromosoma.size()-1)/2;
	}
	/**
	 * trova la posizione che delimita il sottoalbero partente da partenza
	 * @param partenza
	 * @return
	 */
	public int trovaconfine(int partenza){
		while(cromosoma.elementAt(partenza).fine>0){
			partenza=cromosoma.elementAt(partenza).fine;
		}
		return partenza;
	}

	/**
	 * Distrugge le informazioni sulla fine dei sottoalberi, si usa per fini di Debug, non dovrebbe essere usata altrimenti
	 */
	@Deprecated
	public void cripple() {
		for (Gene g : cromosoma) {
			g.fine = 0;
		}
	}

	/**
	 * Ricostruisce ricorsivamente un sottoalbero, viene utilizzata dal
	 * costruttore statico loadFromJ48.
	 * 
	 * @param sottoalbero
	 * @throws Exception
	 */
	private void parse(ClassifierTree sottoalbero, int l) throws Exception {
		Gene g = new Gene();
		if (sottoalbero.m_isLeaf) {// foglia
			g.attributo = Integer.parseInt(sottoalbero.m_localModel.dumpLabel(
					0, sottoalbero.m_train).split(" ")[0]);
			g.punto = Float.NaN;
			cromosoma.add(g);
			if(l>altezza)altezza=l;
		} else {// nodo
			BinC45Split splitter = (BinC45Split) sottoalbero.m_localModel;
			g.attributo = splitter.attIndex();
			g.punto = splitter.m_splitPoint;

			if (sottoalbero.m_train.attribute(g.attributo).isNominal())
				g.taglio = Taglio.Discreto;
			else
				g.taglio = Taglio.Continuo;
			cromosoma.add(g);
			int p = cromosoma.size();
			parse(sottoalbero.m_sons[0],l+1);
			parse(sottoalbero.m_sons[1],l+1);
			cromosoma.elementAt(p - 1).fine = cromosoma.size() - 1;
		}
		if(l==0){
			altezza=(int) getComplessita();
			//0.6931471803=ln(2)
			fattore_di_sbilanciamento=altezza/(Math.log(cromosoma.size())/0.6931471803);
		}
	}
	
	/**
	 * Corregge eventuali incoerenze nei puntatori alla fine del sottoalbero
	 * causati dagli operatori di crossover e mutazione, si suppone che la
	 * mutazione non alteri la struttura interna delle foglie
	 */
	public void ristruttura() {
		ristruttura(0,1);
		altezza=(int) getComplessita();
		//0.6931471803=ln(2)
		fattore_di_sbilanciamento=altezza/(Math.log(cromosoma.size())/0.6931471803);

	};
	
	/**
	 * Funzione interna per la ristrutturazione di un sottoalbero, viene invocata da tesi.models.Cromosoma.ristruttura()
	 * 
	 * @param base
	 * @return
	 */
	private int ristruttura(int base, int l) {
		//System.out.println(base);
		if(base>=cromosoma.size()){
			String trace=Thread.currentThread().getStackTrace()[1].toString();
			System.err.printf("Questo non dovrebbe succedere %s \n",trace);
			System.err.println(this.toYaml());
			//System.err.println("\n tutto questo bordello è stato creato dal crossover di questi due alberi");
			//System.err.println(LastParsedObjects.ricomponi(3));
			
		}
		if (Double.isNaN(cromosoma.elementAt(base).punto)) {			
			if(l>altezza)altezza=l;
			return base+1;
		} else {
			//if(base+1>=cromosoma.size())return base;
			int f = ristruttura(base+1,l+1);
			//System.out.printf("\t %d -> %d\n", base,f);
			cromosoma.elementAt(base).fine = f;
			return ristruttura(f,l+1);			
		}
	}

	/**
	 * Serializza in un CSV
	 * 
	 * @return
	 */
	public String toCsv() {
		StringBuffer sb = new StringBuffer();
		sb.append(Gene.csvHead);
		for (Gene g : cromosoma) {
			sb.append(g.toCsv());
			sb.append("\n");
		}

		return sb.toString();

	}

	public String toString() {
		StringBuffer sb = new StringBuffer();

		for (Gene g : cromosoma) {
			sb.append(g.toString());
			sb.append("\n");
		}

		return sb.toString();

	}

	/**
	 * Serializza in formato Yaml
	 * @return
	 */
	public String toYaml() {
		Yaml yaml = new Yaml();
		return yaml.dump(this);
	}
}
