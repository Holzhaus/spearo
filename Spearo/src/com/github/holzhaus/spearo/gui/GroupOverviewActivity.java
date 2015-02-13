package com.github.holzhaus.spearo.gui;

import com.github.holzhaus.spearo.Group;
import com.github.holzhaus.spearo.R;
import com.github.holzhaus.spearo.Roster;
import com.github.holzhaus.spearo.gui.adapters.GroupAdapter;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class GroupOverviewActivity extends ActionBarActivity {
	private GroupAdapter groupAdapter;
	private ListView groupListView;
	
	static final int EDIT_GROUP = 1;  // The request code
	static final int EDIT_ROSTER = 2;  // The request code

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_overview);
		this.groupAdapter = new GroupAdapter(this);
		this.groupListView = (ListView) findViewById(R.id.group_listview);
		this.groupAdapter.load();
		this.groupListView.setAdapter(this.groupAdapter);
		registerForContextMenu(this.groupListView);
		
		this.groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				final Group group = (Group) parent.getItemAtPosition(position);
				
				if(group.getSize() > 0) {
					Intent intent = new Intent(view.getContext(), RosterActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("group", group);
					bundle.putLong("group_id", groupAdapter.getItemId(position));
					intent.putExtra("ROSTER_GROUP", bundle);
					startActivityForResult(intent, EDIT_ROSTER);
				} else {
					Toast.makeText(getApplicationContext(), String.format(getResources().getString(R.string.group_is_empty), group.getName()), Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	public void editGroup(Group group, long group_id) {
		Intent intent = new Intent(this, GroupEditActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("group", group);
		bundle.putLong("group_id", group_id);
		intent.putExtra("EDIT_GROUP", bundle);
		startActivityForResult(intent, EDIT_GROUP);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    // Check which request we're responding to
	    switch(requestCode) {
	    	case EDIT_GROUP:
		        // Make sure the request was successful
		        if (resultCode == RESULT_OK) {
		        	Log.w("Spearo", "Returned from EDIT_GROUP request");
		            Bundle bundle = intent.getBundleExtra("CHANGED_GROUP");
		            Group group = (Group) bundle.getSerializable("group");
		            long group_id = bundle.getLong("group_id");
		            this.groupAdapter.setItem((int)group_id, group);
		            this.groupAdapter.save();
		        }
		        break;
	    	case EDIT_ROSTER:
		        // Make sure the request was successful
		        if (resultCode == RESULT_OK) {
		        	Log.w("Spearo", "Returned from EDIT_ROSTER request");
		            Bundle bundle = intent.getBundleExtra("CHANGED_ROSTER");
		            Roster roster = (Roster) bundle.getSerializable("roster");
		            long group_id = bundle.getLong("group_id");
		            this.groupAdapter.getItem((int)group_id).setRoster(roster);
		            this.groupAdapter.save();
		        }	    		
	    }
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	      super.onCreateContextMenu(menu, v, menuInfo);
	      if (v.getId()==R.id.group_listview) {
	    	  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
	    	  menu.setHeaderTitle(this.groupAdapter.getItem(info.position).getName());
	          MenuInflater inflater = getMenuInflater();
	          inflater.inflate(R.menu.group_context, menu);
	      }
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	      Group group = this.groupAdapter.getItem(info.position);
	      switch(item.getItemId()) {
	          case R.id.group_edit:
	        	    // edit stuff here
	        	  	this.editGroup(group, this.groupAdapter.getItemId(info.position));
	                return true;
	          case R.id.group_delete:
	        	    // remove stuff here
	        	    this.groupAdapter.remove(group);
	                return true;
	          default:
	                return super.onContextItemSelected(item);
	      }
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onBackPressed() {
		this.groupAdapter.save();
		super.onBackPressed();
	}

	public void addNewGroup() {
		// get prompts.xml view
		View promptView = View.inflate(this, R.layout.prompt_newgroup, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set prompt_newgroup.xml to be the layout file of the alertdialog builder
		alertDialogBuilder.setView(promptView);

		final EditText input = (EditText) promptView.findViewById(R.id.etName);

		// setup a dialog window
		alertDialogBuilder
		.setCancelable(false)
		.setPositiveButton(getResources().getString(R.string.create), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// get user input and set it to result
				String groupname = input.getText().toString();
				String toast_text;
				if (groupname != null && !groupname.isEmpty()) {
					Group group = new Group(groupname);
					groupAdapter.add(group);
					long group_id = groupAdapter.getItemId(groupAdapter.getItemPosition(group));
					groupAdapter.save();
					toast_text = String.format(getResources().getString(R.string.new_group_added), groupname);
					Toast.makeText(getApplicationContext(), toast_text, Toast.LENGTH_SHORT).show();
					editGroup(group, group_id);
				} else {
					toast_text = getResources().getString(R.string.new_group_name_required);
					Toast.makeText(getApplicationContext(), toast_text, Toast.LENGTH_SHORT).show();
				}
			}
		})
		.setNegativeButton(getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,	int id) {
				dialog.cancel();
			}
		});

		// create an alert dialog
		AlertDialog alertD = alertDialogBuilder.create();

		alertD.show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group_overview, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_newgroup:
			//openSearch();
			this.addNewGroup();
			return true;
		case R.id.action_settings:
			//openSettings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
