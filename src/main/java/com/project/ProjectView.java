package com.project;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class ProjectView {
	Parent root;
	
	ArrayList<Project> projectList;
	Project currentProject;
	int projectIndex;

	@FXML public Label projectLabel;
	@FXML public Label hourLabel;
	@FXML public Label startDateLabel;
	@FXML public Label endDateLabel;
	
	@FXML public Label editLabel;
	@FXML public Label hoverLabel;
	@FXML public Label lastLabel;
	
	@FXML public TextField editField;
	@FXML public DatePicker editDateField;
	
	@FXML public Button okButton;
	@FXML public Button cancelButton;
	@FXML public Button deleteButton;
	
	@FXML public Label taskOne;
	@FXML public Label taskTwo;
	@FXML public Label taskThree;
	@FXML public Label taskView;
	@FXML public Label taskDescription;
	
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/YYYY");
	
	Alert alert = new Alert(AlertType.ERROR);
	
	public void SetProjectDetails(Project selectedProject, ArrayList<Project> list) {
		projectList = list;
		currentProject = selectedProject;
		projectIndex = projectList.indexOf(selectedProject);
		projectLabel.setText(selectedProject.projectName);
		if(!(selectedProject.type == null)) {
			projectLabel.setText(selectedProject.projectName + " (" + selectedProject.type + ")");
		}
		
		hourLabel.setText("Estimated Time: " + Integer.toString(selectedProject.estimatedHours) + " Hours");
		
		startDateLabel.setText("Start Date: " + formatter.format(selectedProject.startDate));
		if(selectedProject.deadLine == null) {
			endDateLabel.setVisible(false);
		} else {
			endDateLabel.setText("Deadline: " + formatter.format(selectedProject.deadLine));
			if(LocalDate.now().isBefore(selectedProject.deadLine)) {
				endDateLabel.setTextFill(Color.GREEN);
			} else if (LocalDate.now().isEqual(selectedProject.deadLine)) {
				endDateLabel.setTextFill(Color.ORANGE);
			} else if (LocalDate.now().isAfter(selectedProject.deadLine)) {
				endDateLabel.setTextFill(Color.RED);
			}
		}
		
		SetTasks();

	}
	
	public void StartEdit() {
		lastLabel = hoverLabel;
		editLabel.setVisible(false);
		okButton.setVisible(true);
		cancelButton.setVisible(true);
		
		if(lastLabel == startDateLabel || lastLabel == endDateLabel) {
			editDateField.setVisible(true);
			editDateField.setLayoutX(hoverLabel.getLayoutX());
			editDateField.setLayoutY(hoverLabel.getLayoutY() + 34);
			okButton.setLayoutX(editDateField.getLayoutX() + 194);
			okButton.setLayoutY(editDateField.getLayoutY());
		} else {
			editField.setVisible(true);
			editField.setLayoutX(hoverLabel.getLayoutX());
			editField.setLayoutY(hoverLabel.getLayoutY() + 34);
			okButton.setLayoutX(editField.getLayoutX() + 161);
			okButton.setLayoutY(editField.getLayoutY());
		}
		
		cancelButton.setLayoutX(okButton.getLayoutX() + 46);
		cancelButton.setLayoutY(okButton.getLayoutY());
	}
	
	public void EditProperty() {
		
		if((lastLabel == startDateLabel || lastLabel == endDateLabel) && editDateField.getValue() == null) {
			EndEdit();
			return;
		} else if((lastLabel == projectLabel || lastLabel == hourLabel) && editField.getText().isEmpty()) {
			EndEdit();
			return;
		}
		
		if(lastLabel == projectLabel) {
			currentProject.projectName = editField.getText();
			projectLabel.setText(editField.getText());
			
			if(currentProject.type != null) {
				TextInputDialog textInput = new TextInputDialog(currentProject.type);
				textInput.setHeaderText("Enter a new type of project (Leave blank to not change)");
				Optional<String> result = textInput.showAndWait();
				if(result.isPresent()) {
					currentProject.type = result.get();
					projectLabel.setText(currentProject.projectName + " (" + currentProject.type + ")");
				} 
			}
			
		} else if(lastLabel == hourLabel) {
			try {
				currentProject.estimatedHours = Integer.parseInt(editField.getText());
				hourLabel.setText("Estimated Time: " + editField.getText() + " Hours");
			} catch (Exception e) {
				alert.setTitle("Invalid Time");
				alert.setContentText("Make sure the estimated time is in a whole number.");
				alert.showAndWait();
				EndEdit();
				return;
			}
			
		} else if(lastLabel == startDateLabel) {
			try {
				currentProject.startDate = editDateField.getValue();
;				startDateLabel.setText("Start Date: " + formatter.format(currentProject.startDate));
			} catch (Exception e) {
				alert.setTitle("Invalid Date");
				alert.setContentText("Please put the date in the correct format.");
				alert.showAndWait();
				EndEdit();
				return;
			}
			
		} else if(lastLabel == endDateLabel) {
			try {
				currentProject.deadLine = editDateField.getValue();
				endDateLabel.setText("Deadline: " + formatter.format(currentProject.deadLine));
			} catch (Exception e) {
				alert.setTitle("Invalid Date");
				alert.setContentText("Please put the date in the correct format.");
				alert.showAndWait();
				EndEdit();
				return;
			}
		}
		
		UpdateProject();
		EndEdit();
		
	}
	
	public void CancelEdit(ActionEvent ae) {
		EndEdit();
	}
	
	void EndEdit() {
		editField.setText("");
		editDateField.setValue(null);
		editField.setVisible(false);
		editDateField.setVisible(false);
		okButton.setVisible(false);
		cancelButton.setVisible(false);
	}
	
	void UpdateProject() {
		projectList.set(projectIndex, currentProject);
		SaveLoadProjects.SaveProjects(projectList);
	}
	
	public void OnDelete() throws IOException {
		Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
		confirmAlert.setTitle("Project Removal");
		confirmAlert.setHeaderText("Removing the project.");
		confirmAlert.setContentText("Are you sure you want to delete " + currentProject.projectName.toUpperCase() + "?");
		
		Optional<ButtonType> buttonType = confirmAlert.showAndWait();
		if(buttonType.isPresent() && buttonType.get().equals(ButtonType.OK)) {
			projectList.remove(currentProject);
			SaveLoadProjects.SaveProjects(projectList);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Projects.fxml"));
			SceneManager.SwitchToScene(loader, cancelButton);
        }
	}
	
	public void CreateNewTask() {
		Task newTask = new Task();
		
		TextInputDialog taskNameInput = new TextInputDialog();
		taskNameInput.setTitle("New Task");
		taskNameInput.setContentText("Enter a new task for the project");
		Optional<String> result = taskNameInput.showAndWait();
		if(result.isPresent()) {
			newTask.isDone = false;
			newTask.name = result.get();
		} else {
			return;
		}
		
		TextInputDialog taskDescriptionInput = new TextInputDialog();
		taskDescriptionInput.setTitle("New Task");
		taskDescriptionInput.setContentText("Enter a description for the task.");
		result = taskDescriptionInput.showAndWait();
		if(result.isPresent()) {
			newTask.isDone = false;
			newTask.description = result.get();
			currentProject.projectTasks.add(newTask);
			UpdateProject();
			SetTasks();
		} 
	}
	
	void SetTasks() {
		ArrayList<Label> taskLabels = new ArrayList<Label>();
		Collections.addAll(taskLabels, taskOne, taskTwo, taskThree);
		
		for (int i = 0; i < currentProject.projectTasks.size(); i++) {
			taskLabels.get(i).setVisible(true);
			taskLabels.get(i).setText(currentProject.projectTasks.get(i).name);
			if(i == 1) {
				taskView.setVisible(true);
			} else if(i == 2) {
				break;
			}
		}
	}
	
	public void LabelHover(MouseEvent e) {
		hoverLabel = (Label)e.getTarget();
		hoverLabel.setUnderline(true);
		if(hoverLabel == projectLabel || hoverLabel == hourLabel || hoverLabel == startDateLabel || hoverLabel == endDateLabel) {
			editLabel.setLayoutX(hoverLabel.getLayoutX());
			editLabel.setLayoutY(hoverLabel.getLayoutY() + 40);
			editLabel.setVisible(true);
		}
	}
	
	public void LabelUnhover(MouseEvent e) {
		hoverLabel = (Label)e.getTarget();
		hoverLabel.setUnderline(false);
		if(hoverLabel == projectLabel || hoverLabel == hourLabel || hoverLabel == startDateLabel || hoverLabel == endDateLabel) {
			editLabel.setVisible(false);
		}
	}
	
	Label hoverTask;
	public void TaskHover(MouseEvent mEvent) {
		hoverTask = (Label)mEvent.getTarget();
		
		Task currentTask = null;
		for (Task t : currentProject.projectTasks) {
			if(t.name.equals(hoverTask.getText())) {
				currentTask = t;
			}
		}
		
		if(!hoverTask.getText().equals("View Project Tasks")) {
			taskDescription.setVisible(true);
			taskDescription.setText("(" + currentTask.description + ")");
			taskDescription.setLayoutX(hoverTask.getLayoutX() - 93);
			taskDescription.setLayoutY(hoverTask.getLayoutY() + 27);
		} else {
			hoverTask.setUnderline(true);
		}
	}
	
	public void TaskUnhover(MouseEvent mEvent) {
		hoverTask = (Label)mEvent.getTarget();
		hoverTask.setUnderline(false);
		if(!hoverTask.getText().equals("View Project Tasks")) {
			taskDescription.setVisible(false);
		}
	}
	
	public void ViewTasks(MouseEvent vTasks) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("TaskView.fxml"));
        root = loader.load();
        TaskView tView = loader.getController();
        tView.SetProject(currentProject, projectList);
        SceneManager.SwitchToScene(root, loader, (Node)vTasks.getSource());
	}
	
	public void GoBack(MouseEvent pBack) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Projects.fxml"));
		SceneManager.SwitchToScene(loader, endDateLabel);
	}
	
}
