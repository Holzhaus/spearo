package com.github.holzhaus.spearo;
import android.annotation.SuppressLint;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class Roster implements Serializable {
	private static final long serialVersionUID = 0L;
	protected ArrayList<Speech> unordered;
	protected ArrayList<Speech> held;
	protected ArrayList<Speech> remaining;
	protected ArrayList<Speech> remainingOrdered;
	private RosterOrder rosterOrder = RosterOrder.NONE; 
	
	public Roster() {
		this.unordered = new ArrayList<Speech>();
		this.held = new ArrayList<Speech>();
		this.remaining = new ArrayList<Speech>();
		this.remainingOrdered = new ArrayList<Speech>();
	}
	
	public void setRosterOrder(RosterOrder rosterOrder) {
		this.rosterOrder = rosterOrder;
		this.reorderRemaining();
	}
	
	private void moveHeld() {
		ArrayList<Speech> remove_from_held = new ArrayList<Speech>();
		ArrayList<Speech> remove_from_remaining = new ArrayList<Speech>();
		for(Speech speech: this.held) {
			if(!speech.hasBeenHeld()) {
				int idx = this.unordered.indexOf(speech);
				if (idx == 0 || this.remaining.isEmpty() || this.unordered.indexOf(this.remaining.get(0)) > idx) {
					this.remaining.add(0, speech);
				} else {
					for(int i=0; i<this.remaining.size(); i++) {
						if(this.unordered.indexOf(this.remaining.get(i))>idx) {
							this.remaining.add(i, speech);
							break;
						}
					}
				}
				remove_from_held.add(speech);
			}
		}
		for(Speech speech: remove_from_held) {
			this.held.remove(speech);
		}
		for(Speech speech: this.remaining) {
			if(speech.hasBeenHeld()) {
				this.held.add(speech);
				remove_from_remaining.add(speech);
			}
		}
		for(Speech speech: remove_from_remaining) {
			this.remaining.remove(speech);
		}
	}
	
	public void reorderRemaining() {
		this.moveHeld();
		switch(rosterOrder) {
			case BALANCED:
				this.reorderRemainingBalanced();
				break;
			case GENDERQUOTED:
				this.reorderRemainingGenderQuoted();
				break;
			default:
				this.remainingOrdered.clear();
				this.remainingOrdered.addAll(this.remaining);
		}
	}
	
	private void reorderRemainingGenderQuoted() {
			if (this.remaining.size() > 0) {
				Gender gender;
				HashMap<Gender,ArrayList<Speech>> gendermap = new HashMap<Gender,ArrayList<Speech>>();
				ArrayList<Gender> gender_order = new ArrayList<Gender>();
				int i;
				/* First order the remaining speeches by gender */
				for(Speech speech : this.remaining) {
					gender = speech.getSpeaker().getGender();
					if (!gendermap.containsKey(gender)) {
						gendermap.put(gender, new ArrayList<Speech>());
						/* While we're at it, we preserve the gender_order */ 
						gender_order.add(gender);
					}
					gendermap.get(gender).add(speech);
				}
			
				/* Change gender_order according to held list */
				Set<Gender> genders_found = new HashSet<Gender>();
				if (this.held.size() > 0) {
					for(i=this.held.size()-1; i>=0; i--) {
						gender = this.held.get(0).getSpeaker().getGender();
						if (!genders_found.contains(gender) && gender_order.contains(gender)) {
							gender_order.remove(gender);
							gender_order.add(gender_order.size()-genders_found.size(), gender);
							genders_found.add(gender);
							if(genders_found.size() == gender_order.size()-1) {
								break;
							}
						}
					}
				}
				
				/* Now do the ordering itself */
				this.remainingOrdered.clear();
				while(this.remainingOrdered.size() < this.remaining.size()) {
					for(i=0; i<gender_order.size(); i++) {
						gender = gender_order.get(i);
						if (gendermap.get(gender).size() > 0) {
							this.remainingOrdered.add(gendermap.get(gender).get(0));
							gendermap.get(gender).remove(0);
						} 
					}
				}
			}
		}
	
	private void reorderRemainingBalanced() {
		final class SpeechInfo {
			public Speech speech;
			public int speeches_held;
			public int order_index;
			public SpeechInfo(Speech speech, int order_index, int speeches_held) {
				super();
				this.speech = speech;
				this.order_index = order_index;
				this.speeches_held = speeches_held;
			}
			@SuppressLint("DefaultLocale")
			@Override
			public String toString() {
				return String.format("(%s (%d), IDX=%s)", this.speech.getSpeaker().getName(), this.speeches_held, this.order_index);
			}
			
		}
		if (this.remaining.size() > 0) {
			Speaker speaker;
			Integer num_speeches;
			
			/* Count number of speeches by speaker in held list */
			HashMap<Speaker,Integer> speeches_held = new HashMap<Speaker,Integer>();
			if (this.held.size() > 0) {
				for(Speech s: this.remaining) {
					speaker = s.getSpeaker();
					if (!speeches_held.containsKey(speaker)) {
						speeches_held.put(speaker, 0);
					}
					speeches_held.put(speaker, speeches_held.get(speaker)+1);
				}
			}
			
			/* Put all speeches into a list */
			Speech speech;
			ArrayList<SpeechInfo> speeches = new ArrayList<SpeechInfo>();
			for(int i=0; i<this.remaining.size(); i++) {
				speech = this.remaining.get(i);
				speaker = speech.getSpeaker();
				
				if (!speeches_held.containsKey(speaker)) {
					speeches_held.put(speaker, 0);
				}
				num_speeches = speeches_held.get(speaker);
				speeches.add(new SpeechInfo(speech, this.remaining.size()-i, num_speeches));
				speeches_held.put(speaker, num_speeches+1);
			}
			
			Collections.sort(speeches, new Comparator<SpeechInfo>() {
				@Override
				public int compare(SpeechInfo s1, SpeechInfo s2) {
					if(s1.speeches_held == s2.speeches_held) {
						return (s1.order_index > s2.order_index) ? -1 : (s1.order_index < s2.order_index) ? +1 : 0; 
					} else {
						return (s1.speeches_held > s2.speeches_held) ? +1 : -1;
					}
				}
			});
			
			this.remainingOrdered.clear();
			for(SpeechInfo info: speeches) {
				this.remainingOrdered.add(info.speech);
			}
		}
	}
	
	public List<Speech> getOrdered() {
		ArrayList<Speech> order = new ArrayList<Speech>();
		order.addAll(this.held);
		order.addAll(this.remainingOrdered);
		return order;
	}
	
	public List<Speech> getUnordered() {
		return this.unordered;
	}
	
	public List<Speech> getHeld() {
		return this.held;
	}
	
	public List<Speech> getRemainingOrdered() {
		return this.remainingOrdered;
	}
	
	public List<Speech> getRemainingUnordered() {
		return this.remainingOrdered;
	}
	
	public void add(Speech speech) {
		this.unordered.add(speech);
		this.remaining.add(speech);
		this.reorderRemaining();
	}
	
	public void remove(Speech speech) {
		this.unordered.remove(speech);
		if(this.remaining.contains(speech)) {
			this.remaining.remove(speech);
			this.remainingOrdered.remove(speech);
		} else {
			this.held.remove(speech);
		}
		this.reorderRemaining();
	}
	
	public void clear() {
		this.unordered.clear();
		this.held.clear();
		this.remaining.clear();
		this.remainingOrdered.clear();
	}

	public RosterOrder getRosterOrder() {
		return this.rosterOrder;
	}
}
