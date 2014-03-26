package tesi.main;

public abstract class Settings {

	public static final String parkingson="/home/darshan/Uni/Tesi/tesi/Tesi/dataset/parkingsons/parkinsons.arff";
	public static final String adult = "/home/darshan/Uni/Tesi/tesi/Tesi/dataset/completedataset.arff";
	public static final String breast = "/home/darshan/Uni/Tesi/tesi/Tesi/dataset/breast-cancer/breast-cancer-wisconsin.arff";
	public static final String heart = "/home/darshan/Uni/Tesi/tesi/Tesi/dataset/Statlog (Heart) Data Set /heart.arff";
	public static final String covtype = "/home/darshan/Desktop/Università/Tesi/tesi/Tesi/dataset/covtype/covtype.data.arff";
	//
	public static String dataset_url = "../dataset/completedataset.arff";
	public static String dataset_url_fallback = covtype;
	public static int popolazione_size=-1;//se negativo lo autodimensiona 
	public static int istanze=30;
	public static int albero_size=100; // se negativo lo autodimensiona
	public static String base;
	public static String pattern;
	public static String albero;
	public static double percentualetrainingset=0.5454545454;
	public static double percentualescoringset=0.1818181818;
	public static double percentualetestset= 0.2727272727;
	public static boolean albericolorati=false;//gestisce l'output degli alberi di bloat
}
