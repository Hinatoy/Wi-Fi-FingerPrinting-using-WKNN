package application;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputFilter.Config;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Controller implements Initializable {
	@FXML
	private ImageView exit, menu;
	@FXML
	private AnchorPane pane1, pane2, drag;
	@FXML
	private JFXButton map, settings, saveAs;
	
	@FXML 
	private JFXButton start;
	@FXML
	private Canvas grid;
	
	@FXML
	private JFXButton settings_apply, settings_save, setting_load;
	@FXML
	private TextField area_measure, rows_columns, user_location, number_aps;
	@FXML
	private TextArea locations_aps;
	@FXML 
	private JFXComboBox<String> algorithm;
	
	
	private double x = 0, y = 0;
	private boolean menuPressed = false;
	private boolean started = false;
	private String mapFXML = "map.fxml";
	private String settingsFXML = "settings.fxml";
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		exit.setOnMouseClicked(event -> {
			System.exit(0);
		});
		
		pane1.setVisible(false);
		
		FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), pane1);
		fadeTransition.setFromValue(1);
		fadeTransition.setToValue(0);
		fadeTransition.play();
		
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), pane2);
		translateTransition.setByX(-600);
		translateTransition.play();
		
		menu.setOnMouseClicked(event -> {
			
			pane1.setVisible(true);
			
			FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(0.5), pane1);
			fadeTransition1.setFromValue(1);
			fadeTransition1.setToValue(0.15);
			fadeTransition1.play();
			
			TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(0.5), pane2);
			if(!menuPressed) {
				translateTransition1.setByX(+600);
				menuPressed = true;
			}
			else {
				translateTransition1.setByX(-600);
				menuPressed = false;
				pane1.setVisible(false);
			}
			translateTransition1.play();
			
		});
		
		pane1.setOnMouseClicked(event -> {
			if(menuPressed) {
				FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(0.5), pane1);
				fadeTransition1.setFromValue(1);
				fadeTransition1.setToValue(0.15);
				fadeTransition1.play();
				
				fadeTransition1.setOnFinished(event1 -> {
					pane1.setVisible(false);
				});
				
				TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(0.5), pane2);
				translateTransition1.setByX(-600);
				translateTransition1.play();
				
				menuPressed = false;
			}
		});
		
		drag.setOnMousePressed(event -> {
			x = event.getSceneX();
			y = event.getSceneY();
		});
		
		drag.setOnMouseDragged(event -> {
			Stage stage = (Stage) drag.getScene().getWindow();
			stage.setX(event.getScreenX() - x);
			stage.setY(event.getScreenY() - y);
		});
		
		settings.setOnMouseClicked(event -> {
		    try {
		        FXMLLoader loader = new FXMLLoader(getClass().getResource(settingsFXML));
		        Stage stage = (Stage) settings.getScene().getWindow();
		        Scene scene = new Scene(loader.load());
		        stage.setScene(scene);
		        
		    } catch (IOException io){
		        io.printStackTrace();
		    }
		});
		
		map.setOnMouseClicked(event -> {
		    try {
		        FXMLLoader loader = new FXMLLoader(getClass().getResource(mapFXML));
		        Stage stage = (Stage) settings.getScene().getWindow();
		        Scene scene = new Scene(loader.load());
		        stage.setScene(scene);
		        
		    } catch (IOException io){
		        io.printStackTrace();
		    }
		});
		
		if(arg0.getFile().substring(arg0.getFile().length() - 8).equals(mapFXML)) {
			GraphicsContext context = grid.getGraphicsContext2D();
			drawOnCanvas(context);
	
			start.setOnAction(event -> {
				drawOnCanvas(context);
				buttonStartPressed();
				started = true;
			});
		}
		
		if(arg0.getFile().substring(arg0.getFile().length() - 13).equals(settingsFXML)) {
			algorithm.getItems().add("WKNN");
			algorithm.getItems().add("KNN");
			
			settings_apply.setOnAction(event -> {
				applySettings();
			});
			
			settings_save.setOnAction(event -> {
				saveSettings();
			});
			
		}
	}
	
	
	
	Image wifi = new Image("wifi.png");
	Image pin1 = new Image("pin1.png"); //actual location
	Image pin2 = new Image("pin2.png"); //KNN estimated location
	Image pin3 = new Image("pin3.png"); //WKNN estimated location
