package com.project;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class TimeMain {

	public void ViewSchedules(ActionEvent e) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("DailySchedule.fxml"));
        SceneManager.SwitchToScene(loader, (Node)e.getSource());
	}
	
}
