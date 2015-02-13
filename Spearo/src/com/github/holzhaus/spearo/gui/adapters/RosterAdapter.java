package com.github.holzhaus.spearo.gui.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.holzhaus.spearo.Gender;
import com.github.holzhaus.spearo.R;
import com.github.holzhaus.spearo.Roster;
import com.github.holzhaus.spearo.Speaker;
import com.github.holzhaus.spearo.Speech;

public class RosterAdapter extends BaseAdapter {
		private List<Speech> speeches;
		private final Context context;

	    // the context is needed to inflate views in getView()
	    public RosterAdapter(Context context) {
	        this.context = context;
	    }

	    public void updateRoster(Roster roster) {
	        this.speeches = roster.getOrdered();
	        this.notifyDataSetChanged();
	    }

	    @Override
	    public int getCount() {
	        return this.speeches.size();
	    }

	    // getItem(int) in Adapter returns Object but we can override
	    // it to BananaPhone thanks to Java return type covariance
	    @Override
	    public Speech getItem(int position) {
	        return this.speeches.get(position);
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
	       Speech speech = getItem(position);
	       Speaker speaker = speech.getSpeaker();
	       // Check if an existing view is being reused, otherwise inflate the view
	       if (convertView == null) {
	          convertView = LayoutInflater.from(this.context).inflate(R.layout.item_groupmember, parent, false);
	       }
	       // Lookup view for data population
	       TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
	       TextView tvGender = (TextView) convertView.findViewById(R.id.tvGender);
	       
	       // Populate the data into the template view using the data object
	       tvName.setText(String.format("%d. %s", this.speeches.indexOf(speech), speaker.getName()));
	       tvGender.setText(context.getResources().getString(speaker.getGender() == Gender.MALE ? R.string.gender_male : R.string.gender_female));
	       // Return the completed view to render on screen
	       convertView.setEnabled(!speech.hasBeenHeld());
	       tvName.setTextColor(speech.hasBeenHeld() ? Color.GRAY : Color.BLACK);
	       return convertView;
	   }
}