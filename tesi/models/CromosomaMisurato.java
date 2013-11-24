package tesi.models;

/**
 * Rappresenta un cromosoma insieme alle sue misure di prestazione e matrice di confusione
 * @author darshan
 *
 */
public class CromosomaMisurato implements Comparable<CromosomaMisurato> {
	@Override
	public String toString() {
		return "CromosomaMisurato [prestazioni=" + prestazioni + ", cromosoma=" + cromosoma + "]";
	}
	public CromosomaMisurato(double prestazioni, Cromosoma cromosoma) {
		super();
		this.prestazioni = prestazioni;
		this.cromosoma = cromosoma;
	}
	public double prestazioni;
	public double[][] confusionMatrix;
	public Cromosoma cromosoma;
	@Override
	public int compareTo(CromosomaMisurato o) {		
		if(this.prestazioni>o.prestazioni)return 3;
		if(this.prestazioni<o.prestazioni)return -3;
		if(this.cromosoma.cromosoma.size()>o.cromosoma.cromosoma.size())return -2;
		if(this.cromosoma.cromosoma.size()<o.cromosoma.cromosoma.size())return 2;
		int delta= this.hashCode()-o.hashCode();
		if(delta!=0)return delta;
		return 1;
	}

}
