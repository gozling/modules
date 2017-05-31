package com.ls.dsbr.jsnc.trans;

import java.util.List;

import com.ls.dsbr.common.Jsons;

public class Session {
	private User user;

	private List<User> friends;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<User> getFriends() {
		return friends;
	}

	public void setFriends(List<User> friends) {
		this.friends = friends;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return Jsons.i.toJson(this);
	}
}
