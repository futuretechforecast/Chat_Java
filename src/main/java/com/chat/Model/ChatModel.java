package com.chat.Model;

import com.google.cloud.Timestamp;

public class ChatModel {
	private String content;
	private boolean isRead;
	private String messageId;
	private String receiverId;
	private String senderId;
	private Timestamp time;

	public ChatModel() {

	}

	public ChatModel(String content, boolean isRead, String messageId, String receiverId, String senderId,
			Timestamp time) {
		this.content = content;
		this.isRead = isRead;
		this.messageId = messageId;
		this.receiverId = receiverId;
		this.senderId = senderId;
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean read) {
		isRead = read;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}
}
