package uninsubria.graficuserinterface;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import uninsubria.cognitivcomplexity.CSVGenerator;
import uninsubria.cognitivcomplexity.CalculusResult;
import uninsubria.cognitivcomplexity.CognitiveComplexity;
import uninsubria.graficuserinterface.CalculusResultWrapper;

public class FileComplexityWindow {
	private Stage mainStage = null;
	private Scene complexityFileScene = null;
	private Scene previousScene = null;
	private String path = null;
	private String className = null;
	private int classComplexity = 0;
	
	Map<String, List<CalculusResult>> calculusResult = null;
	private TableView<CalculusResultWrapper> tableView = new TableView<>();
	
	private final ObservableList<CalculusResultWrapper> outputData = FXCollections.observableArrayList();
	
	@SuppressWarnings({ "exports" })
	public FileComplexityWindow(Stage stage, Scene previousScene, String path) {
		this.mainStage = stage;
		this.previousScene = previousScene;
		this.path = path;
		this.getFileComplexity();
		this.setAllGraficComponents();
	}
	
	private void getFileComplexity() {
		this.calculusResult = new CognitiveComplexity(this.path).getCalculusResult();
		
		Iterator<Entry<String, List<CalculusResult>>> iterator = calculusResult.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry<String, List<CalculusResult>> pair = iterator.next();
			this.className = pair.getKey();
			List<CalculusResult> results = pair.getValue();
			for(CalculusResult result: results) {
				this.classComplexity += result.getEntityComplexity();
				outputData.add(new CalculusResultWrapper(
						result.getEntityPosition(), 
						result.getEntityDeclaration(), 
						result.getEntityComplexity()));
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
	private void setAllGraficComponents() {
		mainStage.setTitle("Cognitive Compelxity");
		mainStage.setResizable(false);
		mainStage.setWidth(750);
		mainStage.setHeight(550);
		
		final Label label = new Label("Complexity of class "+this.className+": "+this.classComplexity);
		label.setFont(new Font("Arial", 15));
		label.setPadding(new Insets(10, 10, 0, 0));
		 
		TableColumn line = new TableColumn<>("Line");
		line.setCellValueFactory(new PropertyValueFactory<>("entityPosition"));
		line.setResizable(true);
		line.setMaxWidth(400);
		line.setStyle("-fx-alignment: CENTER;");
		
		TableColumn methodDeclaration = new TableColumn<>("Method Declaration");
        methodDeclaration.setCellValueFactory(new PropertyValueFactory<>("entityDeclaration"));
        methodDeclaration.setResizable(true);
		
		TableColumn complexity = new TableColumn<>("Complexity");
		complexity.setCellValueFactory(new PropertyValueFactory<>("entityComplexity"));
        complexity.setResizable(true);
        complexity.setMaxWidth(800);
        complexity.setStyle("-fx-alignment: CENTER;");
		
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setItems(outputData);
        tableView.setEditable(false);
        tableView.getColumns().addAll(line, methodDeclaration, complexity);
        
        final VBox vbox = new VBox();
        vbox.setVgrow(tableView, Priority.ALWAYS);
        
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        
        
        Button exportButton = new Button("Export CSV");
        exportButton.setOnAction(event -> { 
	    	Alert alert = new Alert(AlertType.NONE);
	    	alert.setTitle("Operation Result");
	    	
	    	boolean result = new CSVGenerator(path, calculusResult).createAndWriteCSVFile();
	    	
	    	if(result) {
	    		alert.setAlertType(AlertType.CONFIRMATION);
	        	alert.setHeaderText("File Creation Successful");
	    	}else {
	    		alert.setAlertType(AlertType.WARNING);
	    		alert.setHeaderText("File not created. Something went wrong, see log file.");
	    	}
	    	alert.show();
        });
        
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {
        	
            mainStage.setScene(previousScene);
            mainStage.sizeToScene();
        });
        
        final HBox hBox = new HBox();
        hBox.setAlignment(Pos.TOP_RIGHT);
        hBox.setMargin(backButton, new Insets(10, 10, 10, 10));
        hBox.setMargin(exportButton, new Insets(10, 10, 10, 10));
        hBox.getChildren().addAll(backButton, exportButton);
        
        vbox.getChildren().addAll(label, tableView, hBox);
 
        complexityFileScene = new Scene(new VBox());
        ((VBox) complexityFileScene.getRoot()).getChildren().addAll(vbox);
        
        mainStage.setScene(complexityFileScene);
        mainStage.show();
	}
}
