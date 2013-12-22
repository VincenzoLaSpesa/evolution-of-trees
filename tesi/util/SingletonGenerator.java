package tesi.util;

import java.util.Random;

/**
 * Uso un singolo generatore di numeri casuali nell'intero progetto così posso controllare le sequenze e ripeterle
 * @author darshan
 *
 */
public abstract class SingletonGenerator {	
	public static Random r;
	static{
		r= new Random();
	}
	
	public static synchronized void set_seed(long seed){
		r= new Random(seed);
	}	
}
