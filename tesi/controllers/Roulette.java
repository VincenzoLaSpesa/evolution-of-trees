package tesi.controllers;

import java.util.ArrayList;
import java.util.Arrays;

import tesi.util.SingletonGenerator;

public class Roulette {
	public double[] vettore;
	
	public Roulette(ArrayList<Double> frequenze){
		vettore= new double[frequenze.size()-1];
		int n=0;
		double acc=0;
		for(double f:frequenze){
			vettore[n]=acc+f;
			acc=vettore[n];
		}
	}
	
	public int estrai(){
		double pallina=SingletonGenerator.r.nextDouble() * vettore[vettore.length-1];
		return Arrays.binarySearch(vettore, pallina);		
	}
}
