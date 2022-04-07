package application;

public class Configuration {
	// number of APs, AP tx power, area size, number of blocks
	static int K = 3; //K in WKNN
	static int NUM_WIFIs = 10;
	static double TXPower = 1;
	static int MAP_NUM_COLS = 10;
	static int MAP_NUM_ROWS = 10;
	static double MAP_WIDTH_IN_CM = 10000;
	static double MAP_HEIGHT_IN_CM = 10000;
	static double RANDOMNESS_DIVIDER = 10000.0/4;
	static boolean SHOW_RSS_DEBUG_MSG = false;
	static double GUI_WIDTH = 720;
	static double GUI_HEIGHT = 480;
	static double GUI_MAP_WIDTH = 380;
	static double GUI_MAP_HEIGHT = 380;
	
	static double USER_X_LOCATION = 0;
	static double USER_Y_LOCATION = 0;
	
	static boolean SKIP_SOME_POINTS_ON_THE_LINE = false;
	static boolean W_ON = true; //WKNN
	
	final static int numberOfMeasurements = 1;
	
	/*
	 * Notes: how to compute an area given coordinates
	 * https://www.mathopenref.com/coordpolygonarea.html
	 */	
}
