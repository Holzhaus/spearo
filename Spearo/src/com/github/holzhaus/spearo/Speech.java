package com.github.holzhaus.spearo;

import java.io.Serializable;

public class Speech implements Serializable {
	private static final long serialVersionUID = 0L;
	private Speaker speaker;
	private Boolean held;
	
	public Speech(Speaker speaker) {
		super();
		this.speaker = speaker;
		this.held = false;
	}
	public Speaker getSpeaker() {
		return speaker;
	}
	public void setSpeaker(Speaker speaker) {
		this.speaker = speaker;
	}
	public Boolean hasBeenHeld() {
		return held;
	}
	public void setHeld(Boolean held) {
		this.held = held;
	}

}
