package com.chat.Model;

public class PushNotificationRequest {

	private String title;
	private String message;
	private String topic;
	private String token;
	private String userId;
	private String name;
	private String image;

	public PushNotificationRequest() {

	}

	public PushNotificationRequest(String title, String message, String token, String name, String userId, String image,
			String topic) {
		this.title = title;
		this.message = message;
		this.token = token;
		this.name = name;
		this.userId = userId;
		this.image = image;
		this.topic = topic;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;

	}

}
