package com.chat.Model;

import java.util.List;

public class FriendListModel {

	private String id;
	private String adderId;
	private String status;
	private List<String> friendIds;

	public FriendListModel() {
	}

	public FriendListModel(String adderId, List<String> friendIds) {
		this.adderId = adderId;
		this.friendIds = friendIds;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAdderId() {
		return adderId;
	}

	public void setAdderId(String adderId) {
		this.adderId = adderId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<String> getFriendIds() {
		return friendIds;
	}

	public void setFriendIds(List<String> friendIds) {
		this.friendIds = friendIds;
	}

}
