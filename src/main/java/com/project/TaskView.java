package com.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class TaskView {
	Parent root;

	@FXML public ListView<String> listView;
	@FXML public Label nameDisplay;
	@FXML public Label descriptionDisplay;
	@FXML public TextField orderField;
	@FXML public CheckBox doneCheckBox;
	
	ArrayList<Project> projects;
	Project parentProject;
	Task selectedTask = null;
	int projectIndex;
	
	public void SetProject(Project givenProject, ArrayList<Project> projectList) {
		parentProject = givenProject;
		projects = projectList;
		projectIndex = projectList.indexOf(givenProject);
		
		ArrayList<String> projectTaskNames = new ArrayList<String>();
		for (Task task : givenProject.projectTasks) {
			projectTaskNames.add(task.name);
		}
		
		listView.getItems().addAll(projectTaskNames);
		nameDisplay.setText("Task (" + parentProject.projectName + ")");
		listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				for (Task t : givenProject.projectTasks) {
					if(listView.getSelectionModel().getSelectedItem() == t.name) {
						SetTaskDetails(t);
						break;
					}
				}
			}
			
		});
	}
	
	void SetTaskDetails(Task task) {
		selectedTask = task;
		nameDisplay.setText(task.name + " (" + parentProject.projectName + ")");
		descriptionDisplay.setText(task.description);
		doneCheckBox.setSelected(task.isDone);
	}
	
	public void ChangeTaskOrder() {
		if(selectedTask == null) { return; }
		
		int newIndex = 0;
		Alert errorAlert = new Alert(AlertType.ERROR);
		
		try {
			newIndex = Integer.parseInt(orderField.getText()) - 1;
		} catch (Exception e) {
			errorAlert.setTitle("Number Error");
			errorAlert.setHeaderText("Incorrect number");
			errorAlert.setContentText("Input a whole number.");
			errorAlert.showAndWait();
			return;
		}
		
		try {
			parentProject.projectTasks.remove(selectedTask);
			parentProject.projectTasks.add(newIndex, selectedTask);
		} catch (IndexOutOfBoundsException e) {
			errorAlert.setTitle("Number Error");
			errorAlert.setHeaderText("Specified list location does not exist");
			errorAlert.setContentText("Specify a location that exists within the task list.");
			errorAlert.showAndWait();
			return;
		}

		RefreshTasks(selectedTask);
		
	}
	
	public void DeleteTask() throws IOException {
		if(selectedTask == null) { return; }
		
		Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
		confirmAlert.setTitle("Task Removal");
		confirmAlert.setHeaderText("Removing the selected task.");
		confirmAlert.setContentText("Are you sure you want to delete " + selectedTask.name.toUpperCase() + "?");
		
		Optional<ButtonType> buttonType = confirmAlert.showAndWait();
		if(buttonType.isPresent() && buttonType.get().equals(ButtonType.OK)) {
			parentProject.projectTasks.remove(selectedTask);
			if(parentProject.projectTasks.isEmpty()) {
				projects.set(projectIndex, parentProject);
				SaveLoadProjects.SaveProjects(projects);
				FXMLLoader loader = new FXMLLoader(getClass().getResource("ProjectView.fxml"));
		        root = loader.load();
		        ProjectView pView = loader.getController();
		        pView.SetProjectDetails(parentProject, projects);
		        SceneManager.SwitchToScene(root, loader, nameDisplay);
			} else {
				RefreshTasks(parentProject.projectTasks.get(0));
			}
        }
		
	}
	
	public void UpdateTaskCompletion() {
		if(selectedTask == null) { return; }
		if(!selectedTask.isDone && doneCheckBox.isSelected()) {
			selectedTask.isDone = true;
			selectedTask.name = "(DONE) " + selectedTask.name;
			parentProject.projectTasks.remove(selectedTask);
			parentProject.projectTasks.add(parentProject.projectTasks.size(), selectedTask);
		} else if(selectedTask.isDone && !doneCheckBox.isSelected()) {
			selectedTask.isDone = false;
			selectedTask.name = selectedTask.name.substring(7);
			parentProject.projectTasks.remove(selectedTask);
			parentProject.projectTasks.add(parentProject.projectTasks.size(), selectedTask);
		}
		
		RefreshTasks(selectedTask);
		
	}
	
	void RefreshTasks(Task taskToShow) {
		projects.set(projectIndex, parentProject);
		SaveLoadProjects.SaveProjects(projects);
		listView.getItems().clear();
		SetProject(parentProject, projects);
		SetTaskDetails(taskToShow);
	}
	
	public void GoBack(MouseEvent pBack) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("ProjectView.fxml"));
        root = loader.load();
        ProjectView pView = loader.getController();
        pView.SetProjectDetails(parentProject, projects);
        SceneManager.SwitchToScene(root, loader, nameDisplay);
	}
	
	Label backLabel;
	public void BackHover(MouseEvent h) {
		backLabel = (Label)h.getTarget();
		backLabel.setUnderline(true);
	}
	
	public void BackUnhover(MouseEvent h) {
		backLabel = (Label)h.getTarget();
		backLabel.setUnderline(false);
	}
	
}
