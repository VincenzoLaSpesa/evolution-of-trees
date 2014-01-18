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
		protected String colonna_corrente;
		
		public FloatStream() {
			colonna_corrente="default";
			Colonne=new TreeMap<String, LinkedList<Double>>();
			createColumn("default");
		}

		/**
		 * Converte la struttura in una stringa che rappresenta i dati in formato CSV <br><b>DISTRUGGE I DATI</b>
		 * @return
		 */
		public StringBuilder ricomponi() {
			StringBuilder sb = new StringBuilder();
			Set<String> kset=Colonne.keySet();
			Double v;
			int a = 0;
			//gli header
			for(String k : kset){
	            sb.append(k).append("\t");
	        }
			sb.append("\n");
			//i dati
			do{
				a=0;
				for(String k : kset){
					v=Colonne.get(k).pollFirst();
					if(v!=null){
						a++;
						sb.append(v).append("\t");
					}
				}
				sb.append("\n");
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
			Colonne.get(colonna_corrente).add(e);
			if (Colonne.get(colonna_corrente).size() > limit)
				Colonne.get(colonna_corrente).removeFirst();
			return Colonne.get(colonna_corrente).size();
		}
		
		public int appendDefault(Double e) {
			Colonne.get("default").add(e);
			if (Colonne.get("default").size() > limit)
				Colonne.get("default").removeFirst();
			return Colonne.get("default").size();
		}

		public int append(String colonna,Double e) {
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
		
}
