package tesi.util.logging;

import java.util.LinkedList;
import java.util.TreeMap;

import tesi.models.Cromosoma;

/**
 * Permette di accumulare dei cromosomi per valutarli in seguito, 
 * è concepito come un ogetto singleton accessibile dall'intera applicazione
 * @author darshan
 */
public abstract class CromosomaStream {
		public static int limit = 1024;
		protected static TreeMap<String, LinkedList<Cromosoma>> Colonne;
		protected static String colonna_corrente;
		
		static {
			colonna_corrente="default";
			Colonne=new TreeMap<String, LinkedList<Cromosoma>>();
			createColumn("default");
		}

		/*public static StringBuilder ricomponi() {
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
					v=Colonne.get(k).pollLast();
					if(v!=null){
						a++;
						sb.append(v).append("\t");
					}
				}
				sb.append("\n");
			}while(a>0);
			return sb;

		}*/

		public static int push(Cromosoma e) {
			Colonne.get(colonna_corrente).push(e.clone());
			if (Colonne.get(colonna_corrente).size() > limit)
				Colonne.get(colonna_corrente).removeLast();
			return Colonne.get(colonna_corrente).size();
		}
		
		public static int pushDefault(Cromosoma e) {
			Colonne.get("default").push(e.clone());
			if (Colonne.get("default").size() > limit)
				Colonne.get("default").removeLast();
			return Colonne.get("default").size();
		}

		public static int push(String colonna,Cromosoma e) {
			Colonne.get(colonna).push(e.clone());
			if (Colonne.get(colonna).size() > limit)
				Colonne.get(colonna).removeLast();
			return Colonne.get(colonna).size();
		}

		/**
		 * Crea una nuova colonna, <b>se la colonna esiste la sovrascrive</b>
		 * @param k
		 * @return
		 */
		public static int createColumn(String k){
			Colonne.put(k, new LinkedList<Cromosoma>());
			return Colonne.size();
		}

		public static String getColonna_corrente() {
			return colonna_corrente;
		}

		public static void setColonna_corrente(String colonna_corrente) {
			CromosomaStream.colonna_corrente = colonna_corrente;
		}
		
}
