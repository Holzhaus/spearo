package com.github.holzhaus.spearo.gui;

import java.util.Arrays;

import com.github.holzhaus.spearo.Group;
import com.github.holzhaus.spearo.R;
import com.github.holzhaus.spearo.Roster;
import com.github.holzhaus.spearo.RosterOrder;
import com.github.holzhaus.spearo.Speaker;
import com.github.holzhaus.spearo.gui.fragments.AvailableSpeakersFragment;
import com.github.holzhaus.spearo.gui.fragments.RosterFragment;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class RosterActivity extends ActionBarActivity implements AvailableSpeakersFragment.OnMemberClickedListener, OnItemSelectedListener
    {
	public Group group;
	public long group_id;
	private Roster roster;
	
	@Override
	public void onMemberClicked(Speaker speaker) {
		this.roster.add(speaker.newSpeech());

		RosterFragment rosterFragment = (RosterFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_roster);
        if (rosterFragment != null) {
            // If roster frag is available, we're in two-pane layout...

            // Call a method in the RosterFragment to update its content
        	rosterFragment.updateRoster(this.roster);
        } else {
            // Otherwise, we're in the one-pane layout and must swap frags...

            // Create fragment and give it an argument for the selected article
            /*ArticleFragment newFragment = new ArticleFragment();
            Bundle args = new Bundle();
            args.putInt(ArticleFragment.ARG_POSITION, position);
            newFragment.setArguments(args);
        
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();*/
        }
		Toast.makeText(this, String.format("%s added to roster.", speaker.getName()), Toast.LENGTH_SHORT).show();
    }
	
	public Roster getRoster() {
		return this.roster;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("ROSTER_GROUP");
		this.group = (Group) bundle.getSerializable("group");
		this.group_id = bundle.getLong("group_id");
		
		if ((savedInstanceState != null) && (savedInstanceState.getSerializable("roster") != null)) {
			this.roster = (Roster) savedInstanceState.getSerializable("roster");
		} else {
			this.roster = this.group.getRoster();
		}
		
		setContentView(R.layout.activity_roster);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle state) {
	    super.onSaveInstanceState(state);
	    state.putSerializable("roster", this.roster);
	}
	
	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("roster", this.roster);
		bundle.putLong("group_id", this.group_id);
	    intent.putExtra("CHANGED_ROSTER", bundle);
	    setResult(RESULT_OK, intent);
	    finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.roster, menu);
		MenuItem item = menu.findItem(R.id.rosterorder_spinner);
	    Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getSupportActionBar().getThemedContext(), R.array.rosterorder_items, R.layout.support_simple_spinner_dropdown_item); //  create the adapter from a StringArray
	    adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
	    String value;
	    switch(this.roster.getRosterOrder()) {
	    	case GENDERQUOTED:
	    		value = "GENDERQUOTED";
	    		break;
	    	case BALANCED:
	    		value = "BALANCED";
	    		break;
	    	default:
	    		value = "NONE";	
	    }
	    spinner.setAdapter(adapter); // set the adapter to provide layout of rows and content
	    spinner.setSelection(Arrays.asList(getResources().getStringArray(R.array.rosterorder_itemids)).indexOf(value));
	    spinner.setOnItemSelectedListener(this); // set the listener, to perform actions based on item selection
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
			case android.R.id.home:
	            this.onBackPressed(); 
	            return true;
			case R.id.action_clearroster:
				//openSearch();
				this.roster.clear();
				RosterFragment rosterFragment = (RosterFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_roster);
		    	if (rosterFragment != null) {
		            rosterFragment.updateRoster(this.roster);
		    	}
				return true;
			case R.id.action_settings:
				//openSettings();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
    @Override
	public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    	RosterOrder new_order;
    	switch(getResources().getStringArray(R.array.rosterorder_itemids)[pos]) {
    		case "GENDERQUOTED":
    			new_order = RosterOrder.GENDERQUOTED;
    			break;
    		case "BALANCED":
    			new_order = RosterOrder.BALANCED;
    			break;
    		default:
    			new_order = RosterOrder.NONE;
    	}
    	this.roster.setRosterOrder(new_order);
    	RosterFragment rosterFragment = (RosterFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_roster);
    	if (rosterFragment != null) {
            // If roster fragment is available, we're in two-pane layout...

            // Call a method in the RosterFragment to update its content
            rosterFragment.updateRoster(this.roster);
        } else {
        	//TODO
        }
    }

    @Override
	public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
 
}
