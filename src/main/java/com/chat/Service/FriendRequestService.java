package com.chat.Service;

import com.chat.Model.FriendRequest;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class FriendRequestService {

	private final Firestore db = FirestoreClient.getFirestore();
	private final CollectionReference userRef = db.collection("users");
	private final CollectionReference fcmToken = db.collection("fcm_token");

	public String sendFriendRequest(FriendRequest request) throws ExecutionException, InterruptedException {

		boolean requestExists = checkIfRequestExists(request.getSenderId(), request.getReceiverId());
		if (requestExists) {
			throw new IllegalArgumentException("Friend request already sent.");
		}

		Map<String, Object> requestData = new HashMap<>();
		requestData.put("senderId", request.getSenderId());
		requestData.put("receiverId", request.getReceiverId());
		requestData.put("status", "pending");
		db.collection("friendRequests").add(requestData).get();

		String receiverFcmToken = getReceiverFcmToken(request.getReceiverId());

		DocumentReference senderRef = userRef.document(request.getSenderId());
		ApiFuture<DocumentSnapshot> future = senderRef.get();
		DocumentSnapshot document = future.get();

		String senderName = "";
		if (document.exists()) {
			senderName = document.getString("name");
		}

		sendNotification(receiverFcmToken, senderName);

		return "Friend request sent successfully from " + request.getSenderId() + " to " + request.getReceiverId();
	}

	public String updateFriendRequestStatus(FriendRequest request) throws ExecutionException, InterruptedException {

		String senderId = request.getSenderId();
		String receiverId = request.getReceiverId();
		String status = request.getStatus();

		Map<String, Object> updateData = new HashMap<>();
		updateData.put("status", status);

		db.collection("friendRequests").whereEqualTo("senderId", receiverId).whereEqualTo("receiverId", senderId).get()
				.get().forEach(document -> {
					db.collection("friendRequests").document(document.getId()).update(updateData);
				});

		String receiverFcmToken = getReceiverFcmToken(receiverId);

		DocumentReference senderRef = userRef.document(senderId);
		ApiFuture<DocumentSnapshot> future = senderRef.get();
		DocumentSnapshot document = future.get();

		String senderName = "";
		if (document.exists()) {
			senderName = document.getString("name");
		}

		if ("accepted".equalsIgnoreCase(status)) {
			sendNotification(receiverFcmToken, senderName + " has accepted your friend request.");
		} else if ("rejected".equalsIgnoreCase(status)) {
			sendNotification(receiverFcmToken, senderName + " has rejected your friend request.");
		}

		return "Friend request " + status + " successfully.";
	}

	public List<Map<String, Object>> getReceivedList(String receiverId)
			throws ExecutionException, InterruptedException {

		CollectionReference friendRequestsCollection = db.collection("friendRequests");

		Query receiverQuery = friendRequestsCollection.whereEqualTo("receiverId", receiverId);
		ApiFuture<QuerySnapshot> receiverRequestSnapshot = receiverQuery.get();

		List<String> senderIds = new ArrayList<>();
		List<Map<String, Object>> friendRequestsDetails = new ArrayList<>();

		for (QueryDocumentSnapshot document : receiverRequestSnapshot.get().getDocuments()) {
			String senderId = document.getString("senderId");
			if (senderId != null) {
				senderIds.add(senderId);
				friendRequestsDetails.add(document.getData());
			}
		}

		if (senderIds.isEmpty()) {
			return new ArrayList<>();
		}

		return fetchUserDetails(senderIds, friendRequestsDetails, "senderId");
	}

	public List<Map<String, Object>> getSendList(String senderId) throws ExecutionException, InterruptedException {

		CollectionReference friendRequestsCollection = db.collection("friendRequests");

		Query senderQuery = friendRequestsCollection.whereEqualTo("senderId", senderId);
		ApiFuture<QuerySnapshot> senderRequestSnapshot = senderQuery.get();

		List<String> receiverIds = new ArrayList<>();
		List<Map<String, Object>> friendRequestsDetails = new ArrayList<>();

		for (QueryDocumentSnapshot document : senderRequestSnapshot.get().getDocuments()) {
			String receiverId = document.getString("receiverId");
			if (receiverId != null) {
				receiverIds.add(receiverId);
				friendRequestsDetails.add(document.getData());
			}
		}

		if (receiverIds.isEmpty()) {
			return new ArrayList<>();
		}

		return fetchUserDetails(receiverIds, friendRequestsDetails, "receiverId");
	}

	private boolean checkIfRequestExists(String senderId, String receiverId)
			throws ExecutionException, InterruptedException {

		var querySnapshot = db.collection("friendRequests").whereEqualTo("senderId", senderId)
				.whereEqualTo("receiverId", receiverId).get().get();

		return !querySnapshot.isEmpty();
	}

	private String getReceiverFcmToken(String list) {
		try {

			var receiverDoc = fcmToken.document(list).get().get();

			if (receiverDoc.exists()) {
				return receiverDoc.getString("fcmToken");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private void sendNotification(String fcmToken, String senderId) {
		if (fcmToken != null) {
			try {
				Message message = Message.builder()
						.setNotification(Notification.builder().setTitle("Friend Request").setBody(senderId).build())
						.setToken(fcmToken).build();

				FirebaseMessaging.getInstance().send(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private List<Map<String, Object>> fetchUserDetails(List<String> userIds,
			List<Map<String, Object>> friendRequestsDetails, String matchField)
			throws ExecutionException, InterruptedException {

		CollectionReference usersCollection = db.collection("users");
		List<Map<String, Object>> userDetailsList = new ArrayList<>();

		int batchSize = 10;
		for (int i = 0; i < userIds.size(); i += batchSize) {
			List<String> batch = userIds.subList(i, Math.min(i + batchSize, userIds.size()));

			Query userQuery = usersCollection.whereIn(FieldPath.documentId(), batch);
			ApiFuture<QuerySnapshot> userQuerySnapshot = userQuery.get();

			for (QueryDocumentSnapshot userDoc : userQuerySnapshot.get().getDocuments()) {
				Map<String, Object> userDetails = userDoc.getData();

				// Attach corresponding friend request details
				userDetails.put("friendRequestDetails", friendRequestsDetails.stream()
						.filter(req -> userDoc.getId().equals(req.get(matchField))).findFirst().orElse(null));

				userDetailsList.add(userDetails);
			}
		}

		return userDetailsList;
	}
}
