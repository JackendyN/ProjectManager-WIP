package com.project;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class CreateNewProject {
	
	Parent root;

	@FXML public TextField nameField;
	@FXML public TextField typeField;
	@FXML public TextField timeField;
	@FXML public DatePicker dateField;
	@FXML public DatePicker deadlineField;
	@FXML public Label backLabel;
	FXMLLoader loader = new FXMLLoader(getClass().getResource("Projects.fxml"));
	
	public void VerifyContents() throws IOException {
		Alert alert = new Alert(AlertType.ERROR);
		if(nameField.getText().isEmpty() || timeField.getText().isEmpty() || dateField.getValue() == null) {
			alert.setTitle("Missing Details");
			alert.setContentText("One or more fields is missing information.");
			alert.showAndWait();
			return;
		} try {
			Integer.parseInt(timeField.getText());
		} catch (Exception e) {
			alert.setTitle("Invalid Time");
			alert.setContentText("Make sure the estimated time is in a whole number.");
			alert.showAndWait();
			return;
		} try {
			@SuppressWarnings("unused")
			LocalDate tempDate;
			tempDate = dateField.getValue();
			if(deadlineField.getValue() != null) { tempDate = deadlineField.getValue(); }
		} catch (Exception e) {
			alert.setTitle("Invalid Date(s)");
			alert.setContentText("One or more entered dates is incorrect. Please use the date picker provided.");
			alert.showAndWait();
			return;
		}
		
		CreateProject(!typeField.getText().isEmpty(), deadlineField.getValue() != null);
		
	}
	
	void CreateProject(boolean hasType, boolean hasDeadline) throws IOException {
		
		Project newProject = new Project();
		ArrayList<Project> currentProjects = new ArrayList<Project>();
		File file = new File("projects.bin");
		Alert a = new Alert(AlertType.ERROR);
		
		if(file.exists()) { 
			currentProjects = SaveLoadProjects.LoadProjects(file);
		}
		
		if(currentProjects.size() >= 10) {
			a.setTitle("Too many projects");
			a.setContentText("A maximum number of 10 projects has been reached.");
			a.showAndWait();
			return;
		}
		
		newProject.projectName = nameField.getText();
		for (Project proj : currentProjects) {
			if(newProject.projectName.equals(proj.projectName)) {
				a.setTitle("Pre-existing Project");
				a.setContentText("A project of this name already exists.");
				a.showAndWait();
				return;
			}
		}
		
		newProject.estimatedHours = Integer.parseInt(timeField.getText());
		newProject.startDate = dateField.getValue();
		newProject.projectTasks = new ArrayList<Task>();
		
		if(hasDeadline) {
			newProject.deadLine = deadlineField.getValue();
		} else {
			newProject.deadLine = null;
		}
		
		if(hasType) {
			newProject.type = typeField.getText();
		} else {
			newProject.type = null;
		}
		
		currentProjects.add(newProject);
		SaveLoadProjects.SaveProjects(currentProjects, file);
		
        root = loader.load();
		
        ProjectMenu menu = loader.getController();
        menu.CreatedProjectText();
        SceneManager.SwitchToScene(root, loader, backLabel);
		
	}
	
	public void BackHover() {
		backLabel.setUnderline(true);
	}
	
	public void BackUnhover() {
		backLabel.setUnderline(false);
	}
	
	public void GoBack(MouseEvent e) throws IOException {
		SceneManager.SwitchToScene(loader, backLabel);
	}
	
}
