package tesi.util.logging;

import java.util.LinkedList;

/**
 * E' una lista a scorrimento che contiene stringhe, viene usata per facilitare
 * il debug delle funzioni ricorsive salvando le serializzazioni testuali degli
 * ogetti che vengono elaborati da classi diverse
 * 
 * @author darshan
 * 
 */
public abstract class LastParsedObjects {
	public static LinkedList<String> oggetti;
	public static int limit = 15;
	static {
		oggetti = new LinkedList<String>();

	}

	public static String ricomponi(int profondita) {
		StringBuilder sb = new StringBuilder();

		int a = 0;
		while (a < profondita && !oggetti.isEmpty()) {
			sb.append(oggetti.pop()).append("\n");
			a++;
		}
		return sb.toString();

	}

	public static int push(String e) {
		oggetti.push(e);
		if (oggetti.size() > limit)
			oggetti.removeLast();
		return oggetti.size();
	}
}
