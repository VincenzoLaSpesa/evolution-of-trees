package tesi.main;



import tesi.models.Dataset;

public abstract class Funzione{

	public abstract String info();
	public abstract void call(Dataset d, int generazioni) throws Exception;
}
