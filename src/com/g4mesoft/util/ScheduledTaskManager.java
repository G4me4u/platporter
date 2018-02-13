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
			task.scheduledTask.doTask();
			itr.remove();
			if (task.repeat) {
				task.time += task.interval;
				tasksToAdd.add(task);
			}
		}
	}
	
	public void addTask(IScheduledTask scheduledTask, long interval, boolean repeat) {
		tasksToAdd.add(new TaskEntry(taskCounter++, scheduledTask, interval, repeat));
	}
	
	private class TaskEntry implements Comparable<TaskEntry> {
		
		private long taskId;

		private IScheduledTask scheduledTask;
		private long interval;
		private boolean repeat;

		private long time;
		
		private TaskEntry(long taskId, IScheduledTask scheduledTask, long interval, boolean repeat){
			this.taskId = taskId;

			this.scheduledTask = scheduledTask;
			this.interval = interval;
			this.repeat = repeat;
			

			this.time = ScheduledTaskManager.this.timer + interval;
		}

		@Override
		public int compareTo(TaskEntry other) {
			if (time != other.time)
				return Long.compare(time, other.time);
			return Long.compare(taskId, other.taskId);
		}
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof TaskEntry)
				return equals((TaskEntry)o);
			return false;
		}
		
		public boolean equals(TaskEntry o) {
			if (o == null)
				return false;
			if (o.time != time)
				return false;
			if (o.interval != interval)
				return false;
			if (o.repeat != repeat)
				return false;
			return o.scheduledTask.equals(scheduledTask);
		}
	}
}
