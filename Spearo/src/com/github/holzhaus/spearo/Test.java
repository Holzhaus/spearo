package com.github.holzhaus.spearo;
public class Test {
	public static void print(Roster roster) {
		System.out.print("Order: ");
		switch(roster.getRosterOrder()) {
			case GENDERQUOTED:
				System.out.println("Gender-quoted");
				break;
			case BALANCED:
				System.out.println("Balanced");
				break;
			default:
				System.out.println("None");
		}
		int i = 1;
		for(Speech s: roster.getOrdered()) {
			System.out.printf("%03d ", i);
			System.out.print(s.hasBeenHeld() ? "H " : "  ");
			System.out.print(s.getSpeaker().getGender()==Gender.MALE ? "M " : "F ");
			System.out.println(s.getSpeaker().getName());
			i++;
		}
		System.out.println();
	}
	public static void main(String[] args) {
		Speaker a = new Speaker("Alice", Gender.FEMALE);
		Speaker b = new Speaker("Bob", Gender.MALE);
		Speaker c = new Speaker("Chris", Gender.MALE);
		Speaker d = new Speaker("David", Gender.MALE);
		Speaker e = new Speaker("Elena", Gender.FEMALE);
		Roster roster = new Roster();
		roster.add(a.newSpeech());
		roster.add(b.newSpeech());
		roster.add(a.newSpeech());
		roster.add(c.newSpeech());
		roster.add(d.newSpeech());
		roster.add(e.newSpeech());
		
		roster.setRosterOrder(RosterOrder.NONE);
		Test.print(roster);
		roster.setRosterOrder(RosterOrder.GENDERQUOTED);
		Test.print(roster);
		roster.setRosterOrder(RosterOrder.BALANCED);
		Test.print(roster);

	}
}