//	Image pin4 = new Image("@../pin4.png"); //WKKN + line estimated location
//	
//	@FXML Button start;
//	@FXML Button iterate;
//	@FXML Canvas canvas;
//	@FXML Canvas grid;
//	@FXML CheckBox knn;
//	@FXML CheckBox wknn;
//	@FXML CheckBox wknnLine;
//	@FXML TextField numberOfK;
//	@FXML TextField x;
//	@FXML TextField y;
//	private boolean started = false;
//	List<List<Integer>> iterationLocationOfAPs = new ArrayList<List<Integer>>();
//	
//	@Override
//	public void initialize(URL arg0, ResourceBundle arg1) {
//		GraphicsContext context = grid.getGraphicsContext2D();
//		GraphicsContext explanation = canvas.getGraphicsContext2D();
//		//drawOnCanvas(context);
//		//drawSchemeLegend(explanation);
//
//		start.setOnAction(event -> {
//			//drawOnCanvas(context);
//			//buttonStartPressed();
//			started = true;
//		});
//		iterate.setOnAction(event -> {
//			if(!started) {
//				//drawOnCanvas(context);
//				//buttonStartPressed();
//				started = true;
//			} else {
//				//drawOnCanvas(context);
//				//buttonIteratePressed();
//			}
//		});
//	}
//	
//	public void drawSchemeLegend(GraphicsContext explanation) {
//		//explanation.clearRect(0, 0, 220, 380);
////		
////		explanation.setFill(Color.WHITE);
////		explanation.fillRoundRect(0, 0, 298, 440, 10, 10);
////		
////		explanation.setFill(Color.BLACK);
////		explanation.setTextBaseline(VPos.CENTER);
////		
////		//scheme legend
////		int width = 26;
////		//actual location
////		explanation.drawImage(pin1, 10, 10, width, width);
////		String pin1 = "actual location";
////		explanation.fillText(pin1, 20 + width, 10 + width/2);
////		
////		//estimated location
////		explanation.drawImage(pin2, 10, 10 + 2*width, width, width);
////		String pin2 = "estimated location";
////		explanation.fillText(pin2, 20 + width, 10 + 2*width + width/2);
////		
////		//AP location
////		explanation.drawImage(wifi, 10, 10 + 4*width, width, width);
////		String wifi = "AP location";
////		explanation.fillText(wifi, 20 + width, 10 + + 4*width + width/2);
////		
////		//chosen blocks
////		explanation.setFill(new Color(1.0, 0.0, 0.0, 0.5));
////		explanation.fillRoundRect(10, 10 + 6*width, width, width, 10, 10);
////		String block1 = "chosen blocks";
////		explanation.setFill(Color.BLACK);
////		explanation.fillText(block1, 20 + width, 10 + 6*width + width/2);
//		
//		//blocks that would have been chosen if not for no-straight-line restriction
////		explanation.setFill(new Color(0.0, 1.0, 0.0, 0.4));
////		explanation.fillRoundRect(10, 10 + 8*width, width, width, 10, 10);
////		String block2 = "blocks that would have been chosen \nif not for no-straight-line restriction";
////		explanation.setFill(Color.BLACK);
////		explanation.fillText(block2, 20 + width, 10 + 8*width + width/2, 250);
//	}
//	
	public void drawOnCanvas(GraphicsContext context) {
		context.clearRect(0, 0, Configuration.GUI_MAP_WIDTH, Configuration.GUI_MAP_HEIGHT); // square space
		int border = 2;
		// if the user's map is not square but rectangular, the beginning point on the map may be different for the map to still be centered
		double startingX = 0;
		double startingY = 0;
		double width = 0;
		double height = 0;
		double ratio = 1;
		if(Configuration.MAP_WIDTH_IN_CM > Configuration.MAP_HEIGHT_IN_CM) {
			ratio = Configuration.MAP_HEIGHT_IN_CM / Configuration.MAP_WIDTH_IN_CM;
			startingY = (Configuration.GUI_MAP_HEIGHT - Configuration.GUI_MAP_HEIGHT * ratio) / 2;
			
			height = Configuration.GUI_MAP_HEIGHT * ratio / Configuration.MAP_NUM_ROWS - 2 * border;
			width = Configuration.GUI_MAP_WIDTH / Configuration.MAP_NUM_COLS - 2 * border;
		} else if(Configuration.MAP_WIDTH_IN_CM < Configuration.MAP_HEIGHT_IN_CM) {
			ratio = Configuration.MAP_WIDTH_IN_CM / Configuration.MAP_HEIGHT_IN_CM;
			startingX = (Configuration.GUI_MAP_WIDTH - Configuration.GUI_MAP_WIDTH * ratio) / 2;
			
			height = Configuration.GUI_MAP_HEIGHT / Configuration.MAP_NUM_ROWS - 2 * border;
			width = Configuration.GUI_MAP_WIDTH * ratio / Configuration.MAP_NUM_COLS - 2 * border;
		} else {
			height = Configuration.GUI_MAP_HEIGHT / Configuration.MAP_NUM_ROWS - 2 * border;
			width = Configuration.GUI_MAP_WIDTH / Configuration.MAP_NUM_COLS - 2 * border;
		}
		
		for(int row = 0; row < Configuration.MAP_NUM_ROWS; row++) {
			for(int col = 0; col < Configuration.MAP_NUM_COLS; col++) {
				double position_x = startingX + col * (border + width + border);
				double position_y = startingY + row * (border + height + border);
				context.setFill(Color.WHITE);
				context.fillRoundRect(position_x, position_y, width, height, 10, 10);
			}
		}
	}
	
