package com.chat.Configuration;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class WebSocketHandler extends TextWebSocketHandler {

	private final ConcurrentHashMap<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
	private final Gson gson = new Gson();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String userId = getUserIdFromSession(session);
		userSessions.put(userId, session);
		System.out.println("User " + userId + " connected.");
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

		JsonObject jsonMessage = gson.fromJson(message.getPayload(), JsonObject.class);

		String senderId = jsonMessage.get("senderId").getAsString();
		String receiverId = jsonMessage.get("receiverId").getAsString();
		String messageText = jsonMessage.get("message").getAsString();
		String name = jsonMessage.get("name").getAsString();

		System.out.println("Message from " + senderId + " to " + receiverId + ": " + messageText);

		WebSocketSession receiverSession = userSessions.get(jsonMessage.get("receiverId").getAsString());

		if (receiverSession != null && receiverSession.isOpen()) {

			JsonObject response = new JsonObject();
			response.addProperty("from", senderId);
			response.addProperty("to", receiverId);
			response.addProperty("name", name);
			response.addProperty("message", messageText);

			receiverSession.sendMessage(new TextMessage(gson.toJson(response)));
			System.out.println("Sent message: " + response);
		} else {

			System.out.println("User " + receiverId + " is not connected.");
		}
	}

	protected void handleCallSignaling(WebSocketSession session, JsonObject jsonMessage) throws Exception {
		String senderId = jsonMessage.get("senderId").getAsString();
		String receiverId = jsonMessage.get("receiverId").getAsString();
		String callType = jsonMessage.get("callType").getAsString(); // 'audio' or 'video'
		String action = jsonMessage.get("action").getAsString(); // 'call', 'answer', 'end'

		System.out.println("Call action: " + action + " from " + senderId + " to " + receiverId);

		WebSocketSession receiverSession = userSessions.get(receiverId);
		if (receiverSession != null && receiverSession.isOpen()) {
			JsonObject response = new JsonObject();
			response.addProperty("from", senderId);
			response.addProperty("to", receiverId);
			response.addProperty("callType", callType);
			response.addProperty("action", action); // Call action (call, answer, end)

			// Send the response message to the receiver
			receiverSession.sendMessage(new TextMessage(gson.toJson(response)));
			System.out.println(response);
		} else {
			System.out.println("User " + receiverId + " is not connected.");
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		String userId = getUserIdFromSession(session);
		userSessions.remove(userId);
		System.out.println("User " + userId + " disconnected.");
	}

	private String getUserIdFromSession(WebSocketSession session) {
		URI uri = session.getUri();
		if (uri != null && uri.getQuery() != null) {
			String[] params = uri.getQuery().split("=");
			if (params.length > 1) {
				return params[1]; // Assuming "userId" is passed as ?userId=user1
			}
		}
		return "anonymous"; // Default userId if none is provided
	}

}