package application;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RTUser_localization {
	private final double INF = 100000;
	private List<Double> currentRSS = new ArrayList<Double>();
	public List<Integer> currentLocation = new ArrayList<Integer>();
	public List<Double> estimatedLocation = new ArrayList<Double>();
	private List<List<Double>> coefficient = new ArrayList<List<Double>>();
	public Map<Double, String> nearestBlocks = new HashMap<Double, String>();
	public Map<Double, String> nearestBlocksWORestr = new HashMap<Double, String>();
	private Map<Double, String> WKNN = new HashMap<Double, String>();
	private location location = new location();
	public radio_map radio_map = new radio_map();
	private List<String> permutations = new ArrayList<String>();
	
	
	RTUser_localization() {
		radio_map.initializeAPsLocation();
	}
	
	RTUser_localization(List<List<Integer>> locationOfAPs) {
		radio_map.iterateAPsLocation(locationOfAPs);
	}
	
	public void localization() {
		radio_map.mapping();
		currentLocation = location.randomizeLocation();
		for(int n = 0; n < Configuration.NUM_WIFIs; n++) {
			currentRSS.add(rss_formula.rss(currentLocation, radio_map.APs.locationOfAPs.get(n)));
		}
		
		for(int i = 0; i < Configuration.MAP_NUM_ROWS; i++) {
			for(int j = 0; j < Configuration.MAP_NUM_COLS; j++) {
				coefficient.add(new ArrayList<Double>());
				double tempCoefficient = 0;
				for(int n = 0; n < Configuration.NUM_WIFIs; n++) {
					tempCoefficient += Math.abs(radio_map.radioMap.get(j).get(i).get(n) - currentRSS.get(n)); 
				}
				coefficient.get(i).add(tempCoefficient);
			}
		}
	}
	
	public boolean skip(int i, int j) {
		Map<Double, String> tempBlocks = new HashMap<Double, String>();
		tempBlocks.putAll(nearestBlocks);
		int counter = nearestBlocks.size();
		//iterate through each block in nearestBlocks
		for(Map.Entry<Double, String> first: tempBlocks.entrySet()) {
			if(counter <= 1) break;
			counter--;
			String temp1 = first.getValue();
			for(Map.Entry<Double, String> second: tempBlocks.entrySet()) {
				String temp2 = second.getValue();
				if(temp1 == temp2 || temp2 == null) continue;
				int indexI1 = Integer.parseInt(temp1.substring(0, temp1.indexOf(',')));
				int indexI2 = Integer.parseInt(temp2.substring(0, temp2.indexOf(',')));
				int indexJ1 = Integer.parseInt(temp1.substring(temp1.indexOf(',') + 1));
				int indexJ2 = Integer.parseInt(temp2.substring(temp2.indexOf(',') + 1));
				//case 1: there are two repeating cell numbers (ex. {1, 2} and {1, 3})
				if(indexI1 == indexI2) {
					if(indexI1 == i) {
						return true;
					}
				}
				
				// (ex. {3, 4} and {2, 4})
				if(indexJ1 == indexJ2) {
					if(indexJ1 == j) {
						return true;
					}
				}
				
				//case 2: third point makes a descending or ascending line
				//ascending
				if(indexI1 + indexJ1 == indexI2 + indexJ2) {
					if(j == indexI1 + indexJ1 - i) {
						return true;
					}
				}
				//descending
				if(indexI1 - indexJ1 == indexI2 - indexJ2) {
					if(j == Math.abs(indexI1 - indexJ1 - i)) {
						return true;
					}
				}
				
				//case 3: a non-straightforward line
				List<Integer> firstLocation = location.generateLocation(indexI1, indexJ1);
				List<Integer> secondLocation = location.generateLocation(indexI2, indexJ2);
				List<Integer> currentCellLocation = location.generateLocation(i, j);
				//formula: ax + by = c
				int a, b, c = 0;
				a = secondLocation.get(1) - firstLocation.get(1);
				b = firstLocation.get(0) - secondLocation.get(0);
				c = a * firstLocation.get(0) + b * firstLocation.get(1);
				if(a * currentCellLocation.get(0) + b * currentCellLocation.get(1) == c) {
					return true;
				}
				
			}
			tempBlocks.replace(first.getKey(), null);
		}
		return false;
	}
	
	public void WKNN() {
		//finding closest blocks
		for(int k = 0; k < Configuration.K; k++) {
			double min = INF;
			double minWORestr = INF;
			int indexI = 0;
			int indexIWORest = 0;
			int indexJ = 0;
			int indexJWORest = 0;
			for(int i = 0; i < Configuration.MAP_NUM_ROWS; i++) {
				for(int j = 0; j < Configuration.MAP_NUM_COLS; j++) {
					if(min > coefficient.get(i).get(j)) {
						minWORestr = coefficient.get(i).get(j);
						indexIWORest = i;
						indexJWORest = j;
						//check if on the same line
						if(Configuration.SKIP_SOME_POINTS_ON_THE_LINE) {
							if (nearestBlocks.size() >= 2) {
								if(skip(i, j)) continue;
							}
						}
						min = coefficient.get(i).get(j);
						indexI = i;
						indexJ = j;
					}
				}
			}
			nearestBlocks.put(min, indexI + "," + indexJ);
			nearestBlocksWORestr.put(minWORestr, indexIWORest + "," + indexJWORest);
			coefficient.get(indexI).set(indexJ, INF);
			coefficient.get(indexIWORest).set(indexJWORest, INF);
			
		}
		
//		System.out.println("\nClosest blocks:");
//		System.out.println(nearestBlocks);
		
		//estimate the position
		double sumOfW = 0;
		double tempW = 0;
		String tempBlock = "";
		List<Integer> tempLocation = new ArrayList<Integer>();
		
		for(Map.Entry<Double, String> entry: nearestBlocks.entrySet())
		sumOfW += 1.0 / entry.getKey();
		
		for(Map.Entry<Double, String> entry: nearestBlocks.entrySet()) {
			tempW = (1.0/entry.getKey()) / sumOfW;
			tempBlock = entry.getValue();
			
			int x = Integer.parseInt(tempBlock.substring(0, tempBlock.indexOf(',')));
			int y = Integer.parseInt(tempBlock.substring(tempBlock.indexOf(',') + 1));
			tempLocation = location.generateLocation(x, y);
			WKNN.put(tempW, tempLocation.get(0) + "," + tempLocation.get(1));
		}
		
//		System.out.println("\n WKNN: ");
//		System.out.println(WKNN);
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
		estimatedLocation.add(tempX);
		estimatedLocation.add(tempY);
//		System.out.println("\nEstimated location: ");
//		System.out.println(estimatedLocation[0] + " " + estimatedLocation[1]);
	}
	
	public double calculateArea(Map<Double, String> nearestBlocks) {
		String permitationObjects = "";
		for(int i = 1; i < Configuration.K; i++) {
			permitationObjects += (char)(i + '0'); //for K less than 10
		}
		permute(permitationObjects, 0, permitationObjects.length() - 1);
		//System.out.println("Permutations: " + permutations);
		
		List<List<Integer>> coordinates = new ArrayList<List<Integer>>();
		for(Map.Entry<Double, String> entry: nearestBlocks.entrySet()) {
			String temp = entry.getValue();
			int indexI = Integer.parseInt(temp.substring(0, temp.indexOf(',')));
			int indexJ = Integer.parseInt(temp.substring(temp.indexOf(',') + 1));
			coordinates.add(location.generateLocation(indexI, indexJ));
		}
		System.out.println("Coordinates: " + coordinates);
		double sum = 0;
		double maxArea = 0;
		String rightPermutation = "";
		for(int i = 0; i < permutations.size(); i++) { //go through all permutations
			sum = 0;
			for(int j = 0; j < permutations.get(i).length(); j++) {
				//System.out.println(permutations.get(i));
				
				//e.g. first iteration of permutation 0312
				//x1, y1 belong to the first (0) pair of coordinates and x2, y2 - to the forth (3)
				int str1 = Character.getNumericValue(permutations.get(i).charAt(j % permutations.get(i).length()));
				int str2 = Character.getNumericValue(permutations.get(i).charAt((j + 1) % permutations.get(i).length()));
				
				//getting actual real life coordinates
				int x1 = coordinates.get(str1).get(0);
				int x2 = coordinates.get(str2).get(0);
				int y1 = coordinates.get(str1).get(1);
				int y2 = coordinates.get(str2).get(1);
				//System.out.println("x1: " + x1 + " y1: " + y1 + " x2: " + x2 + " y2: " + y2);
				//System.out.println(x1 * y2 - x2 * y1);
				sum += x1 * y2 - x2 * y1;
			}
			//System.out.println("Sum: " + sum);
			if(Math.abs(sum) / 2.0 >= maxArea) {
				maxArea = Math.abs(sum) / 2.0;
				rightPermutation = permutations.get(i);
			}
		}
		System.out.println("Right permutation: " + rightPermutation);
		System.out.println(maxArea);
		return maxArea;
	}
	
    private void permute(String str, int start, int end)
    {
        if (start == end)
            permutations.add("0" + str);
        else
        {
            for (int i = start; i <= end; i++)
            {
                str = swap(str, start, i);
                permute(str, start + 1, end);
                str = swap(str, start , i);
            }
        }
    }
    
    public String swap(String a, int i, int j)
    {
        char temp;
        char[] charArray = a.toCharArray();
        temp = charArray[i] ;
        charArray[i] = charArray[j];
        charArray[j] = temp;
        return String.valueOf(charArray);
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
				System.out.printf("%-15f", coefficient.get(j).get(i));
			}
			System.out.println();
		}
	}
	
}
