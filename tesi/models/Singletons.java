package tesi.models;

import tesi.util.logging.CromosomaStream;
import tesi.util.logging.FloatStream;

/**
 * Come suggerisce il nome questa classe è un ogetto Singleton, contiene due
 * FloatStream per accumulare prestazioni e pesi e un CromosomaStream
 * 
 * @author darshan
 * 
 */
public abstract class Singletons {
	public static FloatStream floatstream;
	public static FloatStream pesistream;
	public static CromosomaStream cromosomastream;

	static {
		floatstream = new FloatStream();
		pesistream = new FloatStream();
		cromosomastream = new CromosomaStream();
	}
}