//	public void printData(GraphicsContext explanation, String data) { //print out some data
//		int width = 26;
//		explanation.setFill(Color.BLACK);
//		explanation.fillText(data, 20 + width, 10 + 10*width + width/2);
//	}
//	
	public void drawCurrentLocation(GraphicsContext context, List<Double> currentLocation) {
		System.out.println("Current Location:" + currentLocation.get(0) + " " + currentLocation.get(1));
		int border = 2;
		// if the user's map is not square but rectangular, the beginning point on the map may be different for the map to still be centered
		double startingX = 0;
		double startingY = 0;
		double height = 0, width = 0;
		double ratio = 1;
		if(Configuration.MAP_WIDTH_IN_CM > Configuration.MAP_HEIGHT_IN_CM) {
			ratio = Configuration.MAP_HEIGHT_IN_CM / Configuration.MAP_WIDTH_IN_CM;
			startingY = (Configuration.GUI_MAP_HEIGHT - Configuration.GUI_MAP_HEIGHT * ratio) / 2;
			
			height = Configuration.GUI_MAP_HEIGHT * ratio / Configuration.MAP_NUM_ROWS - 2 * border;
			width = Configuration.GUI_MAP_WIDTH / Configuration.MAP_NUM_COLS - 2 * border;
		} else if(Configuration.MAP_WIDTH_IN_CM < Configuration.MAP_HEIGHT_IN_CM) {
			ratio = Configuration.MAP_WIDTH_IN_CM / Configuration.MAP_HEIGHT_IN_CM;
			startingX = (Configuration.GUI_MAP_WIDTH - Configuration.GUI_MAP_WIDTH * ratio) / 2;
			
			height = Configuration.GUI_MAP_HEIGHT / Configuration.MAP_NUM_ROWS - 2 * border;
			width = Configuration.GUI_MAP_WIDTH * ratio / Configuration.MAP_NUM_COLS - 2 * border;
		} else {
			height = Configuration.GUI_MAP_HEIGHT / Configuration.MAP_NUM_ROWS - 2 * border;
			width = Configuration.GUI_MAP_WIDTH / Configuration.MAP_NUM_COLS - 2 * border;
		}
		
		System.out.println("Starting X: " + startingX + " Starting Y: " + startingY);
		
		double position_x = startingX + (Configuration.GUI_MAP_WIDTH - startingX*2) / Configuration.MAP_WIDTH_IN_CM * currentLocation.get(0);
		double position_y = startingY + (Configuration.GUI_MAP_HEIGHT - startingY*2) / Configuration.MAP_HEIGHT_IN_CM * currentLocation.get(1);
		System.out.println("Current location on map: " + position_x + " " + position_y);
		double logoWidth = 32;
		
		context.drawImage(pin1, position_x - logoWidth/2, position_y - logoWidth, logoWidth, logoWidth);
	}
	
	public void drawEstimatedLocation(GraphicsContext context, List<Double> estimatedLocation) {
		//System.out.println("Current Location:" + currentLocation.get(0) + " " + currentLocation.get(1));
		int border = 2;
		// if the user's map is not square but rectangular, the beginning point on the map may be different for the map to still be centered
		double startingX = 0;
		double startingY = 0;
		double ratio = 1;
		if(Configuration.MAP_WIDTH_IN_CM > Configuration.MAP_HEIGHT_IN_CM) {
			ratio = Configuration.MAP_HEIGHT_IN_CM / Configuration.MAP_WIDTH_IN_CM;
			startingY = (Configuration.GUI_MAP_HEIGHT - Configuration.GUI_MAP_HEIGHT * ratio) / 2;
			
		} else if(Configuration.MAP_WIDTH_IN_CM < Configuration.MAP_HEIGHT_IN_CM) {
			ratio = Configuration.MAP_WIDTH_IN_CM / Configuration.MAP_HEIGHT_IN_CM;
			startingX = (Configuration.GUI_MAP_WIDTH - Configuration.GUI_MAP_WIDTH * ratio) / 2;
			
		}
		
		
		double position_x = startingX + estimatedLocation.get(0) * (Configuration.GUI_MAP_WIDTH - startingX*2) / Configuration.MAP_WIDTH_IN_CM;
		double position_y = startingY + estimatedLocation.get(1) * (Configuration.GUI_MAP_HEIGHT - startingY*2) / Configuration.MAP_HEIGHT_IN_CM;
		System.out.println("Estimated location on map: " + position_x + " " + position_y);
		double width = 32;
		
		context.drawImage(pin2, position_x - width/2 - border, position_y - width - border, width, width);
	}
	
	public void colorChosenBlocks(GraphicsContext context, Map<Double, String> nearestBlocks) {
		int border = 2;
		// if the user's map is not square but rectangular, the beginning point on the map may be different for the map to still be centered
		double startingX = 0;
		double startingY = 0;
		double width = 0;
		double height = 0;
		double ratio = 1;
		if(Configuration.MAP_WIDTH_IN_CM > Configuration.MAP_HEIGHT_IN_CM) {
			ratio = Configuration.MAP_HEIGHT_IN_CM / Configuration.MAP_WIDTH_IN_CM;
			startingY = (Configuration.GUI_MAP_HEIGHT - Configuration.GUI_MAP_HEIGHT * ratio) / 2;
			
			height = Configuration.GUI_MAP_HEIGHT * ratio / Configuration.MAP_NUM_ROWS - 2 * border;
			width = Configuration.GUI_MAP_WIDTH / Configuration.MAP_NUM_COLS - 2 * border;
		} else if(Configuration.MAP_WIDTH_IN_CM < Configuration.MAP_HEIGHT_IN_CM) {
			ratio = Configuration.MAP_WIDTH_IN_CM / Configuration.MAP_HEIGHT_IN_CM;
			startingX = (Configuration.GUI_MAP_WIDTH - Configuration.GUI_MAP_WIDTH * ratio) / 2;
			
			height = Configuration.GUI_MAP_HEIGHT / Configuration.MAP_NUM_ROWS - 2 * border;
			width = Configuration.GUI_MAP_WIDTH * ratio / Configuration.MAP_NUM_COLS - 2 * border;
		} else {
			height = Configuration.GUI_MAP_HEIGHT / Configuration.MAP_NUM_ROWS - 2 * border;
			width = Configuration.GUI_MAP_WIDTH / Configuration.MAP_NUM_COLS - 2 * border;
		}
		
		
		for(Map.Entry<Double, String> entry: nearestBlocks.entrySet()) {
			String block = entry.getValue();
			double position_y = startingY + Integer.parseInt(block.substring(block.indexOf(',') + 1)) * (border + height + border);
			double position_x = startingX + Integer.parseInt(block.substring(0, block.indexOf(','))) * (border + width + border);
			context.setFill(new Color(1.0, 0.0, 0.0, 0.5));
			context.fillRoundRect(position_x, position_y, width, height, 10, 10);
		}
	}
	
