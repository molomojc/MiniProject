package application.server;

/**
 * 
 * This will store the 3by3 GreyScale patch
 */

//e.g
/**
 * 1 2 3 4 5 
 * 6 7 8 9 10  converts to [1 2 3 4 5 6 7 8 9 10]
 * 11 12 13 14 
 */

public class Patch {
	
	int[] featureVector; //Will store the pixels as 1D arrau=y
	 int classify; //This classofies it 
	
	public Patch(int[] featureVector, int classify) {
		this.featureVector = featureVector;
		this.classify = classify;
	}
}
