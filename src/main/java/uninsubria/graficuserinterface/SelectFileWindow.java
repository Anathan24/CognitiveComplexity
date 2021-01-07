package uninsubria.graficuserinterface;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SelectFileWindow extends Application{
	 
	private final String APPLICATION_NAME = "Cognitive Complexity";
	    
	Stage mainStage;
	private Scene selectPathScene;
	
	@SuppressWarnings({ "static-access", "exports" })	
    @Override
    public void start(Stage stage) throws IOException {
    	mainStage = stage;

    	mainStage.setTitle(APPLICATION_NAME);
    	mainStage.setResizable(false);
    	mainStage.setWidth(600);
    	mainStage.setHeight(400);
        
        //TOP Vertical Box
        final Label appName = new Label(APPLICATION_NAME);
        appName.resize(600, 100);
        appName.setFont(new Font(20));
        
        final VBox topVBox = new VBox();
        topVBox.setAlignment(Pos.CENTER);
        topVBox.setPrefSize(600, 150);
        topVBox.getChildren().add(appName);
        
        
        //CENTER Horizontal Box
        final TextField choosenPath = new TextField();
        choosenPath.setFocusTraversable(false);
        choosenPath.setEditable(false);
        choosenPath.setAlignment(Pos.TOP_LEFT);
        choosenPath.setPromptText("Select java a file");
        choosenPath.setPrefSize(365, 30);
        Button selectButton = new Button("Select");
        selectButton.setPrefSize(85, 20);
        selectButton.setOnAction(event -> { 
	    	FileChooser chooser = new FileChooser();
			chooser.setTitle("Select a java file");
			chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java files (*.java)", "*.java"));
			File selectedFile = chooser.showOpenDialog(mainStage);
			if(selectedFile != null) {
				String path = selectedFile.getAbsolutePath();
				choosenPath.setText(path);
			}
        });
        
        final VBox centerVBox = new VBox();
        centerVBox.setAlignment(Pos.CENTER);
        centerVBox.setPrefSize(600, 150);
        centerVBox.setMargin(choosenPath, new Insets(10, 10, 10, 10));
        centerVBox.setMargin(selectButton, new Insets(10, 10, 10, 10));
        centerVBox.setPadding(new Insets(5, 5, 5, 5));
        centerVBox.getChildren().addAll(choosenPath, selectButton);
        
        
        //BOTTOM Vertical Box
        Button calculateComplexityButton = new Button("Calculate Complexity");
        calculateComplexityButton.setPrefSize(175, 25);
        calculateComplexityButton.setAlignment(Pos.CENTER);
        calculateComplexityButton.setOnAction(event -> {
	    	if(choosenPath.getText().isEmpty()) {
	    		Alert alert = new Alert(AlertType.WARNING);
	        	alert.setTitle("Alert");
	        	alert.setHeaderText("No file selected, please select any java file!");
	        	alert.show();
	    	}
	    	else {
	        	new FileComplexityWindow(mainStage, selectPathScene, choosenPath.getText());
			}
        });
        
        final VBox bottomVBox = new VBox();
        bottomVBox.setAlignment(Pos.CENTER);
        bottomVBox.setPrefSize(600, 133);
        bottomVBox.getChildren().addAll(calculateComplexityButton);
        
        selectPathScene = new Scene(new VBox());
        ((VBox) selectPathScene.getRoot()).getChildren().addAll(topVBox, centerVBox, bottomVBox);
        mainStage.centerOnScreen();
        mainStage.setScene(selectPathScene);
    	mainStage.show();
    }
    
    public void setScene() {
    	mainStage.setScene(this.selectPathScene);
    	
    }
    
    public static void main(String[] args) {
		launch();
	}
}