//	public void colorBlocksWORest(GraphicsContext context, Map<Double, String> nearestBlocks, 
//			Map<Double, String> nearestBlocksWORest) {
//		for(Map.Entry<Double, String> entry : nearestBlocksWORest.entrySet()) {
//			if(!nearestBlocks.containsKey(entry.getKey())) {
//				String block = entry.getValue();
//				int position_y = Integer.parseInt(block.substring(block.indexOf(',') + 1)) * 38 + 2;
//				int position_x = Integer.parseInt(block.substring(0, block.indexOf(','))) * 38 + 2;
//				int width = 34;
//				context.setFill(new Color(0.0, 1.0, 0.0, 0.4));
//				context.fillRoundRect(position_x + 14, position_y + 14, width, width, 10, 10);
//			}
//		}
//	}
//	
	public void drawAPs(GraphicsContext context, List<List<Integer>> locationOfAPs) {
		//System.out.println("Current Location:" + currentLocation.get(0) + " " + currentLocation.get(1));
		int border = 2;
		// if the user's map is not square but rectangular, the beginning point on the map may be different for the map to still be centered
		double startingX = 0;
		double startingY = 0;
		double ratio = 1;
		if(Configuration.MAP_WIDTH_IN_CM > Configuration.MAP_HEIGHT_IN_CM) {
			ratio = Configuration.MAP_HEIGHT_IN_CM / Configuration.MAP_WIDTH_IN_CM;
			startingY = (Configuration.GUI_MAP_HEIGHT - Configuration.GUI_MAP_HEIGHT * ratio) / 2;
			
		} else if(Configuration.MAP_WIDTH_IN_CM < Configuration.MAP_HEIGHT_IN_CM) {
			ratio = Configuration.MAP_WIDTH_IN_CM / Configuration.MAP_HEIGHT_IN_CM;
			startingX = (Configuration.GUI_MAP_WIDTH - Configuration.GUI_MAP_WIDTH * ratio) / 2;
			
		}
		
		
		for(int i = 0; i < locationOfAPs.size(); i++) {
			//System.out.println("AP Location:" + locationOfAPs.get(i).get(0) + " " + locationOfAPs.get(i).get(1));
			double position_x = startingX + locationOfAPs.get(i).get(0) * (Configuration.GUI_MAP_WIDTH - startingX*2) / Configuration.MAP_WIDTH_IN_CM;
			double position_y = startingY + locationOfAPs.get(i).get(1) * (Configuration.GUI_MAP_HEIGHT - startingY*2) / Configuration.MAP_HEIGHT_IN_CM;
				
			double width = 16;
			context.drawImage(wifi, position_x - width/2 - border, position_y - width/2 - border, width, width);
		}
	}
	
	
	public void buttonStartPressed() {
		RTUser_localization localization = new RTUser_localization();
		localization.localization();
		//localization.print();
		//iterationLocationOfAPs = localization.radio_map.APs.locationOfAPs;
		
		localization.locAlgorithm();
		colorChosenBlocks(grid.getGraphicsContext2D(), localization.nearestBlocks);
		//colorBlocksWORest(grid.getGraphicsContext2D(), localization.nearestBlocks, localization.nearestBlocksWORestr);
		drawAPs(grid.getGraphicsContext2D(), localization.radio_map.APs.locationOfAPs);
		drawCurrentLocation(grid.getGraphicsContext2D(), localization.currentLocation);
		if(Configuration.W_ON)
				drawEstimatedLocation(grid.getGraphicsContext2D(), localization.estimatedLocationWKNN);
		else
				drawEstimatedLocation(grid.getGraphicsContext2D(), localization.estimatedLocationKNN);
		
	}
