package tesi.models;

import tesi.util.logging.CromosomaStream;
import tesi.util.logging.FloatStream;

public abstract class Singletons {
	public static FloatStream floatstream;
	public static FloatStream pesistream;
	public static CromosomaStream cromosomastream;
	
	static{
		floatstream= new FloatStream();
		pesistream= new FloatStream();
		cromosomastream= new CromosomaStream();
	}
}
