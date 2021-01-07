module uninsubria {
    requires javafx.controls;
    requires javafx.fxml;
	requires log4j.api;
	requires commons.csv;
	requires com.github.javaparser.core;

    opens uninsubria to javafx.fxml;
    exports uninsubria.graficuserinterface;
}