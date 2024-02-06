package control;


import java.io.IOException;
import java.util.ResourceBundle;

import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import model.GameData;
import javafx.animation.KeyFrame;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.util.Duration;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;



public class BoardControl {

    @FXML
    private ResourceBundle resources; // Resource bundle for localization
    
    @FXML // fx:id="boardpane"
    private AnchorPane boardpane; // The main pane that contains the grid
    
    @FXML
    private Button return_btn; // Button to return to the previous screen
    
    @FXML
    private Label turn_Lable;

    @FXML
    private Label countDown_Label;
    
    @FXML
    private Label timer_Label;
    
    private Integer timeSeconds = 0;
    private Timeline timeline;
    
    
    
    // Initialize the timer properties
    private IntegerProperty counter = new SimpleIntegerProperty(60); // Initial time in seconds
    private Timeline timer;

    private GridPane grid; // The grid that will contain the tiles

    @FXML // This method is called by the FXMLLoader when initialization is complete
    
    void initialize() {
        // Bind the time_Label to the counter property
        countDown_Label.textProperty().bind(counter.asString());

        // Create a timeline for the countdown
        timer = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {
                counter.set(counter.get() - 1);
                if (counter.get() == 0) {
                    // Timer completed, stop the timer
                    timer.stop();
                }
            })
        );
        timer.setCycleCount(Timeline.INDEFINITE); // Repeat indefinitely
        startTimer();
        
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                timeSeconds++;
                // update timerLabel
                timer_Label.setText(timeSeconds.toString());
            }
        }));
        timeline.playFromStart();
        
    	
    	
    	
    	turn_Lable.setText("Bree");
        grid = new GridPane(); // Initialize the grid
        int numTiles = getNumOfTiles(); // Get the number of tiles based on the difficulty
        TileControl[][] tiles = new TileControl[numTiles][numTiles]; // Array to hold the tile controllers

        // Loop to create the tiles
        for (int i = 0; i < numTiles; i++) {
            for (int j = 0; j < numTiles; j++) {
                try {
                    // Load the tile from the FXML file
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/tileView.fxml"));
                    Pane pane = loader.load();
                    TileControl controller = loader.getController(); // Get the controller for the tile
                    controller.bindSize(grid.widthProperty(), grid.heightProperty(), numTiles); // Bind the size of the tile to the size of the grid
                    pane.setUserData(controller); // Store the controller in the user data of the pane
                    tiles[i][j] = controller; // Add the controller to the array
                    grid.add(pane, i, j); // Add the pane to the grid
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }       

        // Bind the size of the grid to the size of the boardpane
        grid.prefWidthProperty().bind(boardpane.widthProperty());
        grid.prefHeightProperty().bind(boardpane.heightProperty());

        // Make the grid always grow to fill available space
        GridPane.setVgrow(grid, Priority.ALWAYS);
        GridPane.setHgrow(grid, Priority.ALWAYS);

        // Add the grid to the boardpane
        boardpane.getChildren().add(grid);     

        // Make the grid always fill the boardpane
        AnchorPane.setTopAnchor(grid, 0.0);
        AnchorPane.setBottomAnchor(grid, 0.0);
        AnchorPane.setLeftAnchor(grid, 0.0);
        AnchorPane.setRightAnchor(grid, 0.0);

        // Set the action for the return button
        return_btn.setOnAction(event -> navigateTo("/view/MenuScreenView.fxml"));
    }
    
    

    
	 // Method to start the timer
	    @FXML
	    private void startTimer() {
	        timer.play();
	    }
	
	    // Method to stop the timer
	    @FXML
	    private void stopTimer() {
	        timer.stop();
	    }
    // Method to get the number of tiles based on the difficulty
    private int getNumOfTiles() {
        int numTiles = 0;
        String diff = GameData.getDifficulty();
        switch (diff.toLowerCase()) {
        case "easy":
            numTiles = 7;
            break;
        case "medium":
            numTiles = 10;
            break;
        case "hard":
            numTiles = 13;
            break;
        default:        
        }
        return numTiles;
    }

    // Method to navigate to another screen
    private void navigateTo(String fxmlFile) {
        try {
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource(fxmlFile)));
            Stage stage = (Stage) return_btn.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

	void gameloop(){
    	
    }
    
    void enable_action(int x, int y) {
    	
    }
}



