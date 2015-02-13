package com.github.holzhaus.spearo;

import java.io.Serializable;

public class Speaker implements Serializable {
	private static final long serialVersionUID = 0L;
	private String name;
	private Gender gender;
	
	public Speaker(String name, Gender gender) {
		super();
		this.name = name;
		this.gender = gender;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	public Speech newSpeech() {
		return new Speech(this);
	}

}
