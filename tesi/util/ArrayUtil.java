package tesi.util;

import java.util.Random;

/**
 * Raccolta di funzioni statiche per lavorare con i vettori di numeri interi
 * @author darshan
 */
public abstract class ArrayUtil {

    /**
     * Riempie un vettore di numeri casuali interi compresi tra 0 e max
     * @param v
     * @param max
     */
	public static void vector_fill(int v[], int max) {
        int a;
        int n=v.length;
        Random r = new Random(System.currentTimeMillis());
        for (a = 0; a < n; a++) {
            v[a] = r.nextInt(max);
        }
    }

	public static void vector_fill_sbilanciata(int v[], int max) {
        int a;
        int n=v.length;
        Random r = new Random();
        for (a = 0; a < n; a++) {
            v[a] = r.nextInt(max - (a % max));
        }
    }

    public static void permuta_ricorsivo(int[] elementi, int da, int a) {
        int k;
        if (a > da) {
            for (k = a; k >= da; k--) {
                ArrayUtil.swap(elementi, k, a);
                permuta_ricorsivo(elementi, da, a - 1);
                ArrayUtil.swap(elementi, k, a);
            }
        } else {
            System.out.println(ArrayUtil.dump(elementi));
        }
    }

    public static void permuta_ricorsivo(int[] elementi) {
        permuta_ricorsivo(elementi, 0, elementi.length - 1);
    }

    /**
     * cerca in vettori non ordinati la prima occorrenza di "e"
     * @param v
     * @param e
     * @return la prima occorrenza o -1 se l'elemento non è presente nel vettore
     */
    public static int firstindexof(int[] v, int e) {
        for (int n = 0; n < v.length; n++) {
            if (v[n] == e) {
                return n;
            }
        }
        return -1;
    }

    public static int[] swap(int[] v, int a, int b) {
        int t = v[a];
        v[a] = v[b];
        v[b] = t;
        return v;
    }

    /**
     * mischia il vettore in tempo O(n)
     * @param elementi il vettore
     * @return il vettore disordinato
     */
    public static int[] shuffle(int[] elementi) {
        Random r = new Random();
        int k, n;
        for (n = elementi.length - 1; n >= 0; n--) {
            k = r.nextInt(n + 1);
            ArrayUtil.swap(elementi, k, n);
        }
        return elementi;
    }

    public static String dump(int[] elementi) {
        return dump(elementi, 0);
    }

    public static String dump(int[] elementi, int base) {
        StringBuilder e = new StringBuilder();

        if (elementi.length > base) {
            e.append("[ ").append(elementi[base]);

            for (int i = 1 + base; i < elementi.length; i++) {
                e.append(",");
                e.append(elementi[i]);
            }
        }
        e.append(" ]");
        return e.toString();
    }

    public static String dump(int[][] elementi) {
        int i, k, n, s;
        StringBuilder e = new StringBuilder();
        //String coso;
        if (elementi.length > 0) {
            e.append("[ \n");

            for (i = 0; i < elementi.length; i++) {
                e.append("[ ");
                for (k = 0; k < elementi[i].length; k++) {
                    s = (int) Math.log10(elementi[i][k] + 1);
                    e.append(elementi[i][k]);
                    for (n = (5 - s); n > 0; n--) {
                        e.append(" ");
                    }
                    //e.append(",");
                }
                e.append("] \n");

            }
        }
        e.append("]");
        return e.toString();
    }
    public static String dump(double[][] elementi) {
        int i, k, n, s;
        StringBuilder e = new StringBuilder();
        String coso;
        if (elementi.length > 0) {
            e.append("[ \n");

            for (i = 0; i < elementi.length; i++) {
                e.append("[ ");
                for (k = 0; k < elementi[i].length; k++) {
                	coso=String.format("%.5f", elementi[i][k]);
                	s = coso.length();
                    e.append(coso);
                    for (n = (10 - s); n > 0; n--) {
                        e.append(" ");
                    }
                    //e.append(",");
                }
                e.append("] \n");

            }
        }
        e.append("]");
        return e.toString();
    }

}
