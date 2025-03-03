package com.project;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
	
	@SuppressWarnings("exports")
	public static void SwitchToScene(FXMLLoader loader, Node sampleNode) throws IOException {
		
		Stage stage;
		Scene scene;
		Parent root;
        root = loader.load();
        stage = (Stage)(sampleNode.getScene().getWindow());
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        
	}
	
	@SuppressWarnings("exports")
	public static void SwitchToScene(Parent givenRoot, FXMLLoader loader, Node sampleNode) throws IOException {
		
		Stage stage;
		Scene scene;
        stage = (Stage)(sampleNode.getScene().getWindow());
        scene = new Scene(givenRoot);
        stage.setScene(scene);
        stage.show();
        
	}
	
}
