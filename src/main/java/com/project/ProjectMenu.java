package com.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;


public class ProjectMenu {
	
	Parent root;
	
	ArrayList<Project> projectList = new ArrayList<Project>();
	ArrayList<Label> labelList = new ArrayList<Label>();

	@FXML public Button newProjectButton;
	@FXML public Label confirmationLabel;
	
	@FXML public Label labelOne;
	@FXML public Label labelTwo;
	@FXML public Label labelThree;
	@FXML public Label labelFour;
	@FXML public Label labelFive;
	@FXML public Label labelSix;
	@FXML public Label labelSeven;
	@FXML public Label labelEight;
	@FXML public Label labelNine;
	@FXML public Label labelTen;
	
	public void initialize() {
		UpdateProjects();
		CreateProjectLabels();
	}
	
	public void NewProject(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("NewProject.fxml"));
        SceneManager.SwitchToScene(loader, confirmationLabel);
	}
	
	void UpdateProjects() {
		
		File file = new File("projects.bin");
		if(!file.exists()) {
			System.out.println("No project currently exists.");
			return;
		}
		
		projectList = SaveLoadProjects.LoadProjects();
	}
	
	void CreateProjectLabels() {
		Collections.addAll(labelList, labelOne, labelTwo, labelThree, labelFour, labelFive, labelSix, labelSeven, labelEight, labelNine, labelTen);
		
		for (Project p : projectList) {
			Label currentLabel = labelList.get(projectList.indexOf(p));
			currentLabel.setVisible(true);
			currentLabel.setText(p.projectName);
		}
	}
	
	Label hoverLabel;
	public void OnProjectClick(MouseEvent mEvent) throws IOException {
		Boolean projectFound = false;
		for (Project proj : projectList) {
			if(proj.projectName == hoverLabel.getText()) {
				projectFound = true;
				FXMLLoader loader = new FXMLLoader(getClass().getResource("ProjectView.fxml"));
		        root = loader.load();
		        ProjectView pView = loader.getController();
		        pView.SetProjectDetails(proj, projectList);
		        SceneManager.SwitchToScene(root, loader, confirmationLabel);
			}
		}
		if(!projectFound) {
			System.out.println("Project not found.");
		}
	}
	
	public void CreatedProjectText() {
		confirmationLabel.setVisible(true);
	}
	
	
	public void LabelHover(MouseEvent e) {
		hoverLabel = (Label)e.getTarget();
		hoverLabel.setUnderline(true);
	}
	
	public void LabelUnhover(MouseEvent e) {
		hoverLabel.setUnderline(false);
		hoverLabel = null;
	}
	
	public void GoBack(MouseEvent eBack) throws IOException {
		hoverLabel = null;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
		SceneManager.SwitchToScene(loader, confirmationLabel);
	}
	
}
