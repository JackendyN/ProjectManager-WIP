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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ScheduleView extends OptionScreen {
	
	// Base X: 144
	// Rectangle Width: 608
	@FXML public Rectangle baseRectangle;
	@FXML public Label errorLabel;
	@FXML public Label descriptionLabel;
	@FXML public Label scheduleEmptyLabel;
	@FXML public DatePicker dateField;
	@FXML public Label minimumTimeLabel;
	@FXML public TextField minimumField;
	@FXML public Label maximumTimeLabel;
	@FXML public TextField maximumField;
	@FXML public Button okMinimum;
	@FXML public Button cancelMinimum;
	@FXML public Button okMaximum;
	@FXML public Button cancelMaximum;
	int minimumMinute = 0;
	int maximumMinute = 1440;
	DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
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
		int totalTime = maximumMinute - minimumMinute;
		int distanceFromStart = minimumMinute;
		float scale = (float)baseRectangle.getWidth() / totalTime;
		final double baseX = baseRectangle.getLayoutX();
		AnchorPane pane = (AnchorPane)baseRectangle.getParent(); 
		ClearBlocks(pane);
		
		for (TimeBlock block : schedule.timeBlocks) {
			Rectangle newRectangle = new Rectangle(); 
			pane.getChildren().add(newRectangle);
			currentBlocks.add(newRectangle);
			newRectangle.setLayoutY(baseRectangle.getLayoutY());
			newRectangle.setHeight(baseRectangle.getHeight());

			float startPoint = (float)block.timeStart - distanceFromStart;
			float startDiff = 0;
			if(startPoint < 0) {
				startDiff = Math.abs(startPoint);
				startPoint = 0;
			}
			float widthEnd = Math.min((float)block.timeEnd, maximumMinute);
			newRectangle.setWidth((widthEnd - (float)block.timeStart - startDiff) * scale);
			newRectangle.setFill(block.blockColor.getColor());
			newRectangle.setLayoutX(baseX + (startPoint * scale));
			
			newRectangle.setOnMouseEntered(event -> {
				descriptionLabel.setVisible(true);
				descriptionLabel.setLayoutX(newRectangle.getLayoutX() - (newRectangle.getLayoutX() / 4));
				descriptionLabel.setText(block.toString());
			});
			
			newRectangle.setOnMouseClicked(event -> {
				Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
				confirmAlert.setTitle("Block Removal");
				confirmAlert.setHeaderText("Removing time block.");
				confirmAlert.setContentText("Are you sure you want to delete " + block + "?");

				Optional<ButtonType> buttonType = confirmAlert.showAndWait();
				if(buttonType.isPresent() && buttonType.get().equals(ButtonType.OK)) {
					currentSchedule.timeBlocks.remove(block);
					SaveSchedules(scheduleList);
					SetSchedule(currentSchedule);
				}
			});
			
			newRectangle.setOnMouseExited(event -> descriptionLabel.setVisible(false));
			
		}
	}

	public void ShowTimeField(MouseEvent e) {
		Label label = (Label)e.getSource();
		TextField field;
		Button ok;
		Button cancel;
		if(label.equals(minimumTimeLabel)) {
			field = minimumField;
			ok = okMinimum;
			cancel = cancelMinimum;
		} else if(label.equals(maximumTimeLabel)) {
			field = maximumField;
			ok = okMaximum;
			cancel = cancelMaximum;
		} else {
			return;
		}
		field.setVisible(true);
		field.setText(label.getText());
		ok.setVisible(true);
		cancel.setVisible(true);
		cancel.setOnMouseClicked(event -> {
			field.setVisible(false);
			ok.setVisible(false);
			cancel.setVisible(false);
		});
	}

	public void UpdateTime(MouseEvent e) {
		TextField timeField;
		boolean changingMinimum;
		if(e.getSource().equals(okMinimum)) {
			timeField = minimumField;
			changingMinimum = true;
		} else if(e.getSource().equals(okMaximum)) {
			timeField = maximumField;
			changingMinimum = false;
		} else {
			return;
		}

		LocalTime newTime;
		int newMinutes;
		try {
			newTime = LocalTime.parse(timeField.getText(), timeFormatter);
			newMinutes = (newTime.getHour() * 60) + newTime.getMinute();
			if((changingMinimum && newMinutes > maximumMinute) || !changingMinimum && newMinutes < minimumMinute) {
				throw new TimeRangeException();
			}
		} catch(DateTimeParseException | TimeRangeException te) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid Time");
			alert.setContentText("Please make sure that you have entered an appropriate time.");
			alert.showAndWait();
			return;
		}

		if(changingMinimum) {
			minimumMinute = newMinutes;
			minimumTimeLabel.setText(timeField.getText());
			okMinimum.setVisible(false);
			cancelMinimum.setVisible(false);
			minimumField.setVisible(false);
		} else {
			maximumMinute = newMinutes;
			maximumTimeLabel.setText(timeField.getText());
			okMaximum.setVisible(false);
			cancelMaximum.setVisible(false);
			maximumField.setVisible(false);
		}
		SetSchedule(currentSchedule);

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
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
			try {
				LocalTime blockStart = LocalTime.parse(startField.getText(), formatter);
				LocalTime blockEnd = LocalTime.parse(endField.getText(), formatter);
				String blockDescription = descriptionField.getText();
				Color blockColor = colorField.getValue();
				TimeBlock block = new TimeBlock(blockStart.toSecondOfDay() / 60, blockEnd.toSecondOfDay() / 60, blockDescription, blockColor);
				int previousSize = currentSchedule.timeBlocks.size();
				currentSchedule.AddRange(block);
				if(projectField != null) { 
					block.projectName = projectField.getValue();
				} 

				if(currentSchedule.timeBlocks.size() != previousSize) {
					errorLabel.setVisible(false);
					SaveSchedules(scheduleList);
					SetSchedule(currentSchedule);
					System.out.println("Done!");
				} else { // Fail-safe
					errorLabel.setVisible(true);
					errorLabel.setText("The time block was not able to be added.");
				}
				
			} catch (TimeRangeException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Invalid Time Range");
				alert.setContentText("Please make sure that you have entered appropriate start and end times.");
				alert.showAndWait();

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

	public void GoBack() throws IOException {
		hoverLabel = null;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
		SceneManager.SwitchToScene(loader, descriptionLabel);
	}
	
}
