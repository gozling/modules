package com.ls.dsbr.jsnc.trans;

import java.util.List;

import com.ls.dsbr.jsnc.proto.TransformerTest.LeveType;

public class User {
	private String uid;
	private String nickname;
	private List<String> tags;
	private String login_time;
	private LeveType level_type;
	private boolean enabled;

	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getLogin_time() {
		return login_time;
	}
	public void setLogin_time(String login_time) {
		this.login_time = login_time;
	}
	public LeveType getLevel_type() {
		return level_type;
	}
	public void setLevel_type(LeveType level_type) {
		this.level_type = level_type;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
