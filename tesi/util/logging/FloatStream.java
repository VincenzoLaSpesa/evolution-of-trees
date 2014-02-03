package tesi.util.logging;

import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;

/**
 * Permette di accumulare dei float da varie parti del codice per poi poterli recuperare in un altro punto
 * @author darshan
 */
public class FloatStream {
		public static int limit = 1024;
		protected TreeMap<String, LinkedList<Double>> Colonne;
		protected double[] medie;
		protected String colonna_corrente;
		public boolean active=true;
		
		public FloatStream() {
			colonna_corrente="default";
			Colonne=new TreeMap<String, LinkedList<Double>>();
			//createColumn("default");
		}

		/**
		 * Converte la struttura in una stringa che rappresenta i dati in formato CSV <br><b>DISTRUGGE I DATI</b>
		 * @return
		 */
		public StringBuilder ricomponi() {
			
			this.calcolamedie();
			StringBuilder sb = new StringBuilder();
			Set<String> kset=Colonne.keySet();
			Double v;
			int a = 0;
			//gli header
			for(String k : kset){
				//Evito di stampare la colonna di Default se è vuota
	            if(Colonne.get(k).size()>0)sb.append(k).append("\t");
	        }
			sb.append("medie\n");
			//i dati
			int j=0;
			do{
				a=0;
				for(String k : kset){
					v=Colonne.get(k).pollFirst();
					if(v!=null){
						a++;
						sb.append(v).append("\t");
					}
				}
				if(j<medie.length)sb.append(medie[j]);
				sb.append("\n");
				j++;
			}while(a>0);
			return sb;

		}
		
		/**
		 * Aggiunge gli elementi di ft nell'oggetto corrente, le colonne <b>VENGONO SOVRASCRITTE</B>
		 * @param ft
		 */
		public void merge(FloatStream ft){
			Set<String> kset = ft.Colonne.keySet();
			// i dati
			for (String k : kset) {
				this.createColumn(k);
				this.setColonna_corrente(k);
				LinkedList<Double> L = ft.Colonne.get(k);
				for (Double d : L) {
					this.append(d);
				}
			}
		};

		public int append(Double e) {
			if(!active)return -1;
			Colonne.get(colonna_corrente).add(e);
			if (Colonne.get(colonna_corrente).size() > limit)
				Colonne.get(colonna_corrente).removeFirst();
			return Colonne.get(colonna_corrente).size();
		}
		
		public int appendDefault(Double e) {
			if(!active)return -1;
			Colonne.get("default").add(e);
			if (Colonne.get("default").size() > limit)
				Colonne.get("default").removeFirst();
			return Colonne.get("default").size();
		}

		public int append(String colonna,Double e) {
			if(!active)return -1;
			Colonne.get(colonna).add(e);
			if (Colonne.get(colonna).size() > limit)
				Colonne.get(colonna).removeFirst();
			return Colonne.get(colonna).size();
		}

		/**
		 * Crea una nuova colonna, <b>se la colonna esiste la sovrascrive</b>
		 * @param k
		 * @return
		 */
		public int createColumn(String k){
			Colonne.put(k, new LinkedList<Double>());
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
		
		public void calcolamedie(){
			int j=0;	
			int J=Colonne.lastEntry().getValue().size();
			int N=0;
			medie= new double[J];
			Set<String> kset=Colonne.keySet();
			for(String k : kset){
				LinkedList<Double> L=Colonne.get(k);
				if(L.size() <1)continue;
				N++;
				j=0;
				for(double v : L){
					medie[j]+=+v;
					//if(v>0)N++; 
					j++;
				}								
			}
			//N=N/J;
			for(j=0;j<J;j++){
				medie[j]=medie[j]/N;
			}
			
		}
		
}
