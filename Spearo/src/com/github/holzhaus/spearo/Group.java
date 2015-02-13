package com.github.holzhaus.spearo;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Serializable {
	private static final long serialVersionUID = 0L;
	private ArrayList<Speaker> members = new ArrayList<Speaker>();
	private String name;
	private Roster roster;
	
	public Group(String name) {
		this.name = name;
		this.roster = new Roster();
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getSize() {
		return this.members.size();
	}
	public void add(Speaker speaker) {
		this.members.add(speaker);
	}
	public void remove(Speaker speaker) {
		this.members.remove(speaker);
	}
	public ArrayList<Speaker> getMembers() {
		return this.members;
	}
	
	public void setRoster(Roster roster) {
		this.roster = roster;
	}
	public Roster getRoster() {
		return this.roster;
	}
}
