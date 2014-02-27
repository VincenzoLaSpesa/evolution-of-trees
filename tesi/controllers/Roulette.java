package tesi.controllers;

import java.util.ArrayList;
import java.util.Arrays;

import tesi.util.SingletonGenerator;

public class Roulette {
	public double[] vettore;
	
	public Roulette(ArrayList<Double> frequenze){
		vettore= new double[frequenze.size()];
		int n=0;
		double acc=0;
		for(double f:frequenze){
			vettore[n]=acc+f;
			acc=vettore[n];
			n++;
		}
	}
	
	public Roulette(double[] frequenze) {
		vettore= new double[frequenze.length];
		int n=0;
		double acc=0;
		for(n=0;n<frequenze.length;n++){
			vettore[n]=acc+frequenze[n];
			acc=vettore[n];
		}
	}

	public int estrai(){
		double pallina=SingletonGenerator.r.nextDouble() * vettore[vettore.length-1];
		int i=Arrays.binarySearch(vettore, pallina);
		if(i<0)i=-i-1;
		return i;		
	}
}
