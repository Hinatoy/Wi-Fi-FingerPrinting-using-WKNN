package application;
import java.util.ArrayList;
import java.util.List;

public class location {
	
	public static List<Double> generateLocation(int i, int j) {
		List<Double> location = new ArrayList<Double>();
		
		// generate the middle point of a block at j-row and i-column
		location.add(i * Configuration.MAP_WIDTH_IN_CM / Configuration.MAP_NUM_COLS + Configuration.MAP_WIDTH_IN_CM / Configuration.MAP_NUM_COLS / 2);
		location.add(j * Configuration.MAP_HEIGHT_IN_CM / Configuration.MAP_NUM_ROWS + Configuration.MAP_HEIGHT_IN_CM / Configuration.MAP_NUM_ROWS / 2);
		
		return location;
	}
	
	public static List<Double> randomizeLocation() {
		List<Double> location = new ArrayList<Double>();
		location.add((double)(int)(Math.random()*(Configuration.MAP_WIDTH_IN_CM)));
		location.add((double)(int)(Math.random()*(Configuration.MAP_HEIGHT_IN_CM)));
		return location;
	}
	
	public static void printLocation(List<Double> location) {
		System.out.println("Currect location:");
		System.out.println(location.get(0));
		System.out.println(location.get(1));
		System.out.println();
	}

}
