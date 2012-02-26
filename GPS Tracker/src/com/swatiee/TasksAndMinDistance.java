package com.swatiee;

import java.util.ArrayList;

public class TasksAndMinDistance {
	private ArrayList<Task> tasks;
	private double nextMinDistance;
	private ArrayList<Task> tasksNoMoreInProximity;
 	public void setTasks(ArrayList<Task> tasks) {
		this.tasks = tasks;
	}
	public ArrayList<Task> getTasks() {
		return tasks;
	}
	public void setNextMinDistance(double minDistance) {
		this.nextMinDistance = minDistance;
	}
	public double getNextMinDistance() {
		return nextMinDistance;
	}
	public void setTasksNoMoreInProximity(ArrayList<Task> tasksNoMoreInProximity) {
		this.tasksNoMoreInProximity = tasksNoMoreInProximity;
	}
	public ArrayList<Task> getTasksNoMoreInProximity() {
		return tasksNoMoreInProximity;
	}
	@Override
	public String toString() {
		return "TasksAndMinDistance [nextMinDistance=" + nextMinDistance
				+ ", tasks=" + tasks
				+ ", tasksNoMoreInProximity=" + tasksNoMoreInProximity
				+ "]";
	}
	
	
	
}
