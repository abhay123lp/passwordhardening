package com.scs.pwdHardening.model;

public enum Category {
	SPORTS(0), TECHNOLOGY(1), ENTERTAINMENT(2), LITERATURE(3), GEOGRAPHY(4);
	Category(int index){
		this.index = index;
	}
	public static Category getByIndex(int index){
		for(Category c : Category.values()){
			if(c.index == index){
				return c;
			}
		}
		return null;
	}
	public int index;
}
