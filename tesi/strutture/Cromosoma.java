package tesi.strutture;

import java.io.Serializable;
import java.io.StringReader;
import java.util.Vector;

import org.yaml.snakeyaml.Yaml;

import weka.classifiers.trees.J48;
import weka.classifiers.trees.j48.BinC45Split;
import weka.classifiers.trees.j48.ClassifierTree;

/**
 * Rappresenta un singolo albero di decisione linearizzato in un cromosoma, la linearizzazione avviene con una visita per profondità (DFS).
 * Il vantaggio di questa linearizzazione è che <i> ogni sottoalbero è rappresentato da una stringa contigua</i>
 * @author darshan
 *
 */
public class Cromosoma implements Serializable  {

	private static final long serialVersionUID = 346117818222219309L;
	public Vector<Gene> cromosoma;
	
	/**
	 * Deserializza da un weka.classifiers.trees.J48 generato da Weka, l'albero deve essere binario.
	 * @param j48
	 * @return
	 * @throws Exception
	 */
	public static Cromosoma loadFromJ48( J48 j48) throws Exception {
		if(!j48.getBinarySplits()){
			Exception e= new Exception("L'albero non è binario!");
			throw e; 
		}
		ClassifierTree tree=j48.getTree();
		Cromosoma c=new Cromosoma();
		c.parse(tree);
		return c;
	}
	
	/**
	 * Deserializza da un dump Yaml
	 * @param Yaml_str
	 * @return
	 */
	public static Cromosoma loadFromYaml(String Yaml_str){
		Yaml y=new Yaml();
		StringReader r= new StringReader(Yaml_str);
		Cromosoma c=(Cromosoma) y.load(r);
		return c;
	}
	
	/**
	 * Ricostruisce ricorsivamente un sottoalbero, viene utilizzata dal costruttore statico loadFromJ48.
	 * @param sottoalbero
	 * @throws Exception
	 */
	private void parse(ClassifierTree sottoalbero) throws Exception{
		Gene g= new Gene();
		if(sottoalbero.m_isLeaf){//foglia
			g.attributo=Integer.parseInt(sottoalbero.m_localModel.dumpLabel(0,sottoalbero.m_train).split(" ")[0]);
			g.punto=Float.NaN;
			cromosoma.add(g);
		}else{//ramo
			BinC45Split splitter=(BinC45Split)sottoalbero.m_localModel;
			g.attributo=splitter.attIndex();
			g.punto=splitter.m_splitPoint;

			if(sottoalbero.m_train.attribute(g.attributo).isNominal())
				g.taglio=Taglio.Categorico;
			else 
				g.taglio=Taglio.Continuo;			
			cromosoma.add(g);
			parse(sottoalbero.m_sons[0]);
			parse(sottoalbero.m_sons[1]);
		}
	};
	
	public Cromosoma(){
		cromosoma=new Vector<>();
	}
	
	public String toString(){
		StringBuffer sb= new StringBuffer();
		
		for (Gene g : cromosoma) {
			sb.append(g.toString());
			sb.append("\n");
		}
		
		return sb.toString();
		
	}
	
	public String toYaml(){
		Yaml yaml = new Yaml();
		return yaml.dump(this);		
	}
}
