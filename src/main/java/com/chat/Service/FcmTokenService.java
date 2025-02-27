package com.chat.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.chat.Model.FcmTokenModel;
import com.chat.Model.PushNotificationRequest;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Service
public class FcmTokenService {

	private Logger logger = LoggerFactory.getLogger(FcmTokenService.class);

	private final Firestore db = FirestoreClient.getFirestore();

	public void saveToken(FcmTokenModel model) {
		String userId = model.getUserId();
		DocumentReference docRef = db.collection("fcm_token").document(userId);

		try {

			ApiFuture<WriteResult> writeResult = docRef.set(model, SetOptions.merge());

			System.out.println("Token saved/updated: " + writeResult.get().getUpdateTime());

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.err.println("Error saving token - InterruptedException: " + e.getMessage());
		} catch (ExecutionException e) {
			System.err.println("Error saving token - ExecutionException: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Error saving token: " + e.getMessage());
		}
	}

	private String sendAndGetResponse(Message message) throws InterruptedException, ExecutionException {
		return FirebaseMessaging.getInstance().sendAsync(message).get();
	}

	private AndroidConfig getAndroidConfig(String topic) {
		return AndroidConfig.builder().setTtl(Duration.ofMinutes(2).toMillis()).setCollapseKey(topic)
				.setPriority(AndroidConfig.Priority.HIGH)
				.setNotification(AndroidNotification.builder().setTag(topic).build()).build();
	}

	private ApnsConfig getApnsConfig(String topic) {
		return ApnsConfig.builder().setAps(Aps.builder().setCategory(topic).setThreadId(topic).build()).build();
	}

	private Message.Builder getPreconfiguredMessageBuilder(PushNotificationRequest request) {
		AndroidConfig androidConfig = getAndroidConfig(request.getTopic());
		ApnsConfig apnsConfig = getApnsConfig(request.getTopic());
		return Message.builder().setApnsConfig(apnsConfig).setAndroidConfig(androidConfig).setNotification(
				Notification.builder().setTitle(request.getTitle()).setBody(request.getMessage()).build());

	}

	public void sendNotification(PushNotificationRequest request, String navigationView)
			throws InterruptedException, ExecutionException {

		Map<String, String> data = new HashMap<>();
		data.put("navigation_view", navigationView);
		data.put("userId", request.getUserId());

		if (request.getName() != null) {
			data.put("name", request.getName());
		} else {
			logger.warn("The 'name' field is null in PushNotificationRequest for userId: " + request.getUserId());
		}

		Message message = getPreconfiguredMessageWithData(data, request);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		gson.toJson(message);

		sendAndGetResponse(message);

	}

	private Message getPreconfiguredMessageWithData(Map<String, String> data, PushNotificationRequest request) {
		return getPreconfiguredMessageBuilder(request).putAllData(data).setToken(request.getToken()).build();
	}

}
