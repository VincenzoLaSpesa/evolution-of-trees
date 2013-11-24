package tesi.views;

import java.util.Enumeration;
import java.util.Vector;

import tesi.models.Cromosoma;
import tesi.models.Gene;
import weka.core.Attribute;
import weka.core.Instances;

/**
 * Questa classe serve per produrre dei grafi Graphviz dai cromosomi utilizzando
 * invece che il numero delle colonne il nome delle colonne, per fare questo
 * servono informazioni che non sono contenute del Cromosoma stesso ( da qui il
 * nome che è anche un gioco di parole col fatto che vengono fuori dei grafi
 * "decorati")
 * 
 * @author darshan
 * 
 */
public class CromosomaDecorator {
	static final String[][] separatori = {{ "==", "!=" },{ "<=", ">" } };
	Vector<String> nomicolonne;
	Cromosoma cromosoma;

	public CromosomaDecorator(Cromosoma cromosoma, Vector<String> nomicolonne) {
		super();
		this.nomicolonne = nomicolonne;
		this.cromosoma = cromosoma;
	}

	public CromosomaDecorator(Cromosoma cromosoma) {
		super();
		this.cromosoma = cromosoma;
		nomicolonne = new Vector<String>();
	}

	@SuppressWarnings("unchecked")
	public void caricaColonne(Instances dataset) {
		Enumeration<Attribute> e = (Enumeration<Attribute>) dataset.enumerateAttributes();
		while (e.hasMoreElements()) {
			Attribute a = (Attribute) e.nextElement();
			nomicolonne.add(a.name());
		}
	}

	public StringBuilder getGraph() {
		int n = 0;
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("digraph %s_%d {\n", cromosoma.getClass().getName(), cromosoma.hashCode()));
		for (Gene g : cromosoma.cromosoma) {
			if (Double.isNaN(g.punto)) {
				sb.append(String.format("N%d [label=\"%d\" shape=box style=filled ]\n", n, g.attributo));
			} else {
				sb.append(String.format("N%d [label=\"%s\" ]\n", n, nomicolonne.get(g.attributo)));
				sb.append(String.format("N%d -> N%d [label=\"%s %.2f\" ]\n", n, n + 1,separatori[g.taglio.ordinal()][0], g.punto));
				sb.append(String.format("N%d -> N%d [label=\"%s %.2f\" ]\n", n, g.fine,	separatori[g.taglio.ordinal()][1], g.punto));
			}
			n++;
		}
		sb.append("}");
		return sb;
	};

}
