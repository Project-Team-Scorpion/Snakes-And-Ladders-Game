package control;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MenuScreenControl {

    @FXML
    private Text gameTitle;

    @FXML
    private Button button_start, button_questionWizard, button_History, quit;

    @FXML
    public void initialize() {
    	
        button_start.setOnAction(event -> navigateTo("/view/SettingsView.fxml"));
        button_questionWizard.setOnAction(event -> navigateTo("/view/QuestionWizView.fxml"));
        button_History.setOnAction(event -> navigateTo("/view/HistoryView.fxml"));
        quit.setOnAction(event -> ((Stage) quit.getScene().getWindow()).close());
    }

    private void navigateTo(String fxmlFile) {
        try {
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource(fxmlFile)));
            Stage stage = (Stage) button_start.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            
        }
    }
}