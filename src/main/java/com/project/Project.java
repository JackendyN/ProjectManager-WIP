package com.project;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Project implements Serializable {

	public Project() {
		super();
	}
	
	public String projectName;
	public String type;
	public int estimatedHours;
	public LocalDate startDate;
	public LocalDate deadLine;
	public ArrayList<Task> projectTasks;
	
}
