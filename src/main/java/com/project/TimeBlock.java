package com.project;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javafx.scene.paint.Color;

@SuppressWarnings("serial")
public class TimeBlock implements Serializable {
	
	// In Minutes
		String timeDescription;
		public String projectName = null;
		int timeStart = 0;
		int timeEnd = 0;
		SerializableColor blockColor;
		
		public TimeBlock(int start, int end, String description, Color color) {
			
			timeDescription = description;
			blockColor = new SerializableColor(color);
			
			if(start < 0 || end > 1440) {
				System.out.println("Invalid Time Range.");
				return;
			}
			
			timeStart = start;
			if(end > start) {
				timeEnd = end;
			} else {
				System.out.println("Invalid time range. End time was automatically assigned to an hour after the start time.");
				timeEnd = end + 60;
			}
			
		}
		
		public Boolean inRange(int number) {
			return (number >= timeStart) && (number <= timeEnd);
		}
		
		public String ToString() {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);
			LocalTime startTime;
			LocalTime endTime;
			startTime = LocalTime.of((timeStart / 60), (timeStart % 60));
			endTime = LocalTime.of((timeEnd / 60), (timeEnd % 60));
			String string = (startTime.format(formatter) + " - " + endTime.format(formatter) + ": " + timeDescription);
			if(projectName == null) {
				return string;
			} else {
				return string + " (" + projectName + ")";
			}
		}
		
}
