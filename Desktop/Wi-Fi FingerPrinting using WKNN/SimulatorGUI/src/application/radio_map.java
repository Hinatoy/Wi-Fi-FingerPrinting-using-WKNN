package application;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class radio_map {
	public List<List<List<Double>>> radioMap = new ArrayList<List<List<Double>>>();
	public APs APs = new APs();
	
	private void mapInitialization() {
		for(int i = 0; i < Configuration.MAP_NUM_COLS; i++) {
			radioMap.add(new ArrayList<List<Double>>());
			for(int j = 0; j < Configuration.MAP_NUM_ROWS; j++) {
				radioMap.get(i).add(new ArrayList<Double>());
			}
		}
	}
	
	public void initializeAPsLocation() {
		APs.randomizeAPs();
	}
	
	public void iterateAPsLocation(List<List<Integer>> locationOfAPs) {
		APs.setAPs(locationOfAPs);
	}
	
	public void mapping() {
		List<Double> median = new ArrayList<Double>();
		
		mapInitialization();
		for(int i = 0; i < Configuration.MAP_NUM_COLS; i++) {
			for(int j = 0; j < Configuration.MAP_NUM_ROWS; j++) {
				for(int n = 0; n < Configuration.NUM_WIFIs; n++) {
					for(int l = 0; l < Configuration.numberOfMeasurements; l++) {
						median.add(rss_formula.rss(location.generateLocation(i, j), APs.locationOfAP(n)));
					}
					median = median.stream().sorted().collect(Collectors.toList());
					double tempRSS = 0;
					if(median.size() % 2 == 0) 
						tempRSS = (median.get((median.size()-1)/2) + median.get(median.size()/2))/2;
					else tempRSS = median.get(median.size()/2);
					radioMap.get(i).get(j).add(tempRSS);
					median.clear();
				}
			}
		}
	}
	
	public void print() {
		APs.print();
		for(int i = 0; i < radioMap.size(); i++) {
			for(int j = 0; j < radioMap.get(i).size(); j++) {
				System.out.println("I: " + i + " J: " + j + "\n " + radioMap.get(i).get(j));
			}
		}
	}
}

