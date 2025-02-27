package com.chat.Configuration;

import java.io.InputStream;

import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {

	@PostConstruct
	public void initializeFirebase() {
		try (InputStream serviceAccount = getClass().getClassLoader()
				.getResourceAsStream("firebase-service-account.json")) {
			if (serviceAccount == null) {
				throw new RuntimeException("Firebase service account file not found in resources folder.");
			}

			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount)).setProjectId("chat-32c02").build();

			if (FirebaseApp.getApps().isEmpty()) {
				FirebaseApp.initializeApp(options);
				System.out.println("Firebase Initialized Successfully");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to initialize Firebase");
		}
	}
}