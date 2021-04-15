package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class Controller implements Initializable {
	Image wifi = new Image("/wifi.png");
	Image pin1 = new Image("/pin1.png");
	Image pin2 = new Image("/pin2.png");
	@FXML Button start;
	@FXML Button iterate;
	@FXML Canvas canvas;
	@FXML Canvas grid;
	private boolean started = false;
	List<List<Integer>> iterationLocationOfAPs = new ArrayList<List<Integer>>();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		GraphicsContext context = grid.getGraphicsContext2D();
		GraphicsContext explanation = canvas.getGraphicsContext2D();
		drawOnCanvas(context);
		drawSchemeLegend(explanation);

		start.setOnAction(event -> {
			drawOnCanvas(context);
			buttonStartPressed();
			started = true;
		});
		iterate.setOnAction(event -> {
			if(!started) {
				drawOnCanvas(context);
				buttonStartPressed();
				started = true;
			} else {
				drawOnCanvas(context);
				buttonIteratePressed();
			}
		});
	}
	
	public void drawSchemeLegend(GraphicsContext explanation) {
		explanation.clearRect(0, 0, 220, 380);
		
		explanation.setFill(Color.WHITE);
		explanation.fillRoundRect(0, 0, 298, 380, 10, 10);
		
		explanation.setFill(Color.BLACK);
		explanation.setTextBaseline(VPos.CENTER);
		
		//scheme legend
		int width = 26;
		//actual location
		explanation.drawImage(pin1, 10, 10, width, width);
		String pin1 = "actual location";
		explanation.fillText(pin1, 20 + width, 10 + width/2);
		
		//estimated location
		explanation.drawImage(pin2, 10, 10 + 2*width, width, width);
		String pin2 = "estimated location";
		explanation.fillText(pin2, 20 + width, 10 + 2*width + width/2);
		
		//AP location
		explanation.drawImage(wifi, 10, 10 + 4*width, width, width);
		String wifi = "AP location";
		explanation.fillText(wifi, 20 + width, 10 + + 4*width + width/2);
		
		//chosen blocks
		explanation.setFill(new Color(1.0, 0.0, 0.0, 0.5));
		explanation.fillRoundRect(10, 10 + 6*width, width, width, 10, 10);
		String block1 = "chosen blocks";
		explanation.setFill(Color.BLACK);
		explanation.fillText(block1, 20 + width, 10 + 6*width + width/2);
		
		//blocks that would have been chosen if not for no-straight-line restriction
		explanation.setFill(new Color(0.0, 1.0, 0.0, 0.4));
		explanation.fillRoundRect(10, 10 + 8*width, width, width, 10, 10);
		String block2 = "blocks that would have been chosen \nif not for no-straight-line restriction";
		explanation.setFill(Color.BLACK);
		explanation.fillText(block2, 20 + width, 10 + 8*width + width/2, 250);
	}
	
	public void drawOnCanvas(GraphicsContext context) {
		context.clearRect(0, 0, 408, 408);
		
		for(int row = 0; row < 10; row++) {
			for(int col = 0; col < 10; col++) {
				int position_y = row * 38 + 2;
				int position_x = col * 38 + 2;
				int width = 34;
				context.setFill(Color.WHITE);
				context.fillRoundRect(position_x + 14, position_y + 14, width, width, 10, 10);
			}
		}
	}
	
	public void printData(GraphicsContext explanation, String data) { //print out some data
		int width = 26;
		explanation.setFill(Color.BLACK);
		explanation.fillText(data, 20 + width, 10 + 10*width + width/2);
	}
	
	public void drawCurrentLocation(GraphicsContext context, List<Integer> currentLocation) {
		System.out.println("Current Location:" + currentLocation.get(0) + " " + currentLocation.get(1));
		double position_x = currentLocation.get(0) * 380.0 / 1000.0 + 2;
		double position_y = currentLocation.get(1) * 380.0 / 1000.0 + 2;

		double width = 32;
		context.drawImage(pin1, position_x - 16 + 14, position_y - 32 + 14, width, width);
	}
	
	public void drawEstimatedLocation(GraphicsContext context, List<Double> estimatedLocation) {
		System.out.println("Estimated Location:" + estimatedLocation.get(0) + " " + estimatedLocation.get(1));
		double position_x = estimatedLocation.get(0) * 380.0 / 1000.0 + 2;
		double position_y = estimatedLocation.get(1) * 380.0 / 1000.0 + 2;
		
		double width = 32;
		context.drawImage(pin2, position_x - 16 + 14, position_y - 32 + 14, width, width);
	}
	
	public void colorChosenBlocks(GraphicsContext context, Map<Double, String> nearestBlocks) {
		for(Map.Entry<Double, String> entry: nearestBlocks.entrySet()) {
			String block = entry.getValue();
			int position_y = Integer.parseInt(block.substring(block.indexOf(',') + 1)) * 38 + 2;
			int position_x = Integer.parseInt(block.substring(0, block.indexOf(','))) * 38 + 2;
			int width = 34;
			context.setFill(new Color(1.0, 0.0, 0.0, 0.5));
			context.fillRoundRect(position_x + 14, position_y + 14, width, width, 10, 10);
		}
	}
	
	public void colorBlocksWORest(GraphicsContext context, Map<Double, String> nearestBlocks, 
			Map<Double, String> nearestBlocksWORest) {
		for(Map.Entry<Double, String> entry : nearestBlocksWORest.entrySet()) {
			if(!nearestBlocks.containsKey(entry.getKey())) {
				String block = entry.getValue();
				int position_y = Integer.parseInt(block.substring(block.indexOf(',') + 1)) * 38 + 2;
				int position_x = Integer.parseInt(block.substring(0, block.indexOf(','))) * 38 + 2;
				int width = 34;
				context.setFill(new Color(0.0, 1.0, 0.0, 0.4));
				context.fillRoundRect(position_x + 14, position_y + 14, width, width, 10, 10);
			}
		}
	}
	
	public void drawAPs(GraphicsContext context, List<List<Integer>> locationOfAPs) {
		for(int i = 0; i < locationOfAPs.size(); i++) {
			System.out.println("AP Location:" + locationOfAPs.get(i).get(0) + " " + locationOfAPs.get(i).get(1));
			double position_x = locationOfAPs.get(i).get(0) * 380.0 / 1000.0 + 2;
			double position_y = locationOfAPs.get(i).get(1) * 380.0 / 1000.0 + 2;
				
			double width = 16;
			context.drawImage(wifi, position_x - 8 + 14, position_y - 8 + 14, width, width);
		}
	}
	
	
	public void buttonStartPressed() {
		RTUser_localization localization = new RTUser_localization();
		localization.localization();
		iterationLocationOfAPs = localization.radio_map.APs.locationOfAPs;
		
		localization.WKNN();
		localization.calculateArea(localization.nearestBlocks);
		colorChosenBlocks(grid.getGraphicsContext2D(), localization.nearestBlocks);
		colorBlocksWORest(grid.getGraphicsContext2D(), localization.nearestBlocks, localization.nearestBlocksWORestr);
		drawAPs(grid.getGraphicsContext2D(), localization.radio_map.APs.locationOfAPs);
		drawCurrentLocation(grid.getGraphicsContext2D(), localization.currentLocation);
		drawEstimatedLocation(grid.getGraphicsContext2D(), localization.estimatedLocation);
		
	}
	
	public void buttonIteratePressed() {
		RTUser_localization localization = new RTUser_localization(iterationLocationOfAPs);
		localization.localization();
		
		localization.WKNN();
		
		colorChosenBlocks(grid.getGraphicsContext2D(), localization.nearestBlocks);
		colorBlocksWORest(grid.getGraphicsContext2D(), localization.nearestBlocks, localization.nearestBlocksWORestr);
		drawAPs(grid.getGraphicsContext2D(), localization.radio_map.APs.locationOfAPs);
		drawCurrentLocation(grid.getGraphicsContext2D(), localization.currentLocation);
		drawEstimatedLocation(grid.getGraphicsContext2D(), localization.estimatedLocation);
	}
}