//	
//	public void buttonIteratePressed() {
////		RTUser_localization localization = new RTUser_localization(iterationLocationOfAPs);
////		localization.localization();
////		
////		localization.locAlgorithm();
////		
////		colorChosenBlocks(grid.getGraphicsContext2D(), localization.nearestBlocks);
////		//colorBlocksWORest(grid.getGraphicsContext2D(), localization.nearestBlocks, localization.nearestBlocksWORestr);
////		drawAPs(grid.getGraphicsContext2D(), localization.radio_map.APs.locationOfAPs);
////		drawCurrentLocation(grid.getGraphicsContext2D(), localization.currentLocation);
////		//drawEstimatedLocation(grid.getGraphicsContext2D(), localization.estimatedLocation);
//	}
	
	private void applySettings() {
		//	TextField area_measure, rows_columns, user_location, number_aps, locations_aps;
		if(!area_measure.getText().trim().isEmpty()) {
			String[] areaMeasure = area_measure.getText().split(",");
			Configuration.MAP_WIDTH_IN_CM = Double.parseDouble(areaMeasure[0].substring(1));
			Configuration.MAP_HEIGHT_IN_CM = Double.parseDouble(areaMeasure[1].substring(1, areaMeasure[1].length() - 1));
			System.out.println(Configuration.MAP_WIDTH_IN_CM + " " + Configuration.MAP_HEIGHT_IN_CM);
		}
		
		if(!rows_columns.getText().trim().isEmpty()) {
			String[] rowsColumns = rows_columns.getText().split(",");
			Configuration.MAP_NUM_COLS = Integer.parseInt(rowsColumns[0].substring(1));
			Configuration.MAP_NUM_ROWS = Integer.parseInt(rowsColumns[1].substring(1, rowsColumns[1].length() - 1));
		}
		
		if(!user_location.getText().trim().isEmpty()) {
			String[] userLocation = user_location.getText().split(",");
			Configuration.USER_X_LOCATION = Double.parseDouble(userLocation[0].substring(1));
			Configuration.USER_Y_LOCATION = Double.parseDouble(userLocation[1].substring(1, userLocation[1].length() - 1));
		}
		
		if(!number_aps.getText().trim().isEmpty()) {
			Configuration.NUM_WIFIs = Integer.parseInt(number_aps.getText());
		}
		
		if(!locations_aps.getText().trim().isEmpty()) {
			
		}
		
		if(!algorithm.getSelectionModel().isEmpty()) {
			if(algorithm.getValue().equals("WKNN")) {
				Configuration.W_ON = true;
				System.out.println("WKNN");
			} else if (algorithm.getValue().equals("KNN")) {
				Configuration.W_ON = false;
				System.out.println("KNN");
			}
		}
	}
	
	private void saveSettings() {
        FileChooser fileChooser = new FileChooser();
        
        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("settings");
        
        //Show save file dialog
        File file = fileChooser.showSaveDialog((Stage) settings_save.getScene().getWindow());
        
        String settingsSaveFile = "Working properly!";

        if (file != null) {
            saveTextToFile(settingsSaveFile, file);
        }
	}
	
    private void saveTextToFile(String content, File file) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
        } catch (IOException ex) {
            
        }
    }
}
