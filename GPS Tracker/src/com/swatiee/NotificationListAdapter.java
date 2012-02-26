package com.swatiee;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class NotificationListAdapter extends BaseAdapter {
	private ArrayList<Notofication> notes;
	private Context context;

	
	public NotificationListAdapter(Context context, ArrayList<Notofication> notes) {
		this.notes = notes;
		this.context = context;
	}

	
	
	public int getCount() {
		return notes.size();
	}

	public Notofication getItem(int position) {
		return (null == notes) ? null : notes.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
        public View getView(int position, View convertView, ViewGroup parent) {
        	NotificationListItemNew sv;
        	Notofication t = (Notofication)notes.get(position);
                if (convertView == null) {
                      sv = new NotificationListItemNew(context,t.getTaskName(), t.getEmailOrPhone(), t.getDateTime());

                } else {
                    sv = (NotificationListItemNew) convertView;
                      sv.setDateTime(t.getDateTime()+" [Task - "+t.getTaskName()+"]");
                      sv.setMessage("Notified "+ t.getEmailOrPhone());
                }

                return sv;
            }	
	
	public void forceReload() {
		notifyDataSetChanged();
	}
	

}
