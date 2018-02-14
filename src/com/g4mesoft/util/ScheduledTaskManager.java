package com.g4mesoft.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ScheduledTaskManager {
	
	private long timer;
	private long taskCounter;
	
	private final List<TaskEntry> tasksToAdd;
	private final Set<TaskEntry> tasks;
	
	public ScheduledTaskManager() {
		timer = 0;
		taskCounter = 0;
		
		tasks = new TreeSet<TaskEntry>();
		tasksToAdd = new ArrayList<TaskEntry>();
	}
	
	public void update() {
		timer++;

		if (!tasksToAdd.isEmpty()) {
			for (TaskEntry task : tasksToAdd)
				tasks.add(task);
			tasksToAdd.clear();
		}
		
		Iterator<TaskEntry> itr = tasks.iterator();
		while (itr.hasNext()) {
			TaskEntry task = itr.next();
			if (task.time > timer)
				break;
			if (task.scheduledTask.doTask()) {
				task.time += task.interval;
				tasksToAdd.add(task);
			}
			itr.remove();
		}
	}
	
	public void addTask(IScheduledTask scheduledTask, long interval) {
		tasksToAdd.add(new TaskEntry(taskCounter++, scheduledTask, interval));
	}
	
	private class TaskEntry implements Comparable<TaskEntry> {
		
		private long taskId;

		private IScheduledTask scheduledTask;
		private long interval;

		private long time;
		
		private TaskEntry(long taskId, IScheduledTask scheduledTask, long interval){
			this.taskId = taskId;

			this.scheduledTask = scheduledTask;
			this.interval = interval;
			

			this.time = ScheduledTaskManager.this.timer + interval;
		}

		@Override
		public int compareTo(TaskEntry other) {
			if (time != other.time)
				return Long.compare(time, other.time);
			return Long.compare(taskId, other.taskId);
		}
	}
}
