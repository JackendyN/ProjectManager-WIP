package com.project;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        Scene scene = new Scene(root);

        Image icon = new Image(getClass().getResourceAsStream("clockicon.png"));
        stage.getIcons().add(icon);

        stage.setResizable(true);
        stage.setTitle("Time Manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
    	System.out.println("Started!");
        launch();
    }

}