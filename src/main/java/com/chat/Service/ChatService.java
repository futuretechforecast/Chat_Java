package com.chat.Service;

import com.chat.Model.ChatModel;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ChatService {

	private final Firestore db = FirestoreClient.getFirestore();

	public String saveMessage(ChatModel chatModel) {
		try {

			String chatId = chatModel.getSenderId().compareTo(chatModel.getReceiverId()) < 0
					? chatModel.getSenderId() + "_" + chatModel.getReceiverId()
					: chatModel.getReceiverId() + "_" + chatModel.getSenderId();

			DocumentReference chatDocRef = db.collection("chat").document(chatId);

			CollectionReference messageCollection = chatDocRef.collection("message");
			DocumentReference messageDocRef = messageCollection.document();

			chatModel.setMessageId(messageDocRef.getId());

			Map<String, Object> messageData = new HashMap<>();
			messageData.put("content", chatModel.getContent());
			messageData.put("isRead", chatModel.isRead());
			messageData.put("messageId", chatModel.getMessageId());
			messageData.put("receiverId", chatModel.getReceiverId());
			messageData.put("senderId", chatModel.getSenderId());
			messageData.put("time", FieldValue.serverTimestamp());

			messageDocRef.set(messageData).get();

			return messageDocRef.getId();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to save message: " + e.getMessage());
		}
	}
}
