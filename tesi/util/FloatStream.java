package tesi.util;

import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;

/**
 * Permette di accumulare delle liste di float e poi di stamparle in un file csv, 
 * è concepito come un ogetto singleton accessibile dall'intera applicazione
 * @author darshan
 *	TODO: testare questa classe.
 */
public abstract class FloatStream {
		public static TreeMap<String, LinkedList<Double>> Colonne;
		public static int limit = 1024;
		static {
			Colonne=new TreeMap<String, LinkedList<Double>>();
			Colonne.put("default", new LinkedList<Double>());
		}

		public static StringBuilder ricomponi(int profondita) {
			StringBuilder sb = new StringBuilder();
			Set<String> kset=Colonne.keySet();
			int a = 0;
			//gli header
			for(String k : kset){
	            sb.append(k).append("\t");
	        }
			sb.append("\n");
			//i dati
			do{
				for(String k : kset){

				}
				sb.append("\n");
			}while(a>0);
			return sb;

		}

		public static int push(Double e) {
			Colonne.get("default").push(e);
			if (Colonne.get("default").size() > limit)
				Colonne.get("default").removeLast();
			return Colonne.get("default").size();
		}
}
