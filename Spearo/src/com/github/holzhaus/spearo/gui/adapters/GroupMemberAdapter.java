package com.github.holzhaus.spearo.gui.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.holzhaus.spearo.Gender;
import com.github.holzhaus.spearo.Group;
import com.github.holzhaus.spearo.R;
import com.github.holzhaus.spearo.Speaker;

public class GroupMemberAdapter extends BaseAdapter {
		private List<Speaker> members;
		private final Context context;

	    // the context is needed to inflate views in getView()
	    public GroupMemberAdapter(Context context) {
	        this.context = context;
	    }

	    public void updateMembers(Group group) {
	        this.members = group.getMembers();
	        this.notifyDataSetChanged();
	    }

	    @Override
	    public int getCount() {
	        return this.members.size();
	    }

	    // getItem(int) in Adapter returns Object but we can override
	    // it to BananaPhone thanks to Java return type covariance
	    @Override
	    public Speaker getItem(int position) {
	        return this.members.get(position);
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
	       Speaker speaker = getItem(position);    
	       // Check if an existing view is being reused, otherwise inflate the view
	       if (convertView == null) {
	          convertView = LayoutInflater.from(this.context).inflate(R.layout.item_groupmember, parent, false);
	       }
	       // Lookup view for data population
	       TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
	       TextView tvGender = (TextView) convertView.findViewById(R.id.tvGender);
	       // Populate the data into the template view using the data object
	       tvName.setText(speaker.getName());
	       tvGender.setText(context.getResources().getString(speaker.getGender() == Gender.MALE ? R.string.gender_male : R.string.gender_female));
	       // Return the completed view to render on screen
	       return convertView;
	   }
}