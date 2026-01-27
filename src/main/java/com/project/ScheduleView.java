package com.project;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ScheduleView {
	
	// Base X: 144
	// Rectangle Width: 608
	@FXML public Rectangle baseRectangle;
	@FXML public Label errorLabel;
	@FXML public Label descriptionLabel;
	@FXML public Label scheduleEmptyLabel;
	@FXML public DatePicker dateField;
	Schedule currentSchedule = null;
	ArrayList<Schedule> scheduleList;
	ArrayList<Rectangle> currentBlocks = new ArrayList<Rectangle>();
	static File scheduleFile = new File("Schedules.bin");
	
	@FXML public void initialize() {
		
		if(scheduleFile.exists()) {
			scheduleList = LoadSchedules();
		} else {
			scheduleList = new ArrayList<Schedule>();
		}
		
		dateField.setOnAction(event -> {
			boolean dateFound = false;
			for (Schedule schedule : scheduleList) {
				if(schedule.date.equals(dateField.getValue())) {
					scheduleEmptyLabel.setVisible(false);
					currentSchedule = schedule;
					SetSchedule(schedule);
					dateFound = true;
					break;
				}
			}
			
			if(!dateFound) scheduleEmptyLabel.setVisible(true);
		});
		
		scheduleEmptyLabel.setOnMouseClicked(event -> {
			if(dateField.getValue() != null) {
				currentSchedule = new Schedule(dateField.getValue());
				scheduleList.add(currentSchedule);
				SaveSchedules(scheduleList);
				SetSchedule(currentSchedule);
				scheduleEmptyLabel.setVisible(false);
			}
		});
		
		scheduleEmptyLabel.setOnMouseEntered(event -> scheduleEmptyLabel.setUnderline(true));
		scheduleEmptyLabel.setOnMouseExited(event -> scheduleEmptyLabel.setUnderline(false));
		
	}
	
	public void SetSchedule(Schedule schedule) {
		final float scale = (float)baseRectangle.getWidth() / 1440;
		final double baseX = baseRectangle.getLayoutX();
		AnchorPane pane = (AnchorPane)baseRectangle.getParent(); 
		ClearBlocks(pane);
		
		for (TimeBlock block : schedule.timeBlocks) {
			Rectangle newRectangle = new Rectangle(); 
			pane.getChildren().add(newRectangle);
			currentBlocks.add(newRectangle);
			newRectangle.setLayoutY(baseRectangle.getLayoutY());
			newRectangle.setHeight(baseRectangle.getHeight());
			
			newRectangle.setWidth(((float)block.timeEnd - (float)block.timeStart) * scale);
			newRectangle.setFill(block.blockColor.getColor());
			newRectangle.setLayoutX(baseX + ((float)block.timeStart * scale));
			
			newRectangle.setOnMouseEntered(event -> {
				descriptionLabel.setVisible(true);
				descriptionLabel.setLayoutX(newRectangle.getLayoutX() - (newRectangle.getLayoutX() / 4));
				descriptionLabel.setText(block.ToString());
			});
			
			newRectangle.setOnMouseClicked(event -> {
				currentSchedule.timeBlocks.remove(block);
				scheduleList.add(currentSchedule);
				SaveSchedules(scheduleList);
				SetSchedule(currentSchedule);
			});
			
			newRectangle.setOnMouseExited(event -> descriptionLabel.setVisible(false));
			
		}
	}
	
	void ClearBlocks(AnchorPane pane) {
		for (Rectangle rectangle : currentBlocks) pane.getChildren().remove(rectangle);
		currentBlocks.removeAll(currentBlocks);
	}
	
	@SuppressWarnings("unchecked")
	public void NewBlock() throws IOException {
		
		if(currentSchedule == null) {
			errorLabel.setVisible(true);
			errorLabel.setText("No schedule selected.");
			return;
		}
		
		errorLabel.setVisible(false);
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("New Time Block");
		dialog.setHeaderText("Enter Details:");
		
		FXMLLoader dialogLoader = new FXMLLoader(getClass().getResource("ScheduleDialog.fxml"));
		dialog.getDialogPane().setContent(dialogLoader.load());
		
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		TextField startField = (TextField)dialog.getDialogPane().lookup("#startField");
		TextField endField = (TextField)dialog.getDialogPane().lookup("#endField");
		TextField descriptionField = (TextField)dialog.getDialogPane().lookup("#descriptionField");
		ColorPicker colorField = (ColorPicker)dialog.getDialogPane().lookup("#colorField");
		ComboBox<String> projectField = (ComboBox<String>)dialog.getDialogPane().lookup("#projectField");
		
		if(SaveLoadProjects.GetFile().exists()) {
			ArrayList<Project> projects = SaveLoadProjects.LoadProjects();
			ArrayList<String> projectNameList = new ArrayList<String>();
			for (Project project : projects) {
				projectNameList.add(project.projectName);
			}
			projectField.setItems(FXCollections.observableArrayList(projectNameList));
		}
		
		Optional<ButtonType> button = dialog.showAndWait();
		if(button.isPresent() && button.get() == ButtonType.OK) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
			try {
				LocalTime blockStart = LocalTime.parse(startField.getText(), formatter);
				LocalTime blockEnd = LocalTime.parse(endField.getText(), formatter);
				String blockDescription = descriptionField.getText();
				Color blockColor = colorField.getValue();
				TimeBlock block = new TimeBlock(blockStart.toSecondOfDay() / 60, blockEnd.toSecondOfDay() / 60, blockDescription, blockColor);
				if(projectField != null) { 
					block.projectName = projectField.getValue();
				} 
				int previousSize = currentSchedule.timeBlocks.size();
				currentSchedule.AddRange(block);
				
				if(currentSchedule.timeBlocks.size() != previousSize) {
					errorLabel.setVisible(false);
					scheduleList.add(currentSchedule);
					SaveSchedules(scheduleList);
					SetSchedule(currentSchedule);
					System.out.println("Done!");
				} else {
					errorLabel.setVisible(true);
					errorLabel.setText("The time block was not able to be added.");
				}
				
			} catch (Exception e) {
				System.err.print(e);
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Missing/Incorrect Details");
				alert.setContentText("Something happened. Please make sure you entered everything correctly, then try again.");
				alert.showAndWait();
			}
		}
		
	}
	
	void SaveSchedules(ArrayList<Schedule> schedules) {
		try {
			FileOutputStream outputStream = new FileOutputStream(scheduleFile);
			ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
			for (Schedule s : schedules) objectStream.writeObject(s);
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Something went wrong. Try again.");
			return;
		}
	}
	
	ArrayList<Schedule> LoadSchedules() {
		ArrayList<Schedule> currentList = new ArrayList<Schedule>();
		
		try {
			FileInputStream fin = new FileInputStream(scheduleFile);
			ObjectInputStream ois = new ObjectInputStream(fin);
			Object object;
			
			while(true) {
				
				try {
					object = ois.readObject();
					if(object instanceof Schedule) {
						currentList.add((Schedule)object);
					} else {
						System.err.println("Something went wrong.");
					}
				} catch (EOFException ex) {
					break;
				}
				
			}

			ois.close();
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return currentList;
	}
	
}
