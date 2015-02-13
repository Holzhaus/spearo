package com.github.holzhaus.spearo.gui.adapters;

import com.github.holzhaus.spearo.Group;
import com.github.holzhaus.spearo.R;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GroupAdapter extends BaseAdapter {
	private List<Group> groups = new ArrayList<Group>();
	private final Context context;

    // the context is needed to inflate views in getView()
    public GroupAdapter(Context context) {
        this.context = context;
    }

    public void updateGroups(List<Group> groups) {
        this.groups = groups;
        this.notifyDataSetChanged();
    }
    
    public void add(Group group) {
    	this.groups.add(group);
    	this.notifyDataSetChanged();
    }
    
    public void remove(Group group) {
    	this.groups.remove(group);
    	this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.groups.size();
    }

    // getItem(int) in Adapter returns Object but we can override
    // it to BananaPhone thanks to Java return type covariance
    @Override
    public Group getItem(int position) {
        return this.groups.get(position);
    }
    
    public int getItemPosition(Group group) {
    	return this.groups.indexOf(group);
    }
    
    public void setItem(int position, Group group) {
    	this.groups.remove(position);
    	this.groups.add(position, group);
    	this.notifyDataSetChanged();
    }

    // getItemId() is often useless, I think this should be the default
    // implementation in BaseAdapter
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
       Group group = getItem(position);    
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
          convertView = LayoutInflater.from(this.context).inflate(R.layout.item_group, parent, false);
       }
       // Lookup view for data population
       TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
       TextView tvSize = (TextView) convertView.findViewById(R.id.tvSize);
       TextView tvMembers = (TextView) convertView.findViewById(R.id.tvMembers);
       // Populate the data into the template view using the data object
       tvName.setText(group.getName());
       tvSize.setText(String.valueOf(group.getSize()));
       int num_members = group.getMembers().size();
   	
       String member_text;
       if(num_members == 0) {
           member_text = context.getResources().getString(R.string.group_members_none);
       } else if(num_members == 1) {
           member_text = group.getMembers().get(0).getName();
       } else if(num_members == 2) {
           member_text = String.format(context.getResources().getString(R.string.group_members_two), group.getMembers().get(0).getName(), group.getMembers().get(1).getName());
       } else {
           member_text = String.format(context.getResources().getString(R.string.group_members_more), group.getMembers().get(0).getName(), group.getMembers().get(1).getName(), num_members-2);
       }
       tvMembers.setText(member_text);
       // Return the completed view to render on screen
       return convertView;
   }

	public void save() {
		try{
			FileOutputStream fos = context.openFileOutput("groups.bin", Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this.groups);
			oos.close();
			fos.close();
			Log.w("Spearo", String.format("%d Groups saved.", this.groups.size()));
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	public void load() {
		try
		{
			FileInputStream fis = context.openFileInput("groups.bin");
			ObjectInputStream ois = new ObjectInputStream(fis);
			this.updateGroups((ArrayList<Group>) ois.readObject());
			ois.close();
			fis.close();
			Log.w("Spearo", String.format("%d Groups restored.", this.groups.size()));
		} catch(FileNotFoundException fnfe) {
		} catch(IOException ioe){
			ioe.printStackTrace();
		} catch(ClassNotFoundException c){
			System.out.println("Class not found");
			c.printStackTrace();
		}
	}
  }