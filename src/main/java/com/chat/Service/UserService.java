package com.chat.Service;

import com.chat.Controller.NotificationWebSocketController;
import com.chat.Model.UserModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class UserService {

	FcmTokenService fcmTokenService;

	NotificationWebSocketController webSocketController;

	private final Firestore db = FirestoreClient.getFirestore();
	private final CollectionReference userRef = db.collection("users");

	public UserModel addNewUser(UserModel model) throws Exception {

		Query phoneQuery = userRef.whereEqualTo("phoneNumber", model.getPhoneNumber());
		ApiFuture<QuerySnapshot> future = phoneQuery.get();
		QuerySnapshot querySnapshot = future.get();

		if (!querySnapshot.isEmpty()) {
			throw new RuntimeException("User with phone number " + model.getPhoneNumber() + " already exists.");
		}

		Random random = new Random();
		int randomNumber = 1000 + random.nextInt(9000);
		String chatId = "CH" + randomNumber;
		model.setChatId(chatId);
		model.setRole("User");

		DocumentReference userDocRef = userRef.document();
		model.setId(userDocRef.getId());
		ApiFuture<WriteResult> writeResult = userDocRef.set(model);
		writeResult.get();

		return model;
	}

	public UserModel login(UserModel model) throws Exception {
		ApiFuture<QuerySnapshot> query = userRef.whereEqualTo("email", model.getEmail())
				.whereEqualTo("password", model.getPassword()).get();

		QuerySnapshot querySnapshot = query.get();

		if (querySnapshot.isEmpty()) {
			throw new RuntimeException("Incorrect email or password.");
		} else {
			DocumentSnapshot document = querySnapshot.getDocuments().get(0);
			return document.toObject(UserModel.class);
		}
	}

	public UserModel getDetails(String currentUserId) throws Exception {

		DocumentReference userRef = db.collection("users").document(currentUserId);

		ApiFuture<DocumentSnapshot> future = userRef.get();

		DocumentSnapshot document = future.get();

		if (!document.exists()) {
			throw new RuntimeException("User not found.");
		}

		return document.toObject(UserModel.class);
	}

	public List<UserModel> getAllUsers(String currentUserId) throws ExecutionException, InterruptedException {

		ApiFuture<QuerySnapshot> future = userRef.get();
		QuerySnapshot querySnapshot = future.get();

		List<UserModel> userList = querySnapshot.getDocuments().stream()
				.map(document -> document.toObject(UserModel.class)).filter(user -> !user.getId().equals(currentUserId)) 
				.collect(Collectors.toList());

		CollectionReference friendRequests = db.collection("friendRequests");

		Set<String> rejectedOrPendingReceiverIds = new HashSet<>();

		for (UserModel user : userList) {
			String receiverId = user.getId(); 

			ApiFuture<QuerySnapshot> futureRequest = friendRequests.whereEqualTo("receiverId", currentUserId)
					.whereEqualTo("senderId", receiverId).get();

			
			QuerySnapshot requestSnapshot = futureRequest.get();

			for (QueryDocumentSnapshot doc : requestSnapshot.getDocuments()) {
				String status = doc.getString("status");
				if ("rejected".equals(status) || "pending".equals(status)) {
					rejectedOrPendingReceiverIds.add(receiverId);
				}
			}
		}

		return userList.stream().filter(user -> rejectedOrPendingReceiverIds.contains(user.getId()))
				.collect(Collectors.toList());
	}

}
