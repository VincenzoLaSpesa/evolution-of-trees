package tesi.models.popolazione;



import java.util.TreeSet;

import tesi.models.CromosomaMisurato;

public class PopolazioneOrdinata extends Popolazione {
	public PopolazioneOrdinata() {
		super();
		padri= new TreeSet<CromosomaMisurato>();
	}

	public void trimtosize(int size) {
		TreeSet<CromosomaMisurato> p=(TreeSet<CromosomaMisurato>)padri;
		int n = p.size() - size;
		while (n > 0) {
			p.pollFirst();
			n--;
		}
	}
	
	public Object[] padri_toArray(){
		return this.padri.toArray();
	}

	@Override
	public boolean sort() {
		return false;
	}

	@Override
	public CromosomaMisurato estraimigliore() {
		TreeSet<CromosomaMisurato> p=(TreeSet<CromosomaMisurato>)padri;
		return p.last();
	}

	@Override
	protected void aggiornamassimo(CromosomaMisurato c) {
		//non serve, è già ordinata.		
	}
	
	public int aggiungipadre(CromosomaMisurato c) {
		padri.add(c);
		//aggiornamassimo(c);
		return padri.size();
	};

	public int padrisize() {
		return padri.size();
	}

	@Override
	public int flush() {
		return this.padrisize();
	}




}
