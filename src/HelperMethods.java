
public class HelperMethods {
	public static double getAverage (double array[]){
		double sum = 0;
		for (int index = 0; index < array.length; index++)
			sum += array[index];
		return (sum/array.length);
	}
}
