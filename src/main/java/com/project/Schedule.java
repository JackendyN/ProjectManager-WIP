package com.project;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Schedule implements Serializable {

	LocalDate date;
	ArrayList<TimeBlock> timeBlocks = new ArrayList<TimeBlock>();
	
	public Schedule(LocalDate date) {
		this.date = date;
	}

	public void AddRange(TimeBlock block) throws TimeRangeException {
		if(timeBlocks.isEmpty()) {
			timeBlocks.add(block);
		} else {
			boolean found = false;
			for (TimeBlock tb : timeBlocks) {
				if(block.timeStart < tb.timeStart && !tb.inRange(block.timeEnd)) {
					timeBlocks.add(timeBlocks.indexOf(tb), block);
					found = true;
					break;
				}
			}
			
			if(!found && (block.timeStart >= timeBlocks.get(timeBlocks.size() - 1).timeEnd)) {
				timeBlocks.add(timeBlocks.size(), block);
			} else {
				throw new TimeRangeException();
			}
			
		}
	}
	
}
