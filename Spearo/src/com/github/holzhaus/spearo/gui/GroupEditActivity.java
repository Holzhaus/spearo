package com.github.holzhaus.spearo.gui;

import com.github.holzhaus.spearo.Gender;
import com.github.holzhaus.spearo.Group;
import com.github.holzhaus.spearo.R;
import com.github.holzhaus.spearo.Speaker;
import com.github.holzhaus.spearo.gui.adapters.GroupMemberAdapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class GroupEditActivity extends ActionBarActivity {
	public Group group;
	public long group_id;
	public GroupMemberAdapter memberAdapter;
	public ListView memberListView; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_edit);
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("EDIT_GROUP");
		this.group = (Group) bundle.getSerializable("group");
		this.group_id = bundle.getLong("group_id");
		final EditText groupname = (EditText) findViewById(R.id.etGroupName);
		groupname.setText(this.group.getName());
		groupname.addTextChangedListener(new TextWatcher() {
		    @Override
		    public void onTextChanged(CharSequence s, int start, int before, int count)
		    {}

		    @Override
		    public void beforeTextChanged(CharSequence s, int start, int count, int after)
		    {}

		    @Override
		    public void afterTextChanged(final Editable s)
		    {
		    	group.setName(s.toString());
		    }
		});
		
		groupname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		        if (!hasFocus) {
		        	group.setName(groupname.getText().toString());
		        }
		    }
		});
		
		this.memberAdapter = new GroupMemberAdapter(this);
		this.memberAdapter.updateMembers(this.group);
		this.memberListView = (ListView) findViewById(R.id.member_listview);
		this.memberListView.setAdapter(this.memberAdapter);
		registerForContextMenu(this.memberListView);
	}
	
	@Override
	public void onBackPressed() {
		//super.onBackPressed()
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("group", this.group);
		bundle.putLong("group_id", this.group_id);
	    intent.putExtra("CHANGED_GROUP", bundle);
	    setResult(RESULT_OK, intent);
	    finish();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	      super.onCreateContextMenu(menu, v, menuInfo);
	      if (v.getId()==R.id.member_listview) {
	    	  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
	    	  menu.setHeaderTitle(this.memberAdapter.getItem(info.position).getName());
	          MenuInflater inflater = getMenuInflater();
	          inflater.inflate(R.menu.groupmember_context, menu);
	      }
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	      Speaker member = this.memberAdapter.getItem(info.position);
	      switch(item.getItemId()) {
	          case R.id.groupmember_edit:
	        	    // edit stuff here
	        	  	// TODO: Make members editable
	                return true;
	          case R.id.groupmember_delete:
	        	    // remove stuff here
	        	    this.group.remove(member);
	                return true;
	          default:
	                return super.onContextItemSelected(item);
	      }
	}

	public void addNewMember() {
		// get prompts.xml view
		View promptView = View.inflate(this, R.layout.prompt_newmember, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set prompt_newgroup.xml to be the layout file of the alertDialogBuilder
		alertDialogBuilder.setView(promptView);

		final EditText input = (EditText) promptView.findViewById(R.id.etName);
		final Group group = this.group;
		final RadioButton rb_male = (RadioButton) promptView.findViewById(R.id.radio_male);

		// setup a dialog window
		alertDialogBuilder
		.setCancelable(false)
		.setPositiveButton(getResources().getString(R.string.add), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// get user input and set it to result
				String membername = input.getText().toString();
				String toast_text;
				if (membername != null && !membername.isEmpty()) {
					Gender gender = rb_male.isChecked() ? Gender.MALE : Gender.FEMALE;
					group.add(new Speaker(membername, gender));
					toast_text = String.format(getResources().getString(R.string.new_groupmember_added), membername, group.getName());
				} else {
					toast_text = String.format(getResources().getString(R.string.new_groupmember_name_required), group.getName());
				}
				Toast.makeText(getApplicationContext(), toast_text, Toast.LENGTH_SHORT).show();
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
		getMenuInflater().inflate(R.menu.group_edit, menu);
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
		case R.id.action_newmember:
			//openSearch();
			this.addNewMember();
			return true;
		case R.id.action_settings:
			//openSettings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
