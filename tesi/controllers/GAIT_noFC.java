/**
 * 
 */
package tesi.controllers;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import tesi.models.Cromosoma;
import tesi.models.CromosomaMisurato;
import tesi.models.Popolazione;
import weka.core.Instances;

/**
 * @author darshan
 *
 */
public class GAIT_noFC extends Popolazione {

	public int limit;


	public GAIT_noFC(Instances testset, int nclassi) {
		super(testset, nclassi);
	}

	/* (non-Javadoc)
	 * @see tesi.models.Popolazione#get_fitness()
	 */
	@Override
	public double get_fitness() {
		
		TreeEvaluator te;
		Iterator<Cromosoma> i = popolazione_nonvalutata.iterator();
		double media=0;
		double a=1;
		while (i.hasNext()) {
			
			Cromosoma c = i.next();
			te= new TreeEvaluator(c, testset, nclassi);
			te.evaluate();
			CromosomaMisurato cm=new CromosomaMisurato(te.prestazioni, c);
			popolazione_valutata.add(cm);
			i.remove();
			media+=te.prestazioni;
			if(Double.isNaN(media)){
				System.out.println("trap!");
				
			}
			a++;
			//System.out.printf("\t%d:\t%f\n",a,te.prestazioni);
		}
		media=media/a;
		return media;
	}

	/**
	 * In GAIT la probabilità di crossover dipende unicamente dal fitness, si genera un numero [0 1] e se è minore del valore di fitness ( che è sempre [0 1]) l'elemento viene scelto
	 */
	public void crossover() {
		float f;
		LinkedList<Cromosoma> coppie= new LinkedList<>();
		//estraggo le coppie
		Iterator<CromosomaMisurato> entries = popolazione_valutata.iterator();
		while (entries.hasNext()) {
			f=r.nextFloat();
			CromosomaMisurato e = entries.next();
			if(f<e.prestazioni){
				coppie.add(e.cromosoma);
			}
		}
		//le mischio
		Collections.shuffle(coppie, r);
		//le faccio accoppiare
		Iterator<Cromosoma> i = coppie.iterator();
		int n=coppie.size();
		while (n>2 ) {
			Cromosoma c=GeneticOperator.crossover(i.next(), i.next(), false);
			System.out.print("c");
			n=n-2;			
			popolazione_nonvalutata.add(c);
		}		
		System.out.println("");

	}

	/* (non-Javadoc)
	 * @see tesi.models.Popolazione#mutate(float)
	 */
	@Override
	public void mutate(double probabilita) {
		float f;
		Cromosoma c;
		//
		Iterator<CromosomaMisurato> entries = popolazione_valutata.iterator();
		while (entries.hasNext()) {
			f=r.nextFloat();
			CromosomaMisurato e = entries.next();
			if(f<=probabilita){
				c=GeneticOperator.mutate(e.cromosoma, false);
				popolazione_nonvalutata.add(c);
			}
		}	

	}

	/* (non-Javadoc)
	 * @see tesi.models.Popolazione#evolvi()
	 */
	@Override
	public double evolvi() {
		double f,m;
		crossover();
		f=get_fitness();
		mutate(0.01);
		get_fitness();
		m=estrai_migliore();
		System.out.printf("\t La prestazione dei nuovi individui è %f\n\t la massima %f\n",f,m);
		System.out.printf("\t Ci sono %d = %d + %d elementi attivi\n",popolazione_nonvalutata.size()+popolazione_valutata.size(),popolazione_valutata.size(),popolazione_nonvalutata.size());
		trimtosize(limit);
		return m;

	}
	

		
	

	public Cromosoma GAIT(LinkedList<Cromosoma> popolazione_iniziale){
		this.popolazione_nonvalutata=popolazione_iniziale;
		limit=popolazione_nonvalutata.size();
		get_fitness();
		for(int i=0;i<10;i++){
			System.out.printf("Genero la generazione n %d, ci sono %d elementi\n", i, popolazione_valutata.size());
			evolvi();
			System.out.println("OK");
		}
		return bestcromosoma;
	}

	@Override
	@Deprecated
	/**
	 * In GAIT la probabilità di crossover dipende unicamente dal fitness, si genera un numero [0 1] e se è minore del valore di fitness ( che è sempre [0 1]) l'elemento viene scelto
	 */
	public void crossover(double probabilita) {
		crossover();
		
	}

}
