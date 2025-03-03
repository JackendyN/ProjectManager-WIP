package com.project;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class SaveLoadProjects {

	public static void SaveProjects(ArrayList<Project> projects, File projectFile) {
		try {
			FileOutputStream outputStream = new FileOutputStream(projectFile);
			ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
			
			for (Project p : projects) {
				objectStream.writeObject(p);
			}
			
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Something went wrong. Try again.");
			return;
		}
	}
	
	public static ArrayList<Project> LoadProjects(File projectFile) {
		ArrayList<Project> currentList = new ArrayList<Project>();
		
		try {
			FileInputStream fin = new FileInputStream(projectFile);
			ObjectInputStream ois = new ObjectInputStream(fin);
			Object object;
			
			while(true) {
				
				try {
					object = ois.readObject();
					if(object instanceof Project) {
						currentList.add((Project)object);
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
