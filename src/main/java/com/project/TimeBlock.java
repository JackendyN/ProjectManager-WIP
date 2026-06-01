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
		int timeStart;
		int timeEnd;
		SerializableColor blockColor;
		
		public TimeBlock(int start, int end, String description, Color color) throws TimeRangeException {
			
			timeDescription = description;
			blockColor = new SerializableColor(color);
			
			if(start < 0 || end > 1440) {
				throw new TimeRangeException();
			}
			
			timeStart = start;
			if(end > start) {
				timeEnd = end;
			} else {
				throw new TimeRangeException();
			}
			
		}
		
		public Boolean inRange(int number) {
			return (number >= timeStart) && (number < timeEnd);
		}

		@Override
		public String toString() {
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
