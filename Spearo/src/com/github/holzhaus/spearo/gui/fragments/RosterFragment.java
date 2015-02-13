package com.github.holzhaus.spearo.gui.fragments;
import com.github.holzhaus.spearo.R;
import com.github.holzhaus.spearo.Roster;
import com.github.holzhaus.spearo.Speech;
import com.github.holzhaus.spearo.gui.RosterActivity;
import com.github.holzhaus.spearo.gui.adapters.RosterAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class RosterFragment extends Fragment implements AdapterView.OnItemClickListener {
	private RosterAdapter rosterAdapter;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_roster, container, false);
        
        Roster roster = ((RosterActivity)getActivity()).getRoster();
        this.rosterAdapter = new RosterAdapter(getActivity());
		ListView rosterListView = (ListView) view.findViewById(R.id.listview_roster);
		this.rosterAdapter.updateRoster(roster);
		rosterListView.setAdapter(this.rosterAdapter);
		rosterListView.setOnItemClickListener(this);
		registerForContextMenu(rosterListView);
		
        return view;
    }
    
    @Override
	public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
		Speech speech = (Speech) parent.getItemAtPosition(position);
		speech.setHeld(!speech.hasBeenHeld());
		Roster roster = ((RosterActivity)getActivity()).getRoster();
		roster.reorderRemaining();
		rosterAdapter.updateRoster(roster);
	}

	public void updateRoster(Roster roster) {
		this.rosterAdapter.updateRoster(roster);
		Log.w("Spearo", roster.getOrdered().toString());
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	      super.onCreateContextMenu(menu, v, menuInfo);
	      if (v.getId()==R.id.listview_roster) {
	    	  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
	    	  String title = String.format(getResources().getString(R.string.speech_by_person), this.rosterAdapter.getItem(info.position).getSpeaker().getName());
	    	  menu.setHeaderTitle(title);
	          MenuInflater inflater = getActivity().getMenuInflater();
	          inflater.inflate(R.menu.roster_context, menu);
	      }
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	      Speech speech = this.rosterAdapter.getItem(info.position);
	      switch(item.getItemId()) {
	          case R.id.speech_delete:
	        	    // remove stuff here
	        	  	Roster roster = ((RosterActivity)getActivity()).getRoster();
	        	  	roster.remove(speech);
	        	  	roster.reorderRemaining();
	        		rosterAdapter.updateRoster(roster);
	                return true;
	          default:
	                return super.onContextItemSelected(item);
	      }
	}
}