package com.project;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

@SuppressWarnings("serial")
public class Schedule implements Serializable {

	LocalDate date;
	ArrayList<TimeBlock> timeBlocks = new ArrayList<TimeBlock>();
	
	public Schedule(LocalDate date) {
		this.date = date;
	}

	public void AddRange(TimeBlock block) {
		if(timeBlocks.isEmpty()) {
			timeBlocks.add(block);
		} else {
			boolean found = false;
			for (TimeBlock tr : timeBlocks) {
				if(block.timeStart < tr.timeStart && !tr.inRange(block.timeEnd)) {
					timeBlocks.add(timeBlocks.indexOf(tr), block);
					found = true;
					break;
				}
			}
			
			if(!found && (block.timeStart > timeBlocks.get(timeBlocks.size() - 1).timeEnd)) {
				timeBlocks.add(timeBlocks.size(), block);
			} else if(!found && !(block.timeStart > timeBlocks.get(timeBlocks.size() - 1).timeEnd)) {
				System.out.println("Range could not be added");
			}
			
		}
	}
	
}
