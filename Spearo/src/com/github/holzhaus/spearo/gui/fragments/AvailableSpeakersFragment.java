package com.github.holzhaus.spearo.gui.fragments;
import com.github.holzhaus.spearo.Group;
import com.github.holzhaus.spearo.R;
import com.github.holzhaus.spearo.Speaker;
import com.github.holzhaus.spearo.gui.adapters.GroupMemberAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class AvailableSpeakersFragment extends Fragment {
	public GroupMemberAdapter memberAdapter;
	public ListView memberListView;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_availablespeakers, container, false);
	    Intent intent = getActivity().getIntent();
		Bundle bundle = intent.getBundleExtra("ROSTER_GROUP");
		Group group = (Group) bundle.getSerializable("group");
	    
		this.memberAdapter = new GroupMemberAdapter(getActivity());
		this.memberListView = (ListView) view.findViewById(R.id.listview_availablespeakers);
		this.memberAdapter.updateMembers(group);
		this.memberListView.setAdapter(this.memberAdapter);
		
		this.memberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				Speaker speaker = (Speaker) parent.getItemAtPosition(position);
				/* TODO: Add Speaker to roster */
				mCallback.onMemberClicked(speaker);
			}
		});
		return view;
    }
    
    OnMemberClickedListener mCallback;

    // Container Activity must implement this interface
    public interface OnMemberClickedListener {
        public void onMemberClicked(Speaker speaker);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnMemberClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMemberClickedListener");
        }
    }
}