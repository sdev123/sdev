package com.swatiee;

import java.util.ArrayList;
import java.util.Iterator;

import com.swatiee.Task;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class TaskListAdapter extends BaseAdapter {

	private ArrayList<Task> tasks;
	private Context context;

	public TaskListAdapter(Context context, ArrayList<Task> tasks) {
		this.tasks = tasks;
		this.context = context;
	}

	public int getCount() {
		return tasks.size();
	}

	public Task getItem(int position) {
		return (null == tasks) ? null : tasks.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

/*	public View getView(int position, View convertView, ViewGroup parent) {
		TaskListItem tli;
		if (null == convertView) {
			tli = (TaskListItem)View.inflate(context, R.layout.task_list_item, null);
		} else {
			tli = (TaskListItem)convertView;
		}
		tli.setTask(tasks.get(position));
		return tli;
	}*/

	
        public View getView(int position, View convertView, ViewGroup parent) {
        	TaskListItemNew sv;
        	Task t = (Task)tasks.get(position);
                if (convertView == null) {
                    sv = new TaskListItemNew(context, t.getName(),t.getAddress(),t.getEmail(),t.getPhone(),
                    		t.getLatitude()+"", t.getLongitude()+"", t.getId()+"");
                } else {
                    sv = (TaskListItemNew) convertView;
                    sv.setName(t.getName());
                    sv.setAddress(t.getAddress());
                    sv.setEmail(t.getEmail());
                    sv.setPhone(t.getPhone());
/*                    sv.setLat(t.getLatitude()+"");
                    sv.setLng(t.getLongitude()+"");
                    sv.setId(t.getId()+"");
*/                }

                return sv;
            }	
	
	public void forceReload() {
		notifyDataSetChanged();
	}

	public void toggleTaskCompleteAtPosition(int position) {
		Task task = getItem(position);
		task.toggleComplete();
		notifyDataSetChanged();
	}

	public Long[] removeCompletedTasks() {
		ArrayList<Task> completedTasks = new ArrayList<Task>();
		ArrayList<Long> completedIds = new ArrayList<Long>();
		for (Task task : tasks) {
			if (task.isComplete()) {
				completedIds.add(task.getId());
				completedTasks.add(task);
			}
		}
		tasks.removeAll(completedTasks);
		notifyDataSetChanged();
		return completedIds.toArray(new Long[]{});
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("TaskListAdapter [tasks=\n");
		Iterator<Task> it = tasks.iterator();
		while (it.hasNext()){
			Task t = (Task)it.next();
			sb.append(t.toString());
		}
		sb.append("]");
		return sb.toString();
	}
	
	

}
