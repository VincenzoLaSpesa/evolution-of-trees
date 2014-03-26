package tesi.main;

import java.io.FileReader;

import tesi.interfaces.launchers.AlgoritmoEvolutivoCustomTorneo;
import tesi.models.Dataset;
import tesi.util.SysUtil;
import tesi.util.logging.FloatStream;
import tesi.util.logging.Singletons;
import weka.core.Instances;

public abstract class Torneo {

	public static void torneo_multi(Dataset d, Integer generazioni) throws Exception {
		AlgoritmoEvolutivoCustomTorneo gaitrunner = new AlgoritmoEvolutivoCustomTorneo(d, generazioni, Settings.popolazione_size,false,Settings.albero_size);
		gaitrunner.begin();
	}

	public static void torneo_Multi_Benchmark(Integer generazioni) throws Exception {
		
		System.out.flush();
		System.out.println(SysUtil.getMethodName(1));
		
		FileReader dataset_stream = new FileReader(Settings.dataset_url);
		Instances dataset = new Instances(dataset_stream);
		//int generazioni = 25;
		Dataset d;
		for (int a = 0; a < Settings.istanze; a++) {
			String nomecolonna = "istanza_" + a;
			d = new Dataset(dataset, Settings.percentualetrainingset, Settings.percentualescoringset, Settings.percentualetestset);
			Singletons.cromosomastream.createColumn(nomecolonna);
			Singletons.cromosomastream.setColonna_corrente(nomecolonna);
			Singletons.pesistream.createColumn(nomecolonna);
			Singletons.pesistream.setColonna_corrente(nomecolonna);
			torneo_multi(d, generazioni);
			FloatStream ft=Singletons.cromosomastream.calcola(d.testset, d.nclassi);
			Singletons.floatstream.merge(ft);
			Singletons.cromosomastream.deleteColumn(nomecolonna);
		}
		System.out.println(Singletons.floatstream.ricomponi().toString());
		System.out.println(Singletons.pesistream.ricomponi().toString());				
	}

	public static void torneo_multi_mutante(Dataset d, Integer generazioni) throws Exception {
		AlgoritmoEvolutivoCustomTorneo gaitrunner = new AlgoritmoEvolutivoCustomTorneo(d, generazioni, Settings.popolazione_size,true,Settings.albero_size);
		gaitrunner.begin();
	}

	public static void torneo_MultiMutante_Benchmark(Integer generazioni) throws Exception {
		
		System.out.flush();
		System.out.println(SysUtil.getMethodName(1));
		
		FileReader dataset_stream = new FileReader(Settings.dataset_url);
		Instances dataset = new Instances(dataset_stream);
		//int generazioni = 25;
		Dataset d;
		for (int a = 0; a < Settings.istanze; a++) {
			String nomecolonna = "istanza_" + a;
			d = new Dataset(dataset, Settings.percentualetrainingset, Settings.percentualescoringset, Settings.percentualetestset);
			Singletons.cromosomastream.createColumn(nomecolonna);
			Singletons.cromosomastream.setColonna_corrente(nomecolonna);
			Singletons.pesistream.createColumn(nomecolonna);
			Singletons.pesistream.setColonna_corrente(nomecolonna);
			torneo_multi_mutante(d, generazioni);
			FloatStream ft=Singletons.cromosomastream.calcola(d.testset, d.nclassi);
			Singletons.floatstream.merge(ft);
			Singletons.cromosomastream.deleteColumn(nomecolonna);
		}
		System.out.println(Singletons.floatstream.ricomponi().toString());
		System.out.println(Singletons.pesistream.ricomponi().toString());				
	}

}
