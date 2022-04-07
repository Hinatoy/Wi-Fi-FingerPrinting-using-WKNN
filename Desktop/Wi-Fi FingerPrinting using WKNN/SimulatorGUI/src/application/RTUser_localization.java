package application;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RTUser_localization {
	private final double INF = 100000;
	private List<Double> currentRSS = new ArrayList<Double>();
	public List<Double> currentLocation = new ArrayList<Double>();
	public List<Double> estimatedLocationKNN = new ArrayList<Double>();
	public List<Double> estimatedLocationWKNN = new ArrayList<Double>();
	//public List<Double> estimatedLocationWKNNRestr = new ArrayList<Double>();
	private List<List<Double>> coefficient = new ArrayList<List<Double>>();
	public Map<Double, String> nearestBlocks = new HashMap<Double, String>();
	//public Map<Double, String> nearestBlocksRestr = new HashMap<Double, String>();
	private Map<Double, String> WKNN = new HashMap<Double, String>();
	private location location = new location();
	public radio_map radio_map = new radio_map();
	
	
	RTUser_localization() {
		radio_map.initializeAPsLocation();
	}
	
	RTUser_localization(List<List<Integer>> locationOfAPs) {
		radio_map.iterateAPsLocation(locationOfAPs);
	}
	
	public void localization() {
		radio_map.mapping();
		currentLocation = location.randomizeLocation();
		System.out.println("Current location: " + currentLocation);
		for(int n = 0; n < Configuration.NUM_WIFIs; n++) {
			currentRSS.add(rss_formula.rss(currentLocation, radio_map.APs.locationOfAPs.get(n)));
		}
		
		for(int i = 0; i < Configuration.MAP_NUM_COLS; i++) {
			coefficient.add(new ArrayList<Double>());
			for(int j = 0; j < Configuration.MAP_NUM_ROWS; j++) {
				double tempCoefficient = 0;
				for(int n = 0; n < Configuration.NUM_WIFIs; n++) {
					tempCoefficient += Math.abs(radio_map.radioMap.get(i).get(j).get(n) - currentRSS.get(n));
				}
				coefficient.get(i).add(tempCoefficient);
			}
		}
	}
	
//	public boolean skip(int i, int j) { //not-on-the-same-line restriction
//		Map<Double, String> tempBlocks = new HashMap<Double, String>();
//		tempBlocks.putAll(nearestBlocks);
//		int counter = nearestBlocks.size();
//		//iterate through each block in nearestBlocks
//		for(Map.Entry<Double, String> first: tempBlocks.entrySet()) {
//			if(counter <= 1) break;
//			counter--;
//			String temp1 = first.getValue();
//			for(Map.Entry<Double, String> second: tempBlocks.entrySet()) {
//				String temp2 = second.getValue();
//				if(temp1 == temp2 || temp2 == null) continue;
//				int indexI1 = Integer.parseInt(temp1.substring(0, temp1.indexOf(',')));
//				int indexI2 = Integer.parseInt(temp2.substring(0, temp2.indexOf(',')));
//				int indexJ1 = Integer.parseInt(temp1.substring(temp1.indexOf(',') + 1));
//				int indexJ2 = Integer.parseInt(temp2.substring(temp2.indexOf(',') + 1));
//				//case 1: there are two repeating cell numbers (ex. {1, 2} and {1, 3})
//				if(indexI1 == indexI2) {
//					if(indexI1 == i) {
//						return true;
//					}
//				}
//				
//				// (ex. {3, 4} and {2, 4})
//				if(indexJ1 == indexJ2) {
//					if(indexJ1 == j) {
//						return true;
//					}
//				}
//				
//				//case 2: third point makes a descending or ascending line
//				//ascending
//				if(indexI1 + indexJ1 == indexI2 + indexJ2) {
//					if(j == indexI1 + indexJ1 - i) {
//						return true;
//					}
//				}
//				//descending
//				if(indexI1 - indexJ1 == indexI2 - indexJ2) {
//					if(j == Math.abs(indexI1 - indexJ1 - i)) {
//						return true;
//					}
//				}
//				
//				//case 3: a non-straightforward line
//				List<Double> firstLocation = location.generateLocation(indexI1, indexJ1);
//				List<Double> secondLocation = location.generateLocation(indexI2, indexJ2);
//				List<Double> currentCellLocation = location.generateLocation(i, j);
//				//formula: ax + by = c
//				double a, b, c = 0;
//				a = secondLocation.get(1) - firstLocation.get(1);
//				b = firstLocation.get(0) - secondLocation.get(0);
//				c = a * firstLocation.get(0) + b * firstLocation.get(1);
//				if(a * currentCellLocation.get(0) + b * currentCellLocation.get(1) == c) {
//					return true;
//				}
//				
//			}
//			tempBlocks.replace(first.getKey(), null);
//		}
//		return false;
//	}
	
	private void WKNN() {
		List<Double> tempLocation = new ArrayList<Double>();
		String tempBlock = "";
		double sumOfW = 0;
		double tempW = 0;
		
		for(Map.Entry<Double, String> entry: nearestBlocks.entrySet())
		sumOfW += 1.0 / entry.getKey();
		
		for(Map.Entry<Double, String> entry: nearestBlocks.entrySet()) {
			tempW = (1.0/entry.getKey()) / sumOfW;
			tempBlock = entry.getValue();
			
			int i = Integer.parseInt(tempBlock.substring(0, tempBlock.indexOf(',')));
			int j = Integer.parseInt(tempBlock.substring(tempBlock.indexOf(',') + 1));
			tempLocation = location.generateLocation(i, j);
			WKNN.put(tempW, tempLocation.get(0) + "," + tempLocation.get(1));
		}
		

		String tempPosition = "";
		double tempX = 0, tempY = 0;
		
		for(Map.Entry<Double, String> entry: WKNN.entrySet()) {
			tempW = entry.getKey();
			tempPosition = entry.getValue();
			double positionX = Double.parseDouble(tempPosition.substring(0, tempPosition.indexOf(',')));
			double positionY = Double.parseDouble(tempPosition.substring(tempPosition.indexOf(',') + 1));
			tempX += tempW * positionX;
			tempY += tempW * positionY;
		}
		estimatedLocationWKNN.add(tempX);
		estimatedLocationWKNN.add(tempY);
		System.out.println("Estimated location: " + estimatedLocationWKNN);
	}
	
	private void KNN() {
		List<Double> tempLocation = new ArrayList<Double>();
		String tempBlock = "";
		double tempX = 0, tempY = 0;
		for(Map.Entry<Double, String> entry: nearestBlocks.entrySet()) {
			tempBlock = entry.getValue();
			int i = Integer.parseInt(tempBlock.substring(0, tempBlock.indexOf(',')));
			int j = Integer.parseInt(tempBlock.substring(tempBlock.indexOf(',') + 1));
			tempLocation = location.generateLocation(i, j);
			tempX += tempLocation.get(0);
			tempY += tempLocation.get(1);
		}
		estimatedLocationKNN.add(tempX / Configuration.K);
		estimatedLocationKNN.add(tempY / Configuration.K);
		System.out.println("Estimated location: " + estimatedLocationKNN);
	}
	
	private void chooseClosestBlocks(Map<Double, String> nearestBlocks) {
		for(int k = 0; k < Configuration.K; k++) {
			double min = INF;
			int indexI = 0;
			int indexJ = 0;
			
			for(int i = 0; i < Configuration.MAP_NUM_COLS; i++) {
				for(int j = 0; j < Configuration.MAP_NUM_ROWS; j++) {
					if(min > coefficient.get(i).get(j)) {
						//check if on the same line if needed
//						if(skip) {
//							if (nearestBlocks.size() >= 2) {
//								if(skip(i, j)) continue;
//							}
//						}
						min = coefficient.get(i).get(j);
						indexI = i;
						indexJ = j;
					}
				}
			}
			nearestBlocks.put(min, indexI + "," + indexJ);
			coefficient.get(indexI).set(indexJ, INF);
		}
		
		System.out.println("Nearest Blocks: " + nearestBlocks);
	}
	
	public void locAlgorithm() {
		//finding closest blocks
		//if(Configuration.SKIP_SOME_POINTS_ON_THE_LINE) chooseClosestBlocks(nearestBlocksRestr, true);
		chooseClosestBlocks(nearestBlocks);
		
		//estimate the position using WKNN
		if(Configuration.W_ON) {
			WKNN();
		}
		
		//estimate the position using WKNN with restrictions
//		if(Configuration.SKIP_SOME_POINTS_ON_THE_LINE) {
//			estimatedLocationWKNNRestr = WKNN(nearestBlocksRestr);
//		}
		
		//estimate the position using KNN
		if(!Configuration.W_ON) {
			KNN();
		}
	}
	
	public void print() {
		location.printLocation(currentLocation);
		
		System.out.println("\nCurrent RSS: ");
		for(int i = 0; i < currentRSS.size(); i++) {
			System.out.printf("%-15f", currentRSS.get(i));
		} System.out.println("\n\n\n");
		
		System.out.println("\nCoefficients corresponding to each block:");
		for(int i = 0; i < Configuration.MAP_NUM_ROWS; i++) {
			for(int j = 0; j < Configuration.MAP_NUM_COLS; j++) {
				System.out.printf("%-15f", coefficient.get(i).get(j));
			}
			System.out.println();
		}
	}
	
}
