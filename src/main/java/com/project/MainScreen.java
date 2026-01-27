package com.project;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class MainScreen {

	public void SwitchToProjects(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Projects.fxml"));
        SceneManager.SwitchToScene(loader, (Node)e.getSource());
    }
	
	public void SwitchToTime(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TimeMainScreen.fxml"));
        SceneManager.SwitchToScene(loader, (Node)e.getSource());
    }
}
