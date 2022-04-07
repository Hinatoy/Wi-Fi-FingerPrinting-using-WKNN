
package application;
import java.util.List;
import java.util.Random;

// Java Random : http://www.fredosaurus.com/notes-java/summaries/summary-random.html

public class rss_formula {
	public static double getEuclidianDistance(List<Double> list, List<Integer> AP) {
		return Math.sqrt(
						 Math.pow(list.get(0)-AP.get(0), 2)
						 + Math.pow(list.get(1)-AP.get(1), 2)
					     ); 
	}
	public static double rss(List<Double> list, List<Integer> AP) {
		//double random_value = Math.random(); // uniform random
		
		Random r = new Random(); 
		double random_value = r.nextGaussian(); // mean: 0.0, std_dev: 1.0
		
//		double rss = getEuclidianDistance(list, AP);
		double rss = -40 - 20 * Math.log(getEuclidianDistance(list, AP) / 100);  
//					 + random_value / Configuration.RANDOMNESS_DIVIDER;
		
		if( Configuration.SHOW_RSS_DEBUG_MSG ) {
			System.out.printf("1/Euc: %+f\n", 1.0/getEuclidianDistance(list, AP));
			System.out.printf("Rand : %+f\n", random_value/Configuration.RANDOMNESS_DIVIDER);
		}
		
		return rss;
	}
}
